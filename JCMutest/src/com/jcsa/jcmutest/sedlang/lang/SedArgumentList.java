package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * argument_list |-- ( {expression}* )
 * @author yukimula
 *
 */
public class SedArgumentList extends SedNode {
	
	/* constructor */
	protected SedArgumentList(CirNode source) {
		super(source);
	}
	
	/* getters */
	/**
	 * @return the number of arguments in the list
	 */
	public int number_of_arguments() { return this.number_of_children(); }
	/**
	 * @param k
	 * @return the kth argument in the list
	 * @throws IndexOutOfBoundsException
	 */
	public SedExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (SedExpression) this.get_child(k);
	}
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedArgumentList(this.get_source());
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
