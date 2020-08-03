package com.jcsa.jcparse.lang.ir.stmt.labeled;

import com.jcsa.jcparse.lang.ir.stmt.CirStatement;

/**
 * 	|--	|--	<i>labeled_statement</i>									<br>
 * 	|--	|--	|--	function_beg_statement									<br>
 * 	|--	|--	|--	function_end_statement									<br>
 * 	|--	|--	|--	if_end_statement										<br>
 * 	|--	|--	|--	case_end_statement										<br>
 * 	|--	|--	|--	default_statement										<br>
 * 	|--	|--	|--	label_statement											<br>
 * 	|--	|--	|--	loop_end_statement										<br>
 * 
 * @author yukimula
 *
 */
public interface CirLabeledStatement extends CirStatement {
}
