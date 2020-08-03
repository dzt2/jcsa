package com.jcsa.jcparse.lang.ir.stmt.transit;

import com.jcsa.jcparse.lang.ir.stmt.CirStatement;
import com.jcsa.jcparse.lang.ir.unit.CirLabel;

/**
 * <code>
 * 	<i>uncondition_statement</i>									<br>
 * 	|--	goto_statement												<br>
 * 	|-- if_end_goto_statement										<br>
 * 	|--	loop_break_statement										<br>
 * 	|--	loop_continue_statement										<br>
 * 	|--	switch_break_statement										<br>
 * 	|--	return_goto_statement										<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirUnconditionStatement extends CirStatement {
	
	/**
	 * @return the label of statement to be executed following the goto-statement
	 */
	public CirLabel get_next_label();
	
}
