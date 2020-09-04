package com.jcsa.jcmutest.sedlang.lang.conc;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	SedSetExpressionError		{bool|char|sign|usig|real|addr|list}<br>
 * 	SedAddExpressionError		{char|sign|usig|real|addr}			<br>
 * 	SedMulExpressionError		{char|sign|usig|real}				<br>
 * 	SedAndExpressionError		{char|sign|usig}					<br>
 * 	SedIorExpressionError		{char|sign|usig}					<br>
 * 	SedXorExpressionError		{char|sign|usig}					<br>
 * 	
 * 	@author yukimula
 *
 */
public abstract class SedBinExpressionError extends SedConcreteValueError {

	public SedBinExpressionError(CirStatement statement, 
			SedKeywords keyword, 
			CirExpression orig_expression,
			SedExpression muta_expression)
			throws Exception {
		super(statement, keyword, orig_expression);
		this.add_child(muta_expression);
	}
	
	public SedExpression get_muta_expression() {
		return (SedExpression) this.get_child(3);
	}
	
	@Override
	protected String generate_follow_content() throws Exception {
		return 	this.get_orig_expression().generate_code() +
				", " + this.get_muta_expression().generate_code();
	}

}
