package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * shk_{char|sign|usign|real}(expr)
 * 
 * @author dzt2
 *
 */
public class SedShkExpressionError extends SedConExpressionError {

	protected SedShkExpressionError(CirStatement statement, CirExpression orig_expression) throws Exception {
		super(statement, orig_expression);
	}

	@Override
	protected boolean verify_orig_type() throws Exception {
		switch(this.get_orig_type()) {
		case cchar:
		case csign:
		case usign:
		case creal:	return true;
		default:	return false;
		}
	}

	@Override
	protected String generate_content() throws Exception {
		return "shk_" + this.get_orig_type() + "(" + this.get_orig_expression().generate_code() + ")";
	}

}
