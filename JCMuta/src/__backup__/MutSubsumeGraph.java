package __backup__;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Mutation subsumption graph (dynamic)
 * @author yukimula
 *
 */
public class MutSubsumeGraph {
	
	private MutScoreClusters cluster_set;
	private Map<MutScoreCluster, MutSubsumeNode> nodes;
	private MutSubsumeGraph(MutScoreClusters cluster_set) throws Exception {
		if(cluster_set == null || cluster_set.isEmpty())
			throw new IllegalArgumentException("invalid clusters as null");
		else {
			this.cluster_set = cluster_set;
			this.nodes = new HashMap<MutScoreCluster, MutSubsumeNode>();
		}
	}
	
	/* getters */
	/**
	 * get the number of nodes in graph
	 * @return
	 */
	public int size() { return nodes.size(); }
	/**
	 * get the set of clustering on which the subsumption graph is created
	 * @return
	 */
	public MutScoreClusters get_cluster_set() { return this.cluster_set; }
	/**
	 * whether there is a node with corresponding to the mutant
	 * @param mutant
	 * @return
	 */
	public boolean has_node(Mutant mutant) {
		return this.cluster_set.in_cluster(mutant);
	}
	/**
	 * whether there is a node with corresponding to the cluster
	 * @param cluster
	 * @return
	 */
	public boolean has_node(MutScoreCluster cluster) {
		return this.nodes.containsKey(cluster);
	}
	/**
	 * get the node with corresponding to the cluster
	 * @param cluster
	 * @return
	 * @throws Exception
	 */
	public MutSubsumeNode get_node(MutScoreCluster cluster) throws Exception {
		if(this.nodes.containsKey(cluster)) return nodes.get(cluster);
		else throw new IllegalArgumentException("invalid " + cluster);
	}
	/**
	 * get the node with corresponding to the mutant
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public MutSubsumeNode get_node(Mutant mutant) throws Exception {
		MutScoreCluster cluster = cluster_set.get_cluster(mutant);
		return this.get_node(cluster);
	}
	/**
	 * get all the nodes created in the graph
	 * @return
	 */
	public Iterable<MutSubsumeNode> get_nodes() { return nodes.values(); }
	
	/* setter */
	/**
	 * initialize the nodes in the subsumption graph without any edge and corresponding
	 * to all the clusters (non-equivalence) in the clustering set
	 * @throws Exception
	 */
	protected void init() throws Exception {
		this.nodes.clear();
		Iterable<MutScoreCluster> clusters = cluster_set.get_clusters();
		for(MutScoreCluster cluster : clusters) {
			MutSubsumeNode node = new MutSubsumeNode(
					this, this.nodes.size(), cluster);
			this.nodes.put(cluster, node);
		}
	}
	
	/* factory */
	/**
	 * generate the dynamic mutation subsume graph according to the mutation scores of each
	 * mutants in one single program.
	 * @param cluster_set
	 * @return
	 * @throws Exception
	 */
	public static MutSubsumeGraph graph(MutScoreClusters cluster_set) throws Exception {
		MutSubsumeGraph graph = new MutSubsumeGraph(cluster_set);
		MutSubsumeBuilder.build(graph); return graph;
	}
	
	/* minimal score evaluator */
	private Collection<MutScoreCluster> collect_subsumed_nodes(MutScoreCluster root) throws Exception {
		Queue<MutSubsumeNode> queue = new LinkedList<MutSubsumeNode>();
		Set<MutSubsumeNode> visit_set = new HashSet<MutSubsumeNode>();
		
		MutSubsumeNode root_node = this.get_node(root);
		queue.add(root_node); visit_set.add(root_node);
		
		while(!queue.isEmpty()) {
			MutSubsumeNode node = queue.poll();
			Iterable<MutSubsumeNode> children = node.get_subsummed_nodes();
			for(MutSubsumeNode child : children) {
				if(!visit_set.contains(child)) {
					visit_set.add(child); queue.add(child);
				}
			}
		}
		
		List<MutScoreCluster> clusters = new ArrayList<MutScoreCluster>();
		for(MutSubsumeNode node : visit_set) clusters.add(node.get_cluster()); 
		return clusters;
	}
	public double min_mutation_score(Iterable<MutScoreCluster> selected_clusters, 
			boolean cluster_count, boolean equivalence_count) throws Exception {
		if(selected_clusters == null) return 0;
		else {
			Set<MutScoreCluster> killed_clusters = new HashSet<MutScoreCluster>();
			for(MutScoreCluster cluster : selected_clusters) {
				Collection<MutScoreCluster> subsumed_set = this.collect_subsumed_nodes(cluster);
				killed_clusters.addAll(subsumed_set);
			}
			
			double total_mutants, killed_mutants;
			if(cluster_count) {
				total_mutants = 0;
				for(MutScoreCluster cluster : this.cluster_set.get_clusters()) {
					if(!equivalence_count) {
						if(cluster.get_score_key().get_score_degree() == 0) {
							total_mutants--;
						}
					}
					total_mutants++;
				}
				killed_mutants = killed_clusters.size();
			}
			else {
				total_mutants = 0;
				for(MutScoreCluster cluster : this.cluster_set.get_clusters()) {
					if(!equivalence_count) {
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
