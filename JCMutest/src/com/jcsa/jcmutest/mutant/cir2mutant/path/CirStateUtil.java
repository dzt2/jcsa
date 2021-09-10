package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirCoverCount;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirDiferError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirFlowsError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirReferError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirTrapsError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirValueError;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It is used to construct the execution state tree using static approach.
 * 
 * @author yukimula
 *
 */
final class CirStateUtil {
	
	/* singleton */	/** constructor **/	private CirStateUtil(){}
	private static final CirStateUtil util = new CirStateUtil();
	private SymbolExpression evaluate(SymbolExpression expression, SymbolProcess context) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			try {
				return expression.evaluate(context);
			}
			catch(ArithmeticException ex) {
				return CirValueScope.expt_value;
			}
		}
	}
	
	/* condition collection approaches */
	/**
	 * It recursively collects the operands in the conjunctive expression
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void get_conditions_in_conjunct(SymbolExpression expression, 
			Collection<SymbolExpression> conditions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				/* true in conjunctive should be ignored */
			}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(op == COperator.logic_and) {
				this.get_conditions_in_conjunct(loperand, conditions);
				this.get_conditions_in_conjunct(roperand, conditions);
			}
			else {
				conditions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * It recursively collects the operands in the disjunctive expression
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void get_conditions_in_disjunct(SymbolExpression expression,
			Collection<SymbolExpression> conditions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
			else {
				/* false in disjunctive should be ignored */
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(op == COperator.logic_or) {
				this.get_conditions_in_disjunct(loperand, conditions);
				this.get_conditions_in_disjunct(roperand, conditions);
			}
			else {
				conditions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * It generates the subsumed conditions (include) from the input
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void get_conditions_by_subsumed(SymbolExpression expression,
			Collection<SymbolExpression> conditions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				/* none of conditions is subsumed by true  */
			}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			
			conditions.add(SymbolFactory.sym_condition(expression, true));
			switch(op) {
			case greater_tn:	/** (x > y)	 	==> (x >= y; x != y) **/
				conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				conditions.add(SymbolFactory.not_equals(loperand, roperand));
				break;
			case smaller_tn:	/** (x < y) 	==> (x <= y; x != y) **/
				conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
				conditions.add(SymbolFactory.not_equals(loperand, roperand));
				break;
			case equal_with:	/** (x == y) 	==> (x <= y; x >= y) **/
				conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
				break;
			default:			/** otherwise, none of subsumed ones **/
				break;
			}
		}
		else {
			conditions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * @param expression
	 * @return It collects the basic logical formula required in the input constraint.
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_conditions(SymbolExpression expression) throws Exception {
		/* 1. collect the conditions in the conjunctive */
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		expression = this.evaluate(expression, null);
		this.get_conditions_in_conjunct(expression, conditions);
		
		/* 2. generate the subsumed constraints from it */
		Set<SymbolExpression> constraints = new HashSet<SymbolExpression>();
		for(SymbolExpression condition : conditions) {
			this.get_conditions_by_subsumed(condition, constraints);
		}
		
		/* 3. return all conditions needed */	return constraints;
	}
	/**
	 * @param expression
	 * @return It divides the sub-conditions in the disjunctive expression.
	 * @throws Exception
	 */
	private Collection<SymbolExpression> div_conditions(SymbolExpression expression) throws Exception {
		expression = this.evaluate(expression, null);
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.get_conditions_in_disjunct(expression, conditions);
		return conditions;
	}
	
	/* flow-based checkpoint path find */
	/**
	 * recursively collect the symbolic references under the node
	 * @param node
	 * @param references to preserve the output references being collected
	 */
	private void get_symbol_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		if(node.is_reference()) references.add((SymbolExpression) node);
		for(SymbolNode child : node.get_children()) {
			this.get_symbol_references_in(child, references);
		}
	}
	/**
	 * @param node
	 * @param references
	 * @return whether there is reference used in the node
	 */
	private boolean has_symbol_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		if(references.isEmpty()) {
			return false;
		}
		else if(references.contains(node)) {
			return true;
		}
		else {
			for(SymbolNode child : node.get_children()) {
				if(this.has_symbol_references_in(child, references)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any references is limited in the execution (IF|CASE|ASSIGN)
	 */
	private boolean has_symbol_references_in(CirExecution execution,  Collection<SymbolExpression> references) throws Exception {
		if(references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirAssignStatement) {
				return references.contains(SymbolFactory.
						sym_expression(((CirAssignStatement) statement).get_lvalue()));
			}
			else if(statement instanceof CirIfStatement) {
				return this.has_symbol_references_in(SymbolFactory.sym_expression(
						((CirIfStatement) statement).get_condition()), references);
			}
			else if(statement instanceof CirCaseStatement) {
				return this.has_symbol_references_in(SymbolFactory.sym_expression(
						((CirCaseStatement) statement).get_condition()), references);
			}
			else {
				return false;
			}
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @return find the previous check-point where the expression should be evaluated
	 * @throws Exception
	 */
	private CirExecution find_prior_checkpoint(CirExecution execution, SymbolExpression expression) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					switch(edge.get_type()) {
					case true_flow:	return edge.get_target();
					case fals_flow:	return edge.get_target();
					default:		break;
					}
				}
				return prev_path.get_source();
			}
			else {
				return execution.get_graph().get_entry();
			}
		}
		else {
			CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
			Collection<SymbolExpression> references = new HashSet<SymbolExpression>();
			this.get_symbol_references_in(expression, references);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(this.has_symbol_references_in(edge.get_source(), references)) {
					return edge.get_target();
				}
			}
			return prev_path.get_source();
		}
	}
	
	/* reachability execution path */
	/**
	 * @param flow
	 * @return the attribute representing the coverage of the specified flow
	 * @throws Exception
	 */
	private CirAttribute get_flow_attribute(CirExecutionFlow flow) throws Exception {
		if(flow == null) {
			throw new IllegalArgumentException("Invalid flow as null");
		}
		else if(flow.get_type() == CirExecutionFlowType.true_flow) {
			CirStatement statement = flow.get_source().get_statement();
			CirExpression expression;
			if(statement instanceof CirIfStatement) {
				expression = ((CirIfStatement) statement).get_condition();
			}
			else {
				expression = ((CirCaseStatement) statement).get_condition();
			}
			return CirAttribute.new_constraint(flow.get_source(), expression, true);
		}
		else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
			CirStatement statement = flow.get_source().get_statement();
			CirExpression expression;
			if(statement instanceof CirIfStatement) {
				expression = ((CirIfStatement) statement).get_condition();
			}
			else {
				expression = ((CirCaseStatement) statement).get_condition();
			}
			return CirAttribute.new_constraint(flow.get_source(), expression, false);
		}
		else if(flow.get_type() == CirExecutionFlowType.call_flow) {
			return CirAttribute.new_cover_count(flow.get_source(), 1);
		}
		else if(flow.get_type() == CirExecutionFlowType.retr_flow) {
			return CirAttribute.new_cover_count(flow.get_target(), 1);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + flow.get_type());
		}
	}
	/**
	 * collect the execution flows in the path to the given sequences 
	 * @param path
	 * @param flows
	 */
	private void collect_execution_flows_in(CirExecutionPath path, List<CirExecutionFlow> flows) {
		for(CirExecutionEdge edge : path.get_edges()) {
			/* capture execution flow in edge */
			CirExecutionFlow flow;
			switch(edge.get_type()) {
			case true_flow:
			case fals_flow:
			case call_flow:
			case retr_flow:	flow = edge.get_flow();	break;
			default:		flow = null;			break;
			}
			
			/* append the flow into simple path */
			if(flow != null && !flows.contains(flow)) {
				flows.add(flow);
			}
		}
	}
	/**
	 * generate the execution flows in sequence from function entry to the target using decidable path analysis
	 * @param target
	 * @param flows
	 * @throws Exception
	 */
	private void generate_execution_flows(CirExecution target, List<CirExecutionFlow> flows) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(flows == null) {
			throw new IllegalArgumentException("Invalid flows: null");
		}
		else {
			CirExecution source = target.get_graph().get_entry();
			CirExecutionPath path = new CirExecutionPath(source);
			CirExecutionPathFinder.finder.vf_extend(path, target);
			this.collect_execution_flows_in(path, flows); return;
		}
	}
	/**
	 * generate the execution flows in sequence from program entry to the target using dependence path analysis
	 * @param target
	 * @param dependence_graph
	 * @param flows
	 * @throws Exception
	 */
	private void generate_execution_flows(CirExecution target, 
			CDependGraph dependence_graph, List<CirExecutionFlow> flows) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(dependence_graph == null) {
			this.generate_execution_flows(target, flows);
		}
		else {
			CirExecutionPath path = CirExecutionPathFinder.
					finder.dependence_path(dependence_graph, target);
			this.collect_execution_flows_in(path, flows);
		}
	}
	/**
	 * generate the execution flows in sequence from program entry to the target using dynamic state analysis
	 * @param target
	 * @param state_path
	 * @param flows
	 * @throws Exception
	 */
	private void generate_execution_flows(CirExecution target, 
			CStatePath state_path, List<CirExecutionFlow> flows) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(state_path == null || state_path.size() == 0) {
			this.generate_execution_flows(target, flows);
		}
		else {
			CirExecution source = state_path.get_node(0).get_execution();
			CirExecutionPath path = new CirExecutionPath(source);
			for(CStateNode state_node : state_path.get_nodes()) {
				CirExecutionPathFinder.finder.vf_extend(path, state_node.get_execution());
				this.collect_execution_flows_in(path, flows);
				if(state_node.get_execution() == target) {
					break;
				}
				else {
					path = new CirExecutionPath(state_node.get_execution());
				}
			}
			CirExecutionPathFinder.finder.vf_extend(path, target);
			this.collect_execution_flows_in(path, flows);
		}
	}
	/**
	 * @param tree_node
	 * @param flow
	 * @return It constructs the path for reaching the target node and return the created child from the input
	 * @throws Exception
	 */
	private CirStateNode construct_pred_node(CirStateNode tree_node, CirExecutionFlow flow) throws Exception {
		CirStateNode child = tree_node.new_child(CirStateType.pre_condition, flow.get_target());
		this.generate_conditions_in(child, this.get_flow_attribute(flow)); return child;
	}
	/**
	 * @param tree
	 * @param target
	 * @param context	null | CDependGraph | CStatePath
	 * @return It constructs the previous path for reaching the target under the given context
	 * @throws Exception
	 */
	private CirStateNode construct_pred_tree(CirStateTree tree, CirExecution target, Object context) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			/* 1. generate the execution path to target in the context */
			List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
			if(context instanceof CDependGraph) {
				this.generate_execution_flows(target, (CDependGraph) context, flows);
			}
			else if(context instanceof CStatePath) {
				this.generate_execution_flows(target, (CStatePath) context, flows);
			}
			else {
				this.generate_execution_flows(target, flows);
			}
			
			/* 2. it recursively constructs the reaching previous path */
			CirStateNode tree_node = tree.get_root();
			for(CirExecutionFlow flow : flows) {
				tree_node = this.construct_pred_node(tree_node, flow);
			}
			
			/* 3. it constructs the mid_condition node for reaching target */
			return tree_node.new_child(CirStateType.mid_condition, target);
		}
	}
	
	/* state data block construction */
	/**
	 * It recursively collects the expressions under the location (included)
	 * @param location
	 * @param locations
	 */
	private void get_cir_expressions_in(CirNode location, Collection<CirExpression> expressions) {
		if(location instanceof CirExpression) {
			expressions.add((CirExpression) location);
		}
		for(CirNode child : location.get_children()) {
			this.get_cir_expressions_in(child, expressions);
		}
	}
	/**
	 * @param statement
	 * @return It collects the expression used for initializing the data store-value in state
	 * @throws Exception
	 */
	private Iterable<CirExpression> get_cir_expressions(CirStatement statement) throws Exception {
		Set<CirExpression> expressions = new HashSet<CirExpression>();
		if(statement instanceof CirAssignStatement) {
			this.get_cir_expressions_in(((CirAssignStatement) statement).get_rvalue(), expressions);
			expressions.add(((CirAssignStatement) statement).get_lvalue());
		}
		else if(statement instanceof CirIfStatement) {
			this.get_cir_expressions_in(((CirIfStatement) statement).get_condition(), expressions);
		}
		else if(statement instanceof CirCaseStatement) {
			this.get_cir_expressions_in(((CirCaseStatement) statement).get_condition(), expressions);
		}
		else if(statement instanceof CirCallStatement) {
			this.get_cir_expressions_in(((CirCallStatement) statement).get_arguments(), expressions);
			expressions.add(((CirCallStatement) statement).get_function());
		}
		else { /* none of available expressions used */ }
		return expressions;
	}
	/**
	 * It generates the path conditions from input constraint annotated on the state node
	 * @param tree_node
	 * @param constraint
	 * @throws Exception
	 */
	private void generate_conditions_in(CirStateNode tree_node, CirAttribute constraint) throws Exception {
		if(constraint instanceof CirCoverCount) {	/* coverage */
			CirExecution execution = constraint.get_execution();
			int execution_times = ((CirCoverCount) constraint).get_coverage_count();
			Set<Integer> times = new HashSet<Integer>();
			
			for(int k = 1; k <= execution_times; k = k * 2) {
				times.add(Integer.valueOf(k));
			}
			times.add(Integer.valueOf(execution_times));
			
			for(Integer time : times) {
				tree_node.get_data().add(CirStoreValue.new_cond(execution, time));
			}
		}
		else if(constraint instanceof CirConstraint) {	/* asserted */
			CirExecution execution = constraint.get_execution(), check_point;
			SymbolExpression condition = ((CirConstraint) constraint).get_condition();
			Collection<SymbolExpression> sub_conditions = this.get_conditions(condition);
			
			for(SymbolExpression sub_condition : sub_conditions) {
				check_point = this.find_prior_checkpoint(execution, sub_condition);
				tree_node.get_data().add(CirStoreValue.new_cond(check_point, sub_condition, true));
			}
			
			if(sub_conditions.isEmpty()) {
				SymbolExpression sub_condition = SymbolFactory.sym_constant(Boolean.TRUE);
				check_point = this.find_prior_checkpoint(execution, sub_condition);
				tree_node.get_data().add(CirStoreValue.new_cond(check_point, 1));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid flow_attribute");
		}
	}
	/**
	 * It generates the data store-value(s) in the execution state using original value
	 * @param tree_node
	 * @throws Exception
	 */
	private void generate_data_values_in(CirStateNode tree_node) throws Exception {
		CirStatement statement = tree_node.get_execution().get_statement();
		for(CirExpression expression : this.get_cir_expressions(statement)) {
			if(CirMutations.is_assigned(expression)) {
				CirAssignStatement assignment = (CirAssignStatement) statement;
				tree_node.get_data().add(CirStoreValue.new_data(expression, assignment.get_rvalue()));
			}
			else {
				tree_node.get_data().add(CirStoreValue.new_data(expression, expression));
			}
		}
	}
	/**
	 * It generates the statements to be executed under the tree node.
	 * @param tree_node
	 * @param next_flow	
	 * @throws Exception
	 */
	private void generate_stmt_values_in(CirStateNode tree_node, CirExecutionFlow next_flow) throws Exception {
		if(next_flow != null) {
			CirExecutionPath path = new CirExecutionPath(next_flow.get_target());
			CirExecutionPathFinder.finder.df_extend(path);
			
			Set<CirExecution> next_executions = new HashSet<CirExecution>();
			next_executions.add(next_flow.get_target());
			for(CirExecutionEdge edge : path.get_edges()) {
				next_executions.add(edge.get_target());
			}
			
			for(CirExecution next_execution : next_executions) {
				tree_node.get_data().add(CirStoreValue.new_stmt(next_execution, true));
			}
		}
	}
	/**
	 * @param tree_node
	 * @param init_error
	 * @throws Exception
	 */
	private void generate_muta_values_in_block_error(CirStateNode pred_node, 
			CirStateNode post_node, CirBlockError init_error) throws Exception {
		CirExecution execution = init_error.get_execution();
		if(execution.get_statement() instanceof CirTagStatement) {
			return;
		}
		else if(init_error.is_executed()) {
			pred_node.get_data().add(CirStoreValue.new_stmt(execution, false));
			post_node.get_data().add(CirStoreValue.new_stmt(execution, true));
		}
		else {
			pred_node.get_data().add(CirStoreValue.new_stmt(execution, true));
			post_node.get_data().add(CirStoreValue.new_stmt(execution, false));
		}
	}
	/**
	 * @param pred_node
	 * @param post_node
	 * @param init_error
	 * @throws Exception
	 */
	private void generate_muta_values_in_flows_error(CirStateNode pred_node,
			CirStateNode post_node, CirFlowsError init_error) throws Exception {
		this.generate_stmt_values_in(pred_node, init_error.get_original_flow());
		this.generate_stmt_values_in(post_node, init_error.get_mutation_flow());
	}
	/**
	 * @param pred_node
	 * @param post_node
	 * @param init_error
	 * @throws Exception
	 */
	private void generate_muta_values_in_traps_error(CirStateNode pred_node,
			CirStateNode post_node, CirTrapsError init_error) throws Exception {
		post_node.get_data().add(CirStoreValue.new_trap(init_error.get_execution()));
	}
	/**
	 * @param pred_node
	 * @param post_node
	 * @param init_error
	 * @throws Exception
	 */
	private void generate_muta_values_in_difer_error(CirStateNode pred_node,
			CirStateNode post_node, CirDiferError init_error) throws Exception {
		CirExpression orig_value = init_error.get_orig_expression();
		SymbolExpression muta_value = init_error.get_muta_expression();
		post_node.get_data().add(CirStoreValue.new_data(orig_value, muta_value));
		post_node.get_data().add(CirStoreValue.new_trap(init_error.get_execution()));
	}
	/**
	 * @param pred_node
	 * @param post_node
	 * @param init_error
	 * @throws Exception
	 */
	private void generate_muta_values_in_state_error(CirStateNode pred_node,
			CirStateNode post_node, CirStateError init_error) throws Exception {
		CirExpression orig_value = init_error.get_orig_expression();
		SymbolExpression muta_value = init_error.get_muta_expression();
		post_node.get_data().add(CirStoreValue.new_data(orig_value, muta_value));
	}
	/**
	 * @param store_unit
	 * @param muta_value
	 * @return [next store unit to propagate, the value to assign next value in propagation]
	 * @throws Exception
	 */
	private Object[] find_next_store_value(CirNode store_unit, SymbolExpression muta_value, boolean is_reference) throws Exception {
		/* declarations */
		CirNode parent = store_unit.get_parent();
		CirExpression next_store; SymbolExpression next_value;
		
		/* syntax-directed translation */
		if(parent instanceof CirDeferExpression) {
			next_store = (CirDeferExpression) parent;
			next_value = SymbolFactory.dereference(muta_value);
		}
		else if(parent instanceof CirFieldExpression) {
			String field = ((CirFieldExpression) parent).get_field().get_name();
			next_store = (CirFieldExpression) parent;
			next_value = SymbolFactory.field_expression(muta_value, field);
		}
		else if(parent instanceof CirAddressExpression) {
			if(is_reference) {
				next_store = (CirAddressExpression) parent;
				next_value = SymbolFactory.address_of(muta_value);
			}
			else {
				next_store = null;
				next_value = null;
			}
		}
		else if(parent instanceof CirCastExpression) {
			CType cast_type = ((CirCastExpression) parent).get_type().get_typename();
			next_store = (CirCastExpression) parent;
			next_value = SymbolFactory.cast_expression(cast_type, muta_value);
		}
		else if(parent instanceof CirWaitExpression) {
			CirExecution wait_execution = parent.execution_of();
			CirWaitAssignStatement wait_statement = (CirWaitAssignStatement) wait_execution.get_statement();
			CirExecution call_execution = wait_execution.get_graph().get_execution(wait_execution.get_id() - 1);
			CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
			CirArgumentList alist = call_statement.get_arguments();
			
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			
			next_store = wait_statement.get_lvalue();
			next_value = SymbolFactory.call_expression(muta_value, arguments);
		}
		else if(parent instanceof CirArgumentList) {
			CirCallStatement call_statement = (CirCallStatement) parent.get_parent();
			CirExecution call_execution = call_statement.execution_of();
			CirExecution wait_execution = call_execution.get_graph().get_execution(call_execution.get_id() + 1);
			CirWaitAssignStatement wait_statement = (CirWaitAssignStatement) wait_execution.get_statement();
			
			CirArgumentList alist = call_statement.get_arguments();
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				if(alist.get_argument(k) == store_unit) {
					arguments.add(muta_value);
				}
				else {
					arguments.add(alist.get_argument(k));
				}
			}
			
			next_store = wait_statement.get_lvalue();
			next_value = SymbolFactory.call_expression(call_statement.get_function(), arguments);
		}
		else if(parent instanceof CirInitializerBody) {
			CirInitializerBody ilist = (CirInitializerBody) parent;
			List<Object> elements = new ArrayList<Object>();
			for(int k = 0; k < ilist.number_of_elements(); k++) {
				if(ilist.get_element(k) == store_unit) {
					elements.add(muta_value);
				}
				else {
					elements.add(ilist.get_element(k));
				}
			}
			
			next_store = (CirInitializerBody) parent;
			next_value = SymbolFactory.initializer_list(elements);
		}
		else if(parent instanceof CirComputeExpression) {
			COperator operator = ((CirComputeExpression) parent).get_operator();
			CirComputeExpression expression = (CirComputeExpression) parent;
			next_store = expression; CType data_type = expression.get_data_type();
			
			switch(operator) {
			case negative:	
				next_value = SymbolFactory.arith_neg(muta_value); break;
			case bit_not:
				next_value = SymbolFactory.bitws_rsv(muta_value); break;
			case logic_not:
				next_value = SymbolFactory.logic_not(muta_value); break;
			case arith_add:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.arith_add(data_type, muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.arith_add(data_type, expression.get_operand(0), muta_value);
				}
				break;
			case arith_sub:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.arith_sub(data_type, muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.arith_sub(data_type, expression.get_operand(0), muta_value);
				}
				break;
			case arith_mul:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.arith_mul(data_type, muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.arith_mul(data_type, expression.get_operand(0), muta_value);
				}
				break;
			case arith_div:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.arith_div(data_type, muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.arith_div(data_type, expression.get_operand(0), muta_value);
				}
				break;
			case arith_mod:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.arith_mod(data_type, muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.arith_mod(data_type, expression.get_operand(0), muta_value);
				}
				break;
			case bit_and:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.bitws_and(data_type, muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.bitws_and(data_type, expression.get_operand(0), muta_value);
				}
				break;
			case bit_or:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.bitws_ior(data_type, muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.bitws_ior(data_type, expression.get_operand(0), muta_value);
				}
				break;
			case bit_xor:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.bitws_xor(data_type, muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.bitws_xor(data_type, expression.get_operand(0), muta_value);
				}
				break;
			case left_shift:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.bitws_lsh(data_type, muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.bitws_lsh(data_type, expression.get_operand(0), muta_value);
				}
				break;
			case righ_shift:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.bitws_rsh(data_type, muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.bitws_rsh(data_type, expression.get_operand(0), muta_value);
				}
				break;
			case logic_and:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.logic_and(muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.logic_and(expression.get_operand(0), muta_value);
				}
				break;
			case logic_or:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.logic_ior(muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.logic_ior(expression.get_operand(0), muta_value);
				}
				break;
			case greater_tn:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.greater_tn(muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.greater_tn(expression.get_operand(0), muta_value);
				}
				break;
			case greater_eq:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.greater_eq(muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.greater_eq(expression.get_operand(0), muta_value);
				}
				break;
			case smaller_tn:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.smaller_tn(muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.smaller_tn(expression.get_operand(0), muta_value);
				}
				break;
			case smaller_eq:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.smaller_eq(muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.smaller_eq(expression.get_operand(0), muta_value);
				}
				break;
			case equal_with:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.equal_with(muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.equal_with(expression.get_operand(0), muta_value);
				}
				break;
			case not_equals:
				if(expression.get_operand(0) == store_unit) {
					next_value = SymbolFactory.not_equals(muta_value, expression.get_operand(1));
				}
				else {
					next_value = SymbolFactory.not_equals(expression.get_operand(0), muta_value);
				}
				break;
			default:	throw new IllegalArgumentException("Unsupport: " + parent);
			}
		}
		else if(parent instanceof CirIfStatement) {
			next_store = null; next_value = null;
		}
		else if(parent instanceof CirCaseStatement) {
			next_store = null; next_value = null;
		}
		else if(parent instanceof CirAssignStatement) {
			if(((CirAssignStatement) parent).get_rvalue() == store_unit) {
				next_store = ((CirAssignStatement) parent).get_lvalue();
				next_value = muta_value;
			}
			else {
				next_store = null; next_value = null;
			}
		}
		else {
			next_store = null; next_value = null;
		}
		
		/* return the next location results */
		if(next_store == null) {
			return null;
		}
		else {
			return new Object[] { next_store, next_value };
		}
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return it captures the data propagation from original to mutated value in hierarchical way.
	 * @throws Exception
	 */
	private Map<CirExpression, SymbolExpression> capture_muta_store_values(
			CirExpression orig_expression, SymbolExpression muta_expression,
			boolean is_reference) throws Exception {
		Map<CirExpression, SymbolExpression> results = new HashMap<CirExpression, SymbolExpression>();
		while(true) {
			results.put(orig_expression, muta_expression);
			Object[] muta_store_value = this.find_next_store_value(
					orig_expression, muta_expression, is_reference);
			if(muta_store_value == null) {
				break;
			}
			else {
				orig_expression = (CirExpression) muta_store_value[0];
				muta_expression = (SymbolExpression) muta_store_value[1];
			}
		}
		return results;
	}
	/**
	 * @param pred_node
	 * @param post_node
	 * @param init_error
	 * @throws Exception
	 */
	private void generate_muta_values_in_value_error(CirStateNode pred_node,
			CirStateNode post_node, CirValueError init_error) throws Exception {
		Map<CirExpression, SymbolExpression> results = this.capture_muta_store_values(
				init_error.get_orig_expression(), init_error.get_muta_expression(), false);
		for(CirExpression orig_expression : results.keySet()) {
			SymbolExpression muta_expression = results.get(orig_expression);
			pred_node.get_data().add(CirStoreValue.new_data(orig_expression, orig_expression));
			post_node.get_data().add(CirStoreValue.new_data(orig_expression, muta_expression));
		}
	}
	/**
	 * @param pred_node
	 * @param post_node
	 * @param init_error
	 * @throws Exception
	 */
	private void generate_muta_values_in_refer_error(CirStateNode pred_node,
			CirStateNode post_node, CirReferError init_error) throws Exception {
		Map<CirExpression, SymbolExpression> results = this.capture_muta_store_values(
				init_error.get_orig_expression(), init_error.get_muta_expression(), true);
		for(CirExpression orig_expression : results.keySet()) {
			SymbolExpression muta_expression = results.get(orig_expression);
			pred_node.get_data().add(CirStoreValue.new_data(orig_expression, orig_expression));
			post_node.get_data().add(CirStoreValue.new_data(orig_expression, muta_expression));
		}
	}
	/**
	 * @param pred_node
	 * @param post_node
	 * @param init_error
	 * @throws Exception
	 */
	private void generate_muta_values_in(CirStateNode pred_node, CirStateNode post_node, CirAttribute init_error) throws Exception {
		if(init_error == null) {
			throw new IllegalArgumentException("Invalid: " + init_error);
		}
		else if(init_error instanceof CirBlockError) {
			this.generate_muta_values_in_block_error(pred_node, post_node, (CirBlockError) init_error);
		}
		else if(init_error instanceof CirFlowsError) {
			this.generate_muta_values_in_flows_error(pred_node, post_node, (CirFlowsError) init_error);
		}
		else if(init_error instanceof CirTrapsError) {
			this.generate_muta_values_in_traps_error(pred_node, post_node, (CirTrapsError) init_error);
		}
		else if(init_error instanceof CirDiferError) {
			this.generate_muta_values_in_difer_error(pred_node, post_node, (CirDiferError) init_error);
		}
		else if(init_error instanceof CirValueError) {
			this.generate_muta_values_in_value_error(pred_node, post_node, (CirValueError) init_error);
		}
		else if(init_error instanceof CirReferError) {
			this.generate_muta_values_in_refer_error(pred_node, post_node, (CirReferError) init_error);
		}
		else if(init_error instanceof CirStateError) {
			this.generate_muta_values_in_state_error(pred_node, post_node, (CirStateError) init_error);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + init_error);
		}
	}
	
	/* construct the pred, mid and next condition tree node */
	/**
	 * @param tree
	 * @param context
	 * @throws Exception
	 */
	private void construct_state_tree(CirStateTree tree, Object context) throws Exception {
		if(tree.has_cir_mutations()) {
			/* 1. collect the cir-mutations into corresponding cir-based mutations */
			Map<CirExecution, Collection<CirMutation>> maps = 
					new HashMap<CirExecution, Collection<CirMutation>>();
			for(CirMutation cir_mutation : tree.get_cir_mutations()) {
				CirExecution execution = cir_mutation.get_execution();
				if(!maps.containsKey(execution)) {
					maps.put(execution, new ArrayList<CirMutation>());
				}
				maps.get(execution).add(cir_mutation);
			}
			
			/* 2. generate the reaching part and infection as well as propagation */
			for(CirExecution execution : maps.keySet()) {
				/* 2-1. generate the reaching path of execution states annotated */
				CirStateNode pred_node = this.construct_pred_tree(tree, execution, context);
				this.generate_data_values_in(pred_node);
				
				/* 2-2. generate the state infection part of execution state path */
				for(CirMutation cir_mutation : maps.get(execution)) {
					/* A. divide the state infection constriant to basic conditions */
					Collection<CirAttribute> constraints = new HashSet<CirAttribute>();
					if(cir_mutation.get_constraint() instanceof CirCoverCount) {
						constraints.add(cir_mutation.get_constraint());
					}
					else {
						Collection<SymbolExpression> conditions = this.
								div_conditions(cir_mutation.get_constraint().get_parameter());
						for(SymbolExpression condition : conditions) {
							CirExecution check_point = this.find_prior_checkpoint(execution, condition);
							constraints.add(CirAttribute.new_constraint(check_point, condition, true));
						}
					}
					
					/* B. generate the infection node and error nodes in propagation */
					for(CirAttribute constraint : constraints) {
						CirStateNode next_node = pred_node.new_child(CirStateType.nex_condition, execution);
						this.generate_conditions_in(next_node, constraint);
						this.generate_muta_values_in(pred_node, next_node, cir_mutation.get_init_error());
					}
				}
			}
		}
	}
	/**
	 * It builds up the execution state tree using given context information
	 * @param tree
	 * @param context
	 * @throws Exception
	 */
	protected static void construct(CirStateTree tree, Object context) throws Exception {
		util.construct_state_tree(tree, context);
	}
	
}
