package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * It defines the refined type of CirAnnotation.
 * 
 * @author yukimula
 *
 */
public enum CirAnnotationType {
	
	/* constraint class */
	/** constraint:cov_stmt:execution:statement:integer **/			cov_stmt,
	/** constraint:eva_expr:execution:statement:condition **/		eva_expr,
	
	/* stmt_error class */
	/** stmt_error:mut_flow:source:orig_target:muta_target **/		mut_flow,
	/** stmt_error:mut_stmt:execution:statement:boolean **/			mut_stmt,
	/** stmt_error:trp_stmt:execution:statement:null **/			trp_stmt,
	/** stmt_error:mut_stat:execution:statement:muta_value **/		mut_stat,
	
	/* expr_error class */
	/** expr_error:set_expr:execution:orig_expr:muta_value **/		set_expr,
	/** expr_error:sub_expr:execution:orig_expr:sub_difference **/	sub_expr,
	/** expr_error:ext_expr:execution:orig_expr:ext_difference **/	ext_expr,
	/** expr_error:xor_expr:execution:orig_expr:xor_difference **/	xor_expr,
	
}
