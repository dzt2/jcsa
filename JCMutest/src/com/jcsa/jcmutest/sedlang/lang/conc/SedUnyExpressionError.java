package com.jcsa.jcmutest.sedlang.lang.conc;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SedUnyExpressionError extends SedConcreteValueError {

	public SedUnyExpressionError(CirStatement statement, SedKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword, orig_expression);
	}
	
	@Override
	protected String generate_follow_content() throws Exception {
		return this.get_orig_expression().generate_code();
	}
	
}
