package com.jcsa.jcmutest.mutant.sec2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SecDescriptions extends SecDescription {

	public SecDescriptions(CirStatement statement, SecKeywords keyword) throws Exception {
		super(statement, keyword);
	}
	
	public int number_of_descriptions() {
		return this.number_of_children() - 2;
	}
	
	public SecDescription get_description(int k) throws IndexOutOfBoundsException {
		return (SecDescription) this.get_child(k + 2);
	}

	@Override
	protected String generate_content() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		for(int k = 0; k < this.number_of_descriptions(); k++) {
			buffer.append("\n\t");
			buffer.append(this.get_description(k).generate_code());
		}
		buffer.append("\n}");
		return buffer.toString();
	}
	
}
