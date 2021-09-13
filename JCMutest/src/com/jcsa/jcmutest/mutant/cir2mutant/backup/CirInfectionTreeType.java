package com.jcsa.jcmutest.mutant.cir2mutant.backup;

/**
 * node type of CirInfectionTreeNode
 * 
 * @author yukimula
 *
 */
public enum CirInfectionTreeType {
	
	/** in the step of reaching statement 	**/	pre_condition,
	
	/** in the step of the state infection  **/	mid_condition,
	
	/** in the step of errors propagation 	**/	nex_condition,
	
}
