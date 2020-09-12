package com.jcsa.jcmutest.mutant.sec2mutant.lang.refs;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymContexts;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecInsReferenceError extends SecReferenceError {

	public SecInsReferenceError(CirStatement statement, 
			CirExpression orig_expression, COperator 
			operator, SymExpression operand) throws Exception {
		super(statement, SecKeywords.ins_refr, orig_expression);
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

	
	@Override
	public SecDescription optimize(SymContexts contexts) throws Exception {
		CirStatement statement = this.get_location().get_statement();
		CirExpression orig_expression = this.
				get_orig_reference().get_expression().get_cir_source();
		SymExpression operand = SymEvaluator.evaluate_on(
				this.get_operand().get_expression(), contexts);
		COperator operator = this.get_operator().get_operator();
		return new SecInsReferenceError(statement, orig_expression, operator, operand);
	}
	
}
