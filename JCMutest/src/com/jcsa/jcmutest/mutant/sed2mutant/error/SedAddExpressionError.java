package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * add_{char|sign|usign|real|addr}(expr, expr)
 * @author dzt2
 *
 */
public class SedAddExpressionError extends SedConBinExpressionError {

	protected SedAddExpressionError(CirStatement statement, 
			CirExpression orig_expression,
			SedExpression muta_expression) throws Exception {
		super(statement, orig_expression, muta_expression);
	}

	@Override
	protected boolean verify_orig_type() throws Exception {
		switch(this.get_orig_type()) {
		case cchar:
		case csign:
		case usign:
		case creal:
		case caddr: return true;
		default:	return false;
		}
	}

	@Override
	protected String generate_content() throws Exception {
		return "add_" + this.get_orig_type().toString() + "("
				+ this.get_orig_expression().generate_code() + ", " 
				+ this.get_muta_expression().generate_code() + ")";
	}

}
