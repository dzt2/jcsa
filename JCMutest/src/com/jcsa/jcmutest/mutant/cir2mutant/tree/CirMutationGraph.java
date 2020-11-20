package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateValueError;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;


public class CirMutationGraph {
	
	/* definitions */
	private CirMutations cir_mutations;
	private Mutant mutant;
	private List<CirMutationNode> nodes;
	private List<CirMutationNode> reaching_nodes;
	private CirMutationGraph(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.cir_mutations = new CirMutations(mutant.get_space().get_cir_tree());
			this.nodes = new ArrayList<CirMutationNode>();
			this.reaching_nodes = new ArrayList<CirMutationNode>();
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
	 * @return node that represents the mutation is killed and failure is caused
	 */
	public CirMutationNode get_final_node() { return this.nodes.get(1); }
	/**
	 * @return the nodes created in this graph
	 */
	public Iterable<CirMutationNode> get_nodes() { return this.nodes; }
	/**
	 * @return the set of execution nodes as referred to the faulty statement being reached
	 * 		   from which the gena-flow edge represents the direct infection after faulty
	 * 		   statement is executed and program state is infected with an initial error.
	 */
	public Iterable<CirMutationNode> get_reaching_nodes() { return this.reaching_nodes; }
	
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
		this.reaching_nodes.clear();
		
		CirFunctionCallGraph fgraph = this.get_cir_tree().get_function_call_graph();
		CirFunction main_function = fgraph.get_main_function();
		CirExecution main_entry = main_function.get_flow_graph().get_entry();
		CirExecution main_exit = main_function.get_flow_graph().get_exit();
		this.nodes.add(this.execution_node(main_entry));
		this.nodes.add(this.execution_node(main_exit));
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
	protected CirMutationNode infection_node(SymStateError state_error) throws Exception {
		CirMutationNode node = new CirMutationNode(this,
				CirMutationNodeType.infects,
				state_error.get_execution(), state_error);
		this.nodes.add(node);
		return node;
	}
	/**
	 * clear the status in the nodes and edges in the graph
	 */
	protected void reset_status() {
		for(CirMutationNode node : this.nodes) {
			node.reset_status();
			for(CirMutationEdge edge : node.get_ou_edges()) {
				edge.reset_status();
			}
		}
	}
	
	/* building methods */
	/**
	 * @param dependence_graph
	 * @return building the part of reachability and initial infection.
	 * @throws Exception
	 */
	private Collection<CirMutationNode> build_prefix_paths(CDependGraph dependence_graph) throws Exception {
		List<CirMutationNode> init_error_nodes = new ArrayList<CirMutationNode>();
		
		this.clear_graph();
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
				List<SymConstraint> path_constraints = CirMutationUtils.utils.
						get_path_constraints(cir_mutations, dependence_graph, execution);
				
				CirMutationNode prev_node = this.get_start_node(), next_node;
				for(SymConstraint constraint : path_constraints) {
					if(prev_node.get_execution() != constraint.get_execution()) {
						next_node = this.execution_node(constraint.get_execution());
						prev_node.link_to(CirMutationEdgeType.path_flow, next_node, constraint);
						prev_node = next_node;
					}
				}
				
				this.reaching_nodes.add(prev_node);	/* add the reaching faulty statement */
				
				for(CirMutation cir_mutation : reaching_map.get(execution)) {
					SymConstraint constraint = cir_mutation.get_constraint();
					SymStateError state_error = cir_mutation.get_state_error();
					CirMutationNode error_node = this.infection_node(state_error);
					prev_node.link_to(CirMutationEdgeType.gena_flow, error_node, constraint);
					init_error_nodes.add(error_node);
				}
			}
		}
		
		return init_error_nodes;
	}
	/**
	 * @param root
	 * @return the leafs that are state-error and can be propagated to next.
	 * @throws Exception
	 */
	private Collection<CirMutationNode> build_in(CirMutationNode root) throws Exception {
		List<CirMutationNode> leafs = new ArrayList<CirMutationNode>();
		Queue<CirMutationNode> queue = new LinkedList<CirMutationNode>();
		
		queue.add(root);
		while(!queue.isEmpty()) {
			CirMutationNode prev = queue.poll();
			
			Collection<CirMutation> next_mutations = CirMutationUtils.utils.
					local_propagate(cir_mutations, prev.get_state_error());
			for(CirMutation next_mutation : next_mutations) {
				CirMutationNode next = this.infection_node(next_mutation.get_state_error());
				queue.add(next);
				prev.link_to(CirMutationEdgeType.gate_flow, next, next_mutation.get_constraint());
			}
			
			if(prev.get_state_error() instanceof SymStateValueError) {
				leafs.add(prev);
			}
		}
		
		return leafs;
	}
	/**
	 * @param source
	 * @return the error on data-define propagation directly from source
	 * @throws Exception
	 */
	private Collection<CirMutationNode> build_on(CDependGraph dependence_graph, CirMutationNode source) throws Exception {
		CirExpression def_expression; SymExpression mutation_value;
		SymStateError state_error = source.get_state_error();
		if(state_error instanceof SymExpressionError) {
			def_expression = ((SymExpressionError) state_error).get_expression();
			mutation_value = ((SymExpressionError) state_error).get_mutation_value();
		}
		else if(state_error instanceof SymReferenceError) {
			def_expression = ((SymReferenceError) state_error).get_expression();
			mutation_value = ((SymReferenceError) state_error).get_mutation_value();
		}
		else if(state_error instanceof SymStateValueError) {
			def_expression = ((SymStateValueError) state_error).get_expression();
			mutation_value = ((SymStateValueError) state_error).get_mutation_value();
		}
		else {
			def_expression = null;
			mutation_value = null;
		}
		
		List<CirMutationNode> targets = new ArrayList<CirMutationNode>();
		
		if(def_expression != null) {
			CirExecution source_execution = source.get_execution();
			
			Collection<CirExpression> use_expressions = CirMutationUtils.utils.
					find_use_expressions(dependence_graph, def_expression);
			
			for(CirExpression use_expression : use_expressions) {
				CirStatement use_statement = use_expression.statement_of();
				CirExecution target_execution = use_statement.get_tree().get_localizer().get_execution(use_statement);
				
				List<SymConstraint> constraints = CirMutationUtils.utils.get_path_constraints(cir_mutations, source_execution, target_execution);
				CirMutationNode prev = source, next; boolean first = true;
				for(SymConstraint constraint : constraints) {
					next = this.execution_node(constraint.get_execution());
					if(first) {
						first = false;
						prev.link_to(CirMutationEdgeType.actv_flow, next, constraint);
					}
					else {
						prev.link_to(CirMutationEdgeType.path_flow, next, constraint);
					}
					prev = next;
				}
				
				SymStateError next_error = cir_mutations.expr_error(use_expression, mutation_value);
				next = this.infection_node(next_error);
				prev.link_to(CirMutationEdgeType.gena_flow, next, cir_mutations.expression_constraint(use_statement, Boolean.TRUE, true));
				
				targets.add(next);
			}
		}
		
		return targets;
	}
	/**
	 * build the graph from the source node with the maximal distance as given.
	 * @param source
	 * @param dependence_graph
	 * @param distance
	 * @throws Exception
	 */
	private void build_from(CirMutationNode source, CDependGraph 
			dependence_graph, int distance) throws Exception {
		Collection<CirMutationNode> targets = this.build_in(source);
		if(distance > 0) {
			for(CirMutationNode target : targets) {
				Collection<CirMutationNode> next_targets = this.build_on(dependence_graph, target);
				for(CirMutationNode next_target : next_targets) {
					this.build_from(next_target, dependence_graph, distance - 1);
				}
			}
		}
	}
	/**
	 * create a mutation branch graph w.r.t. the mutant as given
	 * @param mutant
	 * @param dependence_graph used to build reachability and propagation edges
	 * @param maximal_distance the maximal distance from the initial error nodes to following in propagation
	 * @return 
	 * @throws Exception
	 */
	public static CirMutationGraph new_graph(Mutant mutant, CDependGraph dependence_graph, int maximal_distance) throws Exception {
		CirMutationGraph graph = new CirMutationGraph(mutant);
		Collection<CirMutationNode> init_error_nodes = graph.build_prefix_paths(dependence_graph);
		for(CirMutationNode init_error_node : init_error_nodes) {
			graph.build_from(init_error_node, dependence_graph, maximal_distance);
		}
		return graph;
	}
	
}
