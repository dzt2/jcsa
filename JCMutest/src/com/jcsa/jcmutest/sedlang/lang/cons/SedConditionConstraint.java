package com.jcsa.jcmutest.sedlang.lang.cons;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedConditionConstraint extends SedConstraint {

	public SedConditionConstraint(CirStatement statement) throws Exception {
		super(statement, SedKeywords.cassert);
	}
	
	/**
	 * @return the condition being asserted
	 */
	public SedExpression get_condition() {
		return (SedExpression) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return this.get_condition().generate_code();
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedConditionConstraint(this.
				get_statement().get_cir_statement());
	}

}
