package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * assert#location: execute(int)
 * @author yukimula
 *
 */
public class SadExecuteOnAssertion extends SadConstraintAssertion {

	protected SadExecuteOnAssertion(CirNode source) {
		super(source);
	}
	
	/** 
	 * @return the expression that defines the loop-times
	 */
	public SadExpression get_loop_time() {
		return (SadExpression) this.get_child(1);
	}

	@Override
	protected String generate_content() throws Exception {
		return "execute(" + this.get_loop_time().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadExecuteOnAssertion(this.get_cir_source());
	}
	
}
