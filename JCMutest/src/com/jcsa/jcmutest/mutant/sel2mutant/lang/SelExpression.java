package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcparse.lang.sym.SymExpression;

public class SelExpression extends SelToken {
	
	private SymExpression expression;
	protected SelExpression(SymExpression expression) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression");
		else
			this.expression = expression;
	}
	
	/**
	 * @return the symbolic expression that the node describes
	 */
	public SymExpression get_expression() { return this.expression; }

	@Override
	public String generate_code() throws Exception {
		return this.expression.generate_code();
	}
	
}
