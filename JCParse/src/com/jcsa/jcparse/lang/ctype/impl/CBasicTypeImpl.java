package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.ctype.CBasicType;

public class CBasicTypeImpl implements CBasicType {

	/** void **/
	public static CBasicType void_type;
	/** _Bool **/
	public static CBasicType bool_type;
	/** char **/
	public static CBasicType char_type;
	/** unsigned char **/
	public static CBasicType uchar_type;
	/** short **/
	public static CBasicType short_type;
	/** unsigned short **/
	public static CBasicType ushort_type;
	/** int **/
	public static CBasicType int_type;
	/** unsigned (int) **/
	public static CBasicType uint_type;
	/** long (int) **/
	public static CBasicType long_type;
	/** unsigned long (int) **/
	public static CBasicType ulong_type;
	/** long long (int) **/
	public static CBasicType llong_type;
	/** unsigned long long (int) **/
	public static CBasicType ullong_type;
	/** float **/
	public static CBasicType float_type;
	/** double **/
	public static CBasicType double_type;
	/** long double **/
	public static CBasicType ldouble_type;
	/** float _Complex **/
	public static CBasicType float_complex_type;
	/** double _Complex **/
	public static CBasicType double_complex_type;
	/** long double _Complex **/
	public static CBasicType ldouble_complex_type;
	/** float _Imaginary **/
	public static CBasicType float_imaginary_type;
	/** double _Imaginary **/
	public static CBasicType double_imaginary_type;
	/** long double _Imaginary **/
	public static CBasicType ldouble_imaginary_type;
	/** (gnu) __builtin_va_list **/
	public static CBasicType gnu_va_list_type;

	/** tag for this basic type **/
	private CBasicTypeTag tag;

	/**
	 * constructor
	 * 
	 * @param tag
	 */
	protected CBasicTypeImpl(CBasicTypeTag tag) {
		this.tag = tag;
	}

	@Override
	public CBasicTypeTag get_tag() {
		return tag;
	}

	@Override
	public boolean is_defined() {
		return true;
	}

	@Override
	public String toString() {
		switch (tag) {
		case c_void:
			return "void";
		case c_bool:
			return "_Bool";
		case c_char:
			return "char";
		case c_uchar:
			return "unsigned char";
		case c_short:
			return "short";
		case c_ushort:
			return "unsigned short";
		case c_int:
			return "int";
		case c_uint:
			return "unsigned int";
		case c_long:
			return "long";
		case c_ulong:
			return "unsigned long";
		case c_llong:
			return "long long";
		case c_ullong:
			return "unsigned long long";
		case c_float:
			return "float";
		case c_double:
			return "double";
		case c_ldouble:
			return "long double";
		case c_float_complex:
			return "float _Complex";
		case c_double_complex:
			return "double _Complex";
		case c_ldouble_complex:
			return "long double _Complex";
		case c_float_imaginary:
			return "float _Imaginary";
		case c_double_imaginary:
			return "double _Imaginary";
		case c_ldouble_imaginary:
			return "long double _Imaginary";
		case gnu_va_list:
			return "__builtin_va_list";
		default:
			return tag.toString();
		}
	}

	@Override
	public boolean equals(Object val) {
		if (val instanceof CBasicType)
			return ((CBasicType) val).get_tag() == tag;
		else
			return false;
	}

	/**
	 * create singletons for each basic type
	 */
	static {
		void_type = new CBasicTypeImpl(CBasicTypeTag.c_void);
		bool_type = new CBasicTypeImpl(CBasicTypeTag.c_bool);
		char_type = new CBasicTypeImpl(CBasicTypeTag.c_char);
		uchar_type = new CBasicTypeImpl(CBasicTypeTag.c_uchar);
		short_type = new CBasicTypeImpl(CBasicTypeTag.c_short);
		ushort_type = new CBasicTypeImpl(CBasicTypeTag.c_ushort);
		int_type = new CBasicTypeImpl(CBasicTypeTag.c_int);
		uint_type = new CBasicTypeImpl(CBasicTypeTag.c_uint);
		long_type = new CBasicTypeImpl(CBasicTypeTag.c_long);
		ulong_type = new CBasicTypeImpl(CBasicTypeTag.c_ulong);
		llong_type = new CBasicTypeImpl(CBasicTypeTag.c_llong);
		ullong_type = new CBasicTypeImpl(CBasicTypeTag.c_ullong);
		float_type = new CBasicTypeImpl(CBasicTypeTag.c_float);
		double_type = new CBasicTypeImpl(CBasicTypeTag.c_double);
		ldouble_type = new CBasicTypeImpl(CBasicTypeTag.c_ldouble);
		float_complex_type = new CBasicTypeImpl(CBasicTypeTag.c_float_complex);
		double_complex_type = new CBasicTypeImpl(CBasicTypeTag.c_double_complex);
		ldouble_complex_type = new CBasicTypeImpl(CBasicTypeTag.c_ldouble_complex);
		float_imaginary_type = new CBasicTypeImpl(CBasicTypeTag.c_float_imaginary);
		double_imaginary_type = new CBasicTypeImpl(CBasicTypeTag.c_double_imaginary);
		ldouble_imaginary_type = new CBasicTypeImpl(CBasicTypeTag.c_ldouble_imaginary);
		gnu_va_list_type = new CBasicTypeImpl(CBasicTypeTag.gnu_va_list);
	}

	@Override
	public String generate_code() {
		switch(this.tag) {
		case c_void:	return "void";
		case c_bool:	return "bool";
		case c_char:	return "char";
		case c_uchar:	return "unsigned char";
		case c_int:		return "int";
		case c_uint:	return "unsigned int";
		case c_long:	return "long";
		case c_ulong: 	return "unsigned long";
		case c_llong:	return "long long";
		case c_ullong:	return "unsigned long long";
		case c_float:	return "float";
		case c_double:	return "double";
		case c_ldouble:	return "long double";
		case c_float_complex:	return "float complex";
		case c_double_complex:	return "double complex";
		case c_ldouble_complex:	return "long double complex";
		case c_float_imaginary:	return "float imaginary";
		case c_double_imaginary:	return "double imaginary";
		case c_ldouble_imaginary:	return "long double imaginary";
		case gnu_va_list:	return "va_list";
		default: return "unknown";
		}
	}
}
