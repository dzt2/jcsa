package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * field_expression |-- expression . field
 * @author yukimula
 *
 */
public class SymFieldExpression extends SymExpression {
	
	/**
	 * field_expression |-- expression . field
	 * @param data_type
	 * @param body
	 * @param field_name
	 * @throws IllegalArgumentException
	 */
	protected SymFieldExpression(CType data_type, SymExpression body, String field_name) throws IllegalArgumentException {
		super(data_type);
		this.add_child(body);
		this.add_child(new SymField(field_name));
	}
	
	/* getters */
	/**
	 * get the body
	 * @return
	 */
	public SymExpression get_body() { return (SymExpression) this.get_child(0); }
	/**
	 * get the field to extend the body
	 * @return
	 */
	public SymField get_field() { return (SymField) this.get_child(1); }
	
	/* setters */
	/**
	 * set the body of the expression
	 * @param body
	 * @throws IllegalArgumentException
	 */
	public void set_body(SymExpression body) throws IllegalArgumentException {
		this.set_child(0, body);
	}
	
	@Override
	public String toString() {
		return this.get_body().toString() + "." + this.get_field().toString();
	}
}
