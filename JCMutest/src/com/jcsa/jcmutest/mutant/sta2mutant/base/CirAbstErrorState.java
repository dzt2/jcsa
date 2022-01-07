package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * <code>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirAbstErrorState extends CirAbstractState {

	protected CirAbstErrorState(CirExecution execution, CirStateStore store, CirStateValue value) throws Exception {
		super(execution, store, value);
	}
	
	/* unary operands */
	/**
	 * @return the original value of the error state
	 */
	public SymbolExpression get_ovalue() { return this.get_value().get_lvalue(); }
	/**
	 * @return
	 */
	public SymbolExpression get_mvalue() { return this.get_value().get_rvalue(); }
	
}
