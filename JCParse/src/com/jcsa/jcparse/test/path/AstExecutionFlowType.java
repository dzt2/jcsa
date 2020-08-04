package com.jcsa.jcparse.test.path;

/**
 * Type of the flow in execution path described based on AST-node.
 * 
 * @author yukimula
 *
 */
public enum AstExecutionFlowType {
	
	/** from the parent to one of its child **/			down_flow,
	/** from the child node to its parent **/			upon_flow,
	/** from argument_list to function_definition **/	call_flow,
	/** from function_definition to fun_call_expr **/	retr_flow,
	/** from child to another node in same parent **/	move_flow,
	/** from a node to another node in a function **/	goto_flow,
	
}
