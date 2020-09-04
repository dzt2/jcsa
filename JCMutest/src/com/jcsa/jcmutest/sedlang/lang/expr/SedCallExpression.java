package com.jcsa.jcmutest.sedlang.lang.expr;

import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.token.SedArgumentList;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class SedCallExpression extends SedExpression {

	public SedCallExpression(CirExpression cir_expression, CType data_type) throws Exception {
		super(cir_expression, data_type);
	}
	
	public SedExpression get_function() {
		return (SedExpression) this.get_child(0);
	}
	
	public SedArgumentList get_argument_list() {
		return (SedArgumentList) this.get_child(1);
	}

	@Override
	public String generate_code() throws Exception {
		return this.get_function().generate_code() + 
				this.get_argument_list().generate_code();
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedCallExpression(this.
				get_cir_expression(), this.get_data_type());
	}
	
}
