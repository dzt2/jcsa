package com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.unary;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.SedAbstractValueError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedRsvIntegerError extends SedAbstractValueError {

	public SedRsvIntegerError(CirStatement location, SedExpression orig_expression) {
		super(location, orig_expression);
	}

	@Override
	public String generate_content() throws Exception {
		return "rsv_long(" + this.get_orig_expression().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedRsvIntegerError(
				this.get_location().get_cir_statement(),
				this.get_orig_expression());
	}

}
