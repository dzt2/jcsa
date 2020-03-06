package com.jcsa.jcparse.lang.irlang.expr;

/**
 * address_expression	|-- & expression
 * @author yukimula
 *
 */
public interface CirAddressExpression extends CirValueExpression {
	public CirReferExpression get_operand();
	public void set_operand(CirReferExpression operand) throws IllegalArgumentException;
}
