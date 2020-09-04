package com.jcsa.jcmutest.sedlang.lang.token;

import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.expr.SedExpression;

public class SedArgumentList extends SedToken {
	
	/**
	 * @return the number of arguments in the list
	 */
	public int number_of_arguments() {
		return this.number_of_children();
	}
	
	/**
	 * @param k
	 * @return the kth argument within the list
	 * @throws IndexOutOfBoundsException
	 */
	public SedExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (SedExpression) this.get_child(k);
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

	@Override
	protected SedNode construct() throws Exception {
		return new SedArgumentList();
	}

}
