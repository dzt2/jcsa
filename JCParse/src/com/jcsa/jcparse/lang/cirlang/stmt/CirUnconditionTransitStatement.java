package com.jcsa.jcparse.lang.cirlang.stmt;

import com.jcsa.jcparse.lang.cirlang.unit.CirLabel;

/**
 * goto label.
 * <br>
 * <code>
 * 	|--	<i>uncondition_transit_statement</i>							<br>
 * 	|--	|--	direct_goto_statement										<br>
 * 	|--	|--	if_end_goto_statement										<br>
 * 	|--	|--	loop_beg_goto_statement										<br>
 * 	|--	|--	loop_end_goto_statement										<br>
 * 	|--	|--	switch_beg_goto_statement									<br>
 * 	|--	|--	switch_end_goto_statement									<br>
 * 	|--	|--	return_goto_statement										<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirUnconditionTransitStatement extends CirStatement {
	
	/**
	 * @return the label of the statement
	 */
	public CirLabel get_label();
	
}
