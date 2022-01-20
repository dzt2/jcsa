package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * [expr|dvar|vdef]	<== inc_expr(base, difference)
 * 
 * @author yukimula
 *
 */
public class CirIncreErrorState extends CirDataErrorState {

	protected CirIncreErrorState(CirExecution point, CirStateStore store, 
			SymbolExpression base_value, SymbolExpression difference) throws Exception {
		super(point, store, CirStateValue.inc_expr(base_value, difference));
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
