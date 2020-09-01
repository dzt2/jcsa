package com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SedAbsBinaryValueError extends SedAbstractValueError {

	public SedAbsBinaryValueError(CirStatement location, 
			SedExpression orig_expression, 
			SedExpression muta_expression) {
		super(location, orig_expression);
		this.add_child(muta_expression);
	}
	
	public SedExpression get_muta_expression() {
		return (SedExpression) this.get_child(2);
	}
	
}
