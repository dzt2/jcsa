package com.jcsa.jcparse.lang.symbol;

/**
 * 	It defines the category of SymbolNode (symbolic node) describing expressions
 * 	being evaluated at p-state.
 * 	
 * 	@author yukimula
 *	
 */
public enum SymbolClass {
	/* element */
	type_name,
	argument_list,
	field_name,
	expr_operator,
	/* base_expr */
	identifier,
	constant,
	string_literal,
	/* composite */
	binary_expression,
	unary_expression,
	/* special */
	cast_expression,
	call_expression,
	field_expression,
	initializer_list,
	cond_expression,
}