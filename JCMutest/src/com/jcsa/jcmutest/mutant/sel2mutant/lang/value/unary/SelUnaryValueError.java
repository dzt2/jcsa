package com.jcsa.jcmutest.mutant.sel2mutant.lang.value.unary;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.SelTypedValueError;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SelUnaryValueError extends SelTypedValueError {

	public SelUnaryValueError(CirStatement statement, SelKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword, orig_expression);
	}

	@Override
	protected String generate_content() throws Exception {
		return "[" + this.get_value_type().generate_code() + "]("
				+ this.get_orig_expression().generate_code() + ")";
	}

}
