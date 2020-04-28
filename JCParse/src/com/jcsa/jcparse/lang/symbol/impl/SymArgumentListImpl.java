package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.symbol.SymArgumentList;
import com.jcsa.jcparse.lang.symbol.SymExpression;

public class SymArgumentListImpl extends SymNodeImpl implements SymArgumentList {

	@Override
	public int number_of_arguments() { return this.number_of_children(); }
	@Override
	public SymExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (SymExpression) this.get_child(k);
	}
	@Override
	public void add_argument(SymExpression argument) throws IllegalArgumentException {
		this.add_child((SymNodeImpl) argument);
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("(");
		int length = this.number_of_arguments();
		for(int k = 0; k < length; k++) {
			buffer.append(this.get_argument(k).toString());
			if(k < length - 1) { buffer.append(", "); }
		}
		buffer.append(")");
		return buffer.toString();
	}
	
}
