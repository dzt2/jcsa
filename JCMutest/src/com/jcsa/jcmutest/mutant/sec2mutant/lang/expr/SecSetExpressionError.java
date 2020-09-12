package com.jcsa.jcmutest.mutant.sec2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymContexts;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecSetExpressionError extends SecExpressionError {

	public SecSetExpressionError(CirStatement statement, 
			CirExpression orig_expression,
			SymExpression muta_expression) throws Exception {
		super(statement, SecKeywords.set_expr, orig_expression);
		this.add_child(new SecExpression(muta_expression));
	}
	
	public SecExpression get_muta_expression() {
		return (SecExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_expression().generate_code() + " -> "
					+ this.get_muta_expression().generate_code() + ")";
	}
	
	@Override
	public SecDescription optimize(SymContexts contexts) throws Exception {
		SymExpression orig_expression = SymEvaluator.evaluate_on(
				get_orig_expression().get_expression(), contexts);
		SymExpression muta_expression = SymEvaluator.evaluate_on(
				get_muta_expression().get_expression(), contexts);
		
		if(muta_expression.equals(orig_expression)) {
			return SecFactory.pass_statement(this.get_location().get_statement());
		}
		else {
			return SecFactory.set_expression(this.get_location().get_statement(), 
					this.get_orig_expression().get_expression().get_cir_source(),
					muta_expression);
		}
	}
	
}
