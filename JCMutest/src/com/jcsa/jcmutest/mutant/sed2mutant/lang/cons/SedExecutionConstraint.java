package com.jcsa.jcmutest.mutant.sed2mutant.lang.cons;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;

/**
 * execute(statement, int)
 * 
 * @author yukimula
 *
 */
public class SedExecutionConstraint extends SedBasicConstraint {
	
	/**
	 * @return the minimal times that the statement needs to be executed
	 */
	public SedExpression get_times() {
		return (SedExpression) this.get_child(1);
	}
	
	@Override
	protected SedNode clone_self() {
		return new SedExecutionConstraint();
	}

	@Override
	public String generate_code() throws Exception {
		return "execute(" + this.get_location().generate_code() + 
				", " + this.get_times().generate_code() + ")";
	}

}
