package com.jcsa.jcmutest.mutant.cir2mutant.cond;

/**
 * The category of value annotated in store unit.
 * 
 * @author yukimula
 *
 */
public enum CirValueClass {
	
	/* cond class */
	/** cond:statement:cov_stmt:execution_time 	**/	cov_stmt,
	/** cond:statement:eva_expr:symb_condition 	**/	eva_expr,
	
	/* stmt class */
	/** stmt:if.source:ori_flow:original_target **/	ori_flow,
	/** stmt:if.source:mut_flow:mutation_target **/	mut_flow,
	/** stmt:statement:ori_stmt:boolean 		**/	ori_stmt,
	/** stmt:statement:mut_stmt:boolean 		**/	mut_stmt,
	/** stmt:statement:trp_stmt:expt_value 		**/	trp_stmt,
	
	/* expr class */
	/** expr:expression:ori_expr:original_value **/	ori_expr,
	/** expr:expression:mut_expr:mutation_value **/	mut_expr,
	/** vars:references:ori_vars:original_value **/	ori_vars,
	/** vars:references:mut_vars:mutation_value **/	mut_vars,
	
	/* diff class */
	/** [stmt|expr|vars]:cmp_diff:not_equals 	**/	cmp_diff,
	/** [expr|vars](num):ext_diff:subtraction 	**/	ext_diff,
	/** [expr|vars](num):sub_diff:subtraction	**/	sub_diff,
	/** [expr|vars](num):xor_diff:exclude_ors	**/	xor_diff,
	
}
