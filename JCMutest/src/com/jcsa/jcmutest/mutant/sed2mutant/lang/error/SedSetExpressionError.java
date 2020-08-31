package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;

public class SedSetExpressionError extends SedExpressionError {
	
	public SedExpression get_muta_expression() {
		return (SedExpression) this.get_child(2);
	}

	@Override
	protected SedNode clone_self() {
		return new SedSetExpressionError();
	}

	@Override
	public String generate_code() throws Exception {
		return "set_stmt(" + this.get_orig_expression().generate_code() + 
				", " + this.get_muta_expression().generate_code() + ")";
	}
	
}
