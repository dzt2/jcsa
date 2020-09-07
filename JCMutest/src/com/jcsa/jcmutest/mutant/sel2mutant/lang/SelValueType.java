package com.jcsa.jcmutest.mutant.sel2mutant.lang;

/**
 * The abstract type of data value of expressions in C programs.
 * @author yukimula
 *
 */
public enum SelValueType {
	/** null or void **/				cvoid,
	/** boolean **/						cbool,
	/** char, uchar **/					cchar,
	/** short, int, long, enum **/		csign,
	/** ushort, uint, ulong **/			usign, 
	/** float, double **/				creal,
	/** array, pointer, func **/		caddr,
	/** struct, union, complex **/		cbody
}
