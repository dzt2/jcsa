package com.jcsa.jcmutest.mutant.sed2mutant.lang.cons;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;

/**
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	SedConstraint														<br>
 * 	|--	SedBasicConstraint				{location: SedLabel}			<br>
 * 	|--	|--	SedExecutionConstraint		execute(location, int)			<br>
 * 	|--	|--	SedConditionConstraint		assert(location, expr)			<br>
 * 	|--	SedCompositeConstraint			{constraints: SedConstraint+}	<br>
 * 	|--	|--	SedConjunctionConstraint									<br>
 * 	|--	|--	SedDisjunctionConstraint									<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SedConstraint extends SedNode {

	public SedConstraint() {
		super(null);
	}

}
