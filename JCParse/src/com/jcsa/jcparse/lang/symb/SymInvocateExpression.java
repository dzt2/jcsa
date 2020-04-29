package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * invoc_expression	|-- expression argument_list
 * @author yukimula
 *
 */
public class SymInvocateExpression extends SymExpression {
	
	/* constructor */
	protected SymInvocateExpression(CType data_type, SymExpression function) throws IllegalArgumentException {
		super(data_type);
		this.add_child(function);
		this.add_child(new SymArgumentList());
	}
	
	/* getters */
	/**
	 * get the function that the expression invocates
	 * @return
	 */
	public SymExpression get_function() { return (SymExpression) this.get_child(0); }
	/**
	 * get the argument list used to interpret the invocation
	 * @return
	 */
	public SymArgumentList get_argument_list() { return (SymArgumentList) this.get_child(1); }
	
	/* setter */
	/**
	 * set the function to be invocated
	 * @param function
	 * @throws IllegalArgumentException
	 */
	public void set_function(SymExpression function) throws IllegalArgumentException {
		this.set_child(0, function);
	}
	
	@Override
	public String toString() {
		return this.get_function().toString() + this.get_argument_list().toString();
	}
	
}
