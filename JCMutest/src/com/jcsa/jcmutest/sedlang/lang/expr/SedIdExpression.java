package com.jcsa.jcmutest.sedlang.lang.expr;

import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class SedIdExpression extends SedBasicExpression {

	private String name;
	public SedIdExpression(CirExpression cir_expression, 
			CType data_type, String name) throws Exception {
		super(cir_expression, data_type);
		if(name == null || name.isBlank())
			throw new IllegalArgumentException("Invalid name");
		else
			this.name = name;
	}
	
	/**
	 * @return identifier name
	 */
	public String get_name() { return this.name; }

	@Override
	public String generate_code() throws Exception {
		return this.name;
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedIdExpression(
				this.get_cir_expression(), 
				this.get_data_type(), this.name);
	}
	
}
