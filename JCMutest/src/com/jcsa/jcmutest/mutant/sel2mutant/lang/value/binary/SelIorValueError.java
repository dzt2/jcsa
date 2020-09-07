package com.jcsa.jcmutest.mutant.sel2mutant.lang.value.binary;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelValueType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SelIorValueError extends SelBinaryValueError {

	public SelIorValueError(CirStatement statement, CirExpression orig_expression,
			SymExpression muta_expression) throws Exception {
		super(statement, SelKeywords.ior_value, orig_expression, muta_expression);
	}
	
	@Override
	protected boolean is_valid_type(SelValueType type) {
		switch(type) {
		case cchar:
		case csign:
		case usign:	return true;
		default: 	return false;
		}
	}
	
}
