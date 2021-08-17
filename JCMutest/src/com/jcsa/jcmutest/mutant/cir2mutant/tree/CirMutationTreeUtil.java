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

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraint;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It implement the construction of CirMutationTree using RIP-hierarchy model.
 * 
 * @author yukimula
 *
 */
public class CirMutationTreeUtil {
	
	/* singleton mode */ /** constructor **/ private CirMutationTreeUtil() { }
	private static final CirMutationTreeUtil util = new CirMutationTreeUtil();
	
	/* pre_condition (execution) construction */
	/**
	 * @param flow	{true_flow; fals_flow; call_flow; retr_flow}
	 * @return the coverage or constraint attribute to cover the given flow
	 * @throws Exception
	 */
	private CirAttribute get_flow_attribute(CirExecutionFlow flow) throws Exception {
		if(flow == null) {
			throw new IllegalArgumentException("Invalid flow: null");
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
			throw new IllegalArgumentException("Invalid: " + flow);
		}
	}
	/**
	 * collect the execution flows in the path to the given collection.
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
	 * generate the execution flows from program entry to the target in decidable linking paths.
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
	 * generate the execution flows from program entry to the target using dependence analysis
	 * @param target
	 * @param dependence_graph
	 * @param flows
	 * @throws Exception
	 */
	private void generate_execution_flows(CirExecution target, CDependGraph dependence_graph, List<CirExecutionFlow> flows) throws Exception {
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
	 * generate the execution flows from program entry to the target using the testing paths if it can reach it.
	 * @param target
	 * @param state_path
	 * @param flows
	 * @throws Exception if the target cannot be reached by the state path
	 */
	private void generate_execution_flows(CirExecution target, CStatePath state_path, List<CirExecutionFlow> flows) throws Exception {
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
	 * create the pre-condition path from program entry to the specified node using the given flows path
	 * @param tree
	 * @param target
	 * @param flows
	 * @return the reach_ability node to execute the target execution
	 * @throws Exception
	 */
	private CirMutationTreeNode construct_pre_conditions(CirMutationTree tree, 
			CirExecution target, List<CirExecutionFlow> flows) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(flows == null) {
			throw new IllegalArgumentException("Invalid flows: null");
		}
		else {
			/* initialization */
			CirMutationTreeNode node = tree.get_root(); 
			CirAttribute flow_attribute;
			
			/* connecting over the path's flows */
			for(CirExecutionFlow flow : flows) {
				flow_attribute = this.get_flow_attribute(flow);
				node = node.new_child(CirMutationTreeType.pre_condition, 
						flow_attribute, CirMutationTreeFlow.execution);
			}
			
			/* link to the target finally */
			if(node.get_node_execution() != target) {
				flow_attribute = CirAttribute.new_cover_count(target, 1);
				node = node.new_child(CirMutationTreeType.pre_condition, 
						flow_attribute, CirMutationTreeFlow.execution);
			}
			
			/* return the reach_ability node of target */	return node;
		}
	}
	
	/* mid_condition (infection) construction */
	/**
	 * @param condition
	 * @param conditions	the collection to preserve the conditions in disjunction way
	 * @throws Exception
	 */
	private void divide_conditions_in_disjunct(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				conditions.add(SymbolFactory.sym_condition(Boolean.TRUE, true));
			}
			else { }
		}
		if(condition instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			if(operator == COperator.logic_or) {
				this.divide_conditions_in_disjunct(((SymbolBinaryExpression) condition).get_loperand(), conditions);
				this.divide_conditions_in_disjunct(((SymbolBinaryExpression) condition).get_roperand(), conditions);
			}
			else {
				conditions.add(SymbolFactory.sym_condition(condition, true));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_condition(condition, true));
		}
	}
	/**
	 * @param reach_node
	 * @param mutation
	 * @return the set of infection edges created from the CIR-based mutation under the reaching node
	 * @throws Exception
	 */
	private Collection<CirMutationTreeEdge> construct_mid_conditions(CirMutationTreeNode reach_node, CirMutation mutation) throws Exception {
		if(reach_node == null) {
			throw new IllegalArgumentException("Invalid reach_node: null");
		}
		else if(mutation == null) {
			throw new IllegalArgumentException("Invalid mutation as null");
		}
		else {
			CirMutationTreeNode pred_node, post_node;
			Collection<CirMutationTreeEdge> edges = new HashSet<CirMutationTreeEdge>();
			CirAttribute constraint = mutation.get_constraint();
			CirAttribute init_error = mutation.get_init_error();
			
			if(constraint instanceof CirConstraint) {
				SymbolExpression condition = ((CirConstraint) constraint).get_condition();
				condition = condition.evaluate(null);
				Set<SymbolExpression> sub_conditions = new HashSet<SymbolExpression>();
				this.divide_conditions_in_disjunct(condition, sub_conditions);
				CirExecution execution = constraint.get_execution();
				for(SymbolExpression sub_condition : sub_conditions) {
					pred_node = reach_node.new_child(CirMutationTreeType.mid_condition, 
							CirAttribute.new_constraint(execution, sub_condition, true), 
							CirMutationTreeFlow.execution);
					post_node = pred_node.new_child(CirMutationTreeType.mid_condition, 
							init_error, CirMutationTreeFlow.infection);
					edges.add(post_node.get_in_edge());
				}
			}
			else {
				pred_node = reach_node.new_child(
						CirMutationTreeType.mid_condition, 
						constraint, CirMutationTreeFlow.execution);
				post_node = pred_node.new_child(
						CirMutationTreeType.mid_condition, 
						init_error, CirMutationTreeFlow.infection);
				edges.add(post_node.get_in_edge());
			}
			
			return edges;
		}
	}
	
	/* poscondition tree construction */
	/**
	 * generate the next set of errors directly propagated from the input error as source
	 * @param error		source error
	 * @param errors	next generation of state errors
	 * @throws Exception
	 */
	private void propagate_from(CirAttribute error, Collection<CirAttribute> errors) throws Exception {
		if(error == null) {
			throw new IllegalArgumentException("Invalid error: null");
		}
		else if(error instanceof CirBlockError) {
			this.propagate_from_block_error((CirBlockError) error, errors);
		}
		else if(error instanceof CirFlowsError) {
			this.propagate_from_flows_error((CirFlowsError) error, errors);
		}
		else if(error instanceof CirTrapsError) {
			this.propagate_from_traps_error((CirTrapsError) error, errors);
		}
		else if(error instanceof CirStateError) {
			this.propagate_from_state_error((CirStateError) error, errors);
		}
		else if(error instanceof CirValueError) {
			this.propagate_from_value_error((CirValueError) error, errors);
		}
		else if(error instanceof CirReferError) {
			this.propagate_from_refer_error((CirReferError) error, errors);
		}
		else if(error instanceof CirDiferError) {
			this.propagate_from_difer_error((CirDiferError) error, errors);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + error);
		}
	}
	/**
	 * @param error
	 * @param errors
	 * @throws Exception
	 */
	private void propagate_from_block_error(CirBlockError error, Collection<CirAttribute> errors) throws Exception { /* none */ }
	/**
	 * true --> add_executions; false --> del_executions;
	 * @param orig_target
	 * @param muta_target
	 * @return
	 * @throws Exception
	 */
	private Map<Boolean, Collection<CirExecution>>	get_add_del_executions(CirExecution orig_target, CirExecution muta_target) throws Exception {
		if(orig_target == null) {
			throw new IllegalArgumentException("Invalid orig_target: null");
		}
		else if(muta_target == null) {
			throw new IllegalArgumentException("Invalid muta_target: null");
		}
		else {
			/* compute the statements being added or deleted in testing */
			Collection<CirExecution> add_executions = new HashSet<>();
			Collection<CirExecution> del_executions = new HashSet<>();
			CirExecutionPath orig_path = CirExecutionPathFinder.finder.df_extend(orig_target);
			CirExecutionPath muta_path = CirExecutionPathFinder.finder.df_extend(muta_target);
			for(CirExecutionEdge edge : muta_path.get_edges()) { add_executions.add(edge.get_source()); }
			for(CirExecutionEdge edge : orig_path.get_edges()) { del_executions.add(edge.get_source()); }
			add_executions.add(muta_path.get_target()); del_executions.add(orig_path.get_target());

			/* removed the common part for corrections */
			Collection<CirExecution> com_executions = new HashSet<>();
			for(CirExecution execution : add_executions) {
				if(del_executions.contains(execution)) {
					com_executions.add(execution);
				}
			}
			add_executions.removeAll(com_executions);
			del_executions.removeAll(com_executions);

			/* construct mapping from true|false to collections */
			Map<Boolean, Collection<CirExecution>> results =
					new HashMap<>();
			results.put(Boolean.TRUE, add_executions);
			results.put(Boolean.FALSE, del_executions);
			return results;
		}
	}
	/**
	 * @param error
	 * @param errors
	 * @throws Exception
	 */
	private void propagate_from_flows_error(CirFlowsError error, Collection<CirAttribute> errors) throws Exception {
		/* determine the executions being (or not) executed in mutation */
		CirExecution orig_target = error.get_original_flow().get_target();
		CirExecution muta_target = error.get_mutation_flow().get_target();
		Map<Boolean, Collection<CirExecution>> results =
					this.get_add_del_executions(orig_target, muta_target);

		/* construct the next generation of statement errors from source */
		for(Boolean result : results.keySet()) {
			for(CirExecution execution : results.get(result)) {
				errors.add(CirAttribute.new_block_error(execution, result));
			}
		}
	}
	/**
	 * @param error
	 * @param errors
	 * @throws Exception
	 */
	private void propagate_from_traps_error(CirTrapsError error, Collection<CirAttribute> errors) throws Exception { /* none */ }
	/**
	 * @param error
	 * @param errors
	 * @throws Exception
	 */
	private void propagate_from_state_error(CirStateError error, Collection<CirAttribute> errors) throws Exception { /* none */ }
	/**
	 * @param error
	 * @param errors
	 * @throws Exception
	 */
	private void propagate_from_difer_error(CirDiferError error, Collection<CirAttribute> errors) throws Exception {
		errors.add(CirAttribute.new_traps_error(error.get_execution()));
	}
	/**
	 * @param error
	 * @param errors
	 * @throws Exception
	 */
	private void propagate_from_value_error(CirValueError error, Collection<CirAttribute> errors) throws Exception {
		/* declarations */
		CirExpression child = (CirExpression) error.get_location();
		SymbolExpression muta_child = error.get_muta_expression();
		CirNode parent = child.get_parent();
		this.propagate_over_cir_context(parent, child, muta_child, false, errors);
	}
	/**
	 * @param error
	 * @param errors
	 * @throws Exception
	 */
	private void propagate_from_refer_error(CirReferError error, Collection<CirAttribute> errors) throws Exception {
		/* declarations */
		CirExpression child = (CirExpression) error.get_location();
		SymbolExpression muta_child = error.get_muta_expression();
		CirNode parent = child.get_parent();
		this.propagate_over_cir_context(parent, child, muta_child, true, errors);
	}
	/**
	 * @param expression
	 * @param operand
	 * @param muta_operand
	 * @return generate the symbolic expression from child using mutated version to its parent
	 * @throws Exception
	 */
	private SymbolExpression composite_muta_expression(CirComputeExpression expression,
				CirExpression operand, SymbolExpression muta_operand) throws Exception {
		switch(expression.get_operator()) {
		/* unary operator */
		case negative:		return SymbolFactory.arith_neg(muta_operand);
		case bit_not:		return SymbolFactory.bitws_rsv(muta_operand);
		case logic_not:		return SymbolFactory.logic_not(muta_operand);
		/* arithmetic */
		case arith_add:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.arith_add(expression.get_data_type(), muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.arith_add(expression.get_data_type(), expression.get_operand(0), muta_operand);
			}
		}
		case arith_sub:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.arith_sub(expression.get_data_type(), muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.arith_sub(expression.get_data_type(), expression.get_operand(0), muta_operand);
			}
		}
		case arith_mul:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.arith_mul(expression.get_data_type(), muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.arith_mul(expression.get_data_type(), expression.get_operand(0), muta_operand);
			}
		}
		case arith_div:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.arith_div(expression.get_data_type(), muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.arith_div(expression.get_data_type(), expression.get_operand(0), muta_operand);
			}
		}
		case arith_mod:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.arith_mod(expression.get_data_type(), muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.arith_mod(expression.get_data_type(), expression.get_operand(0), muta_operand);
			}
		}
		/* bitwise */
		case bit_and:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.bitws_and(expression.get_data_type(), muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.bitws_and(expression.get_data_type(), expression.get_operand(0), muta_operand);
			}
		}
		case bit_or:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.bitws_ior(expression.get_data_type(), muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.bitws_ior(expression.get_data_type(), expression.get_operand(0), muta_operand);
			}
		}
		case bit_xor:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.bitws_xor(expression.get_data_type(), muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.bitws_xor(expression.get_data_type(), expression.get_operand(0), muta_operand);
			}
		}
		case left_shift:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.bitws_lsh(expression.get_data_type(), muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.bitws_lsh(expression.get_data_type(), expression.get_operand(0), muta_operand);
			}
		}
		case righ_shift:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.bitws_rsh(expression.get_data_type(), muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.bitws_rsh(expression.get_data_type(), expression.get_operand(0), muta_operand);
			}
		}
		/* logical */
		case logic_and:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.logic_and(muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.logic_and(expression.get_operand(0), muta_operand);
			}
		}
		case logic_or:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.logic_ior(muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.logic_ior(expression.get_operand(0), muta_operand);
			}
		}
		/* relational */
		case greater_tn:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.greater_tn(muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.greater_tn(expression.get_operand(0), muta_operand);
			}
		}
		case greater_eq:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.greater_eq(muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.greater_eq(expression.get_operand(0), muta_operand);
			}
		}
		case smaller_tn:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.smaller_tn(muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.smaller_tn(expression.get_operand(0), muta_operand);
			}
		}
		case smaller_eq:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.smaller_eq(muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.smaller_eq(expression.get_operand(0), muta_operand);
			}
		}
		case equal_with:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.equal_with(muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.equal_with(expression.get_operand(0), muta_operand);
			}
		}
		case not_equals:
		{
			if(expression.get_operand(0) == operand) {
				return SymbolFactory.not_equals(muta_operand, expression.get_operand(1));
			}
			else {
				return SymbolFactory.not_equals(expression.get_operand(0), muta_operand);
			}
		}
		/* unsupported */
		default:	throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * syntax-directed error propagation analysis from child to parent using specified mutation expression
	 * @param parent
	 * @param child
	 * @param muta_child
	 * @param is_reference
	 * @throws Exception
	 */
	private void propagate_over_cir_context(CirNode parent, CirExpression child,
			SymbolExpression muta_child, boolean is_reference,
			Collection<CirAttribute> errors) throws Exception {
		/** declarations **/
		SymbolExpression muta_expression; CirAttribute error;

		/** I. (defer child) ==> (defer muta_child) as reference **/
		if(parent instanceof CirDeferExpression) {
			CirDeferExpression expression = (CirDeferExpression) parent;
			muta_expression = SymbolFactory.dereference(muta_child);
			error = CirAttribute.new_refer_error(expression, muta_expression);
			errors.add(error);
		}
		/** child.field ==> muta_child.field as value or refer **/
		else if(parent instanceof CirFieldExpression) {
			CirFieldExpression expression = (CirFieldExpression) parent;
			String field_name = expression.get_field().get_name();
			muta_expression = SymbolFactory.field_expression(muta_child, field_name);
			if(is_reference) {
				error = CirAttribute.new_refer_error(expression, muta_expression);
			}
			else {
				error = CirAttribute.new_value_error(expression, muta_expression);
			}
			errors.add(error);
		}
		/** &child --> &(muta_refer) as value **/
		else if(parent instanceof CirAddressExpression) {
			CirAddressExpression expression = (CirAddressExpression) parent;
			if(is_reference) {
				muta_expression = SymbolFactory.address_of(muta_child);
				error = CirAttribute.new_value_error(expression, muta_expression);
				errors.add(error);
			}
		}
		/** (type child) --> (type muta_child) as value **/
		else if(parent instanceof CirCastExpression) {
			CirCastExpression expression = (CirCastExpression) parent;
			CType cast_type = expression.get_type().get_typename();
			muta_expression = SymbolFactory.cast_expression(cast_type, muta_child);
			error = CirAttribute.new_value_error(expression, muta_expression);
			errors.add(error);
		}
		/** (wait func) --> (wait muta_func(arguments)) **/
		else if(parent instanceof CirWaitExpression) {
			/* local declarations */
			CirWaitExpression expression = (CirWaitExpression) parent;
			CirExecution wait_execution = parent.execution_of();
			CirExecution call_execution =
						wait_execution.get_graph().get_execution(wait_execution.get_id() - 1);
			CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();

			/* call expression creation */
			List<Object> arguments = new ArrayList<>();
			CirArgumentList alist = call_statement.get_arguments();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			muta_expression = SymbolFactory.call_expression(muta_child, arguments);

			/* error generation on wait_expression */
			error = CirAttribute.new_value_error(expression, muta_expression);
			errors.add(error);
		}
		/** {... expr ...} --> {... muta_expr ...} **/
		else if(parent instanceof CirInitializerBody) {
			CirInitializerBody expression = (CirInitializerBody) parent;
			List<Object> elements = new ArrayList<>();
			for(int k = 0; k < expression.number_of_elements(); k++) {
				if(expression.get_element(k) == child) {
					elements.add(muta_child);
				}
				else {
					elements.add(expression.get_element(k));
				}
			}
			muta_expression = SymbolFactory.initializer_list(elements);
			error = CirAttribute.new_value_error(expression, muta_expression);
			errors.add(error);
		}
		/** compositional expression from operand propagation **/
		else if(parent instanceof CirComputeExpression) {
			CirComputeExpression expression = (CirComputeExpression) parent;
			muta_expression = this.composite_muta_expression(expression, child, muta_child);
			error = CirAttribute.new_value_error(expression, muta_expression);
			errors.add(error);
		}
		/** assignment statement (value|refer --> state) **/
		else if(parent instanceof CirAssignStatement) {
			CirAssignStatement statement = (CirAssignStatement) parent;
			if(statement.get_rvalue() == child) {
				error = CirAttribute.new_state_error(statement.get_lvalue(), muta_child);
				errors.add(error);
			}
		}
		/** if.condition ==> do no error propagation **/
		else if(parent instanceof CirIfStatement) { /* no more propagation */ }
		else if(parent instanceof CirCaseStatement) { /* no more propagation */ }
		/** call_statement --> wait_expression as muta_function **/
		else if(parent instanceof CirCallStatement) {
			/* local declarations */
			CirCallStatement call_statement = (CirCallStatement) parent;
			CirExecution call_execution = call_statement.execution_of();
			CirExecution wait_execution = call_execution.get_graph().get_execution(call_execution.get_id() + 1);
			CirWaitAssignStatement wait_statement = (CirWaitAssignStatement) wait_execution.get_statement();
			CirExpression expression = wait_statement.get_rvalue();

			/* construct call expression in the mutation value */
			CirArgumentList alist = call_statement.get_arguments();
			List<Object> arguments = new ArrayList<>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			muta_expression = SymbolFactory.call_expression(muta_child, arguments);

			/* generate value error at wait_expression */
			error = CirAttribute.new_value_error(expression, muta_expression);
			errors.add(error);
		}
		/** argument --> wait_expression using mutation argument in call **/
		else if(parent instanceof CirArgumentList) {
			/* local declarations */
			CirCallStatement call_statement = (CirCallStatement) parent.get_parent();
			CirExecution call_execution = call_statement.execution_of();
			CirExecution wait_execution = call_execution.get_graph().get_execution(call_execution.get_id() + 1);
			CirWaitAssignStatement wait_statement = (CirWaitAssignStatement) wait_execution.get_statement();
			CirExpression expression = wait_statement.get_rvalue();

			/* construct call expression in the mutation value */
			CirArgumentList alist = call_statement.get_arguments();
			List<Object> arguments = new ArrayList<>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				if(alist.get_argument(k) == child) {
					arguments.add(muta_child);
				}
				else {
					arguments.add(alist.get_argument(k));
				}
			}
			muta_expression = SymbolFactory.call_expression(call_statement.get_function(), arguments);

			/* generate value error at wait_expression */
			error = CirAttribute.new_value_error(expression, muta_expression);
			errors.add(error);
		}
		else {
			throw new IllegalArgumentException("Invalid parent: " + parent);
		}
	}
	/**
	 * recursively generate propagation tree from source error node
	 * @param error_node
	 * @throws Exception
	 */
	private Iterable<CirMutationTreeNode> create_propagation_tree_on(CirMutationTreeNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid error_node: null");
		}
		else {
			Set<CirAttribute> errors = new HashSet<>();
			this.propagate_from(source.get_node_attribute(), errors);
			Set<CirMutationTreeNode> targets = new HashSet<>();
			for(CirAttribute error : errors) {
				CirMutationTreeNode target = source.new_child(
						CirMutationTreeType.pos_condition, error, 
						CirMutationTreeFlow.propagate);
				targets.add(target);
			}
			return targets;
		}
	}
	/**
	 * @param init_error_node
	 * @throws Exception
	 */
	private void construct_pos_conditions(CirMutationTreeNode init_error_node) throws Exception {
		Queue<CirMutationTreeNode> queue = new LinkedList<>();
		queue.add(init_error_node);
		while(!queue.isEmpty()) {
			CirMutationTreeNode source = queue.poll();
			Iterable<CirMutationTreeNode> targets = this.create_propagation_tree_on(source);
			for(CirMutationTreeNode target : targets) {
				queue.add(target);
			}
		}
	}
	
	/* construction */
	/**
	 * construct the CIR_based mutation tree in terms of RIP framework
	 * @param tree
	 * @param context
	 * @throws Exception
	 */
	private void construct_mutation_tree(CirMutationTree tree, Object context) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else {
			/* I. maps each execution to the corresponding CirMutation(s) */
			Map<CirExecution, Collection<CirMutation>> maps =
					new HashMap<CirExecution, Collection<CirMutation>>();
			for(CirMutation cir_mutation : tree.get_cir_mutations()) {
				CirExecution execution = cir_mutation.get_execution();
				if(!maps.containsKey(execution)) {
					maps.put(execution, new HashSet<CirMutation>());
				}
				maps.get(execution).add(cir_mutation);
			}
			
			/* II. construct pre_condition and mid_condition parts */
			for(CirExecution execution : maps.keySet()) {
				/* II-A. Construct the execution path using context and build pre-conditions */
				List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
				if(context == null) {
					this.generate_execution_flows(execution, flows);
				}
				else if(context instanceof CDependGraph) {
					this.generate_execution_flows(execution, (CDependGraph) context, flows);
				}
				else if(context instanceof CStatePath) {
					this.generate_execution_flows(execution, (CStatePath) context, flows);
				}
				else {
					throw new IllegalArgumentException("Invalid context: " + context);
				}
				CirMutationTreeNode reach_node = this.construct_pre_conditions(tree, execution, flows);
				
				/* II-B. Construct the infection edges from CIR_based mutations as given */
				for(CirMutation cir_mutation : maps.get(execution)) {
					this.construct_mid_conditions(reach_node, cir_mutation);
				}
			}
			tree.set_infection_edges();		/* update the state infection edges after that */
			
			/* III. construct pos_condition parts of that tree */
			for(CirMutationTreeEdge infection_edge : tree.get_infection_edges()) {
				this.construct_pos_conditions(infection_edge.get_target());
			}
		}
	}
	/**
	 * construct the CIR_based mutation tree in terms of RIP framework
	 * @param tree
	 * @param context
	 * @throws Exception
	 */
	protected static void construct(CirMutationTree tree, Object context) throws Exception {
		util.construct_mutation_tree(tree, context);
	}
	
}
