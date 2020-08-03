package com.jcsa.jcparse.lang.ir.expr.value;

import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * constant_expression |-- {constant: CConstant}
 * @author yukimula
 *
 */
public interface CirConstantExpression extends CirValueExpression {
	
	/**
	 * @return the constant that the expression defines
	 */
	public CConstant get_constant();
	
}
