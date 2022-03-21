package com.jcsa.jcmutest.mutant.uni2mutant.base;

/**
 * 	It describes the category of UniAbstractStore as state evaluation location.
 * 	
 * 	@author yukimula
 *
 */
public enum UniAbstractSType {
	
	/* elementals */
	/** CirFunctionDefinition **/						func_defs,
	/** CirFunctionBody **/								stmt_list,
	/** CirArgumentList **/								args_list,
	/** CirType **/										type_name,
	/** CirField **/									fiel_name,
	/** CirLabel **/									labl_name,
	
	/* statements */
	/** binary, initial, increment, save assign **/		assg_stmt,
	/** CirIfStatement | CirCaseStatement **/			cond_stmt,
	/** CirIfStatement **/								loop_stmt,
	/** CirCallStatement **/							call_stmt,
	/** CirWaitAssignStatement **/						wait_stmt,
	/** CirReturnAssignStatement|CirGotoStatement **/	retr_stmt,
	
	/* goto-label */
	/** CirGotoStatement as goto end of conjuncts **/	gend_node,
	/** CirGotoStatement as skiped transformation **/	skip_node,
	/** CirGotoStatement that returns to loop-beg **/	rlop_node,
	/** CirBegStatement | CirEndStatement **/			bend_node,
	/** CirLabelStatement | CirDefaultStatement **/		labl_node,
	/** CirIfEndStmt | CirCaseEndStmt |  **/			conv_node,
	
	/* expression */
	/** child as CirIfStatement | CirCaseStatement **/	cond_expr,
	/** child as CirArgumentList **/					args_expr,
	/** usage of operands in composite expression **/	oprd_expr,
	/** child as left-value of CirAssignStatement **/	cdef_expr,
	/** child as CirFieldExpression | CirDeferExpr **/	refr_expr,
	/** virtual point to simulate the definition **/	vdef_expr,
	
}
