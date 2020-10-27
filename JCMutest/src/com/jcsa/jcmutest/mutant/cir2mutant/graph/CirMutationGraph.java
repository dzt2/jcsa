package com.jcsa.jcmutest.mutant.cir2mutant.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutationUtils;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * Error propagation graph.
 * 
 * @author yukimula
 *
 */
public class CirMutationGraph {
	
	/* definitions */
	/** the mutation being killed under the propagation graph **/
	private Mutant mutant;
	/** the library to create cir-mutation, and its tree nodes **/
	private CirMutations cir_mutations;
	/** the nodes created under the graph **/
	private List<CirMutationNode> nodes;
	
	/* constructor */
	/**
	 * create an empty error propagation graph
	 * @param mutant
	 * @throws Exception
	 */
	private CirMutationGraph(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.cir_mutations = new CirMutations(mutant.get_space().get_cir_tree());
			this.nodes = new ArrayList<CirMutationNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the mutation being killed under the propagation graph
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the library to create cir-mutation, and its tree nodes
	 */
	public CirMutations get_cir_mutations() {
		return this.cir_mutations;
	}
	/**
	 * @return the nodes created under the graph
	 */
	public Iterable<CirMutationNode> get_nodes() {
		return this.nodes;
	}
	/**
	 * @return get the root nodes in the graph
	 */
	public Iterable<CirMutationNode> get_roots() {
		List<CirMutationNode> roots = new ArrayList<CirMutationNode>();
		for(CirMutationNode node : this.nodes) {
			if(node.is_root())
				roots.add(node);
		}
		return roots;
	}
	
	/* setters */
	/**
	 * create a node w.r.t. the root mutation as its root node in the tree
	 * 
	 * @param root_mutation
	 * @param dependence_graph
	 * @throws Exception
	 */
	protected CirMutationNode new_node(CirMutation root_mutation, 
			CDependGraph dependence_graph) throws Exception {
		if(root_mutation == null)
			throw new IllegalArgumentException("Invalid root_mutation");
		else if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph");
		else {
			CirMutationNode node = new CirMutationNode(this, root_mutation);
			node.build_local_tree(dependence_graph, cir_mutations);
			this.nodes.add(node);
			return node;
		}
	}
	/**
	 * connect the source to target in the graph with specified type
	 * @param type
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	protected CirMutationEdge connect(CirMutationFlow type, 
			CirMutationTreeNode source, CirMutationTreeNode target) throws Exception {
		CirMutationEdge edge = new CirMutationEdge(type, source, target);
		source.get_tree().get_statement_node().ou_edges.add(edge);
		target.get_tree().get_statement_node().in_edges.add(edge);
		return edge;
	}
	/**
	 * build one level from parent to its direct targets in dependence graph
	 * @param parent
	 * @param dependence_graph
	 * @throws Exception
	 */
	private void build_propagation(CirMutationNode parent, 
			CDependGraph dependence_graph, int distance) throws Exception {
		if(distance > 0) {
			for(CirMutationTreeNode leaf : parent.get_tree().get_leafs()) {
				Map<CirMutation, CirMutationFlow> results = CirMutationUtils.global_propagate(
						leaf.get_cir_mutation(), dependence_graph, cir_mutations);
				for(CirMutation child_mutation : results.keySet()) {
					CirMutationNode child = this.new_node(child_mutation, dependence_graph);
					this.connect(results.get(child_mutation), leaf, child.get_tree().get_root());
					this.build_propagation(child, dependence_graph, distance - 1);
				}
				
			}
		}
	}
	
	/* creator */
	/**
	 * @param mutant
	 * @param dependence_graph
	 * @param distance maximal distance from the head mutation to the other parts.
	 * @return create the error propagation graph 
	 * @throws Exception
	 */
	public static CirMutationGraph new_graph(Mutant mutant, CDependGraph dependence_graph, int distance) throws Exception {
		CirMutationGraph graph = new CirMutationGraph(mutant);
		if(mutant.has_cir_mutations()) {
			for(CirMutation root_mutation : mutant.get_cir_mutations()) {
				CirMutationNode root = graph.new_node(root_mutation, dependence_graph);
				graph.build_propagation(root, dependence_graph, distance);
			}
		}
		return graph;
	}
	
	/* concrete analysis */
	/**
	 * @param parent
	 * @param contexts
	 * @param con_results
	 * @return perform the concrete evaluation on the parent node based on given contexts, append
	 * 	       the concrete mutations to the results, and update the next generations from testing.
	 * @throws Exception
	 */
	private Iterable<CirMutationNode> con_evaluate_node(CirMutationNode parent, CStateContexts 
			contexts, Map<CirMutationTreeNode, List<CirMutation>> con_results) throws Exception {
		Map<CirMutationTreeNode, CirMutation> local_results = parent.con_evaluate(contexts);
		List<CirMutationNode> children = new LinkedList<CirMutationNode>();
		
		for(CirMutationTreeNode tree_node : local_results.keySet()) {
			CirMutation con_mutation = local_results.get(tree_node);
			con_results.get(tree_node).add(con_mutation);
		}
		
		for(CirMutationEdge edge : parent.ou_edges) {
			CirMutationTreeNode node = edge.get_source();
			if(local_results.containsKey(node)) {
				CirMutation con_mutation = local_results.get(node);
				Boolean validate1 = con_mutation.get_constraint().validate(null);
				Boolean validate2 = con_mutation.get_state_error().validate(null);
				if(validate1 != null && !validate1.booleanValue()) {
					continue;
				}
				else if(validate2 != null && !validate2.booleanValue()) {
					continue;
				}
				else {
					children.add(edge.get_target().get_tree().get_statement_node());
				}
			}
		}
		
		return children;
	}
	/**
	 * @return mapping from each tree node in the graph to their concrete mutation(s)
	 * @throws Exception
	 */
	private Map<CirMutationTreeNode, List<CirMutation>> init_con_results() throws Exception {
		Map<CirMutationTreeNode, List<CirMutation>> results = 
				new HashMap<CirMutationTreeNode, List<CirMutation>>();
		for(CirMutationNode node : this.nodes) {
			Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
			queue.add(node.get_tree().get_root());
			while(!queue.isEmpty()) {
				CirMutationTreeNode tree_node = queue.poll();
				results.put(tree_node, new LinkedList<CirMutation>());
				for(CirMutationTreeNode child : tree_node.get_children()) {
					queue.add(child);
				}
			}
		}
		return results;
	}
	/**
	 * @return mapping from tree node to its concrete mutation(s)
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, List<CirMutation>> con_evaluate() throws Exception {
		Map<CirMutationTreeNode, List<CirMutation>> results = this.init_con_results();
		Queue<CirMutationNode> queue = new LinkedList<CirMutationNode>();
		for(CirMutationNode root_node : this.get_roots()) {
			queue.clear();
			queue.add(root_node);
			while(!queue.isEmpty()) {
				CirMutationNode parent = queue.poll();
				Iterable<CirMutationNode> children = this.con_evaluate_node(parent, null, results);
				for(CirMutationNode child : children) {
					queue.add(child);
				}
			}
		}
		return results;
	}
	/**
	 * @param candidates
	 * @param statement
	 * @return nodes that match the specified statement
	 */
	private Set<CirMutationNode> match_candidates(Set<CirMutationNode> candidates, CirStatement statement) {
		Set<CirMutationNode> results = new HashSet<CirMutationNode>();
		for(CirMutationNode node : candidates) {
			if(node.get_statement() == statement) {
				results.add(node);
			}
		}
		return results;
	}
	/**
	 * @param state_path
	 * @return mapping from tree node to its concrete mutation(s)
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, List<CirMutation>> con_evaluate(CStatePath state_path) throws Exception {
		if(state_path == null)
			throw new IllegalArgumentException("No state path is provided");
		else {
			Map<CirMutationTreeNode, List<CirMutation>> results = this.init_con_results();
			CStateContexts contexts = new CStateContexts();
			Set<CirMutationNode> root_candidates = new HashSet<CirMutationNode>();
			for(CirMutationNode root : this.get_roots()) root_candidates.add(root);
			Set<CirMutationNode> candidates = new HashSet<CirMutationNode>(), matches;
			Set<CirMutationNode> appended_candidates = new HashSet<CirMutationNode>();
			Set<CirMutationNode> removed_candidates = new HashSet<CirMutationNode>();
			
			for(CStateNode state_node : state_path.get_nodes()) {
				CirStatement statement = state_node.get_statement();
				contexts.accumulate(state_node);
				
				matches = this.match_candidates(root_candidates, statement);
				if(!matches.isEmpty()) {
					candidates.clear();
					candidates.addAll(root_candidates);
				}
				
				matches = this.match_candidates(candidates, statement);
				if(!matches.isEmpty()) {
					removed_candidates.clear();
					appended_candidates.clear();
					
					for(CirMutationNode candidate : candidates) {
						Iterable<CirMutationNode> children = this.con_evaluate_node(candidate, contexts, results);
						removed_candidates.add(candidate);
						for(CirMutationNode child : children) appended_candidates.add(child);
					}
					
					candidates.removeAll(removed_candidates);
					candidates.addAll(appended_candidates);
				}
			}
			
			return results;
		}
	}
	
	/* abstract analysis */
	private Iterable<CirMutationNode> abs_evaluate_node(CirMutationNode parent, CStateContexts 
			contexts, Map<CirMutationTreeNode, CirMutationResult> abs_results) throws Exception {
		Map<CirMutationTreeNode, CirMutation> local_results = parent.con_evaluate(contexts);
		List<CirMutationNode> children = new LinkedList<CirMutationNode>();
		
		for(CirMutationTreeNode tree_node : local_results.keySet()) {
			CirMutation con_mutation = local_results.get(tree_node);
			abs_results.get(tree_node).append_concrete_mutation(con_mutation);
		}
		
		for(CirMutationEdge edge : parent.ou_edges) {
			CirMutationTreeNode node = edge.get_source();
			if(local_results.containsKey(node)) {
				CirMutation con_mutation = local_results.get(node);
				Boolean validate1 = con_mutation.get_constraint().validate(null);
				Boolean validate2 = con_mutation.get_state_error().validate(null);
				if(validate1 != null && !validate1.booleanValue()) {
					continue;
				}
				else if(validate2 != null && !validate2.booleanValue()) {
					continue;
				}
				else {
					children.add(edge.get_target().get_tree().get_statement_node());
				}
			}
		}
		
		return children;
	}
	private Map<CirMutationTreeNode, CirMutationResult> init_abs_results() throws Exception {
		Map<CirMutationTreeNode, CirMutationResult> results = 
				new HashMap<CirMutationTreeNode, CirMutationResult>();
		for(CirMutationNode node : this.nodes) {
			Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
			queue.add(node.get_tree().get_root());
			while(!queue.isEmpty()) {
				CirMutationTreeNode tree_node = queue.poll();
				results.put(tree_node, new CirMutationResult(tree_node));
				for(CirMutationTreeNode child : tree_node.get_children()) {
					queue.add(child);
				}
			}
		}
		return results;
	}
	public Map<CirMutationTreeNode, CirMutationResult> abs_evaluate() throws Exception {
		Map<CirMutationTreeNode, CirMutationResult> results = this.init_abs_results();
		Queue<CirMutationNode> queue = new LinkedList<CirMutationNode>();
		for(CirMutationNode root_node : this.get_roots()) {
			queue.clear();
			queue.add(root_node);
			while(!queue.isEmpty()) {
				CirMutationNode parent = queue.poll();
				Iterable<CirMutationNode> children = this.abs_evaluate_node(parent, null, results);
				for(CirMutationNode child : children) {
					queue.add(child);
				}
			}
		}
		return results;
	}
	public Map<CirMutationTreeNode, CirMutationResult> abs_evaluate(CStatePath state_path) throws Exception {
		if(state_path == null)
			throw new IllegalArgumentException("No state path is provided");
		else {
			Map<CirMutationTreeNode, CirMutationResult> results = this.init_abs_results();
			CStateContexts contexts = new CStateContexts();
			Set<CirMutationNode> root_candidates = new HashSet<CirMutationNode>();
			for(CirMutationNode root : this.get_roots()) root_candidates.add(root);
			Set<CirMutationNode> candidates = new HashSet<CirMutationNode>(), matches;
			Set<CirMutationNode> appended_candidates = new HashSet<CirMutationNode>();
			Set<CirMutationNode> removed_candidates = new HashSet<CirMutationNode>();
			
			for(CStateNode state_node : state_path.get_nodes()) {
				CirStatement statement = state_node.get_statement();
				contexts.accumulate(state_node);
				
				matches = this.match_candidates(root_candidates, statement);
				if(!matches.isEmpty()) {
					candidates.clear();
					candidates.addAll(root_candidates);
				}
				
				matches = this.match_candidates(candidates, statement);
				if(!matches.isEmpty()) {
					removed_candidates.clear();
					appended_candidates.clear();
					
					for(CirMutationNode candidate : candidates) {
						Iterable<CirMutationNode> children = this.abs_evaluate_node(candidate, contexts, results);
						removed_candidates.add(candidate);
						for(CirMutationNode child : children) appended_candidates.add(child);
					}
					
					candidates.removeAll(removed_candidates);
					candidates.addAll(appended_candidates);
				}
			}
			
			return results;
		}
	}
	
}
