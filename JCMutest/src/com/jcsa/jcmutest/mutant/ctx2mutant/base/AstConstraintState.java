package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class AstConstraintState extends AstConditionState {

	protected AstConstraintState(AstCirNode location, SymbolExpression condition, boolean must_need) throws Exception {
		super(AstContextClass.eva_cond, location, condition, 
				SymbolFactory.sym_constant(Boolean.valueOf(must_need)));
		if(!location.is_statement_node()) {
			throw new IllegalArgumentException("Invalid location: null");
		}
	}

	/**
	 * @return the constraint to be evaluated at this point
	 */
	public	SymbolExpression	get_condition()	{ return this.get_loperand(); }
	
	/**
	 * @return whether the condition needs be satisfied every time
	 */
	public	boolean	is_must()	{ return ((SymbolConstant) this.get_roperand()).get_bool(); }
	
	/**
	 * @return whether the condition needs be satisfied at least once
	 */
	public	boolean	is_need()	{ return !((SymbolConstant) this.get_roperand()).get_bool(); }
	
}
