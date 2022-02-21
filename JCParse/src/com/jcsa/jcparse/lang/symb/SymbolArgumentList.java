package com.jcsa.jcparse.lang.symb;

import java.util.ArrayList;
import java.util.List;

/**
 * arg_list --> (expression {, expression}*)
 * @author yukimula
 *
 */
public class SymbolArgumentList extends SymbolElement {

	private SymbolArgumentList() throws Exception {
		super(SymbolClass.argument_list);
	}
	
	/**
	 * @return the number of the arguments used in the list
	 */
	public int number_of_arguments() { return this.number_of_children(); }
	
	/**
	 * @param k
	 * @return the kth argument defined in the list
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (SymbolExpression) this.get_child(k);
	}
	
	/**
	 * @return the arguments defined in the list
	 */
	public Iterable<SymbolExpression> get_arguments() {
		List<SymbolExpression> list = new ArrayList<SymbolExpression>();
		for(SymbolNode child : this.get_children()) {
			list.add((SymbolExpression) child);
		}
		return list;
	}
	
	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolArgumentList();
	}
	
	@Override
	protected String get_code(boolean simplified) throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("(");
		for(int k = 0; k < this.number_of_arguments(); k++) {
			SymbolExpression argument = this.get_argument(k);
			buffer.append(argument.get_code(simplified));
			if(k < this.number_of_arguments() - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}
	
	@Override
	protected boolean is_refer_type() { return false; }
	
	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param arguments
	 * @return arg_list --> {expression}*
	 * @throws Exception
	 */
	protected static SymbolArgumentList create(Iterable<SymbolExpression> arguments) throws Exception {
		SymbolArgumentList list = new SymbolArgumentList();
		for(SymbolExpression argument : arguments) {
			list.add_child(argument);
		}
		return list;
	}
	
}
