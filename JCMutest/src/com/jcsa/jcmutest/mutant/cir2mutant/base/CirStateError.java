package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

public class CirStateError extends CirAttribute {

	protected CirStateError(CirExecution execution, CirExpression orig_expression,
				SymbolExpression muta_expression) throws IllegalArgumentException {
		super(CirAttributeType.sta_error, execution, orig_expression, muta_expression);
	}

	/**
	 * @return the original expression being mutated with value error
	 */
	public CirExpression get_orig_expression() { return (CirExpression) this.get_location(); }
	/**
	 * @return the symbolic expression to seed the original expression
	 */
	public SymbolExpression get_muta_expression() { return this.get_parameter(); }

	@Override
	public CirAttribute optimize(SymbolProcess context) throws Exception {
		SymbolExpression muta_expression = this.get_muta_expression();
		try {
			muta_expression = SymbolEvaluator.evaluate_on(muta_expression, context);
		}
		catch(ArithmeticException ex) {
			return CirAttribute.new_traps_error(this.get_execution());
		}
		return CirAttribute.new_state_error(get_orig_expression(), muta_expression);
	}

	@Override
	public Boolean evaluate(SymbolProcess context) throws Exception {
		return true;
	}

}
