package com.jcsa.jcmutest.mutant.sta2mutant.base;

import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * This implements the normalization of CirAbstractState.
 * 
 * @author yukimula
 *
 */
final class CirStateNormalizer {
	
	/* singleton mode */  /** constructor **/  	private CirStateNormalizer() { }
	private static final CirStateNormalizer normalizer = new CirStateNormalizer();
	
	/* normalization */
	/**
	 * @param state
	 * @param context
	 * @return the normalized form of input state evaluated in the given context
	 * @throws Exception
	 */
	protected static CirAbstractState normalize(CirAbstractState state, SymbolProcess context) throws Exception {
		return normalizer.norm(state, context);
	}
	/**
	 * @param state
	 * @param context
	 * @return the normalized form of input state evaluated in the given context
	 * @throws Exception
	 */
	private CirAbstractState norm(CirAbstractState state, SymbolProcess context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state as null");
		}
		else if(state instanceof CirLimitTimesState) {
			return this.norm_limit_times((CirLimitTimesState) state, context);
		}
		else if(state instanceof CirReachTimesState) {
			return this.norm_reach_times((CirReachTimesState) state, context);
		}
		else if(state instanceof CirMConstrainState) {
			return this.norm_m_constrain((CirMConstrainState) state, context);
		}
		else if(state instanceof CirNConstrainState) {
			return this.norm_n_constrain((CirNConstrainState) state, context);
		}
		else if(state instanceof CirBlockErrorState) {
			return this.norm_block_error((CirBlockErrorState) state, context);
		}
		else if(state instanceof CirFlowsErrorState) {
			return this.norm_flows_error((CirFlowsErrorState) state, context);
		}
		else if(state instanceof CirTrapsErrorState) {
			return this.norm_traps_error((CirTrapsErrorState) state, context);
		}
		else if(state instanceof CirValueErrorState) {
			return this.norm_value_error((CirValueErrorState) state, context);
		}
		else if(state instanceof CirIncreErrorState) {
			return this.norm_incre_error((CirIncreErrorState) state, context);
		}
		else if(state instanceof CirBixorErrorState) {
			return this.norm_bixor_error((CirBixorErrorState) state, context);
		}
		else {
			throw new IllegalArgumentException(state.getClass().getSimpleName());
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return lim_time(entry, max_times)
	 * @throws Exception
	 */
	private CirAbstractState norm_limit_times(CirLimitTimesState state, SymbolProcess context) throws Exception {
		CirExecution target = state.get_execution();
		CirExecutionPath prev_path = StateMutations.inblock_prev_path(target);
		int times = state.get_maximal_times();
		return CirAbstractState.lim_time(prev_path.get_source(), times);
	}
	/**
	 * @param state
	 * @param context
	 * @return cov_time(entry, min_times)
	 * @throws Exception
	 */
	private CirAbstractState norm_reach_times(CirReachTimesState state, SymbolProcess context) throws Exception {
		CirExecution target = state.get_execution();
		CirExecutionPath prev_path = StateMutations.inblock_prev_path(target);
		int times = state.get_minimal_times();
		return CirAbstractState.cov_time(prev_path.get_source(), times);
	}
	/**
	 * @param state
	 * @param context
	 * @return 
	 * @throws Exception
	 */
	private CirAbstractState norm_m_constrain(CirMConstrainState state, SymbolProcess context) throws Exception {
		SymbolExpression condition = state.get_condition();
		condition = StateMutations.evaluate(condition, context);
		if(StateMutations.is_trap_value(condition)) {
			condition = SymbolFactory.sym_constant(Boolean.TRUE);
		}
		
		CirExecutionPath prev_path = StateMutations.inblock_prev_path(state.get_execution());
		CirExecution check_point = StateMutations.find_checkpoint(prev_path, condition);
		return CirAbstractState.mus_cond(check_point, condition, true);
	}
	/**
	 * @param state
	 * @param context
	 * @return 
	 * @throws Exception
	 */
	private CirAbstractState norm_n_constrain(CirNConstrainState state, SymbolProcess context) throws Exception {
		SymbolExpression condition = state.get_condition();
		condition = StateMutations.evaluate(condition, context);
		if(StateMutations.is_trap_value(condition)) {
			condition = SymbolFactory.sym_constant(Boolean.TRUE);
		}
		
		CirExecutionPath prev_path = StateMutations.inblock_prev_path(state.get_execution());
		CirExecution check_point = StateMutations.find_checkpoint(prev_path, condition);
		return CirAbstractState.eva_cond(check_point, condition, true);
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_block_error(CirBlockErrorState state, SymbolProcess context) throws Exception {
		return state;
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_flows_error(CirFlowsErrorState state, SymbolProcess context) throws Exception {
		return state;
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_traps_error(CirTrapsErrorState state, SymbolProcess context) throws Exception {
		CirExecution execution = state.get_execution();
		CirFunction main_function = execution.get_graph().get_function().get_graph().get_main_function();
		if(main_function != null) {
			execution = main_function.get_flow_graph().get_exit();
		}
		else {
			execution = execution.get_graph().get_exit();
		}
		return CirAbstractState.set_trap(execution);
	}
	/**
	 * @param state
	 * @param context
	 * @return normalized form of state value error (either value error or trap)
	 * @throws Exception
	 */
	private CirAbstractState norm_value_error(CirValueErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression muta_value = state.get_muta_value();
		muta_value = StateMutations.evaluate(muta_value, context);
		if(StateMutations.is_trap_value(muta_value)) {
			return CirAbstractState.set_trap(state.get_execution());
		}
		else if(state.get_store_type() == CirStoreClass.vdef) {
			return CirAbstractState.set_vdef(state.get_expression(), state.get_store_key(), muta_value);
		}
		else {
			return CirAbstractState.set_expr(state.get_expression(), muta_value);
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return 
	 * @throws Exception
	 */
	private CirAbstractState norm_incre_error(CirIncreErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression difference = state.get_difference();
		difference = StateMutations.evaluate(difference, context);
		if(StateMutations.is_trap_value(difference)) {
			return CirAbstractState.set_trap(state.get_execution());
		}
		else if(state.get_store_type() == CirStoreClass.vdef) {
			return CirAbstractState.inc_vdef(state.get_expression(), state.get_store_key(), difference);
		}
		else {
			return CirAbstractState.inc_expr(state.get_expression(), difference);
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_bixor_error(CirBixorErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression difference = state.get_difference();
		difference = StateMutations.evaluate(difference, context);
		if(StateMutations.is_trap_value(difference)) {
			return CirAbstractState.set_trap(state.get_execution());
		}
		else if(state.get_store_type() == CirStoreClass.vdef) {
			return CirAbstractState.xor_vdef(state.get_expression(), state.get_store_key(), difference);
		}
		else {
			return CirAbstractState.xor_expr(state.get_expression(), difference);
		}
	}
	
	/* validation */
	/**
	 * @param state		the abstract state to be evaluated as condition
	 * @param context	symbolic context in which the state is evaluated
	 * @return			True {satisfied}; False {non-satisfied}; null {Unknown}
	 * @throws Exception
	 */
	protected static Boolean evaluate(CirAbstractState state, SymbolProcess context) throws Exception {
		return normalizer.eval(state, context);
	}
	/**
	 * @param state		the abstract state to be evaluated as condition
	 * @param context	symbolic context in which the state is evaluated
	 * @return			True {satisfied}; False {non-satisfied}; null {Unknown}
	 * @throws Exception
	 */
	private Boolean eval(CirAbstractState state, SymbolProcess context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(state instanceof CirReachTimesState) {
			return this.eval_reach_times((CirReachTimesState) state, context);
		}
		else if(state instanceof CirLimitTimesState) {
			return this.eval_limit_times((CirLimitTimesState) state, context);
		}
		else if(state instanceof CirMConstrainState) {
			return this.eval_m_constrain((CirMConstrainState) state, context);
		}
		else if(state instanceof CirNConstrainState) {
			return this.eval_n_constrain((CirNConstrainState) state, context);
		}
		else if(state instanceof CirBlockErrorState) {
			return this.eval_block_error((CirBlockErrorState) state, context);
		}
		else if(state instanceof CirFlowsErrorState) {
			return this.eval_flows_error((CirFlowsErrorState) state, context);
		}
		else if(state instanceof CirTrapsErrorState) {
			return this.eval_traps_erorr((CirTrapsErrorState) state, context);
		}
		else if(state instanceof CirValueErrorState) {
			return this.eval_value_error((CirValueErrorState) state, context);
		}
		else if(state instanceof CirIncreErrorState) {
			return this.eval_incre_error((CirIncreErrorState) state, context);
		}
		else if(state instanceof CirBixorErrorState) {
			return this.eval_bixor_error((CirBixorErrorState) state, context);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + state.getClass().getSimpleName());
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_reach_times(CirReachTimesState state, SymbolProcess context) throws Exception {
		if(context == null) {
			return null;
		}
		else {
			/* 1. collect the set of execution points in decidable block */
			CirExecutionPath path = StateMutations.
							inblock_prev_path(state.get_execution());
			Set<CirExecution> executions = new HashSet<CirExecution>();
			for(CirExecutionEdge edge : path.get_edges()) {
				executions.add(edge.get_source());
			}
			executions.add(path.get_target());
			int min_times = state.get_minimal_times();
			
			/* 2. validate the coverage set */
			for(CirExecution execution : executions) {
				SymbolExpression res = context.get_data_stack().load(execution);
				if(res != null) {
					int act_times = ((SymbolConstant) res).get_int();
					if(act_times >= min_times) {
						return Boolean.TRUE;
					}
				}
			}
			return Boolean.FALSE;
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_limit_times(CirLimitTimesState state, SymbolProcess context) throws Exception {
		if(context == null) {
			return null;
		}
		else {
			/* 1. collect the set of execution points in decidable block */
			CirExecutionPath path = StateMutations.
							inblock_prev_path(state.get_execution());
			Set<CirExecution> executions = new HashSet<CirExecution>();
			for(CirExecutionEdge edge : path.get_edges()) {
				executions.add(edge.get_source());
			}
			executions.add(path.get_target());
			int max_times = state.get_maximal_times();
			
			/* 2. validate the coverage set */
			for(CirExecution execution : executions) {
				SymbolExpression res = context.get_data_stack().load(execution);
				if(res != null) {
					int act_times = ((SymbolConstant) res).get_int();
					if(act_times > max_times) {
						return Boolean.FALSE;
					}
				}
			}
			return Boolean.TRUE;
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_m_constrain(CirMConstrainState state, SymbolProcess context) throws Exception {
		SymbolExpression condition = state.get_condition();
		condition = StateMutations.evaluate(condition, context);
		if(StateMutations.is_trap_value(condition)) {
			return Boolean.TRUE;
		}
		else if(condition instanceof SymbolConstant) {
			return ((SymbolConstant) condition).get_bool();
		}
		else {
			return null;
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_n_constrain(CirNConstrainState state, SymbolProcess context) throws Exception {
		SymbolExpression condition = state.get_condition();
		condition = StateMutations.evaluate(condition, context);
		if(StateMutations.is_trap_value(condition)) {
			return Boolean.TRUE;
		}
		else if(condition instanceof SymbolConstant) {
			return ((SymbolConstant) condition).get_bool();
		}
		else {
			return null;
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_block_error(CirBlockErrorState state, SymbolProcess context) throws Exception {
		return Boolean.TRUE;
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_flows_error(CirFlowsErrorState state, SymbolProcess context) throws Exception {
		return state.get_orig_target() != state.get_muta_target();
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_traps_erorr(CirTrapsErrorState state, SymbolProcess context) throws Exception {
		return Boolean.TRUE;
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_value_error(CirValueErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression orig_value = state.get_orig_value();
		SymbolExpression muta_value = state.get_muta_value();
		orig_value = StateMutations.evaluate(orig_value, context);
		muta_value = StateMutations.evaluate(muta_value, context);
		
		if(StateMutations.is_trap_value(muta_value)) {
			return Boolean.TRUE;
		}
		else if(muta_value.equals(orig_value)) {
			return Boolean.FALSE;
		}
		else {
			return null;
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_incre_error(CirIncreErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression difference = state.get_difference();
		difference = StateMutations.evaluate(difference, context);
		if(StateMutations.is_trap_value(difference)) {
			return Boolean.TRUE;
		}
		else if(difference instanceof SymbolConstant) {
			return ((SymbolConstant) difference).get_double() != 0;
		}
		else {
			return null;
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_bixor_error(CirBixorErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression difference = state.get_difference();
		difference = StateMutations.evaluate(difference, context);
		if(StateMutations.is_trap_value(difference)) {
			return Boolean.TRUE;
		}
		else if(difference instanceof SymbolConstant) {
			return ((SymbolConstant) difference).get_long() != 0;
		}
		else {
			return null;
		}
	}
	
}
