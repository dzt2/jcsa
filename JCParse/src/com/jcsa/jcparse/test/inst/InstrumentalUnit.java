package com.jcsa.jcparse.test.inst;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It maintains the value hold by an expression in cir-code during testing.
 * 
 * @author yukimula
 *
 */
public class InstrumentalUnit {
	
	/* definitions */
	/** the expression where the instrumentation is injected **/
	private CirExpression expression;
	/** the Object-value hold by the expression during tests **/
	private Object value;
	/**
	 * create a unit w.r.t. the expression and its value being recorded
	 * @param expression
	 * @throws Exception
	 */
	protected InstrumentalUnit(CirExpression expression) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else {
			this.expression = expression;
			this.value = null;
		}
	}
	
	/* getters */
	/**
	 * @return the statement where the expression instrumented is in
	 */
	public CirStatement get_statement() { return this.expression.statement_of(); }
	/**
	 * @return the expression being instrumented
	 */
	public CirExpression get_expression() { return this.expression; }
	/**
	 * @return whether the value of the expression was recorded
	 */
	public boolean has_value() { return this.value != null; }
	/**
	 * @return the value obtained during the instrumentation of the location.
	 */
	public Object get_value() { return this.value; }
	/**
	 * set the value hold by the expression
	 * @param value
	 */
	protected void set_value(Object value) { this.value = value; }
	
}
