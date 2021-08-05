package com.jcsa.jcparse.test.inst;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * The instrumental unit refers to an expression of cir-program being instrumented
 * during testing, of which value might be fetched and used for further analysis.
 *
 * @author yukimula
 *
 */
public class InstrumentalUnit {

	/* definitions */
	/** the expression being instrumented **/
	private CirExpression expression;
	/** the value hold by the expression or null **/
	private Object value;
	/**
	 * create an instrumental unit w.r.t. the expression with specified value
	 * @param expression
	 * @param value
	 * @throws Exception
	 */
	protected InstrumentalUnit(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: " + expression);
		else {
			this.expression = expression;
			this.value = value;
		}
	}

	/* getters */
	/**
	 * @return the expression being instrumented under testing
	 */
	public CirExpression get_expression() { return this.expression; }
	/**
	 * @return the statement under which the expression being instrumented
	 */
	public CirStatement get_statement() { return this.expression.statement_of(); }
	/**
	 * @return whether the expression is specified with a value
	 */
	public boolean has_value() { return this.value != null; }
	/**
	 * @return the value hold by the unit's expression
	 */
	public Object get_value() { return this.value; }
	/**
	 * set the value of the expression in the unit
	 * @param value
	 */
	protected void set_value(Object value) { this.value = value; }

	/* value getters */
	/**
	 * @return value translated as bool
	 * @throws IllegalArgumentException
	 */
	public boolean get_bool() throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("No value established");
		if(value instanceof Boolean)
			return ((Boolean) value).booleanValue();
		else if(value instanceof Character)
			return ((Character) value).charValue() != 0;
		else if(value instanceof Short)
			return ((Short) value).shortValue() != 0;
		else if(value instanceof Integer)
			return ((Integer) value).intValue() != 0;
		else if(value instanceof Long)
			return ((Long) value).longValue() != 0;
		else if(value instanceof Float)
			return ((Float) value).floatValue() != 0;
		else if(value instanceof Double)
			return ((Double) value).doubleValue() != 0;
		else
			throw new IllegalArgumentException("Unsupport: " + value);
	}
	/**
	 * @return value translated as char
	 * @throws IllegalArgumentException
	 */
	public char get_char() throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("No value established");
		if(value instanceof Boolean)
			return (char) (((Boolean) value).booleanValue() ? 1 : 0);
		else if(value instanceof Character)
			return (((Character) value).charValue());
		else if(value instanceof Short)
			return (char) ((Short) value).shortValue();
		else if(value instanceof Integer)
			return (char) ((Integer) value).intValue();
		else if(value instanceof Long)
			return (char) ((Long) value).longValue();
		else if(value instanceof Float)
			return (char) ((Float) value).floatValue();
		else if(value instanceof Double)
			return (char) ((Double) value).doubleValue();
		else
			throw new IllegalArgumentException("Unsupport: " + value);
	}
	/**
	 * @return value translated as short
	 * @throws IllegalArgumentException
	 */
	public short get_short() throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("No value established");
		if(value instanceof Boolean)
			return (short) (((Boolean) value).booleanValue() ? 1 : 0);
		else if(value instanceof Character)
			return (short) (((Character) value).charValue());
		else if(value instanceof Short)
			return ((Short) value).shortValue();
		else if(value instanceof Integer)
			return (short) ((Integer) value).intValue();
		else if(value instanceof Long)
			return (short) ((Long) value).longValue();
		else if(value instanceof Float)
			return (short) ((Float) value).floatValue();
		else if(value instanceof Double)
			return (short) ((Double) value).doubleValue();
		else
			throw new IllegalArgumentException("Unsupport: " + value);
	}
	/**
	 * @return value translated as int
	 * @throws IllegalArgumentException
	 */
	public int get_int() throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("No value established");
		if(value instanceof Boolean)
			return ((Boolean) value).booleanValue() ? 1 : 0;
		else if(value instanceof Character)
			return (((Character) value).charValue());
		else if(value instanceof Short)
			return ((Short) value).shortValue();
		else if(value instanceof Integer)
			return ((Integer) value).intValue();
		else if(value instanceof Long)
			return (int) ((Long) value).longValue();
		else if(value instanceof Float)
			return (int) ((Float) value).floatValue();
		else if(value instanceof Double)
			return (int) ((Double) value).doubleValue();
		else
			throw new IllegalArgumentException("Unsupport: " + value);
	}
	/**
	 * @return value translated as long
	 * @throws IllegalArgumentException
	 */
	public long get_long() throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("No value established");
		if(value instanceof Boolean)
			return ((Boolean) value).booleanValue() ? 1 : 0;
		else if(value instanceof Character)
			return (((Character) value).charValue());
		else if(value instanceof Short)
			return ((Short) value).shortValue();
		else if(value instanceof Integer)
			return ((Integer) value).intValue();
		else if(value instanceof Long)
			return ((Long) value).longValue();
		else if(value instanceof Float)
			return (long) ((Float) value).floatValue();
		else if(value instanceof Double)
			return (long) ((Double) value).doubleValue();
		else
			throw new IllegalArgumentException("Unsupport: " + value);
	}
	/**
	 * @return value translated as float
	 * @throws IllegalArgumentException
	 */
	public float get_float() throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("No value established");
		if(value instanceof Boolean)
			return ((Boolean) value).booleanValue() ? 1 : 0;
		else if(value instanceof Character)
			return (((Character) value).charValue());
		else if(value instanceof Short)
			return ((Short) value).shortValue();
		else if(value instanceof Integer)
			return ((Integer) value).intValue();
		else if(value instanceof Long)
			return ((Long) value).longValue();
		else if(value instanceof Float)
			return ((Float) value).floatValue();
		else if(value instanceof Double)
			return (float) ((Double) value).doubleValue();
		else
			throw new IllegalArgumentException("Unsupport: " + value);
	}
	/**
	 * @return value translated as double
	 * @throws IllegalArgumentException
	 */
	public double get_double() throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("No value established");
		if(value instanceof Boolean)
			return ((Boolean) value).booleanValue() ? 1 : 0;
		else if(value instanceof Character)
			return (((Character) value).charValue());
		else if(value instanceof Short)
			return ((Short) value).shortValue();
		else if(value instanceof Integer)
			return ((Integer) value).intValue();
		else if(value instanceof Long)
			return ((Long) value).longValue();
		else if(value instanceof Float)
			return ((Float) value).floatValue();
		else if(value instanceof Double)
			return ((Double) value).doubleValue();
		else
			throw new IllegalArgumentException("Unsupport: " + value);
	}

}
