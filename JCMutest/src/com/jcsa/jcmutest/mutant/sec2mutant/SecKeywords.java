package com.jcsa.jcmutest.mutant.sec2mutant;

/**
 * It defines the keywords for symbolic error and constraint language (SEC).
 * 
 * @author yukimula
 *
 */
public enum SecKeywords {
	
	/** assert_on(stmt, expr) **/		asserton,
	
	/** add_stmt(stmt) **/				add_stmt,
	/** del_stmt(stmt) **/				del_stmt,
	/** set_stmt(stmt, stmt) **/		set_stmt,
	
	/** set_expr(expr, expr) **/		set_expr,
	/** add_expr(expr, oprt, expr) **/	add_expr,
	/** ins_expr(expr, oprt, expr) **/	ins_expr,
	/** uny_expr(expr, oprt) **/		uny_expr,
	
	/** set_value(expr, expr) **/		set_value,
	/** add_value(expr, expr) **/		add_value,
	/** mul_value(expr, expr) **/		mul_value,
	/** mod_value(expr, expr) **/		mod_value,
	/** and_value(expr, expr) **/		and_value,
	/** ior_value(expr, expr) **/		ior_value, 
	/** xor_value(expr, expr) **/		xor_value,
	
	/** chg_value(expr) **/				chg_value,
	/** inc_value(expr) **/				inc_value,
	/** dec_value(expr) **/				dec_value,
	/** ext_value(expr) **/				ext_value,
	/** shk_value(expr) **/				shk_value,
	/** neg_value(expr) **/				neg_value,
	/** rsv_value(expr) **/				rsv_value,
	
	/** conjunct(desc+) **/				conjunct,
	/** disjunct(desc+) **/				disjunct,
	
}
