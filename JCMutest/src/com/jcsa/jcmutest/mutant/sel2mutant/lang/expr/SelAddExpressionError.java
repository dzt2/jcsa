package com.jcsa.jcmutest.mutant.sel2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelExpression;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

/**
 * operand(x, o, y): x ==> x o y
 * @author yukimula
 *
 */
public class SelAddExpressionError extends SelExpressionError {
	
	public SelAddExpressionError(CirStatement statement, 
			CirExpression orig_expression, COperator 
			operator, SymExpression operand) throws Exception {
		super(statement, SelKeywords.add_expr, orig_expression);
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
		case logic_or:	
			this.add_child(new SelOperator(operator));
			this.add_child(new SelExpression(operand));
			break;
		default: throw new IllegalArgumentException("Invalid operator");
		}
	}
	
	/**
	 * @return {+, -, *, /, %, &, |, ^, <<, >>, &&, ||}
	 */
	public SelOperator get_operator() { 
		return (SelOperator) this.get_child(3);
	}
	
	/**
	 * @return the operand added after the original one
	 */
	public SelExpression get_operand() {
		return (SelExpression) this.get_child(4);
	}
	
	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_expression().generate_code() +
				", " + this.get_operator().generate_code() + ", "
				+ this.get_operand().generate_code() + ")";
	}
	
}
