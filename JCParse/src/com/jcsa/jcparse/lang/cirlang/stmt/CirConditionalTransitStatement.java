package com.jcsa.jcparse.lang.cirlang.stmt;

import com.jcsa.jcparse.lang.cirlang.expr.CirExpression;
import com.jcsa.jcparse.lang.cirlang.unit.CirLabel;

/**
 * if expression then label else label
 * <code>
 * 	<i>conditional_transit_statement</i>							<br>
 * 	|--	if_transit_statement										<br>
 * 	|--	case_transit_statement										<br>
 * 	|--	loop_transit_statement										<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirConditionalTransitStatement extends CirStatement {
	
	/**
	 * @return the condition to decide the path
	 */
	public CirExpression get_condition();
	
	/**
	 * @return the label to its true branch
	 */
	public CirLabel get_true_label();
	
	/**
	 * @return the label to its false branch
	 */
	public CirLabel get_false_label();
	
}
