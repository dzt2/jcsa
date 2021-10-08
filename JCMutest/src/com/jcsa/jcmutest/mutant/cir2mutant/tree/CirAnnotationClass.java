package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * The predicate class of CirAnnotation to describe its semantics.
 * 
 * @author yukimula
 *
 */
public enum CirAnnotationClass {
	
	/** cov_stmt(statement, execution_times) 	**/	cov_stmt,
	/** eva_expr(statement, condition as true) 	**/	eva_expr,
	/** mut_flow(orig_target, muta_target) 		**/	mut_flow,
	/** trp_stmt(program_exit, expt_value) 		**/	trp_stmt,
	
	/** mut_stmt(statement, boolean) 			**/	mut_stmt,
	/** mut_expr(orig_expression, muta_value) 	**/	mut_expr,
	/** mut_stat(orig_reference, muta_value) 	**/	mut_stat,
	
	/** dif_asub(orig_expr, fabs(m) - fabs(o)) 	**/	dif_asub,
	/** dif_rsub(orig_expr, real(m) - real(o)) 	**/	dif_rsub,
	/** dif_exor(orig_expr, long(m) ^ long(o) 	**/	dif_exor,
	
}
