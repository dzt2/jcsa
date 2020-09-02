package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.adr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedDecAddrExpressionError extends SedAddrExpressionError {

	public SedDecAddrExpressionError(CirStatement location, SedExpression orig_expression) {
		super(location, orig_expression);
	}
	
	@Override
	protected String generate_content() throws Exception {
		return "dec_addr(" + this.get_orig_expression().generate_code() + ")";
	}
	
	@Override
	protected SedNode clone_self() {
		return new SedDecAddrExpressionError(
				this.get_location().get_cir_statement(),
				this.get_orig_expression());
	}

}
