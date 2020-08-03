package com.jcsa.jcparse.lang.ir.expr.value;

import com.jcsa.jcparse.lang.ir.expr.CirValueExpression;
import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * |-- constant {boolean | short | integer | long | float | double}
 * 
 * @author yukimula
 *
 */
public interface CirConstantExpression extends CirValueExpression {
	
	/**
	 * @return the numeric constant that defines the expression
	 */
	public CConstant get_constant();
	
}
