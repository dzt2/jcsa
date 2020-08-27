package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * assert#stmt:set_expr(expr, expr)
 * @author yukimula
 *
 */
public class SadSetExpressionAssertion extends SadMutExpressionAssertion {

	protected SadSetExpressionAssertion(CirNode source) {
		super(source);
	}
	
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
