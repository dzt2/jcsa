package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

/**
 * The type of propagation flow in CirMutationTree. 
 * 
 * @author yukimula
 *
 */
public enum CirMutationFlowType {
	
	/* inner-statement propagation */
	
	/** from operand to its parent as expression **/		operand_parent,
	
	/** from right-value to state of left-reference **/		rvalue_lstate,
	
	/** from left-value to state of left-reference **/		lvalue_lstate,
	
	/** from argument to the final return value **/			argument_retr,
	
	/* between-statement propagation */
	
	/** from state error to the value error at usage **/	state_usage,
	
	/** from error at condition to statement controlled **/	condition_stmt,
	
}
