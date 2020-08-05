package com.jcsa.jcparse.test.path;

/**
 * The type of the execution unit in form of AST-node.
 * 
 * @author yukimula
 *
 */
public enum AstExecutionType {
	
	/** beg_func [definition] **/		beg_func,
	/** beg_stmt [statement] **/		beg_stmt,
	/** beg_expr [expression] **/		beg_expr,
	/** execute	 [leaf_statement] **/	execute,
	/** evaluate [leaf_expression] **/	evaluate,
	/** end_expr [expression] **/		end_expr,
	/** end_stmt [statement] **/		end_stmt,
	/** end_func [definition] **/		end_func,
	/** declare  [declarator] **/		declare,
	
}
