package com.jcsa.jcparse.lang.symb;

/**
 * 	It specifies the class of SymbolNode.
 * 	
 * 	@author yukimula
 *
 */
public enum SymbolClass {
	/* element */
	type_name,
	field_name,
	operator,
	argument_list,
	
	/* base_expr */
	identifier,
	constant,
	literal,
	
	/* composite */
	unary_expression,
	arith_expression,
	bitws_expression,
	logic_expression,
	relation_expression,
	assign_expression,
	
	/* special_expr */
	cast_expression,
	call_expression,
	initializer_list,
	field_expression,
	ifte_expression,
	expression_list,
	
}
