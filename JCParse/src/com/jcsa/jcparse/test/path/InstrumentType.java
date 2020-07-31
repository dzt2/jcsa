package com.jcsa.jcparse.test.path;

/**
 * The type of each instrumental element in form of AstNode, fetched from the
 * instrumental file (xxx.ins), including:<br>
 * 	(1) prev_stmt: it records the time-point before a statement is executed. <br>
 * 	(2) post_stmt: it records the time-point that a statement was completed. <br>
 * 	(3) eval_expr: it records the result of the expression in form of bytes. <br>
 * 
 * @author yukimula
 *
 */
public enum InstrumentType {
	
	/** the time-point before a statement was executed **/	prev_stmt,
	
	/** the time-point after the statement was executed **/	post_stmt,
	
	/** the value evaluated from the expression in test **/	eval_expr,
	
}
