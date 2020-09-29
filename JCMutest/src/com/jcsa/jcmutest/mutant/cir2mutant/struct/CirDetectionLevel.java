package com.jcsa.jcmutest.mutant.cir2mutant.struct;

/**
 * The level of detection for killing a mutation in testing.
 * 
 * @author yukimula
 *
 */
public enum CirDetectionLevel {
	
	/** the statement of mutation is not executed **/	not_executed,
	
	/** the statement is reached but constraint is
	 * 	not satisfied yet **/							not_satisfied,
	
	/** the constraint for infecting the mutation
	 *  is satisfied while the state error is not
	 *  infected after that. **/						non_influence,
	
	/** both the constraint and the state error are
	 *  satisfied and the mutation is valid **/			pass_mutation,
	
}
