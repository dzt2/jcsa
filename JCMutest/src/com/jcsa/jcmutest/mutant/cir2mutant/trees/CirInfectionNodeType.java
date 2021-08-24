package com.jcsa.jcmutest.mutant.cir2mutant.trees;

/**
 * It denotes at which step the node of state infection is located in the 
 * process of reachability-infection-propagation (RIP) framework.
 * 
 * @author yukimula
 *
 */
public enum CirInfectionNodeType {
	
	/** in the step of reaching statement 	**/	pre_condition,
	
	/** in the step of the state infection  **/	mid_condition,
	
	/** in the step of errors propagation 	**/	nex_condition,
	
}
