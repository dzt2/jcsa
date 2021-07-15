package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * Type of the edge in CirMutationTree's edges.
 * 
 * @author yukimula
 *
 */
public enum CirMutationFlow {
	
	/** local subsumption or implication relation **/		subsume,
	
	/** control dependence: cov_stmt --> eva_expr **/		control,
	
	/** error propagation edge: errors --> errors **/		pass_on,
	
	/** ast --> cir  **/									ast_cir,
	
}
