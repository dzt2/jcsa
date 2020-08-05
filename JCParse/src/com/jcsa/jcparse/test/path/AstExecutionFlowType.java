package com.jcsa.jcparse.test.path;

/**
 * The flow in AST-execution sequence.
 * @author yukimula
 *
 */
public enum AstExecutionFlowType {
	/** from the parent to one of its children **/	down_flow,
	/** from the node to its parent **/				upon_flow,
	/** from the node to another spliding node **/	move_flow,
	/** from the node to another node in body **/	goto_flow,
	/** from fun_call_expr to the definition **/	call_flow,
	/** from definition to the fun_call_expr **/	retr_flow,
}
