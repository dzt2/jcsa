package com.jcsa.jcmutest.project;

import java.io.File;

import com.jcsa.jcparse.test.file.TestInputs;

/**
 * <code>
 * 	|--	[test]			// test files directory						<br>
 * 	|--	|--	test.suite	// file that preserve test suite data		<br>
 * 	|--	|--	[mutants]	// the data file preserving mutations		<br>
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
public class MuTestProjectTest {
	
	/* definition */
	private MuTestProject project;
	private TestInputs test_space;
	protected MuTestProjectTest(MuTestProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project");
		else {
			this.project = project;
			this.test_space = new TestInputs();
		}
	}
	
	/* getters */
	/**
	 * @return the mutation test project that defines test space
	 */
	public MuTestProject get_project() { return this.project; }
	/**
	 * @return the space in which the test inputs are preserved
	 */
	public TestInputs get_test_space() { return this.test_space; }
	/**
	 * @return test/inputs where the input data files are preserved
	 */
	public File get_inputs_directory() { return project.get_files().get_inputs_directory(); }
	
	
	
}
