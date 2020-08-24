package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * ins_operand(loperand, operator, roperand)
 * as loperand ==> roperand operator loperand
 * @author yukimula
 *
 */
public class SadInsOperandAssertion extends SadAssertion {
	
	protected SadInsOperandAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the original expression to be inserted with operator
	 */
	public SadExpression get_loperand() {
		return (SadExpression) this.get_child(1);
	}
	/**
	 * @return the operator being inserted in original expression
	 */
	public SadOperator get_operator() {
		return (SadOperator) this.get_child(2);
	}
	/**
	 * @return the expression being inserted to the original one
	 */
	public SadExpression get_roperand() {
		return (SadExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "ins_expr(" + this.get_loperand().generate_code()
				+ ", " + this.get_operator().generate_code() + ", "
				+ this.get_roperand().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadInsOperandAssertion(this.get_cir_source());
	}

}
