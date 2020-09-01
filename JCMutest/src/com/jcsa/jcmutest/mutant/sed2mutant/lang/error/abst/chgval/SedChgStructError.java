package com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.chgval;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst.SedAbsUnaryValueError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedChgStructError extends SedAbsUnaryValueError {

	public SedChgStructError(CirStatement 
			location, SedExpression orig_expression) {
		super(location, orig_expression);
	}

	@Override
	public String generate_content() throws Exception {
		return "chg_body(" + this.get_orig_expression().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedChgStructError(this.get_location().
				get_cir_statement(), this.get_orig_expression());
	}

}
