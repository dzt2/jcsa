package com.jcsa.jcmutest.sedlang.lang.dess;

import com.jcsa.jcmutest.sedlang.lang.SedNode;

public class SedDisjunctDescriptions extends SedDescriptions {

	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("DISJUNCT {");
		for(int k = 0; k < this.number_of_descriptions(); k++) {
			buffer.append("\n\t");
			buffer.append(this.get_description(k).generate_code());
		}
		buffer.append("\n}");
		return buffer.toString();
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedDisjunctDescriptions();
	}

}
