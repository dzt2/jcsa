package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.file.TestInputs;

/**
 * <code>
 * 	|--	[test]			// test files directory						<br>
 * 	|--	|--	test.suite	// file that preserve test suite data		<br>
 * 	|--	|--	[inputs]	// the data files used in testing			<br>
 * 	|--	|--	[n_output]	// normal output files in testing			<br>
 * 	|--	|--	[s_output]	// instrumental output files in testing		<br>
 * 	|--	|--	[m_output]	// mutation output files in testing			<br>
 * 	|--	|--	instrument.txt | instrument.out | instrument.err		<br>
 * </code>
 * @author yukimula
 *
 */
public class MuTestInputSpace {
	
	/** the head of the bash-shell script language **/
	private static final String bash_shell_head = "#! /bin/bash\n\n";
	/** the template for changing current directory **/
	private static final String cd_template = "cd %s\n\n";
	/** command for deleting the instrumental result file in testing **/
	private static final String remove_files_template = "rm %s/*\n";
	/** command for copying source to target file **/
	private static final String copy_file_template = "cp %s %s\n";
	
	/* definition */
	private MuTestProject project;
	private TestInputs test_space;
	protected MuTestInputSpace(MuTestProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
			this.test_space = new TestInputs();
			this.update_test_space();
		}
	}
	
	/* getters */
	/**
	 * @return mutation test project in which the code part is defined
	 */
	public MuTestProject get_project() { return this.project; }
	/**
	 * @return the set of test inputs used to execute testing
	 */
	public TestInputs get_test_space() { return this.test_space; }
	/**
	 * @return test.suite file to preserve the test inputs in space
	 */
	public File get_test_suite_file() { return project.get_files().get_test_suite_file(); }
	/**
	 * @return the directory of inputs data used for testing
	 */
	public File get_inputs_directory() { return project.get_files().get_inputs_directory(); }
	/**
	 * @return the directory of normal outputs in testing
	 */
	public File get_normal_output_directory() { return project.get_files().get_n_output_directory(); }
	/**
	 * @return the directory of normal outputs in testing
	 */
	public File get_instrument_output_directory() { return project.get_files().get_s_output_directory(); }
	/**
	 * @return the directory of mutation outputs in testing
	 */
	public File get_mutation_output_directory() { return project.get_files().get_m_output_directory(); }
	/**
	 * @return instrumental result file
	 */
	public File get_instrument_txt_file() { return project.get_files().get_instrument_txt_file(); }
	/**
	 * @return to preserve the output from instrumental testing
	 */
	public File get_instrument_out_file() { return project.get_files().get_instrument_out_file(); }
	/**
	 * @return to preserve the errors from instrumental testing
	 */
	public File get_instrument_err_file() { return project.get_files().get_instrument_err_file(); }
	private void update_test_space() throws Exception {
		if(this.get_test_suite_file().exists()) {
			this.test_space.load(this.get_test_suite_file());
		}
	}
	
	/* setters */
	/**
	 * Set the test inputs and save them in test suite file and generate the test scripts files
	 * @param test_suite_files
	 * @throws Exception
	 */
	protected void set_test_space(Iterable<File> test_suite_files) throws Exception {
		this.test_space.clear();
		this.test_space.append(test_suite_files);
		this.test_space.save(this.get_test_suite_file());
		this.project.get_exec_space().generate_test_scripts();
	}
	/**
	 * #!bash
	 * cd efiles
	 * rm normal_outputs/*
	 * efile inputs (timeout) > output 2> errors
	 * @param efile executional file
	 * @param sfile shell script file
	 * @param timeout maximal seconds for running one test
	 * @throws Exception
	 */
	protected void set_normal_shell_script(File efile, File sfile, long timeout) throws Exception {
		FileWriter writer = new FileWriter(sfile);
		
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, project.get_files().get_efiles_directory().getAbsolutePath()));
		
		for(TestInput input : this.test_space.get_inputs()) {
			String command = input.command(efile, this.get_normal_output_directory(), timeout);
			writer.write(String.format(remove_files_template, this.get_normal_output_directory().getAbsolutePath()));
			writer.write(command);
			writer.write("\n\n");
		}
		
		writer.close();
	}
	/**
	 * #!bash
	 * cd efiles
	 * rm normal_outputs/*
	 * efile inputs (timeout) > output 2> errors
	 * @param efile executional file compiled from mutation (mfiles)
	 * @param sfile shell script file
	 * @param timeout maximal seconds for running one test
	 * @throws Exception
	 */
	protected void set_mutation_shell_script(File efile, File sfile, long timeout) throws Exception {
		FileWriter writer = new FileWriter(sfile);
		
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, project.get_files().get_efiles_directory().getAbsolutePath()));
		
		for(TestInput input : this.test_space.get_inputs()) {
			String command = input.command(efile, this.get_mutation_output_directory(), timeout);
			writer.write(String.format(
					remove_files_template, this.get_mutation_output_directory().getAbsolutePath()));
			writer.write(command);
			writer.write("\n\n");
		}
		
		writer.close();
	}
	/**
	 * #!bash
	 * 
	 * cd efiles/
	 * 
	 * rm s_output/*
	 * 
	 * command
	 * cp instrument_txt_file s_output_file 
	 * 
	 * @param efile
	 * @param sfile
	 * @param timeout
	 * @throws Exception
	 */
	protected void set_instrumental_shell_script(File efile, File sfile, long timeout) throws Exception {
		FileWriter writer = new FileWriter(sfile);
		
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, project.get_files().get_efiles_directory().getAbsolutePath()));
		writer.write(String.format(remove_files_template, this.get_instrument_output_directory().getAbsolutePath()));
		
		for(TestInput input : this.test_space.get_inputs()) {
			writer.write("\n");
			writer.write(input.command(efile, this.get_instrument_out_file(), this.get_instrument_err_file(), timeout));
			writer.write("\n");
			writer.write(String.format(copy_file_template, this.get_instrument_txt_file().getAbsolutePath(), 
					input.get_instrument_file(this.get_instrument_output_directory()).getAbsolutePath()));
		}
		
		writer.close();
	}
	
}
