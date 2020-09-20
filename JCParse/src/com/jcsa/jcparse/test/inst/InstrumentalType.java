package com.jcsa.jcparse.test.inst;

/**
 * The type of instrumental location is one of the following:<br>
 * <br>
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	function															<br>
 * 	|--	function_definition		{null}									<br>
 * 	+------------------------------------------------------------------+<br>
 * 	statement															<br>
 * 	|--	AstStatement			{null}									<br>
 * 	+------------------------------------------------------------------+<br>
 * 	assignment															<br>
 * 	|--	xxx_assign_expression	{null}									<br>
 * 	|--	init_declarator													<br>
 * 	+------------------------------------------------------------------+<br>
 * 	reference					{null}									<br>
 * 	|--	declarator														<br>
 * 	|--	xxx_assign_expr.loperand										<br>
 * 	|--	incre_xxx_expr.operand											<br>
 * 	|--	field_expr(.)body												<br>
 * 	|--	address_of_expr.operand											<br>
 * 	+------------------------------------------------------------------+<br>
 * 	condition					{cast_bool_value}						<br>
 * 	|--	if_statement.condition											<br>
 * 	|--	conditional_expr.condition										<br>
 * 	|--	while_statement.condition										<br>
 * 	|--	do_while_statement.condition									<br>
 * 	|--	for_statement.condition.expr									<br>
 * 	|--	logic_unary_expr.operand										<br>
 * 	|--	logic_binary_expr.loperand|roperand								<br>
 * 	+------------------------------------------------------------------+<br>
 * 	expression					{value|null}							<br>
 * 	+------------------------------------------------------------------+<br>
 * 	sequence															<br>
 * 	|--	literal					{String}								<br>
 * 	|--	comma_expr				{null}									<br>
 * 	|--	argument_list			{null}									<br>
 * 	|--	initial_body			{null}									<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * <br>
 * @author yukimula
 *
 */
public enum InstrumentalType {
	
	/** function(func_def, null) **/		function,
	
	/** statement(stmt, null) **/			statement,
	
	/** assignment(expr|decl, null) **/		assignment,
	
	/** expression(expr, any|null) **/		expression,
	
	/** reference(lexpr, null) **/			reference,
	
	/** condition(expr, boolean) **/		condition,
	
	/** sequence(body, null) **/			sequence,
	
}
