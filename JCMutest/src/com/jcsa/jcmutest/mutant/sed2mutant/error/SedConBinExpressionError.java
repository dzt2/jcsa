package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SedConBinExpressionError extends SedConExpressionError {
	
	private SedExpression muta_expression;
	private SedLocationType muta_type;
	protected SedConBinExpressionError(CirStatement statement, 
			CirExpression orig_expression,
			SedExpression muta_expression) throws Exception {
		super(statement, orig_expression);
		if(muta_expression == null)
			throw new IllegalArgumentException("Invalid muta_expression");
		else {
			this.muta_expression = muta_expression;
			this.muta_type = SedStateError.location_type(this.muta_expression);
		}
	}
	
	/**
	 * @return the expression to replace the original expression
	 */
	public SedExpression get_muta_expression() {
		return this.muta_expression;
	}
	/**
	 * @return the type of the muta-expression
	 */
	public SedLocationType get_muta_type() {
		return this.muta_type;
	}
	
}
