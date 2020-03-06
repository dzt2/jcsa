package com.jcsa.jcparse.lang.astree.expr.base;

import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * <code>ConstantExpr |--> Constant</code>
 * 
 * @author yukimula
 *
 */
public interface AstConstant extends AstBasicExpression {
	/**
	 * get the constant this node represents
	 * 
	 * @return
	 */
	public CConstant get_constant();
}
