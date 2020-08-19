package com.jcsa.jcmutest.mutant.mutation;

import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides interface to create the cir-mutation, which locates the
 * seeded point to the program written in C-intermediate representation
 * 
 * @author yukimula
 *
 */
public class CirMutations {
	
	/* trapping-class */
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_true|trap_on_false(expression, true|false)
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
	 * @param statement
	 * @return trap_on_statement(statement, 1)
	 * @throws Exception
	 */
	public static CirMutation STRP(CirStatement statement) throws Exception {
		return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.STRP,
				MutaOperator.trap_on_statement, statement, Integer.valueOf(1));
	}
	/**
	 * @param statement
	 * @param loop_time
	 * @return trap_on_statement(statement, loop_time)
	 * @throws Exception
	 */
	public static CirMutation STRP(CirStatement statement, int loop_time) throws Exception {
		return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.STRP,
				MutaOperator.trap_on_statement, statement, Integer.valueOf(loop_time));
	}
	/**
	 * @param statement
	 * @param operator
	 * @return	trap_on_pos(expression, positive)
	 * 			trap_on_neg(expression, negative)
	 * 			trap_on_zro(expression, otherwise)
	 * @throws Exception
	 */
	public static CirMutation VTRP(CirExpression expression, COperator operator) throws Exception {
		switch(operator) {
		case positive:	
			return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.VTRP,
								MutaOperator.trap_on_pos, expression, operator);
		case negative:	
			return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.VTRP,
								MutaOperator.trap_on_neg, expression, operator);
		default:		
			return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.VTRP,
								MutaOperator.trap_on_zro, expression, operator);
		}
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return VTRP::trap_on_case(expr, expr)
	 * @throws Exception
	 */
	public static CirMutation trap_on_case(CirExpression expression, 
			CirExpression parameter) throws Exception {
		return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.
				VTRP, MutaOperator.trap_on_case, expression, parameter);
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return VTRP::trap_on_diff(expr, expr)
	 * @throws Exception
	 */
	public static CirMutation trap_on_diff(CirExpression expression, 
			CirExpression parameter) throws Exception {
		return new CirMutation(MutaGroup.Trapping_Mutation, MutaClass.
				VTRP, MutaOperator.trap_on_dif, expression, parameter);
	}
	
	/* statement-class */
	/**
	 * @param source
	 * @param target
	 * @return set_goto_label(goto_statement, label_statement)
	 * @throws Exception
	 */
	public static CirMutation SGLR(CirStatement source, CirStatement target) throws Exception {
		return new CirMutation(MutaGroup.Statement_Mutation, MutaClass.SGLR,
				MutaOperator.set_goto_label, source, target);
	}
	/**
	 * @param loop_statement
	 * @return 	while_to_do_while(while_statement, null)
	 * 			do_while_to_while(do_while_statement, null)
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
	 * @param statement
	 * @return delete_statement(statement, null)
	 * @throws Exception
	 */
	public static CirMutation STDL(CirStatement statement) throws Exception {
		return new CirMutation(MutaGroup.Statement_Mutation, MutaClass.STDL,
							MutaOperator.delete_statement, statement, null);
	}
	
	/* unary-operator-class */
	/**
	 * @param expression
	 * @param constant
	 * @return inc_constant(expression, constant)
	 * @throws Exception
	 */
	public static CirMutation VINC(CirExpression expression, long constant) throws Exception {
		return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.VINC,
				MutaOperator.inc_constant, expression, Long.valueOf(constant));
	}
	/**
	 * @param expression
	 * @param constant
	 * @return mul_constant(expression, constant)
	 * @throws Exception
	 */
	public static CirMutation VINC(CirExpression expression, double constant) throws Exception {
		return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.VINC,
				MutaOperator.mul_constant, expression, Double.valueOf(constant));
	}
	/**
	 * @param expression
	 * @param operator
	 * @return	insert_arith_neg(expression, negative)
	 * 			insert_bitws_rsv(expression, bit_not)
	 * 			insert_logic_not(expression, logic_not)
	 * 			insert_abs_value(expression, positive)
	 * 			insert_nabs_value(expression, otherwise)
	 * 			insert_prev_inc(expression, increment)
	 * 			insert_prev_dec(expression, decrement)
	 * 			insert_post_inc(expression, arith_add)
	 * 			insert_post_dec(expression, arith_sub)
	 * @throws Exception
	 */
	public static CirMutation UNOI(CirExpression expression, COperator operator) throws Exception {
		switch(operator) {
		case negative:	
			return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.UNOI,
									MutaOperator.insert_arith_neg, expression, operator);
		case bit_not:
			return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.UNOI,
									MutaOperator.insert_bitws_rsv, expression, operator);
		case logic_not:
			return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.UNOI,
									MutaOperator.insert_logic_not, expression, operator);
		case positive:
			return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.UNOI,
									MutaOperator.insert_abs_value, expression, operator);
		case increment:
			return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.UNOI,
									MutaOperator.insert_prev_inc, expression, operator);
		case decrement:
			return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.UNOI,
									MutaOperator.insert_prev_dec, expression, operator);
		case arith_add:
			return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.UNOI,
									MutaOperator.insert_post_inc, expression, operator);
		case arith_sub:
			return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.UNOI,
									MutaOperator.insert_post_dec, expression, operator);
		default: 
			return new CirMutation(MutaGroup.Unary_Operator_Mutation, MutaClass.UNOI,
									MutaOperator.insert_nabs_value, expression, operator);
		}
	}
	
	/* reference-class */
	/**
	 * @param expression
	 * @param parameter
	 * @return set_true|set_false(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation VBRP(CirExpression expression, boolean parameter) throws Exception {
		if(parameter) {
			return new CirMutation(MutaGroup.Reference_Mutation, MutaClass.VBRP,
								MutaOperator.set_true, expression, Boolean.TRUE);
		}
		else {
			return new CirMutation(MutaGroup.Reference_Mutation, MutaClass.VBRP,
								MutaOperator.set_false, expression, Boolean.FALSE);
		}
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_integer(expression, constant)
	 * @throws Exception
	 */
	public static CirMutation VCRP(CirExpression expression, long parameter) throws Exception {
		return new CirMutation(MutaGroup.Reference_Mutation, MutaClass.VCRP,
				MutaOperator.set_integer, expression, Long.valueOf(parameter));
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_double(expression, constant)
	 * @throws Exception
	 */
	public static CirMutation VCRP(CirExpression expression, double parameter) throws Exception {
		return new CirMutation(MutaGroup.Reference_Mutation, MutaClass.VCRP,
				MutaOperator.set_double, expression, Double.valueOf(parameter));
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_reference(expression, name)
	 * @throws Excception
	 */
	public static CirMutation VRRP(CirExpression expression, String parameter) throws Exception {
		return new CirMutation(MutaGroup.Reference_Mutation, MutaClass.VRRP,
				MutaOperator.set_reference, expression, parameter.toString());
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_reference(expression, cir_node)
	 * @throws Exception
	 */
	public static CirMutation VRRP(CirExpression expression, CirExpression parameter) throws Exception {
		return new CirMutation(MutaGroup.Reference_Mutation, MutaClass.VRRP,
							MutaOperator.set_reference, expression, parameter);
	}
	
	/* operator-class */
	private static String operator_name(COperator operator) throws Exception {
		switch(operator) {
		case positive:
		case negative:
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:		return "A";
		case bit_not:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:	return "B";
		case logic_not:
		case logic_and:
		case logic_or:		return "L";
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:	return "R";
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	/**
	 * @param expression
	 * @param operator
	 * @return OXXN::set_operator(expression, operator)
	 * @throws Exception
	 */
	public static CirMutation OXXN(CirExpression expression, COperator operator) throws Exception {
		AstBinaryExpression source = 
						(AstBinaryExpression) expression.get_ast_source();
		String sop = operator_name(source.get_operator().get_operator());
		String top = operator_name(operator);
		MutaClass mclass = MutaClass.valueOf("O" + sop + top + "N");
		return new CirMutation(MutaGroup.Binary_Operator_Mutation, 
				mclass, MutaOperator.set_operator, expression, operator);
	}
	
}
