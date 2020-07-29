package com.jcsa.jcparse.test;

import java.io.File;

import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.exe.CCompiler;
import com.jcsa.jcparse.test.exe.CommandUtil;

/**
 * The configuration data used in C-Testing Project, including:<br>
 * 	---	c_template_file	: config/cruntime.txt<br>
 * 	---	c_instrument_head_file: config/jcinst.h<br>
 * 	---	c_pre_process_mac_file: config/linux.h<br>
 * 	---	lang_standard: ClangStandard for parse<br>
 * 
 * @author yukimula
 *
 */
public class JCTestConfig {
	
	/** the name of the compiler to compile the source code files **/
	private static final String compiler_file_name = "compiler.name";
	/** the name of the language standard used for parsing the C program **/
	private static final String lang_standard_file_name = "lang.standard";
	/** the name of C template for parsing the source code file **/
	private static final String c_template_file_name = "cruntime.txt";
	/** the name of the header file for implementing instrumental code **/
	private static final String c_instrument_head_file_name = "jcinst.h";
	/** the name of the header file for pre-processing the source file **/
	private static final String c_pre_process_mac_file_name = "linux.h";
	
	/** the compiler used for C program compilations **/
	private CCompiler compiler;
	/** the C language standard used for parsing .i file **/
	private ClangStandard lang_standard;
	/** the config/cruntime.txt used for parsing .i file **/
	private File c_template_file;
	/** the config/jcinst.h used for implementing instrumental program **/
	private File c_instrument_head_file;
	/** the config/linux.h used for pre-processing .c file with -imacros **/
	private File c_pre_process_mac_file;
	
	/**
	 * @param lang_standard	the C language standard used for parsing .i file
	 * @param c_template_file the config/cruntime.txt used for parsing .i file
	 * @param c_instrument_head_file the config/jcinst.h used for implementing instrumental program
	 * @param c_pre_process_mac_file the config/linux.h used for pre-processing .c file with -imacros
	 * @throws Exception
	 */
	public JCTestConfig() { }
	
	/* getters */
	/**
	 * @return the compiler used to compile the .c source code files
	 */
	public CCompiler get_compiler() { return this.compiler; }
	/**
	 * @return the C language standard used for parsing .i file
	 */
	public ClangStandard get_lang_standard() { return this.lang_standard; }
	/**
	 * @return the config/cruntime.txt used for parsing .i file
	 */
	public File get_c_template_file() { return this.c_template_file; }
	/**
	 * @return the config/jcinst.h used for implementing instrumental program
	 */
	public File get_c_instrument_head_file() { return this.c_instrument_head_file; }
	/**
	 * @return the config/linux.h used for pre-processing .c file with -imacros
	 */
	public File get_c_pre_process_mac_file() { return this.c_pre_process_mac_file; }
	
	/* setters */
	/**
	 * set the configuration data in the object
	 * @param lang_standard
	 * @param c_template_file
	 * @param c_instrument_head_file
	 * @param c_pre_process_mac_file
	 * @throws Exception
	 */
	public void set(CCompiler compiler, ClangStandard lang_standard, File c_template_file, 
			File c_instrument_head_file, File c_pre_process_mac_file) throws Exception {
		if(compiler == null)
			throw new IllegalArgumentException("Invalid compiler: null");
		else if(lang_standard == null)
			throw new IllegalArgumentException("Invalid language-standard: null");
		else if(c_template_file == null || !c_template_file.exists())
			throw new IllegalArgumentException("Invalid C-template-file: null");
		else if(c_instrument_head_file == null || !c_instrument_head_file.exists())
			throw new IllegalArgumentException("Invalid C-instrumental-header-file");
		else if(c_pre_process_mac_file == null || !c_pre_process_mac_file.exists())
			throw new IllegalArgumentException("Invalid C-Pre-Process-header-file");
		else {
			this.compiler = compiler;
			this.lang_standard = lang_standard;
			this.c_template_file = c_template_file;
			this.c_instrument_head_file = c_instrument_head_file;
			this.c_pre_process_mac_file = c_pre_process_mac_file;
		}
	}
	/**
	 * save the configuration on the specified config directory.
	 * @param config_dir the directory where the configuration data is saved.
	 * @throws Exception
	 */
	public void save(File config_dir) throws Exception {
		if(config_dir == null || !config_dir.isDirectory()) {
			throw new IllegalArgumentException("Invalid directory: null");
		}
		else {
			CommandUtil.write_text(new File(config_dir.getAbsolutePath() + 
					"/" + compiler_file_name), this.compiler.toString());
			CommandUtil.write_text(new File(config_dir.getAbsolutePath() + 
					"/" + lang_standard_file_name), this.lang_standard.toString());
			CommandUtil.copy_file(this.c_template_file, new File(config_dir.
					getAbsolutePath() + "/" + c_template_file_name));
			CommandUtil.copy_file(this.c_instrument_head_file, new File(config_dir.
					getAbsolutePath() + "/" + c_instrument_head_file_name));
			CommandUtil.copy_file(this.c_pre_process_mac_file, new File(config_dir.
					getAbsolutePath() + "/" + c_pre_process_mac_file_name));
		}
	}
	/**
	 * load the configuration data to the object
	 * @param config_dir
	 * @throws Exception
	 */
	public void load(File config_dir) throws Exception {
		if(config_dir == null || !config_dir.isDirectory()) {
			throw new IllegalArgumentException("Invalid directory: null");
		}
		else {
			String compiler_text = CommandUtil.read_text(
					new File(config_dir.getAbsolutePath() + "/" + compiler_file_name));
			String lang_std_text = CommandUtil.read_text(
					new File(config_dir.getAbsolutePath() + "/" + lang_standard_file_name));
			CCompiler compiler = CCompiler.valueOf(compiler_text.strip());
			ClangStandard lang_standard = ClangStandard.valueOf(lang_std_text.strip());
			File c_template_file = new File(config_dir.getAbsolutePath() + "/" + c_template_file_name);
			File c_instrument_head_file = new File(config_dir.getAbsolutePath() + "/" + c_instrument_head_file_name);
			File c_pre_process_mac_file = new File(config_dir.getAbsolutePath() + "/" + c_pre_process_mac_file_name);
			this.set(compiler, lang_standard, c_template_file, c_instrument_head_file, c_pre_process_mac_file); 
			return;
		}
	}
	
}
