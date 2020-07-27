package com.jcsa.jcparse.test.cmd;


public class CommandResult {
	
	/** the status in running command **/
	private CommandStatus status;
	/** the code returned from command process **/
	private int exit_code;
	/** string preserved from standard output **/
	private String stdout;
	/** string preserved from standard errors **/
	private String stderr;
	
	/* getters */
	/**
	 * @return the status when the command exits from process.
	 */
	public CommandStatus get_exit_status() { return this.status; }
	/**
	 * @return the exit code after executing the command-line
	 */
	public int get_exit_code() { return exit_code; }
	/**
	 * @return the standard output generated in running command
	 */
	public String get_stdout() { return stdout; }
	/**
	 * @return the standard error generated in running command
	 */
	public String get_stderr() { return stderr; }
	
	/* constructor */
	/**
	 * @param status the status when the command exits from process.
	 * @param exit_code the exit code after executing the command-line
	 * @param stdout the standard output generated in running command
	 * @param stderr the standard error generated in running command
	 */
	protected CommandResult(CommandStatus status, 
			int exit_code, String stdout, String stderr) {
		this.status = status;
		this.exit_code = exit_code;
		this.stdout = stdout;
		this.stderr = stderr;
	}
	
}
