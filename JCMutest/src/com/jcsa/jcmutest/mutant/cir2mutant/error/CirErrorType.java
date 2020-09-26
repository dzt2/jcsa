package com.jcsa.jcmutest.mutant.cir2mutant.error;

/**
 * The type of state error defined on C-intermediate representation.
 * 
 * @author yukimula
 *
 */
public enum CirErrorType {
	
	/** trap_error(statement): trapping and throw exceptions for **/	trap_error,
	
	/** expr_error(statement, expression, orig_value, muta_value) **/	expr_error,
	
	/** refr_error(statement, reference, muta_expression) **/			refr_error,
	
	/** flow_error(statement, original_flow, mutation_flow) **/			flow_error,
	
}
