package com.jcsa.jcmutest.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcparse.base.BitSequence;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.file.TestInputs;
import com.jcsa.jcparse.test.inst.InstrumentReader;
import com.jcsa.jcparse.test.inst.InstrumentalLine;

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
	 * @return all the test inputs within the project space
	 */
	public Collection<TestInput> get_test_inputs() {
		return (Collection<TestInput>) this.test_space.get_inputs();
	}
	/**
	 * @return the number of test inputs defined in this project space
	 */
	public int number_of_test_inputs() { 
		return this.test_space.number_of_inputs(); 
	}
	/**
	 * @param beg_id
	 * @param end_id
	 * @return the test inputs of which id ranges from [beg_id, end_id)
	 * @throws Exception
	 */
	public Collection<TestInput> get_test_inputs(int beg_id, int end_id) throws Exception {
		List<TestInput> inputs = new ArrayList<TestInput>();
		for(TestInput input : this.test_space.get_inputs()) {
			if(input.get_id() >= beg_id && input.get_id() < end_id) {
				inputs.add(input);
			}
		}
		return inputs;
	}
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
	/**
	 * @param mutant
	 * @return the file that preserves the test results of the mutant
	 * @throws Exception
	 */
	private File get_test_result_file(Mutant mutant) throws Exception {
		String name = mutant.get_mutation().get_location().
				get_tree().get_source_file().getName();
		return new File(this.get_result_directory().getAbsolutePath() + 
				"/" + name + mutant.get_id() + ".rs");
	}
	/**
	 * @param mutant
	 * @return the test results w.r.t. the mutant or null if it has not been tested.
	 * @throws Exception
	 */
	public MuTestProjectTestResult get_test_result(Mutant mutant) throws Exception {
		return MuTestProjectTestResult.load(mutant, 
				this.test_space.number_of_inputs(),
				this.get_test_result_file(mutant));
	}
	/**
	 * @param input
	 * @return the path of instrumental test results or null if input is not executed before.
	 * @throws Exception
	 */
	public List<InstrumentalLine> read_instrumental_lines(CRunTemplate 
			sizeof_template, AstTree tree, TestInput input) throws Exception {
		File instrumental_file = input.
				get_instrument_file(this.get_instrumental_output_directory());
		if(instrumental_file.exists()) {
			InstrumentReader reader = new InstrumentReader(
					sizeof_template, tree, instrumental_file);
			List<InstrumentalLine> lines = new ArrayList<InstrumentalLine>();
			
			InstrumentalLine line;
			while((line = reader.next()) != null) { lines.add(line); }
			return lines;
		}
		else {
			return null;	/* no instrumental results are found in */
		}
	}
	
	/* setters */
	private void load() throws Exception {
		this.test_space.clear();
		if(this.get_test_suite_file().exists()) {
			this.test_space.load(this.get_test_suite_file());
		}
	}
	/**
	 * set the data files in inputs directory
	 * @param inputs_directory
	 * @throws Exception
	 */
	protected void set_inputs_directory(File inputs_directory) throws Exception {
		if(inputs_directory == null || !inputs_directory.isDirectory())
			throw new IllegalArgumentException("Not inputs-directory");
		else {
			FileOperations.delete_in(this.get_inputs_directory());
			FileOperations.copy_all(inputs_directory, this.get_inputs_directory());
		}
	}
	/**
	 * append the test inputs in test suite files are appended in the test space
	 * @param test_suite_files
	 * @throws Exception
	 */
	protected void add_test_inputs(Iterable<File> test_suite_files) throws Exception {
		this.test_space.append(test_suite_files);
		this.test_space.save(this.get_test_suite_file());
	}
	/**
	 * update the test result information w.r.t. mutant using the newest results in
	 * n_outputs and m_outputs.
	 * 
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	protected MuTestProjectTestResult update_test_result(Mutant mutant) throws Exception {
		/* get the test result data from current space */
		MuTestProjectTestResult result = this.get_test_result(mutant);
		if(result == null) {
			result = new MuTestProjectTestResult(mutant, 
					this.test_space.number_of_inputs());
		}
		
		/* update the test result information for mutant */
		File n_outputs = this.get_normal_output_directory();
		File m_outputs = this.get_mutation_output_directory();
		for(TestInput input : this.test_space.get_inputs()) {
			/* get the output files for determining the result */
			File n_stdout = input.get_stdout_file(n_outputs);
			File n_stderr = input.get_stderr_file(n_outputs);
			File m_stdout = input.get_stdout_file(m_outputs);
			File m_stderr = input.get_stderr_file(m_outputs);
			
			if(m_stdout.exists() || m_stderr.exists()) {
				/* record the mutation is executed against the test input */
				result.get_exec_set().set(input.get_id(), BitSequence.BIT1);
				
				/* mutation is killed iff. its stdout or stderr different */
				if(n_stdout.exists() && n_stderr.exists()) {
					if(!this.compare_file(n_stdout, m_stdout)
							|| !this.compare_file(n_stderr, m_stderr)) {
						result.get_kill_set().set(input.get_id(), BitSequence.BIT1);
					}
					else {
						result.get_kill_set().set(input.get_id(), BitSequence.BIT0);
					}
				}
				else {
					/* unable to update the test result since original outputs lost */
				}
			}
		}
		
		/* save the updated data information to file */
		result.save(this.get_test_result_file(mutant));
		return result;
	}
	/**
	 * @param n_output
	 * @param m_output
	 * @return whether two files are identical with their content
	 * @throws Exception
	 */
	private boolean compare_file(File n_output, File m_output) throws Exception {
		if(!n_output.exists())
			return false;	/* none of output from original program */
		else {
			String n_output_text = FileOperations.read(n_output);
			String m_output_text = FileOperations.read(m_output);
			return m_output_text.equals(n_output_text);
		}
	}
	
}
