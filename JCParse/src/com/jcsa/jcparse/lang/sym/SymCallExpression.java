package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymCallExpression extends SymExpression {

	private SymCallExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}
	
	public SymExpression get_function() {
		return (SymExpression) this.get_child(0);
	}
	
	public SymArgumentList get_argument_list() {
		return (SymArgumentList) this.get_child(1);
	}

	@Override
	protected SymNode construct() throws Exception {
		return new SymCallExpression(this.get_data_type());
	}

	/**
	 * @param data_type
	 * @param function
	 * @param arguments
	 * @return call-expression as function arguments
	 * @throws Exception
	 */
	protected static SymCallExpression create(CType data_type, SymExpression function, SymArgumentList arguments) throws Exception {
		SymCallExpression expression = new SymCallExpression(data_type);
		expression.add_child(function);
		expression.add_child(arguments);
		return expression;
	}
	
}
