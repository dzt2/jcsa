package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	eva_bool(statement; condition, must_need)
 * 	
 * 	@author yukimula
 *
 */
public class UniConstraintState extends UniConditionState {

	protected UniConstraintState(UniAbstractStore state_store, 
			Object condition, boolean must_need) throws Exception {
		super(UniAbstractClass.eva_bool, state_store, 
				SymbolFactory.sym_condition(condition, true), 
				SymbolFactory.sym_constant(Boolean.valueOf(must_need)));
	}
	
	/**
	 * @return the logical condition being evaluated at the state location
	 */
	public SymbolExpression get_condition() { return this.get_lvalue(); }
	
	/**
	 * @return whether the constraint needs be satisfied every time
	 */
	public boolean is_must() { return ((SymbolConstant) this.get_rvalue()).get_bool(); }
	
	/**
	 * @return whether the constraint needs be satisfied for once at least
	 */
	public boolean is_need() { return !((SymbolConstant) this.get_rvalue()).get_bool(); }
	
}
