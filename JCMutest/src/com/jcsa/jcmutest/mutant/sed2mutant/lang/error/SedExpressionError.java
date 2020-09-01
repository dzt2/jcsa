package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	SedExpressionError				{orig_expression: SedExpression}<br>
 * 	|--	SedInsExpressionError		ins_expr(oprt, expr)			<br>
 *	|--	SedSetExpressionError		set_expr(expr, expr)			<br>
 * 	|--	SedAddExpressionError		add_expr(expr, oprt, expr)		<br>
 * </code>
 * @author dzt2
 *
 */
public abstract class SedExpressionError extends SedStateError {

	public SedExpressionError(CirStatement location,
			SedExpression orig_expression) {
		super(location);
		this.add_child(orig_expression);
	}
	
	/**
	 * @return the expression where the error is seeded
	 */
	public SedExpression get_orig_expression() {
		return (SedExpression) this.get_child(1);
	}
	
}
