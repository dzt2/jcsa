package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;

/**
 * condition_assertion |-- label::assert(condition)
 * @author yukimula
 *
 */
public class SedConditionAssertion extends SedAssertion {
	
	/* definition */
	protected SedConditionAssertion() {
		super(null);
	}
	/**
	 * @return the expression being asserted as true or false
	 */
	public SedExpression get_condition() {
		return (SedExpression) this.get_child(1);
	}
	/**
	 * @return the label of the statement where the condition is asserted
	 */
	public SedLabel get_statement() {
		return (SedLabel) this.get_child(0);
	}
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedConditionAssertion();
	}
	@Override
	public String generate_code() throws Exception {
		return this.get_statement().generate_code() + 
				"::assert(" + this.get_condition().generate_code() + ")";
	}
	
}
