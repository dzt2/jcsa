package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SymReferenceError extends SymValueError {

	protected SymReferenceError(CirExecution execution, CirExpression expression,
			SymExpression orig_expression, SymExpression muta_expression) throws Exception {
		super(SymInstanceType.refr_error, execution, expression, orig_expression, muta_expression);
	}

}
