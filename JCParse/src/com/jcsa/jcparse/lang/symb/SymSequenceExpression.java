package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * sequence |--	[ (expression)+ ]
 * @author yukimula
 *
 */
public class SymSequenceExpression extends SymExpression {

	protected SymSequenceExpression() {
		super(CBasicTypeImpl.void_type);
	}
	
	/* getters */
	/***
	 * get the number of elements in the sequence
	 * @return
	 */
	public int number_of_elements() { return this.number_of_children(); }
	/**
	 * get the kth element in the sequence
	 * @param k
	 * @return
	 * @throws IllegalArgumentException
	 */
	public SymExpression get_element(int k) throws IllegalArgumentException { 
		return (SymExpression) this.get_child(k); 
	}
	
	/* setters */
	/**
	 * add an element in the sequence tail
	 * @param element
	 * @throws IllegalArgumentException
	 */
	public void add_element(SymExpression element) throws IllegalArgumentException {
		this.add_child(element);
	}
	/**
	 * set the kth element in the sequence
	 * @param k
	 * @param element
	 * @throws IllegalArgumentException
	 */
	public void set_element(int k, SymExpression element) throws IllegalArgumentException {
		this.set_child(k, element);
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		for(int k = 0; k < this.number_of_elements(); k++) {
			buffer.append(this.get_element(k).toString());
			if(k != this.number_of_elements() - 1) 
				buffer.append(", ");
		}
		buffer.append("}");
		return buffer.toString();
	}
	
}
