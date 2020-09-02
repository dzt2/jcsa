package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.cha;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedExpressionError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	|--	|-- SedCharExpressionError	{orig_expr : char|uchar}				<br>
 * 	|--	|--	|--	SedSetCharExpressionError	set_char(expr, char|expr)		<br>
 * 	|--	|--	|--	SedAddCharExpressionError	add_char(expr, char|expr)		<br>
 * 	|--	|--	|--	SedMulCharExpressionError	mul_char(expr, char|expr)		<br>
 * 	|--	|--	|--	SedAndCharExpressionError	and_char(expr, char|expr)		<br>
 * 	|--	|--	|--	SedIorCharExpressionError	ior_char(expr, char|expr)		<br>
 * 	|--	|--	|--	SedXorCharExpressionError	xor_char(expr, char|expr)		<br>
 * 	|--	|--	|--	SedNegCharExpressionError	neg_char(expr)					<br>
 * 	|--	|--	|--	SedRsvCharExpressionError	rsv_char(expr)					<br>
 * 	|--	|--	|--	SedIncCharExpressionError	inc_char(expr)					<br>
 * 	|--	|--	|--	SedDecCharExpressionError	dec_char(expr)					<br>
 * 	|--	|--	|--	SedExtCharExpressionError	ext_char(expr)					<br>
 * 	|--	|--	|--	SedShkCharExpressionError	shk_char(expr)					<br>
 * 	|--	|--	|--	SedChgCharExpressionError	chg_char(expr)					<br>
 * @author dzt2
 *
 */
public abstract class SedCharExpressionError extends SedExpressionError {

	public SedCharExpressionError(CirStatement location, SedExpression orig_expression) {
		super(location, orig_expression);
	}

}
