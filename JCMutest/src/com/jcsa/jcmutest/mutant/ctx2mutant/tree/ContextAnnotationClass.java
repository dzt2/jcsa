package com.jcsa.jcmutest.mutant.ctx2mutant.tree;

/**
 * 	The category of ContextAnnotation to represent abstract execution state.
 * 	
 * 	@author yukimula
 *
 */
public enum ContextAnnotationClass {
	
	/** cov_time(statement; int_times, max_times) **/	cov_time,
	/** eva_cond(statement; condition, must_need) **/	eva_cond,
	/** sed_muta(statement; mutant_ID, operators) **/	sed_muta,
	
	/** set_stmt(statement; orig_exec, muta_exec) **/	set_stmt,
	/** set_flow(statement; orig_next, muta_next) **/	set_flow,
	/** trp_stmt(statement; exception, exception) **/	trp_stmt,
	
	/** set_expr(expression;orig_expr, muta_expr) **/	set_expr,
	/** inc_expr(expression;orig_expr, muta_expr) **/	inc_expr,
	/** xor_expr(expression;orig_expr, muta_expr) **/	xor_expr,
	
	/** set_refr(statement; orig_expr, different) **/	set_refr,
	/** inc_refr(statement; orig_expr, different) **/	inc_refr,
	/** xor_refr(statement; orig_expr, different) **/	xor_refr,
	
}
