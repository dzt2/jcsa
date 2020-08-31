package com.jcsa.jcmutest.mutant.sed2mutant.lang.cons;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;

public class SedConjunctConstraints extends SedCompositeConstraint {

	@Override
	protected SedNode clone_self() {
		return new SedConjunctConstraints();
	}

	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < this.number_of_constraints(); k++) {
			buffer.append(this.get_constraint(k).generate_code());
			if(k < this.number_of_constraints() - 1) {
				buffer.append(" AND ");
			}
		}
		return buffer.toString();
	}

}
