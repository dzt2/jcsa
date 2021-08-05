package com.jcsa.jcparse.lang.lexical;

/**
 * Punctuator in C language
 *
 * @author yukimula
 *
 */
public enum CPunctuator {
	/** error **/
	lex_error,

	/** [ **/
	left_bracket,
	/** ] **/
	right_bracket,
	/** ( **/
	left_paranth,
	/** ) **/
	right_paranth,
	/** { **/
	left_brace,
	/** } **/
	right_brace,

	/** . **/
	dot,
	/** -> **/
	arrow,

	/** ++ **/
	increment,
	/** -- **/
	decrement,

	/** ~ **/
	bit_not,
	/** & **/
	bit_and,
	/** | **/
	bit_or,
	/** ^ **/
	bit_xor,

	/** + **/
	ari_add,
	/** - **/
	ari_sub,
	/** * **/
	ari_mul,
	/** / **/
	ari_div,
	/** % **/
	ari_mod,

	/** && **/
	log_and,
	/** || **/
	log_or,
	/** ! **/
	log_not,

	/** << **/
	left_shift,
	/** >> **/
	right_shift,
	/** <<= **/
	left_shift_assign,
	/** >>= **/
	right_shift_assign,

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

	/** += **/
	ari_add_assign,
	/** -= **/
	ari_sub_assign,
	/** *= **/
	ari_mul_assign,
	/** /= **/
	ari_div_assign,
	/** %= **/
	ari_mod_assign,

	/** &= **/
	bit_and_assign,
	/** |= **/
	bit_or_assign,
	/** ^= **/
	bit_xor_assign,

	/** , **/
	comma,
	/** ; **/
	semicolon,
	/** : **/
	colon,
	/** ... **/
	ellipsis,
	/** ? **/
	question,
	/** = **/
	assign,

	/** # **/
	hash,
	/** ## **/
	hash_hash,
}
