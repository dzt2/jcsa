package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	<code>
 * 	CirAbstractState
 * 	|--	CirDataErrorState		st_class(stmt|expr; l_operand, r_operand)		<br>
 * 	|--	|--	CirValueErrorState	set_expr(stmt|expr; orig_expr, muta_expr)		<br>
 * 	|--	|--	CirIncreErrorState	inc_expr(stmt|expr; orig_expr, muta_expr)		<br>
 * 	|--	|--	CirBixorErrorState	xor_expr(stmt|expr; orig_expr, muta_expr)		<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class CirDataErrorState extends CirAbstErrorState {

	protected CirDataErrorState(CirAbstractClass category, 
			CirAbstractStore location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(category, location, loperand, roperand);
	}
	
	/**
	 * @return the expression or null if it is statement-defined
	 */
	public CirExpression get_expression() {
		CirNode location = this.get_cir_location();
		if(location instanceof CirExpression) {
			return (CirExpression) location;
		}
		return null;
	}
	/**
	 * @return whether the location is a definition point state
	 */
	public boolean is_def() {
		if(this.get_state_store().is_statement()) {
			return true;
		}
		else {
			CirExpression expression = this.get_expression();
			if(StateMutations.is_assigned(expression)) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	/**
	 * @return whether the location is the usage point of state
	 */
	public boolean is_use() { return !this.is_def(); }
	/**
	 * @return the original value hold by the expression of the state
	 */
	public SymbolExpression get_original_value() { return this.get_loperand(); }
	
}
