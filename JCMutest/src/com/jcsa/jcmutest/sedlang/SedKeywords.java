package com.jcsa.jcmutest.sedlang;

/**
 * Keywords in symbolic execution description.
 * 
 * @author yukimula
 *
 */
public enum SedKeywords {
	
	/* constraint */
	execute,
	cassert,
	
	/* statement-error */
	add_stmt,
	del_stmt,
	set_stmt,
	mut_stmt,
	
	/* abs-value-error */
	app_expr,
	ins_expr,
	mut_expr,
	nev_expr,
	
	/* con-value-error */
	chg_expr,
	set_expr,
	add_expr,
	mul_expr,
	and_expr,
	ior_expr,
	xor_expr,
	inc_expr,
	dec_expr,
	ext_expr,
	shk_expr,
	neg_expr,
	rsv_expr,
	
	conjunct,
	disjunct,
}
