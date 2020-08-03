package com.jcsa.jcparse.lang.ir;

import com.jcsa.jcparse.lang.ir.unit.CirFunctionDefinition;

/**
 * 	The syntax of C-intermediate representation language is defined as:	<br>
 * 	<code>
 * 	CirNode																<br>
 * 	|--	translation_unit												<br>
 * 	|--	function_definition												<br>
 * 	|--	field					{field: String}							<br>
 * 	|--	label					{label: String}							<br>
 * 	|--	type					{type: CType}							<br>
 * 	|--	argument_list													<br>
 * 	|--	statement_list													<br>
 * 	|--	<i>statement</i>												<br>
 * 	|--	|--	<i>assign_statement</i>										<br>
 * 	|--	|--	|-- initial_assign_statement								<br>
 * 	|--	|--	|--	binary_assign_statement									<br>
 * 	|--	|--	|--	increase_assign_statement								<br>
 * 	|--	|--	|--	return_assign_statement									<br>
 * 	|--	|--	|-- wait_assign_statement									<br>
 * 	|--	|--	|--	temporal_assign_statement								<br>
 * 	|--	|--	|--	parameter_assign_statement								<br>
 * 	|--	|--	<i>uncondition_statement</i>								<br>
 * 	|--	|--	|--	goto_statement											<br>
 * 	|--	|--	|--	fun_call_statement										<br>
 * 	|--	|--	|--	return_goto_statement									<br>
 * 	|--	|--	<i>conditional_statement</i>								<br>
 * 	|--	|--	|--	if_statement											<br>
 * 	|--	|--	|-- case_statement											<br>
 * 	|--	|--	|--	loop_statement											<br>
 * 	|--	|--	<i>labeled_statement</i>									<br>
 * 	|--	|--	|--	function_beg_statement									<br>
 * 	|--	|--	|--	function_end_statement									<br>
 * 	|--	|--	|--	if_end_statement										<br>
 * 	|--	|--	|--	case_end_statement										<br>
 * 	|--	|--	|--	default_statement										<br>
 * 	|--	|--	|--	label_statement											<br>
 * 	|--	<i>expression</i>												<br>
 * 	|--	|-- <i>reference_expression</i>									<br>
 * 	|--	|--	|-- <i>name_expression</i>									<br>
 * 	|--	|--	|--	|-- declarator_expression								<br>
 * 	|--	|--	|--	|--	identifier_expression								<br>
 * 	|--	|--	|--	|--	implicator_expression								<br>
 * 	|--	|--	|--	|-- return_ref_expression								<br>
 * 	|--	|--	|--	deference_expression	[unary_expression]				<br>
 * 	|--	|--	|-- field_expression										<br>
 * 	|--	|-- value_expression											<br>
 * 	|--	|--	|-- address_expression		[unary_expression]				<br>
 * 	|--	|--	|--	arith_unary_expression	[unary_expression]				<br>
 * 	|--	|--	|-- bitws_unary_expression	[unary_expression]				<br>
 * 	|--	|--	|-- logic_unary_expression	[unary_expression]				<br>
 * 	|--	|--	|--	constant_expression		{constant: CConstant}			<br>
 * 	|--	|--	|-- string_literal_expression	{literal: String}			<br>
 * 	|--	|--	|--	default_value_expression								<br>
 * 	|--	|--	|--	initializer_list_expression								<br>
 * 	|--	|--	|--	wait_value_expression									<br>
 * 	|--	|--	|--	arith_binary_expression	[binary_expression]				<br>
 * 	|--	|--	|--	bitws_binary_expression	[binary_expression]				<br>
 * 	|--	|--	|--	logic_binary_expression	[binary_expression]				<br>
 * 	|--	|--	|--	relational_expression	[binary_expression]				<br>
 * 	</code>
 * 	@author yukimula
 *
 */
public interface CirNode {
	
	/**
	 * @return the tree where the node is defined
	 */
	public CirTree get_tree();
	
	/**
	 * @return the integer key that defines this node
	 */
	public int get_key();
	
	/**
	 * @return the parent of the node
	 */
	public CirNode get_parent();
	
	/**
	 * @return the children under the node
	 */
	public Iterable<CirNode> get_children();
	
	/**
	 * @return the number of children under the node
	 */
	public int number_of_children();
	
	/**
	 * @param k
	 * @return the kth child under the node
	 * @throws IndexOutOfBoundsException
	 */
	public CirNode get_child(int k) throws IndexOutOfBoundsException;
	
	/**
	 * @return the clone of the node
	 */
	public CirNode clone();
	
	/**
	 * @return the function definition where the node belongs to or null
	 * 			if the node is an external unit.
	 */
	public CirFunctionDefinition function_definition();
	
	/**
	 * @param child the child to be appended at the tail of the children of the node
	 * @throws IllegalArgumentException if the node is null or belongs to some node
	 */
	public void add_child(CirNode child) throws IllegalArgumentException;
	
}
