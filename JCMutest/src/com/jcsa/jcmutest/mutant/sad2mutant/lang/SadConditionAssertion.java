package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * assert#statement: condition
 * @author yukimula
 *
 */
public class SadConditionAssertion extends SadConstraintAssertion {

	protected SadConditionAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return symbolic expression as the condition being asserted
	 */
	public SadExpression get_condition() {
		return (SadExpression) this.get_child(1);
	}

	@Override
	protected String generate_content() throws Exception {
		return this.get_condition().generate_code();
	}

	@Override
	protected SadNode clone_self() {
		return new SadConditionAssertion(this.get_cir_source());
	}
	
}
