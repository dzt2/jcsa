package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

/**
 * 	SedStateError													<br>
 * 	|--	SedStatementError		{statement: SedStatement}			<br>
 * 	|--	|--	SedAddStatement		(add_statement(statement))			<br>
 * 	|--	|--	SedDelStatement		(del_statement(statement))			<br>
 * 	|--	|--	SedSetStatement		(set_statement(source, target))		<br>
 * 	|--	SedExpressionError											<br>
 * 	|--	|--	SedInsExpression	(ins_expression(expr, operator))	<br>
 * 	|--	|--	SedAddExpression	(add_expression(expr, oprt, expr))	<br>
 * 	|--	|--	SedSetExpression	(set_expression(expr, expr))		<br>
 * @author yukimula
 *
 */
public abstract class SedStateError extends SedAssertion {

}
