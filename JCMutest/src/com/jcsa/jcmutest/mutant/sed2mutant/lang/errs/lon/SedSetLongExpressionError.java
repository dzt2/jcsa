package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedSetLongExpressionError extends SedLongExpressionError {

	public SedSetLongExpressionError(CirStatement location, 
			SedExpression orig_expression,
			SedExpression muta_expression) {
		super(location, orig_expression);
		this.add_child(muta_expression);
	}
	
	public SedExpression get_muta_expression() {
		return (SedExpression) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "set_long(" + this.get_orig_expression().generate_code()
				+ ", " + this.get_muta_expression().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedSetLongExpressionError(
				this.get_location().get_cir_statement(),
				this.get_orig_expression(), 
				this.get_muta_expression());
	}
	
}
