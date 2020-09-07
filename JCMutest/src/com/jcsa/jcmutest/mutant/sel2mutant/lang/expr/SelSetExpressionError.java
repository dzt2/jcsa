package com.jcsa.jcmutest.mutant.sel2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SelSetExpressionError extends SelExpressionError {

	public SelSetExpressionError(CirStatement statement, 
			CirExpression orig_expression,
			SymExpression muta_expression) throws Exception {
		super(statement, SelKeywords.set_expr, orig_expression);
		this.add_child(new SelExpression(muta_expression));
	}
	
	/**
	 * @return the expression to replace the original one
	 */
	public SelExpression get_muta_expression() {
		return (SelExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_expression().generate_code() + ", "
				+ this.get_muta_expression().generate_code() + ")";
	}

}
