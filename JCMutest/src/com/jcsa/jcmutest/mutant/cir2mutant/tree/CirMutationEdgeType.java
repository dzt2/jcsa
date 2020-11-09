package com.jcsa.jcmutest.mutant.cir2mutant.tree;

public enum CirMutationEdgeType {
	
	/** exec_node|error_node --> exec_node **/	path_flow,
	
	/** exec_node --> error_node **/			gena_flow,
	
	/** error_node --> error_node **/			gate_flow,
	
	/** error_node --> failure|survive **/		term_flow,
	
	/** error_node --> exec_node **/			actv_flow,
	
}
