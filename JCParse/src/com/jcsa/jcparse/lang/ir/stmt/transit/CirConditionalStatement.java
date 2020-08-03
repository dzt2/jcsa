package com.jcsa.jcparse.lang.ir.stmt.transit;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.stmt.CirStatement;
import com.jcsa.jcparse.lang.ir.unit.CirLabel;

/**
 * if condition then goto L1 otherwise L2;
 * 
 * @author yukimula
 *
 */
public interface CirConditionalStatement extends CirStatement {
	
	/**
	 * @return the condition that decides the branch
	 */
	public CirExpression get_condition();
	
	/**
	 * @return the label of the true branch
	 */
	public CirLabel get_true_label();
	
	/**
	 * @return the label of the false branch
	 */
	public CirLabel get_false_label();
	
}
