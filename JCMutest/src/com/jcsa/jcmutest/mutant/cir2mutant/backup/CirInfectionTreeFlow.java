package com.jcsa.jcmutest.mutant.cir2mutant.backup;

/**
 * The type of CirInfectionTreeEdge 
 * 
 * @author yukimula
 *
 */
public enum CirInfectionTreeFlow {
	
	/** pre_condition --> [pre_condition|mid_condition] **/	execution,
	
	/** mid_condition --> mid_condition 				**/	infection,
	
	/** [mid_condition|nex_condtiion] --> nex_condition **/	propagate,
	
}
