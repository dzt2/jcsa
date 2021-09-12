package com.jcsa.jcmutest.mutant.cir2mutant.cond;

/**
 * The category of the store unit to be annotated with any value.
 * 
 * @author yukimula
 *
 */
public enum CirStoreClass {
	/** it denotes the implicit variable to maintain path constraint **/	cond,
	/** it denotes the register preserving the computational results **/	expr,
	/** it denotes the variable to preserve the values for reference **/	vars,
	/** it represents the reference to statements be executed or not **/	stmt,
}
