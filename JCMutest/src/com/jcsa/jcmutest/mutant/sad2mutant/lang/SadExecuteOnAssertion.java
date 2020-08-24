package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * assert execute(constant) at statement
 * @author yukimula
 *
 */
public class SadExecuteOnAssertion extends SadAssertion {
	
	protected SadExecuteOnAssertion(CirNode source) {
		super(source);
	}
	
	public SadConstant get_execute_times() {
		return (SadConstant) this.get_child(1);
	}

	@Override
	protected String generate_content() throws Exception {
		return "execute(" + this.get_execute_times().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadExecuteOnAssertion(this.get_cir_source());
	}

}
