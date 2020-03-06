package com.jcsa.jcmuta.mutant;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.MutaOperator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CScope;

/**
 * The mutation performed by mutating the syntactic structure of source code.
 * 
 * @author yukimula
 *
 */
public class AstMutation {
	
	private MutaClass mutation_class;
	private MutaOperator mutation_operator;
	private AstNode location;
	private Object parameter;
	
	/**
	 * create a mutation that mutates the syntactic structure of location with specified operator
	 * and parameter
	 * @param muta_class
	 * @param muta_operator
	 * @param location
	 * @param parameter
	 * @throws Exception
	 */
	private AstMutation(MutaClass muta_class, MutaOperator muta_operator, 
			AstNode location, Object parameter) throws Exception {
		if(muta_class == null)
			throw new IllegalArgumentException("Invalid muta_class: null");
		else if(muta_operator == null)
			throw new IllegalArgumentException("Invalid muta_operator: null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location as null");
		else {
			this.mutation_class = muta_class;
			this.mutation_operator = muta_operator;
			this.location = location; 
			this.parameter = parameter;
		}
	}
	
	/* getters */
	/**
	 * get the class of mutation operator
	 * @return
	 */
	public MutaClass get_mutation_class() { return this.mutation_class; }
	/**
	 * mutation operator used to change the source code
	 * @return
	 */
	public MutaOperator get_mutation_operator() { return this.mutation_operator; }
	/**
	 * get the location where the mutation operator is performed on
	 * @return
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * get the parameter to define the mutation
	 * @return null if no parameter specified
	 */
	public Object get_parameter() { return this.parameter; }
	
	/* trapping class */
	/**
	 * trap_on_true(expression)
	 * trap_on_false(expression)
	 * @param expression 
	 * 			(1) logical expression or relational expression
	 * 			(2) operand used in logical or relational expression
	 * 			(3) expression used as the condition of if, while, do...while and for statement.
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static AstMutation BTRP(AstExpression expression, boolean value) throws Exception {
		if(value)
			return new AstMutation(MutaClass.BTRP, MutaOperator.trap_on_true, expression, null);
		else
			return new AstMutation(MutaClass.BTRP, MutaOperator.trap_on_false,expression, null);
	}
	/**
	 * trap_on_case(switch_stmt.condition, case_statement.condition)
	 * both parameters belong to the same switch statement's block.
	 * @param statement
	 * @param replace
	 * @return
	 * @throws Exception
	 */
	public static AstMutation CTRP(AstSwitchStatement statement, AstCaseStatement replace) throws Exception {
		return new AstMutation(MutaClass.CTRP, MutaOperator.trap_on_case,
				CTypeAnalyzer.get_expression_of(statement.get_condition()),
				CTypeAnalyzer.get_expression_of(replace.get_expression()));
	}
	public static AstMutation ETRP(AstExpression expression) throws Exception {
		expression = CTypeAnalyzer.get_expression_of(expression);
		return new AstMutation(MutaClass.ETRP, MutaOperator.trap_on_expression, expression, null);
	}
	/**
	 * trap_on_expression(statement.expression) or trap_on_statement(statement)
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static AstMutation STRP(AstStatement statement) throws Exception {
		return new AstMutation(MutaClass.STRP, MutaOperator.trap_on_statement, statement, null);
	}
	/**
	 * trap_on_times(loop_statement, loop_times)
	 * @param loop_statement shall be for, while, do..while statement
	 * @param loop_times
	 * @return
	 * @throws Exception
	 */
	public static AstMutation TTRP(AstStatement loop_statement, int loop_times) throws Exception {
		return new AstMutation(MutaClass.TTRP, MutaOperator.trap_at_statement, loop_statement, loop_times);
	}
	/**
	 * @param expression
	 * @param parameter 'p', 'n', '0', 'u', 'o'
	 * @return
	 * @throws Exception
	 */
	public static AstMutation VTRP(AstExpression expression, char parameter) throws Exception {
		MutaOperator operator;
		switch(parameter) {
		case 'p': operator = MutaOperator.trap_on_pos; break;
		case 'n': operator = MutaOperator.trap_on_neg; break;
		case '0': operator = MutaOperator.trap_on_zro; break;
		default: throw new IllegalArgumentException(
				"Invalid parameter: " + parameter);
		}
		return new AstMutation(MutaClass.VTRP, operator, expression, null);
	}
	
	/* statement mutation */
	/**
	 * delete_operand(expression.operand) where expression is binary or multiple expression
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public static AstMutation OPDL(AstExpression expression, int index) throws Exception {
		if(expression instanceof AstBinaryExpression)
			return new AstMutation(MutaClass.OPDL, MutaOperator.delete_operand, expression, index);
		else
			return new AstMutation(MutaClass.OPDL, MutaOperator.delete_element, expression, index);
	}
	/**
	 * delete_statement(statement)
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static AstMutation STDL(AstStatement statement) throws Exception {
		return new AstMutation(MutaClass.STDL, MutaOperator.delete_statement, statement, null);
	}
	/**
	 * @param statement break or continue
	 * @return
	 * @throws Exception
	 */
	public static AstMutation SBCR(AstStatement statement) throws Exception {
		if(statement instanceof AstBreakStatement)
			return new AstMutation(MutaClass.SBCR, MutaOperator.break_to_continue, statement, null);
		else if(statement instanceof AstContinueStatement)
			return new AstMutation(MutaClass.SBCR, MutaOperator.continue_to_break, statement, null);
		else throw new IllegalArgumentException("Invalid statement class: " + statement.toString());
	}
	/**
	 * @param statement
	 * @param is_break true for ins_break and false for ins_continue
	 * @return
	 * @throws Exception
	 */
	public static AstMutation SBCI(AstStatement statement, boolean is_break) throws Exception {
		if(is_break)
			return new AstMutation(MutaClass.SBCI, MutaOperator.ins_break, statement, null);
		else
			return new AstMutation(MutaClass.SBCI, MutaOperator.ins_continue, statement, null);
	}
	/**
	 * @param statement while_stmt | do_while_stmt
	 * @return
	 * @throws Exception
	 */
	public static AstMutation SWDR(AstStatement statement) throws Exception {
		if(statement instanceof AstWhileStatement)
			return new AstMutation(MutaClass.SWDR, MutaOperator.while_to_do, statement, null);
		else if(statement instanceof AstDoWhileStatement)
			return new AstMutation(MutaClass.SWDR, MutaOperator.do_to_while, statement, null);
		else throw new IllegalArgumentException("Invalid statement class: " + statement.toString());
	}
	/**
	 * set_label(goto_stmt.label, goto_stmt.label)
	 * @param statement
	 * @param replace
	 * @return
	 * @throws Exception
	 */
	public static AstMutation SGLR(AstGotoStatement statement, AstLabeledStatement replace) throws Exception {
		return new AstMutation(
				MutaClass.SGLR, MutaOperator.set_goto_label, 
				statement.get_label(), replace.get_label());
	}
	/**
	 * set_return_value(retr_stmt.expr, retr_stmt.expr)
	 * @param statement
	 * @param replace
	 * @return
	 * @throws Exception
	 */
	public static AstMutation SRTR(AstReturnStatement statement, AstReturnStatement replace) throws Exception {
		return new AstMutation(MutaClass.SRTR, MutaOperator.set_return_value,
				CTypeAnalyzer.get_expression_of(statement.get_expression()),
				CTypeAnalyzer.get_expression_of(replace.get_expression()));
	}
	
	/* unary operator mutation */
	/**
	 * @param expression
	 * @param is_prev
	 * @param operator increment or decrement
	 * @return
	 * @throws Exception
	 */
	public static AstMutation UIOI(AstExpression expression, boolean is_prev, COperator operator) throws Exception {
		MutaOperator mutation_operator;
		if(is_prev) {
			if(operator == COperator.increment) {
				mutation_operator = MutaOperator.insert_prev_inc;
			}
			else {
				mutation_operator = MutaOperator.insert_prev_dec;
			}
		}
		else {
			if(operator == COperator.increment) {
				mutation_operator = MutaOperator.insert_post_inc;
			}
			else {
				mutation_operator = MutaOperator.insert_post_dec;
			}
		}
		return new AstMutation(MutaClass.UIOI, mutation_operator, expression, null);
	}
	/**
	 * @param expression
	 * @param is_prev
	 * @param operator increment | decrement
	 * @return
	 * @throws Exception
	 */
	public static AstMutation UIOR(AstExpression expression, boolean is_prev, COperator operator) throws Exception {
		String prev;
		if(expression instanceof AstIncreUnaryExpression) {
			AstIncreUnaryExpression unary_expr = (AstIncreUnaryExpression) expression;
			if(unary_expr.get_operator().get_operator() == COperator.increment) {
				prev = "prev_inc";
			}
			else {
				prev = "prev_dec";
			}
		}
		else {
			AstIncrePostfixExpression unary_expr = (AstIncrePostfixExpression) expression;
			if(unary_expr.get_operator().get_operator() == COperator.increment) {
				prev = "post_inc";
			}
			else {
				prev = "post_dec";
			}
		}
		
		String post;
		if(is_prev) {
			if(operator == COperator.increment) {
				post = "prev_inc";
			}
			else {
				post = "prev_dec";
			}
		}
		else {
			if(operator == COperator.increment) {
				post = "post_inc";
			}
			else {
				post = "post_dec";
			}
		}
		
		MutaOperator mutation_operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.UIOR, mutation_operator, expression, null);
	}
	/**
	 * 
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public static AstMutation UIOD(AstExpression expression) throws Exception {
		MutaOperator mutation_operator;
		if(expression instanceof AstIncreUnaryExpression) {
			AstIncreUnaryExpression unary_expr = (AstIncreUnaryExpression) expression;
			if(unary_expr.get_operator().get_operator() == COperator.increment) {
				mutation_operator = MutaOperator.delete_prev_inc;
			}
			else {
				mutation_operator = MutaOperator.delete_prev_dec;
			}
		}
		else {
			AstIncrePostfixExpression unary_expr = (AstIncrePostfixExpression) expression;
			if(unary_expr.get_operator().get_operator() == COperator.increment) {
				mutation_operator = MutaOperator.delete_post_inc;
			}
			else {
				mutation_operator = MutaOperator.delete_post_dec;
			}
		}
		return new AstMutation(MutaClass.UIOD, mutation_operator, expression, null);
	}
	/**
	 * @param expression
	 * @param parameter integer (> 0) or integer (< 0) or real (> 1) or real (0 < x < 1)
	 * @return
	 * @throws Exception
	 */
	public static AstMutation VINC(AstExpression expression, Object parameter) throws Exception {
		if(parameter instanceof Integer) {
			int value = (Integer) parameter;
			return new AstMutation(MutaClass.VINC, MutaOperator.inc_value, expression, value);
		}
		else {
			double value = (Double) parameter;
			return new AstMutation(MutaClass.VINC, MutaOperator.mul_value, expression, value);
		}
	}
	/**
	 * 
	 * @param expression
	 * @param parameter negative (arith_neg) | bit_not (bitws_rsv) | logic_not (logic_not)
	 * 					positive (abs)	| others (-abs)
	 * @return
	 * @throws Exception
	 */
	public static AstMutation UNOI(AstExpression expression, COperator parameter) throws Exception {
		MutaOperator operator;
		switch(parameter) {
		case negative:		operator = MutaOperator.insert_arith_neg; break;
		case bit_not:		operator = MutaOperator.insert_bitws_rsv; break;
		case logic_not:		operator = MutaOperator.insert_logic_not; break;
		case positive:		operator = MutaOperator.insert_abs; break;
		default:			operator = MutaOperator.insert_neg_abs; break;
		}
		return new AstMutation(MutaClass.UNOI, operator, expression, null);
	}
	public static AstMutation UNOD(AstExpression expression) throws Exception {
		if(expression instanceof AstArithUnaryExpression) {
			return new AstMutation(MutaClass.UNOD, MutaOperator.delete_arith_neg, expression, null);
		}
		else if(expression instanceof AstBitwiseUnaryExpression) {
			return new AstMutation(MutaClass.UNOD, MutaOperator.delete_bitws_rsv, expression, null);
		}
		else if(expression instanceof AstLogicUnaryExpression) {
			return new AstMutation(MutaClass.UNOD, MutaOperator.delete_logic_not, expression, null);
		}
		else throw new IllegalArgumentException("Invalid expression: null");
	}
	
	/* binary operator mutation */
	private static String operator_name(COperator operator) throws Exception {
		switch(operator) {
		case arith_add:				return "arith_add";
		case arith_sub:				return "arith_sub";
		case arith_mul:				return "arith_mul";
		case arith_div:				return "arith_div";
		case arith_mod:				return "arith_mod";
		case bit_and:				return "bitws_and";
		case bit_or:				return "bitws_ior";
		case bit_xor:				return "bitws_xor";
		case left_shift:			return "bitws_lsh";
		case righ_shift:			return "bitws_rsh";
		case logic_and:				return "logic_and";
		case logic_or:				return "logic_ior";
		case greater_tn:			
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:			
		case assign:				
		case arith_add_assign:		
		case arith_sub_assign:
		case arith_mul_assign:
		case arith_div_assign:
		case arith_mod_assign:		return operator.toString();
		case bit_and_assign:		return "bitws_and_assign";
		case bit_or_assign:			return "bitws_ior_assign";
		case bit_xor_assign:		return "bitws_xor_assign";
		case left_shift_assign:		return "bitws_lsh_assign";
		case righ_shift_assign:		return "bitws_rsh_assign";
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	/**
	 * @param expression
	 * @param replace within arithmetic operator class
	 * @return
	 * @throws Exception
	 */
	public static AstMutation OAAN(AstArithBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OAAN, operator, expression, null);
	}
	/**
	 * @param expression
	 * @param replace within bitwise or shifting operator class
	 * @return
	 * @throws Exception
	 */
	public static AstMutation OABN(AstArithBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OABN, operator, expression, null);
	}
	/**
	 * @param expression
	 * @param replace within logical operator class
	 * @return
	 * @throws Exception
	 */
	public static AstMutation OALN(AstArithBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OALN, operator, expression, null);
	}
	/**
	 * @param expression
	 * @param replace within relational operator class
	 * @return
	 * @throws Exception
	 */
	public static AstMutation OARN(AstArithBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OARN, operator, expression, null);
	}
	/**
	 * @param expression
	 * @param replace
	 * @return
	 * @throws Exception
	 */
	public static AstMutation OBAN(AstBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OBAN, operator, expression, null);
	}
	/**
	 * @param expression
	 * @param replace
	 * @return
	 * @throws Exception
	 */
	public static AstMutation OBBN(AstBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OBBN, operator, expression, null);
	}
	/**
	 * @param expression
	 * @param replace
	 * @return
	 * @throws Exception
	 */
	public static AstMutation OBLN(AstBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OBLN, operator, expression, null);
	}
	/**
	 * @param expression
	 * @param replace
	 * @return
	 * @throws Exception
	 */
	public static AstMutation OBRN(AstBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OBRN, operator, expression, null);
	}
	public static AstMutation OLAN(AstLogicBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OLAN, operator, expression, null);
	}
	public static AstMutation OLBN(AstLogicBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OLBN, operator, expression, null);
	}
	public static AstMutation OLLN(AstLogicBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OLLN, operator, expression, null);
	}
	public static AstMutation OLRN(AstLogicBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OLRN, operator, expression, null);
	}
	public static AstMutation ORAN(AstRelationExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.ORAN, operator, expression, null);
	}
	public static AstMutation ORBN(AstRelationExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.ORBN, operator, expression, null);
	}
	public static AstMutation ORLN(AstRelationExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.ORLN, operator, expression, null);
	}
	public static AstMutation ORRN(AstRelationExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.ORRN, operator, expression, null);
	}
	public static AstMutation OEAA(AstAssignExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OEAA, operator, expression, null);
	}
	public static AstMutation OEBA(AstAssignExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OEBA, operator, expression, null);
	}
	public static AstMutation OAAA(AstArithAssignExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OAAA, operator, expression, null);
	}
	public static AstMutation OABA(AstArithAssignExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OABA, operator, expression, null);
	}
	public static AstMutation OAEA(AstArithAssignExpression expression) throws Exception {
		String prev = expression.get_operator().get_operator().toString();
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_assign");
		return new AstMutation(MutaClass.OAEA, operator, expression, null);
	}
	public static AstMutation OBAA(AstBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OBAA, operator, expression, null);
	}
	public static AstMutation OBBA(AstBinaryExpression expression, COperator replace) throws Exception {
		if(expression.get_operator().get_operator() == replace)
			throw new IllegalArgumentException("Invalid operator");
		String prev = operator_name(expression.get_operator().get_operator());
		String post = operator_name(replace);
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_" + post);
		return new AstMutation(MutaClass.OBBA, operator, expression, null);
	}
	public static AstMutation OBEA(AstBinaryExpression expression) throws Exception {
		String prev = operator_name(expression.get_operator().get_operator());
		MutaOperator operator = MutaOperator.valueOf(prev + "_to_assign");
		return new AstMutation(MutaClass.OBEA, operator, expression, null);
	}
	
	/* expression mutation */
	public static AstMutation VBRP(AstExpression expression, boolean value) throws Exception {
		if(value)
			return new AstMutation(MutaClass.VBRP, MutaOperator.set_true, expression, null);
		else
			return new AstMutation(MutaClass.VBRP, MutaOperator.set_false, expression, null);
	}
	/**
	 * set_constant(constant_expression, AstConstant)
	 * @param expression
	 * @param replace of which type shall be constant with expression's type
	 * @return
	 * @throws Exception
	 */
	public static AstMutation VCRP(AstExpression expression, CConstant replace) throws Exception {
		return new AstMutation(MutaClass.VCRP, MutaOperator.set_constant, expression, replace);
	}
	/**
	 * @param expression shall be a reference
	 * @param cname of instance or parameter
	 * @return
	 * @throws Exception
	 */
	public static AstMutation VRRP(AstExpression expression, CName name) throws Exception {
		return new AstMutation(MutaClass.VRRP, MutaOperator.set_reference, expression, name);
	}
	
	/* semantic mutation class */
	public static AstMutation EQAR(AstRelationExpression expression) throws Exception {
		return new AstMutation(MutaClass.EQAR, MutaOperator.equal_with_to_assign, expression, null);
	}
	/**
	 * 
	 * @param statement if | for | while
	 * @return
	 * @throws Exception
	 */
	public static AstMutation OSBI(AstStatement statement) throws Exception {
		if(statement instanceof AstIfStatement) {
			if(((AstIfStatement) statement).has_else()) {
				statement = ((AstIfStatement) statement).get_false_branch();
			}
			else {
				statement = ((AstIfStatement) statement).get_true_branch();
			}
		}
		else if(statement instanceof AstForStatement) {
			statement = ((AstForStatement) statement).get_body();
		}
		else if(statement instanceof AstWhileStatement) {
			statement = ((AstWhileStatement) statement).get_body();
		}
		else throw new IllegalArgumentException("Invalid statement");
		return new AstMutation(MutaClass.OSBI, MutaOperator.ins_empty_body, statement, null);
	}
	public static AstMutation OIFI(AstIfStatement statement) throws Exception {
		if(statement.has_else()) {
			AstStatement false_branch = statement.get_false_branch();
			if(false_branch instanceof AstIfStatement) {
				if(((AstIfStatement) false_branch).has_else())
					throw new IllegalArgumentException("Invalid assign: " + statement.get_location().read());
				return new AstMutation(MutaClass.OIFI, MutaOperator.ins_elif_in_if, false_branch, null);
			}
			else {
				throw new IllegalArgumentException("Invalid assign: " + statement.get_location().read());
			}
		}
		else {
			throw new IllegalArgumentException("Invalid assign: " + statement.get_location().read());
		}
	}
	public static AstMutation OIFR(AstIfStatement statement) throws Exception {
		if(statement.has_else()) {
			AstStatement false_branch = statement.get_false_branch();
			if(false_branch instanceof AstIfStatement) {
				return new AstMutation(MutaClass.OIFI, MutaOperator.set_elif_as_else, false_branch, null);
			}
			else {
				throw new IllegalArgumentException("Invalid assign: " + statement.get_location().read());
			}
		}
		else {
			throw new IllegalArgumentException("Invalid assign: " + statement.get_location().read());
		}
	}
	public static AstMutation ODFI(AstSwitchStatement statement) throws Exception {
		if(statement.get_body() instanceof AstCompoundStatement) {
			AstCompoundStatement body = (AstCompoundStatement) statement.get_body();
			AstStatementList list = body.get_statement_list();
			for(int k = 0; k < list.number_of_statements(); k++) {
				AstStatement child_stmt = list.get_statement(k);
				if(child_stmt instanceof AstDefaultStatement) {
					throw new IllegalArgumentException("Invalid assign: " + statement.get_location().read());
				}
			}
			return new AstMutation(MutaClass.ODFI, MutaOperator.ins_default, statement, null);
		}
		else {
			throw new IllegalArgumentException("Invalid assign: " + statement.get_location().read());
		}
	}
	public static AstMutation ODFR(AstCaseStatement statement) throws Exception {
		return new AstMutation(MutaClass.ODFR, MutaOperator.set_default, statement, null);
	}
	public static AstMutation OFLT(AstRelationExpression expression) throws Exception {
		switch(expression.get_operator().get_operator()) {
		case equal_with:	
			return new AstMutation(MutaClass.OFLT, MutaOperator.equal_with_to_real_compare, expression, null);
		case not_equals:
			return new AstMutation(MutaClass.OFLT, MutaOperator.not_equals_to_real_compare, expression, null);
		default: throw new IllegalArgumentException("Invalid operator");
		}
	}
	
	/* serialization */
	@Override
	public String toString() {
		String class_name = this.mutation_class.toString();
		String operator_name = this.mutation_operator.toString();
		String location_id = this.location.get_key() + "";
		String parameter_string;
		
		switch(this.mutation_class) {
		/** trap_on_case(case_stmt.expr, case_stmt.expr) **/
		case CTRP:
		{
			AstExpression replace = (AstExpression) this.parameter;
			parameter_string = replace.get_key() + "";
		}
		break;
		/** trap_at_times(statement, times) **/
		case TTRP:
		{
			int loop_times = (int) this.parameter;
			parameter_string = loop_times + "";
		}
		break;
		/** delete_operand(expr, index) **/
		case OPDL:
		{
			int index = (int) this.parameter;
			parameter_string = index + "";
		}
		break;
		/** set_goto_label(label, label) **/
		case SGLR:
		{
			AstLabel next_label = (AstLabel) this.parameter;
			parameter_string = next_label.get_key() + "";
		}
		break;
		/** set_return_value(retr_stmt.expr, retr_stmt.expr) **/
		case SRTR:
		{
			AstExpression replace = (AstExpression) this.parameter;
			parameter_string = replace.get_key() + "";
		}
		break;
		/** inc_val(expr, int) | dec_val(expr, int) | mul_val(expr, double) **/
		case VINC:
		{
			if(this.parameter instanceof Integer) {
				Integer value = (Integer) this.parameter;
				parameter_string = value.toString();
			}
			else {
				Double value = (Double) this.parameter;
				parameter_string = value.toString();
			}
		}
		break;
		/** set_constant(expr, constant) **/
		case VCRP:
		{
			CConstant value = (CConstant) this.parameter;
			
			switch(value.get_type().get_tag()) {
			case c_bool:
				parameter_string = value.get_bool().toString(); break;
			case c_char: case c_uchar:
				int char_code = value.get_char();
				parameter_string = char_code + "";
				break;
			case c_short: case c_ushort: case c_int: case c_uint:
				parameter_string = value.get_integer().toString(); break;
			case c_long: case c_llong: case c_ulong: case c_ullong:
				parameter_string = value.get_long().toString(); break;
			case c_float:
				parameter_string = value.get_float().toString(); break;
			case c_double: case c_ldouble:
				parameter_string = value.get_double().toString(); break;
			default: throw new IllegalArgumentException("Unsupport data type");
			}
		}
		break;
		/** set_reference(expr, cname) **/
		case VRRP:
		{
			CName cname = (CName) this.parameter;
			if(cname == null) parameter_string = "";
			else parameter_string = cname.get_name();
		}
		break;
		/** other case **/
		default: parameter_string = "";	break;
		}
		
		return class_name + "," + operator_name + "," + location_id + "," + parameter_string;
	}
	private static CName find_cname(AstNode location, String name) throws Exception {
		while(location != null) {
			if(location instanceof AstScopeNode) {
				CScope scope = ((AstScopeNode) location).get_scope();
				return scope.get_name(name);
			}
			location = location.get_parent();
		}
		throw new IllegalArgumentException("Unabel to find " + name);
	}
	public static AstMutation parse(AstTree ast_tree, String line) throws Exception {
		String[] items = line.strip().split(",");
		MutaClass mutation_class = MutaClass.valueOf(items[0].strip());
		MutaOperator mutation_operator = MutaOperator.valueOf(items[1].strip());
		if(Integer.parseInt(items[2].strip()) >= ast_tree.number_of_nodes())
			System.out.println("\t==> ??? " + line);
		AstNode location = ast_tree.get_node(Integer.parseInt(items[2].strip()));
		
		Object parameter;
		switch(mutation_class) {
		/** trap_on_case(case_stmt.expr, case_stmt.expr) **/
		case CTRP:
		{
			AstExpression replace = (AstExpression) 
					ast_tree.get_node(Integer.parseInt(items[3].strip()));
			parameter = replace;
		}
		break;
		/** trap_at_times(statement, times) **/
		case TTRP:
		{
			parameter = Integer.parseInt(items[3].strip());
		}
		break;
		/**  delete_operand(expr, index) **/
		case OPDL:
		{
			parameter = Integer.parseInt(items[3].strip());
		}
		break;
		/** set_goto_label(label, label) **/
		case SGLR:
		{
			AstLabel next_label = (AstLabel) 
					ast_tree.get_node(Integer.parseInt(items[3].strip()));
			parameter = next_label;
		}
		break;
		/** set_return_value(retr_stmt.expr, retr_stmt.expr) **/
		case SRTR:
		{
			AstExpression replace = (AstExpression) 
					ast_tree.get_node(Integer.parseInt(items[3].strip()));
			parameter = replace;
		}
		break;
		/** inc_val(expr, int) | dec_val(expr, int) | mul_val(expr, double) **/
		case VINC:
		{
			switch(mutation_operator) {
			case inc_value: parameter = Integer.parseInt(items[3].strip()); break;
			case mul_value:	parameter = Double.parseDouble(items[3].strip()); break;
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
		break;
		/** set_constant(expr, cconstant) **/
		case VCRP:
		{
			CConstant constant = new CConstant();
			try {
				int value = Integer.parseInt(items[3].strip());
				constant.set_int(value); parameter = constant;
			}
			catch(Exception ex) {
				parameter = null;
			}
			
			/*
			if(parameter == null) {
				try {
					boolean value = Boolean.parseBoolean(items[3].strip());
					constant.set_bool(value); parameter = constant;
				}
				catch(Exception ex) {
					parameter = null;
				}
			}
			*/
			
			if(parameter == null) {
				try {
					double value = Double.parseDouble(items[3].strip());
					constant.set_double(value); parameter = constant;
				}
				catch(Exception ex) {
					parameter = null;
				}
			}
			
			if(parameter == null)
				throw new IllegalArgumentException("Unable to interpret " + items[3].strip());
		}
		break;
		/** set_reference(location, cname) **/
		case VRRP:
		{
			CName cname = find_cname(location, items[3].strip());
			parameter = cname;
		}
		break;
		/** for other case **/
		default: parameter = null; break;
		}
		
		return new AstMutation(mutation_class, mutation_operator, location, parameter);
	}
	
}
