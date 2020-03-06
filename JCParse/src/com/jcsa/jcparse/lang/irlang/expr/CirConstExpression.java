package com.jcsa.jcparse.lang.irlang.expr;

import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * const_expression --> {constant}
 * @author yukimula
 *
 */
public interface CirConstExpression extends CirValueExpression {
	public CConstant get_constant();
	public void set_constant(CConstant constant) throws IllegalArgumentException;
}
