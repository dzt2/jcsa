package com.jcsa.jcparse.lang.ir.stmt.labeled;

import com.jcsa.jcparse.lang.ir.stmt.CirStatement;

/**
 * <code>
 * 	<i>label_tag_statement</i>										<br>
 * 	|-- beg_statement												<br>
 * 	|-- end_statement												<br>
 * 	|--	if_end_statement											<br>
 * 	|--	loop_beg_statement											<br>
 * 	|--	loop_end_statement											<br>
 * 	|--	switch_end_statement										<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirLabelTagStatement extends CirStatement {
}
