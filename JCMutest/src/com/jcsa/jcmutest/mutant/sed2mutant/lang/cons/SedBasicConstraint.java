package com.jcsa.jcmutest.mutant.sed2mutant.lang.cons;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;

/**
 * 	|--	SedBasicConstraint				{location: SedLabel}			<br>
 * 	|--	|--	SedExecutionConstraint		execute(location, int)			<br>
 * 	|--	|--	SedConditionConstraint		assert(location, expr)			<br>
 * 
 * 	@author yukimula
 *
 */
public abstract class SedBasicConstraint extends SedConstraint {

	public SedBasicConstraint() {
		super();
	}
	
	public SedLabel get_location() { 
		return (SedLabel) this.get_child(0);
	}
	
}
