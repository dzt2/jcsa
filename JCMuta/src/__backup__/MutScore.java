package __backup__;

import com.jcsa.jcparse.lang.base.BitSequence;

/**
 * score set of each mutants are the tests that kill
 * the target mutant
 * @author yukimula
 */
public class MutScore {
	/* constructor */
	private int mutant;
	private BitSequence score;
	protected MutScore(int mutant, int test_size) throws Exception {
		if(test_size <= 0)
			throw new IllegalArgumentException("Invalid test-size: " + test_size);
		else {
			this.mutant = mutant; 
			this.score = new BitSequence(test_size);
		}
	}
	
	/**
	 * get the mutant under test
	 * @return
	 */
	public int get_mutant() { return mutant; }
	/**
	 * whether the mutant is killed by the test (id)
	 * @param test
	 * @return
	 */
	public boolean is_killed_by(int test) {
		return score.get(test);
	}
	/**
	 * get the mutant's score set
	 * @return
	 */
	public BitSequence get_score_set() {
		return score;
	}
	/**
	 * get the score degree (number of tests that kill
	 * this mutant).
	 * @return
	 */
	public int get_score_degree() {
		return score.degree();
	}
	/**
	 * set the mutant as killed by specified test
	 * @param test
	 * @throws Exception
	 */
	public void kill(int test) throws Exception {
		score.set(test, BitSequence.BIT1);
	}
	
}
