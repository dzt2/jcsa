package com.jcsa.jcparse.lang.irlang.expr;

/**
 * field_expression --> expression . field
 * @author yukimula
 *
 */
public interface CirFieldExpression extends CirReferExpression {

	public CirExpression get_body();
	public CirField get_field();
	public void set_body(CirExpression body) throws IllegalArgumentException;
	public void set_field(CirField field) throws IllegalArgumentException;

}
