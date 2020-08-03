package com.jcsa.jcparse.test.path;

/**
 * The type of instrumental node is:<br>
 * 	1) beg_stmt: the time-point before a statement node was executed;<br>
 * 	2) end_stmt: the time-point after the statement node is executed;<br>
 * 	3) evaluate: the time-point when a expression value is evaluated.<br>
 * @author yukimula
 *
 */
public enum InstrumentType {
	
	/** time-point before a statement was executed **/	beg_stmt,
	
	/** time-point after the statement is executed **/	end_stmt,
	
	/** time-point when an expression is evaluated **/	evaluate,
	
}
