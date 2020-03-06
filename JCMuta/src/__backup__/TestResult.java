package __backup__;

import java.util.Iterator;

/**
 * The test result is represented as a vector for given
 * mutant and test case.
 * @author yukimula
 */
public class TestResult {
	
	/** ID for original program **/
	public static final int PROGRAM_ID = -1;
	
	/** mutant to be tested **/
	private int mutant;
	/** test used to execute **/
	private int test;
	/** status of the output **/
	private TestStatus status;
	/** vector to represent the output-values **/
	private int[] outputs;
	/**
	 * create a test result for given mutant and test case
	 * with the captured test output
	 * @param mutant
	 * @param test
	 * @param output
	 * @throws Exception
	 */
	protected TestResult(Mutant mutant, TestCase test, TestOutput output) throws Exception {
		if(test == null)
			throw new IllegalArgumentException("invalid test: null");
		else if(output == null)
			throw new IllegalArgumentException("invalid output: null");
		else {
			if(mutant != null)
				this.mutant = mutant.get_mutant_id(); 
			else this.mutant = PROGRAM_ID;
			this.test = test.get_test_id();
			this.outputs = new int[5]; 
			this.parse(output);
		}
	}
	public TestResult(int mutant, int test, TestStatus status, Iterator<Integer> vector) throws Exception {
		if(status == null)
			throw new IllegalArgumentException("invalid status: null");
		else {
			this.mutant = mutant; this.test = test;
			this.status = status; this.outputs = new int[5];
			for(int k = 0; k < outputs.length; k++)
				this.outputs[k] = vector.next();
		}
	}
	private void parse(TestOutput output) throws Exception {
		this.status = output.get_status();
		this.outputs[0] = output.get_return_code();
		this.outputs[1] = this.length_of_string(output.get_stdout());
		this.outputs[2] = this.hashcode_string(output.get_stdout());
		this.outputs[3] = this.length_of_string(output.get_stderr());
		this.outputs[4] = this.hashcode_string(output.get_stderr());
	}
	private int length_of_string(String text) {
		if(text == null) return -1;
		else return text.length();
	}
	private int hashcode_string(String text) {
		if(text == null) return 0;
		else return text.hashCode();
	}
	
	/* getters */
	/**
	 * get the mutant to be tested
	 * @return
	 */
	public int get_mutant() { return mutant; }
	/**
	 * get the test to be executed
	 * @return
	 */
	public int get_test() { return test; }
	/**
	 * get the status of execution
	 * @return
	 */
	public TestStatus get_status() { return status; }
	/**
	 * get the output vector 
	 * @return
	 */
	public int[] get_outputs() { return outputs; }
	
	@Override
	public boolean equals(Object result) {
		if(result == null) return false;
		else if(result instanceof TestResult) {
			TestResult res = (TestResult) result;
			if(this.status == res.status) {
				for(int k = 0; k < this.outputs.length; k++) {
					if(this.outputs[k] != res.outputs[k]) {
						return false;
					}
				}
				return true;
			}
			else return false;
		}
		else return false;
	}
	
}
