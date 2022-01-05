package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * mut_refr(execution, expression, [oval, mval]);
 * 
 * @author yukimula
 *
 */
public class CirReferErrorState extends CirDataErrorState {

	protected CirReferErrorState(CirExpression expression, 
			SymbolExpression orig_value,
			SymbolExpression muta_value) throws Exception {
		super(CirStateCategory.mut_refr, expression, orig_value, muta_value);
	}

}
