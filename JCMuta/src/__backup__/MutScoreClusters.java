package __backup__;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MutScoreClusters {
	
	private Map<String, MutScoreCluster> clusters;
	private Map<Mutant, MutScoreCluster> index;
	
	public MutScoreClusters() {
		this.clusters = new HashMap<String, MutScoreCluster>();
		this.index = new HashMap<Mutant, MutScoreCluster>();
	}
	
	/* getters */
	/**
	 * whether none of mutants belonging to the clusters
	 * @return
	 */
	public boolean isEmpty() { return clusters.size() == 0; }
	/**
	 * number of clusters
	 * @return
	 */
	public int size() { return clusters.size(); }
	/**
	 * get all the clusters
	 * @return
	 */
	public Iterable<MutScoreCluster> get_clusters() { return clusters.values(); }
	/**
	 * whether there is cluster in which the mutant lives
	 * @param mutant
	 * @return
	 */
	public boolean in_cluster(Mutant mutant) { return this.index.containsKey(mutant); }
	/**
	 * get the cluster where the mutant lives
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public MutScoreCluster get_cluster(Mutant mutant) throws Exception {
		if(this.index.containsKey(mutant)) return index.get(mutant);
		else throw new IllegalArgumentException("undefined mutant");
	}
	/**
	 * add the mutant into the cluster according to its score.
	 * @param mutant
	 * @param score
	 * @return
	 * @throws Exception
	 */
	public MutScoreCluster add_mutant_and_score(Mutant mutant, MutScore score) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("invalid mutant: null");
		else if(score == null)
			throw new IllegalArgumentException("invalid score as null");
		else if(this.index.containsKey(mutant)) return index.get(mutant);
		else {
			String key = score.get_score_set().toString();
			if(!this.clusters.containsKey(key)) {
				MutScoreCluster cluster = new 
						MutScoreCluster(this, clusters.size(), score);
				this.clusters.put(key, cluster);
			}
			MutScoreCluster cluster = this.clusters.get(key);
			cluster.add(mutant); this.index.put(mutant, cluster); 
			return cluster;
		}
	}
	
	/* factory methods */
	/**
	 * generate the clustering set according to the mutants with respect to their mutation score sets.
	 * @param scores
	 * @return
	 * @throws Exception
	 */
	public static MutScoreClusters clustering(Map<Mutant, MutScore> scores) throws Exception {
		MutScoreClusters clusters = new MutScoreClusters();
		if(scores != null) {
			for(Mutant mutant : scores.keySet()) {
				MutScore score = scores.get(mutant);
				clusters.add_mutant_and_score(mutant, score);
			}
		}
		return clusters;
	}
	
	/* mutation score analysis */
	/**
	 * determine the mutation score of the test cases provided.
	 * @param tests
	 * @param cluster_counter	whether the score is evaluated based on the cluster rather than each mutant
	 * @param equivalence_counter whether the unkilled mutants are counted in mutation score
	 * @return
	 * @throws Exception
	 */
	public double mutation_score(Collection<Integer> tests, 
			boolean cluster_counter, boolean equivalence_counter) throws Exception {
		if(tests == null || tests.isEmpty()) return 0.0;
		else {
			List<MutScoreCluster> killed_clusters = new ArrayList<MutScoreCluster>();
			for(MutScoreCluster cluster : clusters.values()) {
				if(cluster.killed_by(tests)) killed_clusters.add(cluster);
			}
			
			double total_mutants, killed_mutants;
			if(cluster_counter) {
				total_mutants = 0;
				for(MutScoreCluster cluster : clusters.values()) {
					if(!equivalence_counter) {
						if(cluster.get_score_key().get_score_degree() == 0) {
							total_mutants--;
						}
					}
					total_mutants++;
				}
				killed_mutants = 0;
				for(@SuppressWarnings("unused") MutScoreCluster cluster : killed_clusters) {
					killed_mutants++;
				}
			}
			else {
				total_mutants = 0;
				for(MutScoreCluster cluster : clusters.values()) {
					if(!equivalence_counter) {
						if(cluster.get_score_key().get_score_degree() == 0) {
							total_mutants -= cluster.size();
						}
					}
					total_mutants += cluster.size();
				}
				killed_mutants = 0;
				for(MutScoreCluster cluster : killed_clusters) {
					killed_mutants += cluster.size();
				}
			}
			
			return killed_mutants / total_mutants;
		}
	}
	
}
