package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * seed:stmt:add_expr(e1, o, e2) == e1 --> e1 o e2
 * @author yukimula
 *
 */
public class SadAddOperandAssertion extends SadMutExpressionAssertion {

	protected SadAddOperandAssertion(CirNode source) {
		super(source);
	}
	
	public SadOperator get_add_operator() {
		return (SadOperator) this.get_child(2);
	}
	
	public SadExpression get_add_operand() {
		return (SadExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "add_operand(" + this.get_orig_expression().generate_code()
				+ ", " + this.get_add_operator().generate_code() + ", "
				+ this.get_add_operand().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadAddOperandAssertion(this.get_cir_source());
	}

}
