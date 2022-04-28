package com.jcsa.jcmutest.mutant.ctx2mutant.base;

/**
 * 	The category of AstContextState to describe the syntactic mutation.
 * 	
 * 	@author yukimula
 *
 */
public enum AstContextClass {
	
	/** cov_time(statement;	 min_times, max_times) 	**/	cov_time,
	/** eva_cond(statement;	 condition, must_need) 	**/	eva_cond,
	/** sed_muta(location;	 mutant_ID, clas_oprt) 	**/	sed_muta,
	
	/** mut_stmt(statement;	 orig_exec, muta_exec) 	**/	mut_stmt,
	/** mut_flow(statement;	 orig_next, muta_next) 	**/	mut_flow,
	/** mut_trap(module;	 true_exec, trp_value) 	**/	mut_trap,
	
	/** set_expr(expression; orig_expr, muta_expr) 	**/	set_expr,
	/** inc_expr(expression; orig_expr, different) 	**/	inc_expr,
	/** xor_expr(expression; orig_expr, different) 	**/	xor_expr,
	
}	
