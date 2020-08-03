package com.jcsa.jcparse.lang.ir.expr;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.stmt.CirStatement;

/**
 * 	<code>
 * 	<i>expression</i>												<br>
 * 	|--	<i>reference_expression</i>									<br>
 * 	|--	|--	<i>name_expression</i>									<br>
 * 	|--	|--	|--	declarator_expression								<br>
 * 	|--	|--	|--	identifier_expression								<br>
 * 	|--	|--	|--	implicator_expression								<br>
 * 	|--	|--	|--	return_ref_expression								<br>
 * 	|--	|--	deference_expression	[unary_expression]				<br>
 * 	|--	|--	field_expression										<br>
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
 * 	|--	|--	casting_expression										<br>
 * 	|--	|--	arith_binary_expression	[binary_expression]				<br>
 * 	|--	|--	bitws_binary_expression	[binary_expression]				<br>
 * 	|--	|--	logic_binary_expression	[binary_expression]				<br>
 * 	|--	|--	relational_expression	[binary_expression]				<br>
 * 	</code>
 * @author yukimula
 *
 */
public interface CirExpression extends CirNode {
	
	/**
	 * @return the data type of the value hold by the expression
	 */
	public CType get_data_type();
	
	/**
	 * @return the statement where the expression belongs to
	 */
	public CirStatement statement();
	
}
