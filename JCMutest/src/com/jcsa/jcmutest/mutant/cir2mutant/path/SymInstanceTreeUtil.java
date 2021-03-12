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
 * It is used to construct the symbolic instance tree for static template to describe the 
 * conditions defined in program context for detecting the target mutations.
 * 
 * @author yukimula
 *
 */
public class SymInstanceTreeUtil {
	
	/* singleton */
	private SymInstanceTreeUtil() { }
	public static final SymInstanceTreeUtil utils = new SymInstanceTreeUtil();
	
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
		
		if(tree_node.get_node_state().get_execution() != target) { 
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
		if(tree_node.get_node_state().is_state_error()) {
			SymStateError source_error = 
					(SymStateError) tree_node.get_node_state().get_source_instance();
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
		if(tree_node.get_node_state().is_state_error()) {
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
		SymStateValueError state_error = (SymStateValueError) tree_node.get_node_state().get_source_instance();
		
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
		SymStateError state_error = (SymStateError) tree_node.get_node_state().get_source_instance();
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
		
		if(tree_node.get_node_state().is_state_error()) {
			SymStateError state_error = (SymStateError) tree_node.get_node_state().get_source_instance();
			
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
	public SymInstanceTree build_sym_instance_tree(Mutant mutant, int propagation_distance, CDependGraph dependence_graph) throws Exception {
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
	
	/* static evaluation methods */
	/**
	 * reset all state for nodes following the tree_node
	 * @param tree_node
	 * @throws Exception
	 */
	private void reset_tree_on(SymInstanceTreeNode tree_node) throws Exception {
		if(!tree_node.is_root())
			tree_node.get_edge_state().reset();
		tree_node.get_node_state().reset();
		for(SymInstanceTreeNode child : tree_node.get_children()) {
			this.reset_tree_on(child);
		}
	}
	/**
	 * reset all the state on edges and nodes in each node in tree
	 * @param tree
	 * @throws Exception
	 */
	private void reset_tree(SymInstanceTree tree) throws Exception {
		this.reset_tree_on(tree.get_root());
	}
	/**
	 * evaluate the state from edge to node and return whether the evaluation passes through tree node
	 * @param tree_node
	 * @return
	 * @throws Exception
	 */
	private Boolean evaluate_tree_node(SymInstanceTreeNode tree_node, SymbolStateContexts contexts) throws Exception {
		Boolean edge_result;
		if(tree_node.is_root()) {
			edge_result = Boolean.TRUE;
		}
		else {
			edge_result = tree_node.get_edge_state().evaluate(
					tree_node.get_tree().get_cir_mutations(), contexts);
		}
		
		Boolean node_result;
		if(edge_result == null || edge_result.booleanValue()) {
			node_result = tree_node.get_node_state().evaluate(
					tree_node.get_tree().get_cir_mutations(), contexts);
		}
		else {
			node_result = Boolean.FALSE;
		}
		
		return node_result;
	}
	/**
	 * @param leaf_node
	 * @return path from root to leaf
	 * @throws Exception
	 */
	private List<SymInstanceTreeNode> get_path_to(SymInstanceTreeNode leaf) throws Exception {
		List<SymInstanceTreeNode> path = new ArrayList<SymInstanceTreeNode>();
		while(leaf != null) {
			path.add(leaf);
			leaf = leaf.get_parent();
		}
		for(int k = 0; k < path.size() / 2; k++) {
			SymInstanceTreeNode x = path.get(k);
			SymInstanceTreeNode y = path.get(path.size() - k - 1);
			path.set(k, y);
			path.set(path.size() - k - 1, x);
		}
		return path;
	}
	/**
	 * Perform static evaluation from tree_node to its children until failed
	 * @param tree_node
	 * @param paths the collection of paths including all reachable tree node among evaluations
	 * @throws Exception
	 */
	private void stat_evaluate_from(SymInstanceTreeNode tree_node, 
			Collection<List<SymInstanceTreeNode>> paths) throws Exception {
		Boolean result = this.evaluate_tree_node(tree_node, null);
		if(result == null || result.booleanValue()) {
			if(tree_node.is_leaf()) {
				paths.add(this.get_path_to(tree_node));
			}
			else {
				for(SymInstanceTreeNode child : tree_node.get_children()) {
					this.stat_evaluate_from(child, paths);
				}
			}
		}
		/* static evaluation fails at this node */
		else {
			paths.add(this.get_path_to(tree_node));
		}
	}
	/**
	 * @param tree
	 * @return set of paths from root to reachable nodes in tree using static evaluations
	 * @throws Exception
	 */
	public Collection<List<SymInstanceTreeNode>> stat_evaluations(SymInstanceTree tree) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else {
			this.reset_tree(tree);
			Collection<List<SymInstanceTreeNode>> paths = new ArrayList<List<SymInstanceTreeNode>>();
			this.stat_evaluate_from(tree.get_root(), paths);
			return paths;
		}
	}
	
	/* dynamic evaluation methods */
	/**
	 * collect the mutation nodes under the tree_node where the mutant is seeded
	 * @param tree_node
	 * @param mutation_nodes
	 * @throws Exception
	 */
	private void collect_mutation_tree_nodes_in(SymInstanceTreeNode tree_node, 
			Set<SymInstanceTreeNode> mutation_nodes) throws Exception {
		if(tree_node.get_node_state().is_state_error()) {
			mutation_nodes.add(tree_node.get_parent());
		}
		else {
			for(SymInstanceTreeNode child : tree_node.get_children()) {
				this.collect_mutation_tree_nodes_in(child, mutation_nodes);
			}
		}
	}
	/**
	 * @param tree
	 * @return the set of tree node referring to the coverage of mutation points
	 * @throws Exception
	 */
	private Set<SymInstanceTreeNode> collect_mutation_tree_nodes(SymInstanceTree tree) throws Exception {
		Set<SymInstanceTreeNode> mutation_nodes = new HashSet<SymInstanceTreeNode>();
		this.collect_mutation_tree_nodes_in(tree.get_root(), mutation_nodes);
		return mutation_nodes;
	}
	/**
	 * @param mutation_node
	 * @param test_path
	 * @return evaluate on the previous part and return path from root to mutation node (updating
	 * 		   all the state among the edges)
	 * @throws Exception
	 */
	private void dyna_prev_evaluate_until(Set<SymInstanceTreeNode> mutation_nodes, 
			SymInstanceTree tree, CStatePath test_path) throws Exception {
		/* fetch all the states in tree nodes until the mutation nodes */
		CirExecution execution; SymInstanceState state;
		CirMutations cir_mutations = tree.get_cir_mutations();
		Map<CirExecution, Collection<SymInstanceState>> execution_states = 
				new HashMap<CirExecution, Collection<SymInstanceState>>();
		for(SymInstanceTreeNode mutation_node : mutation_nodes) {
			SymInstanceTreeNode tree_node = mutation_node;
			while(tree_node != null) {
				if(!tree_node.is_root()) {
					state = tree_node.get_edge_state();
					execution = state.get_execution();
					if(!execution_states.containsKey(execution)) {
						execution_states.put(execution, new ArrayList<SymInstanceState>());
					}
					execution_states.get(execution).add(state);
				}
				
				state = tree_node.get_node_state();
				execution = state.get_execution();
				if(!execution_states.containsKey(execution)) {
					execution_states.put(execution, new ArrayList<SymInstanceState>());
				}
				execution_states.get(execution).add(state);
				
				tree_node = tree_node.get_parent();
			}
		}
		
		/* dynamic evaluations performed on every point */
		SymbolStateContexts contexts = new SymbolStateContexts();
		for(CStateNode state_node : test_path.get_nodes()) {
			contexts.accumulate(state_node);
			
			CirExecution target = state_node.get_execution();
			if(execution_states.containsKey(target)) {
				for(SymInstanceState target_state : execution_states.get(target)) {
					target_state.evaluate(cir_mutations, contexts);
				}
			}
		}
	}
	/**
	 * update the state before mutation locations until first infection evaluate.
	 * @param tree
	 * @param test_path
	 * @throws Exception
	 */
	private void dyna_prev_evaluations_on(SymInstanceTree tree, CStatePath test_path) throws Exception {
		Set<SymInstanceTreeNode> muta_nodes = this.collect_mutation_tree_nodes(tree);
		this.dyna_prev_evaluate_until(muta_nodes, tree, test_path);
	}
	/**
	 * collect all the states after the tree nodes (including tree_node itself)
	 * @param tree
	 * @param states
	 * @throws Exception
	 */
	private void collect_tree_states_after(SymInstanceTreeNode tree_node, Set<SymInstanceState> states) throws Exception {
		if(!tree_node.is_root()) {
			states.add(tree_node.get_edge_state());
		}
		states.add(tree_node.get_node_state());
		for(SymInstanceTreeNode child : tree_node.get_children()) {
			this.collect_tree_states_after(child, states);
		}
	}
	/**
	 * update all the states following mutation nodes
	 * @param execution
	 * @param mutation_nodes
	 * @param test_path
	 * @throws Exception
	 */
	private void dyna_post_evaluate_from(CirExecution execution, 
			Collection<SymInstanceTreeNode> mutation_nodes, 
			SymInstanceTree tree,
			CStatePath test_path) throws Exception {
		/* collect mapping from execution to the leafs it propagate to */
		Set<SymInstanceState> post_states = new HashSet<SymInstanceState>();
		for(SymInstanceTreeNode mutation_node : mutation_nodes) {
			this.collect_tree_states_after(mutation_node, post_states);
		}
		Map<CirExecution, Collection<SymInstanceState>> execution_states = 
				new HashMap<CirExecution, Collection<SymInstanceState>>();
		for(SymInstanceState post_state : post_states) {
			if(!execution_states.containsKey(post_state.get_execution())) {
				execution_states.put(post_state.get_execution(), new ArrayList<SymInstanceState>());
			}
			execution_states.get(post_state.get_execution()).add(post_state);
		}
		
		/* dynamic evaluation until the first occurrence of execution */
		SymbolStateContexts contexts = new SymbolStateContexts();
		boolean reached = false;
		CirMutations cir_mutations = tree.get_cir_mutations();
		for(CStateNode state_node : test_path.get_nodes()) {
			/* update contexts and reached tag */
			contexts.accumulate(state_node);
			if(state_node.get_execution() == execution) { reached = true; }
			
			/* since mutation nodes is reached */
			if(reached) {
				CirExecution target = state_node.get_execution();
				if(execution_states.containsKey(target)) {
					for(SymInstanceState post_state : execution_states.get(target)) {
						post_state.evaluate(cir_mutations, contexts);
					}
				}
			}
		}
	}
	/**
	 * @param tree
	 * @param test_path
	 * @throws Exception
	 */
	private void dyna_post_evaluations_on(SymInstanceTree tree, CStatePath test_path) throws Exception {
		Set<SymInstanceTreeNode> mutation_nodes = this.collect_mutation_tree_nodes(tree);
		Map<CirExecution, Collection<SymInstanceTreeNode>> execution_nodes = 
				new HashMap<CirExecution, Collection<SymInstanceTreeNode>>();
		for(SymInstanceTreeNode mutation_node : mutation_nodes) {
			CirExecution execution = mutation_node.get_execution();
			if(!execution_nodes.containsKey(execution)) {
				execution_nodes.put(execution, new ArrayList<SymInstanceTreeNode>());
			}
			execution_nodes.get(execution).add(mutation_node);
		}
		
		for(CirExecution execution : execution_nodes.keySet()) {
			this.dyna_post_evaluate_from(execution, execution_nodes.get(execution), tree, test_path);
		}
	}
	/**
	 * collect the reachable parts of paths from root to reachable nodes in tree
	 * @param tree_node
	 * @param paths
	 * @throws Exception
	 */
	private void collect_reachable_paths(SymInstanceTreeNode tree_node, Collection<List<SymInstanceTreeNode>> paths) throws Exception {
		boolean pass_edge;
		if(!tree_node.is_root()) {
			SymInstanceState edge_state = tree_node.get_edge_state();
			if(edge_state.is_acceptable()) {
				pass_edge = true;
			}
			else {
				pass_edge = false;
			}
		}
		else {
			pass_edge = true;
		}
		
		boolean pass_node;
		if(pass_edge) {
			SymInstanceState node_state = tree_node.get_node_state();
			if(node_state.is_acceptable()) {
				pass_node = true;
			}
			else {
				pass_node = false;
			}
		}
		else {
			pass_node = false;
		}
		
		if(pass_node) {
			if(tree_node.is_leaf()) {
				paths.add(this.get_path_to(tree_node));
			}
			else {
				for(SymInstanceTreeNode child : tree_node.get_children()) {
					this.collect_reachable_paths(child, paths);
				}
			}
		}
		else {
			paths.add(this.get_path_to(tree_node));
		}
	}
	/**
	 * dynamic evaluation on 
	 * @param tree
	 * @param test_path
	 * @return reachable path in tree collected
	 * @throws Exception
	 */
	public Collection<List<SymInstanceTreeNode>> dyna_evaluations(SymInstanceTree tree, CStatePath test_path) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(test_path == null)
			throw new IllegalArgumentException("Invalid test_path");
		else {
			this.reset_tree(tree);
			this.dyna_prev_evaluations_on(tree, test_path);
			this.dyna_post_evaluations_on(tree, test_path);
			
			Collection<List<SymInstanceTreeNode>> paths = new ArrayList<List<SymInstanceTreeNode>>();
			this.collect_reachable_paths(tree.get_root(), paths);
			return paths;
		}
	}
	
}
