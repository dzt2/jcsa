package com.jcsa.jcparse.test.file;

import java.io.File;

import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.CommandUtil;
import com.jcsa.jcparse.test.cmd.CCompiler;

/**
 * 	The C-testing project to execute software testing on program provided.
 *
 * 	@author yukimula
 *
 */
public class JCTestProject {

	/* attributes */
	/** the file structure provided by the project **/
	private JCTestProjectFiles files;
	/** the configuration data used in test project **/
	private JCTestProjectConfig config;
	/** the interface to manage the code files in project **/
	private JCTestProjectCode code_part;
	/** the interface to manage the test files in project **/
	private JCTestProjectTest test_part;
	/** the interface to load the test results in project **/
	private JCTestProjectResult result_part;

	/* constructor */
	/**
	 * @param root the root directory where test project is created
	 * @param command_util the interface to execute command-line processing
	 * @throws Exception
	 */
	private JCTestProject(File root, CommandUtil command_util, boolean load_config) throws Exception {
		this.files = new JCTestProjectFiles(root);
		this.config = new JCTestProjectConfig(command_util);
		if(load_config)
			this.config.load(this.files.get_config_directory());
		this.code_part = new JCTestProjectCode(this);
		this.test_part = new JCTestProjectTest(this);
		this.result_part = new JCTestProjectResult(this);
	}

	/* getters */
	/**
	 * @return the name of test project.
	 */
	public String get_name() { return this.get_root_directory().getName(); }
	/**
	 * @return the root directory where the test project is created
	 */
	public File get_root_directory() { return this.files.get_root(); }
	/**
	 * @return the file structure created by the test project in C.
	 */
	public JCTestProjectFiles get_project_files() { return this.files; }
	/**
	 * @return the configuration data used to build up testing.
	 */
	public JCTestProjectConfig get_config() { return this.config; }
	/**
	 * @return the interface to manage the code files in project
	 */
	public JCTestProjectCode get_code_part() { return this.code_part; }
	/**
	 * @return the interface to manage the test files in project
	 */
	public JCTestProjectTest get_test_part() { return this.test_part; }
	/**
	 * @return the interface to load the test results in project
	 */
	public JCTestProjectResult get_result_part() { return this.result_part; }

	/* setters */
	/**
	 * set the source code, header and library files in the project
	 * and update the intermediate, executional files in the project.
	 * @param cfiles the source code files {xxx.c}
	 * @param hfiles the header code files {xxx.h}
	 * @param lfiles the library files used {xxx.lib}
	 * @throws Exception
	 */
	public void set_code(Iterable<File> cfiles,
			Iterable<File> hfiles, Iterable<File> lfiles) throws Exception {
		this.code_part.set(cfiles, hfiles, lfiles);
	}
	/**
	 * Update the intermediate code, instrumental code and executional files.
	 * @throws Exception
	 */
	public void update_code() throws Exception {
		this.code_part.update();
	}
	/**
	 * set the test inputs and input data directory with clearing the old test inputs.
	 * @param test_files the files that provide test inputs in project.
	 * @param inputs_directory the input data directory used for testing
	 * @throws Exception
	 */
	public void set_tests(Iterable<File> test_files, File inputs_directory) throws Exception {
		this.test_part.set_inputs_directory(inputs_directory);
		this.test_part.set_test_inputs(test_files);
	}
	/**
	 * add more test inputs in the given files to the test inputs.
	 * @param test_files
	 * @throws Exception
	 */
	public void add_tests(Iterable<File> test_files) throws Exception {
		this.test_part.add_test_inputs(test_files);
	}
	/**
	 * Perform normal program against the test inputs with specified long-time.
	 * @param test_inputs the set of test inputs used to execute normal pogram
	 * @param timeout the maximal seconds needed for executing each command-line of test input or
	 * 				  non-positive if no limitation is needed.
	 * @throws Exception
	 */
	public void normal_execute(Iterable<TestInput> test_inputs, long timeout) throws Exception {
		this.test_part.normal_execution(test_inputs, timeout);
	}
	/**
	 * Perform instrumental program against the test inputs with specified long-time.
	 * @param test_inputs
	 * @param timeout
	 * @throws Exception
	 */
	public void instrument_execute(Iterable<TestInput> test_inputs, long timeout) throws Exception {
		this.test_part.instrument_execution(test_inputs, timeout);
	}

	/* creator */
	/**
	 * create a new test project and update its configuration data
	 * @param root the directory where the test project is created.
	 * @param command_util the interface to execute command-line processing.
	 * @param compiler the compiler used to compile the source code files.
	 * @param lang_standard the language standard used for parsing code files.
	 * @param c_template_file the template file {config/cruntime.txt}
	 * @param c_instrument_head_file the instrumental header file {config/jcinst.h}
	 * @param c_pre_process_mac_file the pre-processing file for macros {config/linux.h}
	 * @param compile_parameters the parameters used for compilation
	 * @return C-test project with updating the configuration base.
	 * @throws Exception
	 */
	public static JCTestProject new_project(File root, CommandUtil command_util,
			CCompiler compiler, ClangStandard lang_standard, File c_template_file,
			File c_instrument_head_file, File c_pre_process_mac_file,
			Iterable<String> compile_parameters) throws Exception {
		JCTestProject project = new JCTestProject(root, command_util, false);
		project.config.set(compiler, lang_standard, c_template_file,
				c_instrument_head_file, c_pre_process_mac_file, compile_parameters);
		project.config.save(project.files.get_config_directory());
		return project;
	}
	/**
	 * open an existing test project with specified configuration.
	 * @param root the directory where the test project is created.
	 * @param command_util the interface to execute command-line processing.
	 * @return C-test project with the existing configuration base.
	 * @throws Exception
	 */
	public static JCTestProject open_project(File root, CommandUtil command_util) throws Exception {
		return new JCTestProject(root, command_util, true);
	}

}
