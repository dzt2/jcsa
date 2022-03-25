package com.jcsa.jcparse.lang.symbol;

import java.util.List;

/**
 * 	<code>args_list --> (expr {, expr}*)</code>
 * 	@author yukimula
 *
 */
public class SymbolArgumentList extends SymbolElement {
	
	/**
	 * It creates the empty argument list for calling
	 * @throws IllegalArgumentException
	 */
	private SymbolArgumentList() throws IllegalArgumentException {
		super(SymbolClass.argument_list);
	}
	
	/**
	 * @return the number of arguments inserted in this list
	 */
	public int number_of_arguments() { return this.number_of_children(); } 
	
	/**
	 * @param k
	 * @return it fetches the kth argument in the list
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (SymbolExpression) this.get_child(k);
	}

	@Override
	protected SymbolNode new_one() throws Exception { return new SymbolArgumentList(); }

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("(");
		for(int k = 0; k < this.number_of_arguments(); k++) {
			buffer.append(this.get_argument(k).generate_code(simplified));
			if(k < this.number_of_arguments() - 1) { buffer.append(", "); }
		}
		buffer.append(")");
		return buffer.toString();
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param list
	 * @return it creates an argument list with specified input expressions
	 * @throws IllegalArgumentException
	 */
	protected static SymbolArgumentList create(List<SymbolExpression> list) throws IllegalArgumentException {
		SymbolArgumentList alist = new SymbolArgumentList();
		if(list != null && !list.isEmpty()) {
			for(SymbolExpression argument : list) {
				alist.add_child(argument);
			}
		}
		return alist;
	}
	
}
