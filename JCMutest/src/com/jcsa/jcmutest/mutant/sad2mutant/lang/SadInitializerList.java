package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SadInitializerList extends SadExpression {

	protected SadInitializerList(CirNode source, CType data_type) {
		super(source, data_type);
	}
	
	/**
	 * @return the number of elements in initializer list
	 */
	public int number_of_elements() { 
		return this.number_of_children();
	}
	
	/**
	 * @param k
	 * @return the kth element in initializer list
	 * @throws IndexOutOfBoundsException
	 */
	public SadExpression get_element(int k) throws IndexOutOfBoundsException {
		return (SadExpression) this.get_child(k);
	}

	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		for(int k = 0; k < this.number_of_elements(); k++) {
			buffer.append(this.get_element(k).generate_code());
			if(k < this.number_of_elements() - 1) {
				buffer.append(", ");
			}
		}
		buffer.append("}");
		return buffer.toString();
	}

	@Override
	protected SadNode clone_self() {
		return new SadInitializerList(this.get_cir_source(), this.get_data_type());
	}
	
}
