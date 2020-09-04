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
	rsv_expr
	//chg_addr, chg_bool, chg_char, chg_sign, chg_usgn, chg_real, chg_list,
	//set_addr, set_bool, set_char, set_sign, set_usgn, set_real, set_list,
	//add_addr, add_char, add_sign, add_usgn, add_real,
	//mul_char, mul_sign, mul_usgn, mul_real,
	//and_char, and_sign, and_usgn, 
	//ior_char, ior_sign, ior_usgn,
	//xor_char, xor_sign, xor_usgn,
	//inc_addr, inc_char, inc_sign, inc_usgn, inc_real,
	//dec_addr, dec_char, dec_sign, dec_usgn, dec_real,
	//ext_char, ext_sign, ext_usgn, ext_real,
	//shk_char, shk_sign, shk_usgn, shk_real,
	//neg_char, neg_sign, neg_usgn, neg_real,
	//rsv_char, rsv_sign, rsv_usgn,
	
}
