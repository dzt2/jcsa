package com.jcsa.jcmutest.mutant.cir2mutant.stat.anot;

/**
 * The category of value annotated with the store unit in CirAnnotation.
 * 
 * @author yukimula
 *
 */
public enum CirValueClass {
	/** boolean **/									bool,
	/** u_char, u_short, u_int, u_long, u_llong **/	usig,
	/** char, short, int, long, llong, enum **/		sign,
	/** float, double, long double **/				real,
	/** array, pointer **/							addr,
	/** structure or union or complex type **/		auto
}
