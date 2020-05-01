package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceGraph;

/**
 * Used to create infection of state errors.
 * 
 * @author yukimula
 *
 */
public abstract class StateInfection {
	
	public StateInfection() { }
	
	/**
	 * get the statement where the fault is seeded
	 * @param cir_tree
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected abstract CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception;
	
	/**
	 * generate the infections of {state_error, constraints} directly caused by the mutation
	 * @param cir_tree
	 * @param mutation
	 * @param graph
	 * @return
	 * @throws Exception
	 */
	protected abstract Map<StateError, StateConstraints> get_infections(
			CirTree cir_tree, AstMutation mutation, StateErrorGraph graph) throws Exception;
	
	/**
	 * generate the infection subgraph from specified mutation
	 * @param cir_tree
	 * @param mutation
	 * @param dgraph
	 * @return
	 * @throws Exception
	 */
	public StateErrorGraph parse(CirTree cir_tree, AstMutation mutation, CDominanceGraph dgraph) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(dgraph == null)
			throw new IllegalArgumentException("No dominance provided");
		else {
			/* initialization */
			CirStatement statement = this.get_location(cir_tree, mutation);
			if(statement == null) return null; /* unreachable nodes in graph */
			StateErrorGraph graph = new StateErrorGraph();
			
			/* entry --> {path_constraints} --> reach_node */
			StateConstraints path_constraints = PathConditions.path_constraints(statement, dgraph);
			StateErrorNode reach_node = graph.new_node(graph.get_error_set().execute(statement));
			graph.get_beg_node().propagate(reach_node, path_constraints);
			
			/* reach_node -- {constraints} --> infect_node+ */
			Map<StateError, StateConstraints> infections = this.get_infections(cir_tree, mutation, graph);
			for(StateError error : infections.keySet()) {
				StateConstraints constraints = infections.get(error);
				StateErrorNode target = graph.new_node(error);
				reach_node.propagate(target, constraints);
			}
			
			/* return the state error graph */	return graph;
		}
	}
	
	/* tool methods */
	/**
	 * get the begining statement in the range of source node
	 * @param cir_tree
	 * @param node
	 * @return
	 * @throws Exception
	 */
	protected CirStatement get_beg_statement(CirTree cir_tree, AstNode node) throws Exception {
		if(cir_tree.has_cir_range(node)) {
			AstCirPair range = cir_tree.get_cir_range(node);
			return range.get_beg_statement();
		}
		else return null;
	}
	/**
	 * get the final statement in the range of source node
	 * @param cir_tree
	 * @param node
	 * @return
	 * @throws Exception
	 */
	protected CirStatement get_end_statement(CirTree cir_tree, AstNode node) throws Exception {
		if(cir_tree.has_cir_range(node)) {
			AstCirPair range = cir_tree.get_cir_range(node);
			return range.get_end_statement();
		}
		else return null;
	}
	/**
	 * get the result that represents the source node in code
	 * @param cir_tree
	 * @param node
	 * @return
	 * @throws Exception
	 */
	protected CirExpression get_result_of(CirTree cir_tree, AstNode node) throws Exception {
		if(cir_tree.has_cir_range(node)) {
			AstCirPair range = cir_tree.get_cir_range(node);
			return range.get_result();
		}
		else return null;
	}
	
}
