package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;

/**
 * mut_expression |-- label::mut_value(expression)
 * @author yukimula
 *
 */
public class SedMutExpression extends SedStateMutation {
	
	/* getters */
	/**
	 * @return the statement in which the mutation is seeded
	 */
	public SedLabel get_statement() {
		return (SedLabel) this.get_child(0);
	}
	/**
	 * @return the original expression to be mutated in
	 */
	public SedExpression get_orig_expression() {
		return (SedExpression) this.get_child(1);
	}
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedMutExpression();
	}
	@Override
	public String generate_code() throws Exception {
		return this.get_statement().generate_code() + "::mut_value("
				+ this.get_orig_expression().generate_code() + ")";
	}

}
