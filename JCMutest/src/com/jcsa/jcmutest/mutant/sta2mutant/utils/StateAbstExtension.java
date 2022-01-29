package com.jcsa.jcmutest.mutant.sta2mutant.utils;

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
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It implements the local abstract extension from state to other states.
 * 
 * @author yukimula
 *
 */
final class StateAbstExtension {
	
	/* singleton mode */ /** constructor **/ private StateAbstExtension() {}
	private static final StateAbstExtension ext = new StateAbstExtension();
	
	/* basic method to support extension algorithms */
	/**
	 * It determines the next execution times smaller than the given limit
	 * @param max_times maximal/minimal times for statement being executed
	 * @return			the times smaller than the given limit or 1 as top
	 */
	private int get_smaller_maximal_times(int max_times) {
		int times = 1;
		while(times < max_times) { times = times * 2; }
		return times / 2;
	}
	/**
	 * @param expression
	 * @return whether the symbolic expression is a zero-constant
	 */
	private boolean is_zero_constant(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(expression instanceof SymbolConstant) {
			SymbolConstant constant = (SymbolConstant) expression;
			CType data_type = constant.get_data_type();
			if(StateMutations.is_doubles(data_type)) {
				return constant.get_double().doubleValue() == 0.0;
			}
			else {
				return constant.get_long().longValue() == 0L;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * It recursively derives the sub_conditions in the expression when taking
	 * the expression as a logical conjunctive form.
	 * @param expression		the expression in which conditions are derived
	 * @param sub_conditions	to preserve the sets of sub_conditions derived
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
			if(((SymbolConstant) expression).get_bool()) {
				/* True operand is ignored from the conjunctive expression */
			}
			else {		
				sub_conditions.clear();		/* clear and take only a False */
				sub_conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.logic_and) {	/* logical conjunctive */
				this.derive_conditions_in_conjunction(loperand, sub_conditions);
				this.derive_conditions_in_conjunction(roperand, sub_conditions);
			}
			else {				/* a normal condition is directly appended */
				sub_conditions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else {					/* a normal condition is directly appended */
			sub_conditions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * It recursively derives the conditions subsumed by the expression
	 * @param expression		the expression from which the subsumed conditions are derived
	 * @param sub_conditions	to preserve set of conditions directly subsumed by expression
	 * @throws Exception
	 */
	private void derive_subsummed_conditions_from(SymbolExpression expression,
				Collection<SymbolExpression> sub_conditions) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(sub_conditions == null) {
			throw new IllegalArgumentException("Invalid sub_conditions: null");
		}
		else if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				/* TRUE condition does not subsume any other conditions */
			}
			else {
				sub_conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.logic_and) {
				this.derive_conditions_in_conjunction(expression, sub_conditions);
			}
			else if(operator == COperator.equal_with) {
				sub_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				sub_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
			}
			else if(operator == COperator.greater_tn) {
				sub_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				sub_conditions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			else if(operator == COperator.smaller_tn) {
				sub_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
				sub_conditions.add(SymbolFactory.not_equals(loperand, roperand));
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
	 * @return the set of conditions directly subsumed by the input expression
	 * @throws Exception
	 */
	private Collection<SymbolExpression> derive_subsummed_conditions(SymbolExpression expression) throws Exception {
		Set<SymbolExpression> sub_conditions = new HashSet<SymbolExpression>();
		this.derive_subsummed_conditions_from(expression, sub_conditions);
		return sub_conditions;
	}
	
	/* inferring subsumption relation for conditional states */
	/**
	 * lim_time(exec, times) --> { cov_time(exec, 1) }
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_limit_times(CirLimitTimesState state, Collection<CirAbstractState> outputs) throws Exception { }
	/**
	 * cov_time(exec, times) --> eva_expr(exec, True) | cov_time(exec, times/2)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_reach_times(CirReachTimesState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		int times = this.get_smaller_maximal_times(state.get_minimal_times());
		if(times <= 1) {
			/* equivalence subsumption with TRUE-constraint */
			outputs.add(CirAbstractState.eva_cond(execution, Boolean.TRUE, true));
		}
		else {
			/* coverage times requirement for smaller times */
			outputs.add(CirAbstractState.cov_time(execution, times));
		}
	}
	/**
	 * eva_expr(exec, expression) --> { eva_expr(exec, subsumed_condition)+ } | { cov_time(exec, 1) }
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_n_constrain(CirNConstrainState state, Collection<CirAbstractState> outputs) throws Exception {
		/* 1. declarations and subsumed conditions getter */
		CirExecution execution = state.get_execution();
		Collection<SymbolExpression> subsumed_conditions = this.
				derive_subsummed_conditions(state.get_condition());
		
		/* 2. no more subsumed condition refers to coverage */
		if(subsumed_conditions.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		/* 3. directly subsume the subsumed conditions at the point */
		else {
			for(SymbolExpression condition : subsumed_conditions) {
				outputs.add(CirAbstractState.eva_cond(execution, condition, true));
			}
		}
	}
	/**
	 * mus_expr(exec, condition) --> {mus_expr(exec, subsumed*)} | eva_expr(exec, condition)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_m_constrain(CirMConstrainState state, Collection<CirAbstractState> outputs) throws Exception {
		/* 1. declarations and subsumed conditions getter */
		CirExecution execution = state.get_execution();
		Collection<SymbolExpression> subsumed_conditions = this.
				derive_subsummed_conditions(state.get_condition());
		
		/* 2. directly subsume the next conditions at must */
		for(SymbolExpression condition : subsumed_conditions) {
			outputs.add(CirAbstractState.eva_cond(execution, condition, true));
		}
		
		/* 3. directly subsume the next necessary evaluation */
		outputs.add(CirAbstractState.eva_cond(execution, state.get_condition(), true));
	}
	
	/* inferring subsumption relation for path-errors states */
	/**
	 * set_stmt(exec, bool) --> cov_stmt(exec) | lim_stmt(exec)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_block_error(CirBlockErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		if(execution.get_statement() instanceof CirTagStatement) {
			/* no impacts being subsumed by this execution (none) */
		}
		else if(state.is_original_executed()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else { /* none subsumptions on coverage from not-executed */ }
	}
	/**
	 * set_flow(source, orig, muta) --> set_stmt(xxx,xxx) | cov_stmt(source)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_flows_error(CirFlowsErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* 1. declarations and initializations */
		CirExecutionPath orig_path = StateMutations.oublock_post_path(state.get_orig_target());
		CirExecutionPath muta_path = StateMutations.oublock_post_path(state.get_muta_target());
		
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
		
		/* 4. generate the block error states subsumed by flow errors */
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
	 * trp_stmt(exec) --> cov_stmt(exec)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_traps_error(CirTrapsErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		if(!(execution.get_statement() instanceof CirEndStatement)) {
			outputs.add(CirAbstractState.set_trap(execution.get_graph().get_exit()));
		}
		outputs.add(CirAbstractState.cov_time(execution, 1));
	}
	
	/* inferring subsumption relation for data-errors states */
	/**
	 * boolean
	 * @param execution		the execution point where the value error is seeded
	 * @param expression	the expression where the value error is injected in
	 * @param store_type	the type of the store unit to preserve error values 
	 * @param store_key		the symbolic identifier to localize the c-locations
	 * @param orig_value	the original value of the expression being replaced
	 * @param muta_value	the mutate value to replace the original expression
	 * @param outputs		to preserve the states subsumed by the value errors
	 * @throws Exception
	 */
	private void ext_vbool_error(CirExecution execution, 
			CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key, 
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* 1. derive the mutation values subsumed by error */
		Set<SymbolExpression> muvalues = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			if(((SymbolConstant) muta_value).get_bool()) {	/* const --> T|F */
				muvalues.add(StateMutations.true_value);
			}
			else {
				muvalues.add(StateMutations.fals_value);
			}
		}
		else if(muta_value == StateMutations.true_value) {	/* T|F --> Bool */
			muvalues.add(StateMutations.bool_value);
		}
		else if(muta_value == StateMutations.fals_value) {
			muvalues.add(StateMutations.bool_value);
		}
		else if(muta_value == StateMutations.bool_value) {	/* Bool --> Cov */ }
		else {
			muvalues.add(StateMutations.bool_value);		/* Other --> Bool */
		}
		
		/* 2. in case that no subsumed error value to cover */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		/* 3. otherwise, to generate abstract value error states */
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
	 * unsigned integer
	 * @param execution		the execution point where the value error is seeded
	 * @param expression	the expression where the value error is injected in
	 * @param store_type	the type of the store unit to preserve error values 
	 * @param store_key		the symbolic identifier to localize the c-locations
	 * @param orig_value	the original value of the expression being replaced
	 * @param muta_value	the mutate value to replace the original expression
	 * @param outputs		to preserve the states subsumed by the value errors
	 * @throws Exception
	 */
	private void ext_vusig_error(CirExecution execution, 
			CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key, 
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* 1. derive the mutation values subsumed by error */
		Set<SymbolExpression> muvalues = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			long constant = ((SymbolConstant) muta_value).get_long().longValue();
			if(constant == 0) {
				muvalues.add(StateMutations.zero_value);
			}
			else {
				muvalues.add(StateMutations.post_value);
			}
		}
		else if(muta_value == StateMutations.post_value) {
			muvalues.add(StateMutations.nneg_value);
		}
		else if(muta_value == StateMutations.zero_value) {
			muvalues.add(StateMutations.nneg_value);
		}
		else if(muta_value == StateMutations.nneg_value) { }
		else {
			muvalues.add(StateMutations.nneg_value);
		}
		
		/* 2. in case that no subsumed error value to cover */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		/* 3. otherwise, to generate abstract value error states */
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
	 * integer
	 * @param execution		the execution point where the value error is seeded
	 * @param expression	the expression where the value error is injected in
	 * @param store_type	the type of the store unit to preserve error values 
	 * @param store_key		the symbolic identifier to localize the c-locations
	 * @param orig_value	the original value of the expression being replaced
	 * @param muta_value	the mutate value to replace the original expression
	 * @param outputs		to preserve the states subsumed by the value errors
	 * @throws Exception
	 */
	private void ext_vnumb_error(CirExecution execution, 
			CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key, 
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* 1. derive the mutation values subsumed by error */
		Set<SymbolExpression> muvalues = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			long constant = ((SymbolConstant) muta_value).get_long().longValue();
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
		else if(muta_value == StateMutations.nzro_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.nneg_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.npos_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.numb_value) { }
		else {
			muvalues.add(StateMutations.numb_value);
		}
		
		/* 2. in case that no subsumed error value to cover */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		/* 3. otherwise, to generate abstract value error states */
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
	 * double
	 * @param execution		the execution point where the value error is seeded
	 * @param expression	the expression where the value error is injected in
	 * @param store_type	the type of the store unit to preserve error values 
	 * @param store_key		the symbolic identifier to localize the c-locations
	 * @param orig_value	the original value of the expression being replaced
	 * @param muta_value	the mutate value to replace the original expression
	 * @param outputs		to preserve the states subsumed by the value errors
	 * @throws Exception
	 */
	private void ext_vreal_error(CirExecution execution, 
			CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key, 
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* 1. derive the mutation values subsumed by error */
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
		else if(muta_value == StateMutations.nzro_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.nneg_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.npos_value) {
			muvalues.add(StateMutations.numb_value);
		}
		else if(muta_value == StateMutations.numb_value) { }
		else {
			muvalues.add(StateMutations.numb_value);
		}
		
		/* 2. in case that no subsumed error value to cover */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		/* 3. otherwise, to generate abstract value error states */
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
	 * address
	 * @param execution		the execution point where the value error is seeded
	 * @param expression	the expression where the value error is injected in
	 * @param store_type	the type of the store unit to preserve error values 
	 * @param store_key		the symbolic identifier to localize the c-locations
	 * @param orig_value	the original value of the expression being replaced
	 * @param muta_value	the mutate value to replace the original expression
	 * @param outputs		to preserve the states subsumed by the value errors
	 * @throws Exception
	 */
	private void ext_vaddr_error(CirExecution execution, 
			CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key, 
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* 1. derive the mutation values subsumed by error */
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
		
		/* 2. in case that no subsumed error value to cover */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		/* 3. otherwise, to generate abstract value error states */
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
	 * address
	 * @param execution		the execution point where the value error is seeded
	 * @param expression	the expression where the value error is injected in
	 * @param store_type	the type of the store unit to preserve error values 
	 * @param store_key		the symbolic identifier to localize the c-locations
	 * @param orig_value	the original value of the expression being replaced
	 * @param muta_value	the mutate value to replace the original expression
	 * @param outputs		to preserve the states subsumed by the value errors
	 * @throws Exception
	 */
	private void ext_vauto_error(CirExecution execution, 
			CirExpression expression,
			CirStoreClass store_type, SymbolExpression store_key, 
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		outputs.add(CirAbstractState.cov_time(execution, 1));
	}
	/**
	 * set_value(expr, value) --> set_value(expr, avalue)+ | cov_stmt(exec, 1)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_value_error(CirValueErrorState state, Collection<CirAbstractState> outputs) throws Exception {
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
		/* 4. boolean category expression for analysis */
		else if(StateMutations.is_boolean(expression)) {
			this.ext_vbool_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* 5. unsigned integer expression for analysis */
		else if(StateMutations.is_usigned(expression)) {
			this.ext_vusig_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* 6. signed integer expression for subsumption */
		else if(StateMutations.is_integer(expression)) {
			this.ext_vnumb_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* 7. double real types expression of analysis */
		else if(StateMutations.is_doubles(expression)) {
			this.ext_vreal_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* 8. address pointer expression for analysis */
		else if(StateMutations.is_address(expression)) {
			this.ext_vaddr_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
		/* 9. otherwise, coverage the statement only */
		else {
			this.ext_vauto_error(execution, expression, store_type, store_key, orig_value, muta_value, outputs);
		}
	}
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void ext_incre_error(CirIncreErrorState state, Collection<CirAbstractState> outputs) throws Exception {
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
			this.ext_inumb_error(execution, expression, store_type, store_key, base_value, difference, outputs);
		}
		/* 5. real incremental on expression location */
		else {
			this.ext_ireal_error(execution, expression, store_type, store_key, base_value, difference, outputs);
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
	private void ext_inumb_error(CirExecution execution, 
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
	private void ext_ireal_error(CirExecution execution, 
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
	}
	/**
	 * It infers the local subsumed abstract states by the input execution state
	 * @param state			the abstract execution state to subsume the output set
	 * @param outputs		to preserve the abstract state being subsumed by input
	 * @throws Exception
	 */
	private void ext_bixor_error(CirBixorErrorState state, Collection<CirAbstractState> outputs) throws Exception {
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
			this.ext_xnumb_error(execution, expression, store_type, store_key, base_value, difference, outputs);
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
	private void ext_xnumb_error(CirExecution execution, 
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
	}
	
	/* syntax-direct algorithm for local subsumption */
	/**
	 * It performs the local subsumption inference over the state
	 * @param state			the state from which the subsumed states are inferred
	 * @param outputs		to preserve the set of states being subsumed by input
	 * @throws Exception
	 */
	private void ext1(CirAbstractState state, Collection<CirAbstractState> outputs) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(state instanceof CirLimitTimesState) {
			this.ext_limit_times((CirLimitTimesState) state, outputs);
		}
		else if(state instanceof CirReachTimesState) {
			this.ext_reach_times((CirReachTimesState) state, outputs);
		}
		else if(state instanceof CirNConstrainState) {
			this.ext_n_constrain((CirNConstrainState) state, outputs);
		}
		else if(state instanceof CirMConstrainState) {
			this.ext_m_constrain((CirMConstrainState) state, outputs);
		}
		else if(state instanceof CirBlockErrorState) {
			this.ext_block_error((CirBlockErrorState) state, outputs);
		}
		else if(state instanceof CirFlowsErrorState) {
			this.ext_flows_error((CirFlowsErrorState) state, outputs);
		}
		else if(state instanceof CirTrapsErrorState) {
			this.ext_traps_error((CirTrapsErrorState) state, outputs);
		}
		else if(state instanceof CirValueErrorState) {
			this.ext_value_error((CirValueErrorState) state, outputs);
		}
		else if(state instanceof CirIncreErrorState) {
			this.ext_incre_error((CirIncreErrorState) state, outputs);
		}
		else if(state instanceof CirBixorErrorState) {
			this.ext_bixor_error((CirBixorErrorState) state, outputs);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + state.toString());
		}
	}
	/**
	 * It performs one iteration of extending the state to the outputs
	 * @param state		the state from which the extended states are created
	 * @param outputs	to preserve the states being extended from the input
	 * @throws Exception
	 */
	protected static void extend(CirAbstractState state, Collection<CirAbstractState> outputs) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			Set<CirAbstractState> buffer = new HashSet<CirAbstractState>();
			ext.ext1(state.normalize(), buffer);
			for(CirAbstractState output : buffer) {
				outputs.add(output.normalize());
			}
		}
	}
	
	
}
