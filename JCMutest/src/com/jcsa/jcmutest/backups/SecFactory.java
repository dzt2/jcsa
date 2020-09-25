package com.jcsa.jcmutest.backups;

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
	
	/* symbolic expression methods */
	/**
	 * @param expression
	 * @param value
	 * @return the symbolic condition of the expression as the given value
	 * @throws Exception
	 */
	public static SymExpression sym_condition(Object expression, boolean value) throws Exception {
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
		else if(CTypeAnalyzer.is_integer(type) || 
				CTypeAnalyzer.is_real(type) || 
				CTypeAnalyzer.is_pointer(type)) {
			SymExpression operand = SymFactory.new_constant(Integer.valueOf(0));
			if(value) {
				return SymFactory.not_equals(condition, operand);
			}
			else {
				return SymFactory.equal_with(condition, operand);
			}
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
	}
	/**
	 * @param statement
	 * @return symbolic identifier that describes the statement pointer
	 * @throws Exception
	 */
	public static SymExpression sym_statement(CirStatement statement) throws Exception {
		String name = statement.get_tree().get_localizer().get_execution(statement).toString();
		return SymFactory.new_identifier(CBasicTypeImpl.int_type, "@" + name);
	}
	
	/* constraint constructions */
	/**
	 * @param statement
	 * @param times
	 * @return execute(statement, int) where statement is required to be executed
	 * 		   for at least N loops where N > times.
	 * @throws Exception
	 */
	public static SecConstraint execution_constraint(CirStatement statement, int times) throws Exception {
		return new SecExecutionConstraint(statement, 
				SymFactory.new_constant(Integer.valueOf(times)));
	}
	/**
	 * @param statement
	 * @param condition
	 * @param value
	 * @return asserts(statement, condition-as-value)
	 * @throws Exception
	 */
	public static SecConstraint condition_constraint(CirStatement 
			statement, Object expression, boolean value) throws Exception {
		SymExpression condition = sym_condition(expression, value);
		return new SecConditionConstraint(statement, condition);
	}
	/**
	 * @param statement
	 * @param constraints
	 * @return the conjunctions of the constraints as provided.
	 * @throws Exception
	 */
	public static SecConstraint conjunct_constraints(CirStatement 
			statement, Iterable<SecConstraint> constraints) throws Exception {
		SecConjunctConstraints constraint = new SecConjunctConstraints(statement);
		for(SecConstraint child : constraints) { constraint.add_child(child); }
		if(constraint.number_of_constraints() == 0) {
			throw new IllegalArgumentException("No constraint provided");
		}
		else if(constraint.number_of_constraints() == 1) {
			return constraint.get_constraint(0);
		}
		else {
			return constraint;
		}
	}
	/**
	 * @param statement
	 * @param constraints
	 * @return the disjunction of the constraints as provided.
	 * @throws Exception
	 */
	public static SecConstraint disjunct_constraints(CirStatement 
			statement, Iterable<SecConstraint> constraints) throws Exception {
		SecDisjunctConstraints constraint = new SecDisjunctConstraints(statement);
		for(SecConstraint child : constraints) { constraint.add_child(child); }
		if(constraint.number_of_constraints() == 0) {
			throw new IllegalArgumentException("No constraint provided");
		}
		else if(constraint.number_of_constraints() == 1) {
			return constraint.get_constraint(0);
		}
		else {
			return constraint;
		}
	}
	
	/* statement error creation */
	/**
	 * @param statement
	 * @return the statement is executed in testing even though it should NOT
	 *         be executed in the original version of the program.
	 * @throws Exception
	 */
	public static SecStatementError add_statement(CirStatement statement) throws Exception {
		return new SecAddStatementError(statement, statement);
	}
	/**
	 * @param statement
	 * @return the statement is not executed in testing even though it should
	 * 		   have been executed in original version of the program.
	 * @throws Exception
	 */
	public static SecStatementError del_statement(CirStatement statement) throws Exception {
		return new SecDelStatementError(statement, statement);
	}
	/**
	 * @param source
	 * @param target
	 * @return the target is executed following the execution of source even
	 *  	   though it should NOT be in original program.
	 * @throws Exception
	 */
	public static SecStatementError set_statement(CirStatement source, CirStatement target) throws Exception {
		return new SecSetStatementError(source, source, target);
	}
	
	/* expression error creation */
	/**
	 * @param statement
	 * @param orig_expression
	 * @param muta_expression
	 * @return orig_expr --> muta_expr
	 * @throws Exception
	 */
	public static SecExpressionError set_expression(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SecSetExpressionError(
				statement, orig_expression, SymFactory.parse(muta_expression));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param operator
	 * @param operand
	 * @return orig_expr --> orig_expr operator operand
	 * @throws Exception
	 */
	public static SecExpressionError add_expression(CirStatement statement,
			CirExpression orig_expression, COperator operator, Object operand) throws Exception {
		return new SecAddExpressionError(statement, orig_expression, operator, SymFactory.parse(operand));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param operator
	 * @param operand
	 * @return orig_expr --> operand operator orig_expr
	 * @throws Exception
	 */
	public static SecExpressionError ins_expression(CirStatement statement,
			CirExpression orig_expression, COperator operator, Object operand) throws Exception {
		switch(operator) {
		case arith_add:
		case arith_mul:
		case bit_and:
		case bit_or:
		case bit_xor:
		{
			return new SecAddExpressionError(statement, orig_expression, operator, SymFactory.parse(operand));
		}
		case arith_sub:
		case arith_div:
		case arith_mod:
		case left_shift:
		case righ_shift:
		{
			return new SecInsExpressionError(statement, orig_expression, operator, SymFactory.parse(operand));
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param operator
	 * @return orig_expr --> operator(orig_expr)
	 * @throws Exception
	 */
	public static SecExpressionError uny_expression(CirStatement statement,
			CirExpression orig_expression, COperator operator) throws Exception {
		return new SecUnyExpressionError(statement, orig_expression, operator);
	}
	
	/* reference error creation */
	/**
	 * @param statement
	 * @param orig_expression
	 * @param muta_expression
	 * @return orig_expr --> muta_expr {as reference}
	 * @throws Exception
	 */
	public static SecReferenceError set_reference(CirStatement statement,
			CirExpression orig_expression, Object muta_expression) throws Exception {
		return new SecSetReferenceError(statement, orig_expression, SymFactory.parse(muta_expression));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param operator
	 * @param operand
	 * @return orig_expr --> orig_expr operator operand {as reference}
	 * @throws Exception
	 */
	public static SecReferenceError add_reference(CirStatement statement,
			CirExpression orig_expression, COperator operator, Object operand) throws Exception {
		return new SecAddReferenceError(statement, orig_expression, operator, SymFactory.parse(operand));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param operator
	 * @param operand
	 * @return orig_expr --> operand operator orig_expr {as reference}
	 * @throws Exception
	 */
	public static SecReferenceError ins_reference(CirStatement statement,
			CirExpression orig_expression, COperator operator, Object operand) throws Exception {
		return new SecInsReferenceError(statement, orig_expression, operator, SymFactory.parse(operand));
	}
	/**
	 * @param statement
	 * @param orig_expression
	 * @param operator
	 * @return orig_expr --> operator(orig_expr) {as reference}
	 * @throws Exception
	 */
	public static SecReferenceError uny_reference(CirStatement statement,
			CirExpression orig_expression, COperator operator) throws Exception {
		return new SecUnyReferenceError(statement, orig_expression, operator);
	}
	
	/* unique trap|none error */
	/**
	 * @param statement
	 * @return trap()
	 * @throws Exception
	 */
	public static SecUniqueError trap_error(CirStatement statement) throws Exception {
		return new SecTrapError(statement);
	}
	/**
	 * @param statement
	 * @return none()
	 * @throws Exception
	 */
	public static SecUniqueError none_error(CirStatement statement) throws Exception {
		return new SecNoneError(statement);
	}
	
}
