package com.jcsa.jcparse.lang.program.types;

/**
 * 	The type of AstCirLink which preserves connection from AstNode to CirNode.	<br>
 * 
 * 	@author yukimula
 *
 */
public enum AstCirLinkType {
	
	used_expr,
	assg_stmt,
	ifte_stmt,
	call_stmt,
	loct_stmt,
	skip_stmt,
	labl_stmt,
	func_defs,
	tran_unit,
	
}
