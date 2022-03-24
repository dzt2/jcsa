package com.jcsa.jcparse.lang.symb;

import java.util.List;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * as the comma expression in symbolic evaluation
 * 
 * @author yukimula
 *
 */
public class SymbolExpressionList extends SymbolSpecialExpression {
	
	/**
	 * It creates a comma-expression used to sequential evaluation
	 * @param data_type
	 * @throws IllegalArgumentException
	 */
	private SymbolExpressionList(CType data_type) throws IllegalArgumentException {
		super(SymbolClass.expression_list, data_type);
	}
	
	/**
	 * It creates an expression-list and take the type of the last element
	 * @param list
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected static SymbolExpressionList create(List<SymbolExpression> list) throws IllegalArgumentException {
		CType type = CBasicTypeImpl.void_type;
		if(list != null && !list.isEmpty()) {
			for(SymbolExpression element : list) {
				type = element.get_data_type();
			}
		}
		
		SymbolExpressionList elist = new SymbolExpressionList(type);
		if(list != null) {
			for(SymbolExpression element : list) {
				elist.add_child(element);
			}
		}
		return elist;
	}
	
	/**
	 * @return the number of expressions in the list
	 */
	public int number_of_expressions() { return this.number_of_children(); }
	
	/**
	 * @param k
	 * @return the kth expression in the list
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolExpression get_expression(int k) throws IndexOutOfBoundsException { 
		return (SymbolExpression) this.get_child(k); 
	}

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolExpressionList(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{ ");
		for(int k = 0; k < this.number_of_expressions(); k++) {
			buffer.append(this.get_expression(k).generate_code(simplified));
			buffer.append("; ");
		}
		buffer.append("}");
		return buffer.toString();
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
}
