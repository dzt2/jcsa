package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SelDescriptions extends SelDescription {

	protected SelDescriptions(CirStatement statement, 
			SelKeywords keyword) throws Exception {
		super(statement, keyword);
	}
	
	public int number_of_descriptions() {
		return this.number_of_children() - 2;
	}
	
	public SelDescription get_description(int k) throws IndexOutOfBoundsException {
		return (SelDescription) this.get_child(k - 2);
	}

	@Override
	protected String generate_parameters() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("\n{");
		for(int k = 0; k < this.number_of_descriptions(); k++) {
			buffer.append("\n\t");
			buffer.append(this.get_description(k).generate_code());
			buffer.append(";");
		}
		buffer.append("\n}");
		return buffer.toString();
	}
	
}
