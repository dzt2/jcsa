package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * inc_{char|sign|usign|real|addr}(expr)
 * @author dzt2
 *
 */
public class SedIncExpressionError extends SedConExpressionError {

	protected SedIncExpressionError(CirStatement statement, CirExpression orig_expression) throws Exception {
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
		return "inc_" + this.get_orig_type() + "(" + this.get_orig_expression().generate_code() + ")";
	}

}
