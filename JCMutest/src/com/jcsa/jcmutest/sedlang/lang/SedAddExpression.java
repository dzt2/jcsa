package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;

/**
 * add_expression |-- label::add_expr(expression, operator, expression)
 * in which original expression is replaced as expression op parameter
 * 
 * @author yukimula
 *
 */
public class SedAddExpression extends SedStateMutation {
	
	/**
	 * @return the statement where the mutation occurs
	 */
	public SedLabel get_statement() {
		return (SedLabel) this.get_child(0);
	}
	/**
	 * @return the original expression being mutated
	 */
	public SedExpression get_orig_expression() {
		return (SedExpression) this.get_child(1);
	}
	/**
	 * @return the operator appended on the expression
	 */
	public SedOperator get_add_operator() {
		return (SedOperator) this.get_child(2);
	}
	/**
	 * @return the operand appended on the expression
	 */
	public SedExpression get_add_operand() {
		return (SedExpression) this.get_child(3);
	}

	@Override
	protected SedNode copy_self() {
		return new SedAddExpression();
	}
	@Override
	public String generate_code() throws Exception {
		return this.get_statement().generate_code() + "::add_value("
				+ this.get_orig_expression().generate_code() + ", "
				+ this.get_add_operator().generate_code() + ", "
				+ this.get_add_operand().generate_code() + ")";
	}
	
}
