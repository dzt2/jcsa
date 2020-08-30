package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;

/**
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	SedAssertion					{location: SedStatement}			<br>
 * 	|--	SedConstraint													<br>
 * 	|--	|--	SedExecutionConstraint	(execute(statement, int))			<br>
 * 	|--	|--	SedConditionConstraint	(assert(statement, expression))		<br>
 * 	|--	SedStateError													<br>
 * 	|--	|--	SedStatementError		{statement: SedStatement}			<br>
 * 	|--	|--	|--	SedAddStatement		(add_statement(statement))			<br>
 * 	|--	|--	|--	SedDelStatement		(del_statement(statement))			<br>
 * 	|--	|--	|--	SedSetStatement		(set_statement(source, target))		<br>
 * 	|--	|--	SedExpressionError											<br>
 * 	|--	|--	|--	SedInsExpression	(ins_expression(expr, operator))	<br>
 * 	|--	|--	|--	SedAddExpression	(add_expression(expr, oprt, expr))	<br>
 * 	|--	|--	|--	SedSetExpression	(set_expression(expr, expr))		<br>
 * 	|--	SedAssertions				{assertions: List<SedAssertion>}	<br>
 * 	|--	|--	SedConjunction												<br>
 * 	|--	|--	SedDisjunction												<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedAssertion extends SedNode {

	public SedAssertion() {
		super(null);
	}
	
	/**
	 * @return the label of the statement where the assertion is taken
	 */
	public SedLabel get_location() {
		return (SedLabel) this.get_child(0);
	}
	
	@Override
	public String generate_code() throws Exception {
		return "assert " + 
				this.get_location().generate_code() + 
				": " + this.generate_content() + ";";
	}
	
	protected abstract String generate_content() throws Exception;

}
