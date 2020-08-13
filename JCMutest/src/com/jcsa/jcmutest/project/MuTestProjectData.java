package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.file.TestInputs;

/**
 * 	|--	[test]			// test files directory						<br>
 * 	|--	|--	test.suite	// file that preserve test suite data		<br>
 * 	|--	|--	[mutants]	// the data file preserving mutations		<br>
 * 	|--	|--	[inputs]	// the data files used in testing			<br>
 * 	|--	|--	[n_output]	// normal output files in testing			<br>
 * 	|--	|--	[s_output]	// instrumental output files in testing		<br>
 * 	|--	|--	[m_output]	// mutation output files in testing			<br>
 * 	|--	|--	[result]	// analysis result files after testing		<br>
 * 	|--	|--	instrument.txt | instrument.out | instrument.err		<br>
 * 
 * @author yukimula
 *
 */
public class MuTestProjectData {
	
	/* templates for generating shell script code */
	/** the head of the bash-shell script language **/
	private static final String bash_shell_head = "#! /bin/bash\n\n";
	/** the template for changing current directory **/
	private static final String cd_template = "cd %s\n\n";
	/** command for copying instrumental result to specified file **/
	private static final String copy_file_template = "cp %s %s\n";
	/** command for deleting the instrumental result file in testing **/
	private static final String remove_file_template = "rm %s\n";
	
	/* definitions */
	private MuTestProject project;
	private TestInputs test_space;
	protected MuTestProjectData(MuTestProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
			this.test_space = new TestInputs();
			this.test_space.load(project.get_files().get_test_suite_file());
		}
	}
	
	/* getters */
	/**
	 * @return the project on which the testing is performed
	 */
	public MuTestProject get_project() { return this.project; }
	/**
	 * @return the test input set used for executing tests
	 */
	public TestInputs get_test_space() { return this.test_space; }
	/**
	 * @return test suite file in which test inputs preserved
	 */
	public File get_test_suite_file() {
		return project.get_files().get_test_suite_file();
	}
	/**
	 * @return the directory in which the input data is used.
	 */
	public File get_inputs_directory() { 
		return this.project.get_files().get_inputs_directory(); 
	}
	/**
	 * @return the normal output directory
	 */
	public File get_n_output_directory() {
		return project.get_files().get_n_output_directory();
	}
	/**
	 * @return the instrumental output directory
	 */
	public File get_s_output_directory() {
		return project.get_files().get_s_output_directory();
	}
	/**
	 * @return the mutation output directory
	 */
	public File get_m_output_directory() {
		return project.get_files().get_m_output_directory();
	}
	/**
	 * @return the shell script file for running normal testing
	 */
	public File get_normal_shell_file() {
		return new File(this.project.get_files().
				get_efiles_directory().getAbsolutePath() + 
				"/" + this.project.get_name() + ".n.sh");
	}
	/**
	 * @return the shell script file for running instrumental testing
	 */
	public File get_instrumental_shell_file() {
		return new File(this.project.get_files().
				get_efiles_directory().getAbsolutePath() + 
				"/" + this.project.get_name() + ".s.sh");
	}
	/**
	 * @return the shell script file for running mutation testing
	 */
	public File get_mutation_shell_file() {
		return new File(this.project.get_files().
				get_efiles_directory().getAbsolutePath() + 
				"/" + this.project.get_name() + ".m.sh");
	}
	/**
	 * @return the executional directory
	 */
	public File get_exe_directory() { return project.get_files().get_efiles_directory(); }
	
	/* setters */
	private void set_test_space(Iterable<File> test_suite_files) throws Exception {
		this.test_space.clear();
		this.test_space.append(test_suite_files);
		this.test_space.save(this.get_test_suite_file());
	}
	private void gen_normal_shell(long timeout) throws Exception {
		File sfile = this.get_normal_shell_file();
		FileWriter writer = new FileWriter(sfile);
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, this.get_exe_directory().getAbsolutePath()));
		for(TestInput test : this.test_space.get_inputs()) {
			String command = test.command(this.
					project.get_code().get_normal_exe_file(), 
					this.get_n_output_directory(), timeout);
			writer.write(command);
			writer.write("\n");
		}
		writer.close();
	}
	private void gen_instrumental_shell(long timeout) throws Exception {
		File sfile = this.get_normal_shell_file();
		FileWriter writer = new FileWriter(sfile);
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, this.get_exe_directory().getAbsolutePath()));
		
		for(TestInput test : this.test_space.get_inputs()) {
			String ini_command = String.format(remove_file_template, project.
					get_files().get_instrument_txt_file().getAbsolutePath());
			writer.write(ini_command + "\n");
			
			String exe_command = test.command(this.
					project.get_code().get_instrumental_exe_file(), 
					project.get_files().get_instrument_out_file(), 
					project.get_files().get_instrument_err_file(), timeout);
			writer.write(exe_command + "\n");
			
			String sav_command = String.format(copy_file_template, 
					project.get_files().get_instrument_txt_file().getAbsolutePath(),
					test.get_instrument_file(this.get_s_output_directory()));
			writer.write(sav_command + "\n");
			
			writer.write("\n");
		}
		
		writer.close();
	}
	private void gen_mutation_shell(long timeout) throws Exception {
		File sfile = this.get_normal_shell_file();
		FileWriter writer = new FileWriter(sfile);
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, this.get_exe_directory().getAbsolutePath()));
		for(TestInput test : this.test_space.get_inputs()) {
			String command = test.command(this.
					project.get_code().get_normal_exe_file(), 
					this.get_m_output_directory(), timeout);
			writer.write(command);
			writer.write("\n");
		}
		writer.close();
	}
	/**
	 * set the test inputs and generate its shell script files for execution
	 * @param test_suite_files
	 * @throws Exception
	 */
	public void input_tests(Iterable<File> test_suite_files) throws Exception {
		this.set_test_space(test_suite_files);
		this.gen_normal_shell(0);
		this.gen_instrumental_shell(0);
		this.gen_mutation_shell(project.get_config().get_maximal_timeout_seconds());
	}
	/**
	 * Perform the tests against original program
	 * @throws Exception
	 */
	protected void execute_normal_tests() throws Exception {
		FileOperations.delete_in(this.get_n_output_directory());
		project.get_config().get_command_util().do_execute(
				this.get_normal_shell_file(), this.get_exe_directory());
	}
	/**
	 * Perform the tests against instrumental original program
	 * @throws Exception
	 */
	protected void execute_instrumental_tests() throws Exception {
		FileOperations.delete_in(this.get_s_output_directory());
		project.get_config().get_command_util().do_execute(
				this.get_instrumental_shell_file(), this.get_exe_directory());
	}
	/**
	 * Perform the tests against mutation program.
	 * @throws Exception
	 */
	protected void execute_mutation_tests() throws Exception {
		FileOperations.delete_in(this.get_m_output_directory());
		project.get_config().get_command_util().do_execute(
				this.get_mutation_shell_file(), this.get_exe_directory());
	}
	
}
