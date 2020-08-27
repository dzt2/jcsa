package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * seed#stmt:ins_operand(e1, o, e2) ==> e1 = e2 o e1
 * @author yukimula
 *
 */
public class SadInsOperandAssertion extends SadMutExpressionAssertion {

	protected SadInsOperandAssertion(CirNode source) {
		super(source);
	}
	
	public SadOperator get_ins_operator() {
		return (SadOperator) this.get_child(2);
	}
	
	public SadExpression get_ins_operand() {
		return (SadExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "ins_operand(" + this.get_orig_expression().generate_code()
				+ ", " + this.get_ins_operator().generate_code() + ", "
				+ this.get_ins_operand().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadInsOperandAssertion(this.get_cir_source());
	}

}
