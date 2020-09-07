package com.jcsa.jcmutest.mutant.sel2mutant;

/**
 * The keywords in SEL language defines the type of constraints, state errors.
 * 
 * @author yukimula
 *
 */
public enum SelKeywords {
	
	/** execute(stmt, expr) **/					execute,
	/** asserts(stmt, expr) **/					asserts,
	
	/** add_stmt(orig_stmt) **/					add_stmt,
	/** add_stmt(orig_stmt) **/					del_stmt,
	/** add_stmt(orig_stmt, next_stmt) **/		set_stmt,
	
	/** nev_expr(expr, oprt) **/				nev_expr,
	/** set_expr(expr, expr) **/				set_expr,
	/** add_expr(expr, oprt, expr) **/			add_expr,
	/** ins_expr(expr, oprt, expr) **/			ins_expr,
	
	/** chg_value[type](expr) **/				chg_value,
	/** neg_value[type](expr) **/				neg_value,
	/** rsv_value[type](expr) **/				rsv_value,
	/** inc_value[type](expr) **/				inc_value,
	/** dec_value[type](expr) **/				dec_value,
	/** ext_value[type](expr) **/				ext_value,
	/** shk_value[type](expr) **/				shk_value,
	
	/** set_value[type](expr, expr) **/			set_value,
	/** add_value[type](expr, expr) **/			add_value,
	/** mul_value[type](expr, expr) **/			mul_value,
	/** mod_value[type](expr, expr) **/			mod_value,
	/** and_value[type](expr, expr) **/			and_value,
	/** ior_value[type](expr, expr) **/			ior_value,
	/** xor_value[type](expr, expr) **/			xor_value,
	
	/** conjunct{description}+ **/				conjunct,
	/** disjunct{description}+ **/				disjunct,
}
