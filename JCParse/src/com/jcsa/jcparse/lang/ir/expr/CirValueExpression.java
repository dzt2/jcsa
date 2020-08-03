package com.jcsa.jcparse.lang.ir.expr;

/**
 * 	|--	<i>value_expression</i>										<br>
 * 	|--	|--	address_expression		[unary_expression]				<br>
 * 	|--	|--	arith_unary_expression	[unary_expression]				<br>
 * 	|--	|--	bitws_unary_expression	[unary_expression]				<br>
 * 	|--	|--	logic_unary_expression	[unary_expression]				<br>
 * 	|--	|--	constant_expression		{constant: CConstant}			<br>
 * 	|--	|--	string_literal_expression	{literal: String}			<br>
 * 	|--	|--	default_value_expression								<br>
 * 	|--	|--	initializer_list_expression								<br>
 * 	|--	|--	wait_value_expression									<br>
 * 	|--	|--	arith_binary_expression	[binary_expression]				<br>
 * 	|--	|--	bitws_binary_expression	[binary_expression]				<br>
 * 	|--	|--	logic_binary_expression	[binary_expression]				<br>
 * 	|--	|--	relational_expression	[binary_expression]				<br>
 * 
 * @author yukimula
 *
 */
public interface CirValueExpression extends CirExpression {
}
