package com.jcsa.jcmutest.mutant.sec2mutant.lang.refs;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymFactory;

public abstract class SecReferenceError extends SecStateError {

	public SecReferenceError(CirStatement statement, SecKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword);
		if(orig_expression instanceof CirReferExpression) {
			this.add_child(new SecExpression(SymFactory.parse(orig_expression)));
		}
		else {
			throw new IllegalArgumentException(
					"Not reference: " + orig_expression.generate_code(true));
		}
	}
	
	public SecExpression get_orig_reference() {
		return (SecExpression) this.get_child(2);
	}
	
}
