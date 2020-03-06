package com.jcsa.jcparse.lang.ctype;

/**
 * Basic type in C could be:<br>
 * <b> void, char, unsigned char, short, unsigned short, int, unsigned int,
 * long, unsigned long, long long, unsigned long long, float, double, long
 * double, float _Complex, double _Complex, long double _Complex, float
 * _Imaginary, double _Imaginary, long double _Imaginary</b>
 * 
 * @author yukimula
 */
public interface CBasicType extends CType {
	/**
	 * tag to represent basic type type
	 * 
	 * @author yukimula
	 *
	 */
	public static enum CBasicTypeTag {
		/** void **/
		c_void,
		/** _Bool */
		c_bool,
		/** char **/
		c_char,
		/** unsigned char **/
		c_uchar,
		/** short **/
		c_short,
		/** unsigned short **/
		c_ushort,
		/** int **/
		c_int,
		/** unsigned (int) **/
		c_uint,
		/** long (int) **/
		c_long,
		/** unsigned long (int) **/
		c_ulong,
		/** long long (int) **/
		c_llong,
		/** unsigned long long (int) **/
		c_ullong,
		/** float **/
		c_float,
		/** double **/
		c_double,
		/** long double **/
		c_ldouble,
		/** float _Complex **/
		c_float_complex,
		/** double _Complex **/
		c_double_complex,
		/** long double _Complex **/
		c_ldouble_complex,
		/** float _Imaginary **/
		c_float_imaginary,
		/** double _Imaginary **/
		c_double_imaginary,
		/** long double _Imaginary **/
		c_ldouble_imaginary,
		/** __builtin_va_list **/
		gnu_va_list,
	}

	/**
	 * get the tag for this basic type
	 * 
	 * @return
	 */
	public CBasicTypeTag get_tag();
}
