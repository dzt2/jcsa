package com.jcsa.jcmutest.selang.lang.abst;

import com.jcsa.jcmutest.selang.SedKeywords;
import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * mut_expr(orig_expr, muta_expr): orig_expr ==> muta_expr
 * @author yukimula
 *
 */
public class SedMutExpressionError extends SedAbstractValueError {

	public SedMutExpressionError(CirStatement statement, 
			CirExpression orig_expression,
			SedExpression muta_expression)
			throws Exception {
		super(statement, SedKeywords.mut_expr, orig_expression);
		this.add_child(muta_expression);
	}
	
	public SedExpression get_muta_expression() {
		return (SedExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_expression().generate_code() + 
				", " + this.get_muta_expression().generate_code() + ")";
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedMutExpressionError(
				this.get_statement().get_cir_statement(),
				this.get_orig_expression().get_cir_expression(),
				this.get_muta_expression());
	}

}
