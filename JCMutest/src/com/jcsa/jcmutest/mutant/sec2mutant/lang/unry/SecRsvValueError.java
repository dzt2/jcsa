package com.jcsa.jcmutest.mutant.sec2mutant.lang.unry;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.SecValueTypes;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecRsvValueError extends SecUnaryValueError {
	
	public SecRsvValueError(CirStatement statement, 
			CirExpression orig_expression) throws Exception {
		super(statement, SecKeywords.rsv_value, orig_expression);
	}
	
	@Override
	protected boolean verify_type(SecValueTypes type) {
		switch(type) {
		case cchar:
		case csign:
		case usign: return true;
		default:	return false;
		}
	}
	
}
