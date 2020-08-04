package com.jcsa.jcparse.lang.cirlang;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.cirlang.unit.CirFunctionDefinition;

/**
 * The syntax of C-intermediate representation language is defined as:	<br>
 * 	<code>
 * 	<br>
 * 	<i>unit</i>															<br>
 * 	|--	translation_unit												<br>
 * 	|--	function_definition												<br>
 * 	|-- statement_list													<br>
 * 	|-- field									{name: String}			<br>
 * 	|-- label									{label: int}			<br>
 * 	|--	type									{data_type: CType}		<br>
 * 	|--	argument_list													<br>
 * 	<br>
 * 	<i>statement</i>							{label_id: int}			<br>
 * 	|--	<i>assign_statement</i>											<br>
 * 	|--	|--	binary_assign_statement										<br>
 * 	|--	|--	initial_assign_statement									<br>
 * 	|--	|--	parameter_assign_statement									<br>
 * 	|--	|--	temporary_assign_statement									<br>
 * 	|--	|--	return_assign_statement										<br>
 * 	|--	|--	wait_assign_statement										<br>
 * 	|--	|--	increase_assign_statement									<br>
 * 	|-- call_statement													<br>
 * 	|--	<i>uncondition_transit_statement</i>							<br>
 * 	|--	|--	direct_goto_statement										<br>
 * 	|--	|--	if_end_goto_statement										<br>
 * 	|--	|--	loop_beg_goto_statement										<br>
 * 	|--	|--	loop_end_goto_statement										<br>
 * 	|--	|--	switch_beg_goto_statement									<br>
 * 	|--	|--	switch_end_goto_statement									<br>
 * 	|--	|--	return_goto_statement										<br>
 * 	|--	<i>conditional_transit_statement</i>							<br>
 * 	|--	|--	if_transit_statement										<br>
 * 	|--	|--	case_transit_statement										<br>
 * 	|--	|--	loop_transit_statement										<br>
 * 	|--	<i>labeled_tag_statement</i>									<br>
 * 	|--	|--	function_beg_statement										<br>
 * 	|--	|--	function_end_statement										<br>
 * 	|--	|--	goto_labeled_statement										<br>
 * 	|--	|--	if_end_label_statement										<br>
 * 	|--	|--	switch_end_label_statement									<br>
 * 	|--	|--	loop_beg_label_statement									<br>
 * 	|--	|--	loop_end_label_statement									<br>
 * 	<br>
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
 * 	<br>
 * 	</code>
 * 
 * @author yukimula
 *
 */
public interface CirNode {
	
	/**
	 * @return the tree where the node is created
	 */
	public CirTree get_tree();
	
	/**
	 * @return the integer ID of the node in tree
	 */
	public int get_node_id();
	
	/**
	 * @return the parent of this node or null if it is root
	 */
	public CirNode get_parent();
	
	/**
	 * @return the index of the node as the child of its parent
	 * 		   or -1 if the node is a root node
	 */
	public int get_child_index();
	
	/**
	 * @return the number of children defined under this node
	 */
	public int number_of_children();
	
	/**
	 * @return the children under this node
	 */
	public Iterable<CirNode> get_children();
	
	/**
	 * @param k
	 * @return the kth child under the node
	 * @throws IndexOutOfBoundsException
	 */
	public CirNode get_child(int k) throws IndexOutOfBoundsException;
	
	/**
	 * @return the function definition in which the node is defined
	 */
	public CirFunctionDefinition function_definition();
	
	/**
	 * @param child the child to be appended in the tail of the children
	 * @throws IllegalArgumentException
	 */
	public void add_child(CirNode child) throws IllegalArgumentException;
	
	/**
	 * @return the abstract syntactic node to which the node refers
	 */
	public AstNode get_ast_source();
	
}
