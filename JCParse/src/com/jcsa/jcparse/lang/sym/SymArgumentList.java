package com.jcsa.jcparse.lang.sym;

/**
 * argument_list |-- ((expression)*)
 * @author yukimula
 *
 */
public class SymArgumentList extends SymUnit {
	
	protected SymArgumentList() {}
	
	/**
	 * @return the number of arguments in the list
	 */
	public int number_of_arguments() { return this.number_of_children(); }
	/**
	 * @param k
	 * @return the kth argument in list
	 * @throws IndexOutOfBoundsException
	 */
	public SymExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (SymExpression) this.get_child(k);
	}

	@Override
	protected SymNode construct() throws Exception {
		return new SymArgumentList();
	}

	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("(");
		for(int k = 0; k < this.number_of_arguments(); k++) {
			buffer.append(this.get_argument(k).generate_code());
			if(k < this.number_of_arguments() - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

}
