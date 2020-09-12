package com.jcsa.jcmutest.mutant.sec2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymFactory;

public abstract class SecExpressionError extends SecStateError {

	public SecExpressionError(CirStatement statement, SecKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword);
		if(orig_expression.get_parent() instanceof CirAssignStatement) {
			CirAssignStatement stmt = 
					(CirAssignStatement) orig_expression.statement_of();
			if(stmt.get_lvalue() == orig_expression)
				throw new IllegalArgumentException("Not righ-value: "
						+ orig_expression.generate_code(true));
		}
		this.add_child(new SecExpression(SymFactory.parse(orig_expression)));
	}
	
	public SecExpression get_orig_expression() {
		return (SecExpression) this.get_child(2);
	}

}
