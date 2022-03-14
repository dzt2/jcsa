package com.jcsa.jcparse.lang.symbol;

import java.util.ArrayList;
import java.util.List;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * <code>(init_list --> {expr (, expr)*})</code>
 * 
 * @author yukimula
 *
 */
public class SymbolInitializerList extends SymbolSpecialExpression {

	/**
	 * It creates an empty initializer list
	 * @throws Exception
	 */
	private SymbolInitializerList() throws Exception {
		super(SymbolClass.initializer_list, CBasicTypeImpl.void_type);
	}
	
	/**
	 * @return the number of elements included in list
	 */
	public int number_of_elements() { return this.number_of_children(); }
	
	/**
	 * @param k
	 * @return the kth element in the list
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolExpression get_element(int k) throws IndexOutOfBoundsException { 
		return (SymbolExpression) this.get_child(k); 
	}
	
	/**
	 * @return the elements included in the list
	 */
	public Iterable<SymbolExpression> get_elements() {
		List<SymbolExpression> list = new ArrayList<SymbolExpression>();
		for(SymbolNode child : this.get_children()) {
			list.add((SymbolExpression) child);
		}
		return list;
	}
	
	/**
	 * @param elements
	 * @return {e1, e2, e3, ..., eN}
	 * @throws Exception
	 */
	protected static SymbolInitializerList create(Iterable<SymbolExpression> elements) throws Exception {
		if(elements == null) {
			throw new IllegalArgumentException("Invalid elements: null");
		}
		else {
			SymbolInitializerList list = new SymbolInitializerList();
			for(SymbolExpression element : elements) {
				list.add_child(element);
			}
			return list;
		}
	}

	@Override
	protected SymbolNode new_one() throws Exception { return new SymbolInitializerList(); }

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		for(int k = 0; k < this.number_of_elements(); k++) {
			String element = this.get_element(k).generate_code(simplified);
			buffer.append(element);
			if(k < this.number_of_elements() - 1) { buffer.append(", "); }
		}
		buffer.append("}");
		return buffer.toString();
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
}
