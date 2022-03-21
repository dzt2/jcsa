package com.jcsa.jcmutest.mutant.uni2mutant.base;

/**
 * The class of uniformed abstract state (store-value pair) used in mutation
 * analysis for testability evaluation.
 * 
 * @author yukimula
 *
 */
public enum UniAbstractClass {
	
	/* conditioned state */
	/** cov_time(stmt_location; min_times, max_times) **/	cov_time,
	/** eva_bool(stmt_location; condition, need_must) **/	eva_bool,
	/** sed_muta(stmt_location; mutant_ID, clas_oprt) **/	sed_muta,
	
	/* path-errors state */
	/** mut_stmt(stmt_location; orig_exec, muta_exec) **/	mut_stmt,
	/** mut_flow(stmt_location; orig_next, muta_next) **/	mut_flow,
	/** trp_stmt(stmt_location; exception, nul_value) **/	trp_stmt,
	
	/* data-errors state */
	/** set_expr(expr_location; orig_expr, muta_expr) **/	set_expr,
	/** inc_expr(expr_location; base_expr, diff_expr) **/	inc_expr,
	/** xor_expr(expr_location; base_expr, diff_expr) **/	xor_expr,
	
}
