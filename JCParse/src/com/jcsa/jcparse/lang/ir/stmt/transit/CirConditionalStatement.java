package com.jcsa.jcparse.lang.ir.stmt.transit;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.stmt.CirStatement;
import com.jcsa.jcparse.lang.ir.unit.CirLabel;

/**
 * if condition then L1 else L2.
 * @author yukimula
 *
 */
public interface CirConditionalStatement extends CirStatement {
	
	/**
	 * @return the condition that decides the branch
	 */
	public CirExpression get_condition();
	
	/**
	 * @return the branch if condition == true
	 */
	public CirLabel get_true_label();
	
	/**
	 * @return the branch if condition == false
	 */
	public CirLabel get_false_label();
	
}
