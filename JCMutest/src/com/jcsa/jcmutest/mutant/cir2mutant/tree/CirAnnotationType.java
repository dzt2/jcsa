package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * The refined type of CirAnnotation under the categorization.
 * 
 * @author yukimula
 *
 */
public enum CirAnnotationType {
	
	/* constraint class: to describe symbolic constraint for being satisfied */
	/** constraint:cov_stmt:execution:statement:integer 		**/	cov_stmt,
	/** constraint:eva_expr:execution:statement:condition 		**/	eva_expr,
	
	/* stmt_error class: to describe the statement-related error in analysis */
	/** stmt_error:mut_stmt:execution:statement:boolean 		**/	mut_stmt,
	/** stmt_error:mut_flow:if_source:orig_target:muta_target 	**/	mut_flow,
	/** stmt_error:mut_stat:execution:reference:muta_expression **/	mut_stat,
	/** stmt_error:trp_stmt:execution:statement:null 			**/	trp_stmt,
	
	/* expr_error class: to describe the data state error in symbolic forms */
	/** expr_error:set_expr:execution:orig_expr:muta_expr 		**/	set_expr,
	/** expr_error:dif_expr:execution:orig_expr:sub_difference 	**/	dif_expr,
	/** expr_error:ext_expr:execution:orig_expr:ext_difference 	**/	ext_expr,
	/** expr_error:xor_expr:execution:orig_expr:xor_difference 	**/	xor_expr,
	
	/* conc_error class: to describe the data state error in concrete value */
	/** conc_error:set_bool:execution:orig_expr:boolean 		**/	set_bool,
	/** conc_error:set_numb:execution:orig_expr:integer 		**/	set_numb,
	/** conc_error:set_real:execution:orig_expr:double 			**/	set_real,
	/** conc_error:set_addr:execution:orig_expr:integer 		**/	set_addr,
	/** conc_error:set_auto:execution:orig_expr:constant 		**/	set_auto,
	/** conc_error:dif_numb:execution:orig_expr:integer 		**/	dif_numb,
	/** conc_error:dif_real:execution:orig_expr:double 			**/	dif_real,
	/** conc_error:dif_addr:execution:orig_expr:integer 		**/	dif_addr,
	/** conc_error:ext_numb:execution:orig_expr:integer 		**/	ext_numb,
	/** conc_error:ext_real:execution:orig_expr:double 			**/	ext_real,
	/** conc_error:xor_numb:execution:orig_expr:integer 		**/	xor_numb,
	
	/* scop_error class: to describe the data state error in abstract domain */
	/** scop_error:set_scop:execution:orig_expr:values_scope 	**/	set_scop,
	/** scop_error:dif_scop:execution:orig_expr:differ_scope 	**/	dif_scop,
	/** scop_error:ext_scop:execution:orig_expr:extend_scope 	**/	ext_scop,
	/** scop_error:ext_scop:execution:orig_expr:ext_or_scope 	**/	xor_scop,
	
}
