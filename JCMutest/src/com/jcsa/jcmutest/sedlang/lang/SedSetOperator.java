package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;

/**
 * set_operator |-- label::set_oprt(expression, operator)
 * 
 * @author yukimula
 *
 */
public class SedSetOperator extends SedStateMutation {
	
	/* getters */
	/**
	 * @return the statement in which the mutation occurs
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
	 * @return the operator to replace the original one
	 */
	public SedOperator get_muta_operator() {
		return (SedOperator) this.get_child(2);
	}
	
	/* implementations */
	@Override
	protected SedNode copy_self() {
		return new SedSetOperator();
	}
	@Override
	public String generate_code() throws Exception {
		return this.get_statement().generate_code() + "::set_oprt("
				+ this.get_orig_expression().generate_code() + ", "
				+ this.get_muta_operator().generate_code() + ")";
	}
	
}
