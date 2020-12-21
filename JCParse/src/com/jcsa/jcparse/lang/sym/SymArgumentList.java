package com.jcsa.jcparse.lang.sym;

/**
 * argument_list |-- ((expression)*)
 * @author yukimula
 *
 */
public class SymArgumentList extends SymUnit {
	
	private SymArgumentList() {}
	
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
	
	/**
	 * @param arguments
	 * @return argument_list := ({expression}*)
	 * @throws Exception
	 */
	protected static SymArgumentList create(Iterable<SymExpression> arguments) throws Exception {
		SymArgumentList list = new SymArgumentList();
		for(SymExpression argument : arguments) {
			list.add_child(argument);
		}
		return list;
	}
	
}
