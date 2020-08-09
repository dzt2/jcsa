package com.jcsa.jcmutest.mutant.ast2mutant;

import com.jcsa.jcmutest.MutaClass;
import com.jcsa.jcmutest.MutaGroup;
import com.jcsa.jcmutest.MutaOperator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

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
	/**
	 * @param statement
	 * @return break_to_continue(break_statement, null)
	 * @throws Exception
	 */
	public static AstMutation break_to_continue(AstBreakStatement statement) throws Exception {
		AstNode location = statement;
		while(location != null) {
			if(location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement
				|| location instanceof AstForStatement) {
				return new AstMutation(MutaGroup.Statement_Mutation,
						MutaClass.SBCR, MutaOperator.break_to_continue,
						statement, null);
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in loop-statement");
	}
	/**
	 * @param statement
	 * @return continue_to_break(continue_statement, null)
	 * @throws Exception
	 */
	public static AstMutation continue_to_break(AstContinueStatement statement) throws Exception {
		AstNode location = statement;
		while(location != null) {
			if(location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement
				|| location instanceof AstForStatement) {
				return new AstMutation(MutaGroup.Statement_Mutation,
						MutaClass.SBCR, MutaOperator.continue_to_break,
						statement, null);
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in loop-statement");
	}
	/**
	 * @param statement
	 * @return while_to_do_while(while_statement, null)
	 * @throws Exception
	 */
	public static AstMutation while_to_do_while(AstWhileStatement statement) throws Exception {
		return new AstMutation(MutaGroup.Statement_Mutation,
				MutaClass.SWDR, MutaOperator.while_to_do_while,
				statement, null);
	}
	/**
	 * @param statement
	 * @return do_while_to_while(do_while_statement, null)
	 * @throws Exception
	 */
	public static AstMutation do_while_to_while(AstDoWhileStatement statement) throws Exception {
		return new AstMutation(MutaGroup.Statement_Mutation,
				MutaClass.SWDR, MutaOperator.do_while_to_while,
				statement, null);
	}
	/**
	 * @param source_statement
	 * @param target_statement
	 * @return set_goto_label(goto_statement.label, labeled_statement.label)
	 * @throws Exception
	 */
	public static AstMutation set_goto_label(AstGotoStatement source_statement,
			AstLabeledStatement target_statement) throws Exception {
		return new AstMutation(MutaGroup.Statement_Mutation,
				MutaClass.SGLR, MutaOperator.set_goto_label,
				source_statement.get_label(), target_statement.get_label());
	}
	/**
	 * @param statement
	 * @return delete_statement(statement, null)
	 * @throws Exception
	 */
	public static AstMutation delete_statement(AstStatement statement) throws Exception {
		return new AstMutation(MutaGroup.Statement_Mutation,
				MutaClass.STDL, MutaOperator.delete_statement,
				statement, null);
	}
	/**
	 * @param expression
	 * @return prev_inc_to_prev_dec(prev_inc_expression, decrement)
	 * @throws Exception
	 */
	public static AstMutation prev_inc_to_prev_dec(AstIncreUnaryExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.increment) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.prev_inc_to_prev_dec,
					expression, COperator.decrement);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_dec");
		}
	}
	/**
	 * @param expression
	 * @return prev_inc_to_post_dec(prev_inc_expression, decrement)
	 * @throws Exception
	 */
	public static AstMutation prev_inc_to_post_dec(AstIncreUnaryExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.increment) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.prev_inc_to_post_dec,
					expression, COperator.decrement);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_dec");
		}
	}
	/**
	 * @param expression
	 * @return prev_inc_to_post_inc(prev_inc_expression, increment)
	 * @throws Exception
	 */
	public static AstMutation prev_inc_to_post_inc(AstIncreUnaryExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.increment) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.prev_inc_to_post_inc,
					expression, COperator.increment);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_dec");
		}
	}
	/**
	 * @param expression
	 * @return prev_dec_to_prev_inc(prev_dec_expression, increment)
	 * @throws Exception
	 */
	public static AstMutation prev_dec_to_prev_inc(AstIncreUnaryExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.decrement) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.prev_dec_to_prev_inc,
					expression, COperator.increment);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_inc");
		}
	}
	/**
	 * @param expression
	 * @return prev_dec_to_post_inc(prev_dec_expression, increment)
	 * @throws Exception
	 */
	public static AstMutation prev_dec_to_post_inc(AstIncreUnaryExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.decrement) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.prev_dec_to_post_inc,
					expression, COperator.increment);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_inc");
		}
	}
	/**
	 * @param expression
	 * @return prev_dec_to_post_dec(prev_dec_expression, decrement)
	 * @throws Exception
	 */
	public static AstMutation prev_dec_to_post_dec(AstIncreUnaryExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.decrement) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.prev_dec_to_post_dec,
					expression, COperator.decrement);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_inc");
		}
	}
	/**
	 * @param expression
	 * @return post_inc_to_post_dec(post_inc_expression, decrement)
	 * @throws Exception
	 */
	public static AstMutation post_inc_to_post_dec(AstIncrePostfixExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.increment) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.post_inc_to_post_dec,
					expression, COperator.decrement);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_dec");
		}
	}
	/**
	 * @param expression
	 * @return post_inc_to_prev_dec(post_inc_expression, decrement)
	 * @throws Exception
	 */
	public static AstMutation post_inc_to_prev_dec(AstIncrePostfixExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.increment) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.post_inc_to_prev_dec,
					expression, COperator.decrement);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_dec");
		}
	}
	/**
	 * @param expression
	 * @return post_inc_to_prev_inc(post_inc_expression, increment)
	 * @throws Exception
	 */
	public static AstMutation post_inc_to_prev_inc(AstIncrePostfixExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.increment) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.post_inc_to_prev_inc,
					expression, COperator.increment);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_dec");
		}
	}
	/**
	 * @param expression
	 * @return post_dec_to_post_inc(post_dec_expression, increment)
	 * @throws Exception
	 */
	public static AstMutation post_dec_to_post_inc(AstIncrePostfixExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.decrement) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.post_dec_to_post_inc,
					expression, COperator.increment);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_dec");
		}
	}
	/**
	 * @param expression
	 * @return post_dec_to_prev_inc(post_dec_expression, increment)
	 * @throws Exception
	 */
	public static AstMutation post_dec_to_prev_inc(AstIncrePostfixExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.decrement) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.post_dec_to_prev_inc,
					expression, COperator.increment);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_dec");
		}
	}
	/**
	 * @param expression
	 * @return post_dec_to_prev_dec(post_dec_expression, decrement)
	 * @throws Exception
	 */
	public static AstMutation post_dec_to_prev_dec(AstIncrePostfixExpression expression) throws Exception {
		if(expression.get_operator().get_operator() == COperator.decrement) {
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UIOR, MutaOperator.post_dec_to_prev_dec,
					expression, COperator.decrement);
		}
		else {
			throw new IllegalArgumentException("Invalid expression as prev_dec");
		}
	}
	/**
	 * @param expression
	 * @return insert_prev_inc(reference, increment)
	 * @throws Exception
	 */
	public static AstMutation insert_prev_inc(AstExpression expression) throws Exception {
		return new AstMutation(MutaGroup.Unary_Operator_Mutation,
				MutaClass.UIOI, MutaOperator.insert_prev_inc,
				expression, COperator.increment);
	}
	/**
	 * @param expression
	 * @return insert_post_inc(reference, increment)
	 * @throws Exception
	 */
	public static AstMutation insert_post_inc(AstExpression expression) throws Exception {
		return new AstMutation(MutaGroup.Unary_Operator_Mutation,
				MutaClass.UIOI, MutaOperator.insert_post_inc,
				expression, COperator.increment);
	}
	/**
	 * @param expression
	 * @return insert_prev_dec(reference, decrement)
	 * @throws Exception
	 */
	public static AstMutation insert_prev_dec(AstExpression expression) throws Exception {
		return new AstMutation(MutaGroup.Unary_Operator_Mutation,
				MutaClass.UIOI, MutaOperator.insert_prev_dec,
				expression, COperator.decrement);
	}
	/**
	 * @param expression
	 * @return insert_post_dec(reference, decrement)
	 * @throws Exception
	 */
	public static AstMutation insert_post_dec(AstExpression expression) throws Exception {
		return new AstMutation(MutaGroup.Unary_Operator_Mutation,
				MutaClass.UIOI, MutaOperator.insert_post_dec,
				expression, COperator.decrement);
	}
	/**
	 * @param expression
	 * @return delete_prev_inc(prev_inc_expr, null) ...
	 * @throws Exception
	 */
	public static AstMutation UIOD(AstExpression expression) throws Exception {
		if(expression instanceof AstIncreUnaryExpression) {
			switch(((AstIncreUnaryExpression) expression).get_operator().get_operator()) {
			case increment:	
				return new AstMutation(MutaGroup.Unary_Operator_Mutation,
						MutaClass.UIOD, MutaOperator.delete_prev_inc,
						expression, null);
			case decrement:
				return new AstMutation(MutaGroup.Unary_Operator_Mutation,
						MutaClass.UIOD, MutaOperator.delete_prev_dec,
						expression, null);
			default: throw new IllegalArgumentException("Invalid operator: " + expression);
			}
		}
		else if(expression instanceof AstIncrePostfixExpression) {
			switch(((AstIncreUnaryExpression) expression).get_operator().get_operator()) {
			case increment:
				return new AstMutation(MutaGroup.Unary_Operator_Mutation,
						MutaClass.UIOD, MutaOperator.delete_post_inc,
						expression, null);
			case decrement:
				return new AstMutation(MutaGroup.Unary_Operator_Mutation,
						MutaClass.UIOD, MutaOperator.delete_post_dec,
						expression, null);
			default: throw new IllegalArgumentException("Invalid operator: " + expression);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid location: " + expression);
		}
	}
	/**
	 * @param expression
	 * @param different
	 * @return inc_constant(expression, int)
	 * @throws Exception
	 */
	public static AstMutation inc_constant(AstExpression expression, int different) throws Exception {
		return new AstMutation(MutaGroup.Unary_Operator_Mutation,
				MutaClass.VINC, MutaOperator.inc_constant,
				expression, Integer.valueOf(different));
	}
	/**
	 * @param expression
	 * @param multiply
	 * @return mul_constant(expression, int)
	 * @throws Exception
	 */
	public static AstMutation mul_constant(AstExpression expression, double multiply) throws Exception {
		return new AstMutation(MutaGroup.Unary_Operator_Mutation,
				MutaClass.VINC, MutaOperator.mul_constant,
				expression, Double.valueOf(multiply));
	}
	/**
	 * @param expression
	 * @param operator
	 * @return positive-->insert_abs; otherwise-->insert_nabs
	 * @throws Exception
	 */
	public static AstMutation UNOI(AstExpression expression, COperator operator) throws Exception {
		switch(operator) {
		case positive:
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UNOI, MutaOperator.insert_abs_value,
					expression, operator);
		case negative:
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UNOI, MutaOperator.insert_arith_neg,
					expression, operator);
		case bit_not:
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UNOI, MutaOperator.insert_bitws_rsv,
					expression, operator);
		case logic_not:
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UNOI, MutaOperator.insert_logic_not,
					expression, operator);
		default:
			return new AstMutation(MutaGroup.Unary_Operator_Mutation,
					MutaClass.UNOI, MutaOperator.insert_nabs_value,
					expression, operator);
		}
	}
	/**
	 * @param expression
	 * @return delete_xxx(expression, null)
	 * @throws Exception
	 */
	public static AstMutation UNOD(AstUnaryExpression expression) throws Exception {
		switch(expression.get_operator().get_operator()) {
		case negative:	return new AstMutation(MutaGroup.Unary_Operator_Mutation,
							MutaClass.UNOD, MutaOperator.delete_arith_neg,
							expression, null);
		case bit_not:	return new AstMutation(MutaGroup.Unary_Operator_Mutation,
							MutaClass.UNOD, MutaOperator.delete_bitws_rsv,
							expression, null);
		case logic_not:	return new AstMutation(MutaGroup.Unary_Operator_Mutation,
							MutaClass.UNOD, MutaOperator.delete_logic_not,
							expression, null);
			
		default: throw new IllegalArgumentException("Invalid expression as " + expression.generate_code());
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return set_true(expression, true) | set_false(expression, false)
	 * @throws Exception
	 */
	public static AstMutation VBRP(AstExpression expression, boolean value) throws Exception {
		if(value) {
			return new AstMutation(MutaGroup.Reference_Mutation,
					MutaClass.VBRP, MutaOperator.set_true, 
					expression, Boolean.TRUE);
		}
		else {
			return new AstMutation(MutaGroup.Reference_Mutation,
					MutaClass.VBRP, MutaOperator.set_false, 
					expression, Boolean.FALSE);
		}
	}
	/**
	 * @param expression
	 * @param constant
	 * @return set_integer(expression, long)
	 * @throws Exception
	 */
	public static AstMutation VCRP(AstExpression expression, long constant) throws Exception {
		return new AstMutation(MutaGroup.Reference_Mutation, MutaClass.VCRP,
				MutaOperator.set_integer, expression, Long.valueOf(constant));
	}
	/**
	 * @param expression
	 * @param constant
	 * @return set_double(expression, double)
	 * @throws Exception
	 */
	public static AstMutation VCRP(AstExpression expression, double constant) throws Exception {
		return new AstMutation(MutaGroup.Reference_Mutation, MutaClass.VCRP,
				MutaOperator.set_integer, expression, Double.valueOf(constant));
	}
	/**
	 * @param expression
	 * @param name
	 * @return set_reference(expression, name)
	 * @throws Exception
	 */
	public static AstMutation VRRP(AstExpression expression, String name) throws Exception {
		return new AstMutation(MutaGroup.Reference_Mutation, MutaClass.VRRP,
				MutaOperator.set_reference, expression, name);
	}
	/**
	 * @param source
	 * @param target
	 * @return set_return(source.expression, target.expression)
	 * @throws Exception
	 */
	public static AstMutation RTRP(AstReturnStatement source, AstReturnStatement target) throws Exception {
		return new AstMutation(MutaGroup.Reference_Mutation, MutaClass.RTRP,
				MutaOperator.set_return, source.get_expression(), target.get_expression());
	}
	/**
	 * @param expression
	 * @param operator
	 * @return set_operator(expression, operator)
	 * @throws Exception
	 */
	public static AstMutation OAAN(AstArithBinaryExpression expression, COperator operator) throws Exception {
		if(operator == expression.get_operator().get_operator()) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			switch(operator) {
			case arith_add:
			case arith_sub:
			case arith_mul:
			case arith_div:
			case arith_mod:
				return new AstMutation(MutaGroup.Binary_Operator_Mutation, MutaClass.OAAN,
						MutaOperator.set_operator, expression, operator);
			default:		
				throw new IllegalArgumentException("Invalid operator: null");
			}
		}
	}
	public static AstMutation OBAN(AstBitwiseBinaryExpression expression, COperator operator) throws Exception {
		if(operator == expression.get_operator().get_operator()) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			switch(operator) {
			case arith_add:
			case arith_sub:
			case arith_mul:
			case arith_div:
			case arith_mod:
				return new AstMutation(MutaGroup.Binary_Operator_Mutation, MutaClass.OBAN,
						MutaOperator.set_operator, expression, operator);
			default:		
				throw new IllegalArgumentException("Invalid operator: null");
			}
		}
	}
	public static AstMutation OBAN(AstShiftBinaryExpression expression, COperator operator) throws Exception {
		if(operator == expression.get_operator().get_operator()) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			switch(operator) {
			case arith_add:
			case arith_sub:
			case arith_mul:
			case arith_div:
			case arith_mod:
				return new AstMutation(MutaGroup.Binary_Operator_Mutation, MutaClass.OBAN,
						MutaOperator.set_operator, expression, operator);
			default:		
				throw new IllegalArgumentException("Invalid operator: null");
			}
		}
	}
	public static AstMutation OLAN(AstLogicBinaryExpression expression, COperator operator) throws Exception {
		if(operator == expression.get_operator().get_operator()) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			switch(operator) {
			case arith_add:
			case arith_sub:
			case arith_mul:
			case arith_div:
			case arith_mod:
				return new AstMutation(MutaGroup.Binary_Operator_Mutation, MutaClass.OLAN,
						MutaOperator.set_operator, expression, operator);
			default:		
				throw new IllegalArgumentException("Invalid operator: null");
			}
		}
	}
	public static AstMutation ORAN(AstRelationExpression expression, COperator operator) throws Exception {
		if(operator == expression.get_operator().get_operator()) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			switch(operator) {
			case arith_add:
			case arith_sub:
			case arith_mul:
			case arith_div:
			case arith_mod:
				return new AstMutation(MutaGroup.Binary_Operator_Mutation, MutaClass.ORAN,
						MutaOperator.set_operator, expression, operator);
			default:		
				throw new IllegalArgumentException("Invalid operator: null");
			}
		}
	}
	public static AstMutation OABN(AstArithBinaryExpression expression, COperator operator) throws Exception {
		if(operator == expression.get_operator().get_operator()) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			switch(operator) {
			case bit_and:
			case bit_or:
			case bit_xor:
			case left_shift:
			case righ_shift:
				return new AstMutation(MutaGroup.Binary_Operator_Mutation, MutaClass.OABN,
						MutaOperator.set_operator, expression, operator);
			default:		
				throw new IllegalArgumentException("Invalid operator: null");
			}
		}
	}
	public static AstMutation OBBN(AstBitwiseBinaryExpression expression, COperator operator) throws Exception {
		if(operator == expression.get_operator().get_operator()) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			switch(operator) {
			case bit_and:
			case bit_or:
			case bit_xor:
			case left_shift:
			case righ_shift:
				return new AstMutation(MutaGroup.Binary_Operator_Mutation, MutaClass.OBBN,
						MutaOperator.set_operator, expression, operator);
			default:		
				throw new IllegalArgumentException("Invalid operator: null");
			}
		}
	}
	public static AstMutation OBBN(AstShiftBinaryExpression expression, COperator operator) throws Exception {
		if(operator == expression.get_operator().get_operator()) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			switch(operator) {
			case bit_and:
			case bit_or:
			case bit_xor:
			case left_shift:
			case righ_shift:
				return new AstMutation(MutaGroup.Binary_Operator_Mutation, MutaClass.OBBN,
						MutaOperator.set_operator, expression, operator);
			default:		
				throw new IllegalArgumentException("Invalid operator: null");
			}
		}
	}
	public static AstMutation OBBN(AstLogicBinaryExpression expression, COperator operator) throws Exception {
		if(operator == expression.get_operator().get_operator()) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			switch(operator) {
			case bit_and:
			case bit_or:
			case bit_xor:
			case left_shift:
			case righ_shift:
				return new AstMutation(MutaGroup.Binary_Operator_Mutation, MutaClass.OLBN,
						MutaOperator.set_operator, expression, operator);
			default:		
				throw new IllegalArgumentException("Invalid operator: null");
			}
		}
	}
	public static AstMutation ORBN(AstRelationExpression expression, COperator operator) throws Exception {
		if(operator == expression.get_operator().get_operator()) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			switch(operator) {
			case bit_and:
			case bit_or:
			case bit_xor:
			case left_shift:
			case righ_shift:
				return new AstMutation(MutaGroup.Binary_Operator_Mutation, MutaClass.ORBN,
						MutaOperator.set_operator, expression, operator);
			default:		
				throw new IllegalArgumentException("Invalid operator: null");
			}
		}
	}
	
	
}
