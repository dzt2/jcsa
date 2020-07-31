package com.jcsa.jcparse.test.path;

/**
 * 	It defines the type of node in execution path described based on
 * 	abstract syntactic tree node.
 * 	
 * 	@author yukimula
 *
 */
public enum AstExecutionType {
	
	beg_func,
	beg_stmt,
	beg_expr,
	end_expr,	/* it records the value hold by the expression */
	end_stmt,
	end_func,
	
}
