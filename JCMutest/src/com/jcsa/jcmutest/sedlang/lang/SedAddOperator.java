package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;

/**
 * add_operator |-- label::add_oprt(expression, operator)
 * in which expression set as expression op
 * @author yukimula
 *
 */
public class SedAddOperator extends SedStateMutation {
	
	/* getters */
	/**
	 * @return the statement in which the mutation occurs
	 */
	public SedLabel get_statement() {
		return (SedLabel) this.get_child(0);
	}
	/**
	 * @return the original expression being appended with operator
	 */
	public SedExpression get_orig_expression() {
		return (SedExpression) this.get_child(1);
	}
	/**
	 * @return the operator to append on tail of original expression
	 */
	public SedOperator get_add_operator() {
		return (SedOperator) this.get_child(2);
	}
	
	/* implementations */
	@Override
	protected SedNode copy_self() {
		return new SedAddOperator();
	}
	@Override
	public String generate_code() throws Exception {
		return this.get_statement().generate_code() + "::add_oprt("
				+ this.get_orig_expression().generate_code() + ", "
				+ this.get_add_operator().generate_code() + ")";
	}
	
}
