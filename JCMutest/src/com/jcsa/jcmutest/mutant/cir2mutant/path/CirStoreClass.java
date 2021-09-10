package com.jcsa.jcmutest.mutant.cir2mutant.path;

/**
 * It defines the category of the store unit to preserve the value in execution
 * state of some program point.
 * 
 * @author yukimula
 *
 */
public enum CirStoreClass {
	/** to preserve the logical formula representing the pred_condition	**/	cond,
	/** the register to preserve the computational result of expression **/	expr,
	/** the predictive pointer deciding whether a statement is executed **/	stmt,
	/** a memory location to preserve the values for some variable used **/	vars,
}
