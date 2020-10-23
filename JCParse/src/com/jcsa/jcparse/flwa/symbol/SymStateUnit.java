package com.jcsa.jcparse.flwa.symbol;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * Each unit maintains the value pair of [expression, value] in the program.
 * 
 * @author yukimula
 *
 */
public class SymStateUnit {
	
	/* definitions */
	/** the expression of which value is evaluated **/
	private CirExpression location;
	/** the value generated and evaluated from the location **/
	private SymExpression value;
	/**
	 * create the pair of [expression, value]
	 * @param location
	 * @param value
	 * @throws Exception
	 */
	protected SymStateUnit(CirExpression location) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else {
			this.location = location;
			this.value = SymFactory.parse(this.location);
		}
	}
	
	/* getters */
	/**
	 * @return the expression of which value is evaluated
	 */
	public CirExpression get_location() { return this.location; }
	/**
	 * @return the value generated and evaluated from the location
	 */
	public SymExpression get_value() { return this.value; }
	/**
	 * update the value of the expression at this state node
	 * @param value
	 */
	protected void set_value(SymExpression value) { this.value = value; }
	
}
