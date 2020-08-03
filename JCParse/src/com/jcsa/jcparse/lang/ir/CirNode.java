package com.jcsa.jcparse.lang.ir;

import com.jcsa.jcparse.lang.ir.unit.CirFunctionDefinition;

/**
 * 	The syntax of C-intermediate representation language is defined as:	<br>
 * 	<code>
 * 	+------------------------------------------------------------------+<br>
 * 	<i>unit</i>															<br>
 * 	|--	field							{name: String}					<br>
 * 	|--	label							{label: int}					<br>
 * 	|--	type							{type: CType}					<br>
 * 	|--	argument_list													<br>
 * 	|--	statement_list													<br>
 * 	|--	function_definition												<br>
 * 	|--	translation_unit												<br>
 * 	+------------------------------------------------------------------+<br>
 * 	<br>
 * 	+------------------------------------------------------------------+<br>
 * 	<i>statement</i>					{statement_label: int}			<br>
 * 	|--	<i>assignment_statement</i>										<br>
 * 	|--	|--	binary_assign_statement										<br>
 * 	|--	|--	initial_assign_statement									<br>
 * 	|--	|--	increase_assign_statement									<br>
 * 	|--	|--	return_assign_statement										<br>
 * 	|--	|--	temporal_assign_statement									<br>
 * 	|--	|--	wait_assign_statement										<br>
 * 	|--	call_statement													<br>
 * 	|--	<i>uncondition_statement</i>									<br>
 * 	|--	|--	goto_statement												<br>
 * 	|--	|-- if_end_goto_statement										<br>
 * 	|--	|--	loop_break_statement										<br>
 * 	|--	|--	loop_continue_statement										<br>
 * 	|--	|--	switch_break_statement										<br>
 * 	|--	|--	return_goto_statement										<br>
 * 	|--	<i>conditional_statement</i>									<br>
 * 	|--	|--	if_statement												<br>
 * 	|--	|-- loop_statement												<br>
 * 	|--	|-- case_statement												<br>
 * 	|--	<i>label_tag_statement</i>										<br>
 * 	|--	|-- beg_statement												<br>
 * 	|--	|-- end_statement												<br>
 * 	|--	|--	if_end_statement											<br>
 * 	|--	|--	loop_beg_statement											<br>
 * 	|--	|--	loop_end_statement											<br>
 * 	|--	|--	switch_end_statement										<br>
 * 	+------------------------------------------------------------------+<br>
 * 	<br>
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
 * 	</code>
 * 	@author yukimula
 *
 */
public interface CirNode {
	
	/**
	 * @return the tree where the node is created
	 */
	public CirTree get_tree();
	
	/**
	 * @return the integer ID of the node defined
	 */
	public int get_node_id();
	
	/**
	 * @param key the integer ID of the node to be set
	 */
	public void set_node_id(int key);
	
	/**
	 * @return the parent of the node or null if it is the translation_unit
	 */
	public CirNode get_parent();
	
	/**
	 * @return the index of the node as child of its parent or -1 if the 
	 * 			node is a root node in the tree.
	 */
	public int get_child_index();
	
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
	 * @return the kth child under this node
	 * @throws IndexOutOfBoundsException
	 */
	public CirNode get_child(int k) throws IndexOutOfBoundsException;
	
	/**
	 * @param child the node to be appended in the tail of the node
	 * @throws IllegalArgumentException if child is null or belongs to some parent
	 */
	public void add_child(CirNode child) throws IllegalArgumentException;
	
	/**
	 * @param complete whether to generate the complete name of the node
	 * @return the code generated based on the syntax of the cir-node
	 * @throws Exception
	 */
	public String generate_code(boolean complete) throws Exception;
	
	/**
	 * @return the clone of the node
	 */
	public CirNode clone();
	
	/**
	 * @return the function definition where the node belongs to
	 * 		   or null if the node is an external unit.
	 */
	public CirFunctionDefinition get_function_definition();
	
}
