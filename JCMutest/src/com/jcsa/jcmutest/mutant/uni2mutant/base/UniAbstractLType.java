package com.jcsa.jcmutest.mutant.uni2mutant.base;

/**
 * 	It describes the type of UniAbstractStore.
 * 	
 * 	@author yukimula
 *
 */
public enum UniAbstractLType {
	
	/* elementals */
	/** CirAgumentList|AstArgumentList	**/	argls_elm,
	/** CirType|AstTypeName 			**/	ctype_elm,
	/** CirField|AstField 				**/	field_elm,
	/** CirLabel|AstLabel 				**/	label_elm,
	
	/* expression */
	/** CirExpression (bool|IF.cond) 	**/	bool_expr,
	/** CirReferExpression (assg.lop) 	**/	cdef_expr,
	/** CirExpression (otherwise) 		**/	used_expr,
	/** CirExpression (specified def) 	**/	vdef_expr,
	
	/* statements */
	/** CirAssignStatement 				**/	assg_stmt,
	/** CirIfStatement					**/	ifte_stmt,
	/** CirCaseStatement				**/	case_stmt,
	/** CirCallStatement 				**/	call_stmt,
	
	/* goto-label */
	/** CirGotoStatement 				**/	goto_stmt,
	/** CirBegStatement|CirEndStatement **/	bend_stmt,
	/** CirIfEndStatement|CirCaseEnd 	**/	conv_stmt,
	/** CirLabelStatement 				**/	labl_stmt,
	
}
