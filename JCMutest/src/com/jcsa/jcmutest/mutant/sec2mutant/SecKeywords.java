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
	/** trp_stmt(stmt) **/				trp_stmt,
	
	/** set_expr(expr, expr) **/		set_expr,
	/** add_expr(expr, oprt, expr) **/	add_expr,
	/** ins_expr(expr, oprt, expr) **/	ins_expr,
	/** uny_expr(expr, oprt) **/		uny_expr,
	
	/** conjunct(desc+) **/				conjunct,
	/** disjunct(desc+) **/				disjunct,
	
}
