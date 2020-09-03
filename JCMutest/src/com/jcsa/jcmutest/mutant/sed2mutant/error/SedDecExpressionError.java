package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * dec_{char|sign|usign|real|addr}(expr)
 * @author dzt2
 *
 */
public class SedDecExpressionError extends SedConExpressionError {

	protected SedDecExpressionError(CirStatement statement, CirExpression orig_expression) throws Exception {
		super(statement, orig_expression);
	}

	@Override
	protected boolean verify_orig_type() throws Exception {
		switch(this.get_orig_type()) {
		case cchar:
		case csign:
		case usign:
		case creal:	
		case caddr:	return true;
		default:	return false;
		}
	}

	@Override
	protected String generate_content() throws Exception {
		return "dec_" + this.get_orig_type() + "(" + this.get_orig_expression().generate_code() + ")";
	}

}
