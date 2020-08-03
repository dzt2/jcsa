package com.jcsa.jcparse.lang.ir.expr.value;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;

/**
 * <code>
 * 	<i>value_expression</i>											<br>
 * 	|--	constant_expression			{constant: CConstant}			<br>
 * 	|--	default_value_expression									<br>
 * 	|--	initializer_list											<br>
 * 	|--	type_cast_expression										<br>
 * 	|--	string_literal_expression	{literal: String}				<br>
 * 	|--	return_value_expression										<br>
 * 	|--	address_of_expression		{unary_expression}				<br>
 * 	|--	arith_unary_expression		{unary_expression}				<br>
 * 	|--	bitws_unary_expression		{unary_expression}				<br>
 * 	|--	logic_unary_expression		{unary_expression}				<br>
 * 	|--	arith_binary_expression		{binary_expression}				<br>
 * 	|--	bitws_binary_expression		{binary_expression}				<br>
 * 	|--	logic_binary_expression		{binary_expression}				<br>
 * 	|--	relational_expression		{binary_expression}				<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirValueExpression extends CirExpression {
}
