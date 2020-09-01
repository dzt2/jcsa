package com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.zroval;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.SedAbsBinaryValueError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedIorIntegerError extends SedAbsBinaryValueError {

	public SedIorIntegerError(CirStatement location, SedExpression 
			orig_expression, SedExpression muta_expression) {
		super(location, orig_expression, muta_expression);
	}

	@Override
	public String generate_content() throws Exception {
		return "ior_long(" + this.get_orig_expression().generate_code()
				+ ", " + this.get_muta_expression().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedIorIntegerError(get_location().get_cir_statement(),
				this.get_orig_expression(), this.get_muta_expression());
	}

}
