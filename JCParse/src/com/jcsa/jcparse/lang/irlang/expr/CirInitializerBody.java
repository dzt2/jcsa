package com.jcsa.jcparse.lang.irlang.expr;

/**
 * initializer_body |-- { (expression)* }
 * @author yukimula
 *
 */
public interface CirInitializerBody extends CirValueExpression {
	public int number_of_elements();
	public CirExpression get_element(int k) throws IndexOutOfBoundsException;
	public void add_element(CirExpression element) throws IllegalArgumentException;
}
