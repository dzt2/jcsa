package com.jcsa.jcparse.test.inst;

/**
 * The type of the flow between instrumental nodes in the execution path.
 * 
 * @author yukimula
 *
 */
public enum InstrumentalLink {
	
	/** from parent to its child **/	down_flow,
	/** from child to its parent **/	upon_flow,
	/** from node to its sliding **/	move_flow,
	/** from statement to others **/	goto_flow,
	/** from call_expr to function **/	call_flow,
	/** from function to call_expr **/	retr_flow,
	
}
