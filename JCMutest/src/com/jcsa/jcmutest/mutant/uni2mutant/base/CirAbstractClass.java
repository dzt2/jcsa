package com.jcsa.jcmutest.mutant.uni2mutant.base;

/**
 * The category of CirAbstractState.
 * 
 * @author yukimula
 *
 */
public enum CirAbstractClass {
	
	/** cov_stmt(location; min_times, max_times) **/	cov_time,
	/** eva_cond(location; condition, must_need) **/	eva_cond,
	/** sed_muta(location; mutant_ID, class_opr) **/	sed_muta,
	
	/** mut_stmt(location; orig_exec, muta_exec) **/	mut_stmt,
	/** mut_flow(location; orig_next, muta_next) **/	mut_flow,
	/** trp_stmt(location; exception, exception) **/	trp_stmt,
	
	/** set_expr(location; ori_value, mut_value) **/	set_expr,
	/** inc_expr(location; bas_value, different) **/	inc_expr,
	/** xor_expr(location; bas_value, different) **/	xor_expr,
	
}
