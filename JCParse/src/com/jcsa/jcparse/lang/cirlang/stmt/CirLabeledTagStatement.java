package com.jcsa.jcparse.lang.cirlang.stmt;

/**
 * <code>
 * 	|--	<i>labeled_tag_statement</i>									<br>
 * 	|--	|--	function_beg_statement										<br>
 * 	|--	|--	function_end_statement										<br>
 * 	|--	|--	goto_labeled_statement										<br>
 * 	|--	|--	if_end_label_statement										<br>
 * 	|--	|--	switch_end_label_statement									<br>
 * 	|--	|--	loop_beg_label_statement									<br>
 * 	|--	|--	loop_end_label_statement									<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirLabeledTagStatement extends CirStatement {
}
