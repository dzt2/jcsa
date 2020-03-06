package __backup__;

/**
 * Capture the test result from outputs in execution 
 * and compute its mutation score
 * @author yukimula
 */
class TestOracle {
	
	/**
	 * whether the mutant is killed based on its output and original program output
	 * @param origin_output
	 * @param mutant_output
	 * @return
	 * @throws Exception
	 */
	public static boolean is_detected(
			TestOutput origin_output, 
			TestOutput mutant_output) throws Exception {
		if(origin_output == null)
			throw new IllegalArgumentException("invalid output for original program");
		else if(mutant_output == null)
			throw new IllegalArgumentException("invalid output for mutated program");
		else { return origin_output.equals(mutant_output); }
	}
	/**
	 * whether the mutant is killed based on its encoded result and original program
	 * @param origin_result
	 * @param mutant_result
	 * @return
	 * @throws Exception
	 */
	public static boolean is_detected(TestResult origin_result, TestResult mutant_result) throws Exception {
		if(origin_result == null)
			throw new IllegalArgumentException("invalid result for original program");
		else if(mutant_result == null)
			throw new IllegalArgumentException("invalid result for mutated program");
		else { return origin_result.equals(mutant_result); }
	}
	
	/**
	 * record the mutation score for given test case in given mutant scores
	 * @param test
	 * @param detected
	 * @param score
	 * @return
	 * @throws Exception
	 */
	public static boolean set_score_for(TestCase test, boolean detected, MutScore score) throws Exception {
		if(test == null)
			throw new IllegalArgumentException("no test specified");
		else if(score == null)
			throw new IllegalArgumentException("no score specified");
		else {
			score.get_score_set().set(test.get_test_id(), detected); return true;
		}
	}
	
}
