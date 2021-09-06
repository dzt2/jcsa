package com.jcsa.jcmutest.mutant.cir2mutant.__backup__;

/**
 * The refined type of CirAnnotation.
 * 
 * @author yukimula
 *
 */
public enum CirAnnotationType {
	
	/* constraint class */
	/** constraint:cov_stmt:execution:statement:integer 	**/	cov_stmt,
	/** constraint:eva_expr:execution:statement:condition 	**/	eva_expr,
	
	/* stmt_error class */
	/** stmt_error:mut_stmt:execution:statement:boolean 	**/	mut_stmt,
	/** stmt_error:mut_flow:source:orig_target:muta_target 	**/	mut_flow,
	/** stmt_error:mut_stat:execution:reference:muta_value 	**/	mut_stat,
	/** stmt_error:trp_stmt:execution:statement:null 		**/	trp_stmt,
	
	/* expr_error class */
	/** expr_error:set_expr:execution:expression:mut_expr 	**/	set_expr,
	/** expr_error:set_expr:execution:expression:sub_expr 	**/	sub_expr,
	/** expr_error:set_expr:execution:expression:xor_expr 	**/	xor_expr,
	/** expr_error:ext_expr:execution:expression:ext_expr 	**/	ext_expr,
	
	/* conc_error class */
	/** conc_error:set_conc:execution:expression:mut_value 	**/	set_conc,
	/** conc_error:sub_conc:execution:expression:sub_value 	**/	sub_conc,
	/** conc_error:xor_conc:execution:expression:xor_value 	**/	xor_conc,
	/** conc_error:ext_conc:execution:expression:ext_value 	**/	ext_conc,
	
	/* scop_error class */
	/** scop_error:set_scop:execution:expression:mut_scope 	**/	set_scop,
	/** scop_error:sub_scop:execution:expression:sub_scope 	**/	sub_scop,
	/** scop_error:xor_scop:execution:expression:xor_scope 	**/	xor_scop,
	/** scop_error:ext_scop:execution:expression:ext_scope 	**/	ext_scop,
	
}
