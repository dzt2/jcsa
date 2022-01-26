package com.jcsa.jcmutest.mutant.sta2mutant.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirBixorErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirBlockErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirFlowsErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirIncreErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirLimitTimesState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirMConstrainState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirNConstrainState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirReachTimesState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirStoreClass;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirTrapsErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirValueErrorState;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It implements the state extension over the local C-intermediate representative
 * program location.
 * 
 * @author yukimula
 *
 */
public final class CirLocalStateInference {
	
	/* singleton mode */ /** constructor **/ private CirLocalStateInference() {}
	static final CirLocalStateInference loc_infer = new CirLocalStateInference();
	
	/* basic method for supporting of local inference */
	/**
	 * @param max_times
	 * @return the maximal times smaller than the input one
	 */
	private int derive_smaller_times(int max_times) {
		int times = 1;
		while(times <= max_times) {
			times = times * 2;
		}
		return times / 2;
	}
	/**
	 * @param expression
	 * @return whether the constant is zero
	 */
	private boolean is_zero_constant(SymbolExpression expression) {
		if(expression instanceof SymbolConstant) {
			SymbolConstant constant = (SymbolConstant) expression;
			CType data_type = expression.get_data_type();
			if(StateMutations.is_boolean(data_type)) {
				return !constant.get_bool();
			}
			else if(StateMutations.is_integer(data_type)) {
				return constant.get_long() == 0L;
			}
			else {
				return constant.get_double() == 0.0;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * It derives the set of sub-conditions in the conjunctive form of expression
	 * @param expression
	 * @param sub_conditions
	 * @throws Exception
	 */
	private void derive_conditions_in_conjunction(SymbolExpression expression,
			Collection<SymbolExpression> sub_conditions) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(sub_conditions == null) {
			throw new IllegalArgumentException("Invalid sub_conditions: null");
		}
		else if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) { 	/* True is redundant */ }
			else {											/* False is the only */
				sub_conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.logic_and) {
				this.derive_conditions_in_conjunction(loperand, sub_conditions);
				this.derive_conditions_in_conjunction(roperand, sub_conditions);
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
	 * It generates the set of subsumed conditions from the expression
	 * @param expression
	 * @param subsumed_conditions
	 * @throws Exception
	 */
	private void derive_subsumed_conditions_from(SymbolExpression expression,
			Collection<SymbolExpression> subsumed_conditions) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(subsumed_conditions == null) {
			throw new IllegalArgumentException("Invalid subsumed_conditions");
		}
		else {
			/* I. derive the sub-conditions in conjunctive form of expression */
			Set<SymbolExpression> sub_conditions = new HashSet<SymbolExpression>();
			this.derive_conditions_in_conjunction(expression, sub_conditions);
			subsumed_conditions.clear();
			
			/* II. expression is semantically equivalent with TRUE constant */
			if(sub_conditions.isEmpty()) { return; }
			/* III. expression is transformed with only one for subsumption */
			else if(sub_conditions.size() == 1) {
				SymbolExpression condition = sub_conditions.iterator().next();
				if(condition instanceof SymbolBinaryExpression) {
					SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
					SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
					COperator operator = ((SymbolBinaryExpression) condition).get_operator().get_operator();
					if(operator == COperator.equal_with) {
						subsumed_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
						subsumed_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
					}
					else if(operator == COperator.greater_tn) {
						subsumed_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
						subsumed_conditions.add(SymbolFactory.not_equals(loperand, roperand));
					}
					else if(operator == COperator.smaller_tn) {
						subsumed_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
						subsumed_conditions.add(SymbolFactory.not_equals(loperand, roperand));
					}
					else {
						subsumed_conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
					}
				}
				else {
					subsumed_conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
				}
			}
			/* IV. expression is conjunctive and being returned directly */
			else { subsumed_conditions.addAll(sub_conditions); }
		}
	}
	
	/* local subsumption inference for constraint state */
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void linf_limit_times(CirLimitTimesState state, Collection<CirAbstractState> outputs) throws Exception { }
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void linf_reach_times(CirReachTimesState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		int minimal_times = state.get_minimal_times();
		int times = this.derive_smaller_times(minimal_times);
		if(times <= 1) {
			/* coverage state is the final leaf state in hierarchy */
		}
		else {
			outputs.add(CirAbstractState.cov_time(execution, times));
		}
	}
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void linf_m_constrain(CirMConstrainState state, Collection<CirAbstractState> outputs) throws Exception {
		/* I. must_eval(execution, condition) |--> need_eval(execution, condition) */
		CirExecution execution = state.get_execution();
		SymbolExpression orig_condition = state.get_condition();
		outputs.add(CirAbstractState.eva_cond(execution, orig_condition, true));
		
		/* II. derive the subsumed conditions from the original condition */
		Set<SymbolExpression> subsumed_conditions = new HashSet<SymbolExpression>();
		this.derive_subsumed_conditions_from(orig_condition, subsumed_conditions);
		for(SymbolExpression subsumed_condition : subsumed_conditions) {
			outputs.add(CirAbstractState.mus_cond(execution, subsumed_condition, true));
		}
	}
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void linf_n_constrain(CirNConstrainState state, Collection<CirAbstractState> outputs) throws Exception {
		/* I. derive the subsumed conditions and eva_cond(execution, cond) more */
		CirExecution execution = state.get_execution();
		SymbolExpression orig_condition = state.get_condition();
		Set<SymbolExpression> subsumed_conditions = new HashSet<SymbolExpression>();
		this.derive_subsumed_conditions_from(orig_condition, subsumed_conditions);
		for(SymbolExpression subsumed_condition : subsumed_conditions) {
			outputs.add(CirAbstractState.eva_cond(execution, subsumed_condition, true));
		}
		
		/* II. in case that no more condition is subsumed, cover the statement */
		if(subsumed_conditions.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
	}
	
	/* local subsumption inference for path-error state */
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void linf_block_error(CirBlockErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		if(execution.get_statement() instanceof CirTagStatement) {
			/* ignore the mutation over the non-semantic statement */
		}
		else if(state.is_original_executed()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			/* no more subsumption from the block-error state */
		}
	}
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void linf_flows_error(CirFlowsErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* 1. generate the execution paths being executed after flows */
		CirExecutionPath orig_path = CirExecutionPathFinder.finder.df_extend(state.get_orig_target());
		CirExecutionPath muta_path = CirExecutionPathFinder.finder.df_extend(state.get_muta_target());
		
		/* 2. collect the execution points being executed in both paths */
		Collection<CirExecution> orig_executions = new HashSet<CirExecution>();
		Collection<CirExecution> muta_executions = new HashSet<CirExecution>();
		for(CirExecutionEdge edge : orig_path.get_edges()) {
			orig_executions.add(edge.get_target());
		}
		for(CirExecutionEdge edge : muta_path.get_edges()) {
			muta_executions.add(edge.get_target());
		}
		orig_executions.add(orig_path.get_source());
		muta_executions.add(muta_path.get_source());
		
		/* 3. collect the commonly execution points between the paths */
		Collection<CirExecution> common_executions = new HashSet<CirExecution>();
		for(CirExecution execution : orig_executions) {
			if(muta_executions.contains(execution)) {
				common_executions.add(execution);
			}
		}
		orig_executions.removeAll(common_executions);
		muta_executions.removeAll(common_executions);
		
		/* 4. generate the subsumed block error states from this flow */
		for(CirExecution execution : orig_executions) {
			if(!(execution.get_statement() instanceof CirTagStatement))
				outputs.add(CirAbstractState.set_stmt(execution, false));
		}
		for(CirExecution execution : muta_executions) {
			if(!(execution.get_statement() instanceof CirTagStatement))
				outputs.add(CirAbstractState.set_stmt(execution, true));
		}
		outputs.add(CirAbstractState.cov_time(state.get_source_execution(), 1));
	}
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void linf_traps_error(CirTrapsErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		outputs.add(CirAbstractState.cov_time(execution, 1));
	}
	
	/* local subsumption inference for data-error state */
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void linf_value_error(CirValueErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* 1. declarations and the data elements in the input state */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		CirStoreClass store_type = state.get_store_type();
		SymbolExpression store_key = state.get_store_key();
		SymbolExpression orig_value = state.get_orig_value();
		SymbolExpression muta_value = state.get_muta_value();
		
		/* 2. trap_value |--> TRAP(execution) */
		if(StateMutations.is_trap_value(muta_value)) {
			outputs.add(CirAbstractState.set_trap(execution));
		}
		/* 3. equivalent value |--> FALSE at execution */
		else if(orig_value.equals(muta_value)) {
			outputs.add(CirAbstractState.eva_cond(execution, Boolean.FALSE, true));
		}
		/* 4. categorization over the boolean hierarchy */
		else if(StateMutations.is_boolean(expression)) {
			this.linf_vbool_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* 5. categorization over the integer hierarchy */
		else if(StateMutations.is_integer(expression)) {
			this.linf_vnumb_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* 6. categorization over the double hierarchy */
		else if(StateMutations.is_doubles(expression)) {
			this.linf_vreal_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* 7. categorization over the address hierarchy */
		else if(StateMutations.is_address(expression)) {
			this.linf_vaddr_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* 8. otherwise, consider it as an auto class */
		else {
			this.linf_vauto_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
	}
	/**
	 * It infers the local subsumed abstract states from boolean value error
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param muta_value
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_vbool_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<CirAbstractState> outputs) throws Exception {
		/* boolean categorization */
		Set<SymbolExpression> muta_values = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			if(((SymbolConstant) muta_value).get_bool()) {
				muta_values.add(StateMutations.true_value);
			}
			else {
				muta_values.add(StateMutations.fals_value);
			}
		}
		else if(muta_value == StateMutations.true_value) {
			muta_values.add(StateMutations.bool_value);
		}
		else if(muta_value == StateMutations.fals_value) {
			muta_values.add(StateMutations.bool_value);
		}
		else if(muta_value == StateMutations.bool_value) { }
		else {
			muta_values.add(StateMutations.bool_value);
		}
		
		/* 2. generate the subsumed error state from the categorization */
		if(muta_values.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression value : muta_values) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.set_vdef(expression, store_key, value));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, value));
				}
			}
		}
	}
	/**
	 * It infers the local subsumed abstract states from integer value error
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param muta_value
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_vnumb_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<CirAbstractState> outputs) throws Exception {
		/* numeric categorization */
		Set<SymbolExpression> errors = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			long constant = ((SymbolConstant) muta_value).get_long();
			if(constant > 0) {
				errors.add(StateMutations.post_value);
			}
			else if(constant < 0) {
				errors.add(StateMutations.negt_value);
			}
			else {
				errors.add(StateMutations.zero_value);
			}
		}
		else if(muta_value == StateMutations.post_value) {
			errors.add(StateMutations.nzro_value);
			errors.add(StateMutations.nneg_value);
		}
		else if(muta_value == StateMutations.zero_value) {
			errors.add(StateMutations.npos_value);
			errors.add(StateMutations.nneg_value);
		}
		else if(muta_value == StateMutations.negt_value) {
			errors.add(StateMutations.nzro_value);
			errors.add(StateMutations.npos_value);
		}
		else if(muta_value == StateMutations.npos_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.nneg_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.nzro_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.numb_value) { }
		else {
			errors.add(StateMutations.numb_value);
		}
		
		/* 2. generate the subsumed error state from mutation */
		if(errors.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression error : errors) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.set_vdef(expression, store_key, error));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, error));
				}
			}
		}
		
		/* 3. generate the numeric and bitwise difference error */
		SymbolExpression difference;
		difference = SymbolFactory.arith_sub(expression.get_data_type(), muta_value, orig_value);
		difference = StateMutations.evaluate(difference);
		if(difference instanceof SymbolConstant) {
			if(store_type == CirStoreClass.vdef) {
				outputs.add(CirAbstractState.inc_vdef(expression, store_key, difference));
			}
			else {
				outputs.add(CirAbstractState.inc_expr(expression, difference));
			}
		}
		difference = SymbolFactory.bitws_xor(expression.get_data_type(), muta_value, orig_value);
		difference = StateMutations.evaluate(difference);
		if(difference instanceof SymbolConstant) {
			if(store_type == CirStoreClass.vdef) {
				outputs.add(CirAbstractState.xor_vdef(expression, store_key, difference));
			}
			else {
				outputs.add(CirAbstractState.xor_expr(expression, difference));
			}
		}
	}
	/**
	 * It infers the local subsumed abstract states from double value error
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param muta_value
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_vreal_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<CirAbstractState> outputs) throws Exception {
		/* numeric categorization */
		Set<SymbolExpression> errors = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			double constant = ((SymbolConstant) muta_value).get_double();
			if(constant > 0) {
				errors.add(StateMutations.post_value);
			}
			else if(constant < 0) {
				errors.add(StateMutations.negt_value);
			}
			else {
				errors.add(StateMutations.zero_value);
			}
		}
		else if(muta_value == StateMutations.post_value) {
			errors.add(StateMutations.nzro_value);
			errors.add(StateMutations.nneg_value);
		}
		else if(muta_value == StateMutations.zero_value) {
			errors.add(StateMutations.npos_value);
			errors.add(StateMutations.nneg_value);
		}
		else if(muta_value == StateMutations.negt_value) {
			errors.add(StateMutations.nzro_value);
			errors.add(StateMutations.npos_value);
		}
		else if(muta_value == StateMutations.npos_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.nneg_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.nzro_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.numb_value) { }
		else {
			errors.add(StateMutations.numb_value);
		}
		
		/* 2. generate the subsumed error state from mutation */
		if(errors.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression error : errors) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.set_vdef(expression, store_key, error));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, error));
				}
			}
		}
		
		/* 3. generate the numeric and bitwise difference error */
		SymbolExpression difference;
		difference = SymbolFactory.arith_sub(expression.get_data_type(), muta_value, orig_value);
		difference = StateMutations.evaluate(difference);
		if(difference instanceof SymbolConstant) {
			if(store_type == CirStoreClass.vdef) {
				outputs.add(CirAbstractState.inc_vdef(expression, store_key, difference));
			}
			else {
				outputs.add(CirAbstractState.inc_expr(expression, difference));
			}
		}
	}
	/**
	 * It infers the local subsumed abstract states from address value error
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param muta_value
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_vaddr_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<CirAbstractState> outputs) throws Exception {
		/* numeric categorization */
		Set<SymbolExpression> errors = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			long constant = ((SymbolConstant) muta_value).get_long();
			if(constant == 0) {
				errors.add(StateMutations.null_value);
			}
			else {
				errors.add(StateMutations.nnul_value);
			}
		}
		else if(muta_value == StateMutations.null_value) {
			errors.add(StateMutations.addr_value);
		}
		else if(muta_value == StateMutations.nnul_value) {
			errors.add(StateMutations.addr_value);
		}
		else if(muta_value == StateMutations.addr_value) { }
		else {
			errors.add(StateMutations.addr_value);
		}
		
		/* 2. generate the subsumed error state from mutation */
		if(errors.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression error : errors) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.set_vdef(expression, store_key, error));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, error));
				}
			}
		}
	}
	/**
	 * It infers the local subsumed abstract states from boolean value error
	 * @param execution
	 * @param expression
	 * @param store_type
	 * @param store_key
	 * @param muta_value
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_vauto_error(CirExecution execution, CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<CirAbstractState> outputs) throws Exception {
		outputs.add(CirAbstractState.cov_time(execution, 1));
	}
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void linf_incre_error(CirIncreErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations and data element getters from state */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		CirStoreClass store_type = state.get_store_type();
		SymbolExpression store_key = state.get_store_key();
		SymbolExpression base_value = state.get_base_value();
		SymbolExpression difference = state.get_difference();
		
		/* 2. trap_value |--> TRAP(execution) */
		if(StateMutations.is_trap_value(difference)) {
			outputs.add(CirAbstractState.set_trap(execution));
		}
		/* 3. equivalent value |--> FALSE at execution */
		else if(this.is_zero_constant(difference)) {
			outputs.add(CirAbstractState.eva_cond(execution, Boolean.FALSE, true));
		}
		/* 4. as integer incremental expression location */
		else if(StateMutations.is_integer(expression)) {
			this.linf_inumb_error(execution, expression, store_type, store_key, base_value, difference, outputs);
		}
		/* 5. real incremental on expression location */
		else {
			this.linf_ireal_error(execution, expression, store_type, store_key, base_value, difference, outputs);
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
	private void linf_inumb_error(CirExecution execution, 
			CirExpression expression, CirStoreClass store_type, 
			SymbolExpression store_key, SymbolExpression base_value,
			SymbolExpression difference, Collection<CirAbstractState> outputs) throws Exception {
		/* numeric categorization */
		Set<SymbolExpression> errors = new HashSet<SymbolExpression>();
		if(difference instanceof SymbolConstant) {
			long constant = ((SymbolConstant) difference).get_long();
			if(constant > 0) {
				errors.add(StateMutations.post_value);
			}
			else if(constant < 0) {
				errors.add(StateMutations.negt_value);
			}
			else {
				errors.add(StateMutations.zero_value);
			}
		}
		else if(difference == StateMutations.post_value) {
			errors.add(StateMutations.nzro_value);
			errors.add(StateMutations.nneg_value);
		}
		else if(difference == StateMutations.zero_value) {
			errors.add(StateMutations.npos_value);
			errors.add(StateMutations.nneg_value);
		}
		else if(difference == StateMutations.negt_value) {
			errors.add(StateMutations.nzro_value);
			errors.add(StateMutations.npos_value);
		}
		else if(difference == StateMutations.npos_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(difference == StateMutations.nneg_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(difference == StateMutations.nzro_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(difference == StateMutations.numb_value) { }
		else {
			errors.add(StateMutations.numb_value);
		}
		
		/* 2. generate the subsumed error state from mutation */
		if(errors.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression error : errors) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.inc_vdef(expression, store_key, error));
				}
				else {
					outputs.add(CirAbstractState.inc_expr(expression, error));
				}
			}
		}
		
		/* 3. translate to the set_expr error state */
		SymbolExpression muta_value = SymbolFactory.arith_add(
				expression.get_data_type(), base_value, difference);
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
	private void linf_ireal_error(CirExecution execution, 
			CirExpression expression, CirStoreClass store_type, 
			SymbolExpression store_key, SymbolExpression base_value,
			SymbolExpression difference, Collection<CirAbstractState> outputs) throws Exception {
		/* numeric categorization */
		Set<SymbolExpression> errors = new HashSet<SymbolExpression>();
		if(difference instanceof SymbolConstant) {
			double constant = ((SymbolConstant) difference).get_double();
			if(constant > 0) {
				errors.add(StateMutations.post_value);
			}
			else if(constant < 0) {
				errors.add(StateMutations.negt_value);
			}
			else {
				errors.add(StateMutations.zero_value);
			}
		}
		else if(difference == StateMutations.post_value) {
			errors.add(StateMutations.nzro_value);
			errors.add(StateMutations.nneg_value);
		}
		else if(difference == StateMutations.zero_value) {
			errors.add(StateMutations.npos_value);
			errors.add(StateMutations.nneg_value);
		}
		else if(difference == StateMutations.negt_value) {
			errors.add(StateMutations.nzro_value);
			errors.add(StateMutations.npos_value);
		}
		else if(difference == StateMutations.npos_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(difference == StateMutations.nneg_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(difference == StateMutations.nzro_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(difference == StateMutations.numb_value) { }
		else {
			errors.add(StateMutations.numb_value);
		}
		
		/* 2. generate the subsumed error state from mutation */
		if(errors.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression error : errors) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.inc_vdef(expression, store_key, error));
				}
				else {
					outputs.add(CirAbstractState.inc_expr(expression, error));
				}
			}
		}
		
		/* 3. translate to the set_expr error state */
		SymbolExpression muta_value = SymbolFactory.arith_add(
				expression.get_data_type(), base_value, difference);
		if(store_type == CirStoreClass.vdef) {
			outputs.add(CirAbstractState.set_vdef(expression, store_key, muta_value));
		}
		else {
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
	}
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void linf_bixor_error(CirBixorErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations and data element getters from state */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		CirStoreClass store_type = state.get_store_type();
		SymbolExpression store_key = state.get_store_key();
		SymbolExpression base_value = state.get_base_value();
		SymbolExpression difference = state.get_difference();
		
		/* 2. trap_value |--> TRAP(execution) */
		if(StateMutations.is_trap_value(difference)) {
			outputs.add(CirAbstractState.set_trap(execution));
		}
		/* 3. equivalent value |--> FALSE at execution */
		else if(this.is_zero_constant(difference)) {
			outputs.add(CirAbstractState.eva_cond(execution, Boolean.FALSE, true));
		}
		/* 4. integer bitwise-difference over the location */
		else {
			this.linf_xnumb_error(execution, expression, store_type, store_key, base_value, difference, outputs);
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
	private void linf_xnumb_error(CirExecution execution, 
			CirExpression expression, CirStoreClass store_type, 
			SymbolExpression store_key, SymbolExpression base_value,
			SymbolExpression difference, Collection<CirAbstractState> outputs) throws Exception {
		/* numeric categorization */
		Set<SymbolExpression> errors = new HashSet<SymbolExpression>();
		if(difference instanceof SymbolConstant) {
			long constant = ((SymbolConstant) difference).get_long();
			if(constant > 0) {
				errors.add(StateMutations.post_value);
			}
			else if(constant < 0) {
				errors.add(StateMutations.negt_value);
			}
			else {
				errors.add(StateMutations.zero_value);
			}
		}
		else if(difference == StateMutations.post_value) {
			errors.add(StateMutations.nzro_value);
			errors.add(StateMutations.nneg_value);
		}
		else if(difference == StateMutations.zero_value) {
			errors.add(StateMutations.npos_value);
			errors.add(StateMutations.nneg_value);
		}
		else if(difference == StateMutations.negt_value) {
			errors.add(StateMutations.nzro_value);
			errors.add(StateMutations.npos_value);
		}
		else if(difference == StateMutations.npos_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(difference == StateMutations.nneg_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(difference == StateMutations.nzro_value) {
			errors.add(StateMutations.numb_value);
		}
		else if(difference == StateMutations.numb_value) { }
		else {
			errors.add(StateMutations.numb_value);
		}
		
		/* 2. generate the subsumed error state from mutation */
		if(errors.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else {
			for(SymbolExpression error : errors) {
				if(store_type == CirStoreClass.vdef) {
					outputs.add(CirAbstractState.xor_vdef(expression, store_key, error));
				}
				else {
					outputs.add(CirAbstractState.xor_expr(expression, error));
				}
			}
		}
		
		/* 3. translate to the set_expr error state */
		SymbolExpression muta_value = SymbolFactory.bitws_xor(
				expression.get_data_type(), base_value, difference);
		if(store_type == CirStoreClass.vdef) {
			outputs.add(CirAbstractState.set_vdef(expression, store_key, muta_value));
		}
		else {
			outputs.add(CirAbstractState.set_expr(expression, muta_value));
		}
	}
	
	/* recursive algorithm and public interface */
	/**
	 * It generates the directly subsumed abstract states by the input one
	 * @param state		the source abstract execution state to subsume others
	 * @param outputs	to preserve the set of states being subsumed by input
	 * @throws Exception
	 */
	private void linf(CirAbstractState state, Collection<CirAbstractState> outputs) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(state instanceof CirLimitTimesState) {
			this.linf_limit_times((CirLimitTimesState) state, outputs);
		}
		else if(state instanceof CirReachTimesState) {
			this.linf_reach_times((CirReachTimesState) state, outputs);
		}
		else if(state instanceof CirNConstrainState) {
			this.linf_n_constrain((CirNConstrainState) state, outputs);
		}
		else if(state instanceof CirMConstrainState) {
			this.linf_m_constrain((CirMConstrainState) state, outputs);
		}
		else if(state instanceof CirBlockErrorState) {
			this.linf_block_error((CirBlockErrorState) state, outputs);
		}
		else if(state instanceof CirFlowsErrorState) {
			this.linf_flows_error((CirFlowsErrorState) state, outputs);
		}
		else if(state instanceof CirTrapsErrorState) {
			this.linf_traps_error((CirTrapsErrorState) state, outputs);
		}
		else if(state instanceof CirValueErrorState) {
			this.linf_value_error((CirValueErrorState) state, outputs);
		}
		else if(state instanceof CirIncreErrorState) {
			this.linf_incre_error((CirIncreErrorState) state, outputs);
		}
		else if(state instanceof CirBixorErrorState) {
			this.linf_bixor_error((CirBixorErrorState) state, outputs);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + state.toString());
		}
	}
	/**
	 * It generates the directly subsumed abstract states by the input one
	 * @param state		the source abstract execution state to subsume others
	 * @param outputs	to preserve the set of states being subsumed by input
	 * @throws Exception
	 */
	protected static void local_subsume(CirAbstractState state, Collection<CirAbstractState> outputs) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			Set<CirAbstractState> buffer = new HashSet<CirAbstractState>();
			loc_infer.linf(state, buffer);
			for(CirAbstractState output : buffer) {
				outputs.add(output.normalize());
			}
		}
	}
	
}
