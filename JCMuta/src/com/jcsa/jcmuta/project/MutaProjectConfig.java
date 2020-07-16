package com.jcsa.jcmuta.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmuta.MutationUtil;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.ClangStandard;

/**
 * The configuration of mutation project including:
 * 	1) csizeof.txt
 * 	2) jcmulib.h
 * 	3) jcmulib.c
 * 	4) parameter
 * 		lang_std
 * 		compile_program_template
 * 		compile_mutant_template
 * 		execute_testing_template
 * @author yukimula
 *
 */
public class MutaProjectConfig {
	
	/* file names */
	private static final String csizeof_file_name = "csizeof.txt";
	private static final String jcmulib_header_name = "jcmulib.h";
	private static final String jcmulib_source_name = "jcmulib.c";
	private static final String parameter_file_name = "parameter.txt";
	
	/* parameter names */
	private static final String lang_standard_name = "lang_standard";
	private static final String compile_program_name = "compile_program";
	private static final String compile_mutant_name = "compile_mutant";
	private static final String execute_testing_name = "execute_testing";
	private static final String library_config_name = "library_config";
	
	/* parent */
	/** mutation testing project **/
	private MutaProject project;
	
	/* file list for configuration */
	/** csizeof.txt **/
	private File csizeof_file;
	/** jcmulib.h **/
	private File jcmulib_header;
	/** jcmulib.c **/
	private File jcmulib_source;
	/** parameter.txt **/
	private File parameter_file;
	
	/* parameters */
	/** c-sizeof template **/
	private CRunTemplate csizeof_template;
	/** C language standard **/
	private ClangStandard lang_standard;
	/** command template for compiling original program **/
	private String compile_program_template;
	/** command template for compiling mutated programs **/
	private String compile_mutant_template;
	/** command template for executing program against test **/
	private String execute_testing_template;
	/** the compile configuration related with external lib **/
	private String library_config;
	
	/* constructor */
	protected MutaProjectConfig(MutaProject project) throws Exception {
		this.project = project;
		
		File directory = project.project_directory;
		this.csizeof_file = new File(directory.getAbsolutePath() + File.separator + csizeof_file_name);
		this.jcmulib_header = new File(directory.getAbsolutePath() + File.separator + jcmulib_header_name);
		this.jcmulib_source = new File(directory.getAbsolutePath() + File.separator + jcmulib_source_name);
		this.parameter_file = new File(directory.getAbsolutePath() + File.separator + parameter_file_name);
		
		this.update_csize_file(); 
		this.update_parameters();
	}
	/**
	 * update the csizeof template by parsing file
	 * @throws Exception
	 */
	private void update_csize_file() throws Exception {
		if(this.csizeof_file.exists()) {
			this.csizeof_template = new CRunTemplate(this.csizeof_file);
		}
		else this.csizeof_template = null;
	}
	/**
	 * update the csizeof, command templates and language standard
	 * @throws Exception
	 */
	private void update_parameters() throws Exception {
		if(this.parameter_file.exists()) {
			Map<String, String> map = new HashMap<String, String>();
			
			BufferedReader reader = new BufferedReader(
					new FileReader(this.parameter_file));
			String line; int index;
			while((line = reader.readLine()) != null) {
				line = line.strip();
				index = line.indexOf(':');
				if(index >= 0) {
					String key = line.substring(0, index).strip();
					String value = line.substring(index + 1).strip();
					map.put(key, value);
				}
			}
			reader.close();
			
			this.lang_standard = ClangStandard.valueOf(map.get(lang_standard_name));
			this.compile_program_template = map.get(compile_program_name);
			this.compile_mutant_template = map.get(compile_mutant_name);
			this.execute_testing_template = map.get(execute_testing_name);
			this.library_config = map.get(library_config_name);
		}
		else {
			this.lang_standard = null;
			this.compile_program_template = null;
			this.compile_mutant_template = null;
			this.execute_testing_template = null;
			this.library_config = null;
		}
	}
	
	/* file getters */
	/**
	 * get the header file to declare mutation functions
	 * @return
	 */
	public File get_jcmulib_header_file() { return this.jcmulib_header; }
	/**
	 * get the source file to define mutation functions
	 * @return
	 */
	public File get_jcmulib_source_file() { return this.jcmulib_source; }
	
	/* parameter getters */
	/**
	 * get the language standard used to parse the AST of source code
	 * @return
	 */
	public ClangStandard get_lang_standard() { return this.lang_standard; }
	/**
	 * get the template for sizeof data
	 * @return
	 */
	public CRunTemplate get_sizeof_template() { return this.csizeof_template; }
	/**
	 * get the command for compiling the original program from a set of source files
	 * @param source_files the set of source files being compiled
	 * @param executional_file the path of the binary program file
	 * @param library_configuration the configuration used in compilation or empty
	 * @return command for compiling original program
	 * @throws Exception
	 */
	public String compile_program_command(Iterable<File> source_files, File executional_file) throws Exception {
		StringBuilder buffer = new StringBuilder();
		for(File source_file : source_files) 
			buffer.append(source_file.getAbsolutePath()).append(" ");
		return String.format(this.compile_program_template, buffer.toString().
				strip(), executional_file.getAbsolutePath(), this.library_config);
	}
	/**
	 * get the command for compiling the mutated program from a set of source files
	 * @param source_files the set of source files with mutated ones
	 * @param executional_file
	 * @param library_configuration
	 * @return
	 * @throws Exception
	 */
	public String compile_mutant_command(Iterable<File> source_files, File executional_file) throws Exception {
		StringBuilder buffer = new StringBuilder();
		for(File source_file : source_files) 
			buffer.append(source_file.getAbsolutePath()).append(" ");
		buffer.append(this.jcmulib_header.getAbsolutePath());
		
		return String.format(this.compile_mutant_template, this.project.project_directory.getAbsolutePath(), 
				buffer.toString(), executional_file.getAbsolutePath(), this.library_config);
	}
	/**
	 * generate the command to execute the test case against the executional program
	 * with respect to the output path.
	 * @param seconds
	 * @param executional_file
	 * @param test_argument
	 * @param output_file
	 * @return
	 * @throws Exception
	 */
	public String execute_testing_command(int seconds, 
			File executional_file, String test_argument, 
			File output_file) throws Exception {
		return String.format(this.execute_testing_template, seconds, executional_file.
				getAbsolutePath(), test_argument, output_file.getAbsolutePath());
	}
	
	/* setters */
	/**
	 * update the csize template data
	 * @param csizeof_file
	 * @throws Exception
	 */
	public void set_csizeof_file(File csizeof_file) throws Exception {
		MutationUtil.copy_file(csizeof_file, this.csizeof_file);
		this.update_csize_file();
	}
	/**
	 * update the header file to declare functions in mutation code
	 * @param jcmulib_header_file
	 * @throws Exception
	 */
	public void set_jcmulib_header_file(File jcmulib_header_file) throws Exception {
		MutationUtil.copy_file(jcmulib_header_file, this.jcmulib_header);
	}
	/**
	 * update the source file to define functions in the mutation code
	 * @param jcmulib_source_file
	 * @throws Exception
	 */
	public void set_jcmulib_source_file(File jcmulib_source_file) throws Exception {
		MutationUtil.copy_file(jcmulib_source_file, this.jcmulib_source);
	}
	/**
	 * update the parameters by parsing the new parameter file
	 * @param parameter_file
	 * @throws Exception
	 */
	public void set_parameter_file(File parameter_file) throws Exception {
		MutationUtil.copy_file(parameter_file, this.parameter_file);
		this.update_parameters();
	}
	
}
