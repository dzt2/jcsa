package com.jcsa.jcmutest.mutant.sta2mutant.abst;

public enum AstContextType {
	
	declarator,
	init_body,
	stmt_body,
	call_expr,
	
	key_word,
	operator,
	args_list,
	type_name,
	
	assignment,
	decl_stmt,
	retr_stmt,
	ifte_stmt,
	
	skip_stmt,
	labl_stmt,
	swit_stmt,
	loop_stmt,
	
	increment,
	constant,
	literal,
	reference,
	expression,
	
	function,
	ast_root,
	
}
