package com.jcsa.jcparse.lang.symb;

import java.util.List;

import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

public class SymbolInitializerList extends SymbolSpecialExpression {
	
	/**
	 * It creates an empty initializer list
	 * @throws IllegalArgumentException
	 */
	private SymbolInitializerList() throws IllegalArgumentException {
		super(SymbolClass.initializer_list, CBasicTypeImpl.void_type);
	}
	
	/**
	 * It creates an initializer list with specified list of elements
	 * @param list
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected static SymbolInitializerList create(List<SymbolExpression> list) throws IllegalArgumentException {
		SymbolInitializerList elist = new SymbolInitializerList();
		if(list != null && !list.isEmpty()) {
			for(SymbolExpression element : list) {
				elist.add_child(element);
			}
		}
		return elist;
	}
	
	/**
	 * @return the number of elements included in the initializer list
	 */
	public int number_of_elements() { return this.number_of_children(); }
	
	/**
	 * @param k
	 * @return the kth element in the list
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolExpression get_element(int k) throws IndexOutOfBoundsException { return (SymbolExpression) this.get_child(k); }

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolInitializerList();
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		for(int k = 0; k < this.number_of_elements(); k++) {
			buffer.append(this.get_element(k).generate_code(simplified));
			if(k < this.number_of_elements() - 1) { buffer.append(", "); }
		}
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
}
