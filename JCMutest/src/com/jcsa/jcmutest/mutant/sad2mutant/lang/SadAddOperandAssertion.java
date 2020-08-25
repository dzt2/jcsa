package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * seed#statement: add_expr(expression, operator, expression)
 * @author yukimula
 *
 */
public class SadAddOperandAssertion extends SadMutationAssertion {

	protected SadAddOperandAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the original expression to be added with more operands
	 */
	public SadExpression get_orig_expression() {
		return (SadExpression) this.get_child(1);
	}
	
	/**
	 * @return the operator to be added in the tail of original expression
	 */
	public SadOperator get_add_operator() {
		return (SadOperator) this.get_child(2);
	}
	
	/**
	 * @return the operand to be added in the tail of original expression
	 */
	public SadExpression get_add_operand() {
		return (SadExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "add_expr(" + this.get_orig_expression().generate_code()
				+ ", " + this.get_add_operator().generate_code() + ", "
				+ this.get_add_operand().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadAddOperandAssertion(this.get_cir_source());
	}

}
