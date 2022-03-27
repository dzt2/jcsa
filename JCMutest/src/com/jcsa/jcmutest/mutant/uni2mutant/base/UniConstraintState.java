package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	eva_cond(statement; condition, must_need)
 * 	
 * 	@author yukimula
 *
 */
public class UniConstraintState extends UniConditionState {
	
	/**
	 * It creates a state to evaluate symbolic condition
	 * @param _store	the statement location
	 * @param condition	the condition to be evaluated
	 * @param must_need	true (always satisfied needed)
	 * 					false (satisfied at least one)
	 * @throws Exception
	 */
	protected UniConstraintState(UniAbstractStore _store, Object condition, boolean must_need) throws Exception {
		super(UniAbstractClass.eva_cond, _store, 
				SymbolFactory.sym_condition(condition, true),
				SymbolFactory.sym_constant(Boolean.valueOf(must_need)));
	}
	
	/**
	 * @return	the condition to be evaluated
	 */
	public SymbolExpression get_condition() { return this.get_loperand(); }
	
	/**
	 * @return whether the condition must be satified every time
	 */
	public boolean is_must_constraint() {
		return ((SymbolConstant) this.get_roperand()).get_bool().booleanValue();
	}
	
	/**
	 * @return whether the condition is satisfied at least once
	 */
	public boolean is_need_constraint() { return !this.is_must_constraint(); }
	
}
