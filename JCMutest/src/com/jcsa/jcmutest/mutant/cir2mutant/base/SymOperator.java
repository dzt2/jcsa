package com.jcsa.jcmutest.mutant.cir2mutant.base;

/**
 * 	Refined type of symbolic condition.<br>
 * 	<br>
 * 	<code>
 * 	kill_fault:ast_muta(e, e.s, mid)			<br>
 * 	kill_fault:cir_muta(e, e.s, mid.cid)		<br>
 * 	<br>
 * 	evaluation:cov_stmt(e, e.s, time)			<br>
 * 	evaluation:eva_expr(e, e.s, expr)			<br>
 * 	<br>
 * 	path_error:del_exec(e, e.s, null)			<br>
 * 	path_error:inc_exec(e, e.s, null)			<br>
 * 	path_error:dec_exec(e, e.s, null)			<br>
 * 	path_error:mut_flow(e, t.n, f.n)			<br>
 * 	<br>
 * 	data_error:mut_expr(e, expr, error)			<br>
 * 	data_error:mut_refr(e, expr, error)			<br>
 * 	data_error:mut_stat(e, expr, error)			<br>
 * 	<br>
 * 	data_error:set_bool(e, expr, error)			<br>
 * 	data_error:set_true(e, expr, null)			<br>
 * 	data_error:set_fals(e, expr, null)			<br>
 * 	data_error:chg_bool(e, expr, null)			<br>
 * 	<br>
 * 	data_error:set_numb(e, expr, error)			<br>
 * 	data_error:set_post(e, expr, null)			<br>
 * 	data_error:set_negt(e, expr, null)			<br>
 * 	data_error:set_zero(e, expr, null)			<br>
 * 	data_error:set_npos(e, expr, null)			<br>
 * 	data_error:set_nneg(e, expr, null)			<br>
 * 	data_error:set_nzro(e, expr, null)			<br>
 * 	data_error:chg_numb(e, expr, null)			<br>
 * 	<br>
 * 	data_error:set_addr(e, expr, error)			<br>
 * 	data_error:set_null(e, expr, null)			<br>
 * 	data_error:set_invp(e, expr, null)			<br>
 * 	data_error:chg_addr(e, expr, null)			<br>
 * 	<br>
 * 	data_error:set_auto(e, expr, error)			<br>
 * 	data_error:chg_auto(e, expr, null)			<br>
 * 	<br>
 * 	data_error:inc_scop(e, expr, null)			<br>
 * 	data_error:dec_scop(e, expr, null)			<br>
 * 	data_error:ext_scop(e, expr, null)			<br>
 * 	data_error:shk_scop(e, expr, null)			<br>
 * 	<br>
 * 	</code>
 * 	@author yukimula
 *	
 */
public enum SymOperator {
	ast_muta,	cir_muta,	
	cov_stmt,	eva_expr,
	del_stmt,	inc_exec,	dec_exec,	mut_flow,
	mut_expr,	mut_refr,	mut_stat,
	set_bool,	set_true,	set_fals,	chg_bool,
	set_numb,	set_post,	set_negt,	set_zero,	set_npos,	set_nneg,	set_nzro,	chg_numb,
	set_addr,	set_null,	set_invp,	chg_addr,
	inc_scop,	dec_scop,	ext_scop,	shk_scop,
	set_auto,	chg_auto,
}
