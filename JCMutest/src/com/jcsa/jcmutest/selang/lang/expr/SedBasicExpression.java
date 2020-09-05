package com.jcsa.jcmutest.selang.lang.expr;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public abstract class SedBasicExpression extends SedExpression {

	public SedBasicExpression(CirExpression 
			cir_expression, CType data_type) throws Exception {
		super(cir_expression, data_type);
	}

}
