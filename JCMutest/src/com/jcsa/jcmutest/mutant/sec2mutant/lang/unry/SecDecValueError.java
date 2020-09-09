package com.jcsa.jcmutest.mutant.sec2mutant.lang.unry;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.SecValueTypes;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecDecValueError extends SecUnaryValueError {

	public SecDecValueError(CirStatement statement, CirExpression orig_expression)
			throws Exception {
		super(statement, SecKeywords.dec_value, orig_expression);
	}

	@Override
	protected boolean verify_type(SecValueTypes type) {
		switch(type) {
		case cchar:
		case csign:
		case usign:
		case creal:
		case caddr:	return true;
		default:	return false;
		}
	}
	
}
