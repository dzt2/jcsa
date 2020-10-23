package com.jcsa.jcmutest.mutant.cir2mutant.rtree;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.irlang.CirTree;

/**
 * Structural model of error propagation model.
 * 
 * @author yukimula
 *
 */
public class CirMutationGraph {
	
	/* definitions */
	private CirMutations cir_mutations;
	private Mutant mutant;
	private List<CirMutationNode> nodes;
	
	/**
	 * create a state error graph with initial mutations infected from the mutant
	 * @param cir_tree
	 * @param mutant
	 * @throws Exception
	 */
	private CirMutationGraph(CirTree cir_tree, Mutant mutant) throws Exception {
		this.cir_mutations = new CirMutations(cir_tree);
		this.mutant = mutant;
		this.nodes = new ArrayList<CirMutationNode>();
	}
	
	/* getters */
	public CirMutations get_cir_mutations() { return this.cir_mutations; }
	public Mutant get_mutant() { return this.mutant; }
	public Iterable<CirMutationNode> get_nodes() { return nodes; }
	
	/* setters */
	protected CirMutationEdge connect(Iterable<CirConstraint> constraints, CirMutationTreeNode 
			source, CirMutationTreeNode target) throws Exception {
		CirMutationEdge edge = new CirMutationEdge(source, target, constraints);
		source.get_tree().get_statement_node().ou_edges.add(edge);
		target.get_tree().get_statement_node().in_edges.add(edge);
		return edge;
	}
	protected CirMutationNode new_node(CirMutation root_mutation) throws Exception {
		CirMutationNode node = new CirMutationNode(this, root_mutation);
		this.nodes.add(node);
		return node;
	}
	public static CirMutationGraph new_graph(Mutant mutant, CDependGraph dependence_graph, int max_distance) throws Exception {
		CirMutationGraph graph = new CirMutationGraph(mutant.get_space().get_cir_tree(), mutant);
		CirMutationGraphUtils.build_mutation_graph(graph, dependence_graph, max_distance);
		return graph;
	}
	
}
