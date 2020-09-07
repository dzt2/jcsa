package com.jcsa.jcmutest.mutant.sel2mutant;

public enum SelKeywords {
	
	/* constraint */
	execute,
	asserts,
	
	/* statement error */
	add_stmt,
	del_stmt,
	set_stmt,
	
	/* expression error */
	ins_expr,
	set_expr,
	add_expr,
	put_expr,
	
	/* typed value error */
	chg_value,
	neg_value,
	rsv_value,
	inc_value,
	dec_value,
	ext_value,
	shk_value,
	
	set_value,
	add_value,
	mul_value,
	mod_value,
	and_value,
	ior_value,
	xor_value,
	
	/* composite error */
	conjunct,
	disjunct,
	
}
