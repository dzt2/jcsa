package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * multi_expression	|--	operator {expression}+
 * @author yukimula
 *
 */
public class SedMultiExpression extends SedExpression {

	protected SedMultiExpression(CirNode source, CType data_type) {
		super(source, data_type);
	}
	
	/* getters */
	/**
	 * @return {+, *, &, |, ^, &&, ||}
	 */
	public SedOperator get_operator() { return (SedOperator) this.get_child(0); }
	/**
	 * @return the number of operands used in multiple expression
	 */
	public int number_of_operands() { return this.number_of_children() - 1; }
	/**
	 * @param k
	 * @return the kth operand in the expression
	 * @throws IndexOutOfBoundsException
	 */
	public SedExpression get_operand(int k) throws IndexOutOfBoundsException {
		return (SedExpression) this.get_child(k + 1);
	}
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedMultiExpression(this.get_source(), this.get_data_type());
	}
	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("(").append(this.get_operator().generate_code());
		for(int k = 0; k < this.number_of_operands(); k++) {
			buffer.append(" ").append(this.get_operand(k).generate_code());
		}
		buffer.append(")");
		return buffer.toString();
	}
	
}
