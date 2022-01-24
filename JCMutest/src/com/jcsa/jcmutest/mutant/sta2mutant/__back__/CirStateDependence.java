package com.jcsa.jcmutest.mutant.sta2mutant.__back__;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirBixorErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirBlockErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirDataErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirFlowsErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirIncreErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirLimitTimesState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirMConstrainState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirNConstrainState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirReachTimesState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirStoreClass;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirTrapsErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirValueErrorState;
import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
import com.jcsa.jcparse.flwa.depend.CDependType;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolUnaryExpression;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;


/**
 * It computes the dependence relations between abstract execution states.
 * 
 * @author yukimula
 *
 */
public class CirStateDependence {
	
	/* singleton mode */ /** constructor **/ private CirStateDependence() { }
	private static final CirStateDependence utils = new CirStateDependence();
	
	/* basic methods */
	/**
	 * It divides the sub-conditions in conjunctive form
	 * @param expression
	 * @param sub_conditions
	 * @throws Exception
	 */
	private void divide_conditions_in(SymbolExpression expression,
			Collection<SymbolExpression> sub_conditions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) { /* redundancies */ }
			else {
				sub_conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(operator == COperator.logic_and) {
				this.divide_conditions_in(loperand, sub_conditions);
				this.divide_conditions_in(roperand, sub_conditions);
			}
			else {
				sub_conditions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else {
			sub_conditions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * It generates the set of expressions directly subsumed by 
	 * @param expression
	 * @param sub_conditions
	 * @throws Exception
	 */
	private void subsuming_conditions(SymbolExpression expression,
			Collection<SymbolExpression> sub_conditions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) { /* USELESS */ }
			else {
				sub_conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.logic_and) {
				this.divide_conditions_in(expression, sub_conditions);
			}
			else if(operator == COperator.greater_tn) {
				sub_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				sub_conditions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			else if(operator == COperator.smaller_tn) {
				sub_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
				sub_conditions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			else if(operator == COperator.equal_with) {
				sub_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				sub_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
			}
			else {
				sub_conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
		}
		else {
			sub_conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is 0
	 */
	private boolean is_zero_constant(SymbolExpression expression) {
		if(expression instanceof SymbolConstant) {
			if(StateMutations.is_integer(expression.get_data_type())) {
				return ((SymbolConstant) expression).get_long() == 0;
			}
			else {
				return ((SymbolConstant) expression).get_double() == 0;
			}
		}
		else {
			return false;
		}
	}
	/* control dependence paths */
	/**
	 * @param execution		the CFG-node as target to be reached from prior flow
	 * @return				a control flow that directly controls this execution
	 * @throws Exception
	 */
	private CirExecutionFlow find_control_flow_in_nul(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			CirExecutionPath path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = path.get_iterator(true);
			while(iterator.hasNext()) {
				CirExecutionFlow flow = iterator.next().get_flow();
				if(flow.get_type() == CirExecutionFlowType.true_flow) {
					return flow;
				}
				else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
					return flow;
				}
				else if(flow.get_type() == CirExecutionFlowType.skip_flow) {
					return flow;
				}
				else if(flow.get_type() == CirExecutionFlowType.call_flow) {
					return flow;
				}
				else if(flow.get_type() == CirExecutionFlowType.retr_flow) {
					CirExecution wait = flow.get_target();
					CirExecution call = wait.get_graph().get_execution(wait.get_id() - 1);
					return call.get_ou_flow(0);
				}
				else { /* not a valid flow to be preserved in the model */ }
			}
			return null;	/* failed to find a flow to control the execution */
		}
	}
	/**
	 * @param context		the dependence graph to find control dependence flow
	 * @param execution		the CFG-node as target to be reached from prior flow
	 * @return				a control flow that directly controls this execution
	 * @throws Exception
	 */
	private CirExecutionFlow find_control_flow_in_dpg(CDependGraph context, CirExecution execution) throws Exception {
		CirInstanceGraph instance_graph = context.get_program_graph();
		if(instance_graph.has_instances_of(execution)) {
			for(CirInstanceNode instance_node : instance_graph.get_instances_of(execution)) {
				if(context.has_node(instance_node)) {
					CDependNode dependence_node = context.get_node(instance_node);
					for(CDependEdge dependence_edge : dependence_node.get_ou_edges()) {
						CDependType dependence_type = dependence_edge.get_type();
						if(dependence_type == CDependType.predicate_depend) {
							CDependPredicate elem = (CDependPredicate) dependence_edge.get_element();
							CirExecution if_execution = dependence_edge.get_target().get_execution();
							for(CirExecutionFlow ou_flow : if_execution.get_ou_flows()) {
								if(elem.get_predicate_value() && ou_flow.get_type() == CirExecutionFlowType.true_flow) {
									return ou_flow;
								}
								else if(!elem.get_predicate_value() && ou_flow.get_type() == CirExecutionFlowType.fals_flow) {
									return ou_flow;
								}
							}
							throw new IllegalArgumentException("Not found branch at " + if_execution.get_statement());
						}
						else if(dependence_type == CDependType.stmt_call_depend) {
							CirExecution call_node = dependence_edge.get_target().get_execution();
							return call_node.get_ou_flow(0);
						}
						else if(dependence_type == CDependType.stmt_exit_depend) {
							CirExecution wait_node = dependence_edge.get_target().get_execution();
							CirExecution call_node = wait_node.get_graph().get_execution(wait_node.get_id() - 1);
							return call_node.get_ou_flow(0);
						}
						else { /* not a control flow edge in this execution */ }
					}
				}
			}
		}
		return this.find_control_flow_in_nul(execution);
	}
	/**
	 * @param context		the execution path to find a control dependence flow
	 * @param execution		the CFG-node as target to be reached from prior flow
	 * @return				a control flow that directly controls this execution
	 * @throws Exception
	 */
	private CirExecutionFlow find_control_flow_in_exp(CirExecutionPath context, CirExecution execution) throws Exception {
		Stack<CirExecutionFlow> stack = new Stack<CirExecutionFlow>();
		boolean found = false;
		
		for(CirExecutionEdge edge : context.get_edges()) {
			CirExecutionFlow flow = edge.get_flow();
			switch(flow.get_type()) {
			case true_flow:
			case fals_flow:
			case call_flow:
			case skip_flow:
			case retr_flow:	stack.push(flow);
			default:		break;
			}
			if(flow.get_target() == execution) { 
				found = true;
				break;
			}
		}
		
		if(found) {
			if(!stack.isEmpty()) {
				CirExecutionFlow flow = stack.pop();
				if(flow.get_type() == CirExecutionFlowType.retr_flow) {
					CirExecution wait = flow.get_target();
					CirExecution call = wait.get_graph().get_execution(wait.get_id() - 1);
					return call.get_ou_flow(0);
				}
				else {
					return flow;
				}
			}
			return this.find_control_flow_in_nul(execution);
		}
		else {
			return this.find_control_flow_in_nul(execution);
		}
	}
	/**
	 * @param context		the execution path to find a control dependence flow
	 * @param execution		the CFG-node as target to be reached from prior flow
	 * @return				a control flow that directly controls this execution
	 * @throws Exception
	 */
	private CirExecutionFlow find_control_flow_in_stp(CStatePath context, CirExecution execution) throws Exception {
		Stack<CirExecutionFlow> stack = new Stack<CirExecutionFlow>();
		boolean found = false;
		
		for(CStateNode node : context.get_nodes()) {
			CirExecution point = node.get_execution();
			if(point.get_in_degree() == 1) {
				stack.push(point.get_in_flow(0));
			}
			if(point == execution) {
				found = true; break;
			}
			if(point.get_ou_degree() == 1) {
				stack.push(point.get_ou_flow(0));
			}
		}
		
		if(found) {
			while(!stack.isEmpty()) {
				CirExecutionFlow flow = stack.pop();
				if(flow.get_type() == CirExecutionFlowType.retr_flow) {
					CirExecution wait = flow.get_target();
					CirExecution call = wait.get_graph().get_execution(wait.get_id() - 1);
					return call.get_ou_flow(0);
				}
				else if(flow.get_type() == CirExecutionFlowType.true_flow
						|| flow.get_type() == CirExecutionFlowType.fals_flow
						|| flow.get_type() == CirExecutionFlowType.skip_flow
						|| flow.get_type() == CirExecutionFlowType.call_flow) {
					return flow;
				}
				else { /* ignore the invalid flows that do not control */ }
			}
			return this.find_control_flow_in_nul(execution);
		}
		else {
			return this.find_control_flow_in_nul(execution);
		}
	}
	/**
	 * @param context		CDependGraph | CirExecutionPath | CStatePath | null
	 * @param execution		the CFG-node as target to be reached from prior flow
	 * @return				a control flow that directly controls this execution
	 * @throws Exception
	 */
	private CirExecutionFlow find_control_flow(Object context, CirExecution execution) throws Exception {
		if(context == null) {
			return this.find_control_flow_in_nul(execution);
		}
		else if(context instanceof CDependGraph) {
			return this.find_control_flow_in_dpg((CDependGraph) context, execution);
		}
		else if(context instanceof CirExecutionPath) {
			return this.find_control_flow_in_exp((CirExecutionPath) context, execution);
		}
		else if(context instanceof CStatePath) {
			return this.find_control_flow_in_stp((CStatePath) context, execution);
		}
		else {
			return this.find_control_flow_in_nul(execution);
		}
	}
	
	/* dependence inference in local C-intermediate representative point */
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	public static void local_depend(CirAbstractState state, Collection<CirAbstractState> outputs) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			outputs.clear();
			Set<CirAbstractState> buffer = new HashSet<CirAbstractState>();
			utils.ldep_iter(state.normalize(), buffer);
			for(CirAbstractState output : buffer) {
				outputs.add(output.normalize());
			}
		}
	}
	/**
	 * It computes the dependence from the input state in the local context
	 * @param state		the abstract state to depend on the others
	 * @param outputs	to preserve the set of states on which the input state depends
	 * @throws Exception
	 */
	private void ldep_iter(CirAbstractState state, Collection<CirAbstractState> outputs) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(state instanceof CirReachTimesState) {		/* cov|eva */
			this.ldep_reach_times((CirReachTimesState) state, outputs);
		}
		else if(state instanceof CirLimitTimesState) {		/* no more */
			this.ldep_limit_times((CirLimitTimesState) state, outputs);
		}
		else if(state instanceof CirNConstrainState) {
			this.ldep_m_constrain((CirMConstrainState) state, outputs);
		}
		else if(state instanceof CirMConstrainState) {
			this.ldep_n_constrain((CirNConstrainState) state, outputs);
		}
		else if(state instanceof CirBlockErrorState) {
			this.ldep_block_error((CirBlockErrorState) state, outputs);
		}
		else if(state instanceof CirFlowsErrorState) {
			this.ldep_flows_error((CirFlowsErrorState) state, outputs);
		}
		else if(state instanceof CirTrapsErrorState) {
			this.ldep_traps_error((CirTrapsErrorState) state, outputs);
		}
		else if(state instanceof CirValueErrorState) {
			this.ldep_value_error((CirValueErrorState) state, outputs);
		}
		else if(state instanceof CirIncreErrorState) {
			this.ldep_incre_error((CirIncreErrorState) state, outputs);
		}
		else if(state instanceof CirBixorErrorState) {
			this.ldep_bixor_error((CirBixorErrorState) state, outputs);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + state);
		}
	}
	/* conditions state in local context dependence */
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_limit_times(CirLimitTimesState state, Collection<CirAbstractState> outputs) throws Exception { }
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_reach_times(CirReachTimesState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations */
		CirExecution execution = state.get_execution();
		int mtimes = state.get_minimal_times(), times = 1;
		while(times < mtimes) { times = times * 2; }
		times = times / 2;
		
		/* coverage --> TRUE */
		if(times > 1) {
			outputs.add(CirAbstractState.cov_time(execution, times));
		}
		else {
			outputs.add(CirAbstractState.eva_cond(execution, Boolean.TRUE, true));
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_m_constrain(CirMConstrainState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations */
		CirExecution execution = state.get_execution();
		SymbolExpression condition = state.get_condition();
		
		/* must_condition --> need_condition */
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
		
		/* subsumed conditions set from this */
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.subsuming_conditions(condition, conditions);
		for(SymbolExpression sub_condition : conditions) {
			outputs.add(CirAbstractState.mus_cond(execution, sub_condition, true));
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_n_constrain(CirNConstrainState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations */
		CirExecution execution = state.get_execution();
		SymbolExpression condition = state.get_condition();
		
		/* subsumed conditions set from this */
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.subsuming_conditions(condition, conditions);
		for(SymbolExpression sub_condition : conditions) {
			outputs.add(CirAbstractState.eva_cond(execution, sub_condition, true));
		}
	}
	/* path-error state in local context dependence */
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_block_error(CirBlockErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		if(state.is_original_executed()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_flows_error(CirFlowsErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations */
		CirExecutionPath orig_path = CirExecutionPathFinder.finder.df_extend(state.get_orig_target());
		CirExecutionPath muta_path = CirExecutionPathFinder.finder.df_extend(state.get_muta_target());
		Set<CirExecution> origset = new HashSet<CirExecution>(), mutaset = new HashSet<CirExecution>();
		
		/* collect points */
		for(CirExecutionEdge edge : orig_path.get_edges()) { origset.add(edge.get_source()); }
		for(CirExecutionEdge edge : muta_path.get_edges()) { mutaset.add(edge.get_source()); }
		origset.add(orig_path.get_target()); mutaset.add(muta_path.get_target());
		
		/* collect the common execution points */
		Collection<CirExecution> commons = new HashSet<CirExecution>();
		for(CirExecution execution : origset) {
			if(mutaset.contains(execution)) {
				commons.add(execution);
			}
		}
		origset.removeAll(commons); mutaset.removeAll(commons);
		
		/* subsuming block errors from flows */
		for(CirExecution execution : origset) {
			outputs.add(CirAbstractState.set_stmt(execution, false));
		}
		for(CirExecution execution : mutaset) {
			outputs.add(CirAbstractState.set_stmt(execution, true));
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_traps_error(CirTrapsErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		outputs.add(CirAbstractState.cov_time(execution, 1));
	}
	/* data-error state in local context dependence */
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_value_error(CirValueErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations and data getters */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		SymbolExpression orig_value = state.get_orig_value();
		SymbolExpression muta_value = state.get_muta_value();
		CirStoreClass store_type = state.get_store_type();
		SymbolExpression store_key = state.get_store_key();
		
		/* CASE-1. Trapping Occurs in the case */
		if(StateMutations.is_trap_value(muta_value)) {
			outputs.add(CirAbstractState.set_trap(execution));
		}
		/* CASE-2. Equivalent Mutation removed */
		else if(muta_value.equals(orig_value)) {
			outputs.add(CirAbstractState.eva_cond(execution, Boolean.FALSE, true));
		}
		/* CASE-3. Boolean Value Error Category */
		else if(StateMutations.is_boolean(expression)) {
			this.ldep_vbool_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* CASE-4. Integer Value Error Category */
		else if(StateMutations.is_integer(expression)) {
			this.ldep_vnumb_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* CASE-5. Double Value Error Category */
		else if(StateMutations.is_doubles(expression)) {
			this.ldep_vreal_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* CASE-6. Address Value Error Category */
		else if(StateMutations.is_address(expression)) {
			this.ldep_vaddr_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* CASE-7. Otherwise Value Error Class */
		else {
			this.ldep_vauto_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param orig_value
	 * @param muta_value
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_vbool_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* to generate mutation values */
		Set<SymbolExpression> muta_values = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			if(((SymbolConstant) muta_value).get_bool()) {
				muta_values.add(StateMutations.true_value);
			}
			else {
				muta_values.add(StateMutations.fals_value);
			}
		}
		else if(muta_value instanceof SymbolUnaryExpression) {
			SymbolExpression operand = ((SymbolUnaryExpression) muta_value).get_operand();
			COperator operator = ((SymbolUnaryExpression) muta_value).get_operator().get_operator();
			if(operator == COperator.address_of) {
				muta_values.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
			else if(operator == COperator.negative) {
				muta_values.add(operand);
			}
			else {
				muta_values.add(StateMutations.bool_value);
			}
		}
		else if(muta_value instanceof SymbolBinaryExpression) {
			muta_values.add(StateMutations.bool_value);
		}
		else if(muta_value == StateMutations.bool_value) { }
		else {
			muta_values.add(StateMutations.bool_value);
		}
		
		/* generate depended abstract states */
		if(muta_values.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression muvalue : muta_values) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.set_vdef(expression, store_key, muvalue));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, muvalue));
				}
			}
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param orig_value
	 * @param muta_value
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_vnumb_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* collect the set of depended values from muta_value */
		Set<SymbolExpression> muvalues = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			long constant = ((SymbolConstant) muta_value).get_long();
			if(constant > 0) {
				muvalues.add(StateMutations.post_value);
			}
			else if(constant < 0) {
				muvalues.add(StateMutations.negt_value);
			}
			else {
				muvalues.add(StateMutations.zero_value);
			}
		}
		else if(muta_value == StateMutations.post_value) {
			muvalues.add(StateMutations.nzro_value);
			muvalues.add(StateMutations.nneg_value);
		}
		else if(muta_value == StateMutations.zero_value) {
			muvalues.add(StateMutations.npos_value);
			muvalues.add(StateMutations.nneg_value);
		}
		else if(muta_value == StateMutations.negt_value) {
			muvalues.add(StateMutations.npos_value);
			muvalues.add(StateMutations.nzro_value);
		}
		else if(muta_value == StateMutations.npos_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.nneg_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.nzro_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.numb_value) { }
		else {
			muvalues.add(StateMutations.numb_value);
		}
		
		/* generate the next states being depended */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression muvalue : muvalues) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.set_vdef(expression, store_key, muvalue));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, muvalue));
				}
			}
		}
		
		/* difference error state generation */
		SymbolExpression difference;
		difference = SymbolFactory.arith_sub(expression.get_data_type(), muta_value, orig_value);
		difference = StateMutations.evaluate(difference);
		if(difference instanceof SymbolConstant) {
			if(((SymbolConstant) difference).get_long() == 0) {
				outputs.add(CirAbstractState.eva_cond(execution, Boolean.FALSE, true));
			}
			else if(store_type == CirStoreClass.vdef) {
				outputs.add(CirAbstractState.inc_vdef(expression, store_key, difference));
			}
			else {
				outputs.add(CirAbstractState.inc_expr(expression, difference));
			}
		}
		
		/* bit-differentiate state generation */
		SymbolExpression xor_differ;
		xor_differ = SymbolFactory.bitws_xor(expression.get_data_type(), muta_value, orig_value);
		xor_differ = StateMutations.evaluate(xor_differ);
		if(xor_differ instanceof SymbolConstant) {
			if(((SymbolConstant) xor_differ).get_long() == 0) {
				outputs.add(CirAbstractState.eva_cond(execution, Boolean.FALSE, true));
			}
			else if(store_type == CirStoreClass.vdef) {
				outputs.add(CirAbstractState.xor_vdef(expression, store_key, xor_differ));
			}
			else {
				outputs.add(CirAbstractState.xor_expr(expression, xor_differ));
			}
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param orig_value
	 * @param muta_value
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_vreal_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* collect the set of depended values from muta_value */
		Set<SymbolExpression> muvalues = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			double constant = ((SymbolConstant) muta_value).get_double();
			if(constant > 0) {
				muvalues.add(StateMutations.post_value);
			}
			else if(constant < 0) {
				muvalues.add(StateMutations.negt_value);
			}
			else {
				muvalues.add(StateMutations.zero_value);
			}
		}
		else if(muta_value == StateMutations.post_value) {
			muvalues.add(StateMutations.nzro_value);
			muvalues.add(StateMutations.nneg_value);
		}
		else if(muta_value == StateMutations.zero_value) {
			muvalues.add(StateMutations.npos_value);
			muvalues.add(StateMutations.nneg_value);
		}
		else if(muta_value == StateMutations.negt_value) {
			muvalues.add(StateMutations.npos_value);
			muvalues.add(StateMutations.nzro_value);
		}
		else if(muta_value == StateMutations.npos_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.nneg_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.nzro_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.numb_value) { }
		else {
			muvalues.add(StateMutations.numb_value);
		}
		
		/* generate the next states being depended */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression muvalue : muvalues) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.set_vdef(expression, store_key, muvalue));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, muvalue));
				}
			}
		}
		
		/* difference error state generation */
		SymbolExpression difference;
		difference = SymbolFactory.arith_sub(expression.get_data_type(), muta_value, orig_value);
		difference = StateMutations.evaluate(difference);
		if(difference instanceof SymbolConstant) {
			if(((SymbolConstant) difference).get_double() == 0.0) {
				outputs.add(CirAbstractState.eva_cond(execution, Boolean.FALSE, true));
			}
			else if(store_type == CirStoreClass.vdef) {
				outputs.add(CirAbstractState.inc_vdef(expression, store_key, difference));
			}
			else {
				outputs.add(CirAbstractState.inc_expr(expression, difference));
			}
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param orig_value
	 * @param muta_value
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_vaddr_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* collect the set of depended values from muta_value */
		Set<SymbolExpression> muvalues = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			long constant = ((SymbolConstant) muta_value).get_long();
			if(constant == 0) {
				muvalues.add(StateMutations.null_value);
			}
			else {
				muvalues.add(StateMutations.nnul_value);
			}
		}
		else if(muta_value == StateMutations.null_value) {
			muvalues.add(StateMutations.addr_value);
		}
		else if(muta_value == StateMutations.nnul_value) {
			muvalues.add(StateMutations.addr_value);
		}
		else if(muta_value == StateMutations.addr_value) { }
		else {
			muvalues.add(StateMutations.nnul_value);
		}
		
		/* generate mutation value error state */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression muvalue : muvalues) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.set_vdef(expression, store_key, muvalue));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, muvalue));
				}
			}
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param orig_value
	 * @param muta_value
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_vauto_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		outputs.add(CirAbstractState.cov_time(execution, 1));
	}
	/* incremental error in local context dependence analysis */
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_incre_error(CirIncreErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations and data getters */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		SymbolExpression base_value = state.get_base_value();
		SymbolExpression difference = state.get_difference();
		CirStoreClass store_type = state.get_store_type();
		SymbolExpression store_key = state.get_store_key();
		
		/* CASE-1. TRAPPING OCCURS */
		if(StateMutations.is_trap_value(difference)) {
			outputs.add(CirAbstractState.set_trap(execution));
		}
		/* CASE-2. ZERO DIFFERENCE IS EQUIVALENT */
		else if(this.is_zero_constant(difference)) {
			outputs.add(CirAbstractState.eva_cond(execution, Boolean.FALSE, true));
		}
		/* CASE-3. INTEGER DIFFERENCE ERROR HERE */
		else if(StateMutations.is_integer(expression)) {
			this.ldep_inumb_error(execution, expression, store_type, store_key, base_value, difference, outputs);
		}
		/* CASE-4. REAL DIFFERENCE ERROR GENERATE */
		else if(StateMutations.is_doubles(expression)) {
			this.ldep_ireal_error(execution, expression, store_type, store_key, base_value, difference, outputs);
		}
		/* CASE-5. ADDRESS DIFFERENCE ERROR HERE */
		else if(StateMutations.is_address(expression)) {
			this.ldep_iaddr_error(execution, expression, store_type, store_key, base_value, difference, outputs);
		}
		/* CASE-6. OTHERWISE, INVALID DATA TYPES */
		else {
			throw new IllegalArgumentException("Unsupport: " + state.toString());
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param base_value
	 * @param difference
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_inumb_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression base_value, SymbolExpression difference,
			Collection<CirAbstractState> outputs) throws Exception {
		/* collect the difference values next */
		Set<SymbolExpression> dvalues = new HashSet<SymbolExpression>();
		if(difference instanceof SymbolConstant) {
			long constant = ((SymbolConstant) difference).get_long();
			if(constant > 0) {
				dvalues.add(StateMutations.post_value);
			}
			else {
				dvalues.add(StateMutations.negt_value);
			}
		}
		else if(difference == StateMutations.post_value) {
			dvalues.add(StateMutations.nzro_value);
		}
		else if(difference == StateMutations.negt_value) {
			dvalues.add(StateMutations.nzro_value);
		}
		else if(difference == StateMutations.nzro_value) { }
		else {
			dvalues.add(StateMutations.nzro_value);
		}
		
		/* generate sub-increment error here */
		if(dvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression dvalue : dvalues) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.inc_vdef(expression, store_key, dvalue));
				}
				else {
					outputs.add(CirAbstractState.inc_expr(expression, dvalue));
				}
			}
		}
		
		/* set expression error extensions */
		SymbolExpression muta_value = SymbolFactory.arith_add(expression.get_data_type(), base_value, difference);
		muta_value = StateMutations.evaluate(muta_value);
		if(store_type == CirStoreClass.vdef) {
			outputs.add(CirAbstractState.set_vdef(expression, store_key, muta_value));
		}
		else {
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param base_value
	 * @param difference
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_ireal_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression base_value, SymbolExpression difference,
			Collection<CirAbstractState> outputs) throws Exception {
		/* collect the difference values next */
		Set<SymbolExpression> dvalues = new HashSet<SymbolExpression>();
		if(difference instanceof SymbolConstant) {
			double constant = ((SymbolConstant) difference).get_double();
			if(constant > 0) {
				dvalues.add(StateMutations.post_value);
			}
			else {
				dvalues.add(StateMutations.negt_value);
			}
		}
		else if(difference == StateMutations.post_value) {
			dvalues.add(StateMutations.nzro_value);
		}
		else if(difference == StateMutations.negt_value) {
			dvalues.add(StateMutations.nzro_value);
		}
		else if(difference == StateMutations.nzro_value) { }
		else {
			dvalues.add(StateMutations.nzro_value);
		}
		
		/* generate sub-increment error here */
		if(dvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression dvalue : dvalues) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.inc_vdef(expression, store_key, dvalue));
				}
				else {
					outputs.add(CirAbstractState.inc_expr(expression, dvalue));
				}
			}
		}
		
		/* set expression error extensions */
		SymbolExpression muta_value = SymbolFactory.arith_add(expression.get_data_type(), base_value, difference);
		muta_value = StateMutations.evaluate(muta_value);
		if(store_type == CirStoreClass.vdef) {
			outputs.add(CirAbstractState.set_vdef(expression, store_key, muta_value));
		}
		else {
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param base_value
	 * @param difference
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_iaddr_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key,
			SymbolExpression base_value, SymbolExpression difference,
			Collection<CirAbstractState> outputs) throws Exception {
		/* collect the difference values next */
		Set<SymbolExpression> dvalues = new HashSet<SymbolExpression>();
		if(difference instanceof SymbolConstant) {
			long constant = ((SymbolConstant) difference).get_long();
			if(constant > 0) {
				dvalues.add(StateMutations.post_value);
			}
			else {
				dvalues.add(StateMutations.negt_value);
			}
		}
		else if(difference == StateMutations.post_value) {
			dvalues.add(StateMutations.nzro_value);
		}
		else if(difference == StateMutations.negt_value) {
			dvalues.add(StateMutations.nzro_value);
		}
		else if(difference == StateMutations.nzro_value) { }
		else {
			dvalues.add(StateMutations.nzro_value);
		}
		
		/* generate sub-increment error here */
		if(dvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression dvalue : dvalues) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.inc_vdef(expression, store_key, dvalue));
				}
				else {
					outputs.add(CirAbstractState.inc_expr(expression, dvalue));
				}
			}
		}
		
		/* set expression error extensions */
		SymbolExpression muta_value = SymbolFactory.arith_add(expression.get_data_type(), base_value, difference);
		muta_value = StateMutations.evaluate(muta_value);
		if(store_type == CirStoreClass.vdef) {
			outputs.add(CirAbstractState.set_vdef(expression, store_key, muta_value));
		}
		else {
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
	}
	/* bit-xor error in the local context dependence analysis */
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ldep_bixor_error(CirBixorErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations and data getters */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		SymbolExpression base_value = state.get_base_value();
		SymbolExpression difference = state.get_difference();
		CirStoreClass store_type = state.get_store_type();
		SymbolExpression store_key = state.get_store_key();
		
		/* CASE-1. TRAPPING OCCURS */
		if(StateMutations.is_trap_value(difference)) {
			outputs.add(CirAbstractState.set_trap(execution));
		}
		/* CASE-2. ZERO DIFFERENCE IS EQUIVALENT */
		else if(this.is_zero_constant(difference)) {
			outputs.add(CirAbstractState.eva_cond(execution, Boolean.FALSE, true));
		}
		/* analysis following */
		else {
			Set<SymbolExpression> xvalues = new HashSet<SymbolExpression>();
			if(difference instanceof SymbolConstant) {
				long constant = ((SymbolConstant) difference).get_long();
				if(constant > 0) {
					xvalues.add(StateMutations.post_value);
				}
				else {
					xvalues.add(StateMutations.negt_value);
				}
			}
			else if(difference == StateMutations.post_value) {
				xvalues.add(StateMutations.nzro_value);
			}
			else if(difference == StateMutations.negt_value) {
				xvalues.add(StateMutations.nzro_value);
			}
			else if(difference == StateMutations.nzro_value) {}
			else {
				xvalues.add(StateMutations.nzro_value);
			}
			
			/* generate the error of xor in extension */
			if(xvalues.isEmpty()) {
				outputs.add(CirAbstractState.cov_time(execution, 1));
			}
			else {
				for(SymbolExpression xvalue : xvalues) {
					if(store_type == CirStoreClass.vdef) {
						outputs.add(CirAbstractState.xor_vdef(expression, store_key, xvalue));
					}
					else {
						outputs.add(CirAbstractState.xor_expr(expression, xvalue));
					}
				}
			}
			
			/* set expression error extensions from */
			SymbolExpression muta_value = SymbolFactory.bitws_xor(
					expression.get_data_type(), base_value, difference);
			muta_value = StateMutations.evaluate(muta_value);
			if(store_type == CirStoreClass.vdef) {
				outputs.add(CirAbstractState.set_vdef(expression, store_key, muta_value));
			}
			else {
				outputs.add(CirAbstractState.set_expr(expression, muta_value));
			}
		}
	}
	
	/* dependence analysis across the local C-intermediate location */
	/**
	 * It implements the direct dependence analysis using global analysis across statement
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	public static void global_depend(CirAbstractState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			outputs.clear();
			Set<CirAbstractState> buffer = new HashSet<CirAbstractState>();
			utils.gdep_iter(state.normalize(), buffer, context);
			for(CirAbstractState output : buffer) {
				outputs.add(output.normalize());
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void gdep_iter(CirAbstractState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(state instanceof CirReachTimesState) {
			this.gdep_reach_times((CirReachTimesState) state, outputs, context);
		}
		else if(state instanceof CirLimitTimesState) {
			this.gdep_limit_times((CirLimitTimesState) state, outputs, context);
		}
		else if(state instanceof CirMConstrainState) {
			this.gdep_m_constrain((CirMConstrainState) state, outputs, context);
		}
		else if(state instanceof CirNConstrainState) {
			this.gdep_n_constrain((CirNConstrainState) state, outputs, context);
		}
		else if(state instanceof CirBlockErrorState) {
			this.gdep_block_error((CirBlockErrorState) state, outputs, context);
		}
		else if(state instanceof CirFlowsErrorState) {
			this.gdep_flows_error((CirFlowsErrorState) state, outputs, context);
		}
		else if(state instanceof CirTrapsErrorState) {
			this.gdep_traps_error((CirTrapsErrorState) state, outputs, context);
		}
		else if(state instanceof CirValueErrorState) {
			CirLDataStatePropagate.propagate(context, (CirDataErrorState) state, outputs);
		}
		else if(state instanceof CirIncreErrorState) {
			CirLDataStatePropagate.propagate(context, (CirDataErrorState) state, outputs);
		}
		else if(state instanceof CirBixorErrorState) {
			CirLDataStatePropagate.propagate(context, (CirDataErrorState) state, outputs);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + state);
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void gdep_reach_times(CirReachTimesState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		CirExecution execution = state.get_execution();
		int minimal_times = state.get_minimal_times();
		CirExecutionFlow flow = this.find_control_flow(context, execution);
		if(minimal_times == 1 && flow != null) {
			CirExecution if_execution = flow.get_source(); 
			CirStatement statement = if_execution.get_statement();
			CirExpression expression; execution = if_execution;
			if(statement instanceof CirIfStatement) {
				expression = ((CirIfStatement) statement).get_condition();
			}
			else if(statement instanceof CirCaseStatement) {
				expression = ((CirCaseStatement) statement).get_condition();
			}
			else {
				expression = null;
			}
			
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				outputs.add(CirAbstractState.eva_cond(if_execution, expression, true));
			}
			else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
				outputs.add(CirAbstractState.eva_cond(if_execution, expression, false));
			}
			else { outputs.add(CirAbstractState.cov_time(execution, 1)); }
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void gdep_limit_times(CirLimitTimesState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		CirExecution execution = state.get_execution();
		int maximal_times = state.get_maximal_times();
		CirExecutionFlow flow = this.find_control_flow(context, execution);
		
		if(maximal_times <= 0 && flow != null) {
			CirExecution if_execution = flow.get_source(); 
			CirStatement statement = if_execution.get_statement();
			CirExpression expression; execution = if_execution;
			if(statement instanceof CirIfStatement) {
				expression = ((CirIfStatement) statement).get_condition();
			}
			else if(statement instanceof CirCaseStatement) {
				expression = ((CirCaseStatement) statement).get_condition();
			}
			else {
				expression = null;
			}
			
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				outputs.add(CirAbstractState.eva_cond(if_execution, expression, false));
			}
			else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
				outputs.add(CirAbstractState.eva_cond(if_execution, expression, true));
			}
			else { outputs.add(CirAbstractState.cov_time(execution, 1)); }
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void gdep_m_constrain(CirMConstrainState state, Collection<CirAbstractState> outputs, Object context) throws Exception { }
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void gdep_n_constrain(CirNConstrainState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		CirExecution execution = state.get_execution();
		SymbolExpression condition = state.get_condition();
		CirExecutionFlow flow = this.find_control_flow(context, execution);
		
		if(flow != null && condition instanceof SymbolConstant) {
			CirExecution if_execution = flow.get_source(); 
			CirStatement statement = if_execution.get_statement();
			CirExpression expression; execution = if_execution;
			if(statement instanceof CirIfStatement) {
				expression = ((CirIfStatement) statement).get_condition();
			}
			else if(statement instanceof CirCaseStatement) {
				expression = ((CirCaseStatement) statement).get_condition();
			}
			else {
				expression = null;
			}
			
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				outputs.add(CirAbstractState.eva_cond(if_execution, expression, true));
			}
			else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
				outputs.add(CirAbstractState.eva_cond(if_execution, expression, false));
			}
			else { outputs.add(CirAbstractState.cov_time(execution, 1)); }
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void gdep_block_error(CirBlockErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception { }
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void gdep_flows_error(CirFlowsErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception { }
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void gdep_traps_error(CirTrapsErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception { }
	
}
