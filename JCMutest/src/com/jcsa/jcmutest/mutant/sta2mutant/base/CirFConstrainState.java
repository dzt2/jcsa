package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * [stmt:statement] <== cov_cond(false, condition)
 * 
 * @author yukimula
 *
 */
public class CirFConstrainState extends CirConditionState {

	protected CirFConstrainState(CirExecution point, SymbolExpression condition) throws Exception {
		super(point, CirStateValue.cov_cond(false, condition));
	}
	
	/**
	 * @return the symbolic constraint being negated over this point
	 */
	public SymbolExpression get_constraint() { return this.get_roperand(); }
	
}
