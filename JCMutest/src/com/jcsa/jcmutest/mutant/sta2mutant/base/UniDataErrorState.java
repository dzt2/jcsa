package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	<code>
 * 		|--	UniDataErrorState			[category, location,  l_operand, r_operand]	<br>
 * 		|--	|--	UniValueErrorState		[set_expr, expr|stmt, orig_expr, muta_expr]	<br>
 * 		|--	|--	UniIncreErrorState		[inc_expr, expr|stmt, orig_expr, different]	<br>
 * 		|--	|--	UniBixorErrorState		[xor_expr, expr|stmt, orig_expr, different]	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public class UniDataErrorState extends UniAbstErrorState {

	protected UniDataErrorState(UniAbstractClass category, 
			CirNode location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(category, location, loperand, roperand);
		if(location instanceof CirExpression || location instanceof CirStatement) { }
		else {
			throw new IllegalArgumentException(location.getClass().getSimpleName());
		}
	}
	
	/**
	 * @return whether this is a used point
	 */
	public boolean is_use() { 
		CirExpression expression = this.get_expression();
		if(expression == null) { return false; }
		else {
			return !StateMutations.is_assigned(expression);
		}
	}
	
	/**
	 * @return whether this is a definition point
	 */
	public boolean is_def() { return !this.is_use(); }
	
	/**
	 * @return the expression if it is taken as usage point
	 */
	public CirExpression get_expression() { 
		if(this.get_location() instanceof CirExpression) {
			return (CirExpression) this.get_location();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return the original value hold by the data state
	 */
	public SymbolExpression get_original_value() { return this.get_loperand(); }

}
