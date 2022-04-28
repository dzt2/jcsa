package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class EvaCondMutation extends ContextMutation {

	protected EvaCondMutation(AstCirNode location, Object condition, boolean must_need) throws Exception {
		super(ContextMutaClass.eva_cond, location, 
				SymbolFactory.sym_condition(condition, true), 
				SymbolFactory.sym_constant(must_need));
	}
	
	/**
	 * @return the constraint to be evaluated at this point
	 */
	public	SymbolExpression	get_condition()	{ return this.get_loperand(); }
	
	/**
	 * @return whether the condition needs be satisfied every time
	 */
	public	boolean	ist_must()	{ return ((SymbolConstant) this.get_roperand()).get_bool(); }
	
}
