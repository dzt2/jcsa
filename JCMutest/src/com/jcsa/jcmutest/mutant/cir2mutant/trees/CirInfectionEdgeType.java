package com.jcsa.jcmutest.mutant.cir2mutant.trees;

/**
 * It denotes the type of edge between CirInfectionNode(s) and annotated on a
 * unique CirInfectionEdge, describing the relationship between nodes in tree.
 * 
 * @author yukimula
 *
 */
public enum CirInfectionEdgeType {
	
	/** pre_condition --> [pre_condition|mid_condition] **/	execution,
	
	/** mid_condition --> mid_condition 				**/	infection,
	
	/** [mid_condition|nex_condtiion] --> nex_condition **/	propagate,
	
}
