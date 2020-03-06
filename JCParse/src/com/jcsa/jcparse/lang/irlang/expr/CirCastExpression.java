package com.jcsa.jcparse.lang.irlang.expr;

/**
 * cast_expression	|--	cast type expression
 * @author yukimula
 *
 */
public interface CirCastExpression extends CirValueExpression {
	
	public CirType get_type();
	public CirExpression get_operand();
	public void set_type(CirType type) throws IllegalArgumentException;
	public void set_operand(CirExpression operand) throws IllegalArgumentException;
	
}
