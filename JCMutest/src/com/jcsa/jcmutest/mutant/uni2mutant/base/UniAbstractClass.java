package com.jcsa.jcmutest.mutant.uni2mutant.base;

/**
 * 	It defines the class of UniAbstractState.
 * 	
 * 	@author yukimula
 *
 */
public enum UniAbstractClass {
	
	/* conditional states */
	/** cov_time(statement; min_times, max_times) 	**/	cov_time,
	/** eva_cond(statement; condition, must_need) 	**/	eva_cond,
	/** sed_muta(statement; mutant_ID, clas_oprt) 	**/	sed_muta,
	
	/* path-errors states */
	/** mut_stmt(statement; orig_exec, muta_exec) 	**/	mut_stmt,
	/** mut_flow(statement; orig_next, muta_next) 	**/	mut_flow,
	/** trp_stmt(statement; exception, exception) 	**/	trp_stmt,
	
	/* data-errors states */
	/** set_expr(expression; orig_expr, muta_expr)	**/	set_expr,
	/** inc_expr(expression; orig_expr, different)	**/	inc_expr,
	/** xor_expr(expression; orig_expr, different)	**/	xor_expr,
	
}
