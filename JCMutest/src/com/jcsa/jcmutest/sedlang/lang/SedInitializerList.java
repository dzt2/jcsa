package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * initializer_list |-- { (expression)* }
 * @author yukimula
 *
 */
public class SedInitializerList extends SedExpression {
	
	/* definitions */
	protected SedInitializerList(CirNode source, CType data_type) {
		super(source, data_type);
	}
	/**
	 * @return the number of elements in the initializer list
	 */
	public int number_of_elements() { return this.number_of_children(); }
	/**
	 * @param k
	 * @return the kth element in the list
	 * @throws IndexOutOfBoundsException
	 */
	public SedExpression get_element(int k) throws IndexOutOfBoundsException {
		return (SedExpression) this.get_child(k);
	}
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedInitializerList(this.get_source(), this.get_data_type());
	}
	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		for(int k = 0; k < this.number_of_elements(); k++) {
			buffer.append(this.get_element(k).generate_code());
			if(k < this.number_of_elements() - 1) {
				buffer.append(",");
			}
		}
		buffer.append("]");
		return buffer.toString();
	}
	
}
