package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * SymbolUnaryExpression		{operator: -, ~, !, *, &, =} 
 * @author yukimula
 *
 */
public class SymbolUnaryExpression extends SymbolExpression {

	private SymbolUnaryExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}

	@Override
	protected SymbolNode construct() throws Exception {
		return new SymbolUnaryExpression(this.get_data_type());
	}
	
	/**
	 * @return {operator: -, ~, !, *, &, =}
	 */
	public SymbolOperator get_operator() { return (SymbolOperator) this.get_child(0); }
	/**
	 * @return unary operand in the expression
	 */
	public SymbolExpression get_operand() { return (SymbolExpression) this.get_child(1); }
	
	/**
	 * @param operator
	 * @param operand
	 * @return SymbolUnaryExpression		{operator: -, ~, !, *, &, =} expression
	 * @throws Exception
	 */
	protected static SymbolUnaryExpression create(
			CType data_type, SymbolOperator operator, 
			SymbolExpression operand) throws Exception {
		switch(operator.get_operator()) {
		case negative:
		case bit_not:
		case logic_not:
		case address_of:
		case dereference:
		case assign:
		{
			SymbolUnaryExpression expression = new SymbolUnaryExpression(data_type);
			expression.add_child(operator); expression.add_child(operand); 
			return expression;
		}
		default: throw new IllegalArgumentException(operator.get_operator().toString());
		}
	}
	
}
