package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * The category of values (interpretation) of the state annotated with some
 * store unit in C program point in the mutation testing.
 * 
 * @author yukimula
 *
 */
public enum CirValueClass {
	
	/**	cov_stmt(INT_TIMES)		==>	[stmt]				**/	cov_stmt,
	/**	eva_cond(CONDITION)		==>	[stmt]				**/	eva_cond,
	
	/**	set_stmt(BOOL, BOOL)	==>	[stmt]				**/	set_stmt,
	/**	set_trap(EXEC, EXPT)	==>	[stmt]				**/	set_trap,
	/**	set_flow(EXEC, EXEC)	==>	[stmt]				**/	set_flow,
	
	/**	set_expr(ORIG, MUTA)	==>	[usep|defp|vdef]	**/	set_expr,
	/**	inc_expr(ORIG, DIFF)	==>	[usep|defp|vdef]	**/ inc_expr,
	/**	xor_expr(ORIG, DIFF)	==>	[usep|defp|vdef]	**/	xor_expr,
	
}
