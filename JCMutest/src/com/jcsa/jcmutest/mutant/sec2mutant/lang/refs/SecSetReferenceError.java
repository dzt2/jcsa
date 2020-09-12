package com.jcsa.jcmutest.mutant.sec2mutant.lang.refs;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecSetReferenceError extends SecReferenceError {

	public SecSetReferenceError(CirStatement statement, 
			CirExpression orig_expression,
			SymExpression muta_expression) throws Exception {
		super(statement, SecKeywords.set_refr, orig_expression);
		this.add_child(new SecExpression(muta_expression));
	}
	
	public SecExpression get_muta_expression() {
		return (SecExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_reference().generate_code() + ", "
				+ this.get_muta_expression().generate_code() + ")";
	}
	
}
