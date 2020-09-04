package com.jcsa.jcmutest.sedlang.lang.expr;

import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class SedInitializerList extends SedExpression {

	public SedInitializerList(CirExpression cir_expression, 
			CType data_type) throws Exception {
		super(cir_expression, data_type);
	}
	
	public int number_of_elements() {
		return this.number_of_children();
	}
	
	public SedExpression get_element(int k) throws IndexOutOfBoundsException {
		return (SedExpression) this.get_child(k);
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
	protected SedNode construct() throws Exception {
		return new SedInitializerList(
				this.get_cir_expression(),
				this.get_data_type());
	}

}
