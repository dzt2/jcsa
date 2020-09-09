package com.jcsa.jcmutest.mutant.sec2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecAddExpressionError extends SecExpressionError {

	public SecAddExpressionError(CirStatement statement, 
			CirExpression orig_expression, COperator
			operator, SymExpression operand) throws Exception {
		super(statement, SecKeywords.add_expr, orig_expression);
		this.add_child(new SecOperator(operator));
		this.add_child(new SecExpression(operand));
		
		switch(operator) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		case logic_and:
		case logic_or:		break;
		default: throw new IllegalArgumentException(operator.toString());
		}
	}
	
	public SecOperator get_operator() {
		return (SecOperator) this.get_child(3);
	}
	
	public SecExpression get_operand() {
		return (SecExpression) this.get_child(4);
	}
	
	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_expression().generate_code() +
				", " + this.get_operator().generate_code() + ", "
				+ this.get_operand().generate_code() + ")";
	}
	
}
