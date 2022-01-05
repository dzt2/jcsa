package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * scp_expr(execution, expression, [difference]);
 * 
 * @author yukimula
 *
 */
public class CirScopeErrorState extends CirDifferentState {

	protected CirScopeErrorState(CirExpression expression, SymbolExpression difference) throws Exception {
		super(CirStateCategory.scp_expr, expression, difference);
	}

}
