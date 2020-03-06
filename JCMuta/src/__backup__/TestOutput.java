package __backup__;

/**
 * The output of testing execution
 * @author yukimula
 */
public class TestOutput {
	
	/** status of the execution **/
	private TestStatus status;
	/** return-value of main() **/
	private int return_code;
	/** standard outputs and error **/
	private String stdout, stderr;
	/**
	 * create an empty execution output 
	 * @param status
	 * @throws Exception
	 */
	protected TestOutput(TestStatus status) {
		if(status == null)
			throw new IllegalArgumentException("Invalid status: null");
		else {
			this.status = status; this.clear();
		}
	}
	
	/* getters and setters */
	/**
	 * clear the values in the output
	 */
	public void clear() {
		this.return_code = 0;
		this.stdout = null;
		this.stderr = null;
	}
	/**
	 * get the status of the output
	 * @return
	 */
	public TestStatus get_status() {
		return this.status;
	}
	/**
	 * get the return-value of the main()
	 * @return
	 */
	public int get_return_code() {
		return this.return_code;
	}
	/**
	 * get standard output
	 * @return
	 */
	public String get_stdout() { return stdout; }
	/**
	 * get standard errors
	 * @return
	 */
	public String get_stderr() { return stderr; }
	/**
	 * set the return value of main()
	 * @param return_code
	 */
	public void set_return_code(int return_code) {
		this.return_code = return_code;
	}
	/**
	 * set the standard output
	 * @param stdout
	 */
	public void set_stdout(String stdout) {
		this.stdout = stdout;
	}
	/**
	 * set the standard error
	 * @param stderr
	 */
	public void set_stderr(String stderr) {
		this.stderr = stderr;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		else if(obj instanceof TestOutput) {
			TestOutput out = (TestOutput) obj;
			if(this.return_code == out.return_code) {
				if(match_string(this.stdout, out.stdout)) {
					if(match_string(this.stderr, out.stderr)) {
						return true;
					}
					else return false;
				}
				else return false;
			}
			else return false;
		}
		else return false;
	}
	private boolean match_string(String x, String y) {
		if(x == y) return true;
		else if(x == null) return false;
		else if(y == null) return false;
		else return x.equals(y);
	}
	
}
