package com.jcsa.jcmutest.mutant.ast2mutant;

import com.jcsa.jcmutest.MutaClass;
import com.jcsa.jcmutest.MutaGroup;
import com.jcsa.jcmutest.MutaOperator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;

/**
 * It provides the interfaces to create the mutation on AST-nodes 
 * and read or write the mutations in the file.
 * 
 * @author yukimula
 *
 */
public class AstMutations {
	
	/* factory methods */
	/**
	 * @param expression
	 * @return trap_on_true(expression, true)
	 * @throws Exception
	 */
	public static AstMutation trap_on_true(AstExpression expression) throws Exception {
		return new AstMutation(MutaGroup.Trapping_Mutation, 
				MutaClass.BTRP, MutaOperator.trap_on_true, 
				expression, Boolean.TRUE);
	}
	/**
	 * @param expression
	 * @return trap_on_false(expression, false)
	 * @throws Exception
	 */
	public static AstMutation trap_on_false(AstExpression expression) throws Exception {
		return new AstMutation(MutaGroup.Trapping_Mutation, 
				MutaClass.BTRP, MutaOperator.trap_on_false, 
				expression, Boolean.FALSE);
	}
	/**
	 * @param switch_statement
	 * @param case_statement
	 * @return trap_on_case(switch_statement.condition, case_statement.expression) 
	 * @throws Exception
	 */
	public static AstMutation trap_on_case(AstSwitchStatement 
			switch_statement, AstCaseStatement case_statement) throws Exception {
		return new AstMutation(MutaGroup.Trapping_Mutation, 
				MutaClass.CTRP, 
				MutaOperator.trap_on_case, 
				switch_statement.get_condition(), 
				case_statement.get_expression());
	}
	/**
	 * @param expression
	 * @return trap_on_expression(expression, null)
	 * @throws Exception
	 */
	public static AstMutation trap_on_expression(AstExpression expression) throws Exception {
		return new AstMutation(MutaGroup.Trapping_Mutation,
				MutaClass.ETRP, MutaOperator.trap_on_expression,
				expression, null);
	}
	/**
	 * @param statement
	 * @return trap_on_statement(statement, null)
	 * @throws Exception
	 */
	public static AstMutation trap_on_statement(AstStatement statement) throws Exception {
		return new AstMutation(MutaGroup.Trapping_Mutation,
				MutaClass.STRP, MutaOperator.trap_on_statement,
				statement, null);
	}
	/**
	 * @param loop_statement while|do_while|for
	 * @return trap_for_time(loop_statement, loop_times)
	 * @throws Exception
	 */
	public static AstMutation trap_for_time(AstStatement loop_statement, int loop_times) throws Exception {
		return new AstMutation(MutaGroup.Trapping_Mutation, MutaClass.TTRP,
				MutaOperator.trap_for_time, loop_statement, Integer.valueOf(loop_times));
	}
	/**
	 * @param expression
	 * @return trap_on_pos(expression, null)
	 * @throws Exception
	 */
	public static AstMutation trap_on_pos(AstExpression expression) throws Exception {
		return new AstMutation(MutaGroup.Trapping_Mutation, MutaClass.VTRP,
				MutaOperator.trap_on_pos, expression, null);
	}
	/**
	 * @param expression
	 * @return trap_on_zro(expression, null)
	 * @throws Exception
	 */
	public static AstMutation trap_on_zro(AstExpression expression) throws Exception {
		return new AstMutation(MutaGroup.Trapping_Mutation, MutaClass.VTRP,
				MutaOperator.trap_on_zro, expression, null);
	}
	/**
	 * @param expression
	 * @return trap_on_neg(expression, null)
	 * @throws Exception
	 */
	public static AstMutation trap_on_neg(AstExpression expression) throws Exception {
		return new AstMutation(MutaGroup.Trapping_Mutation, MutaClass.VTRP,
				MutaOperator.trap_on_neg, expression, null);
	}
	
	
	
	
	
	
}
