package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * The abstract category of CirAnnotation.
 * 
 * @author yukimula
 *
 */
public enum CirAnnotationClass {
	
	/** cov_stmt; eva_expr; 					**/		constraint,
	
	/** mut_stmt; mut_flow; mut_stat; trp_stmt; **/		stmt_error,
	
	/** set_expr; dif_expr; xor_expr; ext_stmt; **/		expr_error,
	
	/** set_bool; set_numb; set_real; set_addr; **/		conc_error,
	
	/** set_scop; dif_scop; ext_scop; xor_scop; **/		scop_error,
	
}
