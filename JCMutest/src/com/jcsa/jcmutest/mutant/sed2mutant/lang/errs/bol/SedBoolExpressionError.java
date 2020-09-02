package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.bol;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	|--	|--	SedBoolExpressionError	{orig_expr : boolean}				<br>
 * 	|--	|--	|--	SedSetBoolExpressionError	set_bool(expr, bool|expr)	<br>
 * 	|--	|--	|--	SedNotBoolExpressionError	not_bool(expr)				<br>
 * 	@author dzt2
 *
 */
public abstract class SedBoolExpressionError extends SedExpressionError {

	public SedBoolExpressionError(CirStatement location, SedExpression orig_expression) {
		super(location, orig_expression);
	}

}
