package com.jcsa.jcparse.lang.symb;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymbolInitializerList extends SymbolExpression {

	private SymbolInitializerList(CType type) throws Exception {
		super(SymbolClass.initializer_list, type);
	}
	
	/**
	 * @return the number of elements defined in the list
	 */
	public int number_of_elements() { return this.number_of_children(); }
	
	/**
	 * @param k
	 * @return the kth element defined in the list
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolExpression get_element(int k) throws IndexOutOfBoundsException {
		return (SymbolExpression) this.get_child(k);
	}
	
	/**
	 * @return the list of elements defined in this node
	 */
	public Iterable<SymbolExpression> get_elements() {
		List<SymbolExpression> list = new ArrayList<SymbolExpression>();
		for(int k = 0; k < this.number_of_elements(); k++) {
			list.add(this.get_element(k));
		}
		return list;
	}

	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolInitializerList(this.get_data_type());
	}

	@Override
	protected String get_code(boolean simplified) throws Exception {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < this.number_of_elements(); k++) {
			buffer.append(this.get_element(k).get_code(simplified));
			if(k < this.number_of_elements() - 1) {
				buffer.append(", ");
			}
		}
		return buffer.toString();
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type
	 * @param elements
	 * @return
	 * @throws Exception
	 */
	protected static SymbolInitializerList create(CType type, 
			Iterable<SymbolExpression> elements) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(elements == null) {
			throw new IllegalArgumentException("Invalid elements: null");
		}
		else {
			SymbolInitializerList list = new SymbolInitializerList(type);
			for(SymbolExpression element : elements) {
				list.add_child(element);
			}
			return list;
		}
	}
	
}
