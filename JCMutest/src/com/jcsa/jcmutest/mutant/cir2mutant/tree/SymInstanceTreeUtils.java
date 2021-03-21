package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
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
 * It implements the construction and evaluation on symbolic instance in tree.
 * 
 * @author yukimula
 *
 */
class SymInstanceTreeUtils {
	
	/* singleton mode */	private SymInstanceTreeUtils() { }
	protected static final SymInstanceTreeUtils utils = new SymInstanceTreeUtils();
	
	/* previous path construction */
	/**
	 * @param dependence_graph
	 * @param instance
	 * @return the sequence of execution flows (true, false, call, return) required from program entry to
	 * 		   the instance of execution node (statement) in program as coverage target.
	 * @throws Exception
	 */
	private List<CirExecutionFlow> extract_prev_flows(CDependGraph dependence_graph, CirInstanceNode instance) throws Exception {
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
	private List<CirExecutionFlow> extract_prev_flows(CDependGraph dependence_graph, CirExecution execution) throws Exception {
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
	 * @return create the tree path from entry to reach the target via given flows
	 * @throws Exception
	 */
	private SymInstanceTreeNode new_coverage_tree_node(SymInstanceTree tree, 
			List<CirExecutionFlow> flows, CirExecution target) throws Exception {
		SymInstanceTreeNode tree_node = tree.get_root();
		CirMutations cir_mutations = tree.get_cir_mutations();
		
		for(CirExecutionFlow flow : flows) {
			SymInstance node_instance, edge_instance;
			
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
				edge_instance = cir_mutations.expression_constraint(statement, predicate, true);
				node_instance = cir_mutations.expression_constraint(flow.get_target().get_statement(), Boolean.TRUE, true);
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
				edge_instance = cir_mutations.expression_constraint(statement, predicate, false);
				node_instance = cir_mutations.expression_constraint(flow.get_target().get_statement(), Boolean.TRUE, true);
				break;
			}
			case call_flow:
			case retr_flow:
			{
				CirStatement source_statement = flow.get_source().get_statement();
				CirStatement target_statement = flow.get_target().get_statement();
				edge_instance = cir_mutations.expression_constraint(source_statement, Boolean.TRUE, true);
				node_instance = cir_mutations.expression_constraint(target_statement, Boolean.TRUE, true);
				break;
			}
			default:
			{
				node_instance = null; 
				edge_instance = null;
				break;
			}
			}
			
			tree_node = tree_node.new_child(edge_instance, node_instance);
		}
		
		if(tree_node.get_node_status().get_execution() != target) { 
			SymInstance instance;
			instance = cir_mutations.expression_constraint(target.get_statement(), Boolean.TRUE, true);
			tree_node = tree_node.new_child(instance, instance);
		}
		
		return tree_node;
	}
	/**
	 * @param dependence_graph
	 * @param execution
	 * @return collection of tree node created from the root to 
	 * @throws Exception
	 */
	private Collection<SymInstanceTreeNode> build_muta_nodes(SymInstanceTree tree, 
				CirExecution execution, CDependGraph dependence_graph) throws Exception {
		Collection<SymInstanceTreeNode> muta_nodes = new ArrayList<SymInstanceTreeNode>();
		List<CirExecutionFlow> coverage_flows;
		
		/* dynamic instance path for covering target statement */
		if(dependence_graph != null) {
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			if(instance_graph.has_instances_of(execution)) {
				for(CirInstanceNode instance : instance_graph.get_instances_of(execution)) {
					if(dependence_graph.has_node(instance)) {
						coverage_flows = this.extract_prev_flows(dependence_graph, instance);
						muta_nodes.add(this.new_coverage_tree_node(tree, coverage_flows, execution));
					}
				}
			}
		}
		
		/* static non-instance path for reaching target statement */
		if(muta_nodes.isEmpty()) {
			coverage_flows = this.extract_prev_flows(dependence_graph, execution);
			muta_nodes.add(this.new_coverage_tree_node(tree, coverage_flows, execution));
		}
		
		/* collection of coverage path annotated with constraints to target execution */
		return muta_nodes;
	}
	
	/* data flow analysis */
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
	
	/* following path construction */
	/**
	 * build up the local propagation tree from source tree node
	 * @param tree_node
	 * @param leafs
	 * @throws Exception
	 */
	private void build_local_propagation(SymInstanceTreeNode tree_node, Collection<SymInstanceTreeNode> leafs) throws Exception {
		if(tree_node.get_node_status().is_state_error()) {
			SymStateError source_error = 
					(SymStateError) tree_node.get_node_status().get_instance();
			Collection<CirMutation> next_mutations = SymInstanceUtils.propagate(
							tree_node.get_tree().get_cir_mutations(), source_error);
			
			if(next_mutations.isEmpty()) {
				leafs.add(tree_node);
			}
			else {
				for(CirMutation next_mutation : next_mutations) {
					SymInstance edge_instance = next_mutation.get_constraint();
					SymInstance node_instance = next_mutation.get_state_error();
					SymInstanceTreeNode next_node = tree_node.new_child(edge_instance, node_instance);
					this.build_local_propagation(next_node, leafs);
				}
			}
		}
	}
	/**
	 * @param tree_node
	 * @return build up the local propagation tree from tree-node and return newly created leaf nodes
	 * @throws Exception
	 */
	private Collection<SymInstanceTreeNode> build_local_propagations(SymInstanceTreeNode tree_node) throws Exception {
		Collection<SymInstanceTreeNode> leafs = new ArrayList<SymInstanceTreeNode>();
		if(tree_node.get_node_status().is_state_error()) {
			this.build_local_propagation(tree_node, leafs);
		}
		return leafs;
	}
	/**
	 * @param tree_node
	 * @param use_expressions
	 * @return 
	 * @throws Exception
	 */
	private Collection<SymInstanceTreeNode> build_data_propagation(SymInstanceTreeNode tree_node, 
			Collection<CirExpression> use_expressions) throws Exception {
		Collection<SymInstanceTreeNode> next_nodes = new ArrayList<SymInstanceTreeNode>();
		CirMutations cir_mutations = tree_node.get_tree().get_cir_mutations();
		SymStateValueError state_error = (SymStateValueError) tree_node.get_node_status().get_instance();
		
		for(CirExpression use_expression : use_expressions) {
			CirStatement use_statement = use_expression.statement_of();
			SymInstance edge_instance = cir_mutations.expression_constraint(use_statement, Boolean.TRUE, true);
			SymInstance node_instance = cir_mutations.expr_error(use_expression, state_error.get_mutation_value());
			next_nodes.add(tree_node.new_child(edge_instance, node_instance));
		}
		
		return next_nodes;
	}
	/**
	 * @param tree_node
	 * @return
	 * @throws Exception
	 */
	private Collection<SymInstanceTreeNode> build_control_propagation(SymInstanceTreeNode tree_node) throws Exception {
		SymStateError state_error = (SymStateError) tree_node.get_node_status().get_instance();
		CirExpression location = ((SymValueError) state_error).get_expression();
		SymbolExpression muta_value = ((SymValueError) state_error).get_mutation_value();
		CirMutations cir_mutations = tree_node.get_tree().get_cir_mutations();
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
					constraint = cir_mutations.expression_constraint(if_statement, location, false);
					next_error = cir_mutations.flow_error(fals_flow, true_flow);
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
					constraint = cir_mutations.expression_constraint(if_statement, location, true);
					next_error = cir_mutations.flow_error(true_flow, fals_flow);
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
				constraint = cir_mutations.expression_constraint(if_statement, condition, true);
				next_error = cir_mutations.flow_error(true_flow, fals_flow);
				next_nodes.add(tree_node.new_child(constraint, next_error));
				
				condition1 = SymbolFactory.sym_condition(location, false);
				condition2 = SymbolFactory.sym_condition(muta_value, true);
				condition = SymbolFactory.logic_and(condition1, condition2);
				constraint = cir_mutations.expression_constraint(if_statement, condition, true);
				next_error = cir_mutations.flow_error(fals_flow, true_flow);
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
	private Collection<SymInstanceTreeNode> build_long_propagations(SymInstanceTreeNode tree_node, 
			CDependGraph dependence_graph) throws Exception {
		Collection<SymInstanceTreeNode> next_nodes = new ArrayList<SymInstanceTreeNode>();
		
		if(tree_node.get_node_status().is_state_error()) {
			SymStateError state_error = (SymStateError) tree_node.get_node_status().get_instance();
			
			if(state_error instanceof SymStateValueError) {
				Collection<CirExpression> use_expressions = this.get_use_expressions(
						dependence_graph, ((SymStateValueError) state_error).get_expression());
				Collection<SymInstanceTreeNode> targets = this.build_data_propagation(tree_node, use_expressions);
				next_nodes.addAll(targets);
			}
			else if(state_error instanceof SymValueError) {
				Collection<SymInstanceTreeNode> targets = this.build_control_propagation(tree_node);
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
	private void build_propagations_on(SymInstanceTreeNode tree_node, 
			int distance, CDependGraph dependence_graph) throws Exception {
		Collection<SymInstanceTreeNode> local_leafs = this.build_local_propagations(tree_node);
		
		if(distance > 0 && !local_leafs.isEmpty()) {
			Collection<SymInstanceTreeNode> long_roots = new ArrayList<SymInstanceTreeNode>();
			for(SymInstanceTreeNode local_leaf : local_leafs) {
				long_roots.addAll(this.build_long_propagations(local_leaf, dependence_graph));
			}
			
			for(SymInstanceTreeNode long_root : long_roots) {
				this.build_propagations_on(long_root, distance - 1, dependence_graph);
			}
		}
	}
	
	/* building methods */
	/**
	 * build up the symbolic instance tree as template for mutation analysis
	 * @param mutant
	 * @param propagation_distance maximal distance for propagating state errors
	 * @param dependence_graph used to build path and data flow propagation
	 * @throws Exception
	 */
	protected SymInstanceTree build_sym_instance_tree(Mutant mutant, int propagation_distance, CDependGraph dependence_graph) throws Exception {
		SymInstanceTree tree = new SymInstanceTree(mutant);
		
		Map<CirExecution, Collection<CirMutation>> execution_infections
				= new HashMap<CirExecution, Collection<CirMutation>>();
		if(mutant.has_cir_mutations()) {
			for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
				CirExecution execution = cir_mutation.get_execution();
				if(!execution_infections.containsKey(execution)) {
					execution_infections.put(execution, new ArrayList<CirMutation>());
				}
				execution_infections.get(execution).add(cir_mutation);
			}
		}
		
		for(CirExecution mutation_execution : execution_infections.keySet()) {
			Collection<CirMutation> infections = execution_infections.get(mutation_execution);
			Collection<SymInstanceTreeNode> muta_tree_nodes = 
					this.build_muta_nodes(tree, mutation_execution, dependence_graph);
			
			for(SymInstanceTreeNode muta_tree_node : muta_tree_nodes) {
				for(CirMutation infection : infections) {
					SymInstanceTreeNode infect_tree_node = muta_tree_node.
							new_child(infection.get_constraint(), infection.get_state_error());
					this.build_propagations_on(infect_tree_node, propagation_distance, dependence_graph);
				}
			}
		}
		
		return tree;
	}
	
	/* state evaluation methods on tree */
	/**
	 * perform static evaluation on states of tree_node and all its children recursively
	 * @param tree_node
	 * @throws Exception
	 */
	private void static_evaluate_from(SymInstanceTreeNode tree_node) throws Exception {
		/* local evaluation on tree_node (edge --> node) */
		Boolean edge_result;
		if(!tree_node.has_edge_status()) {
			edge_result = Boolean.TRUE;
		}
		else {
			edge_result = tree_node.get_tree().eval_on(tree_node.get_edge_status(), null).get_evaluation_result();
		}
		Boolean node_result;
		if(edge_result == null || edge_result.booleanValue()) {
			node_result = tree_node.get_tree().eval_on(tree_node.get_node_status(), null).get_evaluation_result();
		}
		else {
			node_result = Boolean.FALSE;
		}
		Boolean result = node_result;
		
		/* passing through this tree node, recursively solve */
		if(result == null || result.booleanValue()) {
			for(SymInstanceTreeNode child : tree_node.get_children()) {
				this.static_evaluate_from(child);
			}
		}
	}
	/**
	 * perform static evaluations on given tree
	 * @param tree
	 * @throws Exception
	 */
	protected void static_evaluations(SymInstanceTree tree) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else {
			this.static_evaluate_from(tree.get_root());
		}
	}
	/**
	 * update the states before mutation nodes using dynamic information
	 * @param tree
	 * @param test_path
	 * @throws Exception
	 */
	private void dynamic_prev_evaluations(SymInstanceTree tree, CStatePath test_path) throws Exception {
		/* collect all the nodes from root to mutation nodes */
		Collection<SymInstanceTreeNode> mutation_nodes = tree.get_mutation_nodes();
		Set<SymInstanceTreeNode> prev_nodes = new HashSet<SymInstanceTreeNode>();
		for(SymInstanceTreeNode mutation_node : mutation_nodes) {
			SymInstanceTreeNode tree_node = mutation_node;
			while(tree_node != null) {
				prev_nodes.add(tree_node);
				tree_node = tree_node.get_parent();
			}
		}
		
		/* divide the states to corresponding execution */
		Map<CirExecution, Collection<SymInstanceStatus>> exec_status = 
				new HashMap<CirExecution, Collection<SymInstanceStatus>>();
		SymInstanceStatus status; CirExecution execution;
		for(SymInstanceTreeNode prev_node : prev_nodes) {
			if(prev_node.has_edge_status()) {
				status = prev_node.get_edge_status();
				execution = status.get_execution();
				if(!exec_status.containsKey(execution)) {
					exec_status.put(execution, new ArrayList<SymInstanceStatus>());
				}
				exec_status.get(execution).add(status);
			}
			status = prev_node.get_node_status();
			execution = status.get_execution();
			if(!exec_status.containsKey(execution)) {
				exec_status.put(execution, new ArrayList<SymInstanceStatus>());
			}
			exec_status.get(execution).add(status);
		}
		
		/* perform dynamic evaluation on all prev-states */
		SymbolStateContexts contexts = new SymbolStateContexts();
		for(CStateNode state_node : test_path.get_nodes()) {
			contexts.accumulate(state_node);
			execution = state_node.get_execution();
			if(exec_status.containsKey(execution)) {
				for(SymInstanceStatus prev_status : exec_status.get(execution)) {
					tree.eval_on(prev_status, contexts);
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
	private void dynamic_post_evaluations(SymInstanceTree tree, CirExecution muta_execution, 
			Iterable<SymInstanceTreeNode> mutation_nodes, CStatePath test_path) throws Exception {
		/* collect nodes after reachable mutation nodes */
		Collection<SymInstanceTreeNode> post_nodes = new HashSet<SymInstanceTreeNode>();
		for(SymInstanceTreeNode mutation_node : mutation_nodes) {
			post_nodes.addAll(mutation_node.get_all_children());
			post_nodes.remove(mutation_node);
		}
		
		/* mapping from execution to corresponding state being updated */
		Map<CirExecution, Collection<SymInstanceStatus>> exec_status = 
				new HashMap<CirExecution, Collection<SymInstanceStatus>>();
		SymInstanceStatus status; CirExecution execution;
		for(SymInstanceTreeNode post_node : post_nodes) {
			if(post_node.has_edge_status()) {
				status = post_node.get_edge_status();
				execution = status.get_execution();
				if(!exec_status.containsKey(execution)) {
					exec_status.put(execution, new ArrayList<SymInstanceStatus>());
				}
				exec_status.get(execution).add(status);
			}
			status = post_node.get_node_status();
			execution = status.get_execution();
			if(!exec_status.containsKey(execution)) {
				exec_status.put(execution, new ArrayList<SymInstanceStatus>());
			}
			exec_status.get(execution).add(status);
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
				if(exec_status.containsKey(target)) {
					for(SymInstanceStatus post_status : exec_status.get(target)) {
						tree.eval_on(post_status, contexts);
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
	private void dynamic_post_evaluations(SymInstanceTree tree, CStatePath test_path) throws Exception {
		Collection<SymInstanceTreeNode> mutation_nodes = tree.get_mutation_nodes();
		Map<CirExecution, Collection<SymInstanceTreeNode>> execution_nodes = 
				new HashMap<CirExecution, Collection<SymInstanceTreeNode>>();
		for(SymInstanceTreeNode mutation_node : mutation_nodes) {
			if(mutation_node.is_acceptable()) {
				CirExecution execution = mutation_node.get_execution();
				if(!execution_nodes.containsKey(execution)) {
					execution_nodes.put(execution, new ArrayList<SymInstanceTreeNode>());
				}
				execution_nodes.get(execution).add(mutation_node);
			}
		}
		
		for(CirExecution execution : execution_nodes.keySet()) {
			this.dynamic_post_evaluations(tree, execution, execution_nodes.get(execution), test_path);
		}
	}
	/**
	 * perform dynamic evaluation to update states on given test path with information at runtime
	 * @param tree
	 * @param test_path
	 * @throws Exception
	 */
	protected void dynamic_evaluation(SymInstanceTree tree, CStatePath test_path) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(test_path == null)
			throw new IllegalArgumentException("Invalid test_path");
		else {
			this.dynamic_prev_evaluations(tree, test_path);
			this.dynamic_post_evaluations(tree, test_path);
		}
	}
	
	/* reachable path finding algorithm */
	/**
	 * collect paths reachable from the node
	 * @param node
	 * @param paths
	 */
	private void collect_reachable_paths(SymInstanceTreeNode node, Collection<List<SymInstanceTreeNode>> paths) {
		if(node.is_acceptable()) {
			if(node.is_leaf()) {
				paths.add(node.get_parents_path());
			}
			else {
				for(SymInstanceTreeNode child : node.get_children()) {
					this.collect_reachable_paths(child, paths);
				}
			}
		}
		else {
			paths.add(node.get_parents_path());
		}
	}
	/**
	 * @param tree
	 * @return
	 * @throws Exception
	 */
	protected Collection<List<SymInstanceTreeNode>> collect_reachable_paths(SymInstanceTree tree) {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else {
			Collection<List<SymInstanceTreeNode>> paths = new ArrayList<List<SymInstanceTreeNode>>();
			this.collect_reachable_paths(tree.get_root(), paths);
			return paths;
		}
	}
	
}
