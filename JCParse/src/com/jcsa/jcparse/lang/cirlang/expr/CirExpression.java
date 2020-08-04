package com.jcsa.jcparse.lang.cirlang.expr;

import com.jcsa.jcparse.lang.cirlang.CirNode;
import com.jcsa.jcparse.lang.cirlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	<code>
 * 	<i>expression</i>							{data_type: CType}		<br>
 * 	|--	<i>refer_expression</i>											<br>
 * 	|--	|--	<i>name_expression</i>				{get_name(boolean)}		<br>
 * 	|--	|--	|-- identifier_expression									<br>
 * 	|--	|--	|--	declarator_expression									<br>
 * 	|--	|--	|--	temporary_expression									<br>
 * 	|--	|--	|--	return_refer_expression									<br>
 * 	|--	|--	defer_expression					[unary_expression]		<br>
 * 	|--	|--	field_expression											<br>
 * 	|--	<i>value_expression</i>											<br>
 * 	|--	|--	constant_expression					{constant: CConstant}	<br>
 * 	|--	|--	string_literal						{literal: String}		<br>
 * 	|--	|--	default_value_expression									<br>
 * 	|--	|--	initializer_list											<br>
 * 	|--	|--	type_cast_expression										<br>
 * 	|--	|--	return_value_expression										<br>
 * 	|--	|--	address_expression					[unary_expression]		<br>
 * 	|--	|--	arith_unary_expression				[unary_expression]		<br>
 * 	|--	|--	bitws_unary_expression				[unary_expression]		<br>
 * 	|--	|--	logic_unary_expression				[unary_expression]		<br>
 * 	|--	|--	arith_binary_expression				[binary_expression]		<br>
 * 	|--	|--	bitws_binary_expression				[binary_expression]		<br>
 * 	|--	|--	logic_binary_expression				[binary_expression]		<br>
 * 	|--	|--	relational_expression				[binary_expression]		<br>
 * 	</code>
 * 	@author yukimula
 *
 */
public interface CirExpression extends CirNode {
	
	/**
	 * @return the data type of the expression value
	 */
	public CType get_data_type();
	
	/**
	 * @return the statement where the expression is used
	 */
	public CirStatement statement_of();
	
}
