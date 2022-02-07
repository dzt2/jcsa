package com.jcsa.jcmutest.mutant.cir2mutant.base;

/**
 * It defines the class of CirAbstractState and its structural definition.
 * 
 * @author yukimula
 *
 */
public enum CirAbstractClass {
	
	/* conditional state */
	/** cov_stmt(statement, stmt_key; lest_most, int_times) **/	cov_stmt,
	/** eva_expr(statement, stmt_key; must_need, condition) **/	eva_expr,
	/** ast_muta(statement, stmt_key; muta_id, mu_operator) **/	ast_muta,
	
	/* path-errors state */
	/** mut_stmt(statement, stmt_key; orig_exec, muta_exec) **/	mut_stmt,
	/** mut_flow(statement, stmt_key; orig_trgt, muta_trgt) **/	mut_flow,
	/** trp_stmt(statement, stmt_key; exception, exception) **/	trp_stmt,
	
	/* data-errors state */
	/** set_expr(expr|stmt, expr_key; ori_value, mut_value) **/	set_expr,
	/** inc_expr(expr|stmt, expr_key; bas_value, different) **/	inc_expr,
	/** xor_expr(expr|stmt, expr_key; bas_value, different) **/	xor_expr,
	
	
	
}
