package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * chg_{bool|char|sign|usign|real|list}(expr)
 * @author dzt2
 *
 */
public class SedChgExpressionError extends SedConExpressionError {

	protected SedChgExpressionError(CirStatement statement, CirExpression orig_expression) throws Exception {
		super(statement, orig_expression);
	}

	@Override
	protected boolean verify_orig_type() throws Exception {
		switch(this.get_orig_type()) {
		case cbool:
		case cchar:
		case csign:
		case usign:
		case creal:
		case caddr:
		case clist: return true;
		default:	return false;
		}
	}

	@Override
	protected String generate_content() throws Exception {
		return "chg_" + this.get_orig_type() + "(" + this.get_orig_expression().generate_code() + ")";
	}

}
