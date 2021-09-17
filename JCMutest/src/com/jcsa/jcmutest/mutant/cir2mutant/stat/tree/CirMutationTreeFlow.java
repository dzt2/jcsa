package com.jcsa.jcmutest.mutant.cir2mutant.stat.tree;

/**
 * It denotes the type of CirMutationTreeEdge linking two nodes in the tree.
 * 
 * @author yukimula
 *
 */
public enum CirMutationTreeFlow {
	/** [pre_condition] --> [pre_condition|mid_condition] 	**/	execution,
	/** [mid_condition] --> [mid_condition]					**/	infection,
	/** [mid_condition|pos_condition] --> [pos_condition] 	**/	propagate,
}
