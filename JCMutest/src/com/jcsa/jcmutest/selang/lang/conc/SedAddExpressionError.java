package com.jcsa.jcmutest.selang.lang.conc;

import com.jcsa.jcmutest.selang.SedKeywords;
import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedAddExpressionError extends SedBinExpressionError {

	public SedAddExpressionError(CirStatement statement, 
			CirExpression orig_expression,
			SedExpression muta_expression) throws Exception {
		super(statement, SedKeywords.add_expr, orig_expression, muta_expression);
	}

	@Override
	protected boolean verify_expression_type(SedKeywords type) {
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
		return new SedAddExpressionError(
				this.get_statement().get_cir_statement(),
				this.get_orig_expression().get_cir_expression(),
				this.get_muta_expression());
	}

}