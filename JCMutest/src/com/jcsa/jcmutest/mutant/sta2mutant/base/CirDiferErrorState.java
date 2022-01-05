package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * mut_diff(execution, expression, [oval, mval]);
 * 
 * @author yukimula
 *
 */
public class CirDiferErrorState extends CirDataErrorState {

	protected CirDiferErrorState(CirExpression expression, 
			SymbolExpression orig_value,
			SymbolExpression muta_value) throws Exception {
		super(CirStateCategory.mut_diff, expression, orig_value, muta_value);
	}

}
