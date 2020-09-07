package com.jcsa.jcmutest.mutant.sel2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SelDescriptions extends SelDescription {

	public SelDescriptions(CirStatement statement, SelKeywords keyword) throws Exception {
		super(statement, keyword);
	}
	
	/**
	 * @return the number of descriptions under this node
	 */
	public int number_of_descriptions() { return this.number_of_children() - 2; }
	
	/**
	 * @param k
	 * @return the kth description in the node
	 * @throws IndexOutOfBoundsException
	 */
	public SelDescription get_description(int k) throws IndexOutOfBoundsException {
		return (SelDescription) this.get_child(k + 2);
	}

	@Override
	protected String generate_content() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("\n{");
		for(int k = 0; k < this.number_of_descriptions(); k++) {
			buffer.append("\n\t");
			buffer.append(this.get_description(k).generate_code());
		}
		buffer.append("\n}");
		return buffer.toString();
	}
	
}
