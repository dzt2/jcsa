package com.jcsa.jcparse.test.state;

/**
 * The classifier of data type for expressions in C program.
 *
 * @author yukimula
 *
 */
public enum CValueType {

	/** void **/						cvoid,

	/** bool or used as boolean **/		cbool,

	/** char | uchar **/				cchar,

	/** short|int|long|enum **/			csign,

	/** unsigned short|int|long **/		usign,

	/** float|double|ldouble **/		creal,

	/** pointer|function **/			caddr,

	/** array|string **/				clist,

	/** struct|union **/				cbody,

}
