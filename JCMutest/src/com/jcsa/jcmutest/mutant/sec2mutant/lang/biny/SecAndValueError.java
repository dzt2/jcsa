package com.jcsa.jcmutest.mutant.sec2mutant.lang.biny;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.SecValueTypes;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecAndValueError extends SecBinaryValueError {

	public SecAndValueError(CirStatement statement, CirExpression orig_expression,
			SymExpression muta_expression) throws Exception {
		super(statement, SecKeywords.and_value, orig_expression, muta_expression);
	}

	@Override
	protected boolean verify_type(SecValueTypes type) {
		switch(type) {
		case cchar:
		case csign:
		case usign:	return true;
		default:	return false;
		}
	}
	
}
