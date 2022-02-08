package com.jcsa.jcmutest.mutant.cir2mutant.utils;

import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBixorErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraintState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirCoverTimesState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirFlowsErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirIncreErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirSyMutationState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirTrapsErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirValueErrorState;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It implements the normalization of CirAbstractState based on some context
 * of symbolic computation.
 * 
 * @author yukimula
 *
 */
public final class CirStateNormalizer {
	
	/* singleton mode */ /** constructor **/ private CirStateNormalizer() {}
	static final CirStateNormalizer normalizer = new CirStateNormalizer();
	
	/* normalize methods */
	/**
	 * It normalizes the input state to corresponding standard structural form.
	 * @param state		the state to be normalized
	 * @param context	the context in which the state is normalized
	 * @return			the normalized structural form of input state
	 * @throws Exception
	 */
	public static CirAbstractState normalize(CirAbstractState state, SymbolProcess context) throws Exception {
		return normalizer.norm(state, context);
	}
	/**
	 * It normalizes the input state to corresponding standard structural form.
	 * @param state		the state to be normalized
	 * @param context	the context in which the state is normalized
	 * @return			the normalized structural form of input state
	 * @throws Exception
	 */
	private CirAbstractState norm(CirAbstractState state, SymbolProcess context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(state instanceof CirCoverTimesState) {
			return this.norm_cov_times((CirCoverTimesState) state, context);
		}
		else if(state instanceof CirConstraintState) {
			return this.norm_constrain((CirConstraintState) state, context);
		}
		else if(state instanceof CirSyMutationState) {
			return this.norm_sy_mutant((CirSyMutationState) state, context);
		}
		else if(state instanceof CirBlockErrorState) {
			return this.norm_blc_error((CirBlockErrorState) state, context);
		}
		else if(state instanceof CirFlowsErrorState) {
			return this.norm_flw_error((CirFlowsErrorState) state, context);
		}
		else if(state instanceof CirTrapsErrorState) {
			return this.norm_trp_error((CirTrapsErrorState) state, context);
		}
		else if(state instanceof CirValueErrorState) {
			return this.norm_val_error((CirValueErrorState) state, context);
		}
		else if(state instanceof CirIncreErrorState) {
			return this.norm_inc_error((CirIncreErrorState) state, context);
		}
		else if(state instanceof CirBixorErrorState) {
			return this.norm_xor_error((CirBixorErrorState) state, context);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + state);
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return cov_times([S], (bool, times)) --> cov_times([S'], (bool, times))
	 * @throws Exception
	 */
	private CirAbstractState norm_cov_times(CirCoverTimesState state, SymbolProcess context) throws Exception {
		CirExecution target = state.get_execution();
		CirExecutionPath path = CirMutations.inblock_prev_path(target);
		int times = state.get_executed_times();
		CirExecution execution = path.get_source();
		if(state.is_limit_coverage()) {
			return CirAbstractState.lim_time(execution, times);
		}
		else {
			return CirAbstractState.cov_time(execution, times);
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_constrain(CirConstraintState state, SymbolProcess context) throws Exception {
		SymbolExpression condition = state.get_condition();
		condition = CirMutations.evaluate(condition, context);
		if(CirMutations.is_trap_value(condition)) {
			condition = SymbolFactory.sym_constant(Boolean.TRUE);
		}
		
		CirExecution target = state.get_execution();
		CirExecutionPath path = CirMutations.inblock_prev_path(target);
		CirExecution execution = CirMutations.find_checkpoint(path, condition);
		
		if(state.is_must_constrain()) {
			return CirAbstractState.eva_must(execution, condition);
		}
		else {
			return CirAbstractState.eva_need(execution, condition);
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_sy_mutant(CirSyMutationState state, SymbolProcess context) throws Exception {
		CirExecution target = state.get_execution();
		CirExecutionPath path = CirMutations.inblock_prev_path(target);
		CirExecution execution = path.get_source();
		return CirAbstractState.ast_muta(execution, state.get_mutant_id(), state.get_operator());
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_blc_error(CirBlockErrorState state, SymbolProcess context) throws Exception {
		return state;
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_flw_error(CirFlowsErrorState state, SymbolProcess context) throws Exception {
		return state;
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_trp_error(CirTrapsErrorState state, SymbolProcess context) throws Exception {
		return state;
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_val_error(CirValueErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression muta_value = state.get_muta_value();
		muta_value = CirMutations.evaluate(muta_value, context);
		if(CirMutations.is_trap_value(muta_value)) {
			return CirAbstractState.trp_stmt(state.get_execution());
		}
		else if(state.has_expression()) {
			return CirAbstractState.set_expr(state.get_expression(), muta_value);
		}
		else {
			CirExpression expression = (CirExpression) state.get_identifier().get_source();
			return CirAbstractState.set_vdef(expression, muta_value);
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_inc_error(CirIncreErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression muta_value = state.get_difference();
		muta_value = CirMutations.evaluate(muta_value, context);
		if(CirMutations.is_trap_value(muta_value)) {
			return CirAbstractState.trp_stmt(state.get_execution());
		}
		else if(state.has_expression()) {
			return CirAbstractState.inc_expr(state.get_expression(), muta_value);
		}
		else {
			CirExpression expression = (CirExpression) state.get_identifier().get_source();
			return CirAbstractState.inc_vdef(expression, muta_value);
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private CirAbstractState norm_xor_error(CirBixorErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression muta_value = state.get_difference();
		muta_value = CirMutations.evaluate(muta_value, context);
		if(CirMutations.is_trap_value(muta_value)) {
			return CirAbstractState.trp_stmt(state.get_execution());
		}
		else if(state.has_expression()) {
			return CirAbstractState.xor_expr(state.get_expression(), muta_value);
		}
		else {
			CirExpression expression = (CirExpression) state.get_identifier().get_source();
			return CirAbstractState.xor_vdef(expression, muta_value);
		}
	}
	
	/* evaluation methods */
	/**
	 * It evaluates the state to a boolean value according to its category and the 
	 * given symbolic computational context.
	 * @param state		the state to be evaluated by this method
	 * @param context	the context in which the state is evaluated
	 * @return			True {passed}; False {fail}; null {Unknown}
	 * @throws Exception
	 */
	public static Boolean evaluate(CirAbstractState state, SymbolProcess context) throws Exception {
		return normalizer.eval(state, context);
	}
	/**
	 * It evaluates the state to a boolean value according to its category and the 
	 * given symbolic computational context.
	 * @param state		the state to be evaluated by this method
	 * @param context	the context in which the state is evaluated
	 * @return			True {passed}; False {fail}; null {Unknown}
	 * @throws Exception
	 */
	private Boolean eval(CirAbstractState state, SymbolProcess context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(state instanceof CirCoverTimesState) {
			return this.eval_cov_times((CirCoverTimesState) state, context);
		}
		else if(state instanceof CirConstraintState) {
			return this.eval_constrain((CirConstraintState) state, context);
		}
		else if(state instanceof CirSyMutationState) {
			return this.eval_sy_mutant((CirSyMutationState) state, context);
		}
		else if(state instanceof CirBlockErrorState) {
			return this.eval_blc_error((CirBlockErrorState) state, context);
		}
		else if(state instanceof CirFlowsErrorState) {
			return this.eval_flw_error((CirFlowsErrorState) state, context);
		}
		else if(state instanceof CirTrapsErrorState) {
			return this.eval_trp_error((CirTrapsErrorState) state, context);
		}
		else if(state instanceof CirValueErrorState) {
			return this.eval_val_error((CirValueErrorState) state, context);
		}
		else if(state instanceof CirIncreErrorState) {
			return this.eval_inc_error((CirIncreErrorState) state, context);
		}
		else if(state instanceof CirBixorErrorState) {
			return this.eval_xor_error((CirBixorErrorState) state, context);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + state);
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_cov_times(CirCoverTimesState state, SymbolProcess context) throws Exception {
		if(context == null) {
			return null;
		}
		else {
			/* 1. collect the set of execution points in decidable block */
			CirExecutionPath path = CirMutations.
								inblock_prev_path(state.get_execution());
			Set<CirExecution> executions = new HashSet<CirExecution>();
			for(CirExecutionEdge edge : path.get_edges()) {
				executions.add(edge.get_source());
			}
			executions.add(path.get_target());
			int times = state.get_executed_times();
			
			/* 2. collect the actually executed times in the context */
			Set<Integer> actual_times = new HashSet<Integer>();
			for(CirExecution execution : executions) {
				SymbolExpression res = context.get_data_stack().load(execution);
				if(res != null) {
					Integer act_times = ((SymbolConstant) res).get_int();
					actual_times.add(act_times);
				}
			}
			
			/* 3. evaluate the cov_times or lim_times state */
			if(state.is_limit_coverage()) {
				for(Integer actual_time : actual_times) {
					if(actual_time > times) {
						return Boolean.FALSE;
					}
				}
				return Boolean.TRUE;
			}
			else {
				for(Integer actual_time : actual_times) {
					if(actual_time >= times) {
						return Boolean.TRUE;
					}
				}
				return Boolean.FALSE;
			}
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_constrain(CirConstraintState state, SymbolProcess context) throws Exception {
		SymbolExpression condition = state.get_condition();
		condition = CirMutations.evaluate(condition, context);
		if(CirMutations.is_trap_value(condition)) {
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
	private Boolean eval_sy_mutant(CirSyMutationState state, SymbolProcess context) throws Exception {
		if(context == null) {
			return null;
		}
		else {
			/* 1. collect the set of execution points in decidable block */
			CirExecutionPath path = CirMutations.inblock_prev_path(state.get_execution());
			Set<CirExecution> executions = new HashSet<CirExecution>();
			for(CirExecutionEdge edge : path.get_edges()) {
				executions.add(edge.get_source());
			}
			executions.add(path.get_target());
			
			/* 2. determine whether the execution is executed or not */
			for(CirExecution execution : executions) {
				SymbolExpression res = context.get_data_stack().load(execution);
				if(res != null) {
					Integer act_times = ((SymbolConstant) res).get_int();
					if(act_times.intValue() > 0) { return null; }
				}
			}
			return Boolean.FALSE;	/* not killed if not covered */
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_blc_error(CirBlockErrorState state, SymbolProcess context) throws Exception {
		if(state.get_execution().get_statement() instanceof CirTagStatement) {
			return Boolean.FALSE;
		}
		else {
			return Boolean.TRUE;
		}
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_flw_error(CirFlowsErrorState state, SymbolProcess context) throws Exception {
		return Boolean.valueOf(state.get_orig_target() != state.get_muta_target());
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_trp_error(CirTrapsErrorState state, SymbolProcess context) throws Exception {
		return Boolean.TRUE;
	}
	/**
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Boolean eval_val_error(CirValueErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression orig_value = state.get_orig_value();
		SymbolExpression muta_value = state.get_muta_value();
		orig_value = CirMutations.evaluate(orig_value, context);
		muta_value = CirMutations.evaluate(muta_value, context);
		
		if(CirMutations.is_trap_value(muta_value)) {
			return Boolean.TRUE;
		}
		else if(orig_value.equals(muta_value)) {
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
	private Boolean eval_inc_error(CirIncreErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression difference = state.get_difference();
		difference = CirMutations.evaluate(difference, context);
		
		if(CirMutations.is_trap_value(difference)) {
			return Boolean.TRUE;
		}
		else if(difference instanceof SymbolConstant) {
			if(CirMutations.is_doubles(state.get_expression())) {
				return Boolean.valueOf(((SymbolConstant) difference).get_double() != 0.0);
			}
			else {
				return Boolean.valueOf(((SymbolConstant) difference).get_long() != 0L);
			}
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
	private Boolean eval_xor_error(CirBixorErrorState state, SymbolProcess context) throws Exception {
		SymbolExpression difference = state.get_difference();
		difference = CirMutations.evaluate(difference, context);
		
		if(CirMutations.is_trap_value(difference)) {
			return Boolean.TRUE;
		}
		else if(difference instanceof SymbolConstant) {
			if(CirMutations.is_doubles(state.get_expression())) {
				return Boolean.valueOf(((SymbolConstant) difference).get_double() != 0.0);
			}
			else {
				return Boolean.valueOf(((SymbolConstant) difference).get_long() != 0L);
			}
		}
		else {
			return null;
		}
	}
	
}
