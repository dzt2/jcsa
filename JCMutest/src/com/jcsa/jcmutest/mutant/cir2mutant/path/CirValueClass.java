package com.jcsa.jcmutest.mutant.cir2mutant.path;

/**
 * The category of value type used in C-intermediate representative program.
 * 
 * @author yukimula
 *
 */
public enum CirValueClass {
	/** void 						**/	none,
	/** boolean 					**/	bool,
	/** unsigned (char|int|long) 	**/	usig,
	/** signed (char|int|long|enum)	**/	sign,
	/** float, double, ldouble 		**/	real,
	/** array, pointer 				**/	addr,
	/** struct, union 				**/	auto,
}
