package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.byt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	|--	|--	SedByteExpressionError	{orig_expr : struct|union|void}			<br>
 * 	|--	|--	|--	SedSetByteExpressionError	set_byte(expr, expr)			<br>
 * 	|--	|--	|--	SedChgByteExpressionError	chg_byte(expr)					<br>
 * 	@author dzt2
 *
 */
public abstract class SedByteExpressionError extends SedExpressionError {

	public SedByteExpressionError(CirStatement location, SedExpression orig_expression) {
		super(location, orig_expression);
	}

}
