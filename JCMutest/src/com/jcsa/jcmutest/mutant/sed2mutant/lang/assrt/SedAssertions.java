package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;


public abstract class SedAssertions extends SedAssertion {
	
	public int number_of_assertions() {
		return this.number_of_children() - 1;
	}
	public SedAssertion get_assertion(int k) throws IndexOutOfBoundsException {
		return (SedAssertion) this.get_child(k + 1);
	}
	
}
