package com.jcsa.jcmuta.mutant.err2mutation;

/**
 * Type of abstract state error.<br>
 * <code>
 * 	execute(stmt)					<br>
 * 	not_execute(stmt)				<br>
 * 	set_bool --> not_bool			<br>
 * 	set_numb, xor_numb				<br>
 * 	neg_numb, rsv_numb				<br>
 * 	dif_numb, inc_numb, dec_numb	<br>
 * 	chg_numb						<br>
 * 	dif_addr, set_addr, mov_addr	<br>
 * 	mut_value, mut_refer			<br>
 * </code>
 * @author yukimula
 *
 */
public enum ErrorType {
	
	/** execute(stmt) **/			execute,
	/** not_execute(stmt) **/		not_execute,
	
	/** set_bool(expr) **/			set_bool,
	/** chg_bool(expr) **/			chg_bool,
	
	/** set_numb(expr, value) **/	set_numb,
	/** neg_numb(expr) **/			neg_numb,
	/** xor_numb(expr, value) **/	xor_numb,
	/** rsv_numb(expr) **/			rsv_numb,
	/** dif_numb(expr, value) **/	dif_numb,
	/** inc_numb(expr) **/			inc_numb,
	/** dec_numb(expr) **/			dec_numb,
	/** chg_numb(expr) **/			chg_numb,
	
	/** dif_addr(expr, value) **/	dif_addr,
	/** set_addr(expr, value) **/	set_addr,
	/** chg_addr(expr) **/			chg_addr,
	
	/** mut_expr(expr) **/			mut_expr,
	/** mut_refer(expr) **/			mut_refer,
	
}
