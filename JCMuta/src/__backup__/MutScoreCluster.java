package __backup__;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.base.BitSequence;

public class MutScoreCluster {
	
	private MutScoreClusters clusters;
	private int id;
	private MutScore score;
	private String key;
	private List<Mutant> mutants;
	protected MutScoreCluster(MutScoreClusters clusters, int id, MutScore score) throws Exception {
		if(score == null)
			throw new IllegalArgumentException("invalid score as null");
		else if(clusters == null)
			throw new IllegalArgumentException("invalid clusters: null");
		else {
			this.clusters = clusters;
			this.id = id;
			this.score = score;
			this.key = score.get_score_set().toString();
			this.mutants = new ArrayList<Mutant>();
		}
	}
	
	/* getters */
	/**
	 * get the clusters where the cluster is created.
	 * @return
	 */
	public MutScoreClusters get_clusters() { return this.clusters; }
	/**
	 * get the integer ID of the cluster in set
	 * @return
	 */
	public int get_id() { return this.id; }
	/**
	 * get the mutation score of the mutants within the cluster
	 * @return
	 */
	public MutScore get_score_key() { return this.score; }
	/**
	 * get the string key denoting the mutants in the cluster
	 * @return
	 */
	public String get_string_key() { return this.key; }
	/**
	 * get all the mutants in the cluster
	 * @return
	 */
	public Iterable<Mutant> get_mutants() { return mutants; }
	/**
	 * get the number of mutants in the cluster
	 * @return
	 */
	public int size() { return this.mutants.size(); }
	/**
	 * add the mutant into the cluster
	 * @param mutant
	 * @throws Exception
	 */
	public void add(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("invalid mutant: null");
		else {
			this.mutants.add(mutant);
		}
	}
	/**
	 * whether the mutants in the cluster can be killed 
	 * by the specified test case
	 * @param test
	 * @return
	 * @throws Exception
	 */
	public boolean killed_by(int test) throws Exception {
		return this.score.get_score_set().get(test);
	}
	/**
	 * whether the mutants in the cluster can be killed
	 * by any test in the specified set?
	 * @param tests
	 * @return
	 * @throws Exception
	 */
	public boolean killed_by(Iterable<Integer> tests) throws Exception {
		if(tests == null) return false;
		else {
			BitSequence bits = score.get_score_set();
			for(Integer test : tests) {
				if(bits.get(test)) return true;
			}
			return false;
		}
	}
	
}
