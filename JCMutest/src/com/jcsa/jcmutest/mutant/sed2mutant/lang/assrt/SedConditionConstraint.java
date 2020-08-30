package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;

/**
 * assert@label: condition
 * @author yukimula
 *
 */
public class SedConditionConstraint extends SedConstraint {
	
	/**
	 * @return the condition being asserted
	 */
	public SedExpression get_condition() {
		return (SedExpression) this.get_child(1);
	}
	
	@Override
	protected String generate_content() throws Exception {
		return this.get_condition().generate_code();
	}

	@Override
	protected SedNode clone_self() {
		return new SedConditionConstraint();
	}

}
