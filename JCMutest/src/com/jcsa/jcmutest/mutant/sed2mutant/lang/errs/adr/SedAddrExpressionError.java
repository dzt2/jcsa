package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.adr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	|--	|--	SedAddrExpressionError	{orig_expr : pointer|address}		<br>
 * 	|--	|--	|--	SedSetAddrExpressionError	set_addr(expr, long|expr)	<br>
 * 	|--	|--	|--	SedAddAddrExpressionError	add_addr(expr, long|expr)	<br>
 * 	|--	|--	|--	SedIncAddrExpressionError	inc_addr(expr)				<br>
 * 	|--	|--	|--	SedDecAddrExpressionError	dec_addr(expr)				<br>
 * 	|--	|--	|--	SedChgAddrExpressionError	chg_addr(expr)				<br>
 * 	
 * 	@author yukimula
 *
 */
public abstract class SedAddrExpressionError extends SedExpressionError {

	public SedAddrExpressionError(CirStatement location, SedExpression orig_expression) {
		super(location, orig_expression);
	}

}
