package com.jcsa.jcmutest.mutant.cir2mutant.__backup__;

/**
 * The abstract class of CirAnnotation.
 * 
 * @author yukimula
 *
 */
public enum CirAnnotationClass {
	
	/* symbolic annotations to describe CirAttribute directly */
	/** cov_stmt, eva_expr 						**/	constraint,
	/** mut_stmt, mut_flow, mut_stat, trp_stmt 	**/	stmt_error,
	/** set_expr, sub_expr, xor_expr, ext_expr 	**/	expr_error,
	
	/* concrete annotations to define the state differences */
	/** set_type, sub_type, xor_type, ext_type 	**/	conc_error,
	
	/* abstract annotations to define the domain of values */
	/** set_scop, sub_scop, xor_scop, ext_scop 	**/	scop_error,
	
}
