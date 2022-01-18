package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirNConstrainState extends CirConditionState {

	protected CirNConstrainState(CirExecution point, SymbolExpression condition) throws Exception {
		super(point, CirStateValue.cov_cond(true, condition));
	}
	
	/**
	 * @return the constraint being statisfied at this point
	 */
	public SymbolExpression get_condition() { return this.get_roperand(); }
	
}
