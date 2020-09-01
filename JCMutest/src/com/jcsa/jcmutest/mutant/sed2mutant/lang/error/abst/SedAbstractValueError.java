package com.jcsa.jcmutest.mutant.sed2mutant.lang.error.abst;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.error.SedStateError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	|--	SedAbstractValueError			{orig_expression: SedExpression}<br>
 * 	|--	|--	SedNotBooleanError			not_expr(expr)					<br>
 * 	|--	|--	SedRsvIntegerError			rsv_expr(expr)					<br>
 * 	|--	|--	SedNegNumericError			neg_expr(expr)					<br>
 * 	|--	|--	SedSetBooleanError			set_bool(expr, bool|expr)		<br>
 * 	|--	|--	SedMutBooleanError			not_bool(expr)					<br>
 * 	|--	|--	SedSetCharacterError		set_char(expr, char|expr)		<br>
 * 	|--	|--	SedChgCharacterError		chg_char(expr)					<br>
 * 	|--	|--	SedSetIntegerError			set_int(expr, long|expr)		<br>
 * 	|--	|--	SedChgIntegerError			mut_int(expr)					<br>
 * 	|--	|--	SedSetDoubleError			set_real(expr, double|expr)		<br>
 * 	|--	|--	SedChgDoubleError			mut_real(expr)					<br>
 * 	|--	|--	SedSetAddressError			set_addr(expr, long|expr)		<br>
 * 	|--	|--	SedChgAddressError			mut_addr(expr)					<br>
 * 	|--	|--	SedSetStructError			set_body(expr, expr)			<br>
 * 	|--	|--	SedChgStructError			mut_body(expr)					<br>
 * 	|--	|--	SedAddIntegerError			add_int(expr, long)				<br>
 * 	|--	|--	SedIncIntegerError			inc_int(expr)					<br>
 * 	|--	|--	SedDecIntegerError			dec_int(expr)					<br>
 * 	|--	|--	SedAddDoubleError			add_real(expr, double)			<br>
 * 	|--	|--	SedIncDoubleError			inc_real(expr)					<br>
 * 	|--	|--	SedDecDoubleError			dec_real(expr)					<br>
 * 	|--	|--	SedAddAddressError			add_addr(expr, long)			<br>
 * 	|--	|--	SedIncAddressError			inc_addr(expr)					<br>
 * 	|--	|--	SedDecAddressError			dec_addr(expr)					<br>
 * 	|--	|--	SedMulIntegerError			mul_int(expr, long)				<br>
 * 	|--	|--	SedGrowIntegerError			grw_int(expr)					<br>
 * 	|--	|--	SedShrinkIntegerError		shk_int(expr)					<br>
 * 	|--	|--	SedMulDoubleError			mul_real(expr, double)			<br>
 * 	|--	|--	SedGrowDoubleError			grw_real(expr)					<br>
 * 	|--	|--	SedShrinkDoubleError		shk_real(expr)					<br>
 * 	|--	|--	SedAndIntegerError			and_int(expr)					<br>
 * 	|--	|--	SedIorIntegerError			ior_int(expr)					<br>
 * 	|--	|--	SedXorIntegerError			xor_int(expr)					<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SedAbstractValueError extends SedStateError {

	public SedAbstractValueError(CirStatement location, 
			SedExpression orig_expression) {
		super(location);
		this.add_child(orig_expression);
	}
	
	/**
	 * @return the expression in which the error occurs
	 */
	public SedExpression get_orig_expression() {
		return (SedExpression) this.get_child(1);
	}

}
