package com.jcsa.jcmutest.mutant.mutation;

import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It provides interfaces to create, save and parse the mutation defined
 * on C-intermediate representation language (CIR).
 * 
 * @author yukimula
 *
 */
public class CirMutations {
	
	/* factory methods */
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_true|trap_on_false(expression, boolean)
	 * @throws Exception
	 */
	public static CirMutation BTRP(CirExpression expression, boolean parameter) throws Exception {
		if(parameter) {
			return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.BTRP,
					MutaOperator.trap_on_true, expression, Boolean.TRUE);
		}
		else {
			return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.BTRP,
					MutaOperator.trap_on_false, expression, Boolean.FALSE);
		}
	}
	/**
	 * trap when expression == parameter
	 * @param expression
	 * @param parameter
	 * @return trap_on_case(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation CTRP(CirExpression expression, CirExpression parameter) throws Exception {
		return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.CTRP,
						MutaOperator.trap_on_case, expression, parameter);
	}
	/**
	 * @param expression
	 * @return trap_on_expression(expression)
	 * @throws Exception
	 */
	public static CirMutation ETRP(CirExpression expression) throws Exception {
		return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.ETRP,
						MutaOperator.trap_on_expression, expression, null);
	}
	/**
	 * @param statement
	 * @return trap_on_statement(statement)
	 * @throws Exception
	 */
	public static CirMutation STRP(CirStatement statement) throws Exception {
		return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.STRP,
						MutaOperator.trap_on_statement, statement, null);
	}
	/**
	 * @param statement
	 * @return trap_for_statement(statement, loop_time);
	 * @throws Exception
	 */
	public static CirMutation TTRP(CirStatement statement, int loop_time) throws Exception {
		return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.TTRP,
				MutaOperator.trap_for_time, statement, Integer.valueOf(loop_time));
	}
	/**
	 * @param expression
	 * @param pos_or_neg
	 * @return	pos_or_neg = true  ==> trap_on_pos(expression, null)
	 * 			pos_or_neg = false ==> trap_on_neg(expression, null)
	 * 			pos_or_neg = null  ==> trap_on_zro(expression, null)
	 * @throws Exception
	 */
	public static CirMutation VTRP(CirExpression expression, Boolean pos_or_neg) throws Exception {
		if(pos_or_neg == null) {
			return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.
						VTRP, MutaOperator.trap_on_zro, expression, null);
		}
		else if(pos_or_neg.booleanValue()) {
			return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.
						VTRP, MutaOperator.trap_on_pos, expression, null);
		}
		else {
			return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.
						VTRP, MutaOperator.trap_on_neg, expression, null);
		}
	}
	
	
	
	
	
	
	
	
	
}
