package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public abstract class AstAbstErrorState extends AstContextState {

	protected AstAbstErrorState(AstContextClass category, 
			AstCirNode location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(category, location, loperand, roperand);
	}

}
