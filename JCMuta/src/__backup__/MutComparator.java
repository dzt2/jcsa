package __backup__;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcparse.lang.base.BitSequence;

/**
 * Compare the <code>TestResult</code> between every two program(mutant)s
 * @author yukimula
 */
public class MutComparator {
	
	private TestOracleManager oracle;
	private List<MutScore> score_buffer;
	private Set<Integer> compare_index;
	private List<TestResult> results1, results2;
	private List<TestResult> rbuffer1, rbuffer2;
	protected MutComparator(TestOracleManager oracle) throws Exception {
		if(oracle == null)
			throw new IllegalArgumentException("invalid oracle: null");
		else {
			this.oracle = oracle; 
			score_buffer = new ArrayList<MutScore>();
			compare_index = new HashSet<Integer>();
			results1 = new ArrayList<TestResult>();
			results2 = new ArrayList<TestResult>();
			rbuffer1 = new ArrayList<TestResult>();
			rbuffer2 = new ArrayList<TestResult>();
		}
	}
	
	/**
	 * Compare the mutant with original program
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	protected MutDifference compare(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("invalid mutant: null");
		else {
			MutScore score = oracle.produce_score(mutant);
			score_buffer.clear(); score_buffer.add(score);
			oracle.load_scores(score_buffer); 
			return get_difference(TestResult.PROGRAM_ID, mutant.get_mutant_id(), score);
		}
	}
	/**
	 * Compare the results between m1 and m2
	 * @param m1
	 * @param m2
	 * @return
	 * @throws Exception
	 */
	protected MutDifference compare(Mutant m1, Mutant m2) throws Exception {
		if(m1 == null || m2 == null)
			throw new IllegalArgumentException("invalid mutants");
		else {
			if(m1.get_mutant_id() != m2.get_mutant_id()) {
				/* reading the mutation scores */
				MutScore score1 = oracle.produce_score(m1);
				MutScore score2 = oracle.produce_score(m2);
				score_buffer.clear(); 
				score_buffer.add(score1);
				score_buffer.add(score2);
				oracle.load_scores(score_buffer);
				
				/* compare the score and collect the conflicted bits */
				MutScore answer = oracle.produce_score(m1);
				BitSequence x = score1.get_score_set();
				BitSequence y = score2.get_score_set();
				compare_index.clear();
				int n = score1.get_score_set().length();
				for(int tid = 0; tid < n; tid++) {
					boolean xval = x.get(tid);
					boolean yval = y.get(tid);
					if(xval && yval) {
						compare_index.add(tid);
					}
					else if(xval && !yval) {
						answer.kill(tid);
					}
					else if(!xval && yval) {
						answer.kill(tid);
					}
					else {  }
				}
				
				/* read the test results */
				if(!compare_index.isEmpty()) {
					results1.clear(); results2.clear();
					this.oracle.load_results(m1, results1);
					this.oracle.load_results(m2, results2);
					
					rbuffer1.clear(); rbuffer2.clear();
					for(TestResult result : results1) {
						if(compare_index.contains(result.get_test()))
							rbuffer1.add(result);
					}
					for(TestResult result : results2) {
						if(compare_index.contains(result.get_test()))
							rbuffer2.add(result);
					}
					results1.clear(); results2.clear();
					
					for(int k = 0; k < rbuffer1.size(); k++) {
						TestResult result1 = rbuffer1.get(k);
						TestResult result2 = rbuffer2.get(k);
						if(!result1.equals(result2))
							answer.kill(k);
					}
				}
				
				/* return */	return this.get_difference(m1.get_mutant_id(), m2.get_mutant_id(), answer);
			}
			else return this.get_difference(m1.get_mutant_id(), m2.get_mutant_id(), oracle.produce_score(m1));
		}
	}
	private MutDifference get_difference(int m1, int m2, MutScore score) throws Exception {
		return new MutDifference(m1, m2, score.get_score_set());
	}
	
}
