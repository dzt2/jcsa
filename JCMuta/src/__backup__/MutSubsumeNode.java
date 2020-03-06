package __backup__;

import java.util.LinkedList;
import java.util.List;

/**
 * Each node in mutant subsumption graph refers a cluster of mutants with respect to
 * the identical mutation score set.
 * @author yukimula
 *
 */
public class MutSubsumeNode {
	
	private MutSubsumeGraph graph;
	private int id;
	private MutScoreCluster cluster;
	private List<MutSubsumeNode> in, ou;
	protected MutSubsumeNode(MutSubsumeGraph graph, int id, MutScoreCluster cluster) throws Exception {
		if(cluster == null)
			throw new IllegalArgumentException("invalid cluster as null");
		else if(graph == null)
			throw new IllegalArgumentException("Invalid graph as null");
		else { 
			this.id = id;
			this.graph = graph; this.cluster = cluster;
			this.in = new LinkedList<MutSubsumeNode>();
			this.ou = new LinkedList<MutSubsumeNode>();
		}
	}
	
	/* getters */
	/**
	 * get the mutation subsumption graph where this node created
	 * @return
	 */
	public MutSubsumeGraph get_graph() { return this.graph; }
	/**
	 * get the integer ID of the node in subsumption graph
	 * @return
	 */
	public int get_id() { return this.id; }
	/**
	 * get the number of mutants belonging to the cluster of this node
	 * @return
	 */
	public int size() { return cluster.size(); }
	/**
	 * get the set of mutants in the cluster of the node
	 * @return
	 */
	public Iterable<Mutant> get_mutants() { return cluster.get_mutants(); }
	/**
	 * get the cluster of the node in subsumption graph
	 * @return
	 */
	public MutScoreCluster get_cluster() { return this.cluster; }
	/**
	 * get the mutation score set of the node
	 * @return
	 */
	public MutScore get_score() { return this.cluster.get_score_key(); }
	/**
	 * get the nodes that directly subsume this node in the graph
	 * @return
	 */
	public Iterable<MutSubsumeNode> get_subsuming_nodes() { return this.in; }
	/**
	 * get the nodes that are directly subsumed by this node in graph
	 * @return
	 */
	public Iterable<MutSubsumeNode> get_subsummed_nodes() { return this.ou; }
	
	/* setter */
	/**
	 * set this node as directly subsuming the specified node
	 * @param next_node
	 * @return
	 * @throws Exception
	 */
	protected boolean link_to(MutSubsumeNode next_node) throws Exception {
		if(next_node == null || next_node == this)
			throw new IllegalArgumentException("invalid next_node: null");
		else if(this.ou.contains(next_node))	return false;
		else {
			this.ou.add(next_node); next_node.in.add(this); return true;
		}
	}
	
	@Override
	public String toString() { return "Nodes[" + this.id + "]"; }
	
}
