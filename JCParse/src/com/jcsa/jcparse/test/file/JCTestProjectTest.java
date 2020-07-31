package com.jcsa.jcparse.test.file;

import java.io.File;

import com.jcsa.jcparse.test.CommandUtil;

/**
 * It provides the interfaces to execute the testing part of project.
 * 	---	[efiles/xxx.sh]
 * 	---	[efiles/xxx.ins.sh]
 * 	--- test.suite
 * 	---	[inputs]
 * 	--- [n_output]
 * 	--- [i_output]
 * 	
 * @author yukimula
 *
 */
public class JCTestProjectTest {
	
	/* constructor */
	/** the test project that performs on the testing **/
	private JCTestProject project;
	/** the test inputs used for testing the programs **/
	private TestInputs test_inputs;
	/**
	 * create the testing space in the test project.
	 * @param project
	 */
	protected JCTestProjectTest(JCTestProject project) throws Exception {
		this.project = project;
		this.test_inputs = new TestInputs();
		this.test_inputs.load(project.get_project_files().get_test_suite_file());
	}
	
	/* getters */
	/**
	 * @return the shell script file for executing normal program
	 */
	public File get_normal_shell_file() {
		return new File(this.project.get_project_files().
				get_exe_directory().getAbsolutePath() + "/" + 
				this.project.get_name() + ".sh");
	}
	/**
	 * @return the shell script file for executing instrumental program
	 */
	public File get_instrumental_shell_file() {
		return new File(this.project.get_project_files().
				get_exe_directory().getAbsolutePath() + "/" + 
				this.project.get_name() + ".ins.sh");
	}
	/**
	 * @return the test inputs used for testing project.
	 */
	public TestInputs get_test_inputs() { return this.test_inputs; }
	/**
	 * @return the total number of test inputs for running the project
	 */
	public int number_of_test_inputs() { return this.test_inputs.number_of_inputs(); }
	/**
	 * @param k
	 * @return the kth test input in the project
	 * @throws IndexOutOfBoundsException
	 */
	public TestInput get_test_input(int k) throws IndexOutOfBoundsException {
		return this.test_inputs.get_input(k);
	}
	/**
	 * @return the directory where the input data files are used.
	 */
	public File get_inputs_directory() {
		return this.project.get_project_files().get_input_data_directory();
	}
	/**
	 * @return the directory where the normal outputs are generated.
	 */
	public File get_normal_output_directory() {
		return this.project.get_project_files().get_normal_output_directory();
	}
	/**
	 * @return the directory where the instrumental outputs are generated.
	 */
	public File get_instrumental_output_directory() {
		return this.project.get_project_files().get_instrument_output_directory();
	}
	
	/* setters */
	private void copy_by(File source, File target) throws Exception {
		if(source.isDirectory()) {
			CommandUtil.make_directory(target);
			File[] files = source.listFiles();
			for(File file : files) {
				File tfile = new File(target.getAbsolutePath() + "/" + file.getName());
				this.copy_by(file, tfile);
			}
		}
		else {
			CommandUtil.copy_file(source, target);
		}
	}
	/**
	 * set the input data used in the project.
	 * @param input_dir
	 * @throws Exception
	 */
	protected void set_inputs_directory(File input_dir) throws Exception {
		CommandUtil.delete_files_in(this.get_inputs_directory());
		this.copy_by(this.get_inputs_directory(), input_dir);
	}
	/**
	 * remove the old test inputs and set new test inputs in the space
	 * @param test_files the set of test inputs file being used
	 * @throws Exception
	 */
	protected void set_test_inputs(Iterable<File> test_files) throws Exception {
		this.test_inputs.clear();
		this.test_inputs.append(test_files);
		this.test_inputs.save(this.project.get_project_files().get_test_suite_file());
	}
	/**
	 * add the new test inputs to the given test suite files
	 * @param test_files the test suite files that provide test inputs
	 * @throws Exception
	 */
	protected void add_test_inputs(Iterable<File> test_files) throws Exception {
		this.test_inputs.append(test_files);
		this.test_inputs.save(this.project.get_project_files().get_test_suite_file());
	}
	
	/* execution methods */
	/**
	 * Perform normal program against the test inputs with specified long-time.
	 * @param inputs the set of test inputs
	 * @param timeout the maximal seconds needed for executing each command-line of test input or 
	 * 			non-positive if no limitation is needed.
	 * @throws Exception
	 */
	protected void normal_execution(Iterable<TestInput> inputs, long timeout) throws Exception {
		CommandUtil util = this.project.get_config().get_command_util();
		JCTestProjectFiles pfiles = this.project.get_project_files();
		
		if(!util.gen_normal_test_shell(pfiles.get_exe_directory(), 
				this.project.get_code_part().get_executional_file(), 
				inputs, this.get_normal_output_directory(), 
				timeout, this.get_normal_shell_file())) {
			throw new RuntimeException("Unable to generate " + 
				this.get_normal_shell_file().getAbsolutePath());
		}
		
		CommandUtil.delete_files_in(this.get_normal_output_directory());
		
		if(!util.do_execute_shell(this.
				get_normal_shell_file(), pfiles.get_exe_directory())) {
			throw new RuntimeException("Fails occurs in executing tests");
		}
	}
	/**
	 * Perform instrumental program against the test inputs with specified long-time.
	 * @param inputs the set of test inputs
	 * @param timeout the maximal seconds needed for executing each command-line of test input or 
	 * 			non-positive if no limitation is needed.
	 * @throws Exception
	 */
	protected void instrument_execution(Iterable<TestInput> inputs, long timeout) throws Exception {
		CommandUtil util = this.project.get_config().get_command_util();
		JCTestProjectFiles pfiles = this.project.get_project_files();
		
		if(!util.gen_instrumental_shell(pfiles.get_exe_directory(), 
				this.project.get_code_part().get_instrument_executional_file(), 
				inputs, pfiles.get_instrument_result_file(), 
				this.get_instrumental_output_directory(), timeout, 
				this.get_instrumental_shell_file())) {
			throw new RuntimeException("Unable to generate " + 
					this.get_instrumental_shell_file().getAbsolutePath());
		}
		
		CommandUtil.delete_files_in(this.get_instrumental_output_directory());
		
		if(!util.do_execute_shell(this.
				get_instrumental_shell_file(), pfiles.get_exe_directory())) {
			throw new RuntimeException("Fails occurs in executing tests");
		}
	}
	
}
