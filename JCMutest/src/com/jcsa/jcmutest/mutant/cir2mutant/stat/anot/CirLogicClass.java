package com.jcsa.jcmutest.mutant.cir2mutant.stat.anot;

/**
 * It defines the predicate function used to describe CirAnnotation.
 * 
 * @author yukimula
 *
 */
public enum CirLogicClass {
	
	/* assertions class */
	/** cov_stmt(cond:statement; sign:exec_times) 	**/	cov_stmt,
	/** eva_expr(cond:statement, bool:conditions) 	**/	eva_expr,
	/** trp_stmt(stmt:statement, bool:expt_value) 	**/	trp_stmt,
	
	/* muta_value class */
	/** mut_stmt(stmt:statement, bool:expression) 	**/	mut_stmt,
	/** mut_expr(expr:expression type:muta_value) 	**/	mut_expr,
	/** mut_refr(refr:reference, type:muta_value) 	**/ mut_refr,
	
	/* difference class */
	/** sub_diff(expr[num|addr], type:difference) 	**/	sub_diff,
	/** ext_diff(expr|refr[num], type:difference) 	**/	ext_diff,
	/** xor_diff(expr|refr[int], type:difference) 	**/	xor_diff,
	
}
