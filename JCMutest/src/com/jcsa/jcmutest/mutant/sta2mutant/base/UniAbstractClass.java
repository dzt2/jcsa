package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * 	It defines the class of uniform abstract execution state UniAbstractState.	<br>
 * 	<br>
 * 	<code>
 * 		cov_time(statement;  min_times, max_times): a coverage time counters;	<br>
 * 		eva_cond(statement;  condition, must_need):	a conditional assertions;	<br>
 * 		
 * 		set_stmt(statement;	 orig_exec, muta_exec): statement error mutation;	<br>
 * 		set_flow(statement;	 orig_next, muta_next):	flow-related error state;	<br>
 * 		set_stmt(statement;	 orig_exec, exception): throw exceptions to exit;	<br>
 * 		
 * 		set_expr(expression; orig_expr, muta_expr): expression used mutation;	<br>
 * 		inc_expr(expression; orig_expr, different): incremental differential;	<br>
 * 		xor_expr(expression; orig_expr, different): bit-related differential;	<br>
 * 		
 * 		set_expr(statement;  reference, muta_expr): reference being defined;	<br>
 * 		inc_expr(statement;  reference, different): incremental differential;	<br>
 * 		xor_expr(expression; reference, different): bit-related differential;	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public enum UniAbstractClass {
	cov_time,
	eva_cond,
	sed_muta,
	
	set_stmt,
	set_flow,
	trp_stmt,
	
	set_expr,
	inc_expr,
	xor_expr,
}
