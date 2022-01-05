package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * xor_expr(execution, expression, [difference]);
 * 
 * @author yukimula
 *
 */
public class CirBixorErrorState extends CirDifferentState {

	protected CirBixorErrorState(CirExpression expression, SymbolExpression difference) throws Exception {
		super(CirStateCategory.xor_expr, expression, difference);
	}

}
