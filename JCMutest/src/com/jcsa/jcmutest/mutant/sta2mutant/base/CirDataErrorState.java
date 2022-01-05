package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * <code>
 * 	|--	CirDataErrorState		[category, execution, expression, {oval, mval}]	<br>
 * 	|--	|--	CirDiferErrorState	[mut_diff, execution, expression, {oval, mval}]	<br>
 * 	|--	|--	CirValueErrorState	[mut_expr, execution, expression, {oval, mval}]	<br>
 * 	|--	|--	CirReferErrorState	[mut_refr, execution, reference,  {oval, mval}]	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirDataErrorState extends CirAbstractState {

	protected CirDataErrorState(CirStateCategory category, CirExpression expression,
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		super(category, expression.execution_of(), expression, 2);
		this.set_parameter(0, CirStateValuation.evaluate(orig_value));
		this.set_parameter(1, CirStateValuation.evaluate(muta_value));
	}
	
	/**
	 * @return the expression on which the data state error will be injected
	 */
	public CirExpression get_expression() { return (CirExpression) this.get_location(); }
	/**
	 * @return the original value from which the error is transitioned to mutation
	 */
	public SymbolExpression get_orig_value() { return this.get_parameter(0); }
	/**
	 * @return the mutation value to which the error is transitioned from mutation
	 */
	public SymbolExpression get_muta_value() { return this.get_parameter(1); }
	
}
