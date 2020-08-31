package com.jcsa.jcmutest.mutant.sed2mutant.lang.cons;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;

/**
 * assert(statement, expression)
 * 
 * @author yukimula
 *
 */
public class SedConditionConstraint extends SedBasicConstraint {
	
	/**
	 * @return the condition being asserted in the statement
	 */
	public SedExpression get_condition() {
		return (SedExpression) this.get_child(1);
	}
	
	@Override
	protected SedNode clone_self() {
		return new SedConditionConstraint();
	}
	
	@Override
	public String generate_code() throws Exception {
		return "assert(" + this.get_location().generate_code() + 
				", " + this.get_condition().generate_code() + ")";
	}
	
}
