package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * The category of abstract execution state in mutation testing.
 * 
 * @author yukimula
 *
 */
public enum CirStateClass {
	
	/*	CirConditionState	*/
	/**	cov_stmt(execution, [stmt:statement], 	{TRUE});		**/	cov_stmt,
	/**	cov_time(execution,	[stmt:statement],	{int_times});	**/	cov_time,
	/**	eva_cond(execution, [cond|vcon:xxxx],	{CONDITION});	**/	eva_cond,
	
	/*	CirPathErrorState	*/
	/**	mut_stmt(execution,	[stmt:statement],	{BOOL, BOOL});	**/	mut_stmt,
	/**	mut_flow(execution,	[stmt:statement],	{EXEC, EXEC});	**/	mut_flow,
	/**	trp_stmt(execution,	[stmt:statement],	{EXCEPTION});	**/	trp_stmt,
	
	/*	CirDataErrorState	*/
	/**	dif_usep(execution,	[cond|usep:xxxx],	{ORIG, MUTA});	**/	dif_usep,
	/**	mut_usep(execution, [cond|usep:xxxx],	{ORIG, MUTA});	**/	mut_usep,
	/**	mut_defp(execution, [defp|vdef:xxxx],	{ORIG, MUTA});	**/	mut_defp,
	
	/*	CirDifferentState	*/
	/**	inc_expr(execution,	[usep|defp|vdef],	{DIFFERENCE});	**/	inc_expr,
	/**	xor_expr(execution,	[usep|defp|vdef],	{DIFFERENCE});	**/	xor_expr,
	/**	ext_expr(execution,	[usep|defp|vdef],	{DIFFERENCE});	**/	ext_expr,
	
}
