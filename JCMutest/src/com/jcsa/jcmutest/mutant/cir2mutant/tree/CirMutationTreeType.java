package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * The type of each node in cir-mutation tree.
 * @author dzt2
 *
 */
public enum CirMutationTreeType {
	/** before reaching infection **/	precondition,
	/** state-infection-condition **/	midcondition,
	/** after infection is caused **/	poscondition,
}
