package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * set_expr(expression, expression)
 * 
 * @author yukimula
 *
 */
public class SadSetExpressionAssertion extends SadAssertion {
	
	protected SadSetExpressionAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the original expression to be replaced
	 */
	public SadExpression get_orig_expression() {
		return (SadExpression) this.get_child(1);
	}
	
	/**
	 * @return the expression to mutate original one
	 */
	public SadExpression get_muta_expression() {
		return (SadExpression) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "set_expr(" + this.get_orig_expression().generate_code()
				+ ", " + this.get_muta_expression().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadSetExpressionAssertion(this.get_cir_source());
	}

}
