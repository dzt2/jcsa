package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * neg_{char|sign|usign|real}
 * @author yukimula
 *
 */
public class SedNegExpressionError extends SedConExpressionError {

	protected SedNegExpressionError(CirStatement statement, CirExpression orig_expression) throws Exception {
		super(statement, orig_expression);
	}

	@Override
	protected String generate_content() throws Exception {
		return "neg_" + this.get_orig_type().toString() + "("
				+ this.get_orig_expression().generate_code() + ")";
	}

	@Override
	protected boolean verify_orig_type() throws Exception {
		switch(this.get_orig_type()) {
		case cchar:
		case csign:
		case usign:
		case creal:	return true;
		default: 	return false;
		}
	}

}
