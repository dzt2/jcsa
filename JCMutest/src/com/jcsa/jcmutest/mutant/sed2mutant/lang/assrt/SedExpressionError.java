package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;

public abstract class SedExpressionError extends SedStateError {
	
	/**
	 * @return the expression being mutated
	 */
	public SedExpression get_orig_expression() {
		return (SedExpression) this.get_child(1);
	}
	
}
