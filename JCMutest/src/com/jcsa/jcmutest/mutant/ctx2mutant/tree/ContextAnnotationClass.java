package com.jcsa.jcmutest.mutant.ctx2mutant.tree;

/**
 * 	The category of ContextAnnotation
 * 	
 * 	@author yukimula
 *
 */
public enum ContextAnnotationClass {
	
	/** cov_time(statement; min_times) **/	cov_time,
	/** eva_cond(statement; condition) **/	eva_cond,
	/** mus_cond(statement; condition) **/	mus_cond,
	
	/** set_stmt(statement; muta_exec) **/	set_stmt,
	/** set_flow(statement; muta_next) **/	set_flow,
	/** trp_stmt(statement; exception) **/	trp_stmt,
	
	/** set_expr(expression;muta_expr) **/	set_expr,
	/** inc_expr(expression;different) **/	inc_expr,
	/** xor_expr(expression;different) **/	xor_expr,
	
}
