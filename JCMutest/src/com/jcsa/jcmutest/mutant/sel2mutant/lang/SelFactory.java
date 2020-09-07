package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.cons.SelConditionConstraint;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.cons.SelConstraint;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.cons.SelExecutionConstraint;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelConjunctDescriptions;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelDescription;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelDescriptions;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelDisjunctDescriptions;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.expr.SelAddExpressionError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.expr.SelExpressionError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.expr.SelInsExpressionError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.expr.SelNevExpressionError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.expr.SelSetExpressionError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.stmt.SelAddStatementError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.stmt.SelDelStatementError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.stmt.SelSetStatementError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.stmt.SelStatementError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.SelTypedValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.binary.SelAddValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.binary.SelAndValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.binary.SelIorValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.binary.SelModValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.binary.SelMulValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.binary.SelSetValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.binary.SelXorValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.unary.SelChgValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.unary.SelDecValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.unary.SelExtValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.unary.SelIncValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.unary.SelNegValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.unary.SelRsvValueError;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.unary.SelShkValueError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * It provides interface to create SelNode.
 * 
 * @author yukimula
 *
 */
public class SelFactory {
	
	/* constraint */
	/**
	 * @param expression
	 * @param value
	 * @return symbolic condition in which the expression is required to be the specified boolean value
	 * @throws Exception
	 */
	public static SymExpression get_condition(Object expression, boolean value) throws Exception {
		SymExpression condition = SymFactory.parse(expression);
		CType type = CTypeAnalyzer.get_value_type(condition.get_data_type());
		if(CTypeAnalyzer.is_boolean(type)) {
			if(value) {
				return condition;
			}
			else {
				return SymFactory.logic_not(condition);
			}
		}
		else if(CTypeAnalyzer.is_integer(type) || CTypeAnalyzer.is_real(type) || CTypeAnalyzer.is_pointer(type)) {
			if(value) {
				return SymFactory.not_equals(condition, Integer.valueOf(0));
			}
			else {
				return SymFactory.equal_with(condition, Integer.valueOf(0));
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + type.generate_code());
		}
	}
	/**
	 * @param statement
	 * @param loop_times
	 * @return execute(stmt, times): statement is executed at least N times.
	 * @throws Exception
	 */
	public static SelConstraint execution_constraint(CirStatement statement, int loop_times) throws Exception {
		if(loop_times < 0) loop_times = 0;
		return new SelExecutionConstraint(statement, loop_times);
	}
	/**
	 * @param statement	the statement where constraint is performed
	 * @param condition the condition being asserted in the location
	 * @param assert_value the value that the condition required be
	 * @return asserts(stmt, expr)
	 * @throws Exception
	 */
	public static SelConstraint condition_constraint(CirStatement statement, Object expression, boolean assert_value) throws Exception {
		return new SelConditionConstraint(statement, get_condition(expression, assert_value));
	}
	
	/* statement error */
	/**
	 * @param statement
	 * @return the statement is executed when it should NOT be executed
	 * @throws Exception
	 */
	public static SelStatementError add_statement(CirStatement statement) throws Exception {
		return new SelAddStatementError(statement, statement);
	}
	/**
	 * @param statement
	 * @return the statement is not executed when it should be executed
	 * @throws Exception
	 */
	public static SelStatementError del_statement(CirStatement statement) throws Exception {
		return new SelDelStatementError(statement, statement);
	}
	/**
	 * @param orig_statement
	 * @param muta_statement
	 * @return set_stmt(orig_stmt, muta_stmt): the muta_stmt is executed following orig_stmt.
	 * @throws Exception
	 */
	public static SelStatementError set_statement(CirStatement 
			orig_statement, CirStatement muta_statement) throws Exception {
		return new SelSetStatementError(orig_statement, orig_statement, muta_statement);
	}
	
	/* expression error */
	/**
	 * @param statement
	 * @param expression
	 * @param operator
	 * @return nev_expr(expr, operator): e ==> o(e)
	 * @throws Exception
	 */
	public static SelExpressionError ins_expression(CirStatement statement, 
			CirExpression expression, COperator operator) throws Exception {
		return new SelNevExpressionError(statement, expression, operator);
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param muta_expression
	 * @return set_expr(orig_expr, muta_expr): the muta_expr replaces the orig_expr
	 * @throws Exception
	 */
	public static SelExpressionError set_expression(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SelSetExpressionError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param operator
	 * @param operand
	 * @return add_expr(e1, o, e2): e1 ==> e1 o e2
	 * @throws Exception
	 */
	public static SelExpressionError add_expression(CirStatement statement,
			CirExpression orig_expression, COperator operator,
			Object operand) throws Exception {
		return new SelAddExpressionError(statement, 
				orig_expression, operator, SymFactory.parse(operand));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param operator
	 * @param operand
	 * @return add_expr(e1, o, e2): e1 ==> e2 o e1
	 * @throws Exception
	 */
	public static SelExpressionError ins_expression(CirStatement statement,
			CirExpression orig_expression, COperator operator,
			Object operand) throws Exception {
		return new SelInsExpressionError(statement, 
				orig_expression, operator, SymFactory.parse(operand));
	}
	
	/* unary value error */
	public static SelTypedValueError chg_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SelChgValueError(statement, expression);
	}
	public static SelTypedValueError neg_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SelNegValueError(statement, expression);
	}
	public static SelTypedValueError rsv_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SelRsvValueError(statement, expression);
	}
	public static SelTypedValueError inc_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SelIncValueError(statement, expression);
	}
	public static SelTypedValueError dec_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SelDecValueError(statement, expression);
	}
	public static SelTypedValueError ext_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SelExtValueError(statement, expression);
	}
	public static SelTypedValueError shk_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SelShkValueError(statement, expression);
	}
	
	/* binary value error */
	public static SelTypedValueError set_value(CirStatement statement, 
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SelSetValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SelTypedValueError add_value(CirStatement statement, 
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SelAddValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SelTypedValueError mul_value(CirStatement statement, 
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SelMulValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SelTypedValueError mod_value(CirStatement statement, 
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SelModValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SelTypedValueError and_value(CirStatement statement, 
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SelAndValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SelTypedValueError ior_value(CirStatement statement, 
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SelIorValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SelTypedValueError xor_value(CirStatement statement, 
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SelXorValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	
	/* descriptions */
	public static SelDescriptions conjunct(CirStatement statement,
			Iterable<SelDescription> descriptions) throws Exception {
		SelDescriptions list = new SelConjunctDescriptions(statement);
		for(SelDescription description : descriptions) {
			list.add_child(description);
		}
		return list;
	}
	public static SelDescriptions disjunct(CirStatement statement,
			Iterable<SelDescription> descriptions) throws Exception {
		SelDescriptions list = new SelDisjunctDescriptions(statement);
		for(SelDescription description : descriptions) {
			list.add_child(description);
		}
		return list;
	}
	
}
