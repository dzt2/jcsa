package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * argument_list |-- ( expression* )
 * @author yukimula
 *
 */
public class SadArgumentList extends SadToken {

	protected SadArgumentList(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the number of arguments in the list
	 */
	public int number_of_arguments() {
		return this.number_of_children();
	}
	
	/**
	 * @param k
	 * @return the kth argument in the list
	 * @throws IndexOutOfBoundsException
	 */
	public SadExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (SadExpression) this.get_child(k);
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
	protected SadNode clone_self() {
		return new SadArgumentList(this.get_cir_source());
	}

}
