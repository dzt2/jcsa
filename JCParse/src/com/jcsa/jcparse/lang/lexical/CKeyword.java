package com.jcsa.jcparse.lang.lexical;

/**
 * Keyword defined in C standard <br>
 * Note: this is based on GNU-C manual
 *
 * @author yukimula
 */
public enum CKeyword {
	/** auto **/
	c89_auto,
	/** break **/
	c89_break,
	/** case **/
	c89_case,
	/** char **/
	c89_char,
	/** const, __const **/
	c89_const,
	/** continue **/
	c89_continue,
	/** default **/
	c89_default,
	/** do **/
	c89_do,
	/** double **/
	c89_double,
	/** else **/
	c89_else,
	/** enum **/
	c89_enum,
	/** extern **/
	c89_extern,
	/** float **/
	c89_float,
	/** for **/
	c89_for,
	/** goto **/
	c89_goto,
	/** if **/
	c89_if,
	/** int **/
	c89_int,
	/** long **/
	c89_long,
	/** register **/
	c89_register,
	/** return **/
	c89_return,
	/** short **/
	c89_short,
	/** signed, __signed, __signed__ **/
	c89_signed,
	/** sizeof **/
	c89_sizeof,
	/** static **/
	c89_static,
	/** struct **/
	c89_struct,
	/** switch **/
	c89_switch,
	/** typeof **/
	c89_typedef,
	/** union **/
	c89_union,
	/** unsigned **/
	c89_unsigned,
	/** void **/
	c89_void,
	/** volatile **/
	c89_volatile,
	/** while **/
	c89_while,

	/** inline, __inline, __inline__ **/
	c99_inline,
	/** restrict, __restrict, __restrict__ **/
	c99_restrict,
	/** _Bool **/
	c99_bool,
	/** _Complex, __complex, __complex__ **/
	c99_complex,
	/** _Imaginary, __imag, __imag__ **/
	c99_imaginary,

	/** __FUNCTION__ **/
	gnu_function,
	/** __PRETTY_FUNCTION__ **/
	gnu_pretty_function,
	/** __alignof, __alignof__ **/
	gnu_alignof,
	/** __asm, __asm__ **/
	gnu_asm,
	/** __attribute, __attribute__ **/
	gnu_attribute,
	/** __builtin_offsetof **/
	gnu_builtin_offsetof,
	/** __builtin_va_arg **/
	gnu_builtin_va_arg,
	/** _builtin_va_list **/
	gnu_builtin_va_list,
	/** __extension__ **/
	gnu_extension,
	/** __func__ **/
	gnu_func,
	/** __label__ **/
	gnu_label,
	/** __null **/
	gnu_null,
	/** __real, __real__ **/
	gnu_real,
	/** __typeof **/
	gnu_typeof,
	/** __thread **/
	gnu_thread,
}
