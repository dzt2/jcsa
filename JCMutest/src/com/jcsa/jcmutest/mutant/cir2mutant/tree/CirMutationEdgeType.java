package com.jcsa.jcmutest.mutant.cir2mutant.tree;

public enum CirMutationEdgeType {
	
	/** exec_node|error_node --> exec_node **/	path_flow,
	
	/** exec_node --> error_node **/			gena_flow,
	
	/** child --> parent as expression **/		oprd_flow,
	
	/** argument --> parameter flow **/			args_flow,
	
	/** condition --> flow error **/			cond_flow,
	
	/** return value --> wait_expr error **/	retr_flow,
	
	/** error_node --> failure|survive **/		term_flow,
}
