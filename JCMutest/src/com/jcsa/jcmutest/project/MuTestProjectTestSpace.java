package com.jcsa.jcmutest.project;

import java.io.File;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcparse.test.file.TestInputs;

/**
 * <code>
 * 	|--	[test]			// test files directory						<br>
 * 	|--	|--	test.suite	// file that preserve test suite data		<br>
 * 	|--	|--	[inputs]	// the data files used in testing			<br>
 * 	|--	|--	[n_output]	// normal output files in testing			<br>
 * 	|--	|--	[s_output]	// instrumental output files in testing		<br>
 * 	|--	|--	[m_output]	// mutation output files in testing			<br>
 * 	|--	|--	[result]	// analysis result files after testing		<br>
 * 	|--	|--	instrument.txt | instrument.out | instrument.err		<br>
 * </code>
 * @author yukimula
 *
 */
public class MuTestProjectTestSpace {
	
	/* definition */
	private MuTestProject project;
	private TestInputs test_space;
	protected MuTestProjectTestSpace(MuTestProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
			this.test_space = new TestInputs();
			this.load();
		}
	}
	
	/* getters */
	/**
	 * @return mutation test project in which the test space created
	 */
	public MuTestProject get_project() { return this.project; }
	/**
	 * @return the set of test inputs used to execute the program
	 */
	public TestInputs get_test_space() { return this.test_space; }
	/**
	 * @return test suite data file in which test inputs are provided
	 */
	public File get_test_suite_file() { 
		return this.project.get_files().get_test_suite_file(); 
	}
	/**
	 * @return the directory which provides data for testing
	 */
	public File get_inputs_directory() {
		return this.project.get_files().get_inputs_directory();
	}
	/**
	 * @return the directory where test outputs from original program are preserved
	 */
	public File get_normal_output_directory() {
		return this.project.get_files().get_n_output_directory();
	}
	/**
	 * @return the directory where outputs from instrumental program are preserved
	 */
	public File get_instrumental_output_directory() {
		return this.project.get_files().get_s_output_directory();
	}
	/**
	 * @return the directory where outputs from mutation program are preserved
	 */
	public File get_mutation_output_directory() {
		return this.project.get_files().get_m_output_directory();
	}
	/**
	 * @return the directory where mutation test results are preserved
	 */
	public File get_result_directory() {
		return this.project.get_files().get_result_directory();
	}
	/**
	 * @param mutant
	 * @return get the file of test results with mutant specified
	 */
	private File get_test_result_file(Mutant mutant) {
		return new File(this.get_result_directory().getAbsolutePath() + "/" + mutant.get_id());
	}
	/**
	 * @param mutant
	 * @return the test result of the mutant loaded from result directory
	 * @throws Exception
	 */
	public MuTestProjectTestResult get_test_result(Mutant mutant) throws Exception {
		MuTestProjectTestResult result = new MuTestProjectTestResult(mutant);
		result.load(this.get_test_result_file(mutant)); return result;
	}
	/**
	 * @return the file that saves the instrumental result data
	 */
	public File get_instrumental_txt_file() {
		return this.project.get_files().get_instrument_txt_file();
	}
	/**
	 * @return the file that saves the standard output of instrumental program
	 */
	public File get_instrumental_out_file() {
		return this.project.get_files().get_instrument_out_file();
	}
	/**
	 * @return the file that saves the standard error of instrumental program
	 */
	public File get_instrumental_err_file() {
		return this.project.get_files().get_instrument_err_file();
	}
	
	/* setters */
	private void load() throws Exception {
		this.test_space.clear();
		if(this.get_test_suite_file().exists()) {
			this.test_space.load(this.get_test_suite_file());
		}
	}
	/**
	 * put the inputs from test-suite-files into the test space and save in file
	 * @param test_suite_files
	 * @throws Exception
	 */
	protected void set_test_inputs(Iterable<File> test_suite_files, File inputs_directory) throws Exception {
		this.test_space.clear();
		this.test_space.append(test_suite_files);
		this.test_space.save(this.get_test_suite_file());
		
		FileOperations.delete_in(this.get_inputs_directory());
		if(inputs_directory.isDirectory()) {
			FileOperations.copy_all(inputs_directory, this.get_inputs_directory());
		}
	}
	/**
	 * take the results in current n_outputs and m_outputs as the outputs during
	 * testing the mutant as provided and generate its result and save in file.
	 * @param mutant
	 * @throws Exception
	 */
	protected MuTestProjectTestResult save_test_result(Mutant mutant) throws Exception {
		MuTestProjectTestResult result = new MuTestProjectTestResult(mutant);
		result.update(this.test_space, 
				this.get_normal_output_directory(), 
				this.get_mutation_output_directory(), 
				this.get_test_result_file(mutant));
		return result;
	}
	
}
