package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class AstBixorErrorState extends AstDataErrorState {

	protected AstBixorErrorState(AstCirNode location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(AstContextClass.xor_expr, location, loperand, roperand);
	}
	
	/**
	 * @return the different value of the expression
	 */
	public SymbolExpression get_difference() { return this.get_roperand(); }

}
