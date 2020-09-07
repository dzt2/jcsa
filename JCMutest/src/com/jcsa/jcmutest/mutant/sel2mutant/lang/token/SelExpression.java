package com.jcsa.jcmutest.mutant.sel2mutant.lang.token;

import com.jcsa.jcparse.lang.sym.SymExpression;

public class SelExpression extends SelToken {
	
	private SymExpression expression;
	public SelExpression(SymExpression expression) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression");
		else
			this.expression = expression;
	}
	
	/**
	 * @return the expression that this node defines
	 */
	public SymExpression get_expression() { return this.expression; }

	@Override
	public String generate_code() throws Exception {
		return this.expression.generate_code();
	}
	
}
