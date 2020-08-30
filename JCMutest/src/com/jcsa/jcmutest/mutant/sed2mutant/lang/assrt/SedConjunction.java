package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;

public class SedConjunction extends SedAssertions {

	@Override
	protected String generate_content() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Conjunct{\n");
		for(int k = 0; k < this.number_of_assertions(); k++) {
			buffer.append("\t");
			buffer.append(this.get_assertion(k).generate_code());
			buffer.append("\n");
		}
		buffer.append("}");
		return buffer.toString();
	}

	@Override
	protected SedNode clone_self() {
		return new SedConjunction();
	}

}
