package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class SymExpressionError extends SymValueError {

	protected SymExpressionError(CirExecution execution, CirExpression expression,
			SymbolExpression orig_expression, SymbolExpression muta_expression) throws Exception {
		super(SymInstanceType.expr_error, execution, expression, orig_expression, muta_expression);
	}

}
