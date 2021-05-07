package com.jcsa.jcmutest.mutant.sym2mutant.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sym2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateValueError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymValueError;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceNode;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceTree;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceTreeEdge;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceTreeNode;
import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependReference;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
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
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;


/**
 * It implements interfaces for constructing and evaluation symbolic tree of killing a mutation.
 * 
 * @author yukimula
 *
 */
public class SymTreeUtils {
	
	/* previous construction */
	/**
	 * @param dependence_graph
	 * @param instance
	 * @return the sequence of execution flows (true, false, call, return) required from program entry to
	 * 		   the instance of execution node (statement) in program as coverage target.
	 * @throws Exception
	 */
	private static List<CirExecutionFlow> extract_prev_flows(CDependGraph dependence_graph, CirInstanceNode instance) throws Exception {
		List<CirExecutionFlow> dependence_flows = new ArrayList<CirExecutionFlow>();
		CirExecutionPath dependence_path;
		
		if(dependence_graph!= null && dependence_graph.has_node(instance)) {
			dependence_path = CirExecutionPathFinder.finder.dependence_path(dependence_graph, instance);
		}
		else {
			CirExecution source = instance.get_execution().get_graph().get_entry();
			dependence_path = new CirExecutionPath(source);
			CirExecutionPathFinder.finder.vf_extend(dependence_path, instance.get_execution());
		}
		
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
	 * @return
	 * @throws Exception
	 */
	private static List<CirExecutionFlow> extract_prev_flows(CDependGraph dependence_graph, CirExecution execution) throws Exception {
		List<CirExecutionFlow> dependence_flows = new ArrayList<CirExecutionFlow>();
		CirExecutionPath dependence_path;
		
		if(dependence_graph != null) {
			dependence_path = CirExecutionPathFinder.finder.dependence_path(dependence_graph, execution);
		}
		else {
			CirExecution source = execution.get_graph().get_entry();
			dependence_path = new CirExecutionPath(source);
			CirExecutionPathFinder.finder.vf_extend(dependence_path, execution);
		}
		
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
	 * @param tree
	 * @param flows
	 * @param target
	 * @return construct a path from root to the leaf of target execution being covered.
	 * @throws Exception
	 */
	private static SymInstanceTreeNode new_coverage_path_for(SymInstanceTree tree, List<CirExecutionFlow> flows, CirExecution target) throws Exception {
		/* declarations */
		SymInstanceTreeNode tree_node = tree.get_root();
		SymInstance edge_instance, node_instance;
		
		/* construct from root to prev-leaf on given leafs */
		for(CirExecutionFlow flow : flows) {
			/* generate instances for edge and child node */
			switch(flow.get_type()) {
			case true_flow:
			{
				CirStatement statement = flow.get_source().get_statement();
				CirExpression predicate;
				if(statement instanceof CirIfStatement) {
					predicate = ((CirIfStatement) statement).get_condition();
				}
				else {
					predicate = ((CirCaseStatement) statement).get_condition();
				}
				edge_instance = SymInstanceUtils.expr_constraint(statement, predicate, true);
				node_instance = SymInstanceUtils.expr_constraint(flow.get_target(), Boolean.TRUE, true);
				break;
			}
			case fals_flow:
			{
				CirStatement statement = flow.get_source().get_statement();
				CirExpression predicate;
				if(statement instanceof CirIfStatement) {
					predicate = ((CirIfStatement) statement).get_condition();
				}
				else {
					predicate = ((CirCaseStatement) statement).get_condition();
				}
				edge_instance = SymInstanceUtils.expr_constraint(statement, predicate, false);
				node_instance = SymInstanceUtils.expr_constraint(flow.get_target(), Boolean.TRUE, true);
				break;
			}
			case call_flow:
			case retr_flow:
			{
				CirStatement source_statement = flow.get_source().get_statement();
				CirStatement target_statement = flow.get_target().get_statement();
				edge_instance = SymInstanceUtils.expr_constraint(source_statement, Boolean.TRUE, true);
				node_instance = SymInstanceUtils.expr_constraint(target_statement, Boolean.TRUE, true);
				break;
			}
			default:
			{
				node_instance = null; 
				edge_instance = null;
				break;
			}
			}
			/* construct the child node from current tree node */
			tree_node = tree_node.new_child(edge_instance, node_instance);
		}
		
		if(tree_node.get_execution() != target) {
			edge_instance = SymInstanceUtils.expr_constraint(tree_node.get_execution(), Boolean.TRUE, true);
			node_instance = SymInstanceUtils.expr_constraint(target.get_statement(), Boolean.TRUE, true);
			tree_node = tree_node.new_child(edge_instance, node_instance);
		}
		return tree_node;
	}
	/**
	 * @param tree
	 * @param dependence_graph
	 * @return the set of tree nodes that reach the statement where the mutant is seeded.
	 * @throws Exception
	 */
	private static Collection<SymInstanceTreeNode> construct_prev_paths(SymInstanceTree tree, 
			CirExecution mutation_execution, CDependGraph dependence_graph) throws Exception {
		/* declarations */
		Collection<SymInstanceTreeNode> muta_nodes = new ArrayList<SymInstanceTreeNode>();
		List<CirExecutionFlow> coverage_flows;
		
		/* dependence-based coverage paths from root the nodes covering the faulty statement */
		if(dependence_graph != null) {
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			if(instance_graph.has_instances_of(mutation_execution)) {
				for(CirInstanceNode instance : instance_graph.get_instances_of(mutation_execution)) {
					if(dependence_graph.has_node(instance)) {
						coverage_flows = SymTreeUtils.extract_prev_flows(dependence_graph, instance);
						muta_nodes.add(SymTreeUtils.new_coverage_path_for(tree, coverage_flows, mutation_execution));
					}
				}
			}
		}
		
		/* non-dependence based coverage path construction from root to mutation statement */
		if(muta_nodes.isEmpty()) {
			coverage_flows = SymTreeUtils.extract_prev_flows(dependence_graph, mutation_execution);
			muta_nodes.add(SymTreeUtils.new_coverage_path_for(tree, coverage_flows, mutation_execution));
		}
		
		/* the collection of tree nodes representing mutation being reached */	return muta_nodes;
	}
	/* data flow analysis */
	/**
	 * @param statement
	 * @param expression
	 * @return whether the expression is defined in the statement
	 * @throws Exception
	 */
	private static boolean is_defined(CirStatement statement, CirExpression expression) throws Exception {
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
	private static void append_use_expressions(CirStatement statement, CirExpression expression, Collection<CirExpression> use_expressions) throws Exception {
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
	private static Collection<CirExpression> get_use_expressions(CDependGraph dependence_graph, CirExpression definition) throws Exception {
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
				SymTreeUtils.append_use_expressions(edge.get_target().get_statement(), definition, use_expressions);
				if(SymTreeUtils.is_defined(edge.get_target().get_statement(), definition)) {
					break;
				}
			}
		}
		
		return use_expressions;
	}
	/* postfix construction */
	/**
	 * build up the local propagation tree from source tree node
	 * @param tree_node
	 * @param leafs
	 * @throws Exception
	 */
	private static void build_local_propagation(SymInstanceTreeNode tree_node, Collection<SymInstanceTreeNode> leafs) throws Exception {
		if(tree_node.is_state_error()) {
			SymStateError source_error = (SymStateError) tree_node.get_instance();
			Collection<CirMutation> next_mutations = SymInstanceUtils.propagate(source_error);
			
			if(next_mutations.isEmpty()) {
				leafs.add(tree_node);
			}
			else {
				for(CirMutation next_mutation : next_mutations) {
					SymInstance edge_instance = next_mutation.get_constraint();
					SymInstance node_instance = next_mutation.get_state_error();
					SymInstanceTreeNode next_node = tree_node.new_child(edge_instance, node_instance);
					SymTreeUtils.build_local_propagation(next_node, leafs);
				}
			}
		}
	}
	/**
	 * @param tree_node
	 * @return build up the local propagation tree from tree-node and return newly created leaf nodes
	 * @throws Exception
	 */
	private static Collection<SymInstanceTreeNode> build_local_propagations(SymInstanceTreeNode tree_node) throws Exception {
		Collection<SymInstanceTreeNode> leafs = new ArrayList<SymInstanceTreeNode>();
		if(tree_node.is_state_error()) {
			SymTreeUtils.build_local_propagation(tree_node, leafs);
		}
		return leafs;
	}
	/**
	 * @param tree_node
	 * @param use_expressions
	 * @return 
	 * @throws Exception
	 */
	private static Collection<SymInstanceTreeNode> build_data_propagation(SymInstanceTreeNode tree_node, 
			Collection<CirExpression> use_expressions) throws Exception {
		Collection<SymInstanceTreeNode> next_nodes = new ArrayList<SymInstanceTreeNode>();
		SymStateValueError state_error = (SymStateValueError) tree_node.get_instance();
		
		for(CirExpression use_expression : use_expressions) {
			CirStatement use_statement = use_expression.statement_of();
			SymInstance edge_instance = SymInstanceUtils.expr_constraint(use_statement, Boolean.TRUE, true);
			SymInstance node_instance = SymInstanceUtils.expr_error(use_expression, state_error.get_mutation_value());
			next_nodes.add(tree_node.new_child(edge_instance, node_instance));
		}
		
		return next_nodes;
	}
	/**
	 * @param tree_node
	 * @return
	 * @throws Exception
	 */
	private static Collection<SymInstanceTreeNode> build_control_propagation(SymInstanceTreeNode tree_node) throws Exception {
		SymStateError state_error = (SymStateError) tree_node.get_instance();
		CirExpression location = ((SymValueError) state_error).get_expression();
		SymbolExpression muta_value = ((SymValueError) state_error).get_mutation_value();
		SymConstraint constraint; SymStateError next_error;
		Collection<SymInstanceTreeNode> next_nodes = new ArrayList<SymInstanceTreeNode>();
		
		if(location.get_parent() instanceof CirIfStatement || location.get_parent() instanceof CirCaseStatement) {
			muta_value = SymbolEvaluator.evaluate_on(muta_value);
			if(muta_value instanceof SymbolConstant) {
				if(((SymbolConstant) muta_value).get_bool()) {
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
					constraint = SymInstanceUtils.expr_constraint(if_statement, location, false);
					next_error = SymInstanceUtils.flow_error(fals_flow, true_flow);
					next_nodes.add(tree_node.new_child(constraint, next_error));
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
					constraint = SymInstanceUtils.expr_constraint(if_statement, location, true);
					next_error = SymInstanceUtils.flow_error(true_flow, fals_flow);
					next_nodes.add(tree_node.new_child(constraint, next_error));
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
				
				SymbolExpression condition1 = SymbolFactory.sym_condition(location, true);
				SymbolExpression condition2 = SymbolFactory.sym_condition(muta_value, false);
				SymbolExpression condition = SymbolFactory.logic_and(condition1, condition2);
				constraint = SymInstanceUtils.expr_constraint(if_statement, condition, true);
				next_error = SymInstanceUtils.flow_error(true_flow, fals_flow);
				next_nodes.add(tree_node.new_child(constraint, next_error));
				
				condition1 = SymbolFactory.sym_condition(location, false);
				condition2 = SymbolFactory.sym_condition(muta_value, true);
				condition = SymbolFactory.logic_and(condition1, condition2);
				constraint = SymInstanceUtils.expr_constraint(if_statement, condition, true);
				next_error = SymInstanceUtils.flow_error(fals_flow, true_flow);
				next_nodes.add(tree_node.new_child(constraint, next_error));
			}
		}
		
		return next_nodes;
	}
	/**
	 * @param tree_node
	 * @param dependence_graph
	 * @return generate long propagation via data dependence relationships
	 * @throws Exception 
	 */
	private static Collection<SymInstanceTreeNode> build_long_propagations(
			SymInstanceTreeNode tree_node, CDependGraph dependence_graph) throws Exception {
		Collection<SymInstanceTreeNode> next_nodes = new ArrayList<SymInstanceTreeNode>();
		
		if(tree_node.is_state_error()) {
			SymStateError state_error = (SymStateError) tree_node.get_instance();
			
			if(state_error instanceof SymStateValueError) {
				Collection<CirExpression> use_expressions = SymTreeUtils.get_use_expressions(
						dependence_graph, ((SymStateValueError) state_error).get_expression());
				Collection<SymInstanceTreeNode> targets = SymTreeUtils.build_data_propagation(tree_node, use_expressions);
				next_nodes.addAll(targets);
			}
			else if(state_error instanceof SymValueError) {
				Collection<SymInstanceTreeNode> targets = SymTreeUtils.build_control_propagation(tree_node);
				next_nodes.addAll(targets);
			}
		}
		
		return next_nodes;
	}
	/**
	 * build the local propagation tree from input node and propagate it for distance as given
	 * @param tree_node
	 * @param distance
	 * @throws Exception
	 */
	private static void construct_propagations_on(SymInstanceTreeNode tree_node, 
			int distance, CDependGraph dependence_graph) throws Exception {
		Collection<SymInstanceTreeNode> local_leafs = SymTreeUtils.build_local_propagations(tree_node);
		
		if(distance > 0 && !local_leafs.isEmpty()) {
			Collection<SymInstanceTreeNode> long_roots = new ArrayList<SymInstanceTreeNode>();
			for(SymInstanceTreeNode local_leaf : local_leafs) {
				long_roots.addAll(SymTreeUtils.build_long_propagations(local_leaf, dependence_graph));
			}
			
			for(SymInstanceTreeNode long_root : long_roots) {
				SymTreeUtils.construct_propagations_on(long_root, distance - 1, dependence_graph);
			}
		}
	}
	/* entire tree construction */
	/**
	 * @param tree the symbolic tree being constructed
	 * @param distance maximal distance for propagating errors
	 * @param dependence_graph dependence graph to build paths
	 * @throws Exception
	 */
	public static void construct_tree(SymInstanceTree tree, int distance, CDependGraph dependence_graph) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else {
			/* collection execution-infection pairs */
			Map<CirExecution, Collection<CirMutation>> execution_infections
				= new HashMap<CirExecution, Collection<CirMutation>>();
			if(tree.get_mutant().has_cir_mutations()) {
				for(CirMutation cir_mutation : tree.get_mutant().get_cir_mutations()) {
					CirExecution execution = cir_mutation.get_execution();
					if(!execution_infections.containsKey(execution)) {
						execution_infections.put(execution, new ArrayList<CirMutation>());
					}
					execution_infections.get(execution).add(cir_mutation);
				}
			}
			
			/* construct prev-post paths on execution pivot */
			for(CirExecution execution : execution_infections.keySet()) {
				Collection<CirMutation> infections = execution_infections.get(execution);
				Collection<SymInstanceTreeNode> muta_tree_nodes = 
						SymTreeUtils.construct_prev_paths(tree, execution, dependence_graph);
				
				for(SymInstanceTreeNode muta_tree_node : muta_tree_nodes) {
					for(CirMutation infection : infections) {
						SymInstanceTreeNode infection_node = muta_tree_node.new_child(
								infection.get_constraint(), infection.get_state_error());
						SymTreeUtils.construct_propagations_on(infection_node, distance, dependence_graph);
					}
				}
			}
		}
	}
	
	/* static evaluation methods */
	private static void static_evaluate_on(SymInstanceTreeEdge edge) throws Exception {
		edge.get_concrete_status().append(null);
		Boolean result = edge.get_concrete_status().get_evaluation_result();
		if(result == null || result.booleanValue()) {
			SymTreeUtils.static_evaluate_on(edge.get_child());
		}
		else {
			return;	/* unreachable to the direct children in */
		}
	}
	private static void static_evaluate_on(SymInstanceTreeNode node) throws Exception {
		node.get_concrete_status().append(null);
		Boolean result = node.get_concrete_status().get_evaluation_result();
		if(result == null || result.booleanValue()) {
			for(SymInstanceTreeEdge edge : node.get_ou_edges()) {
				SymTreeUtils.static_evaluate_on(edge);
			}
		}
		else {
			return;	/* unreachable to the direct children in */
		}
	}
	/**
	 * @param tree
	 * @return the collection of reachable paths from static evaluation result.
	 * @throws Exception
	 */
	public static void static_evaluate(SymInstanceTree tree) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else {
			SymTreeUtils.static_evaluate_on(tree.get_root());
		}
	}
	
	/* dynamic evaluation methods */
	/**
	 * update the states before mutation nodes using dynamic information
	 * @param tree
	 * @param test_path
	 * @throws Exception
	 */
	private static void dynamic_prev_evaluations(SymInstanceTree tree, CStatePath test_path) throws Exception {
		/* collect all the nodes from root to mutation nodes */
		Collection<SymInstanceTreeEdge> infection_edges = tree.get_infection_edges();
		Set<SymInstanceNode> prev_nodes = new HashSet<SymInstanceNode>();
		for(SymInstanceTreeEdge infection_edge : infection_edges) {
			SymInstanceTreeEdge tree_edge = infection_edge;
			while(tree_edge != null) {
				prev_nodes.add(tree_edge);
				prev_nodes.add(tree_edge.get_parent());
				tree_edge = tree_edge.get_parent().get_in_edge();
			}
		}
		
		/* divide the instance edge or node based on execution */
		Map<CirExecution, Collection<SymInstanceNode>> execution_nodes = 
				new HashMap<CirExecution, Collection<SymInstanceNode>>();
		for(SymInstanceNode prev_node : prev_nodes) {
			CirExecution execution = prev_node.get_execution();
			if(!execution_nodes.containsKey(execution)) {
				execution_nodes.put(execution, new ArrayList<SymInstanceNode>());
			}
			execution_nodes.get(execution).add(prev_node);
		}
		
		/* perform dynamic evaluation on all prev-states */
		SymbolStateContexts contexts = new SymbolStateContexts();
		for(CStateNode state_node : test_path.get_nodes()) {
			contexts.accumulate(state_node);
			CirExecution execution = state_node.get_execution();
			if(execution_nodes.containsKey(execution)) {
				for(SymInstanceNode node : execution_nodes.get(execution)) {
					node.get_concrete_status().append(contexts);
				}
			}
		}
	}
	/**
	 * update the states after mutation nodes w.r.t. the given muta_execution node
	 * @param tree
	 * @param test_path
	 * @throws Exception
	 */
	private static void dynamic_post_evaluations(SymInstanceTree tree, CirExecution muta_execution, CStatePath test_path) throws Exception {
		/* collect nodes after reachable mutation nodes */
		Collection<SymInstanceNode> post_nodes = new HashSet<SymInstanceNode>();
		Collection<SymInstanceTreeEdge> infection_edges = tree.get_infection_edges();
		for(SymInstanceTreeEdge infection_edge : infection_edges) {
			if(infection_edge.get_parent().get_execution() == muta_execution) {
				post_nodes.add(infection_edge);
				Collection<SymInstanceTreeNode> children = infection_edge.get_child().get_all_children();
				for(SymInstanceTreeNode child : children) {
					post_nodes.add(child);
					if(child.get_in_edge() != null) {
						post_nodes.add(child.get_in_edge());
					}
				}
			}
		}
		
		/* mapping from execution to corresponding node or edges in tree */
		Map<CirExecution, Collection<SymInstanceNode>> execution_nodes = 
				new HashMap<CirExecution, Collection<SymInstanceNode>>();
		for(SymInstanceNode post_node : post_nodes) {
			CirExecution execution = post_node.get_execution();
			if(!execution_nodes.containsKey(execution)) {
				execution_nodes.put(execution, new ArrayList<SymInstanceNode>());
			}
			execution_nodes.get(execution).add(post_node);
		}
		
		/* perform dynamic evaluation on all post-states */
		SymbolStateContexts contexts = new SymbolStateContexts();
		boolean reached = false;
		for(CStateNode state_node : test_path.get_nodes()) {
			/* update contexts and reached tag */
			contexts.accumulate(state_node);
			if(state_node.get_execution() == muta_execution) { reached = true; }
			
			/* since mutation nodes is reached */
			if(reached) {
				CirExecution target = state_node.get_execution();
				if(execution_nodes.containsKey(target)) {
					for(SymInstanceNode node : execution_nodes.get(target)) {
						node.get_concrete_status().append(contexts);
					}
				}
			}
		}
	}
	/**
	 * perform dynamic evaluation on post part after mutation nodes
	 * @param tree
	 * @param test_path
	 * @throws Exception
	 */
	private static void dynamic_post_evaluations(SymInstanceTree tree, CStatePath test_path) throws Exception {
		Set<CirExecution> muta_executions = new HashSet<CirExecution>();
		Collection<SymInstanceTreeEdge> infection_edges = tree.get_infection_edges();
		for(SymInstanceTreeEdge infection_edge : infection_edges) {
			Boolean result = infection_edge.get_parent().get_concrete_status().get_evaluation_result();
			if(result == null || result.booleanValue())
				muta_executions.add(infection_edge.get_parent().get_execution());
		}
		
		for(CirExecution muta_execution : muta_executions) {
			SymTreeUtils.dynamic_post_evaluations(tree, muta_execution, test_path);
		}
	}
	/**
	 * @param tree
	 * @param test_path
	 * @throws Exception
	 */
	public static void dynamic_evaluate(SymInstanceTree tree, CStatePath test_path) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(test_path == null)
			throw new IllegalArgumentException("Invalid test_path: null");
		else {
			SymTreeUtils.dynamic_prev_evaluations(tree, test_path);
			SymTreeUtils.dynamic_post_evaluations(tree, test_path);
		}
	}
	
}
