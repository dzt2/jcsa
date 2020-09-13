package com.jcsa.jcmutest.mutant.sec2mutant.lang.refs;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecInsReferenceError extends SecReferenceError {

	public SecInsReferenceError(CirStatement statement, 
			CirExpression orig_expression, COperator 
			operator, SymExpression operand) throws Exception {
		super(statement, SecKeywords.ins_refr, orig_expression);
		switch(operator) {
		case arith_sub:
		case arith_mod:
		case left_shift:
		case righ_shift:
		{
			this.add_child(new SecOperator(operator));
			this.add_child(new SecExpression(operand));
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
	}
	
	public SecOperator get_operator() { return (SecOperator) this.get_child(3); }
	
	public SecExpression get_operand() { return (SecExpression) this.get_child(4); }

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_reference().generate_code() + ", "
				+ this.get_operator().generate_code() + ", "
				+ this.get_operand().generate_code() + ")";
	}
	
}
