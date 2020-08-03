package com.jcsa.jcparse.lang.ir.stmt;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.unit.CirStatementList;

/**
 * <code>
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
 * </code>
 * @author yukimula
 *
 */
public interface CirStatement extends CirNode {
	
	/**
	 * @return the function body which the statement belongs to
	 */
	public CirStatementList get_function_body();
	
	/**
	 * @return the integer label of the statement in function body
	 */
	public int get_statement_label();
	
}
