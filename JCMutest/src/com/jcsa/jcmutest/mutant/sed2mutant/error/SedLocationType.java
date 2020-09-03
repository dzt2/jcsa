package com.jcsa.jcmutest.mutant.sed2mutant.error;

/**
 * The type of the SedNode where the error occurs, either expression's data
 * type or the statement.
 * 
 * @author yukimula
 *
 */
public enum SedLocationType {
	
	/** SedLabel **/				cstmt,
	
	/** void|null|func **/			cvoid,
	
	/** bool **/					cbool,
	
	/** char|uchar **/				cchar,
	
	/** short|int|long **/			csign,
	
	/** ushort|uint|ulong **/		usign,
	
	/** float|double|ldouble **/	creal,
	
	/** array|pointer **/			caddr,
	
	/** struct|union **/			clist,
	
	/** function **/				cfunc,
}
