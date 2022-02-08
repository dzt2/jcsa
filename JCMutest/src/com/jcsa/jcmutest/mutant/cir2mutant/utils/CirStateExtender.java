package com.jcsa.jcmutest.mutant.cir2mutant.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBixorErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraintState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirCoverTimesState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirDataErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirFlowsErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirIncreErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirPathErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirSyMutationState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirTrapsErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirValueErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It implements the local extension of the CirAbstractState to a series of 
 * extended states as its annotations.
 * 
 * @author yukimula
 *
 */
public final class CirStateExtender {
	
	/* singleton mode */ /** constructor **/ private CirStateExtender() { }
	private static final CirStateExtender extender = new CirStateExtender();
	
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
			if(CirMutations.is_doubles(data_type)) {
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
	
	/* extension for conditional and path-error states */
	/**
	 * cov_time(exec, times) --> eva_expr(exec, True) | cov_time(exec, times/2)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_cov_times(CirCoverTimesState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		int times = this.get_smaller_maximal_times(state.get_executed_times());
		if(state.is_reach_coverage() && times >= 1) {
			outputs.add(CirAbstractState.cov_time(execution, times));
		}
	}
	/**
	 * eva_expr(exec, expression) --> { eva_expr(exec, subsumed_condition)+ } | { cov_time(exec, 1) }
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_constrain(CirConstraintState state, Collection<CirAbstractState> outputs) throws Exception {
		/* 1. declarations and subsumed conditions getter */
		CirExecution execution = state.get_execution();
		Collection<SymbolExpression> subsumed_conditions = this.
				derive_subsummed_conditions(state.get_condition());
		
		/* 2. in case that the state is must-constraint */
		if(state.is_must_constrain()) {
			for(SymbolExpression condition : subsumed_conditions) {
				outputs.add(CirAbstractState.eva_must(execution, condition));
			}
			outputs.add(CirAbstractState.eva_need(execution, state.get_condition()));
		}
		/* 3. in case that the state is need-constraint */
		else {
			if(subsumed_conditions.isEmpty()) {
				outputs.add(CirAbstractState.cov_time(execution, 1));
			}
			else {
				for(SymbolExpression subsumed_condition : subsumed_conditions) {
					outputs.add(CirAbstractState.eva_need(execution, subsumed_condition));
				}
			}
		}
	}
	/**
	 * ast_muta(exec, mutation) --> cov_stmt(exec, 1)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_sy_mutant(CirSyMutationState state, Collection<CirAbstractState> outputs) throws Exception {
		outputs.add(CirAbstractState.cov_time(state.get_execution(), 1));
	}
	/**
	 * mut_stmt(exec, boolean) --> cov_stmt(execution, 1)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_blc_error(CirBlockErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		if(state.get_execution().get_statement() instanceof CirTagStatement) {
			/* invalid tag-statement is no available location */
		}
		else if(state.is_original_executed()) {
			outputs.add(CirAbstractState.cov_time(state.get_execution(), 1));
		}
		else {
			/* not to consider the statement not executed in original version */
		}
	}
	/**
	 * mut_flow(exec) --> cov_stmt(exec, 1)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_flw_error(CirFlowsErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* 1. declarations and initializations */
		CirExecutionPath orig_path = CirMutations.oublock_post_path(state.get_orig_target());
		CirExecutionPath muta_path = CirMutations.oublock_post_path(state.get_muta_target());
		
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
				outputs.add(CirAbstractState.mut_stmt(execution, false));
		}
		for(CirExecution execution : muta_executions) {
			if(!(execution.get_statement() instanceof CirTagStatement))
				outputs.add(CirAbstractState.mut_stmt(execution, true));
		}
		outputs.add(CirAbstractState.cov_time(state.get_execution(), 1));
	}
	/**
	 * trp_stmt(execution) --> none
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_trp_error(CirTrapsErrorState state, Collection<CirAbstractState> outputs) throws Exception { }
	
	/* extension for value error states */
	/**
	 * @param execution		the execution point where the value error is defined
	 * @param expression	the expression to be replaced with the mutated value
	 * @param is_virtual	whether this location is virtual definition location
	 * @param orig_value	the original value hold by the original expression
	 * @param muta_value	the mutation value to replace with original version
	 * @param outputs		to preserve the extended states from the initial one
	 * @throws Exception
	 */
	private void ext_vbool_error(CirExecution execution, CirExpression expression,
			boolean is_virtual, SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* 1. infer the abstract mutation value domains */
		Set<SymbolExpression> muvalues = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			if(((SymbolConstant) muta_value).get_bool()) {
				muvalues.add(CirMutations.true_value);
			}
			else {
				muvalues.add(CirMutations.fals_value);
			}
		}
		else if(muta_value == CirMutations.true_value) {
			muvalues.add(CirMutations.bool_value);
		}
		else if(muta_value == CirMutations.fals_value) {
			muvalues.add(CirMutations.bool_value);
		}
		else if(muta_value == CirMutations.bool_value) { }
		else {
			muvalues.add(CirMutations.bool_value);
		}
		
		/* 2. in case that no subsumed error value to cover */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		/* 3. otherwise, to generate abstract value error states */
		else {
			for(SymbolExpression muvalue : muvalues) {
				if(is_virtual) {
					outputs.add(CirAbstractState.set_vdef(expression, muvalue));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, muvalue));
				}
			}
		}
	}
	/**
	 * @param execution		the execution point where the value error is defined
	 * @param expression	the expression to be replaced with the mutated value
	 * @param is_virtual	whether this location is virtual definition location
	 * @param orig_value	the original value hold by the original expression
	 * @param muta_value	the mutation value to replace with original version
	 * @param outputs		to preserve the extended states from the initial one
	 * @throws Exception
	 */
	private void ext_vusig_error(CirExecution execution, CirExpression expression,
			boolean is_virtual, SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* 1. infer the abstract mutation value domains */
		Set<SymbolExpression> muvalues = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			if(((SymbolConstant) muta_value).get_long() == 0) {
				muvalues.add(CirMutations.zero_value);
			}
			else {
				muvalues.add(CirMutations.post_value);
			}
		}
		else if(muta_value == CirMutations.nzro_value) {
			muvalues.add(CirMutations.nneg_value);
		}
		else if(muta_value == CirMutations.zero_value) {
			muvalues.add(CirMutations.nneg_value);
		}
		else if(muta_value == CirMutations.nneg_value) { }
		else {
			muvalues.add(CirMutations.nneg_value);
		}
		
		/* 2. in case that no subsumed error value to cover */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		/* 3. otherwise, to generate abstract value error states */
		else {
			for(SymbolExpression muvalue : muvalues) {
				if(is_virtual) {
					outputs.add(CirAbstractState.set_vdef(expression, muvalue));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, muvalue));
				}
			}
		}
	}
	/**
	 * @param execution		the execution point where the value error is defined
	 * @param expression	the expression to be replaced with the mutated value
	 * @param is_virtual	whether this location is virtual definition location
	 * @param orig_value	the original value hold by the original expression
	 * @param muta_value	the mutation value to replace with original version
	 * @param outputs		to preserve the extended states from the initial one
	 * @throws Exception
	 */
	private void ext_vnumb_error(CirExecution execution, CirExpression expression,
			boolean is_virtual, SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* 1. infer the abstract mutation value domains */
		Set<SymbolExpression> muvalues = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			long constant = ((SymbolConstant) muta_value).get_long();
			if(constant > 0) {
				muvalues.add(CirMutations.post_value);
			}
			else if(constant < 0) {
				muvalues.add(CirMutations.negt_value);
			}
			else {
				muvalues.add(CirMutations.zero_value);
			}
		}
		else if(muta_value == CirMutations.post_value) {
			muvalues.add(CirMutations.nneg_value);
			muvalues.add(CirMutations.nzro_value);
		}
		else if(muta_value == CirMutations.zero_value) {
			muvalues.add(CirMutations.nneg_value);
			muvalues.add(CirMutations.npos_value);
		}
		else if(muta_value == CirMutations.negt_value) {
			muvalues.add(CirMutations.npos_value);
			muvalues.add(CirMutations.nzro_value);
		}
		else if(muta_value == CirMutations.npos_value) {
			muvalues.add(CirMutations.numb_value);
		}
		else if(muta_value == CirMutations.nzro_value) {
			muvalues.add(CirMutations.numb_value);
		}
		else if(muta_value == CirMutations.nneg_value) {
			muvalues.add(CirMutations.numb_value);
		}
		else if(muta_value == CirMutations.numb_value) { }
		else {
			muvalues.add(CirMutations.numb_value);
		}
		
		/* 2. in case that no subsumed error value to cover */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		/* 3. otherwise, to generate abstract value error states */
		else {
			for(SymbolExpression muvalue : muvalues) {
				if(is_virtual) {
					outputs.add(CirAbstractState.set_vdef(expression, muvalue));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, muvalue));
				}
			}
		}
	}
	/**
	 * @param execution		the execution point where the value error is defined
	 * @param expression	the expression to be replaced with the mutated value
	 * @param is_virtual	whether this location is virtual definition location
	 * @param orig_value	the original value hold by the original expression
	 * @param muta_value	the mutation value to replace with original version
	 * @param outputs		to preserve the extended states from the initial one
	 * @throws Exception
	 */
	private void ext_vreal_error(CirExecution execution, CirExpression expression,
			boolean is_virtual, SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* 1. infer the abstract mutation value domains */
		Set<SymbolExpression> muvalues = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			double constant = ((SymbolConstant) muta_value).get_double();
			if(constant > 0) {
				muvalues.add(CirMutations.post_value);
			}
			else if(constant < 0) {
				muvalues.add(CirMutations.negt_value);
			}
			else {
				muvalues.add(CirMutations.zero_value);
			}
		}
		else if(muta_value == CirMutations.post_value) {
			muvalues.add(CirMutations.nneg_value);
			muvalues.add(CirMutations.nzro_value);
		}
		else if(muta_value == CirMutations.zero_value) {
			muvalues.add(CirMutations.nneg_value);
			muvalues.add(CirMutations.npos_value);
		}
		else if(muta_value == CirMutations.negt_value) {
			muvalues.add(CirMutations.npos_value);
			muvalues.add(CirMutations.nzro_value);
		}
		else if(muta_value == CirMutations.npos_value) {
			muvalues.add(CirMutations.numb_value);
		}
		else if(muta_value == CirMutations.nzro_value) {
			muvalues.add(CirMutations.numb_value);
		}
		else if(muta_value == CirMutations.nneg_value) {
			muvalues.add(CirMutations.numb_value);
		}
		else if(muta_value == CirMutations.numb_value) { }
		else {
			muvalues.add(CirMutations.numb_value);
		}
		
		/* 2. in case that no subsumed error value to cover */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		/* 3. otherwise, to generate abstract value error states */
		else {
			for(SymbolExpression muvalue : muvalues) {
				if(is_virtual) {
					outputs.add(CirAbstractState.set_vdef(expression, muvalue));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, muvalue));
				}
			}
		}
	}
	/**
	 * @param execution		the execution point where the value error is defined
	 * @param expression	the expression to be replaced with the mutated value
	 * @param is_virtual	whether this location is virtual definition location
	 * @param orig_value	the original value hold by the original expression
	 * @param muta_value	the mutation value to replace with original version
	 * @param outputs		to preserve the extended states from the initial one
	 * @throws Exception
	 */
	private void ext_vaddr_error(CirExecution execution, CirExpression expression,
			boolean is_virtual, SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		/* 1. infer the abstract mutation value domains */
		Set<SymbolExpression> muvalues = new HashSet<SymbolExpression>();
		if(muta_value instanceof SymbolConstant) {
			long constant = ((SymbolConstant) muta_value).get_long();
			if(constant == 0) {
				muvalues.add(CirMutations.null_value);
			}
			else {
				muvalues.add(CirMutations.nnul_value);
			}
		}
		else if(muta_value == CirMutations.null_value) {
			muvalues.add(CirMutations.addr_value);
		}
		else if(muta_value == CirMutations.nnul_value) {
			muvalues.add(CirMutations.addr_value);
		}
		else if(muta_value == CirMutations.addr_value) { }
		else {
			muvalues.add(CirMutations.nnul_value);
		}
		
		/* 2. in case that no subsumed error value to cover */
		if(muvalues.isEmpty()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		/* 3. otherwise, to generate abstract value error states */
		else {
			for(SymbolExpression muvalue : muvalues) {
				if(is_virtual) {
					outputs.add(CirAbstractState.set_vdef(expression, muvalue));
				}
				else {
					outputs.add(CirAbstractState.set_expr(expression, muvalue));
				}
			}
		}
	}
	/**
	 * @param execution		the execution point where the value error is defined
	 * @param expression	the expression to be replaced with the mutated value
	 * @param is_virtual	whether this location is virtual definition location
	 * @param orig_value	the original value hold by the original expression
	 * @param muta_value	the mutation value to replace with original version
	 * @param outputs		to preserve the extended states from the initial one
	 * @throws Exception
	 */
	private void ext_vauto_error(CirExecution execution, CirExpression expression,
			boolean is_virtual, SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<CirAbstractState> outputs) throws Exception {
		outputs.add(CirAbstractState.cov_time(execution, 1));
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_value_error(CirValueErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* 1. declarations and the data elements in the input state */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		SymbolExpression orig_value = state.get_orig_value();
		SymbolExpression muta_value = state.get_muta_value();
		boolean is_virtual = !state.has_expression();
		
		/* 2. trap_value |--> TRAP(execution) */
		if(StateMutations.is_trap_value(muta_value)) {
			outputs.add(CirAbstractState.trp_stmt(execution));
		}
		/* 3. equivalent value |--> FALSE at execution */
		else if(orig_value.equals(muta_value)) {
			outputs.add(CirAbstractState.eva_need(execution, Boolean.FALSE));
		}
		/* 4. boolean category expression for analysis */
		else if(StateMutations.is_boolean(expression)) {
			this.ext_vbool_error(execution, expression, is_virtual, orig_value, muta_value, outputs);
		}
		/* 5. unsigned integer expression for analysis */
		else if(StateMutations.is_usigned(expression)) {
			this.ext_vusig_error(execution, expression, is_virtual, orig_value, muta_value, outputs);
		}
		/* 6. signed integer expression for subsumption */
		else if(StateMutations.is_integer(expression)) {
			this.ext_vnumb_error(execution, expression, is_virtual, orig_value, muta_value, outputs);
		}
		/* 7. double real types expression of analysis */
		else if(StateMutations.is_doubles(expression)) {
			this.ext_vreal_error(execution, expression, is_virtual, orig_value, muta_value, outputs);
		}
		/* 8. address pointer expression for analysis */
		else if(StateMutations.is_address(expression)) {
			this.ext_vaddr_error(execution, expression, is_virtual, orig_value, muta_value, outputs);
		}
		/* 9. otherwise, coverage the statement only */
		else {
			this.ext_vauto_error(execution, expression, is_virtual, orig_value, muta_value, outputs);
		}
		
		
	}
	
	/* extension for differential error states */
	/**
	 * @param execution		the execution point where the value error is defined
	 * @param expression	the expression to be replaced with the mutated value
	 * @param is_virtual	whether this location is virtual definition location
	 * @param base_value	the original value hold by the original expression
	 * @param difference	the mutation value to increase into the original one
	 * @param outputs		to preserve the extended states from the initial one
	 * @throws Exception
	 */
	private void ext_inumb_error(CirExecution execution, CirExpression expression,
			boolean is_virtual, SymbolExpression base_value, SymbolExpression difference,
			Collection<CirAbstractState> outputs) throws Exception {
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
				if(is_virtual) {
					outputs.add(CirAbstractState.inc_vdef(expression, error));
				}
				else {
					outputs.add(CirAbstractState.inc_expr(expression, error));
				}
			}
		}
	}
	/**
	 * @param execution		the execution point where the value error is defined
	 * @param expression	the expression to be replaced with the mutated value
	 * @param is_virtual	whether this location is virtual definition location
	 * @param base_value	the original value hold by the original expression
	 * @param difference	the mutation value to increase into the original one
	 * @param outputs		to preserve the extended states from the initial one
	 * @throws Exception
	 */
	private void ext_ireal_error(CirExecution execution, CirExpression expression,
			boolean is_virtual, SymbolExpression base_value, SymbolExpression difference,
			Collection<CirAbstractState> outputs) throws Exception {
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
				if(is_virtual) {
					outputs.add(CirAbstractState.inc_vdef(expression, error));
				}
				else {
					outputs.add(CirAbstractState.inc_expr(expression, error));
				}
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_incre_error(CirIncreErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations and data element getters from state */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		SymbolExpression base_value = state.get_orig_value();
		SymbolExpression difference = state.get_difference();
		boolean is_virtual = !state.has_expression();
		
		/* 2. trap_value |--> TRAP(execution) */
		if(StateMutations.is_trap_value(difference)) {
			outputs.add(CirAbstractState.trp_stmt(execution));
		}
		/* 3. equivalent value |--> FALSE at execution */
		else if(this.is_zero_constant(difference)) {
			outputs.add(CirAbstractState.eva_need(execution, Boolean.FALSE));
		}
		/* 4. as integer incremental expression location */
		else if(StateMutations.is_integer(expression)) {
			this.ext_inumb_error(execution, expression, is_virtual, base_value, difference, outputs);
		}
		/* 5. real incremental on expression location */
		else {
			this.ext_ireal_error(execution, expression, is_virtual, base_value, difference, outputs);
		}
	}
	/**
	 * @param execution		the execution point where the value error is defined
	 * @param expression	the expression to be replaced with the mutated value
	 * @param is_virtual	whether this location is virtual definition location
	 * @param base_value	the original value hold by the original expression
	 * @param difference	the mutation value to increase into the original one
	 * @param outputs		to preserve the extended states from the initial one
	 * @throws Exception
	 */
	private void ext_xnumb_error(CirExecution execution, CirExpression expression,
			boolean is_virtual, SymbolExpression base_value, SymbolExpression difference,
			Collection<CirAbstractState> outputs) throws Exception {
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
				if(is_virtual) {
					outputs.add(CirAbstractState.xor_vdef(expression, error));
				}
				else {
					outputs.add(CirAbstractState.xor_expr(expression, error));
				}
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_bixor_error(CirBixorErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* declarations and data element getters from state */
		CirExecution execution = state.get_execution();
		CirExpression expression = state.get_expression();
		SymbolExpression base_value = state.get_orig_value();
		SymbolExpression difference = state.get_difference();
		boolean is_virtual = !state.has_expression();
		
		/* 2. trap_value |--> TRAP(execution) */
		if(StateMutations.is_trap_value(difference)) {
			outputs.add(CirAbstractState.trp_stmt(execution));
		}
		/* 3. equivalent value |--> FALSE at execution */
		else if(this.is_zero_constant(difference)) {
			outputs.add(CirAbstractState.eva_need(execution, Boolean.FALSE));
		}
		/* 4. as integer incremental expression location */
		else {
			this.ext_xnumb_error(execution, expression, is_virtual, base_value, difference, outputs);
		}
	}
	
	/* interfaces */
	/**
	 * It performs one iteration of extension from the input state to the outputs
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void ext_one(CirAbstractState state, Collection<CirAbstractState> outputs) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(state instanceof CirCoverTimesState) {
			this.ext_cov_times((CirCoverTimesState) state, outputs);
		}
		else if(state instanceof CirConstraintState) {
			this.ext_constrain((CirConstraintState) state, outputs);
		}
		else if(state instanceof CirSyMutationState) {
			this.ext_sy_mutant((CirSyMutationState) state, outputs);
		}
		else if(state instanceof CirBlockErrorState) {
			this.ext_blc_error((CirBlockErrorState) state, outputs);
		}
		else if(state instanceof CirFlowsErrorState) {
			this.ext_flw_error((CirFlowsErrorState) state, outputs);
		}
		else if(state instanceof CirTrapsErrorState) {
			this.ext_trp_error((CirTrapsErrorState) state, outputs);
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
	 * It extends the state and preserves the set of extended ones to the outputs
	 * @param state
	 * @param outputs
	 * @param all_or_one	True if extending for all; False if extending for once
	 * @throws Exception
	 */
	public static void extend(CirAbstractState state, Collection<CirAbstractState> outputs, boolean all_or_one) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(all_or_one) {
			/* declarations for BFS-traversal */
			Queue<CirAbstractState> queue = new LinkedList<CirAbstractState>();
			Set<CirAbstractState> records = new HashSet<CirAbstractState>();
			queue.add(state); 
			while(!queue.isEmpty()) {
				CirAbstractState parent = queue.poll();
				records.add(parent);
				Set<CirAbstractState> buffer = new HashSet<CirAbstractState>();
				extender.ext_one(parent, buffer);
				for(CirAbstractState child : buffer) {
					child = child.normalize();
					if(!records.contains(child)) {
						queue.add(child);
					}
				}
			}
			records.remove(state);
			outputs.addAll(records);
		}
		else {
			Set<CirAbstractState> buffer = new HashSet<CirAbstractState>();
			extender.ext_one(state.normalize(), buffer);
			for(CirAbstractState output : buffer) {
				outputs.add(output.normalize());
			}
		}
	}
	/**
	 * It infers the direclty subsumed states from the input state under context
	 * @param state		the input state from which the subsumed states are created
	 * @param outputs	to preserve the directly subsumed states from the input
	 * @param context	CDependGraph | CirExecutionPath | CStatePath | null
	 * @throws Exception
	 */
	public static void subsume(CirAbstractState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			Set<CirAbstractState> buffer = new HashSet<CirAbstractState>();
			// state = state.normalize();
			if(state instanceof CirConditionState) {
				CirCondStateInference.infer((CirConditionState) state, buffer, context);
			}
			else if(state instanceof CirPathErrorState) {
				CirPathStateInference.infer((CirPathErrorState) state, buffer, context);
			}
			else if(state instanceof CirDataErrorState) {
				CirDataStateInference.infer((CirDataErrorState) state, buffer, context);
			}
			else {
				throw new IllegalArgumentException("Unsupported: " + state);
			}
			for(CirAbstractState output : buffer) { outputs.add(output.normalize()); }
		}
	}
	
}
