package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * 	The category of abstract execution state.
 * 	
 * 	@author yukimula
 *
 */
public enum AbsExecutionClass {
	
	/** cov_stmt(statement;	 min_times, max_times) 	**/	cov_time,
	/** eva_cond(statement;	 condition, must_need) 	**/	eva_cond,
	
	/** set_stmt(statement;  orig_exec, muta_exec) 	**/	set_stmt,
	/** set_flow(statement;  orig_next; muta_next) 	**/	set_flow,
	/** trp_stmt(statement;  exception, exception) 	**/ trp_stmt,
	
	/** set_expr(expression; ori_value; mut_value) 	**/	set_expr,
	/** inc_expr(expression; ori_value, different) 	**/	inc_expr,
	/** xor_expr(expression; ori_value, different) 	**/	xor_expr,
	
}
