package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * inc_expr(execution, expression, {difference});
 * 
 * @author yukimula
 *
 */
public class CirIncreErrorState extends CirDifferentState {

	protected CirIncreErrorState(CirExpression expression, 
			SymbolExpression difference) throws Exception {
		super(CirStateCategory.inc_expr, expression, difference);
	}

}
