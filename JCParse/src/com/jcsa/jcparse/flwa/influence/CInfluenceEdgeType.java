package com.jcsa.jcparse.flwa.influence;

/**
 * The type of the influence edge in influence graph can be one of the following type:<br>
 * <code>
 * 	execute_condition: 		if_statement --> if_statement.condition<br>
 * 	execute_condition: 		case_statement --> case_statement.condition<br>
 * 	execute_right_value:	assignment --> assignment.rvalue<br>
 * 	execute_function:		call_statement --> call_statement.function<br>
 * 	
 * 	execute_to_label:		goto_statement --> goto_statement.label<br>
 * 	label_to_statement:		label --> next.statement<br>
 * 	execute_when_true:		if_statement.condition --> statement<br>
 * 	execute_when_false:		if_statement.condition --> statement<br>
 * 	
 * 	call_by_argument:		call_statement.function --> call_statement.argument<br>
 * 	arg_param_assign:		call_statement.argument --> init_assignment.rvalue<br>
 * 	retr_wait_assign:		retr_assignment.lvalue	--> wait_expression<br>
 * 	argument_to_wait:		call_statement.argument	--> wait_expression.function<br>
 * 	
 * 	use_def_assign:			assignment.rvalue --> assignment.lvalue<br>
 * 	def_use_assign:			assignment.lvalue --> other_stmt.expression<br>
 * 	operand_used_in:		expression|field  --> expression.parent<br>
 * </code>
 * @author yukimula
 *
 */
public enum CInfluenceEdgeType {
	
	/* execution related */
	/** if_stmt --> if_stmt.condition **/		execute_condition,
	/** assign --> assign.right_value **/		execute_right_value,
	/** goto_statement --> label **/			execute_to_label,
	/** call_stmt --> call_stmt.func **/		execute_function,
	
	/* transition related */
	/** label --> statement **/					label_to_statement,
	/** if_stmt.condition --> statement **/		execute_when_true,
	/** if_stmt.condition --> statement **/		execute_when_false,
	
	/* calling related */
	/** call_stmt.func --> call_stmt.arg **/	call_by_argument,
	/** call_stmt.arg --> init_stmt.rval **/	arg_param_assign,
	/** retr_assign.lval --> wait_expr **/		retr_wait_assign,
	/** call_stmt.arg --> wait_expr.func **/	argument_to_wait,
	
	/* assignment related */
	/** assignment.rval --> assignment.lval **/	use_def_assign,
	/** assignment.lval --> other_stmt.expr **/	def_use_assign,
	/** wait_expr.func --> wait_expr **/		fun_wait_assign,
	/** field --> field_expression **/			
	/** expression --> expression.parent **/	operand_used_in,
	
}
