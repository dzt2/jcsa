package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirConstraintState extends CirConditionState {

	protected CirConstraintState(CirAbstractStore location, 
			Object condition, boolean must_need) throws Exception {
		super(CirAbstractClass.eva_cond, location, 
				SymbolFactory.sym_condition(condition, true), 
				SymbolFactory.sym_constant(Boolean.valueOf(must_need)));
	}
	
	/**
	 * @return the symbolic constraint being evaluated in the statement
	 */
	public SymbolExpression get_constraint() { return this.get_loperand(); }
	
	/**
	 * @return the constraint is satisfied if it is met for every time
	 */
	public boolean is_must() { return ((SymbolConstant) this.get_roperand()).get_bool(); }
	
	/**
	 * @return the constraint is satisfied if it is met for at least once
	 */
	public boolean is_need() { return !((SymbolConstant) this.get_roperand()).get_bool(); }
	
}
