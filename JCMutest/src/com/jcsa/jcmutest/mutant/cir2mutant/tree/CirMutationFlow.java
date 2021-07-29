package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * Type of the edge in CirMutationTree's edges.
 * 
 * @author yukimula
 *
 */
public enum CirMutationFlow {
	
	/* initialization */
	/** ast_kill --> cir_kill  **/			ast_ext,
	/** cir_kill --> condition **/			cir_pre,
	/** cir_kill --> init_error **/			cir_pos,
	
	/* long dependence */
	/** cov_stmt --> condition **/			cov_dep,
	/** data_error propagate to **/			err_pas,
	
	/* local extension */
	/** condition --> con | cov **/			log_ext,
	/** data_error--> data_error **/		err_ext,
	
}
