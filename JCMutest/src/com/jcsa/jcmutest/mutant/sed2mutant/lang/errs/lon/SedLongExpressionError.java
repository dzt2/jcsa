package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.lon;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	|--	|--	SedLongExpressionError	{orig_expr : (u)int|long|llong}			<br>
 * 	|--	|--	|--	SedSetLongExpressionError	set_long(expr, long|expr)		<br>
 * 	|--	|--	|--	SedAddLongExpressionError	add_long(expr, long|expr)		<br>
 * 	|--	|--	|--	SedMulLongExpressionError	mul_long(expr, long|expr)		<br>
 * 	|--	|--	|--	SedAndLongExpressionError	and_long(expr, char|expr)		<br>
 * 	|--	|--	|--	SedIorLongExpressionError	ior_long(expr, char|expr)		<br>
 * 	|--	|--	|--	SedXorLongExpressionError	xor_long(expr, char|expr)		<br>
 * 	|--	|--	|--	SedNegLongExpressionError	neg_long(expr)					<br>
 * 	|--	|--	|--	SedRsvLongExpressionError	rsv_long(expr)					<br>
 * 	|--	|--	|--	SedIncLongExpressionError	inc_long(expr)					<br>
 * 	|--	|--	|--	SedDecLongExpressionError	dec_long(expr)					<br>
 * 	|--	|--	|--	SedExtLongExpressionError	ext_long(expr)					<br>
 * 	|--	|--	|--	SedShkLongExpressionError	shk_long(expr)					<br>
 * 	|--	|--	|--	SedChgLongExpressionError	chg_long(expr)					<br>
 * 	@author dzt2
 *
 */
public abstract class SedLongExpressionError extends SedExpressionError {

	public SedLongExpressionError(CirStatement location, SedExpression orig_expression) {
		super(location, orig_expression);
	}

}
