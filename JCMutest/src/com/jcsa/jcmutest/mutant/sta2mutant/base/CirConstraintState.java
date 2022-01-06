package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * execution [stmt:statement] <== {eva_cond:constraint}
 * 
 * @author yukimula
 *
 */
public class CirConstraintState extends CirConditionState {

	protected CirConstraintState(CirExecution execution, Object condition, boolean value) throws Exception {
		super(execution, CirStateValue.eva_cond(condition, value));
	}
	
	/**
	 * @return the symbolic condition to be evaluated at this state point
	 */
	public SymbolExpression get_condition() { return this.get_uvalue(); }
	
}
