package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirCoverCount;
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
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It implements the construction of CirMutationTree and generation of
 * CirAnnotation as features from the CirAttribute automatically.
 *
 * @author yukimula
 *
 */
class CirMutationTreeUtil {

	/* singleton mode */	/** constructor **/	private CirMutationTreeUtil() {}
	protected static final CirMutationTreeUtil util = new CirMutationTreeUtil();

	/* precondition tree construction */
	/**
	 * create the flow-reachability constraint as attribute for linking mutation
	 * tree node in the precondition module
	 * @param flow
	 * @return
	 * @throws Exception
	 */
	private CirAttribute		get_flow_attribute(CirExecutionFlow flow) throws Exception {
		/* null parameter is avoided 	*/
		if(flow == null) {
			throw new IllegalArgumentException("Invalid flow as null");
		}
		/* true_flow: constraint as {condition == true in if_stmt} */
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
		/* fals_flow: constraint as {condition == false in if_stmt} */
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
		/* call_flow: constraint as {cover call_stmt by 1} */
		else if(flow.get_type() == CirExecutionFlowType.call_flow) {
			return CirAttribute.new_cover_count(flow.get_source(), 1);
		}
		/* retr_flow: constraint as {cover wait_stmt by 1} */
		else if(flow.get_type() == CirExecutionFlowType.retr_flow) {
			return CirAttribute.new_cover_count(flow.get_target(), 1);
		}
		/* otherwise, covering either of the node on edge is available */
		else {
			return CirAttribute.new_cover_count(flow.get_source(), 1);
		}
	}
	/**
	 * @param target
	 * @return create a prefix-path reaching the target from its function entry
	 * @throws Exception
	 */
	private CirExecutionPath 	get_execution_path(CirExecution target) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			CirExecution source = target.get_graph().get_entry();
			CirExecutionPath path = new CirExecutionPath(source);
			CirExecutionPathFinder.finder.vf_extend(path, target);
			return path;
		}
	}
	/**
	 * @param target
	 * @param dependence_graph is null will call get_execution_path(target)
	 * @return generate the execution path for reaching target using static dependence graph model
	 * @throws Exception
	 */
	private CirExecutionPath 	get_execution_path(CirExecution target, CDependGraph dependence_graph) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(dependence_graph == null) {
			return this.get_execution_path(target);
		}
		else {
			return CirExecutionPathFinder.finder.dependence_path(dependence_graph, target);
		}
	}
	/**
	 * @param target
	 * @param state_path null to call get_execution_path(target)
	 * @return generate the concrete execution path reaching target using dynamic analysis path
	 * @throws Exception
	 */
	private CirExecutionPath 	get_execution_path(CirExecution target, CStatePath state_path) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(state_path == null || state_path.size() == 0) {
			return this.get_execution_path(target);
		}
		else {
			CirExecution source = state_path.get_node(0).get_execution();
			CirExecutionPath path = new CirExecutionPath(source);
			for(CStateNode state_node : state_path.get_nodes()) {
				CirExecutionPathFinder.finder.vf_extend(path, state_node.get_execution());
				if(state_node.get_execution() == target) {
					break;
				}
			}
			CirExecutionPathFinder.finder.vf_extend(path, target);
			return path;
		}
	}
	/**
	 * create a reachability path with path constriants from root to the target with specified flows in the
	 * concrete path.
	 * @param tree
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private CirMutationTreeNode	create_reachability_tree(CirMutationTree tree, CirExecutionPath path) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else {
			/* 1. connect from root to the previous constraint in target */
			CirMutationTreeNode node = tree.get_root(); CirAttribute attribute;
			for(CirExecutionEdge edge : path.get_edges()) {
				attribute = this.get_flow_attribute(edge.get_flow());
				node = node.link(CirMutationTreeType.precondition,
						attribute, CirMutationTreeFlow.execute).get_target();
			}

			/* 2. linking to the target execution if needed */
			if(node.get_attribute().get_execution() != path.get_target()) {
				attribute = CirAttribute.new_cover_count(path.get_target(), 1);
				node = node.link(CirMutationTreeType.precondition,
						attribute, CirMutationTreeFlow.execute).get_target();
			}

			/* 3. return the target-reach node */	return node;
		}
	}
	/**
	 * reach_node --> ( constraint --> init_error )+
	 * @param reach_node
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirMutationTreeEdge create_infection_edge_on(CirMutationTreeNode reach_node, CirMutation mutation) throws Exception {
		if(reach_node == null) {
			throw new IllegalArgumentException("Invalid reach_node: null");
		}
		else if(mutation == null) {
			throw new IllegalArgumentException("Invalid mutation as null");
		}
		else {
			CirMutationTreeNode node = reach_node;
			node = node.link(CirMutationTreeType.midcondition, mutation.
					get_constraint(), CirMutationTreeFlow.execute).get_target();
			return node.link(CirMutationTreeType.midcondition, mutation.
					get_init_error(), CirMutationTreeFlow.infect);
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
			this.propagate_from(source.get_attribute(), errors);
			Set<CirMutationTreeNode> targets = new HashSet<>();
			for(CirAttribute error : errors) {
				CirMutationTreeNode target = source.link(
						CirMutationTreeType.poscondition, error,
						CirMutationTreeFlow.propagate).get_target();
				targets.add(target);
			}
			return targets;
		}
	}
	/**
	 * @param init_error_node
	 * @throws Exception
	 */
	private void create_propagation_tree(CirMutationTreeNode init_error_node) throws Exception {
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

	/* integrated interfaces for tree construction */
	/**
	 * @param tree
	 * @param context null|CDependGraph|CStatePath
	 * @throws Exception
	 */
	protected void construct_mutation_tree_in(CirMutationTree tree, Object context) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else {
			/* manage each execution to corresponding cir-mutations */
			Map<CirExecution, Collection<CirMutation>> maps =
					new HashMap<CirExecution, Collection<CirMutation>>();
			for(CirMutation cir_mutation : tree.get_cir_mutations()) {
				CirExecution execution = cir_mutation.get_execution();
				if(!maps.containsKey(execution)) {
					maps.put(execution, new HashSet<CirMutation>());
				}
				maps.get(execution).add(cir_mutation);
			}

			/* reachability & infection construction */
			Set<CirMutationTreeEdge> infect_edges = new HashSet<>();
			for(CirExecution execution : maps.keySet()) {
				CirExecutionPath path;
				if(context == null) {
					path = this.get_execution_path(execution);
				}
				else if(context instanceof CDependGraph) {
					path = this.get_execution_path(execution, (CDependGraph) context);
				}
				else if(context instanceof CStatePath) {
					path = this.get_execution_path(execution, (CStatePath) context);
				}
				else {
					throw new IllegalArgumentException("Invalid: " + context);
				}
				CirMutationTreeNode reach_node = this.create_reachability_tree(tree, path);
				for(CirMutation cir_mutation : maps.get(execution)) {
					infect_edges.add(this.create_infection_edge_on(reach_node, cir_mutation));
				}
			}

			/* propagation tree construction */
			for(CirMutationTreeEdge infect_edge : infect_edges) {
				this.create_propagation_tree(infect_edge.get_target());
			}
		}
	}

	/* symbolic expression analysis and evaluation */
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
	 * @return the set of references defined in the node
	 */
	private Collection<SymbolExpression> get_symbol_references_in(SymbolNode node) {
		Set<SymbolExpression> references = new HashSet<>();
		this.get_symbol_references_in(node, references); return references;
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
	 * recursive collect the symbolic conditions defined in logical conjunction
	 * @param expression
	 * @param expressions
	 * @throws Exception
	 */
	private void get_symbol_conditions_in(SymbolExpression expression, Collection<SymbolExpression> expressions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				/* ignore TRUE since it is equivalent to cov_stmt(1) */
			}
			else {
				expressions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.logic_and) {
				this.get_symbol_conditions_in(((SymbolBinaryExpression) expression).get_loperand(), expressions);
				this.get_symbol_conditions_in(((SymbolBinaryExpression) expression).get_roperand(), expressions);
			}
			else {
				expressions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else {
			expressions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void generate_symbol_conditions_in(SymbolExpression expression, Collection<SymbolExpression> conditions) throws Exception {
		Set<SymbolExpression> expressions = new HashSet<>();
		this.get_symbol_conditions_in(expression, expressions);
		for(SymbolExpression sub_expression : expressions) {
			conditions.add(sub_expression);
			if(sub_expression instanceof SymbolBinaryExpression) {
				COperator operator = ((SymbolBinaryExpression) sub_expression).get_operator().get_operator();
				SymbolExpression loperand = ((SymbolBinaryExpression) sub_expression).get_loperand();
				SymbolExpression roperand = ((SymbolBinaryExpression) sub_expression).get_roperand();
				switch(operator) {
				case greater_tn:
				{
					conditions.add(SymbolFactory.greater_eq(loperand, roperand));
					conditions.add(SymbolFactory.not_equals(loperand, roperand));
					break;
				}
				case smaller_tn:
				{
					conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
					conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
					break;
				}
				case equal_with:
				{
					conditions.add(SymbolFactory.greater_eq(loperand, roperand));
					conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
					break;
				}
				default:			break;
				}
			}
		}
	}

	/**
	 * @param execution
	 * @param expression
	 * @return find the previous check-point where the expression should be evaluated
	 * @throws Exception
	 */
	private CirExecution find_previous_point(CirExecution execution, SymbolExpression expression) throws Exception {
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
			Collection<SymbolExpression> references = this.get_symbol_references_in(expression);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(this.has_symbol_references_in(edge.get_source(), references)) {
					return edge.get_target();
				}
			}
			return prev_path.get_source();
		}
	}
	/**
	 * @param expression
	 * @param context
	 * @return symbolic evaluation
	 * @throws Exception
	 */
	private SymbolExpression symbol_evaluate(SymbolExpression expression, SymbolProcess context) throws Exception {
		return SymbolEvaluator.evaluate_on(expression, context);
	}

	/* concrete annotation generation from CirAttribute statically */
	/**
	 * generate the concrete annotations from concrete instance of attribute in evaluation
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	protected void generate_annotations(CirAttribute attribute, SymbolProcess
			context, Collection<CirAnnotation> annotations) throws Exception {
		/* invalid class */
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		/* constraint-class */
		else if(attribute instanceof CirConstraint) {
			this.generate_annotations_in_constraint((CirConstraint) attribute, context, annotations);
		}
		else if(attribute instanceof CirCoverCount) {
			this.generate_annotations_in_cover_count((CirCoverCount) attribute, context, annotations);
		}
		/* statement-error */
		else if(attribute instanceof CirBlockError) {
			this.generate_annotations_in_block_error((CirBlockError) attribute, context, annotations);
		}
		else if(attribute instanceof CirTrapsError) {
			this.generate_annotations_in_traps_error((CirTrapsError) attribute, context, annotations);
		}
		else if(attribute instanceof CirFlowsError) {
			this.generate_annotations_in_flows_error((CirFlowsError) attribute, context, annotations);
		}
		/* expression-error */
		else if(attribute instanceof CirValueError) {
			this.generate_annotations_in_value_error((CirValueError) attribute, context, annotations);
		}
		else if(attribute instanceof CirReferError) {
			this.generate_annotations_in_refer_error((CirReferError) attribute, context, annotations);
		}
		else if(attribute instanceof CirStateError) {
			this.generate_annotations_in_state_error((CirStateError) attribute, context, annotations);
		}
		/* unsupported class */
		else {
			throw new IllegalArgumentException("Invalid: " + attribute);
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_cover_count(CirCoverCount attribute,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. declarations and getters */
		CirExecution execution = attribute.get_execution();
		int coverage_times = attribute.get_coverage_count();

		/* 2. capture execution domain */
		List<Integer> execution_times = new ArrayList<>();
		for(int k = 1; k < coverage_times; k = k * 2) {
			execution_times.add(Integer.valueOf(k));
		}
		execution_times.add(Integer.valueOf(coverage_times));

		/* 3. generate coverage attributes */
		for(int execution_time : execution_times) {
			annotations.add(CirAnnotation.cov_stmt(execution, execution_time));
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_constraint(CirConstraint attribute,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* declarations */
		SymbolExpression expression = this.symbol_evaluate(attribute.get_parameter(), null);
		Set<SymbolExpression> conditions = new HashSet<>();
		this.generate_symbol_conditions_in(expression, conditions);
		CirExecution execution = attribute.get_execution();

		/* generate constraint annotation */
		for(SymbolExpression condition : conditions) {
			CirExecution prev_execution = this.find_previous_point(execution, condition);
			annotations.add(CirAnnotation.eva_expr(prev_execution, condition));
		}

		/* coverage generation only when necessary */
		if(conditions.isEmpty()) {
			CirExecution checkpoint = this.find_previous_point(
					execution, SymbolFactory.sym_constant(Boolean.TRUE));
			annotations.add(CirAnnotation.cov_stmt(checkpoint, 1));
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_block_error(CirBlockError attribute,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		if(attribute.is_executed()) {
			annotations.add(CirAnnotation.add_stmt(execution));
		}
		else {
			annotations.add(CirAnnotation.del_stmt(execution));
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_flows_error(CirFlowsError attribute,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution orig_target = attribute.get_original_flow().get_target();
		CirExecution muta_target = attribute.get_mutation_flow().get_target();
		Map<Boolean, Collection<CirExecution>> results =
						this.get_add_del_executions(orig_target, muta_target);
		for(Boolean result : results.keySet()) {
			for(CirExecution execution : results.get(result)) {
				if(result.booleanValue()) {
					annotations.add(CirAnnotation.add_stmt(execution));
				}
				else {
					annotations.add(CirAnnotation.del_stmt(execution));
				}
			}
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_traps_error(CirTrapsError attribute,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		execution = execution.get_graph().get_exit();
		annotations.add(CirAnnotation.trp_stmt(execution));
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_value_error(CirValueError attribute,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* declarations */
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression orig_expression = SymbolFactory.sym_expression(expression);
		SymbolExpression muta_expression = attribute.get_parameter();
		orig_expression = this.symbol_evaluate(orig_expression, context);
		try {
			muta_expression = this.symbol_evaluate(muta_expression, context);
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(attribute.get_execution().get_graph().get_exit()));
		}

		/* compared and return if equivalence */
		if(orig_expression.equals(muta_expression)) { return; /* invalid */ }

		/* generate set_xxxx when muta_expression is constant */
		if(muta_expression instanceof SymbolConstant) {
			SymbolConstant muta_constant = (SymbolConstant) muta_expression;
			if(CirMutation.is_boolean(expression)) {
				annotations.add(CirAnnotation.set_bool(expression, muta_constant.get_bool()));
			}
			else if(CirMutation.is_integer(expression)) {
				annotations.add(CirAnnotation.set_numb(expression, muta_constant.get_long()));
			}
			else if(CirMutation.is_numeric(expression)) {
				annotations.add(CirAnnotation.set_numb(expression, muta_constant.get_double()));
			}
			else if(CirMutation.is_pointer(expression)) {
				annotations.add(CirAnnotation.set_addr(expression, muta_constant.get_long()));
			}
			else {
				annotations.add(CirAnnotation.chg_auto(expression));
			}
		}
		/* generate chg_xxxx and mut_expr in non-constant case */
		else {
			annotations.add(CirAnnotation.mut_expr(expression, muta_expression));
			if(CirMutation.is_boolean(expression)) {
				annotations.add(CirAnnotation.chg_bool(expression));
			}
			else if(CirMutation.is_numeric(expression)) {
				annotations.add(CirAnnotation.chg_numb(expression));
			}
			else if(CirMutation.is_pointer(expression)) {
				annotations.add(CirAnnotation.chg_addr(expression));
			}
			else {
				annotations.add(CirAnnotation.chg_auto(expression));
			}
		}

		/* value domain compared using symbolic analysis */
		if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			SymbolExpression difference = SymbolFactory.arith_sub(
					expression.get_data_type(), muta_expression, orig_expression);
			difference = this.symbol_evaluate(difference, context);
			if(difference instanceof SymbolConstant) {
				if(CirMutation.is_integer(expression)) {
					if(((SymbolConstant) difference).get_long() > 0) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(((SymbolConstant) difference).get_long() < 0) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
				else if(CirMutation.is_pointer(expression)) {
					if(((SymbolConstant) difference).get_long() > 0) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(((SymbolConstant) difference).get_long() < 0) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
				else {
					if(((SymbolConstant) difference).get_double() > 0) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(((SymbolConstant) difference).get_double() < 0) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
			}
		}

		/* ext_scop or shk_scop generation when both are constant */
		if(orig_expression instanceof SymbolConstant && muta_expression instanceof SymbolConstant) {
			SymbolConstant orig_constant = (SymbolConstant) orig_expression;
			SymbolConstant muta_constant = (SymbolConstant) muta_expression;
			if(CirMutation.is_integer(expression) || CirMutation.is_pointer(expression)) {
				long orig_value = Math.abs(orig_constant.get_long());
				long muta_value = Math.abs(muta_constant.get_long());
				if(muta_value > orig_value) {
					annotations.add(CirAnnotation.ext_scop(expression));
				}
				else if(muta_value < orig_value) {
					annotations.add(CirAnnotation.shk_scop(expression));
				}
			}
			else if(CirMutation.is_numeric(expression)) {
				double orig_value = Math.abs(orig_constant.get_double());
				double muta_value = Math.abs(muta_constant.get_double());
				if(muta_value > orig_value) {
					annotations.add(CirAnnotation.ext_scop(expression));
				}
				else if(muta_value < orig_value) {
					annotations.add(CirAnnotation.shk_scop(expression));
				}
			}
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_refer_error(CirReferError attribute,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* declarations */
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression orig_expression = SymbolFactory.sym_expression(expression);
		SymbolExpression muta_expression = attribute.get_parameter();
		orig_expression = this.symbol_evaluate(orig_expression, context);
		try {
			muta_expression = this.symbol_evaluate(muta_expression, context);
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(attribute.get_execution().get_graph().get_exit()));
		}

		/* compared and return if equivalence */
		if(orig_expression.equals(muta_expression)) { return; /* invalid */ }

		/* generate set_xxxx when muta_expression is constant */
		if(muta_expression instanceof SymbolConstant) {
			SymbolConstant muta_constant = (SymbolConstant) muta_expression;
			if(CirMutation.is_boolean(expression)) {
				annotations.add(CirAnnotation.set_bool(expression, muta_constant.get_bool()));
			}
			else if(CirMutation.is_integer(expression)) {
				annotations.add(CirAnnotation.set_numb(expression, muta_constant.get_long()));
			}
			else if(CirMutation.is_numeric(expression)) {
				annotations.add(CirAnnotation.set_numb(expression, muta_constant.get_double()));
			}
			else if(CirMutation.is_pointer(expression)) {
				annotations.add(CirAnnotation.set_addr(expression, muta_constant.get_long()));
			}
			else {
				annotations.add(CirAnnotation.chg_auto(expression));
			}
		}
		/* generate chg_xxxx and mut_expr in non-constant case */
		else {
			annotations.add(CirAnnotation.mut_refr(expression, muta_expression));
			if(CirMutation.is_boolean(expression)) {
				annotations.add(CirAnnotation.chg_bool(expression));
			}
			else if(CirMutation.is_numeric(expression)) {
				annotations.add(CirAnnotation.chg_numb(expression));
			}
			else if(CirMutation.is_pointer(expression)) {
				annotations.add(CirAnnotation.chg_addr(expression));
			}
			else {
				annotations.add(CirAnnotation.chg_auto(expression));
			}
		}

		/* value domain compared using symbolic analysis */
		if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			SymbolExpression difference = SymbolFactory.arith_sub(
					expression.get_data_type(), muta_expression, orig_expression);
			difference = this.symbol_evaluate(difference, context);
			if(difference instanceof SymbolConstant) {
				if(CirMutation.is_integer(expression)) {
					if(((SymbolConstant) difference).get_long() > 0) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(((SymbolConstant) difference).get_long() < 0) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
				else if(CirMutation.is_pointer(expression)) {
					if(((SymbolConstant) difference).get_long() > 0) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(((SymbolConstant) difference).get_long() < 0) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
				else {
					if(((SymbolConstant) difference).get_double() > 0) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(((SymbolConstant) difference).get_double() < 0) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
			}
		}

		/* ext_scop or shk_scop generation when both are constant */
		if(orig_expression instanceof SymbolConstant && muta_expression instanceof SymbolConstant) {
			SymbolConstant orig_constant = (SymbolConstant) orig_expression;
			SymbolConstant muta_constant = (SymbolConstant) muta_expression;
			if(CirMutation.is_integer(expression) || CirMutation.is_pointer(expression)) {
				long orig_value = Math.abs(orig_constant.get_long());
				long muta_value = Math.abs(muta_constant.get_long());
				if(muta_value > orig_value) {
					annotations.add(CirAnnotation.ext_scop(expression));
				}
				else if(muta_value < orig_value) {
					annotations.add(CirAnnotation.shk_scop(expression));
				}
			}
			else if(CirMutation.is_numeric(expression)) {
				double orig_value = Math.abs(orig_constant.get_double());
				double muta_value = Math.abs(muta_constant.get_double());
				if(muta_value > orig_value) {
					annotations.add(CirAnnotation.ext_scop(expression));
				}
				else if(muta_value < orig_value) {
					annotations.add(CirAnnotation.shk_scop(expression));
				}
			}
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_state_error(CirStateError attribute,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression muta_expression = attribute.get_parameter();
		try {
			muta_expression = this.symbol_evaluate(muta_expression, context);
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(attribute.get_execution().get_graph().get_exit()));
		}
		annotations.add(CirAnnotation.mut_stat(expression, muta_expression));
	}

	/* abstract annotation summarization from concrete annotations */
	/**
	 * generate the abstract annotations from concrete annotations as evidence
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	protected void summarize_annotations(Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* divide the concrete annotations into type using rules */
		Map<CirAnnotationType, Collection<CirAnnotation>> inputs =
				new HashMap<>();
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			CirAnnotationType operator;
			switch(concrete_annotation.get_operator()) {
			case cov_stmt:
			case eva_expr:
			case mut_stmt:
			case mut_flow:
			case trp_stmt:
			case mut_expr:
			case mut_refr:
			case mut_stat:
			case chg_bool:
			case chg_numb:
			case chg_addr:
			case chg_auto:
			case set_bool:
			case set_numb:
			case set_addr:	operator = concrete_annotation.get_operator(); 	break;
			case inc_scop:
			case dec_scop:	operator = CirAnnotationType.inc_scop;			break;
			case ext_scop:
			case shk_scop:	operator = CirAnnotationType.ext_scop;			break;
			default:		throw new IllegalArgumentException("Unsupport: " + concrete_annotation);
			}
			if(!inputs.containsKey(operator))
				inputs.put(operator, new HashSet<CirAnnotation>());
			inputs.get(operator).add(concrete_annotation);
		}

		/* summarize based on annotation type based on rules */
		for(CirAnnotationType operator : inputs.keySet()) {
			concrete_annotations = inputs.get(operator);
			switch(operator) {
			case cov_stmt:	this.summarize_annotations_in_cov_stmt(concrete_annotations, abstract_annotations);	break;
			case eva_expr:	this.summarize_annotations_in_eva_expr(concrete_annotations, abstract_annotations);	break;
			case mut_stmt:	this.summarize_annotations_in_mut_stmt(concrete_annotations, abstract_annotations); break;
			case mut_flow:	this.summarize_annotations_in_mut_flow(concrete_annotations, abstract_annotations); break;
			case trp_stmt:	this.summarize_annotations_in_trp_stmt(concrete_annotations, abstract_annotations); break;
			case mut_expr:	this.summarize_annotations_in_mut_expr(concrete_annotations, abstract_annotations); break;
			case mut_refr:	this.summarize_annotations_in_mut_refr(concrete_annotations, abstract_annotations);	break;
			case mut_stat:	this.summarize_annotations_in_mut_stat(concrete_annotations, abstract_annotations);	break;
			case chg_bool:	this.summarize_annotations_in_chg_xxxx(concrete_annotations, abstract_annotations);	break;
			case chg_numb:	this.summarize_annotations_in_chg_xxxx(concrete_annotations, abstract_annotations);	break;
			case chg_addr:	this.summarize_annotations_in_chg_xxxx(concrete_annotations, abstract_annotations);	break;
			case chg_auto:	this.summarize_annotations_in_chg_xxxx(concrete_annotations, abstract_annotations);	break;
			case set_bool:	this.summarize_annotations_in_set_bool(concrete_annotations, abstract_annotations);	break;
			case set_numb:	this.summarize_annotations_in_set_numb(concrete_annotations, abstract_annotations);	break;
			case set_addr:	this.summarize_annotations_in_set_addr(concrete_annotations, abstract_annotations);	break;
			case inc_scop:	this.summarize_annotations_in_inc_scop(concrete_annotations, abstract_annotations);	break;
			case ext_scop:	this.summarize_annotations_in_ext_scop(concrete_annotations, abstract_annotations);	break;
			default:		break;
			}
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_cov_stmt(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_eva_expr(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_stmt(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_trp_stmt(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_flow(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(concrete_annotations.size() == 1) {
			abstract_annotations.addAll(concrete_annotations);
			CirAnnotation annotation = concrete_annotations.iterator().next();
			CirExecution orig_target = annotation.get_location().execution_of();
			CirExecution muta_target = (CirExecution) annotation.get_parameter().get_source();
			Map<Boolean, Collection<CirExecution>> results =
					this.get_add_del_executions(orig_target, muta_target);
			for(Boolean result : results.keySet()) {
				for(CirExecution execution : results.get(result)) {
					if(result.booleanValue()) {
						abstract_annotations.add(CirAnnotation.add_stmt(execution));
					}
					else {
						abstract_annotations.add(CirAnnotation.del_stmt(execution));
					}
				}
			}
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_expr(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(concrete_annotations.size() == 1) {
			abstract_annotations.addAll(concrete_annotations);
		}

		Set<CirExpression> expressions = new HashSet<>();
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expressions.add((CirExpression) concrete_annotation.get_location());
		}

		for(CirExpression expression : expressions) {
			if(CirMutation.is_boolean(expression)) {
				abstract_annotations.add(CirAnnotation.chg_bool(expression));
			}
			else if(CirMutation.is_numeric(expression)) {
				abstract_annotations.add(CirAnnotation.chg_numb(expression));
			}
			else if(CirMutation.is_pointer(expression)) {
				abstract_annotations.add(CirAnnotation.chg_addr(expression));
			}
			else {
				abstract_annotations.add(CirAnnotation.chg_auto(expression));
			}
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_refr(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(concrete_annotations.size() == 1) {
			abstract_annotations.addAll(concrete_annotations);
		}

		Set<CirExpression> expressions = new HashSet<>();
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expressions.add((CirExpression) concrete_annotation.get_location());
		}

		for(CirExpression expression : expressions) {
			if(CirMutation.is_boolean(expression)) {
				abstract_annotations.add(CirAnnotation.chg_bool(expression));
			}
			else if(CirMutation.is_numeric(expression)) {
				abstract_annotations.add(CirAnnotation.chg_numb(expression));
			}
			else if(CirMutation.is_pointer(expression)) {
				abstract_annotations.add(CirAnnotation.chg_addr(expression));
			}
			else {
				abstract_annotations.add(CirAnnotation.chg_auto(expression));
			}
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_stat(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(concrete_annotations.size() == 1) {
			abstract_annotations.addAll(concrete_annotations);
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_chg_xxxx(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_bool(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage analysis */
		Map<Boolean, Boolean> domains = new HashMap<>();
		CirExpression expression = null; SymbolConstant constant;
		domains.put(Boolean.TRUE, Boolean.FALSE);
		domains.put(Boolean.FALSE, Boolean.FALSE);
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			constant = (SymbolConstant) concrete_annotation.get_parameter();
			domains.put(constant.get_bool(), Boolean.TRUE);
		}

		/* domain-based summarization */
		if(domains.get(Boolean.TRUE)) {
			if(domains.get(Boolean.FALSE)) {
				abstract_annotations.add(CirAnnotation.chg_bool(expression));
			}
			else {
				abstract_annotations.add(CirAnnotation.set_bool(expression, true));
				abstract_annotations.add(CirAnnotation.chg_bool(expression));
			}
		}
		else {
			if(domains.get(Boolean.FALSE)) {
				abstract_annotations.add(CirAnnotation.set_bool(expression, false));
				abstract_annotations.add(CirAnnotation.chg_bool(expression));
			}
			else { /* none of errors is occurred (not reachable in this branch) */ }
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_numb(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage analysis */
		Map<CirAnnotationType, Boolean> domains = new HashMap<>();
		domains.put(CirAnnotationType.set_post, Boolean.FALSE);
		domains.put(CirAnnotationType.set_zero, Boolean.FALSE);
		domains.put(CirAnnotationType.set_negt, Boolean.FALSE);
		CirExpression expression = null; SymbolConstant constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			constant = (SymbolConstant) concrete_annotation.get_parameter();
			if(CirMutation.is_integer(expression)) {
				if(constant.get_long() > 0) {
					domains.put(CirAnnotationType.set_post, Boolean.TRUE);
				}
				else if(constant.get_long() < 0) {
					domains.put(CirAnnotationType.set_negt, Boolean.TRUE);
				}
				else {
					domains.put(CirAnnotationType.set_zero, Boolean.TRUE);
				}
			}
			else {
				if(constant.get_double() > 0) {
					domains.put(CirAnnotationType.set_post, Boolean.TRUE);
				}
				else if(constant.get_double() < 0) {
					domains.put(CirAnnotationType.set_negt, Boolean.TRUE);
				}
				else {
					domains.put(CirAnnotationType.set_zero, Boolean.TRUE);
				}
			}
		}

		/* domain-based summarization */
		if(domains.get(CirAnnotationType.set_post)) {
			if(domains.get(CirAnnotationType.set_negt)) {
				if(domains.get(CirAnnotationType.set_zero)) {						/* (+, -, 0) */
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
				}
				else {																/* (+, -) */
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
					abstract_annotations.add(CirAnnotation.set_nzro(expression));
				}
			}
			else {
				if(domains.get(CirAnnotationType.set_zero)) {						/* (+, 0) */
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
					abstract_annotations.add(CirAnnotation.set_nneg(expression));
				}
				else {																/* (+) */
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
					abstract_annotations.add(CirAnnotation.set_post(expression));
					abstract_annotations.add(CirAnnotation.set_nneg(expression));
					abstract_annotations.add(CirAnnotation.set_nzro(expression));
				}
			}
		}
		else {
			if(domains.get(CirAnnotationType.set_negt)) {
				if(domains.get(CirAnnotationType.set_zero)) {						/* (-, 0) */
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
					abstract_annotations.add(CirAnnotation.set_npos(expression));
				}
				else {																/* (-) */
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
					abstract_annotations.add(CirAnnotation.set_negt(expression));
					abstract_annotations.add(CirAnnotation.set_npos(expression));
					abstract_annotations.add(CirAnnotation.set_nzro(expression));
				}
			}
			else {
				if(domains.get(CirAnnotationType.set_zero)) {						/* (0) */
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
					abstract_annotations.add(CirAnnotation.set_zero(expression));
					abstract_annotations.add(CirAnnotation.set_npos(expression));
					abstract_annotations.add(CirAnnotation.set_nneg(expression));
				}
				else { /* none of error is generated according to none domains */ }
			}
		}

		/* unique constant value */
		if(concrete_annotations.size() == 1) {
			abstract_annotations.addAll(concrete_annotations);
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_addr(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage analysis */
		Map<CirAnnotationType, Boolean> domains = new HashMap<>();
		domains.put(CirAnnotationType.set_null, Boolean.FALSE);
		domains.put(CirAnnotationType.set_invp, Boolean.FALSE);
		CirExpression expression = null; SymbolConstant constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			constant = (SymbolConstant) concrete_annotation.get_parameter();
			if(constant.get_long() == 0) {
				domains.put(CirAnnotationType.set_null, Boolean.TRUE);
			}
			else {
				domains.put(CirAnnotationType.set_invp, Boolean.TRUE);
			}
		}

		/* domain-based summarization */
		if(domains.get(CirAnnotationType.set_null)) {
			if(domains.get(CirAnnotationType.set_invp)) {
				abstract_annotations.add(CirAnnotation.chg_addr(expression));
			}
			else {
				abstract_annotations.add(CirAnnotation.chg_addr(expression));
				abstract_annotations.add(CirAnnotation.set_null(expression));
			}
		}
		else {
			if(domains.get(CirAnnotationType.set_invp)) {
				abstract_annotations.add(CirAnnotation.chg_addr(expression));
				abstract_annotations.add(CirAnnotation.set_invp(expression));
			}
			else { /* none of error is generated according to none domains */ }
		}

		/* unique constant value */
		if(concrete_annotations.size() == 1) {
			abstract_annotations.addAll(concrete_annotations);
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_inc_scop(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage analysis */
		Map<CirAnnotationType, Boolean> domains = new HashMap<>();
		domains.put(CirAnnotationType.inc_scop, Boolean.FALSE);
		domains.put(CirAnnotationType.dec_scop, Boolean.FALSE);
		CirExpression expression = null;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			domains.put(concrete_annotation.get_operator(), Boolean.TRUE);
		}

		/* domain-based summarization */
		if(domains.get(CirAnnotationType.inc_scop)) {
			if(domains.get(CirAnnotationType.dec_scop)) { }
			else {
				abstract_annotations.add(CirAnnotation.inc_scop(expression));
			}
		}
		else {
			if(domains.get(CirAnnotationType.dec_scop)) {
				abstract_annotations.add(CirAnnotation.dec_scop(expression));
			}
			else { }
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_ext_scop(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage analysis */
		Map<CirAnnotationType, Boolean> domains = new HashMap<>();
		domains.put(CirAnnotationType.ext_scop, Boolean.FALSE);
		domains.put(CirAnnotationType.shk_scop, Boolean.FALSE);
		CirExpression expression = null;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			domains.put(concrete_annotation.get_operator(), Boolean.TRUE);
		}

		/* domain-based summarization */
		if(domains.get(CirAnnotationType.ext_scop)) {
			if(domains.get(CirAnnotationType.shk_scop)) { }
			else {
				abstract_annotations.add(CirAnnotation.ext_scop(expression));
			}
		}
		else {
			if(domains.get(CirAnnotationType.shk_scop)) {
				abstract_annotations.add(CirAnnotation.shk_scop(expression));
			}
			else { }
		}
	}
	
	/* evaluation methods */
	/**
	 * @param tree
	 * @throws Exception
	 */
	private void upon_evaluate(CirMutationTree tree) throws Exception {
		Set<CirMutationTreeNode> prev_nodes = new HashSet<CirMutationTreeNode>();
		for(CirMutationTreeEdge edge : tree.get_infection_edges()) {
			CirMutationTreeNode prev_node = edge.get_source().get_parent();
			while(prev_node != null) {
				prev_nodes.add(prev_node);
				prev_node = prev_node.get_parent();
			}
		}
		for(CirMutationTreeNode prev_node : prev_nodes) {
			prev_node.get_status().add(null);
		}
	}
	/**
	 * recursively evaluate the attribute in node using context information
	 * @param node
	 * @param context
	 * @throws Exception
	 */
	private void down_evaluate(CirMutationTreeNode node, SymbolProcess context) throws Exception {
		Boolean result = node.get_status().add(context);
		if(result == null || result.booleanValue()) {
			for(CirMutationTreeEdge edge : node.get_ou_edges()) {
				this.down_evaluate(edge.get_target(), context);
			}
		}
	}
	/**
	 * perform evaluation from infection-node to the entire of the tree edges
	 * @param tree
	 * @param context
	 * @throws Exception
	 */
	protected void evaluate_at(CirMutationTree tree, SymbolProcess context) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else {
			this.upon_evaluate(tree);
			for(CirMutationTreeEdge edge : tree.get_infection_edges()) {
				this.down_evaluate(edge.get_source(), context);
			}
		}
	}
	
}
