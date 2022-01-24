package com.jcsa.jcmutest.mutant.sta2mutant.__back__;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirLimitTimesState;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * This class implements the inference (subsumption) of CirAbstractState in the
 * original C-intermediate representative program location.
 * 
 * @author yukimula
 *
 */
public final class CirStateLocalInference {
	
	/* singleton mode */ /** constructor **/ private CirStateLocalInference() {}
	private static final CirStateLocalInference local_inference = new CirStateLocalInference();
	
	/* basic method to supporting */
	/**
	 * @param limits
	 * @return (limits / 2) or 0
	 */
	private int get_smaller_times(int limits) {
		int times = 1;
		while(times <= limits) {
			times = times * 2;
		}
		times = times / 2;
		return times;
	}
	/**
	 * @return whether the symbolic expression is constant-zero
	 */
	private boolean is_zero_constant(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(expression instanceof SymbolConstant) {
			SymbolConstant constant = (SymbolConstant) expression;
			CType data_type = expression.get_data_type();
			if(StateMutations.is_integer(data_type)) {
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
	 * It collects the set of sub-conditions in the term of conjunctive
	 * @param expression		
	 * @param sub_conditions	to preserve the sub-conditions in the expression
	 * @throws Exception
	 */
	private void get_conditions_in_conjunctive(SymbolExpression expression,
			Collection<SymbolExpression> sub_conditions) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(sub_conditions == null) {
			throw new IllegalArgumentException("Invalid sub_conditions: null");
		}
		else if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) { 	/* TRUE is Redundant */ }
			else {											/* FALSE is the only */
				sub_conditions.clear();
				sub_conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(operator == COperator.logic_and) {			/* Conjunctive divide */
				this.get_conditions_in_conjunctive(loperand, sub_conditions);
				this.get_conditions_in_conjunctive(roperand, sub_conditions);
			}
			else {											/* otherwise */
				sub_conditions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else {												/* otherwise */
			sub_conditions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * It derives the directly subsumed conditions from the input expression
	 * @param expression			the input symbolic condition
	 * @param subsumed_conditions	to preserve the directly subsumed by condition
	 * @throws Exception
	 */
	private void get_subsumed_conditions_from(SymbolExpression expression,
			Collection<SymbolExpression> subsumed_conditions) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression as null");
		}
		else if(subsumed_conditions == null) {
			throw new IllegalArgumentException("Invalid subsumed_conditions");
		}
		else {
			/* I. derive the sub-conditions from conjunctive and initialization */
			Set<SymbolExpression> sub_conditions = new HashSet<SymbolExpression>();
			this.get_conditions_in_conjunctive(expression, sub_conditions);
			subsumed_conditions.clear();
			
			/* II. the original expression is TRUE */
			if(sub_conditions.isEmpty()) { return; }
			/* III. only one expression exists in input conjunctive expression */
			else if(sub_conditions.size() == 1) {
				SymbolExpression condition = sub_conditions.iterator().next();
				if(condition instanceof SymbolBinaryExpression) {
					COperator operator = ((SymbolBinaryExpression) condition).get_operator().get_operator();
					SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
					SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
					if(operator == COperator.greater_tn) {
						subsumed_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
						subsumed_conditions.add(SymbolFactory.not_equals(loperand, roperand));
					}
					else if(operator == COperator.smaller_tn) {
						subsumed_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
						subsumed_conditions.add(SymbolFactory.not_equals(loperand, roperand));
					}
					else if(operator == COperator.equal_with) {
						subsumed_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
						subsumed_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
					}
					else {
						subsumed_conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
					}
				}
				else {
					subsumed_conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
				}
			}
			/* IV. the input expression is logical conjunctive and return it */
			else { subsumed_conditions.addAll(sub_conditions); }
		}
	}
	
	/* local inference of subsumption */
	/**
	 * limit_times(execution, times) |--> {}
	 * 
	 * @param state		the state to subsume the output states in second set
	 * @param outputs	to preserve the set of states subsumed by the inputs
	 * @throws Exception
	 */
	private void linf_limit_times(CirLimitTimesState state, Collection<CirAbstractState> outputs) throws Exception {
		/* 1. derivation and initialization */
		CirExecution execution = state.get_execution();
		int maximal_times = state.get_maximal_times();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
