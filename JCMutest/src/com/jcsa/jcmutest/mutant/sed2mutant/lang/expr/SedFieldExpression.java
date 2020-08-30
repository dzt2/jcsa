package com.jcsa.jcmutest.mutant.sed2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedField;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SedFieldExpression extends SedExpression {

	public SedFieldExpression(CirNode cir_source, CType data_type) {
		super(cir_source, data_type);
	}
	
	public SedExpression get_body() {
		return (SedExpression) this.get_child(0);
	}
	public SedField get_field() {
		return (SedField) this.get_child(1);
	}

	@Override
	protected SedNode clone_self() {
		return new SedFieldExpression(
				this.get_cir_source(), 
				this.get_data_type());
	}

	@Override
	public String generate_code() throws Exception {
		return "(" + this.get_body().generate_code() + ")." + this.get_field().generate_code();
	}
	
}
