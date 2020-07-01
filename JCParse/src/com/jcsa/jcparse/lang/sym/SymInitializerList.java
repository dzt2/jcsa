package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

public class SymInitializerList extends SymExpression {

	protected SymInitializerList() {
		super(CBasicTypeImpl.void_type, null);
	}
	
	/**
	 * @return the number of elements in the list
	 */
	public int number_of_elements() { return this.number_of_children(); }
	/**
	 * @param k
	 * @return the kth element in the initializer list
	 * @throws IndexOutOfBoundsException
	 */
	public SymExpression get_element(int k) throws IndexOutOfBoundsException {
		return (SymExpression) this.get_child(k);
	}

	@Override
	protected SymNode clone_self() {
		return new SymInitializerList();
	}

	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		for(int k = 0; k < this.number_of_elements(); k++) {
			buffer.append(this.get_element(k).generate_code(ast_code));
			if(k < this.number_of_elements() - 1) buffer.append(", "); 
		}
		buffer.append("}");
		return buffer.toString();
	}

}
