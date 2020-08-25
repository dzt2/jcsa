package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SadConjunctAssertion extends SadAssertion {

	protected SadConjunctAssertion(CirNode source) {
		super(source);
	}
	
	public int number_of_assertions() {
		return this.number_of_children() - 1;
	}
	
	public SadAssertion get_assertion(int k) throws IndexOutOfBoundsException {
		return (SadAssertion) this.get_child(k + 1);
	}

	@Override
	protected String generate_content() throws Exception {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < this.number_of_assertions(); k++) {
			buffer.append("{").append(this.get_assertion(k).generate_code()).append("}");
			if(k < this.number_of_assertions() - 1) {
				buffer.append(" AND ");
			}
		}
		return buffer.toString();
	}

	@Override
	protected SadNode clone_self() {
		return new SadConjunctAssertion(this.get_cir_source());
	}

}
