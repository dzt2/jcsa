package com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SedAbsUnaryValueError extends SedAbstractValueError {

	public SedAbsUnaryValueError(CirStatement location, 
			SedExpression orig_expression) {
		super(location, orig_expression);
	}
	
}
