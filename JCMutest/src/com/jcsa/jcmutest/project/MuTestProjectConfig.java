package com.jcsa.jcmutest.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.cmd.CCompiler;

/**
 * The configuration data used for mutation testing project include:<br>
 * 	1. command_util: the utility instance for running command;<br>
 * 	2. compiler: the system compiler used for compilation;<br>
 * 	3. lang_std: the language standard used to parse source code;<br>
 * 	4. sizeof_template_file: configs/cruntime.txt to determine sizeof;<br>
 * 	5. instrument_head_file: config/jcinst.h to compile instrumental code;<br>
 * 	6. preprocess_macro_file: config/linux.h to -imacros the preprocess;<br>
 * 	7. compilation_parameters: the parameters like -lm to compile C code;<br>
 * 	8. mutation_head_file: config/jcmutest.h to compile the mutation code;<br>
 * 	9. max_timeout_second: the maximal seconds required to complete a test;<br>
 * @author yukimula
 *
 */
public class MuTestProjectConfig {

	/* file-names */
	/** the name of the configuration data file in config/ **/
	private static final String config_file_name = "config.data";
	/** the name of sizeof template file under the config/ **/
	private static final String sizeof_template_file_name = "sizeof.tmp";
	/** the name of the header file for instrumentation in config/ **/
	private static final String instrument_head_file_name = "jcinst.h";
	/** the name of the pre-processing macro file under the config **/
	private static final String preprocess_macro_file_name = "linux.h";
	/** the name of the mutation header code file in the config/ **/
	private static final String mutation_head_file_name = "jcmutest.h";

	/* attributes */
	/** the mutation test project in which the configuration is defined **/
	private MuTestProject project;
	/** the utility instance for running command **/
	private MuCommandUtil command_util;
	/** the system compiler used for compilation **/
	private CCompiler compiler;
	/** the language standard used to parse source code **/
	private ClangStandard lang_std;
	/** the maximal seconds for running one test against program **/
	private long max_timeout_second;
	/** the parameters like -lm to compile C code **/
	private List<String> compilation_parameters;
	/** configs/cruntime.txt for sizeof operation in static analysis **/
	private File sizeof_template_file;
	/** config/jcinst.h to compile instrumental code **/
	private File instrument_head_file;
	/** config/linux.h to -imacros the preprocess **/
	private File preprocess_macro_file;
	/** config/jcmutest.h to compile the mutation code **/
	private File mutation_head_file;
	/** the configuration file in which parameters are preserved **/
	private File config_file;

	/* constructor */
	/**
	 * @param project
	 * @param command_util
	 * @throws Exception
	 */
	protected MuTestProjectConfig(MuTestProject project,
			MuCommandUtil command_util) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(command_util == null)
			throw new IllegalArgumentException("Invalid command_util");
		else {
			this.project = project;
			this.command_util = command_util;

			File dir = this.project.get_files().get_config_directory();
			this.config_file = new File(dir.getAbsolutePath() + "/" + config_file_name);
			this.sizeof_template_file = new File(dir.getAbsolutePath() + "/" + sizeof_template_file_name);
			this.instrument_head_file = new File(dir.getAbsolutePath() + "/" + instrument_head_file_name);
			this.preprocess_macro_file = new File(dir.getAbsolutePath() + "/" + preprocess_macro_file_name);
			this.mutation_head_file = new File(dir.getAbsolutePath() + "/" + mutation_head_file_name);

			this.compiler = null;
			this.lang_std = null;
			this.compilation_parameters = new ArrayList<>();
			this.load_config_file();
		}
	}
	/**
	 * save the configuration data in the file
	 * @throws Exception
	 */
	private void save_config_file() throws Exception {
		FileWriter writer = new FileWriter(this.config_file);
		if(this.compiler != null)
			writer.write("compiler: " + compiler + "\n");
		if(this.lang_std != null)
			writer.write("lang_std: " + lang_std + "\n");
		writer.write("max_timeout_seconds: " + this.max_timeout_second + "\n");
		writer.write("compilation_parameters: ");
		for(String parameter : this.compilation_parameters) {
			if(!parameter.trim().isEmpty())
				writer.write(" " + parameter.trim());
		}
		writer.write("\n");
		writer.close();
	}
	/**
	 * reload the configuration data from files
	 * @throws Exception
	 */
	private void load_config_file() throws Exception {
		if(this.config_file.exists()) {
			BufferedReader reader = new BufferedReader(
						new FileReader(this.config_file));
			String line, title, value; int index;
			while((line = reader.readLine()) != null) {
				if(!line.trim().isEmpty()) {
					index = line.indexOf(':');
					title = line.substring(0, index).trim();
					value = line.substring(index + 1).trim();

					if(title.equals("compiler")) {
						this.compiler = CCompiler.valueOf(value);
					}
					else if(title.equals("lang_std")) {
						this.lang_std = ClangStandard.valueOf(value);
					}
					else if(title.equals("max_timeout_seconds")) {
						this.max_timeout_second = Long.parseLong(value);
					}
					else if(title.equals("compilation_parameters")) {
						String[] parameters = value.split(" ");
						this.compilation_parameters.clear();
						for(String parameter : parameters) {
							if(!parameter.trim().isEmpty()) {
								this.compilation_parameters.add(parameter.trim());
							}
						}
					}
				}
			}
			reader.close();
		}
	}

	/* getters */
	/**
	 * @return mutation test project that the config serves for
	 */
	public MuTestProject get_project() { return this.project; }
	/**
	 * @return the utility instance for running command
	 */
	public MuCommandUtil get_command_util() { return this.command_util; }
	/**
	 * @return the system compiler used for compilation
	 */
	public CCompiler get_compiler() { return this.compiler; }
	/**
	 * @return the language standard used to parse source code
	 */
	public ClangStandard get_lang_standard() { return this.lang_std; }
	/**
	 * @return the parameters like -lm to compile C code
	 */
	public Iterable<String> get_compile_parameters() { return this.compilation_parameters; }
	/**
	 * @return configs/cruntime.txt for sizeof operation in static analysis
	 */
	public File get_sizeof_template_file() { return this.sizeof_template_file; }
	/**
	 * @return config/linux.h to -imacros the preprocess
	 */
	public File get_preprocess_macro_file() { return this.preprocess_macro_file; }
	/**
	 * @return config/jcmutest.h to compile the mutation code
	 */
	public File get_mutation_head_file() { return this.mutation_head_file; }
	/**
	 * @return config/jcinst.h to compile instrumental code
	 */
	public File get_instrument_head_file() { return this.instrument_head_file; }
	/**
	 * @return the configuration file in which parameters are preserved
	 */
	public File get_config_data_file() { return this.config_file; }
	/**
	 * @return the maximal seconds for running one test against program
	 */
	public long get_maximal_timeout_seconds() { return this.max_timeout_second; }

	/* setters */
	/**
	 * set the configuration data in mutation test project
	 * @param compiler the system compiler used for compilation
	 * @param lang_std the language standard used to parse source code
	 * @param compilation_parameters the parameters like -lm to compile C code
	 * @param sizeof_template_file configs/cruntime.txt for sizeof operation in static analysis
	 * @param instrument_head_file config/jcinst.h to compile instrumental code
	 * @param preprocess_macro_file config/linux.h to -imacros the preprocess
	 * @param mutation_head_file config/jcmutest.h to compile the mutation code
	 * @throws Exception
	 */
	protected void set(CCompiler compiler, ClangStandard lang_std,
			Iterable<String> compilation_parameters, File sizeof_template_file,
			File instrument_head_file, File preprocess_macro_file,
			File mutation_head_file, long max_timeout_seconds) throws Exception {
		if(compiler == null)
			throw new IllegalArgumentException("Invalid compiler: null");
		else if(lang_std == null)
			throw new IllegalArgumentException("Invalid lang_std: null");
		else if(compilation_parameters == null)
			throw new IllegalArgumentException("Invalid parameters: null");
		else if(sizeof_template_file == null || !sizeof_template_file.exists())
			throw new IllegalArgumentException("Invalid sizeof_template_file");
		else if(instrument_head_file == null || !instrument_head_file.exists())
			throw new IllegalArgumentException("Invalid instrument_head_file");
		else if(preprocess_macro_file == null || !preprocess_macro_file.exists())
			throw new IllegalArgumentException("Invalid preprocess_macro_file");
		else if(mutation_head_file == null || !mutation_head_file.exists())
			throw new IllegalArgumentException("Invalid mutation_head_file");
		else {
			this.compiler = compiler;
			this.lang_std = lang_std;
			this.max_timeout_second = max_timeout_seconds;
			this.compilation_parameters.clear();
			for(String parameter : compilation_parameters) {
				if(!parameter.trim().isEmpty()) {
					this.compilation_parameters.add(parameter.trim());
				}
			}
			FileOperations.copy(sizeof_template_file, this.sizeof_template_file);
			FileOperations.copy(instrument_head_file, this.instrument_head_file);
			FileOperations.copy(preprocess_macro_file, this.preprocess_macro_file);
			FileOperations.copy(mutation_head_file, this.mutation_head_file);
			this.save_config_file();
		}
	}

}
