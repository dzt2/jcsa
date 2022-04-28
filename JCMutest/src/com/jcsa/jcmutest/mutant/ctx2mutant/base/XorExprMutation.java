package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class XorExprMutation extends ContextMutation {
	
	protected XorExprMutation(AstCirNode location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(ContextMutaClass.xor_expr, location, loperand, roperand);
	}
	
	/**
	 * @return the original value hold by the expression
	 */
	public	SymbolExpression	get_orig_value() { return this.get_loperand(); }
	
	/**
	 * @return the different value hold to the expression
	 */
	public	SymbolExpression	get_difference() { return this.get_roperand(); }

}
