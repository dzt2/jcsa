package com.jcsa.jcparse.lang.sym;

/**
 * argument_list	|--	(expression)+
 * @author dzt2
 *
 */
public class SymArgumentList extends SymNode {
	
	protected SymArgumentList() { }
	
	/**
	 * @return the number of arguments in the list
	 */
	public int number_of_arguments() { return this.number_of_children(); }
	/**
	 * @param k
	 * @return the kth argument in this list
	 * @throws IndexOutOfBoundsException
	 */
	public SymExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (SymExpression) this.get_child(k);
	}

	@Override
	protected SymNode new_self() {
		return new SymArgumentList();
	}

	@Override
	protected String generate_code(boolean ast_style) throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("(");
		for(int k = 0; k < this.number_of_arguments(); k++) {
			buffer.append(this.get_argument(k).generate_code(ast_style));
			if(k < this.number_of_arguments() - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

}
