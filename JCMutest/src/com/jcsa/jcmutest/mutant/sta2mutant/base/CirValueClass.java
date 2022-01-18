package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * The category of value used to define the abstract execution state.
 * 
 * @author yukimula
 *
 */
public enum CirValueClass {
	
	/*	conditioned state	*/
	/**	cov_cond(should_or_not, condition);	**/	cov_cond,
	/**	cov_time(most_or_least, int_times);	**/	cov_time,
	
	/*	path-errors state	*/
	/**	set_stmt(orig_exec, muta_exec);		**/	set_stmt,
	/**	set_flow(orig_target, muta_target);	**/	set_flow,
	/**	set_trap(execution, exception);		**/	set_trap,
	
	/* 	data-errors state 	*/
	/**	set_expr(orig_value, muta_value);	**/	set_expr,
	/**	inc_expr(base_value, difference);	**/	inc_expr,
	/**	xor_expr(base_value, difference);	**/	xor_expr,
	
}
