package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SadDisjunctAssertion extends SadCompositeAssertion {

	protected SadDisjunctAssertion(CirNode source) {
		super(source);
	}

	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < this.number_of_assertions(); k++) {
			buffer.append(this.get_assertion(k).generate_code());
			if(k < this.number_of_assertions() - 1) {
				buffer.append(" OR ");
			}
		}
		return buffer.toString();
	}

	@Override
	protected SadNode clone_self() {
		return new SadDisjunctAssertion(this.get_cir_source());
	}
	
}
