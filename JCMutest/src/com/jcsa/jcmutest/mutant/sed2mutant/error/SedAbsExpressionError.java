package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	|--	|--	SedAbstractExpressionError									<br>
 * 	|--	|--	|--	SedInsExpressionError	ins_expr(expr, oprt)			<br>
 * 	|--	|--	|--	SedAppExpressionError	app_expr(e, o, e)				<br>
 * 	|--	|--	|--	SedMutExpressionError	mut_expr(expr, expr)			<br>
 * 	@author yukimula
 *
 */
public abstract class SedAbsExpressionError extends SedExpressionError {

	protected SedAbsExpressionError(CirStatement statement, 
			CirExpression orig_expression) throws Exception {
		super(statement, orig_expression);
	}

}
