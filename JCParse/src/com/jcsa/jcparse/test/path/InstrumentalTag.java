package com.jcsa.jcparse.test.path;

/**
 * The tag defined in instrumental data line.
 * 
 * @author yukimula
 *
 */
public enum InstrumentalTag {
	
	/** (call_fun, definition, null) **/	call_fun,
	/** (exit_fun, definition, null) **/	exit_fun,
	
	/** (beg_stmt, statement, null) **/		beg_stmt,
	/** (end_stmt, statement, null) **/		end_stmt,
	/** (execute, statement, null) **/		execute,
	
	/** (beg_expr, expression, null) **/	beg_expr,
	/** (end_expr, expression, value) **/	end_expr,
	/** (evaluate, expression, value) **/	evaluate,
	
}
