package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * <code>
 * 	|--	CirDifferentState		[category, execution, expression, {difference}]	<br>
 * 	|--	|--	CirIncreErrorState	[inc_expr, execution, expression, {difference}]	<br>
 * 	|--	|--	CirBixorErrorState	[xor_expr, execution, expression, {difference}]	<br>
 * 	|--	|--	CirScopeErrorState	[scp_expr, execution, expression, {difference}]	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirDifferentState extends CirAbstractState {

	protected CirDifferentState(CirStateCategory category, 
			CirExpression expression,
			SymbolExpression difference) throws Exception {
		super(category, expression.execution_of(), expression, 1);
		this.set_parameter(0, CirStateValuation.evaluate(difference));
	}
	
	/**
	 * @return the expression from which the difference is generated
	 */
	public CirExpression get_expression() { return (CirExpression) this.get_location(); }
	/**
	 * @return the difference introduced in the target expression of program
	 */
	public SymbolExpression get_difference() { return this.get_parameter(0); }
	
}
