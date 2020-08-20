package com.jcsa.jcmutest.mutant.mutation;

/**
 * The function defines the generic structure of mutation seeded in C
 * intermediate representation language (i.e. CirMutation).
 * 
 * @author yukimula
 *
 */
public enum MutaFunction {
	/** trap_on_stmt(statement, int) 
	 *  which traps on statement if it is executed for k times **/	
	trap_on_stmt,
	
	/** trap_on_same(expression, bool|long|double|string|cir) 
	 *  which traps exception once expression == parameter **/	
	trap_on_equal,
	
	/** trap_on_diff(expression, bool|long|double|string|cir)
	 *  which traps exception once expression != parameter **/
	trap_on_diff,
	
	/** trap_on_great(expression, long|double|string|cir)
	 *  which traps exception when expression > parameter **/
	trap_on_great,
	
	/** trap_on_small(expression, long|double|string|cir)
	 *  which traps exception when expression < parameter **/
	trap_on_small,
	
	/** set_goto_stmt(source_statement, target_statement)
	 *  which set the next statement from source as target **/
	set_goto_stmt,
	
	/** delete_stmt(statement) which removes the statement
	 *  from the execution path **/
	delete_stmt,
	
	/** set_expression(expression, bool|long|double|string|cir)
	 *  in which the expression is replaced with the parameter **/
	set_expression,
	
	/** set_data_state(expression, bool|long|double|string|cir, int)
	 *  in which the expression is replaced with parameter at kth
	 *  time of the statement being executed **/
	set_data_state,
	
	/** inc_expression(expression, long|double|string|cir) in which
	 *  the expression is inserted as (expression + parameter) **/
	inc_expression,
	
	/** mul_expression(expression, long|double|string|cir) in which
	 *  the expression is inserted as (expression * parameter) **/
	mul_expression,
	
	/** set_operator(binary_expression, operator) in which operator
	 *  of the binary-expression as the parameter specified **/
	set_operator,
	
	/** trap_operator(binary_expression, operator) in which original
	 *  expression different from parameter will trap the exception **/
	trap_operator,
	
	/** del_operator(unary_expression) in which unary operator in 
	 *  the expression is removed **/
	del_operator,
	
}
