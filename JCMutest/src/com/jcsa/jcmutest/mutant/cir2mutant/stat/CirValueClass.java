package com.jcsa.jcmutest.mutant.cir2mutant.stat;

/**
 * The category of symbolic value hold in execution state.
 * 
 * @author yukimula
 *
 */
public enum CirValueClass {
	/** boolean 	**/	bool,
	/** unsigned 	**/	usig,
	/** signed int 	**/	sign,
	/** double 		**/	real,
	/** pointer 	**/	addr,
	/** structure 	**/	auto,
}
