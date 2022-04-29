package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutations;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class AstBlockErrorState extends AstAbstErrorState {

	protected AstBlockErrorState(AstCirNode location, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		super(AstContextClass.set_stmt, location, loperand, roperand);
		if(!location.is_statement_node()) {
			throw new IllegalArgumentException("Invalid: " + location);
		}
	}

	/**
	 * @return whether the statement is executed in original program
	 */
	public boolean is_original_executed() { 
		if(this.get_loperand() instanceof SymbolConstant) {
			return ((SymbolConstant) this.get_loperand()).get_bool();
		}
		else {
			return false;
		}
	}
	
	/**
	 * @return whether the statement is executed in mutation program
	 */
	public boolean is_mutation_executed() { 
		if(this.get_roperand() instanceof SymbolConstant) {
			return ((SymbolConstant) this.get_roperand()).get_bool();
		}
		else {
			return false;
		}
	}
	
	/**
	 * @return whether the block error causes an exception errors
	 */
	public boolean is_trapping_exception() {
		return ContextMutations.has_trap_value(this.get_roperand());
	}
	
}
