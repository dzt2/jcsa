package com.jcsa.jcmutest.mutant.sed2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedArgumentList;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SedCallExpression extends SedExpression {

	public SedCallExpression(CirNode cir_source, CType data_type) {
		super(cir_source, data_type);
	}
	
	public SedExpression get_function() {
		return (SedExpression) this.get_child(0);
	}
	public SedArgumentList get_argument_list() {
		return (SedArgumentList) this.get_child(1);
	}

	@Override
	protected SedNode clone_self() {
		return new SedCallExpression(this.
				get_cir_source(), this.get_data_type());
	}

	@Override
	public String generate_code() throws Exception {
		return this.get_function().generate_code() + 
				this.get_argument_list().generate_code();
	}
	
}
