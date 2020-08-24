package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SadConditionAssertion extends SadAssertion {

	protected SadConditionAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the condition to be asserted in the location
	 */
	public SadExpression get_condition() {
		return (SadExpression) this.get_child(1);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_condition().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
