package com.jcsa.jcparse.lang.ir.stmt;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.unit.CirStatementList;

/**
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
 * 	|--	|--	|--	direct_goto_statement									<br>
 * 	|--	|--	|--	switch_goto_statement									<br>
 * 	|--	|--	|--	if_end_goto_statement									<br>
 * 	|--	|--	|-- case_end_goto_statement									<br>
 * 	|--	|--	|--	loop_end_goto_statement									<br>
 * 	|--	|--	|--	return_goto_statement									<br>
 * 	|--	|--	<i>conditional_statement</i>								<br>
 * 	|--	|--	|--	if_statement											<br>
 * 	|--	|--	|-- case_statement											<br>
 * 	|--	|--	|--	loop_statement											<br>
 * 	|--	|--	call_statement												<br>
 * 	|--	|--	<i>labeled_statement</i>									<br>
 * 	|--	|--	|--	function_beg_statement									<br>
 * 	|--	|--	|--	function_end_statement									<br>
 * 	|--	|--	|--	if_end_statement										<br>
 * 	|--	|--	|--	case_end_statement										<br>
 * 	|--	|--	|--	default_statement										<br>
 * 	|--	|--	|--	label_statement											<br>
 * 	|--	|--	|--	loop_end_statement										<br>
 * @author yukimula
 *
 */
public interface CirStatement extends CirNode {
	
	/**
	 * @return the list where the statement is created
	 */
	public CirStatementList get_statement_list();
	
	/**
	 * @return the index of this statement in the list
	 */
	public int get_statement_index();
	
}
