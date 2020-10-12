package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

/**
 * The level of detecting a cir-mutation
 * 
 * @author yukimula
 *
 */
public enum CirDetectionLevel {
	
	not_reachable,			/* {}			|--> False(statement) */
	
	not_satisfied,			/* [False, Any]	|--> False(constraint) */
	
	not_infected,			/* [Any, False]	|--> False(state_error) */
	
	infected,				/* [True, True]	|--> True(mutation?) */
	
	prev_infectable,		/* [True, Null]	|--> Unknown(state_error) */
	
	post_infectable,		/* [Null, True]	|--> Unknown(constraint) */
	
	part_infectable,		/* [True, True]	|--> Unknown(mutation) */
	
}
