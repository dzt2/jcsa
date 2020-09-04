package com.jcsa.jcmutest.sedlang.lang.dess;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SedDescriptions extends SedDescription {
	
	public SedDescriptions(CirStatement statement, SedKeywords keyword) throws Exception {
		super(statement, keyword);
	}

	public int number_of_descriptions() {
		return this.number_of_children() - 2;
	}
	
	public SedDescription get_description(int k) 
			throws IndexOutOfBoundsException {
		return (SedDescription) this.get_child(k + 2);
	}

	@Override
	protected String generate_content() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("\n{");
		for(int k = 0; k < this.number_of_descriptions(); k++) {
			buffer.append("\n\t");
			buffer.append(this.get_description(k).generate_code());
		}
		return buffer.append("\n}").toString();
	}
	
}
