package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymExpression;
import com.jcsa.jcparse.lang.symbol.SymStatement;

public class SymStatementImpl extends SymNodeImpl implements SymStatement {
	
	private CirExecution execution;
	protected SymStatementImpl() {
		super();
	}
	

	@Override
	public CirExecution get_execution() { return this.execution; }
	@Override
	public void set_execution(CirExecution execution) throws IllegalArgumentException {
		this.execution = execution;
	}

	@Override
	public int number_of_expressions() {
		return this.number_of_children();
	}
	@Override
	public SymExpression get_expression(int k) throws IndexOutOfBoundsException {
		return (SymExpression) this.get_child(k);
	}
	@Override
	public void add_expression(SymExpression expression) throws IllegalArgumentException {
		this.add_child((SymNodeImpl) expression);
	}

}
