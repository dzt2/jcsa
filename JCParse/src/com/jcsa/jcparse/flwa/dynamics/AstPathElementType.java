package com.jcsa.jcparse.flwa.dynamics;

/**
 * Type of element in execution path of AstNode can be:<br>
 * 	(1) data_value: it represents the value evaluated from an expression;<br>
 * 	(2) beg_stmt: it describes that the statement is started at this point.<br>
 * 	(3) end_stmt: it describes that the statement is completed at this point.<br>
 * @author yukimula
 *
 */
public enum AstPathElementType {
	/** expression_node |-- bytes[] **/	data_value,
	/** statement_node	|-- 0 **/		beg_stmt,
	/** statement_node	|-- 1 **/		end_stmt,
}
