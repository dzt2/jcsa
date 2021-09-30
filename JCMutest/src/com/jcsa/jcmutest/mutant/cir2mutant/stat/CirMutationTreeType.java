package com.jcsa.jcmutest.mutant.cir2mutant.stat;

/**
 * The type of CirMutationTreeNode to denote at which step a mutant is killed.
 * 
 * @author yukimula
 *
 */
public enum CirMutationTreeType {
	/** before and until reaching the faulty statement **/	pre_condition,
	/** includes infection condition and initial error **/	mid_condition,
	/** after the initial errors have been introduced  **/	nex_condition,
}
