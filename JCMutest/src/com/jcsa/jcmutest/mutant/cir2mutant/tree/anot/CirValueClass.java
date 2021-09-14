package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

/**
 * It specifies the type of value that is annotated with some store unit in the
 * particular location within the C-intermediate representative code.
 * 
 * @author yukimula
 *
 */
public enum CirValueClass {
	
	/** [cond:statement] --> (cov_stmt:integer) 	**/	cov_stmt,
	/** [cond:statement] --> (eva_expr:condition) 	**/	eva_expr,
	
	/** [stmt:statement] --> (mut_stmt:boolean) 	**/	mut_stmt,
	/** [expr:expression]--> (mut_expr:muta_value) 	**/	mut_expr,
	/** [stmt:statement] --> (trp_stmt:expt_value) 	**/	trp_stmt,
	
	/** [expr:expression]--> (sub_diff:muta - orig) **/	sub_diff,
	/** [expr:expression]--> (ext_diff:amut - aori) **/	ext_diff,
	/** [expr:expression]--> (xor_diff:muta ^ orig) **/	xor_diff,
	
}
