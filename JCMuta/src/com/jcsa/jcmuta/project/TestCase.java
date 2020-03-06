package com.jcsa.jcmuta.project;

/**
 * Test case
 * @author yukimula
 *
 */
public class TestCase {
	
	/** the space where test case is created **/
	private MutaTestSpace space;
	/** the integer ID of test case in space **/
	private int id;
	/** the argument to execute the test command **/
	private String argument;
	
	/**
	 * create a test case in the space with respect to the ID and argument
	 * @param space
	 * @param id
	 * @param argument
	 * @throws Exception
	 */
	protected TestCase(MutaTestSpace space, int id, String argument) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else if(argument == null)
			throw new IllegalArgumentException("Invalid test: null");
		else {
			this.space = space; this.id = id; this.argument = argument;
		}
	}
	
	/* getters */
	/**
	 * get the space where the test case is created
	 * @return
	 */
	public MutaTestSpace get_space() { return this.space; }
	/**
	 * get the integer ID of the test case in space
	 * @return
	 */
	public int get_id() { return this.id; }
	/**
	 * get the string argument of the test command
	 * @return
	 */
	public String get_argument() { return this.argument; }
	
}
