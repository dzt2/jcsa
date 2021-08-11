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
	 * collect the sequence of control-related flows into output in the specified path
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
	 * create a prefix-path reaching the target from its function entry; and 
	 * capture the sequence of execution flows corresponding with the control 
	 * flows in CFG of program under test.
	 * @param target
	 * @throws Exception
	 */
	private void get_execution_flows_in(CirExecution target, List<CirExecutionFlow> flows) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			CirExecution source = target.get_graph().get_entry();
			CirExecutionPath path = new CirExecutionPath(source);
			CirExecutionPathFinder.finder.vf_extend(path, target);
			this.collect_execution_flows_in(path, flows); 
		}
	}
	/**
	 * create a prefix-path reaching the target from its function entry; and
	 * capture the control-related flows in the sequence of generated path.
	 * @param target
	 * @param dependence_graph
	 * @param flows
	 * @throws Exception
	 */
	private void get_execution_flows_in(CirExecution target, CDependGraph 
			dependence_graph, List<CirExecutionFlow> flows) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(dependence_graph == null) {
			this.get_execution_flows_in(target, flows);
		}
		else {
			CirExecutionPath path = CirExecutionPathFinder.
					finder.dependence_path(dependence_graph, target);
			this.collect_execution_flows_in(path, flows);
		}
	}
	/**
	 * 
	 * @param target
	 * @param state_path
	 * @param flows
	 * @throws Exception
	 */
	private void get_execution_flows_in(CirExecution target, CStatePath 
			state_path, List<CirExecutionFlow> flows) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(state_path == null || state_path.size() == 0) {
			this.get_execution_flows_in(target, flows);
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
	 * create a reachability path with path constriants from root to the target with specified flows in the
	 * concrete path described using the control-related flows among it.
	 * @param tree
	 * @param target
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private CirMutationTreeNode	create_reachability_tree(CirMutationTree tree, 
			CirExecution target, List<CirExecutionFlow> flows) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(flows == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else {
			/* 1. connect from root to the previous constraint in target */
			CirMutationTreeNode node = tree.get_root(); CirAttribute attribute;
			for(CirExecutionFlow flow : flows) {
				attribute = this.get_flow_attribute(flow);
				node = node.link(CirMutationTreeType.precondition, 
						attribute, CirMutationTreeFlow.execute).get_target();
			}

			/* 2. linking to the target execution if needed */
			if(node.get_attribute().get_execution() != target) {
				attribute = CirAttribute.new_cover_count(target, 1);
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
	 * @return the set of edges from state infection constraint to initial error for each CirMutation in tree's mutant.
	 * @throws Exception
	 */
	protected Collection<CirMutationTreeEdge> construct_mutation_tree_in(CirMutationTree tree, Object context) throws Exception {
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
				List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
				if(context == null) {
					this.get_execution_flows_in(execution, flows);
				}
				else if(context instanceof CDependGraph) {
					this.get_execution_flows_in(execution, (CDependGraph) context, flows);
				}
				else if(context instanceof CStatePath) {
					this.get_execution_flows_in(execution, (CStatePath) context, flows);
				}
				else {
					throw new IllegalArgumentException("Invalid: " + context);
				}
				CirMutationTreeNode reach_node = this.create_reachability_tree(tree, execution, flows);
				for(CirMutation cir_mutation : maps.get(execution)) {
					infect_edges.add(this.create_infection_edge_on(reach_node, cir_mutation));
				}
			}

			/* propagation tree construction */
			for(CirMutationTreeEdge infect_edge : infect_edges) {
				this.create_propagation_tree(infect_edge.get_target());
			}
			return infect_edges;
		}
	}
	
}
