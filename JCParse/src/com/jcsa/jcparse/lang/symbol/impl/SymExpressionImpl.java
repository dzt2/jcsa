package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.symbol.SymExpression;
import com.jcsa.jcparse.lang.symbol.SymNode;
import com.jcsa.jcparse.lang.symbol.SymStatement;

public abstract class SymExpressionImpl extends SymNodeImpl implements SymExpression {
	
	private CType data_type;
	protected SymExpressionImpl(CType data_type) {
		super();
		this.data_type = data_type;
	}

	@Override
	public CType get_data_type() { return this.data_type; }

	@Override
	public SymStatement get_statement() {
		SymNode parent = this.get_parent();
		while(parent != null) {
			if(parent instanceof SymStatement)
				return (SymStatement) parent;
			else parent = parent.get_parent();
		}
		return null;
	}

}
