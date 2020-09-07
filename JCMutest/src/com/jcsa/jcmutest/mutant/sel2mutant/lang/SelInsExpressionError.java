package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SelInsExpressionError extends SelExpressionError {

	protected SelInsExpressionError(CirStatement statement, 
			CirExpression orig_expression,
			COperator ins_operator) throws Exception {
		super(statement, SelKeywords.ins_expr, orig_expression);
		switch(ins_operator) {
		case negative:
		case bit_not:
		case logic_not:
		case increment:
		case decrement:	this.add_child(new SelOperator(ins_operator)); break;
		default: throw new IllegalArgumentException(ins_operator.toString());
		}
	}
	
	/**
	 * @return {-, ~, !, ++, --}
	 */
	public SelOperator get_ins_operator() {
		return (SelOperator) this.get_child(3);
	}

	@Override
	protected String generate_parameters() throws Exception {
		return "(" + this.get_orig_expression().generate_code() +
				", " + this.get_ins_operator().generate_code() + ")";
	}

}
