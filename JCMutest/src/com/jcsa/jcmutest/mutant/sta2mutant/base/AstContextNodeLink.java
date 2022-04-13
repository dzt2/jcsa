package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * 	It specifies the type of link from parent to child as AstContextNode.
 * 	
 * 	@author yukimula
 *
 */
public enum AstContextNodeLink {
	
	/** decl_stmt --> declaration **/	declare,
	/** expr_stmt --> expression **/	evaluate,
	/** com_stmt --> child **/			execute,
	/** tran_unit --> function **/		function,
	
	/** assign_node --> lvalue **/		lvalue,
	/** assign_node --> rvalue **/		rvalue,
	/** incre_expr --> operand **/		ivalue,
	uoperand,
	loperand,
	roperand,
	operator,
	reference,
	address,
	cast_type,
	array,
	index,
	toperand,
	foperand,
	element,
	fbody,
	field,
	callee,
	argument,
	
	/** if|for|while|do --> expr **/	condition,
	/** if|for|while|do --> body **/	true_body,
	/** if --> else.body **/			false_body,
	/** switch|function --> body **/	body,
	
	/** case|switch --> expression **/	n_condition,
	/** for --> initializer **/			initial,
	/** for --> iteration **/			iterate,
	
}
