package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	eva_cond(statement; condition, must_need);
 * 	
 *	@author yukimula
 *
 */
public class AbsConstraintState extends AbsConditionalState {

	protected AbsConstraintState(AbsExecutionStore _store, 
			SymbolExpression condition,
			boolean must_need) throws Exception {
		super(AbsExecutionClass.eva_cond, _store, condition, 
				SymbolFactory.sym_constant(Boolean.valueOf(must_need)));
	}
	
	/**
	 * @return the symbolic constraint for evaluating
	 */
	public SymbolExpression get_condition() { return this.get_loperand(); }
	
	/**
	 * @return whether the constraint must be satisfied every time of coverage
	 */
	public boolean is_must() { return ((SymbolConstant) this.get_roperand()).get_bool(); }
	
	/**
	 * @return whether the constraint needs to be satisfied at least once.
	 */
	public boolean is_need() { return !((SymbolConstant) this.get_roperand()).get_bool(); }
	
}
