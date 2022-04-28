package com.jcsa.jcmutest.mutant.ctx2mutant.base;

/**
 * 	It defines the type of abstract execution state in mutation testing based on
 * 	the AstCirNode as its state-location.	<br>
 * 	
 * 	@author yukimula
 *	
 */
public enum ContextMutaClass {
	
	/** sed_muta(location;	 clas_oprt, parameter) 	**/	sed_muta,
	/** cov_time(statement;	 min_times, max_times) 	**/	cov_time,
	/** eva_cond(statement;	 condition, must_need) 	**/	eva_cond,
	
	/** set_stmt(statement;  orig_exec, muta_exec) 	**/	set_stmt,
	/** set_flow(statement;  orig_next, muta_next) 	**/	set_flow,
	/** trp_stmt(statement;	 orig_exec, exception) 	**/	trp_stmt,
	
	/** set_expr(expression; orig_expr, muta_expr) **/	set_expr,
	/** inc_expr(expression; orig_expr, different) **/	inc_expr,
	/** xor_expr(expression; orig_expr, different) **/	xor_expr,
	
}
