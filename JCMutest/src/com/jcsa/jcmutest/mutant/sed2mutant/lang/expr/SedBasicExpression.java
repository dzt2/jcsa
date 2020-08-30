package com.jcsa.jcmutest.mutant.sed2mutant.lang.expr;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

public abstract class SedBasicExpression extends SedExpression {

	public SedBasicExpression(CirNode cir_source, CType data_type) {
		super(cir_source, data_type);
	}

}
