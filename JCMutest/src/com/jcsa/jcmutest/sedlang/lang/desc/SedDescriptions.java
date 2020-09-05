package com.jcsa.jcmutest.sedlang.lang.desc;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.token.SedKeyword;

public abstract class SedDescriptions extends SedNode {
	
	public SedDescriptions(SedKeywords keyword) throws Exception {
		if(keyword == null)
			throw new IllegalArgumentException("Invalid keyword");
		else {
			this.add_child(new SedKeyword(keyword));
		}
	}
	
	/**
	 * @return conjunct | disjunct
	 */
	public SedKeyword get_keyword() {
		return (SedKeyword) this.get_child(0);
	}
	
	/**
	 * @return the number of descriptions within
	 */
	public int number_of_descriptions() {
		return this.number_of_children() - 1;
	}
	
	/**
	 * @param k
	 * @return the kth descriptions
	 * @throws IndexOutOfBoundsException
	 */
	public SedDescription get_description(int k) throws IndexOutOfBoundsException {
		return (SedDescription) this.get_child(k + 1);
	}

	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.get_keyword().generate_code());
		buffer.append("\n{");
		for(int k = 0; k < this.number_of_descriptions(); k++) {
			buffer.append("\n\t");
			buffer.append(this.get_description(k).generate_code());
		}
		buffer.append("\n}");
		return buffer.toString();
	}
	
}
