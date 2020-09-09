package com.jcsa.jcmutest.mutant.sec2mutant.lang;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.biny.SecAddValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.biny.SecAndValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.biny.SecBinaryValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.biny.SecIorValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.biny.SecModValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.biny.SecMulValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.biny.SecSetValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.biny.SecXorValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConjunctDescriptions;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescriptions;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDisjunctDescriptions;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecAddStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecDelStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecTrpStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.unry.SecChgValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.unry.SecDecValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.unry.SecExtValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.unry.SecIncValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.unry.SecNegValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.unry.SecRsvValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.unry.SecShkValueError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.unry.SecUnaryValueError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * It provides interface to create SecNode.<br>
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecNode																	<br>
 * 	|--	SecToken															<br>
 * 	|--	SecDescription					{statement: SecStatement}			<br>
 * 	|--	|--	SecConstraint				asserton(stmt, expr) === evaluator	<br>
 * 	|--	|--	SecAbstractError												<br>
 * 	|--	|--	SecConcreteError												<br>
 * 	|--	|--	SecDescriptions													<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecToken																<br>
 * 	|--	SecKeyword						{keyword: SecKeywords}				<br>
 * 	|--	SecType							{vtype: SecValueTypes}				<br>
 * 	|--	SecOperator						{operator: COperator}				<br>
 * 	|--	SecExpression					{expression: SymExpression}			<br>
 * 	|--	SecStatement					{statement: CirStatement}			<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecAbstractError														<br>
 * 	|--	SecStatementError				{orig_stmt: SecStatement}			<br>
 * 	|--	|--	SecAddStatementError		add_stmt(orig_stmt)					<br>
 * 	|--	|--	SecDelStatementError		del_stmt(orig_stmt)					<br>
 * 	|--	|--	SecSetStatementError		set_stmt(orig_stmt, muta_stmt)		<br>
 * 	|--	SecExpressionError				{orig_expr: SecExpression}			<br>
 * 	|--	|--	SecSetExpressionError		set_expr(orig_expr, muta_expr)		<br>
 * 	|--	|--	SecAddExpressionError		add_expr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecInsExpressionError		ins_expr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecUnyExpressionError		uny_expr(orig_expr, oprt)			<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecConcreteError					{orig_expr; type: SecType}			<br>
 * 	|--	SecUnaryValueError													<br>
 * 	|--	|--	SecChgValueError			chg_value[bool|char|sign...body]	<br>
 * 	|--	|--	SecNegValueError			neg_value[char|sign|usign|real]		<br>
 * 	|--	|--	SecRsvValueError			rsv_value[char|sign|usign]			<br>
 * 	|--	|--	SecIncValueError			inc_value[char|sign|usign|real|addr]<br>
 * 	|--	|--	SecDecValueError			dec_value[char|sign|usign|real|addr]<br>
 * 	|--	|--	SecExtValueError			ext_value[char|sign|usign|real]		<br>
 * 	|--	|--	SecShkValueError			shk_value[char|sign|usign|real]		<br>
 * 	|--	SecBinaryValueError				{muta_expr: SecExpression}			<br>
 * 	|--	|--	SecSetValueError			set_value[char|sign|usign|real|addr]<br>
 * 	|--	|--	SecAddValueError			add_value[char|sign|usign|real|addr]<br>
 * 	|--	|--	SecMulValueError			mul_value[char|sign|usign|real]		<br>
 * 	|--	|--	SecModValueError			mod_value[char|sign|usign]			<br>
 * 	|--	|--	SecAndValueError			and_value[char|sign|usign]			<br>
 * 	|--	|--	SecIorValueError			ior_value[char|sign|usign]			<br>
 * 	|--	|--	SecXorValueError			xor_value[char|sign|usign]			<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecDescriptions						{descriptions: SecDescription+}		<br>
 * 	|--	SecConjunctDescriptions												<br>
 * 	|--	SecDisjunctDescriptions												<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public class SecFactory {
	
	/* constraint */
	/**
	 * @param statement
	 * @return the identifier that represents the pointer to the statement in state table for 
	 * 		   coverage analysis of the testing.
	 * @throws Exception
	 */
	public static SymExpression get_statement_pointer(CirStatement statement) throws Exception {
		return SymFactory.new_identifier(CBasicTypeImpl.uint_type, "exec#" + statement.hashCode());
	}
	/**
	 * @param operand
	 * @param value
	 * @return boolean expression to assert the input operand as true or false
	 * @throws Exception
	 */
	public static SymExpression get_condition(Object operand, boolean value) throws Exception {
		SymExpression expression = SymFactory.parse(operand);
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(type)) {
			if(value) {
				return expression;
			}
			else {
				return SymFactory.logic_not(expression);
			}
		}
		else if(CTypeAnalyzer.is_number(type) || CTypeAnalyzer.is_pointer(type)) {
			SymExpression zero = SymFactory.new_constant(Integer.valueOf(0));
			if(value) {
				return SymFactory.not_equals(expression, zero);
			}
			else {
				return SymFactory.equal_with(expression, zero);
			}
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
	}
	/**
	 * @param statement
	 * @param condition
	 * @param value
	 * @return constraint that the condition is required to be true or false (value specified) at statement point.
	 * @throws Exception
	 */
	public static SecConstraint assert_constraint(CirStatement statement, Object condition, boolean value) throws Exception {
		SymExpression expression = get_condition(condition, value);
		return new SecConstraint(statement, expression);
	}
	/**
	 * @param statement
	 * @param execute_times
	 * @return the statement is required to be executed for at least N times (execute_times) during testing
	 * @throws Exception
	 */
	public static SecConstraint execute_constraint(CirStatement statement, int execute_times) throws Exception {
		if(execute_times < 0) execute_times = 0;
		SymExpression expression = get_statement_pointer(statement);
		expression = SymFactory.greater_eq(expression, SymFactory.parse(Integer.valueOf(execute_times)));
		return new SecConstraint(statement, get_condition(expression, true));
	}
	
	/* statement error */
	public static SecStatementError add_statement(CirStatement statement) throws Exception {
		return new SecAddStatementError(statement, statement);
	}
	public static SecStatementError del_statement(CirStatement statement) throws Exception {
		return new SecDelStatementError(statement, statement);
	}
	public static SecStatementError set_statement(CirStatement 
			orig_statement, CirStatement muta_statement) throws Exception {
		return new SecAddStatementError(orig_statement, muta_statement);
	}
	public static SecStatementError trap_statement(CirStatement statement) throws Exception {
		return new SecTrpStatementError(statement, statement);
	}
	
	/* expression error */
	public static SecExpressionError set_expression(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SecSetExpressionError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SecExpressionError add_expression(CirStatement statement,
			CirExpression orig_expression, COperator operator, Object operand) throws Exception {
		return new SecAddExpressionError(statement, orig_expression, operator, SymFactory.parse(operand));
	}
	public static SecExpressionError ins_expression(CirStatement statement,
			CirExpression orig_expression, COperator operator, Object operand) throws Exception {
		return new SecInsExpressionError(statement, orig_expression, operator, SymFactory.parse(operand));
	}
	public static SecExpressionError uny_expression(CirStatement statement,
			CirExpression orig_expression, COperator operator) throws Exception {
		return new SecUnyExpressionError(statement, orig_expression, operator);
	}
	
	/* binary value error */
	public static SecBinaryValueError set_value(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SecSetValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SecBinaryValueError add_value(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SecAddValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SecBinaryValueError mul_value(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SecMulValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SecBinaryValueError mod_value(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SecModValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SecBinaryValueError and_value(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SecAndValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SecBinaryValueError ior_value(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SecIorValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	public static SecBinaryValueError xor_value(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SecXorValueError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	
	/* unary value error */
	public static SecUnaryValueError chg_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SecChgValueError(statement, expression);
	}
	public static SecUnaryValueError inc_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SecIncValueError(statement, expression);
	}
	public static SecUnaryValueError dec_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SecDecValueError(statement, expression);
	}
	public static SecUnaryValueError ext_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SecExtValueError(statement, expression);
	}
	public static SecUnaryValueError shk_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SecShkValueError(statement, expression);
	}
	public static SecUnaryValueError neg_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SecNegValueError(statement, expression);
	}
	public static SecUnaryValueError rsv_value(CirStatement statement, CirExpression expression) throws Exception {
		return new SecRsvValueError(statement, expression);
	}
	
	/* composite descriptions */
	public static SecDescriptions conjunct(CirStatement statement, Collection<SecDescription> descriptions) throws Exception {
		if(descriptions == null || descriptions.size() < 1)
			throw new IllegalArgumentException("Invalid descriptions: null");
		else {
			SecDescriptions result = new SecConjunctDescriptions(statement);
			for(SecDescription description : descriptions) {
				result.add_child(description);
			}
			return result;
		}
	}
	public static SecDescriptions disjunct(CirStatement statement, Collection<SecDescription> descriptions) throws Exception {
		if(descriptions == null || descriptions.size() < 1)
			throw new IllegalArgumentException("Invalid descriptions: null");
		else {
			SecDescriptions result = new SecDisjunctDescriptions(statement);
			for(SecDescription description : descriptions) {
				result.add_child(description);
			}
			return result;
		}
	}
	
	/* verification */
	public static boolean is_constraint(SecDescription constraint) {
		if(constraint instanceof SecConstraint) {
			return true;
		}
		else if(constraint instanceof SecDescriptions) {
			int n = ((SecDescriptions) constraint).number_of_descriptions();
			for(int k = 0; k < n; k++) {
				if(!is_constraint(((SecDescriptions) constraint).get_description(k))) {
					return false;
				}
			}
			return n > 0;
		}
		else {
			return false;
		}
	}
	public static boolean is_state_error(SecDescription state_error) {
		if(state_error instanceof SecStatementError
			|| state_error instanceof SecExpressionError
			|| state_error instanceof SecBinaryValueError
			|| state_error instanceof SecUnaryValueError) {
			return true;
		}
		else if(state_error instanceof SecDescriptions) {
			int n = ((SecDescriptions) state_error).number_of_descriptions();
			for(int k = 0; k < n; k++) {
				if(!is_state_error(((SecDescriptions) state_error).get_description(k))) {
					return false;
				}
			}
			return n > 0;
		}
		else {
			return false;
		}
	}
	
}
