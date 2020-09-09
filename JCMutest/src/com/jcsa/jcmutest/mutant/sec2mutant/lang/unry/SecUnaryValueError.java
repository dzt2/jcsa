package com.jcsa.jcmutest.mutant.sec2mutant.lang.unry;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConcreteDescription;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SecUnaryValueError extends SecConcreteDescription {

	public SecUnaryValueError(CirStatement statement, SecKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword, orig_expression);
	}
	
	@Override
	protected String generate_content() throws Exception {
		return ":" + this.get_orig_expression().get_type().generate_code()
				+ "(" + this.get_orig_expression().generate_code() + ")";
	}
	
}
