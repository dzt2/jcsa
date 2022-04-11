package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
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

	protected UniConstraintState(CirStatement location, 
			SymbolExpression condition, boolean must_need) throws Exception {
		super(UniAbstractClass.eva_cond, location, condition, 
				SymbolFactory.sym_constant(Boolean.valueOf(must_need)));
	}
	
	/**
	 * @return the constraint being evaluated in this state
	 */
	public SymbolExpression get_condition() { return this.get_loperand(); }
	
	/**
	 * @return whether the condition must be satisfied every time
	 */
	public boolean is_must() { return ((SymbolConstant) this.get_roperand()).get_bool(); }
	
	/**
	 * @return whether the condition must be satisfied at least one time
	 */
	public boolean is_need() { return !((SymbolConstant) this.get_roperand()).get_bool(); }
	
}
