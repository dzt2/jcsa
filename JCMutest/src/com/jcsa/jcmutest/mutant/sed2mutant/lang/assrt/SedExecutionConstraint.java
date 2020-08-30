package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedConstant;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;

public class SedExecutionConstraint extends SedConstraint {
	
	/**
	 * @return the label of statement expected to be executed
	 */
	public SedLabel get_statement() {
		return (SedLabel) this.get_child(1);
	}
	/**
	 * @return the times expected to execute the statement
	 */
	public SedConstant get_times() {
		return (SedConstant) this.get_child(2);
	}
	@Override
	protected String generate_content() throws Exception {
		return "execute(" + get_statement().generate_code() + 
				", " + this.get_times().generate_code() + ")";
	}
	@Override
	protected SedNode clone_self() {
		return new SedExecutionConstraint();
	}
	
}
