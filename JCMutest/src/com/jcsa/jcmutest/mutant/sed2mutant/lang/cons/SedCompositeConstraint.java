package com.jcsa.jcmutest.mutant.sed2mutant.lang.cons;

public abstract class SedCompositeConstraint extends SedConstraint {
	
	public int number_of_constraints() {
		return this.number_of_children();
	}
	
	public SedConstraint get_constraint(int k) throws IndexOutOfBoundsException {
		return (SedConstraint) this.get_child(k);
	}
	
}
