package com.jcsa.jcmutest.mutant.sel2mutant.lang.value.unary;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.SelValueType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SelChgValueError extends SelUnaryValueError {

	public SelChgValueError(CirStatement statement, 
			CirExpression orig_expression) throws Exception {
		super(statement, SelKeywords.chg_value, orig_expression);
	}
	
	@Override
	protected boolean is_valid_type(SelValueType type) {
		switch(type) {
		case cbool:
		case cchar:
		case csign:
		case usign:
		case creal:
		case caddr:
		case cbody:	return true;
		default: 	return false;
		}
	}

}
