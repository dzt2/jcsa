package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstanceUtils;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymValueError;
import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependReference;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.flwa.symbol.SymEvaluator;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * It implements the construction of symbolic instance nodes and edges.
 * @author yukimula
 *
 */
class SymInstanceBuilder {
	
	/* singleton mode */
	/** the private constructor for generating singleton of the builder **/
	private SymInstanceBuilder() { }
	/** the singleton of the builder for building symbolic instance graph **/
	protected static final SymInstanceBuilder builder = new SymInstanceBuilder();
	
	/* infection part */
	/**
	 * @param dependence_graph
	 * @param instance
	 * @return the sequence of flows that reach from program entry to the instance of target via the control dependence flow.
	 * @throws Exception
	 */
	private List<CirExecutionFlow> get_control_dependence_flows(CDependGraph dependence_graph, CirExecution execution) throws Exception {
		List<CirExecutionFlow> dependence_flows = new ArrayList<CirExecutionFlow>();
		CirExecutionPath dependence_path = CirExecutionPathFinder.finder.dependence_path(dependence_graph, execution);
		for(CirExecutionEdge dependence_edge : dependence_path.get_edges()) {
			switch(dependence_edge.get_type()) {
			case true_flow:
			case fals_flow:
			case call_flow:
			case retr_flow:	dependence_flows.add(dependence_edge.get_flow());	break;
			default: 		break;
			}
		}
		return dependence_flows;
	}
	/**
	 * @param execution
	 * @return the local dependence flows from function entry to the target execution
	 * @throws Exception
	 */
	private List<CirExecutionFlow> get_local_dependence_flows(CirExecution execution) throws Exception {
		CirExecution source = execution.get_graph().get_entry();
		CirExecutionPath dependence_path = new CirExecutionPath(source);
		CirExecutionPathFinder.finder.vf_extend(dependence_path, execution);
		List<CirExecutionFlow> dependence_flows = new ArrayList<CirExecutionFlow>();
		for(CirExecutionEdge dependence_edge : dependence_path.get_edges()) {
			switch(dependence_edge.get_type()) {
			case true_flow:
			case fals_flow:
			case call_flow:
			case retr_flow:	dependence_flows.add(dependence_edge.get_flow());	break;
			default: 		break;
			}
		}
		return dependence_flows;
	}
	/**
	 * @param dependence_graph
	 * @param execution
	 * @return the sequence of flows that reach from program entry to the instance of target via the control dependence flow.
	 * @throws Exception
	 */
	private List<CirExecutionFlow> get_dependence_flows(CDependGraph dependence_graph, CirExecution execution) throws Exception {
		if(dependence_graph != null) {
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			if(instance_graph.has_instances_of(execution)) {
				return this.get_control_dependence_flows(dependence_graph, execution);
			}
			else {
				return this.get_local_dependence_flows(execution);
			}
		}
		else {
			return this.get_local_dependence_flows(execution);
		}
	}
	/**
	 * @param dependence_graph used to construct dependence flows path
	 * @param source the node w.r.t. the program entry
	 * @param target the execution of statement being reached from the entry
	 * @return the node w.r.t. the given execution as target generated from the symbolic instance graph as given
	 * @throws Exception
	 */
	private SymInstanceNode construct_reaching_path(CDependGraph dependence_graph, SymInstanceNode source, CirExecution target) throws Exception {
		/* declarations */
		SymInstanceNode prev_node = source, next_node;
		SymInstanceGraph sym_graph = source.get_graph();
		CirMutations cir_mutations = sym_graph.get_cir_mutations();
		SymConstraint constraint; 
		
		List<CirExecutionFlow> dependence_flows = this.get_dependence_flows(dependence_graph, target);
		for(CirExecutionFlow dependence_flow : dependence_flows) {
			/* link the previous node to the source of the current flow */
			if(prev_node.get_execution() != dependence_flow.get_source()) {
				constraint = cir_mutations.expression_constraint(dependence_flow.get_source().get_statement(), Boolean.TRUE, true);
				next_node = sym_graph.new_node(dependence_flow.get_source(), null);
				prev_node.link_to(SymInstanceEdgeType.exec_flow, next_node, constraint); 
				prev_node = next_node;
			}
			
			/* generate the constraint for reaching target of flow from its source */
			if(dependence_flow.get_type() == CirExecutionFlowType.true_flow) {
				CirStatement if_statement = dependence_flow.get_source().get_statement();
				CirExpression condition;
				if(if_statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) if_statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) if_statement).get_condition();
				}
				constraint = cir_mutations.expression_constraint(if_statement, condition, true);
			}
			else if(dependence_flow.get_type() == CirExecutionFlowType.fals_flow) {
				CirStatement if_statement = dependence_flow.get_source().get_statement();
				CirExpression condition;
				if(if_statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) if_statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) if_statement).get_condition();
				}
				constraint = cir_mutations.expression_constraint(if_statement, condition, false);
			}
			else {
				constraint = cir_mutations.expression_constraint(dependence_flow.get_target().get_statement(), Boolean.TRUE, true);
			}
			
			/* link from the source of the flow to the target of flow */
			next_node = sym_graph.new_node(dependence_flow.get_target(), null);
			prev_node.link_to(SymInstanceEdgeType.exec_flow, next_node, constraint);
			prev_node = next_node;
		}
		
		if(prev_node.get_execution() != target) {
			next_node = sym_graph.new_node(target, null);
			constraint = cir_mutations.expression_constraint(target.get_statement(), Boolean.TRUE, true);
			prev_node.link_to(SymInstanceEdgeType.exec_flow, next_node, constraint);
			prev_node = next_node;
		}
		
		return prev_node;
	}
	/**
	 * generate the paths reaching the symbolic instance graph's reaching nodes
	 * @param dependence_graph
	 * @param sym_graph
	 * @throws Exception
	 */
	protected void generate_reaching_paths(CDependGraph dependence_graph, SymInstanceGraph sym_graph) throws Exception {
		if(sym_graph == null)
			throw new IllegalArgumentException("Invalid sym_graph as null");
		else {
			/* declarations */
			CirExecution entry = sym_graph.get_mutant().get_space().get_cir_tree().
					get_function_call_graph().get_main_function().get_flow_graph().get_entry();
			SymInstanceNode source = sym_graph.new_node(entry, null), target;
			sym_graph.reaching_ndoes = new ArrayList<SymInstanceNode>();
			CirStatement muta_statement; CirExecution muta_execution; 
			
			/* collect the set of cir-mutations w.r.t. the execution where it is seeded */
			Map<CirExecution, List<CirMutation>> init_set = new HashMap<CirExecution, List<CirMutation>>();
			if(sym_graph.get_mutant().has_cir_mutations()) {
				for(CirMutation cir_mutation : sym_graph.get_mutant().get_cir_mutations()) {
					muta_statement = cir_mutation.get_statement();
					muta_execution = muta_statement.get_tree().get_localizer().get_execution(muta_statement);
					if(!init_set.containsKey(muta_execution)) {
						init_set.put(muta_execution, new ArrayList<CirMutation>());
					}
					init_set.get(muta_execution).add(cir_mutation);
				}
			}
			
			/* solve the reaching and infection part of each reaching node */
			sym_graph.reaching_ndoes = new ArrayList<SymInstanceNode>();
			for(CirExecution target_execution : init_set.keySet()) {
				target = this.construct_reaching_path(dependence_graph, source, target_execution);
				for(CirMutation cir_mutation : init_set.get(target_execution)) {
					muta_statement = cir_mutation.get_statement();
					muta_execution = muta_statement.get_tree().get_localizer().get_execution(muta_statement);
					SymInstanceNode init_error_node = sym_graph.new_node(muta_execution, cir_mutation.get_state_error());
					target.link_to(SymInstanceEdgeType.infc_flow, init_error_node, cir_mutation.get_constraint());
				}
				sym_graph.reaching_ndoes.add(target);
			}
		}
	}
	
	/* propagation part */
	/**
	 * build up the error propagation starting from the source
	 * @param source
	 * @throws Exception
	 */
	private void build_local_propagations(SymInstanceNode source) throws Exception {
		if(source.has_state_error()) {
			Collection<CirMutation> next_mutations = SymInstanceUtils.
					propagate(source.get_graph().get_cir_mutations(), source.get_state_error());
			for(CirMutation next_mutation : next_mutations) {
				CirStatement statement = next_mutation.get_statement();
				CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
				SymInstanceNode next_node = source.get_graph().new_node(execution, next_mutation.get_state_error());
				source.link_to(SymInstanceEdgeType.inpa_flow, next_node, next_mutation.get_constraint());
				this.build_local_propagations(next_node);	/* recursively solve the next to enclosing locations */
			}
		}
	}
	/**
	 * @param root
	 * @return
	 * @throws Exception
	 */
	private Collection<SymInstanceNode> get_leafs(SymInstanceNode root) throws Exception {
		Queue<SymInstanceNode> queue = new LinkedList<SymInstanceNode>();
		List<SymInstanceNode> leafs = new ArrayList<SymInstanceNode>();
		
		queue.add(root); 
		while(!queue.isEmpty()) {
			SymInstanceNode node = queue.poll();
			if(node.get_ou_degree() == 0) {
				leafs.add(node);
			}
			else {
				for(SymInstanceEdge edge : node.get_ou_edges()) {
					queue.add(edge.get_target());
				}
			}
		}
		
		return leafs;
	}
	/**
	 * @param source
	 * @return build the local propagation tree and returns its leafs
	 * @throws Exception
	 */
	private Collection<SymInstanceNode> local_propagate(SymInstanceNode source) throws Exception {
		this.build_local_propagations(source);
		return this.get_leafs(source);
	}
	/**
	 * @param statement
	 * @param expression
	 * @return whether the expression is defined in the statement
	 * @throws Exception
	 */
	private boolean is_defined(CirStatement statement, CirExpression expression) throws Exception {
		if(statement instanceof CirAssignStatement) {
			String key = ((CirAssignStatement) statement).get_lvalue().generate_code(true);
			Queue<CirNode> queue = new LinkedList<CirNode>(); queue.add(expression);
			while(!queue.isEmpty()) {
				CirNode parent = queue.poll();
				if(parent.generate_code(true).equals(key)) {
					return true;
				}
				else {
					for(CirNode child : parent.get_children()) {
						queue.add(child);
					}
				}
			}
			return false;
		}
		else {
			return false;
		}
	}
	/**
	 * the set of expressions w.r.t. the given definition used in the statement
	 * @param statement
	 * @param expression
	 * @param use_expressions
	 * @throws Exception
	 */
	private void append_use_expressions(CirStatement statement, CirExpression expression, Collection<CirExpression> use_expressions) throws Exception {
		Queue<CirNode> queue = new LinkedList<CirNode>(); queue.add(statement);
		String code = expression.generate_code(true);
		while(!queue.isEmpty()) {
			CirNode parent = queue.poll();
			if(parent instanceof CirExpression && 
					parent.generate_code(true).equals(code)) {
				boolean is_lvalue = false;
				if(statement instanceof CirAssignStatement) {
					if(((CirAssignStatement) statement).get_lvalue() == parent) {
						is_lvalue = true;
					}
				}
				if(!is_lvalue)
					use_expressions.add((CirExpression) parent);
			}
			else {
				for(CirNode child : parent.get_children()) {
					queue.add(child);
				}
			}
		}
	}
	/**
	 * @param dependence_graph
	 * @param definition
	 * @return the set of expressions of which value is computed at the definition point
	 * @throws Exception
	 */
	private Collection<CirExpression> get_use_expressions(CDependGraph dependence_graph, CirExpression definition) throws Exception {
		Set<CirExpression> use_expressions = new HashSet<CirExpression>();
		CirStatement def_statement = definition.statement_of();
		CirExecution def_execution = def_statement.get_tree().get_localizer().get_execution(def_statement);
		
		if(dependence_graph != null) {
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			if(instance_graph.has_instances_of(def_execution)) {
				for(CirInstanceNode instance : instance_graph.get_instances_of(def_execution)) {
					if(dependence_graph.has_node(instance)) {
						CDependNode dependence_node = dependence_graph.get_node(instance);
						for(CDependEdge dependence_edge : dependence_node.get_in_edges()) {
							switch(dependence_edge.get_type()) {
							case use_defin_depend:
							case param_arg_depend:
							case wait_retr_depend:
							{
								CDependReference element = (CDependReference) dependence_edge.get_element();
								if(element.get_def() == definition) {
									use_expressions.add(element.get_use());
								}
							}
							default:	break;
							}
						}
					}
				}
			}
		}
		
		/* using decidable path analysis to fetch use-expressions */
		if(use_expressions.isEmpty()) {
			CirExecutionPath path = CirExecutionPathFinder.finder.df_extend(def_execution);
			for(CirExecutionEdge edge : path.get_edges()) {
				this.append_use_expressions(edge.get_target().get_statement(), definition, use_expressions);
				if(this.is_defined(edge.get_target().get_statement(), definition)) {
					break;
				}
			}
		}
		
		return use_expressions;
	}
	/**
	 * @param state_error
	 * @param use_expressions
	 * @return generate the data propagation edge from 
	 * @throws Exception
	 */
	private Collection<SymInstanceNode> data_propagate_on(
			SymInstanceNode source, 
			Collection<CirExpression> use_expressions) throws Exception {
		Collection<SymInstanceNode> targets = new ArrayList<SymInstanceNode>();
		SymStateValueError state_error = (SymStateValueError) source.get_state_error();
		for(CirExpression use_expression : use_expressions) {
			CirStatement use_statement = use_expression.statement_of();
			CirExecution use_execution = use_statement.get_tree().get_localizer().get_execution(use_statement);
			SymInstanceNode target = source.get_graph().new_node(use_execution, source.get_graph().
					get_cir_mutations().expr_error(use_expression, state_error.get_mutation_value()));
			source.link_to(SymInstanceEdgeType.oupa_flow, target, 
					source.get_graph().get_cir_mutations().expression_constraint(use_statement, Boolean.TRUE, true));
			targets.add(target);
		}
		return targets;
	}
	/**
	 * @param source
	 * @return the set of symbolic instance nodes generated via data propagation from source
	 * @throws Exception
	 */
	private Collection<SymInstanceNode> data_propagate(CDependGraph dependence_graph, SymInstanceNode source) throws Exception {
		List<SymInstanceNode> next_nodes = new ArrayList<SymInstanceNode>();
		if(source.has_state_error() && dependence_graph != null) {
			SymStateError state_error = source.get_state_error();
			if(state_error instanceof SymStateValueError) {
				Collection<CirExpression> use_expressions = this.get_use_expressions(
						dependence_graph, ((SymStateValueError) state_error).get_expression());
				Collection<SymInstanceNode> targets = this.data_propagate_on(source, use_expressions);
				next_nodes.addAll(targets);
			}
			else if(state_error instanceof SymValueError) {
				CirExpression location = ((SymValueError) state_error).get_expression();
				SymExpression muta_value = ((SymValueError) state_error).get_mutation_value();
				CirMutations cir_mutations = source.get_graph().get_cir_mutations();
				SymConstraint constraint; SymStateError next_error; SymInstanceNode next_node;
				if(location.get_parent() instanceof CirIfStatement || location.get_parent() instanceof CirCaseStatement) {
					muta_value = SymEvaluator.evaluate_on(muta_value);
					if(muta_value instanceof SymConstant) {
						if(((SymConstant) muta_value).get_bool()) {
							CirStatement if_statement = location.statement_of();
							CirExecution if_execution = if_statement.get_tree().get_localizer().get_execution(if_statement);
							CirExecutionFlow true_flow = null, fals_flow = null;
							for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
								if(flow.get_type() == CirExecutionFlowType.true_flow) {
									true_flow = flow;
								}
								else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
									fals_flow = flow;
								}
							}
							constraint = cir_mutations.expression_constraint(if_statement, location, false);
							next_error = cir_mutations.flow_error(fals_flow, true_flow);
							next_node = source.get_graph().new_node(if_execution, next_error);
							next_nodes.add(next_node);
							source.link_to(SymInstanceEdgeType.cont_flow, next_node, constraint);
						}
						else {
							CirStatement if_statement = location.statement_of();
							CirExecution if_execution = if_statement.get_tree().get_localizer().get_execution(if_statement);
							CirExecutionFlow true_flow = null, fals_flow = null;
							for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
								if(flow.get_type() == CirExecutionFlowType.true_flow) {
									true_flow = flow;
								}
								else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
									fals_flow = flow;
								}
							}
							constraint = cir_mutations.expression_constraint(if_statement, location, true);
							next_error = cir_mutations.flow_error(true_flow, fals_flow);
							next_node = source.get_graph().new_node(if_execution, next_error);
							next_nodes.add(next_node);
							source.link_to(SymInstanceEdgeType.cont_flow, next_node, constraint);
						}
					}
					else {
						CirStatement if_statement = location.statement_of();
						CirExecution if_execution = if_statement.get_tree().get_localizer().get_execution(if_statement);
						CirExecutionFlow true_flow = null, fals_flow = null;
						for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
							if(flow.get_type() == CirExecutionFlowType.true_flow) {
								true_flow = flow;
							}
							else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
								fals_flow = flow;
							}
						}
						
						SymExpression condition1 = SymFactory.sym_condition(location, true);
						SymExpression condition2 = SymFactory.sym_condition(muta_value, false);
						SymExpression condition = SymFactory.logic_and(condition1, condition2);
						constraint = cir_mutations.expression_constraint(if_statement, condition, true);
						next_error = cir_mutations.flow_error(true_flow, fals_flow);
						next_node = source.get_graph().new_node(if_execution, next_error);
						next_nodes.add(next_node);
						source.link_to(SymInstanceEdgeType.cont_flow, next_node, constraint);
						
						condition1 = SymFactory.sym_condition(location, false);
						condition2 = SymFactory.sym_condition(muta_value, true);
						condition = SymFactory.logic_and(condition1, condition2);
						constraint = cir_mutations.expression_constraint(if_statement, condition, true);
						next_error = cir_mutations.flow_error(fals_flow, true_flow);
						next_node = source.get_graph().new_node(if_execution, next_error);
						next_nodes.add(next_node);
						source.link_to(SymInstanceEdgeType.cont_flow, next_node, constraint);
					}
				}
			}
		}
		return next_nodes;
	}
	/**
	 * generate the propagation graph from the source root
	 * @param dependence_graph
	 * @param source
	 * @param maximal_distance
	 * @throws Exception
	 */
	private void propagate_from(CDependGraph dependence_graph, SymInstanceNode source, int maximal_distance) throws Exception {
		Collection<SymInstanceNode> local_leafs = this.local_propagate(source);
		if(maximal_distance > 0) {
			for(SymInstanceNode local_leaf : local_leafs) {
				Collection<SymInstanceNode> next_roots = this.data_propagate(dependence_graph, local_leaf);
				for(SymInstanceNode next_root : next_roots) {
					this.propagate_from(dependence_graph, next_root, maximal_distance - 1);
				}
			}
		}
	}
	/**
	 * generate the propagation part from reaching nodes in the graph
	 * @param dependence_graph
	 * @param sym_graph
	 * @throws Exception
	 */
	protected void propagate(CDependGraph dependence_graph, SymInstanceGraph sym_graph, int maximal_distance) throws Exception {
		for(SymInstanceNode reaching_node : sym_graph.get_reaching_nodes()) {
			for(SymInstanceEdge infect_edge : reaching_node.get_ou_edges()) {
				this.propagate_from(dependence_graph, infect_edge.get_target(), maximal_distance);
			}
		}
	}
	
}
