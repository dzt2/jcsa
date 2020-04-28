package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.symbol.SymExpression;
import com.jcsa.jcparse.lang.symbol.SymNode;
import com.jcsa.jcparse.lang.symbol.SymSequence;

public class SymSequenceImpl extends SymExpressionImpl implements SymSequence {

	protected SymSequenceImpl(CType data_type) {
		super(data_type);
	}

	@Override
	public int number_of_elements() { return this.number_of_children(); }
	@Override
	public SymExpression get_element(int k) throws IndexOutOfBoundsException {
		return (SymExpression) this.get_child(k);
	}
	@Override
	public void add_element(SymExpression element) throws IllegalArgumentException {
		this.add_child((SymNodeImpl) element);
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		for(SymNode child : this.get_children()) {
			buffer.append(" ");
			buffer.append(child.toString());
		}
		buffer.append(" ]");
		return buffer.toString();
	}
}
