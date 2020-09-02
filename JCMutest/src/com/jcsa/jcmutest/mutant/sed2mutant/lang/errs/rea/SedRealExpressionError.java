package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.rea;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SedRealExpressionError extends SedExpressionError {

	public SedRealExpressionError(CirStatement location, SedExpression orig_expression) {
		super(location, orig_expression);
	}

}
