package __backup__;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.lang.base.BitSequence;

/**
 * Used to build up the mutation subsumption graph (dynamic)
 * 
 * @author yukimula
 *
 */ 
class MutSubsumeBuilder {
	
	/* definitions and constructor */
	private MutSubsumeGraph graph;
	private List<Collection<MutSubsumeNode>> mlist;
	private static final MutSubsumeBuilder builder = new MutSubsumeBuilder();
	private MutSubsumeBuilder() { 
		this.mlist = new ArrayList<Collection<MutSubsumeNode>>();
	}
	
	/* main interfaces */
	/**
	 * build up the subsumption graph according to the score set of each mutant
	 * @param graph
	 * @throws Exception
	 */
	protected static void build(MutSubsumeGraph graph) throws Exception {
		builder.start(graph);
		builder.build_all();
		builder.stop();
	}
	private void start(MutSubsumeGraph graph) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("invalid graph: null");
		else { this.graph = graph; graph.init(); }
	}
	private void build_all() throws Exception {
		this.sorting();
		this.linking();
	}
	private void stop() { this.graph = null; }
	
	/* basic methods */
	/**
	 * whether source node subsumes the target node (equivalence is not considered thus)
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	private boolean subsume(MutSubsumeNode source, MutSubsumeNode target) throws Exception {
		MutScore source_score = source.get_score();
		MutScore target_score = target.get_score();
		if(source_score.get_score_degree() == 0) return false;
		/* else if(source_score.get_score_degree() >= 
				target_score.get_score_degree()) return false; */
		else { 
			BitSequence x = source_score.get_score_set();
			BitSequence y = target_score.get_score_set();
			BitSequence z = x.and(y); return z.equals(x);
		}
	}
	
	/* processing methods */
	/**
	 * sorting the mutation subsumption nodes according to their score degrees.
	 * @return
	 * @throws Exception
	 */
	private int sorting() throws Exception {
		Iterable<MutSubsumeNode> nodes = graph.get_nodes();
		
		/* 1. collect the degree to its subsumption nodes */
		Map<Integer, Collection<MutSubsumeNode>> degree_nodes = 
				new HashMap<Integer, Collection<MutSubsumeNode>>();
		for(MutSubsumeNode node : nodes) {
			int degree = node.get_score().get_score_degree();
			if(degree > 0) {
				if(!degree_nodes.containsKey(degree))
					degree_nodes.put(degree, new LinkedList<MutSubsumeNode>());
				degree_nodes.get(degree).add(node);
			}
		}
		
		/* 2. sorting the score degrees in ascending order */
		int[] degrees = new int[degree_nodes.size()]; int k = 0;
		for(Integer degree : degree_nodes.keySet()) { 
			degrees[k++] = degree;
		}
		Arrays.sort(degrees);
		
		/* 3. build up the list of subsumption nodes according to their degrees */
		this.mlist.clear();
		for(k = 0; k < degrees.length; k++) {
			this.mlist.add(degree_nodes.get(degrees[k]));
		}
		
		return this.mlist.size();
	}
	/**
	 * get all the nodes that are directly or indirectly subsumed by root (including root itself)
	 * @param leaf
	 * @param nodes
	 * @return
	 * @throws Exception
	 */
	private void get_descent_set(MutSubsumeNode root, Set<MutSubsumeNode> nodes) throws Exception {
		Queue<MutSubsumeNode> queue = new LinkedList<MutSubsumeNode>();
		queue.add(root); 
		while(!queue.isEmpty()) {
			MutSubsumeNode node = queue.poll();
			if(!nodes.contains(node)) {
				nodes.add(node);
				
				Iterable<MutSubsumeNode> children = node.get_subsummed_nodes();
				for(MutSubsumeNode child : children) {
					queue.add(child);
				}
			}
		}
	}
	/**
	 * get all the nodes that directly or indirectly subsume the leaf (including leaf itself)
	 * @param leaf
	 * @param nodes
	 * @throws Exception
	 */
	private void get_acesent_set(MutSubsumeNode leaf, Set<MutSubsumeNode> nodes) throws Exception {
		Queue<MutSubsumeNode> queue = new LinkedList<MutSubsumeNode>();
		queue.add(leaf); 
		while(!queue.isEmpty()) {
			MutSubsumeNode node = queue.poll();
			if(!nodes.contains(node)) {
				nodes.add(node);
				
				Iterable<MutSubsumeNode> parents = node.get_subsuming_nodes();
				for(MutSubsumeNode parent : parents) {
					queue.add(parent);
				}
			}
		}
	}
	/**
	 * determine the nodes directly subsumed by the root in the layer of the sorting list
	 * @param layer
	 * @param root
	 * @return
	 * @throws Exception
	 */
	private Collection<MutSubsumeNode> direct_subsume(int layer, MutSubsumeNode root) throws Exception {
		List<MutSubsumeNode> solution = new ArrayList<MutSubsumeNode>();
		Set<MutSubsumeNode> visit_set = new HashSet<MutSubsumeNode>();
		
		int beg = layer + 1, end = this.mlist.size() - 1;
		while((beg < this.mlist.size()) && (end > layer)) {
			Collection<MutSubsumeNode> beg_nodes = this.mlist.get(beg++);
			Collection<MutSubsumeNode> end_nodes = this.mlist.get(end--);
			
			for(MutSubsumeNode beg_node : beg_nodes) {
				if(!visit_set.contains(beg_node)) {
					if(this.subsume(root, beg_node)) {
						solution.add(beg_node);
						this.get_descent_set(beg_node, visit_set);
					}
					else {
						visit_set.add(beg_node);
					}
				}
			}
			
			for(MutSubsumeNode end_node : end_nodes) {
				if(!visit_set.contains(end_node)) {
					if(!this.subsume(root, end_node)) {
						this.get_acesent_set(end_node, visit_set);
					}
				}
			}
		}
		
		return solution;
	}
	/**
	 * creating the links between nodes as direct dynamic subsumption
	 * @throws Exception
	 */
	private void linking() throws Exception {
		for(int layer = this.mlist.size() - 1; layer >= 0; layer--) {
			Collection<MutSubsumeNode> nodes = this.mlist.get(layer);
			for(MutSubsumeNode node : nodes) {
				Collection<MutSubsumeNode> dchildren = this.direct_subsume(layer, node);
				for(MutSubsumeNode child : dchildren) {
					node.link_to(child);
				}
			}
		}
	}
	
}
