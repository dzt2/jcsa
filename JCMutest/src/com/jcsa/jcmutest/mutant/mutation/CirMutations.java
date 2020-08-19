package com.jcsa.jcmutest.mutant.mutation;

import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

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
	/**
	 * @param loop_statement
	 * @return	while_to_do_while(while_condition, null)
	 * 			do_while_to_while(do_while_condition, null)
	 * @throws Exception
	 */
	public static CirMutation SWDR(CirIfStatement loop_statement) throws Exception {
		if(loop_statement.get_ast_source() instanceof AstWhileStatement) {
			return new CirMutation(MutaGroup.Statement_Mutation, MutaClass.SWDR, 
					MutaOperator.while_to_do_while, loop_statement, null);
		}
		else if(loop_statement.get_ast_source() instanceof AstDoWhileStatement) {
			return new CirMutation(MutaGroup.Statement_Mutation, MutaClass.SWDR, 
					MutaOperator.do_while_to_while, loop_statement, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + loop_statement.get_ast_source());
		}
	}
	/**
	 * @param source
	 * @param target
	 * @return set_goto_label(source_statement, target_statement)
	 * @throws Exception
	 */
	public static CirMutation SGLR(CirStatement source, CirStatement target) throws Exception {
		return new CirMutation(MutaGroup.Statement_Mutation, MutaClass.
					SGLR, MutaOperator.set_goto_label, source, target);
	}
	/**
	 * @param statement
	 * @return delete_statement(statement, null)
	 * @throws Exception
	 */
	public static CirMutation STDL(CirStatement statement) throws Exception {
		return new CirMutation(MutaGroup.Statement_Mutation, MutaClass.
				STDL, MutaOperator.delete_statement, statement, null);
	}
	/**
	 * @param expression
	 * @param difference
	 * @return inc_value(expression, difference)
	 * @throws Exception
	 */
	public static CirMutation VINC(CirExpression expression, int difference) throws Exception {
		return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.VINC,
				MutaOperator.inc_constant, expression, Integer.valueOf(difference));
	}
	/**
	 * @param expression
	 * @param multiply
	 * @return mul_value(expression, multiply)
	 * @throws Exception
	 */
	public static CirMutation VINC(CirExpression expression, double multiply) throws Exception {
		return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.VINC,
				MutaOperator.mul_constant, expression, Double.valueOf(multiply));
	}
	/**
	 * @param expression
	 * @param operator
	 * @return 	negative --> insert_arith_neg(expression, operator)
	 * 			bit_not	 --> insert_bitws_rsv(expression, operator)
	 * 			logic_not--> insert_logic_not(expression, operator)
	 * 			positive --> insert_abs_value(expression, null)
	 * 			otherwise--> insert_nabs_value(expression, null)
	 * @throws Exception
	 */
	public static CirMutation UNOI(CirExpression expression, COperator operator) throws Exception {
		switch(operator) {
		case negative:	
		{
			return new CirMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UNOI, MutaOperator.insert_arith_neg,
					expression, operator);
		}
		case bit_not:
		{
			return new CirMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UNOI, MutaOperator.insert_bitws_rsv,
					expression, operator);
		}
		case logic_not:
		{
			return new CirMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UNOI, MutaOperator.insert_logic_not,
					expression, operator);
		}
		case positive:
		{
			return new CirMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UNOI, MutaOperator.insert_abs_value,
					expression, null);
		}
		default:
		{
			return new CirMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UNOI, MutaOperator.insert_nabs_value,
					expression, null);
		}
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return set_true(expression, true) | set_false(expression, false)
	 * @throws Exception
	 */
	public static CirMutation VBRP(CirExpression expression, boolean value) throws Exception {
		if(value) {
			return new CirMutation(MutaGroup.Reference_Mutation,
					MutaClass.VBRP, MutaOperator.set_true,
					expression, Boolean.TRUE);
		}
		else {
			return new CirMutation(MutaGroup.Reference_Mutation,
					MutaClass.VBRP, MutaOperator.set_false,
					expression, Boolean.FALSE);
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return set_constant(expression, Long)
	 * @throws Exception
	 */
	public static CirMutation VCRP(CirExpression expression, long value) throws Exception {
		return new CirMutation(MutaGroup.Reference_Mutation,
				MutaClass.VCRP, MutaOperator.set_integer,
				expression, Long.valueOf(value));
	}
	/**
	 * @param expression
	 * @param name
	 * @return set_reference(expression, name)
	 * @throws Exception
	 */
	public static CirMutation VRRP(CirExpression expression, String name) throws Exception {
		return new CirMutation(MutaGroup.Reference_Mutation,
				MutaClass.VRRP, MutaOperator.set_reference,
				expression, name);
	}
	/**
	 * @param expression
	 * @param replace
	 * @return set_return(expression, replace)
	 * @throws Exception
	 */
	public static CirMutation RTRP(CirExpression expression, CirExpression replace) throws Exception {
		return new CirMutation(MutaGroup.Reference_Mutation,
				MutaClass.RTRP, MutaOperator.set_return,
				expression, replace);
	}
	/**
	 * @param expression
	 * @param parameter Long | Double | String | CirExpression
	 * @return trap_on_case(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_same(CirExpression expression, Object parameter) throws Exception {
		if(parameter instanceof Long
			|| parameter instanceof Double
			|| parameter instanceof String
			|| parameter instanceof CirExpression) {
			return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.CTRP,
							MutaOperator.trap_on_case, expression, parameter);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + parameter);
		}
	}
	/**
	 * @param expression
	 * @param parameter Long | Double | String | CirExpression
	 * @return trap_on_diff(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_diff(CirExpression expression, Object parameter) throws Exception {
		if(parameter instanceof Long
				|| parameter instanceof Double
				|| parameter instanceof String
				|| parameter instanceof CirExpression) {
				return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.VTRP,
								MutaOperator.trap_on_dif, expression, parameter);
			}
			else {
				throw new IllegalArgumentException("Invalid: " + parameter);
			}
	}
	/**
	 * @param operator
	 * @return A(arithmetic), B(bitwise), L(logical), R(relational)
	 * @throws Exception
	 */
	private static char operator_code(COperator operator) throws Exception {
		switch(operator) {
		case positive:
		case negative:
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:	return 'A';
		case bit_not:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift: return 'B';
		case logic_not:
		case logic_and:
		case logic_or:	 return 'L';
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals: return 'R';
		default: throw new IllegalArgumentException("Unsupport: " + operator);
		}
	}
	/**
	 * @param source
	 * @param operator
	 * @return set_operator(expression, operator)
	 * @throws Exception
	 */
	public static CirMutation set_operator(CirComputeExpression source, COperator operator) throws Exception {
		char sop = operator_code(source.get_operator());
		char top = operator_code(operator);
		MutaClass mclass = MutaClass.valueOf("O" + sop + top + "N");
		return new CirMutation(MutaGroup.Binary_Operator_Mutation,
				mclass, MutaOperator.set_operator, source, operator);
	}
	/**
	 * @param source
	 * @param operator
	 * @return cmp_operator(expression, operator)
	 * @throws Exception
	 */
	public static CirMutation cmp_operator(CirComputeExpression source, COperator operator) throws Exception {
		char sop = operator_code(source.get_operator());
		char top = operator_code(operator);
		MutaClass mclass = MutaClass.valueOf("O" + sop + top + "N");
		return new CirMutation(MutaGroup.Binary_Operator_Mutation,
				mclass, MutaOperator.cmp_operator, source, operator);
	}
	
}
