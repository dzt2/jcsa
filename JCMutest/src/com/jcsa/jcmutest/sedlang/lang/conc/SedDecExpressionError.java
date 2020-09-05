package com.jcsa.jcmutest.sedlang.lang.conc;

import com.jcsa.jcmutest.sedlang.SedExpressionType;
import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedDecExpressionError extends SedUnyExpressionError {
	
	public SedDecExpressionError(CirStatement statement, CirExpression orig_expression)
			throws Exception {
		super(statement, SedKeywords.dec_expr, orig_expression);
	}
	
	@Override
	protected boolean verify_expression_type(SedExpressionType type) {
		switch(type) {
		case caddr:
		case cchar:
		case csign:
		case usign:
		case creal: return true;
		default:	return false;
		}
	}
	
	@Override
	protected SedNode construct() throws Exception {
		return new SedDecExpressionError(
				this.get_statement().get_cir_statement(),
				this.get_orig_expression().get_cir_expression());
	}
	
}
