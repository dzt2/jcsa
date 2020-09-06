package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymInitializerList extends SymExpression {

	protected SymInitializerList(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}
	
	public int number_of_elements() { return this.number_of_children(); }
	
	public SymExpression get_element(int k) throws IndexOutOfBoundsException {
		return (SymExpression) this.get_child(k);
	}

	@Override
	protected SymNode construct() throws Exception {
		return new SymInitializerList(this.get_data_type());
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

}
