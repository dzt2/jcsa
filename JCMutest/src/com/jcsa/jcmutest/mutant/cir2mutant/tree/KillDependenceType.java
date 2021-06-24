package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * The type of killable dependence edge.
 * 
 * @author yukimula
 *
 */
public enum KillDependenceType {
	
	/** cov_stmt(execution) --> cov_stmt(statement) | eva_expr(statement.condition) **/		execution,
	
	/** mutant --> infection_condition | initial_error_observation **/						infection,
	
	/** source_error_observation --> target_error_observation {forward data flows} **/		propagate,
	
	/** extension within the local block **/												implicate,
	
}
