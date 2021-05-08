package com.jcsa.jcmutest.mutant.sym2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class SymStateValueError extends SymValueError {

	protected SymStateValueError(CirExecution execution, CirExpression expression,
			SymbolExpression orig_expression, SymbolExpression muta_expression) throws Exception {
		super(SymInstanceType.stat_error, execution, expression, orig_expression, muta_expression);
	}
	
}
