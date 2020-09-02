package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SedStateError						{location: SedLabel}				<br>
 * 	|--	SedStatementError				{orig_statement: SedLabel}			<br>
 * 	|--	|--	SedAddStatementError		add_stmt(orig_stmt)					<br>
 * 	|--	|--	SedDelStatementError		del_stmt(orig_stmt)					<br>
 * 	|--	|--	SedSetStatementError		set_stmt(orig_stmt, muta_stmt)		<br>
 * 	|--	SedExpressionError				{orig_expression: SedExpression}	<br>
 * 	|--	|--	SedAbstExpressionError											<br>
 * 	|--	|--	|--	SedInsExpressionError	ins_expr(expr, oprt)				<br>
 * 	|--	|--	|--	SedSetExpressionError	set_expr(orig_expr, muta_expr)		<br>
 * 	|--	|--	|--	SedAddExpressionError	add_expr(orig_expr, oprt, oprd)		<br>
 * 	|--	|--	SedBoolExpressionError	{orig_expr : boolean}					<br>
 * 	|--	|--	|--	SedSetBoolExpressionError	set_bool(expr, bool|expr)		<br>
 * 	|--	|--	|--	SedNotBoolExpressionError	not_bool(expr)					<br>
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
 * 	|--	|--	SedRealExpressionError	{orig_expr : float|double}				<br>
 * 	|--	|--	|--	SedSetRealExpressionError	set_real(expr, double|expr)		<br>
 * 	|--	|--	|--	SedAddRealExpressionError	add_real(expr, double|expr)		<br>
 * 	|--	|--	|--	SedMulRealExpressionError	mul_real(expr, double|expr)		<br>
 * 	|--	|--	|--	SedNegRealExpressionError	neg_real(expr)					<br>
 * 	|--	|--	|--	SedIncRealExpressionError	inc_real(expr)					<br>
 * 	|--	|--	|--	SedDecRealExpressionError	dec_real(expr)					<br>
 * 	|--	|--	|--	SedExtRealExpressionError	ext_real(expr)					<br>
 * 	|--	|--	|--	SedShkRealExpressionError	shk_real(expr)					<br>
 * 	|--	|--	|--	SedChgRealExpressionError	chg_real(expr)					<br>
 * 	|--	|--	SedAddrExpressionError	{orig_expr : pointer|address}			<br>
 * 	|--	|--	|--	SedSetAddrExpressionError	set_addr(expr, long|expr)		<br>
 * 	|--	|--	|--	SedAddAddrExpressionError	add_addr(expr, long|expr)		<br>
 * 	|--	|--	|--	SedIncAddrExpressionError	inc_addr(expr)					<br>
 * 	|--	|--	|--	SedDecAddrExpressionError	dec_addr(expr)					<br>
 * 	|--	|--	|--	SedChgAddrExpressionError	chg_addr(expr)					<br>
 * 	|--	|--	SedByteExpressionError	{orig_expr : struct|union|void}			<br>
 * 	|--	|--	|--	SedSetByteExpressionError	set_byte(expr, expr)			<br>
 * 	|--	|--	|--	SedChgByteExpressionError	chg_byte(expr)					<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedStateError extends SedNode {
	
	/** param[0] := location; param[1] := generate_content() **/
	public static final String StateErrorStringTemplate = "seed#%s::%s";
	
	public SedStateError(CirStatement location) {
		super(null);
		this.add_child(new SedLabel(null, location));
	}
	
	/**
	 * @return the label of the statement where the state error occurs.
	 */
	public SedLabel get_location() { return (SedLabel) this.get_child(0); }
	
	@Override
	public String generate_code() throws Exception {
		return String.format(StateErrorStringTemplate, 
				this.get_location().generate_code(), 
				this.generate_content());
	}
	
	/**
	 * @return the content following error head "seed#location::"
	 * @throws Exception
	 */
	protected abstract String generate_content() throws Exception;
	
}
