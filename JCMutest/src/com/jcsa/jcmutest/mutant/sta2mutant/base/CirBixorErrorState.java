package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * [expr|dvar|vdef]	<== xor_expr(base, difference)
 * 
 * @author yukimula
 *
 */
public class CirBixorErrorState extends CirDataErrorState {

	protected CirBixorErrorState(CirExecution point, CirStateStore store, 
			SymbolExpression base_value, SymbolExpression difference) throws Exception {
		super(point, store, CirStateValue.xor_expr(base_value, difference));
	}
	

	/**
	 * @return the original value being incremented
	 */
	public SymbolExpression get_base_value() { return this.get_loperand(); }
	/**
	 * @return the value incremented to original one
	 */
	public SymbolExpression get_difference() { return this.get_roperand(); }
	
}
