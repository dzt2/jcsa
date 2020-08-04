package com.jcsa.jcparse.lang.cirlang.stmt;

import com.jcsa.jcparse.lang.cirlang.CirNode;
import com.jcsa.jcparse.lang.cirlang.unit.CirStatementList;

/**
 * <code>
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
 * </code>
 * @author yukimula
 *
 */
public interface CirStatement extends CirNode {
	
	/**
	 * @return the function body where the statement is created
	 */
	public CirStatementList get_function_body();
	
	/**
	 * @return the integer ID as the label of the statement in the body
	 */
	public int get_statement_label();
	
}
