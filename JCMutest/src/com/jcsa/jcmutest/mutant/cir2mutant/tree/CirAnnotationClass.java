package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * It defines the category of CirAnnotation.
 * 
 * @author yukimula
 *
 */
public enum CirAnnotationClass {
	
	/** cov_stmt; eva_expr; **/						constraint,
	
	/** mut_stmt; mut_flow; trp_stmt; mut_stat; **/	stmt_error,
	
	/** set_expr; sub_expr; ext_expr; xor_expr; **/	expr_error,
	
}
