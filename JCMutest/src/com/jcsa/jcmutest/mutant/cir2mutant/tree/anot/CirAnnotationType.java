package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

/**
 * It denotes the type of value that is annotated on a specified store unit in 
 * the annotation model.
 * 
 * @author yukimula
 *
 */
public enum CirAnnotationType {
	
	/* value annotated on logical formula of the path constraint */
	/** [cond:statement]	-->	(cov_stmt:integer) 				**/	cov_stmt,
	/** [cond:statement]	-->	(eva_expr:condition) 			**/	eva_expr,
	
	/* value annotated with state-related data error for program */
	/** [stmt:statement]	-->	(trp_stmt:expt_value) 			**/	trp_stmt,
	/** [stmt:statement]	-->	(mut_stmt:boolean)				**/	mut_stmt,
	/** [expr|refr:xxxx]	-->	(mut_expr:mutation_value) 		**/	mut_expr,
	
	/* value annotated with difference-based features in testing */
	/** [stmt|expr|refr]	-->	(cmp_diff:muta != orig) 		**/	cmp_diff,
	/** [expr|refr](num,adr)-->	(sub_diff:muta - orig) 			**/	sub_diff,
	/** [expr|refr](num)	-->	(ext_diff:abs(muta) - abs(orig))**/	ext_diff,
	/** [expr|refr](int)	-->	(xor_diff:muta ^ orig) 			**/	xor_diff,
	
}
