package com.jcsa.jcmuta.mutant.error2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lopt.models.depend.CDependGraph;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceGraph;

/**
 * To build up the state error graph from the mutation source.
 * 
 * @author yukimula
 *
 */
public class StateErrorBuilder {
	
	/**
	 * establish whether the constraint needs to be optimized
	 * @param optimize
	 * @throws Exception
	 */
	public static void set_constraint_optimization(boolean optimize) throws Exception {
		if(optimize) {
			StateInfections.open_optimize_constraint();
			StatePropagation.set_optimization(optimize);
		}
		else {
			StateInfections.close_optimize_constraint();
			StatePropagation.set_optimization(optimize);
		}
	}
	
	/**
	 * generate the state error graph w.r.t. the mutation 
	 * @param mutation
	 * @param cir_tree
	 * @param dominance_graph
	 * @param dependence_graph
	 * @param max_distance
	 * @return
	 * @throws Exception
	 */
	public static StateErrorGraph build(AstMutation mutation, CirTree cir_tree, CDominanceGraph 
			dominance_graph, CDependGraph dependence_graph, int max_distance) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(dominance_graph == null)
			throw new IllegalArgumentException("Invalid dominance graph");
		else if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence graph");
		else {
			StateErrorGraph graph = StateInfections.parse(cir_tree, mutation, dominance_graph);
			StateErrorNode reach_node = graph.get_beg_node().get_ou_edges().iterator().next().get_target();
			for(StateErrorEdge infection_edge : reach_node.get_ou_edges()) {
				StatePropagation.propagate_for(infection_edge.get_target(), dependence_graph, max_distance);
			}
			return graph;
		}
	}
	
}
