package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * [stmt:statement] <== cov_cond(true, condition)
 * 
 * @author yukimula
 *
 */
public class CirTConstrainState extends CirConditionState {

	protected CirTConstrainState(CirExecution point, SymbolExpression condition) throws Exception {
		super(point, CirStateValue.cov_cond(true, condition));
	}
	
	/**
	 * @return the symbolic constraint being evaluated for true at this point
	 */
	public SymbolExpression get_constraint() { return this.get_roperand(); }
	
}
