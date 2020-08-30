package com.jcsa.jcmutest.mutant.sed2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SedInitializerList extends SedExpression {

	public SedInitializerList(CirNode cir_source, CType data_type) {
		super(cir_source, data_type);
	}
	
	public int number_of_elements() { return this.number_of_children(); }
	public SedExpression get_element(int k) throws IndexOutOfBoundsException {
		return (SedExpression) this.get_child(k);
	}

	@Override
	protected SedNode clone_self() {
		return new SedInitializerList(this.get_cir_source(), this.get_data_type());
	}

	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		for(int k = 0; k < this.number_of_children(); k++) {
			buffer.append(" ");
			buffer.append(this.get_element(k).generate_code());
			if(k < this.number_of_children() - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(" }");
		return buffer.toString();
	}

}
