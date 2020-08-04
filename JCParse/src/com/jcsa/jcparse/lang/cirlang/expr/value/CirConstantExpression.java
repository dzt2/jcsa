package com.jcsa.jcparse.lang.cirlang.expr.value;

import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * <code>
 * 	constant_expression |-- {constant: CConstant}
 * </code>
 * @author yukimula
 *
 */
public interface CirConstantExpression extends CirValueExpression {
	
	/**
	 * @return the constant of the expression
	 */
	public CConstant get_constant();
	
}
