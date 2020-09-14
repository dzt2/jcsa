package com.jcsa.jcmutest.mutant.sec2mutant.util;

public enum SecStateEdgeType {
	/** exec(source) --> exec(target) **/			lead_to,
	/** exec(stmt) --(constraint)--> init_error **/	infect,
	/** operand --(constraint)--> expression **/	op_expr,
	/** argument --(constraint)--> call-expr **/	ag_call,
	/** reference --(path-const)--> expression **/	def_use,
	/** condition --(constraint)--> stmt_error **/	control,
	/** rvalue_error ----> lvalue_error **/			assign,
}
