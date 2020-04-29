package com.jcsa.jcparse.lang.symb;

/**
 * argument_list |-- (expression)*
 * @author yukimula
 *
 */
public class SymArgumentList extends SymNode {
	
	protected SymArgumentList() { }
	
	/**
	 * get the number of arguments in the list
	 * @return
	 */
	public int number_of_arguments() { return this.number_of_children(); }
	
	/**
	 * get the kth argument in the list
	 * @param k
	 * @return
	 * @throws IllegalArgumentException
	 */
	public SymExpression get_argument(int k) throws IllegalArgumentException {
		return (SymExpression) this.get_child(k);
	}
	
	/**
	 * add a new argument in the list
	 * @param argument
	 * @throws IllegalArgumentException
	 */
	public void add_argument(SymExpression argument) throws IllegalArgumentException {
		this.add_child(argument);
	}
	
	/**
	 * set the kth argument in the list
	 * @param k
	 * @param argument
	 * @throws IllegalArgumentException
	 */
	public void set_argument(int k, SymExpression argument) throws IllegalArgumentException {
		this.set_child(k, argument);
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("(");
		for(int k = 0; k < this.number_of_arguments(); k++) {
			buffer.append(this.get_argument(k).toString());
			if(k != this.number_of_arguments() - 1) 
				buffer.append(", ");
		}
		buffer.append(")");
		return buffer.toString();
	}
	
}
