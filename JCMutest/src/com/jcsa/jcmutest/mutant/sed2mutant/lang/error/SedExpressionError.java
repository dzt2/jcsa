package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;

public abstract class SedExpressionError extends SedStateError {
	
	/**
	 * @return the original expression where the error occurs
	 */
	public SedExpression get_orig_expression() {
		return (SedExpression) this.get_child(1);
	}
	
}
