package com.jcsa.jcmutest.mutant.sel2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SelNevExpressionError extends SelExpressionError {

	public SelNevExpressionError(CirStatement statement, 
			CirExpression orig_expression,
			COperator operator) throws Exception {
		super(statement, SelKeywords.nev_expr, orig_expression);
		switch(operator) {
		case negative:
		case bit_not:
		case logic_not:
		case increment:
		case decrement:	this.add_child(new SelOperator(operator)); break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	
	/**
	 * @return the unary operator inserted in the expression
	 */
	public SelOperator get_operator() { 
		return (SelOperator) this.get_child(3); 
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_expression().generate_code() + 
				", " + this.get_operator().generate_code() + ")";
	}
	
}
