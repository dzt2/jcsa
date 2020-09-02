package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.abs;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	|--	|--	SedAbstExpressionError										<br>
 * 	|--	|--	|--	SedInsExpressionError	ins_expr(expr, oprt)				<br>
 * 	|--	|--	|--	SedSetExpressionError	set_expr(orig_expr, muta_expr)		<br>
 * 	|--	|--	|--	SedAddExpressionError	add_expr(orig_expr, oprt, oprd)		<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedAbstExpressionError extends SedExpressionError {
	
	public SedAbstExpressionError(CirStatement 
			location, SedExpression orig_expression) {
		super(location, orig_expression);
	}
	
}
