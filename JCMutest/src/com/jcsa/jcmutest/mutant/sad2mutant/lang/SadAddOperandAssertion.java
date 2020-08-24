package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * add_expr(expression, operator, expression)
 * as loperand ==> loperand operator roperand
 * @author yukimula
 *
 */
public class SadAddOperandAssertion extends SadAssertion {
	
	protected SadAddOperandAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the original expression to be appended with operator
	 */
	public SadExpression get_loperand() {
		return (SadExpression) this.get_child(1);
	}
	/**
	 * @return the operator being added in original expression
	 */
	public SadOperator get_operator() {
		return (SadOperator) this.get_child(2);
	}
	/**
	 * @return the expression being added to the original one
	 */
	public SadExpression get_roperand() {
		return (SadExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "add_expr(" + this.get_loperand().generate_code()
				+ ", " + this.get_operator().generate_code() + ", "
				+ this.get_roperand().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadAddOperandAssertion(this.get_cir_source());
	}

}
