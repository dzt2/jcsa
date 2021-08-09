package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * The refined second-order class of the CirAnnotation
 * 
 * @author yukimula
 *
 */
public enum CirAnnotationType {

	/* constraint class */
	/** constraint:cov_stmt:execution:statement:integer 	**/		cov_stmt,
	/** constraint:eva_expr:execution:statement:condition 	**/		eva_expr,
	
	/* stmt_error class */
	/** stmt_error:mut_flow:source:orig_target:muta_target 	**/		mut_flow,
	/** stmt_error:mut_stmt:execution:statement:boolean 	**/		mut_stmt,
	/** stmt_error:trp_stmt:execution:statement:null 		**/		trp_stmt,
	
	/* symb_error class */
	/** symb_error:mut_expr:execution:expression:mutation 	**/		mut_expr,
	/** symb_error:mut_refr:execution:expression:mutation 	**/		mut_refr,
	/** symb_error:mut_stat:execution:expression:mutation 	**/		mut_stat,
	
	/* bool_error class */
	/** bool_error:set_bool:execution:expression:boolean 	**/		set_bool,
	/** bool_error:chg_bool:execution:expression:null 		**/		chg_bool,
	
	/* numb_error class */
	/** numb_error:set_numb:execution:expression:number 	**/		set_numb,
	/** numb_error:set_post:execution:expression:null 		**/		set_post,
	/** numb_error:set_npos:execution:expression:null 		**/		set_npos,
	/** numb_error:set_zero:execution:expression:null 		**/		set_zero,
	/** numb_error:set_nzro:execution:expression:null 		**/		set_nzro,
	/** numb_error:set_negt:execution:expression:null 		**/		set_negt,
	/** numb_error:set_nneg:execution:expression:null 		**/		set_nneg,
	/** numb_error:chg_numb:execution:expression:null 		**/		chg_numb,
	
	/* addr_error class */
	/** addr_error:set_addr:execution:expression:integer 	**/		set_addr,
	/** addr_error:set_null:execution:expression:null 		**/		set_null,
	/** addr_error:set_invp:execution:expression:null 		**/		set_invp,
	/** addr_error:chg_addr:execution:expression:null 		**/		chg_addr,
	
	/* auto_error class */
	/** auto_error:set_auto:execution:expression:constant 	**/		set_auto,
	/** auto_error:chg_auto:execution:expression:null 		**/		chg_auto,
	
	/* scop_error class */
	/** scop_error:inc_scop:execution:expression:null	 	**/		inc_scop,
	/** scop_error:dec_scop:execution:expression:null	 	**/		dec_scop,
	/** scop_error:ext_scop:execution:expression:null	 	**/		ext_scop,
	/** scop_error:shk_scop:execution:expression:null	 	**/		shk_scop,
	
}
