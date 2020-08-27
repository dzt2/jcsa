package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * assert#statement:trapping()
 * @author yukimula
 *
 */
public class SadTrappingAssertion extends SadMutationAssertion {
	
	protected SadTrappingAssertion(CirNode source) {
		super(source);
	}
	
	@Override
	protected String generate_content() throws Exception {
		return "trapping()";
	}
	
	@Override
	protected SadNode clone_self() {
		return new SadTrappingAssertion(this.get_cir_source());
	}
	
}
