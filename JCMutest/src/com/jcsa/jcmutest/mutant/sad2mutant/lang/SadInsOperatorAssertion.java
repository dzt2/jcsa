package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * ins_operator(expression, operator): expression ==> operator expression
 * 
 * @author yukimula
 *
 */
public class SadInsOperatorAssertion extends SadAssertion {

	protected SadInsOperatorAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the original expression being inserted with operator
	 */
	public SadExpression get_operand() {
		return (SadExpression) this.get_child(1);
	}
	/**
	 * @return the unary operator inserted in the original expression
	 */
	public SadOperator get_operator() {
		return (SadOperator) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "ins_operator(" + this.get_operand().generate_code() + 
				", " + this.get_operator().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadInsOperatorAssertion(this.get_cir_source());
	}
	
}
