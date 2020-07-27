package com.jcsa.jcparse.test.inp;

/**
 * It defines the test input for running the program under test.
 * 
 * @author yukimula
 *
 */
public class TestInput {
	
	/** the test input space **/
	private TestInputs inputs;
	/** the unique integer ID of test input **/
	private int id;
	/** the command-line parameter to run the program **/
	private String parameter;
	
	/**
	 * @param inputs the space where the test input is created
	 * @param id the unique integer ID of this test input in space
	 * @param parameter command-line parameter for running program
	 */
	protected TestInput(TestInputs inputs, int id, String parameter) {
		this.inputs = inputs;
		this.id = id;
		this.parameter = parameter;
	}
	
	/**
	 * @return the space where the test input is defined
	 */
	public TestInputs get_inputs() { return this.inputs; }
	/**
	 * @return the unique integer ID of this test input in space
	 */
	public int get_id() { return this.id; }
	/**
	 * @return command-line parameter for running program
	 */
	public String get_parameter() { return this.parameter; }
	
}
