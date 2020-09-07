package com.jcsa.jcmutest.mutant.sel2mutant.lang.value.unary;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelValueType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SelExtValueError extends SelUnaryValueError {

	public SelExtValueError(CirStatement statement, CirExpression orig_expression)
			throws Exception {
		super(statement, SelKeywords.ext_value, orig_expression);
	}
	
	@Override
	protected boolean is_valid_type(SelValueType type) {
		switch(type) {
		case cchar:
		case csign:
		case usign:
		case creal:	return true;
		default: 	return false;
		}
	}
	
}
