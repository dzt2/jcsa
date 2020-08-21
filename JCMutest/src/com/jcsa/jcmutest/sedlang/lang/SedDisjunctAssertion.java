package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;

/**
 * disjunct_assertion |-- assertion (|| assertion)+
 * @author yukimula
 *
 */
public class SedDisjunctAssertion extends SedAssertion {
	
	/* definition */
	protected SedDisjunctAssertion() {
		super(null);
	}
	/**
	 * @return the number of assertions being conjuncted
	 */
	public int number_of_assertions() {
		return this.number_of_children();
	}
	/**
	 * @param k
	 * @return the kth assertion in the conjunction
	 * @throws IndexOutOfBoundsException
	 */
	public SedAssertion get_assertion(int k) throws IndexOutOfBoundsException {
		return (SedAssertion) this.get_child(k);
	}
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedDisjunctAssertion();
	}
	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		for(int k = 0; k < this.number_of_assertions(); k++) {
			buffer.append(this.get_assertion(k).generate_code());
			if(k < this.number_of_assertions() - 1) {
				buffer.append(" || ");
			}
		}
		buffer.append("}");
		return buffer.toString();
	}
	
}
