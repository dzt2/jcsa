package com.jcsa.jcmutest.mutant.cir2mutant.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutationUtils;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
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
	
	/* evaluation methods */
	public Map<CirMutationTreeNode, List<CirMutation>> con_evaluate() throws Exception {
		return CirMutationUtils.con_evaluate(this);
	}
	public Map<CirMutationTreeNode, List<CirMutation>> con_evaluate(CStatePath state_path) throws Exception {
		return CirMutationUtils.con_evaluate(this, state_path);
	}
	public Map<CirMutationTreeNode, CirMutationResult> abs_evaluate() throws Exception {
		return CirMutationUtils.abs_evaluate(this);
	}
	public Map<CirMutationTreeNode, CirMutationResult> abs_evaluate(CStatePath state_path) throws Exception {
		return CirMutationUtils.abs_evaluate(this, state_path);
	}
	
}
