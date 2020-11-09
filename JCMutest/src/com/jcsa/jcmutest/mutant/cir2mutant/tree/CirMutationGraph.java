package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;


public class CirMutationGraph {
	
	/* definitions */
	private CirMutations cir_mutations;
	private Mutant mutant;
	private List<CirMutationNode> nodes;
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
	 * @return mutant being killed by the branches in this graph
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return syntactic mutation of the mutant
	 */
	public AstMutation get_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return C-intermediate representation tree in which the mutant is seeded
	 */
	public CirTree get_cir_tree() { return this.mutant.get_space().get_cir_tree(); }
	/**
	 * @return used to create cir-mutation in the graph
	 */
	public CirMutations get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the unique start node for executing the graph
	 */
	public CirMutationNode get_start_node() { return this.nodes.get(0); }
	/**
	 * @return terminal node that represents program failure
	 */
	public CirMutationNode get_failure_node() { return this.nodes.get(1); }
	/**
	 * @return terminal node that represents mutation surviving from tests
	 */
	public CirMutationNode get_survive_node() { return this.nodes.get(2); }
	/**
	 * @return the nodes created in this graph
	 */
	public Iterable<CirMutationNode> get_nodes() { return this.nodes; }
	
	/* node creators */
	/**
	 * clear the nodes in the graph and rebuild the graph with start, failure and survive nodes
	 * @throws Exception
	 */
	protected void clear_graph() throws Exception {
		for(CirMutationNode node : this.nodes) {
			node.delete();
		}
		this.nodes.clear();
		
		CirFunctionCallGraph fgraph = this.get_cir_tree().get_function_call_graph();
		CirFunction main_function = fgraph.get_main_function();
		CirExecution main_entry = main_function.get_flow_graph().get_entry();
		CirExecution main_exit = main_function.get_flow_graph().get_exit();
		this.nodes.add(new CirMutationNode(this, CirMutationNodeType.startit, main_entry, null));
		this.nodes.add(new CirMutationNode(this, CirMutationNodeType.failure, main_exit, null));
		this.nodes.add(new CirMutationNode(this, CirMutationNodeType.survive, main_exit, null));
	}
	/**
	 * @param execution
	 * @return create an isolated node for reaching the target execution
	 * @throws Exception
	 */
	protected CirMutationNode execution_node(CirExecution execution) throws Exception {
		CirMutationNode node = new CirMutationNode(this, CirMutationNodeType.execute, execution, null);
		this.nodes.add(node);
		return node;
	}
	/**
	 * @param state_error
	 * @return create an isolated node for infecting the state-error
	 * @throws Exception
	 */
	protected CirMutationNode infection_node(CirStateError state_error) throws Exception {
		CirMutationNode node = new CirMutationNode(this,
				CirMutationNodeType.infects,
				state_error.get_execution(), state_error);
		this.nodes.add(node);
		return node;
	}
	
	/* building methods */
	/**
	 * @param dependence_graph
	 * @return building the part of reachability and initial infection.
	 * @throws Exception
	 */
	private Collection<CirMutationNode> build_prefix_paths(CDependGraph dependence_graph) throws Exception {
		this.clear_graph();
		List<CirMutationNode> leafs = new ArrayList<CirMutationNode>();
		
		if(this.mutant.has_cir_mutations()) {
			Map<CirExecution, List<CirMutation>> reaching_map = new HashMap<CirExecution, List<CirMutation>>();
			for(CirMutation cir_mutation : this.mutant.get_cir_mutations()) {
				CirStatement statement = cir_mutation.get_statement();
				CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
				if(!reaching_map.containsKey(execution)) {
					reaching_map.put(execution, new LinkedList<CirMutation>());
				}
				reaching_map.get(execution).add(cir_mutation);
			}
			
			
			for(CirExecution execution : reaching_map.keySet()) {
				List<CirConstraint> path_constraints = CirMutationUtils.utils.
						get_path_constraints(cir_mutations, dependence_graph, execution);
				CirMutationNode prev = this.get_start_node(), true_next, fals_next;
				for(CirConstraint constraint : path_constraints) {
					true_next = this.execution_node(constraint.get_execution());
					fals_next = this.get_survive_node();
					prev.link_to(CirMutationEdgeType.path_flow, true_next, constraint);
					
					CirConstraint neg_constraint = cir_mutations.expression_constraint(
							constraint.get_statement(), constraint.get_condition(), false);
					prev.link_to(CirMutationEdgeType.term_flow, fals_next, neg_constraint);
					
					prev = true_next;
				}
				
				for(CirMutation cir_mutation : reaching_map.get(execution)) {
					CirConstraint constraint = cir_mutation.get_constraint();
					CirStateError state_error = cir_mutation.get_state_error();
					CirMutationNode error_node = this.infection_node(state_error);
					prev.link_to(CirMutationEdgeType.gena_flow, error_node, constraint);
					leafs.add(error_node);
				}
			}
		}
		
		return leafs;
	}
	public static CirMutationGraph new_graph(Mutant mutant, CDependGraph dependence_graph, int maximal_distance) throws Exception {
		CirMutationGraph graph = new CirMutationGraph(mutant);
		graph.build_prefix_paths(dependence_graph);
		return graph;
	}
	
}
