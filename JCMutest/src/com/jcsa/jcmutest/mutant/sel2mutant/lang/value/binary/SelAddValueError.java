package com.jcsa.jcmutest.mutant.sel2mutant.lang.value.binary;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelValueType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SelAddValueError extends SelBinaryValueError {

	public SelAddValueError(CirStatement statement, CirExpression orig_expression,
			SymExpression muta_expression) throws Exception {
		super(statement, SelKeywords.add_value, orig_expression, muta_expression);
	}
	
	@Override
	protected boolean is_valid_type(SelValueType type) {
		switch(type) {
		case cchar:
		case csign:
		case usign:
		case creal:
		case caddr: return true;
		default: 	return false;
		}
	}
	
}
