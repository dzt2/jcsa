package com.jcsa.jcmutest.sedlang.lang.expr;

import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.token.SedField;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class SedFieldExpression extends SedExpression {

	public SedFieldExpression(CirExpression cir_expression, CType data_type) throws Exception {
		super(cir_expression, data_type);
	}
	
	/**
	 * @return body expression
	 */
	public SedExpression get_body() { 
		return (SedExpression) this.get_child(0); 
	}
	
	/**
	 * @return the field of the expression
	 */
	public SedField get_field() { 
		return (SedField) this.get_child(1); 
	}

	@Override
	public String generate_code() throws Exception {
		return "(" + this.get_body().generate_code() + 
				")." + this.get_field().generate_code();
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedFieldExpression(this.
				get_cir_expression(), this.get_data_type());
	}
	
}
