package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedDecCharExpressionError extends SedCharExpressionError {

	public SedDecCharExpressionError(CirStatement location, SedExpression orig_expression) {
		super(location, orig_expression);
	}

	@Override
	protected String generate_content() throws Exception {
		return "dec_char(" + this.get_orig_expression().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedDecCharExpressionError(
				this.get_location().get_cir_statement(),
				this.get_orig_expression());
	}

}
