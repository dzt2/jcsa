package com.jcsa.jcmutest.mutant.sec2mutant.lang.refs;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymComputation;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymContexts;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecUnyReferenceError extends SecReferenceError {

	public SecUnyReferenceError(CirStatement statement, 
			CirExpression orig_expression,
			COperator operator) throws Exception {
		super(statement, SecKeywords.uny_refr, orig_expression);
		switch(operator) {
		case negative:
		case bit_not:
		case logic_not:	
		{
			this.add_child(new SecOperator(operator));
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
	}
	
	public SecOperator get_operator() { return (SecOperator) this.get_child(3); }

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_reference().generate_code() + 
				", " + this.get_operator().generate_code() + ")";
	}

	@Override
	public SecDescription optimize(SymContexts contexts) throws Exception {
		switch(this.get_operator().get_operator()) {
		case negative:
			SymExpression expression = this.get_orig_reference().get_expression();
			expression = SymEvaluator.evaluate_on(expression, contexts);
			if(expression instanceof SymConstant) {
				if(SymComputation.compare((SymConstant) expression, 0)) {
					return SecFactory.pass_statement(this.get_location().get_statement());
				}
			}
		case bit_not:
		case logic_not: return this;
		default: throw new IllegalArgumentException(this.generate_code());
		}
	}
	
}
