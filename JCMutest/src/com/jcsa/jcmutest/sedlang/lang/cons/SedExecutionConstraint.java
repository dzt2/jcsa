package com.jcsa.jcmutest.sedlang.lang.cons;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedExecutionConstraint extends SedConstraint {

	public SedExecutionConstraint(CirStatement statement, 
			SedExpression loop_times) throws Exception {
		super(statement, SedKeywords.execute);
		this.add_child(loop_times);
	}
	
	/**
	 * @return the loop-times of the execution of statement
	 */
	public SedExpression get_loop_times() {
		return (SedExpression) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_loop_times().generate_code() + ")";
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedExecutionConstraint(this.
				get_statement().get_cir_statement(),
				this.get_loop_times());
	}
	
}
