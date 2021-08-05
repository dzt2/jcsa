package com.jcsa.jcparse.lang.lexical;

/**
 * Operator in C expression
 *
 * @author yukimula
 */
public enum COperator {
	/** = **/
	assign,

	/** + **/
	arith_add,
	/** - **/
	arith_sub,
	/** * **/
	arith_mul,
	/** / **/
	arith_div,
	/** % **/
	arith_mod,

	/** += **/
	arith_add_assign,
	/** -= **/
	arith_sub_assign,
	/** *= **/
	arith_mul_assign,
	/** /= **/
	arith_div_assign,
	/** %= **/
	arith_mod_assign,

	/** ++ **/
	increment,
	/** -- **/
	decrement,

	/** + **/
	positive,
	/** - **/
	negative,
	/** & **/
	address_of,
	/** * **/
	dereference,

	/** << **/
	left_shift,
	/** >> **/
	righ_shift,
	/** <<= **/
	left_shift_assign,
	/** >>= **/
	righ_shift_assign,

	/** > **/
	greater_tn,
	/** >= **/
	greater_eq,
	/** < **/
	smaller_tn,
	/** <= **/
	smaller_eq,
	/** == **/
	equal_with,
	/** != **/
	not_equals,

	/** && **/
	logic_and,
	/** || **/
	logic_or,
	/** ! **/
	logic_not,
	/** ~ **/
	bit_not,

	/** & **/
	bit_and,
	/** | **/
	bit_or,
	/** ^ **/
	bit_xor,
	/** &= **/
	bit_and_assign,
	/** |= **/
	bit_or_assign,
	/** ^= **/
	bit_xor_assign,

}
