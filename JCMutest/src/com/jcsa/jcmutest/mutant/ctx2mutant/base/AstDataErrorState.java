package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public abstract class AstDataErrorState extends AstAbstErrorState {

	protected AstDataErrorState(AstContextClass category, AstCirNode location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(category, location, loperand, roperand);
		if(!location.is_expression_node()) {
			throw new IllegalArgumentException("Unsupport: " + location);
		}
	}
	
	/**
	 * @return the expression in which the state mutation is embedded
	 */
	public	AstCirNode	get_expression()	{ return this.get_location(); }
	
	/**
	 * @return the original value hold by the expression 
	 */
	public	SymbolExpression get_original_value()	{ return this.get_loperand(); }
	
}
