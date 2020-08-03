package com.jcsa.jcparse.lang.ir.expr;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;

/**
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	<i>expression</i>					{data_type: CType}				<br>
 * 	|--	<i>refer_expression</i>											<br>
 * 	|--	|-- <i>name_expression</i>										<br>
 * 	|--	|--	|-- declarator_reference									<br>
 * 	|--	|--	|-- identifier_reference									<br>
 * 	|--	|--	|-- temporary_reference										<br>
 * 	|--	|--	|--	return_reference										<br>
 * 	|--	|-- field_expression											<br>
 * 	|--	|--	de_reference_expression		{unary_expression}				<br>
 * 	|--	<i>value_expression</i>											<br>
 * 	|--	|--	constant_expression			{constant: CConstant}			<br>
 * 	|--	|--	default_value_expression									<br>
 * 	|--	|--	initializer_list											<br>
 * 	|--	|--	type_cast_expression										<br>
 * 	|--	|--	string_literal_expression	{literal: String}				<br>
 * 	|--	|--	return_value_expression										<br>
 * 	|--	|--	address_of_expression		{unary_expression}				<br>
 * 	|--	|--	arith_unary_expression		{unary_expression}				<br>
 * 	|--	|--	bitws_unary_expression		{unary_expression}				<br>
 * 	|--	|--	logic_unary_expression		{unary_expression}				<br>
 * 	|--	|--	arith_binary_expression		{binary_expression}				<br>
 * 	|--	|--	bitws_binary_expression		{binary_expression}				<br>
 * 	|--	|--	logic_binary_expression		{binary_expression}				<br>
 * 	|--	|--	relational_expression		{binary_expression}				<br>
 * 	+------------------------------------------------------------------+<br>
 * 	<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirExpression extends CirNode {
	
	/**
	 * @return the data type of the value hold by the expression
	 */
	public CType get_data_type();
	
}
