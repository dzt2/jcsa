package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * <code>
 * 	|--	CirDataErrorState	[class, execution] [expr|stmt, key] [lvalue, rvalue]<br>
 * 	|--	|--	CirValueErrorState	[set_expr]; [expr|stmt, key]; [o_value, m_value]<br>
 * 	|--	|--	CirIncreErrorState	[inc_expr];	[expr|stmt, key]; [b_value, d_value]<br>
 * 	|--	|--	CirBixorErrorState	[inc_expr];	[expr|stmt, key]; [b_value, d_value]<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirDataErrorState extends CirAbstErrorState {

	protected CirDataErrorState(CirAbstractClass category, CirExecution execution, CirNode location,
			SymbolExpression identifier, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		super(category, execution, location, identifier, loperand, roperand);
	}
	
	/* getters */
	/**
	 * @return whether the location refers to any valid location in program
	 */
	public boolean has_expression() {
		return this.get_location() instanceof CirExpression;
	}
	/**
	 * @return the expression as the use-point or assigned-left-point, or null
	 * 		   for virtually definition point [statement, expr_id]
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
	 * @return whether the state is to mutate a usage point
	 */
	public boolean is_use() {
		CirExpression expression = this.get_expression();
		if(expression == null) {
			return false;
		}
		else {
			return !CirMutations.is_assigned(expression);
		}
	}
	/**
	 * @return whether the state is to mutate a definition point
	 */
	public boolean is_def() {
		CirExpression expression = this.get_expression();
		if(expression == null) {
			return true;
		}
		else {
			return CirMutations.is_assigned(expression);
		}
	}
	/**
	 * @return the data type of the error state
	 */
	public CType get_data_type() {
		CirExpression expression = this.get_expression();
		if(expression != null) {
			return expression.get_data_type();
		}
		else {
			return this.get_identifier().get_data_type();
		}
	}
	/**
	 * @return the original value hold by the state
	 */
	public SymbolExpression get_orig_value() { return this.get_loperand(); }
	
}
