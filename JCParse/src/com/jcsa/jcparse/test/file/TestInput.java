package com.jcsa.jcparse.test.file;

import java.io.File;

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
	
	/**
	 * @param odir
	 * @return the file to preserve the standard output information
	 */
	public File get_stdout_file(File odir) {
		return new File(odir.getAbsolutePath() + "/" + this.id + ".out");
	}
	/**
	 * @param odir
	 * @return the file to preserve the standard error information
	 */
	public File get_stderr_file(File odir) {
		return new File(odir.getAbsolutePath() + "/" + this.id + ".err");
	}
	/**
	 * @param odir
	 * @return the file where the instrumental result is preserved.
	 */
	public File get_instrument_file(File odir) {
		return new File(odir.getAbsolutePath() + "/" + this.id + ".ins");
	}
	
	/** the template for running the command of test input on program **/
	private static final String command_template = "%s %s >%s 2>%s";
	/** the template for running the command with timeout seconds **/
	private static final String timeout_template = "timeout %d %s";
	/**
	 * @param efile the executional file used to run the program
	 * @param odir the directory where the stdout and stderr is generated
	 * @param timeout the maximal seconds that is needed for running test 
	 * 		  or negative (or zero) when the time-out is not established.
	 * @return the command for running the test input over the efile as specified.
	 * @throws Exception
	 */
	public String command(File efile, File odir, long timeout) throws Exception {
		File stdout = this.get_stdout_file(odir);
		File stderr = this.get_stderr_file(odir);
		String cmd = String.format(command_template, efile.getAbsolutePath(), 
									this.parameter, stdout.getAbsolutePath(), 
									stderr.getAbsolutePath());
		if(timeout > 0) {
			return String.format(timeout_template, timeout, cmd);
		}
		else {
			return cmd;
		}
	}
	
}
