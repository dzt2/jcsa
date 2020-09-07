package com.jcsa.jcmutest.mutant.sel2mutant.lang.value.unary;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.SelValueType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SelDecValueError extends SelUnaryValueError {

	public SelDecValueError(CirStatement statement, CirExpression orig_expression)
			throws Exception {
		super(statement, SelKeywords.dec_value, orig_expression);
	}
	
	@Override
	protected boolean is_valid_type(SelValueType type) {
		switch(type) {
		case cchar:
		case csign:
		case usign:
		case creal:
		case caddr:	return true;
		default: 	return false;
		}
	}
	
}
