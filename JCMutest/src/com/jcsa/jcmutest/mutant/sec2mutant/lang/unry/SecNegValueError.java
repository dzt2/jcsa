package com.jcsa.jcmutest.mutant.sec2mutant.lang.unry;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.SecValueTypes;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecNegValueError extends SecUnaryValueError {
	
	public SecNegValueError(CirStatement statement, 
			CirExpression orig_expression) throws Exception {
		super(statement, SecKeywords.neg_value, orig_expression);
	}
	
	@Override
	protected boolean verify_type(SecValueTypes type) {
		switch(type) {
		case cchar:
		case csign:
		case usign:
		case creal: return true;
		default:	return false;
		}
	}
	
}
