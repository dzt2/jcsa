package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * initializer_list |== { expression* }
 * @author yukimula
 *
 */
public class SymbolInitializerList extends SymbolExpression {

	private SymbolInitializerList(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}

	@Override
	protected SymbolNode construct() throws Exception {
		return new SymbolInitializerList(this.get_data_type());
	}

	/**
	 * @return the number of elements in the initializer list
	 */
	public int number_of_elements() { return this.number_of_children(); }

	/**
	 * @param k
	 * @return the kth element in the list
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolExpression get_element(int k) throws IndexOutOfBoundsException { return (SymbolExpression) this.get_child(k); }

	/**
	 * @param data_type
	 * @param elements
	 * @return initializer_list |== { expression* }
	 * @throws Exception
	 */
	protected static SymbolInitializerList create(CType data_type, Iterable<SymbolExpression> elements) throws Exception {
		SymbolInitializerList list = new SymbolInitializerList(data_type);
		for(SymbolExpression element : elements) {
			list.add_child(element);
		}
		return list;
	}

}
