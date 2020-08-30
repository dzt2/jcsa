package com.jcsa.jcmutest.mutant.sed2mutant.lang.token;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SedArgumentList extends SedToken {

	public SedArgumentList(CirNode cir_source) {
		super(cir_source);
	}
	
	public int number_of_arguments() {
		return this.number_of_children();
	}
	public SedExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (SedExpression) this.get_child(k);
	}

	@Override
	protected SedNode clone_self() {
		return new SedArgumentList(this.get_cir_source());
	}

	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("(");
		for(int k = 0; k < this.number_of_arguments(); k++) {
			buffer.append(this.get_argument(k).generate_code());
			if(k < this.number_of_arguments() - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}
	
	
}
