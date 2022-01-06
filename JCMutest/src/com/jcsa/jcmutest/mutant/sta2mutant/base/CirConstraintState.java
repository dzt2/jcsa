package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * eva_cond(execution, {cond|vcon:statement}, {condition});
 * 
 * @author yukimula
 *
 */
public class CirConstraintState extends CirConditionState {

	protected CirConstraintState(CirStateStore state_store, SymbolExpression value) throws Exception {
		super(CirStateClass.eva_cond, state_store, value);
	}
	
	/**
	 * @return the symbolic condition (constraint) to be evaluated on this state
	 */
	public SymbolExpression get_condition() { return this.get_uvalue(); }
	
}
