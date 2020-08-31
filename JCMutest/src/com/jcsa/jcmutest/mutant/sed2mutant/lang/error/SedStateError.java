package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;

/**
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	SedStateError						{location: SedLabel}			<br>
 * 	|--	SedStatementError				{orig_statement: SedLabel}		<br>
 * 	|--	|--	SedAddStatementError		add_stmt(orig_stmt)				<br>
 * 	|--	|--	SedDelStatementError		del_stmt(orig_stmt)				<br>
 * 	|--	|--	SedSetStatementError		set_stmt(orig_stmt, muta_stmt)	<br>
 * 	|--	SedExpressionError				{orig_expression: SedExpr}		<br>
 * 	|--	|--	SedInsExpressionError		ins_expr(expr, oprt)			<br>
 * 	|--	|--	SedAddExpressionError		add_expr(expr, oprt, expr)		<br>
 * 	|--	|--	SedSetExpressionError		set_expr(expr, expr)			<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * @author dzt2
 *
 */
public abstract class SedStateError extends SedNode {

	public SedStateError() {
		super(null);
	}
	
	/**
	 * @return the statement where the state error occurs.
	 */
	public SedLabel get_location() {
		return (SedLabel) this.get_child(0);
	}
	
}
