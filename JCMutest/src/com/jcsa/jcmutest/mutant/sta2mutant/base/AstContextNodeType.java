package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * 	The type of the node in AstContextTree (AstContextNode).
 * 	
 * 	@author yukimula
 *
 */
public enum AstContextNodeType {
	
	declaration,
	name,
	operator,
	keyword,
	typename,
	field,
	function,
	transition,
	
	base_statement,
	comp_statement,
	skip_statement,
	retr_statement,
	labl_statement,
	ifte_statement,
	case_statement,
	swit_statement,
	loop_statement,
	
	refr_expression,
	cons_expression,
	strl_expression,
	unry_expression,
	bnry_expression,
	cast_expression,
	incr_expression,
	assg_expression,
	call_expression,
	ifte_expression,
	seqs_expression,
	init_expression,
}
