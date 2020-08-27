package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * seed#stmt:mut_expr(expression)
 * @author yukimula
 *
 */
public abstract class SadMutExpressionAssertion extends SadStateErrorAssertion {

	protected SadMutExpressionAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the original expression being mutated
	 */
	public SadExpression get_orig_expression() {
		return (SadExpression) this.get_child(1);
	}
	
}
