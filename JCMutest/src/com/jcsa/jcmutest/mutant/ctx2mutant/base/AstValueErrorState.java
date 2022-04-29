package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class AstValueErrorState extends AstAbstErrorState {

	protected AstValueErrorState(AstCirNode location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(AstContextClass.set_expr, location, loperand, roperand);
	}
	

	/**
	 * @return the expression in which the state mutation is embedded
	 */
	public	AstCirNode	get_expression()	{ return this.get_location(); }
	
	/**
	 * @return the original value hold by the expression 
	 */
	public	SymbolExpression get_original_value()	{ return this.get_loperand(); }
	
	/**
	 * @return the mutated value of the expression
	 */
	public SymbolExpression get_mutation_value() { return this.get_roperand(); }
	
}
