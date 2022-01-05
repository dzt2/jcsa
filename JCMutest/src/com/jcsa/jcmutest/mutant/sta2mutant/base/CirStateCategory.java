package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * It defines the category of each execution state (abstract) in 
 * mutation testing.
 * 
 * @author yukimula
 *
 */
public enum CirStateCategory {
	
	/* conditions state */
	/** cov_stmt(execution, statement, {int_times}); 	**/	cov_time,
	/**	eva_expr(execution, statement, {condition});	**/	eva_expr,
	/**	end_stmt(execution, statement, {exception});	**/	end_stmt,
	
	/* data error state */
	/**	mut_diff(execution, expression, {oval, mval});	**/	mut_diff,
	/**	mut_expr(execution, expression, {oval, mval});	**/	mut_expr,
	/**	mut_refr(execution, expression, {oval, mval});	**/	mut_refr,
	
	/* difference state */
	/** inc_expr(execution, expression, {difference}); 	**/	inc_expr,
	/** xor_expr(execution, expression, {difference}); 	**/	xor_expr,
	/** scp_expr(execution, expression, {difference}); 	**/	scp_expr,
	
	/* path error state */
	/** mut_brac(execution, expression, {oval, mval}); 	**/	mut_brac,
	/** mut_stmt(execution, expression, {oval, mval}); 	**/	mut_stmt,
	/** mut_flow(execution, expression, {oval, mval}); 	**/	mut_flow,
	
}
