package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	SedStateError						{location: SedLabel}			<br>
 * 	|--	SedStatementError				{orig_statement: SedLabel}		<br>
 * 	|--	|--	SedAddStatementError		add_stmt(statement)				<br>
 * 	|--	|--	SedDelStatementError		del_stmt(statement)				<br>
 * 	|--	|--	SedMutStatementError		mut_stmt(statement, statement)	<br>
 * 	|--	SedExpressionError				{orig_expression: SedExpression}<br>
 * 	|--	|--	SedInsExpressionError		ins_expr(oprt, expr)			<br>
 *	|--	|--	SedSetExpressionError		set_expr(expr, expr)			<br>
 * 	|--	|--	SedAddExpressionError		add_expr(expr, oprt, expr)		<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SedStateError extends SedNode {

	public SedStateError(CirStatement location) {
		super(null);
		this.add_child(new SedLabel(null, location));
	}
	
	/**
	 * @return the statement in which the error is seeded
	 */
	public SedLabel get_location() {
		return (SedLabel) this.get_child(0);
	}
	
	@Override
	public String generate_code() throws Exception {
		return "seed#" + this.get_location().
				generate_code() + "::" + this.generate_content();
	}
	
	/**
	 * @return the content to be generated following seed#stmt::
	 * @throws Exception
	 */
	public abstract String generate_content() throws Exception;
	
}
