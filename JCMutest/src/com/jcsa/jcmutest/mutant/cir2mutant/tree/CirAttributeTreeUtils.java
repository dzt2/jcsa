package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockError;
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
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It implements the construction of CirAttributeNode and CirAttributeTree.
 * 
 * @author yukimula
 *
 */
final class CirAttributeTreeUtils {
	
	/* singleton mode */ /** constructor **/ 	private CirAttributeTreeUtils() {}
	private static final CirAttributeTreeUtils util = new CirAttributeTreeUtils();
	
	/* prev_nodes methods */
	/**
	 * It creates an attribute to represent the constriant for covering the flow
	 * @param flow
	 * @return
	 * @throws Exception
	 */
	private CirAttribute new_flow_attribute(CirExecutionFlow flow) throws Exception {
		if(flow == null) {
			throw new IllegalArgumentException("Invalid flow as null");
		}
		else if(flow.get_type() == CirExecutionFlowType.true_flow) {
			CirStatement statement = flow.get_source().get_statement();
			CirExpression condition;
			if(statement instanceof CirIfStatement) {
				condition = ((CirIfStatement) statement).get_condition();
			}
			else {
				condition = ((CirCaseStatement) statement).get_condition();
			}
			return CirAttribute.new_constraint(flow.get_source(), condition, true);
		}
		else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
			CirStatement statement = flow.get_source().get_statement();
			CirExpression condition;
			if(statement instanceof CirIfStatement) {
				condition = ((CirIfStatement) statement).get_condition();
			}
			else {
				condition = ((CirCaseStatement) statement).get_condition();
			}
			return CirAttribute.new_constraint(flow.get_source(), condition, false);
		}
		else if(flow.get_type() == CirExecutionFlowType.call_flow) {
			return CirAttribute.new_cover_count(flow.get_source(), 1);
		}
		else if(flow.get_type() == CirExecutionFlowType.retr_flow) {
			return CirAttribute.new_cover_count(flow.get_target(), 1);
		}
		else {
			throw new IllegalArgumentException("Invalid flow: " + flow);
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
	 * It constructs the pred_nodes for reaching the target execution of mutation.
	 * @param tree
	 * @param target
	 * @param context
	 * @throws Exception
	 */
	private CirAttributeNode construct_pre_attribute_nodes(CirAttributeTree tree, CirExecution target, Object context) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			/* 1. it generates the previous path for reaching the target */
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
			
			/* 2. it creates the previous path for reaching the target node */
			CirAttributeNode node = tree.get_root();
			for(CirExecutionFlow flow : flows) {
				node = node.new_child(this.new_flow_attribute(flow));
			}
			
			/* 3. it finally links the tail of the previous_node to the reach node */
			if(node.get_attribute().get_execution() != target) {
				node = node.new_child(CirAttribute.new_cover_count(target, 1));
			}
			
			/* 4. it returns the execution of the target node in */	return node;
		}
	}
	/**
	 * @param reach_node
	 * @param cir_mutation
	 * @return reach_node --> cond_node --> kill_node --> init_node
	 * @throws Exception
	 */
	private CirAttributeNode construct_mid_attribute_nodes(CirAttributeNode reach_node, CirMutation cir_mutation) throws Exception {
		CirAttributeNode cond_node = reach_node.new_child(cir_mutation.get_constraint());
		CirAttributeNode muta_node = cond_node.new_child(CirAttribute.new_kill_mutant(cir_mutation));
		CirAttributeNode init_node = muta_node.new_child(cir_mutation.get_init_error());
		return init_node;
	}
	
	/* static error propagation analysis */
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
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private void construct_pos_attribute_nodes(CirAttributeNode source) throws Exception {
		/* 1. capture the error-propagation nodes list */
		Set<CirAttribute> errors = new HashSet<CirAttribute>();
		this.propagate_from(source.get_attribute(), errors);
		
		/* 2. recursively construct the post-nodes from */
		for(CirAttribute next_error : errors) {
			CirAttributeNode child = source.new_child(next_error);
			this.construct_pos_attribute_nodes(child);
		}
	}
	
	/* construction interface */
	/**
	 * It constructs the structure of attribute tree for representing reaching-infection-propagation process.
	 * @param tree
	 * @param context
	 * @throws Exception
	 */
	private void construct_attribute_tree(CirAttributeTree tree, Object context) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else {
			/* 1. collects the map from execution to corresponding mutations */
			Map<CirExecution, Collection<CirMutation>> maps = 
					new HashMap<CirExecution, Collection<CirMutation>>();
			for(CirMutation cir_mutation : tree.get_cir_mutations()) {
				CirExecution execution = cir_mutation.get_execution();
				if(!maps.containsKey(execution)) {
					maps.put(execution, new ArrayList<CirMutation>());
				}
				maps.get(execution).add(cir_mutation);
			}
			
			/* 2. it performs previous construction over the tree and path context */
			Set<CirAttributeNode> error_nodes = new HashSet<CirAttributeNode>();
			for(CirExecution target : maps.keySet()) {
				CirAttributeNode reach_node = this.construct_pre_attribute_nodes(tree, target, context);
				for(CirMutation cir_mutation : maps.get(target)) {
					error_nodes.add(this.construct_mid_attribute_nodes(reach_node, cir_mutation));
				}
			}
			
			/* 3. it constructs the postfix subtrees from initial error nodes */
			tree.update_muta_nodes();
			for(CirAttributeNode error_node : error_nodes) { 
				this.construct_pos_attribute_nodes(error_node);
			}
		}
	}
	/**
	 * It constructs the structure of attribute tree for representing reaching-infection-propagation process.
	 * @param tree
	 * @param context		null or CDependGraph or CStatePath
	 * @throws Exception
	 */
	protected static void construct(CirAttributeTree tree, Object context) throws Exception {
		util.construct_attribute_tree(tree, context);
	}
	
}
