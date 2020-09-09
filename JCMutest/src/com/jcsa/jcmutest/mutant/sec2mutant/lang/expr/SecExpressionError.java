package com.jcsa.jcmutest.mutant.sec2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecAbstractDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymFactory;

public abstract class SecExpressionError extends SecAbstractDescription {

	public SecExpressionError(CirStatement statement, SecKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword);
		this.add_child(new SecExpression(SymFactory.parse(orig_expression)));
	}
	
	public SecExpression get_orig_expression() {
		return (SecExpression) this.get_child(2);
	}

}
