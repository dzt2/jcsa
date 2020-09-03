package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * mut_expr(expr, expr'): expr ==> expr'
 * @author yukimula
 *
 */
public class SedMutExpressionError extends SedAbsExpressionError {

	/* definitions */
	private SedExpression muta_expression;
	protected SedMutExpressionError(CirStatement statement, 
			CirExpression orig_expression,
			SedExpression muta_expression) throws Exception {
		super(statement, orig_expression);
		if(muta_expression == null)
			throw new IllegalArgumentException("Invalid muta_expression");
		else
			this.muta_expression = muta_expression;
	}
	
	/**
	 * @return the expression that mutate the original expression one
	 */
	public SedExpression get_muta_expression() { return muta_expression; }

	@Override
	protected String generate_content() throws Exception {
		return "mut_expr(" + this.get_orig_expression().generate_code() +
				", " + this.muta_expression.generate_code() + ")";
	}

}
