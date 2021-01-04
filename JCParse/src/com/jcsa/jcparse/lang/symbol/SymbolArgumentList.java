package com.jcsa.jcparse.lang.symbol;

/**
 * argument_list |-- ( expression* )
 * @author yukimula
 *
 */
public class SymbolArgumentList extends SymbolUnit {
	
	private SymbolArgumentList() { }
	
	/**
	 * @return the number of arguments under the list
	 */
	public int number_of_arguments() { return this.number_of_children(); }
	/**
	 * @param k
	 * @return the kth argument expression in the list
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (SymbolExpression) this.get_child(k);
	}

	@Override
	protected SymbolNode construct() throws Exception {
		return new SymbolArgumentList();
	}
	
	/**
	 * @param arguments
	 * @return argument_list |-- ( expression* )
	 * @throws Exception
	 */
	protected static SymbolArgumentList create(Iterable<SymbolExpression> arguments) throws Exception {
		SymbolArgumentList list = new SymbolArgumentList();
		for(SymbolExpression argument : arguments) 
			list.add_child(argument);
		return list;
	}
	
}
