package com.jcsa.jcparse.test.path;

/**
 * Type of the node in execution path based on AST-node sequence.
 * 
 * @author yukimula
 *
 */
public enum AstExecutionUnitType {
	
	/** beg_func [definition] **/			beg_func,
	/** end_func [definition] **/			end_func,
	/** beg_stmt [statement] **/			beg_stmt,
	/** end_stmt [statement] **/			end_stmt,
	/** execute [statement] **/				execute,
	/** beg_expr [expression] **/			beg_expr,
	/** end_expr [expression] **/			end_expr,
	/** evaluate [expression] **/			evaluate,
	/** declare [declarator] **/			declare,
	
}
