package com.jcsa.jcmutest.mutant.uni2mutant.base;

/**
 * 	It describes the type of UniAbstractStore.
 * 	
 * 	@author yukimula
 *
 */
public enum UniAbstractLType {
	
	/* elementals */
	/** CirField **/								fiel_elem,
	/** CirLabel **/								labl_elem,
	/** CirArgumentList **/							args_elem,
	/** CirType **/									type_elem,
	
	/* expression */
	/** CirExpression (if|case.condition) **/		bool_expr,
	/** CirReferExpression (assignment.lvalue) **/	cdef_expr,
	/** CirExpression (CirArgumentList.arg[k]) **/	argv_expr,
	/** CirExpression (otherwise) **/				used_expr,
	
	/* statements */
	/** CirAssignStatement **/						assg_stmt,
	/** CirIfStatement **/							ifte_stmt,
	/** CirCaseStatement (case) **/					case_stmt,
	/** CirCallStatement **/						call_stmt,
	
	/* goto-label */
	/** CirBegStatement|CirEndStatement **/			bend_stmt,
	/** CirGotoStatement **/						goto_stmt,
	/** CirLabelStatement|CirDefaultStatement **/	labl_stmt,
	/** CirIfEndStatement|CirCaseEndStatement **/	conv_stmt,
	
	/* virtualize */
	/** virtually definition point of non-def **/	vdef_expr,
	
}
