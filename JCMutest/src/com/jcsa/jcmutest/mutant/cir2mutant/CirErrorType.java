package com.jcsa.jcmutest.mutant.cir2mutant;

/**
 * The type of state error in C-intermediate representation code.
 * 
 * @author yukimula
 *
 */
public enum CirErrorType {
	
	/** trap_on(statement) **/							trap_error,
	/** set_flow(orig_flow, muta_flow) **/				flow_error,
	/** set_expr(expression, orig_val, muta_val) **/	expr_error,
	/** set_refer(reference, orig_val, muta_val) **/	refr_error,
	stat_error,
	
}
