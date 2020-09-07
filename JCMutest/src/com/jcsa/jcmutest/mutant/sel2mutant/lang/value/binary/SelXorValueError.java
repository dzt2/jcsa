package com.jcsa.jcmutest.mutant.sel2mutant.lang.value.binary;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.SelValueType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SelXorValueError extends SelBinaryValueError {

	public SelXorValueError(CirStatement statement, CirExpression orig_expression,
			SymExpression muta_expression) throws Exception {
		super(statement, SelKeywords.xor_value, orig_expression, muta_expression);
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
