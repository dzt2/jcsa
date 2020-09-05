package com.jcsa.jcmutest.sedlang.lang.abst;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.token.SedOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * nev_expr(expr, oprt): expr ==> oprt(expr)
 * @author yukimula
 *
 */
public class SedNevExpressionError extends SedAbstractValueError {

	public SedNevExpressionError(CirStatement statement, 
			CirExpression orig_expression,
			COperator operator) throws Exception {
		super(statement, SedKeywords.nev_expr, orig_expression);
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator");
		else {
			switch(operator) {
			case negative:
			case bit_not:
			case logic_not: 
						this.add_child(new SedOperator(operator)); break;
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
	}
	
	public SedOperator get_nev_operator() {
		return (SedOperator) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "::" + this.get_nev_operator().get_operator() + "("
				+ this.get_orig_expression().generate_code() + ")";
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedNevExpressionError(
				this.get_statement().get_cir_statement(),
				this.get_orig_expression().get_cir_expression(),
				this.get_nev_operator().get_operator());
	}
	
	
}
