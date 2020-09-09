package com.jcsa.jcmutest.mutant.sec2mutant.lang.token;

import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecExpression extends SecToken {
	
	private SymExpression expression;
	public SecExpression(SymExpression expression) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression");
		else {
			this.expression = expression;
			this.add_child(new SecType(expression.get_data_type()));
		}
	}
	
	public SymExpression get_expression() { return this.expression; }
	
	public SecType get_type() { return (SecType) this.get_child(0); }

	@Override
	public String generate_code() throws Exception {
		return this.expression.generate_code();
	}
	
}
