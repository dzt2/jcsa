package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * call_expression |-- expression argument_list
 * @author yukimula
 *
 */
public class SymbolCallExpression extends SymbolExpression {

	private SymbolCallExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}

	@Override
	protected SymbolNode construct() throws Exception {
		return new SymbolCallExpression(this.get_data_type());
	}

	public SymbolExpression get_function() { return (SymbolExpression) this.get_child(0); }

	public SymbolArgumentList get_argument_list() { return (SymbolArgumentList) this.get_child(1); }

	/**
	 * @param function
	 * @param arguments
	 * @return call_expression |-- expression argument_list
	 * @throws Exception
	 */
	protected static SymbolCallExpression create(CType data_type, SymbolExpression function, SymbolArgumentList arguments) throws Exception {
		SymbolCallExpression expression = new SymbolCallExpression(data_type);
		expression.add_child(function); expression.add_child(arguments);
		return expression;
	}

}
