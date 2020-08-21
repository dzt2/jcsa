package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;

/**
 * ins_expression |-- label::ins_value(expression, operator, expression)
 * in which original expression replaced as operand op expression.
 * @author yukimula
 *
 */
public class SedInsExpression extends SedStateMutation {
	
	/* getters */
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
	public SedOperator get_ins_operator() {
		return (SedOperator) this.get_child(2);
	}
	/**
	 * @return the operand appended on the expression
	 */
	public SedExpression get_ins_operand() {
		return (SedExpression) this.get_child(3);
	}
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedInsExpression();
	}
	@Override
	public String generate_code() throws Exception {
		return this.get_statement().generate_code() + "::ins_value("
				+ this.get_orig_expression().generate_code() + ", "
				+ this.get_ins_operator().generate_code() + ", "
				+ this.get_ins_operand().generate_code() + ")";
	}
	
}
