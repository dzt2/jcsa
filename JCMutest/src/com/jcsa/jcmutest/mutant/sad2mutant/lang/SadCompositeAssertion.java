package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcparse.lang.irlang.CirNode;

public abstract class SadCompositeAssertion extends SadAssertion {

	protected SadCompositeAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the number of assertions being combined
	 */
	public int number_of_assertions() {
		return this.number_of_assertions() - 1;
	}
	
	/**
	 * @param k
	 * @return the kth assertion to be verified
	 * @throws IndexOutOfBoundsException
	 */
	public SadAssertion get_assertion(int k) throws IndexOutOfBoundsException {
		return (SadAssertion) this.get_child(k + 1);
	}
	
	@Override
	public String generate_code() throws Exception {
		return "comp#" + this.get_location().generate_code() + 
								"::{" + this.generate_content() + "}";
	}
	
	protected abstract String generate_content() throws Exception;
	
}
