package com.jcsa.jcparse.test.file;

import java.io.File;

import com.jcsa.jcparse.test.exe.CommandUtil;

/**
 * It constructs the structure of files in test project as:<br>
 * <br>
 * 	[root]																	<br>
 * 	// code suite files														<br>
 * 	|--	[cfiles]		{xxx.c source code files before pre-processed}		<br>
 * 	|--	[ifiles]		{xxx.i source code files after pre-processed}		<br>
 * 	|--	[sfiles]		{xxx.s source code files being instrumented}		<br>
 * 	|--	[hfiles]		{xxx.h header files being included in pre-process}	<br>
 * 	|-- [lfiles]		{xxx.l library files to be linked in compilation}	<br>
 * 	|--	[efiles]		{xxx.exe, xxx.normal.sh, xxx.instrument.sh}			<br>
 * 	// test suite files														<br>
 * 	|--	test.suite		{test inputs data is preserved in this file}		<br>
 * 	|--	[inputs]		{directory where the data used in testing}			<br>
 * 	|--	[n_output]		{directory where the normal outputs are saved}		<br>
 * 	|--	[i_output]		{directory where the instrumental outputs saved}	<br>
 * 	|-- instrument.txt	{intermediate file to save instrumental results}	<br>
 * 	// project files														<br>
 * 	|--	[config]		{directory where the configuration data is saved}	<br>
 * 	|--	name.project	{project file being saved}							<br>
 * @author yukimula
 *
 */
public class JCTestProjectFiles {
	
	/* parameters for file names */
	private static final String c_file_directory_name = "cfiles";
	private static final String i_file_directory_name = "ifiles";
	private static final String s_file_directory_name = "sfiles";
	private static final String h_file_directory_name = "hfiles";
	private static final String l_file_directory_name = "lfiles";
	private static final String exe_directory_name = "efiles";
	private static final String test_suite_file_name = "test.suite";
	private static final String input_data_directory_name = "inputs";
	private static final String normal_output_directory_name = "n_output";
	private static final String instrument_output_directory_name = "i_output";
	private static final String instrument_result_file_name = "instrument.txt";
	private static final String config_directory_name = "config";
	
	/* attributes */
	/** root directory where the project is saved **/
	private File root;
	/** the directory where C source files {before pre-processed} **/
	private File c_file_directory;
	/** the directory where C source files {after pre-processed} **/
	private File i_file_directory;
	/** the directory where instrumental code files are preserved **/
	private File s_file_directory;
	/** the directory where the header files are saved **/
	private File h_file_directory;
	/** the directory where the external library files are linked **/
	private File l_file_directory;
	/** the directory where exe, shell script file are generated **/
	private File exe_directory;
	/** the file where the test inputs are saved. **/
	private File test_suite_file;
	/** the directory of which test input data used is included **/
	private File input_data_directory;
	/** the directory where the normal testing outputs are saved **/
	private File normal_output_directory;
	/** the directory where the instrumental test outputs saved **/
	private File instrument_output_directory;
	/** the file to preserve the instrumental analysis results **/
	private File instrument_result_file;
	/** the directory where the configuration data is saved **/
	private File config_directory;
	
	/* constructor */
	/**
	 * @param dir the directory that is going to be created
	 * @return
	 * @throws Exception
	 */
	private File try_to_mkdir(File dir) throws Exception {
		if(!dir.exists()) {
			dir.mkdir();
			while(!dir.exists());
		}
		return dir;
	}
	/**
	 * @param file the file to be created
	 * @return
	 * @throws Exception
	 */
	private File try_to_create(File file) throws Exception {
		if(!file.exists()) {
			CommandUtil.write_text(file, "");
			while(!file.exists());
		}
		return file;
	}
	/**
	 * create the file structure of test project.
	 * @param root
	 * @throws Exception
	 */
	protected JCTestProjectFiles(File root) throws Exception {
		if(root == null)
			throw new IllegalArgumentException("Invalid root: null");
		else {
			this.root = this.try_to_mkdir(root);
			this.c_file_directory = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + c_file_directory_name));
			this.i_file_directory = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + i_file_directory_name));
			this.s_file_directory = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + s_file_directory_name));
			this.h_file_directory = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + h_file_directory_name));
			this.l_file_directory = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + l_file_directory_name));
			this.exe_directory = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + exe_directory_name));
			this.test_suite_file = this.try_to_create(new File(root.getAbsolutePath() + "/" + test_suite_file_name));
			this.input_data_directory = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + input_data_directory_name));
			this.normal_output_directory = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + normal_output_directory_name));
			this.instrument_output_directory = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + instrument_output_directory_name));
			this.instrument_result_file = this.try_to_create(new File(root.getAbsolutePath() + "/" + instrument_result_file_name));
			this.config_directory = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + config_directory_name));
		}
	}
	
	/* getters */
	/**
	 * @return root directory where the project is saved
	 */
	public File get_root() { return this.root; }
	/**
	 * @return the directory where C source files {before pre-processed}
	 */
	public File get_c_file_directory() { return this.c_file_directory; }
	/**
	 * @return the directory where C source files {after pre-processed}
	 */
	public File get_i_file_directory() { return this.i_file_directory; }
	/**
	 * @return the directory where instrumental code files are preserved
	 */
	public File get_s_file_directory() { return this.s_file_directory; }
	/**
	 * @return the directory where the header files are saved
	 */
	public File get_h_file_directory() { return this.h_file_directory; }
	/**
	 * @return the directory where the external library files are linked
	 */
	public File get_l_file_directory() { return this.l_file_directory; }
	/**
	 * @return the directory where exe, shell script file are generated
	 */
	public File get_exe_directory() { return this.exe_directory; }
	/**
	 * @return the file where the test inputs are saved.
	 */
	public File get_test_suite_file() { return this.test_suite_file; }
	/**
	 * @return the directory of which test input data used is included
	 */
	public File get_input_data_directory() { return this.input_data_directory; }
	/**
	 * @return the directory where the normal testing outputs are saved
	 */
	public File get_normal_output_directory() { return this.normal_output_directory; }
	/**
	 * @return the directory where the instrumental test outputs saved
	 */
	public File get_instrument_output_directory() { return this.instrument_output_directory; }
	/**
	 * @return the file to preserve the instrumental analysis results
	 */
	public File get_instrument_result_file() { return this.instrument_result_file; }
	/**
	 * @return the directory where the configuration data is saved
	 */
	public File get_config_directory() { return this.config_directory; }
	
}
