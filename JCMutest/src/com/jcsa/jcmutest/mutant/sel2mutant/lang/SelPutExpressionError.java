package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SelPutExpressionError extends SelExpressionError {

	protected SelPutExpressionError(CirStatement statement, 
			CirExpression orig_expression,
			COperator operator, SymExpression operand) throws Exception {
		super(statement, SelKeywords.put_expr, orig_expression);
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
		case logic_or:	break;
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.add_child(new SelOperator(operator));
		this.add_child(new SelExpression(operand));
	}
	
	/**
	 * @return {+, -, *, /, %, &, |, ^, <<, >>, &&, ||}
	 */
	public SelOperator get_operator() { return (SelOperator) this.get_child(3); }
	
	/**
	 * @return operand appended before the original expression
	 */
	public SelExpression get_operand() { return (SelExpression) this.get_child(4); }

	@Override
	protected String generate_parameters() throws Exception {
		return "(" + this.get_orig_expression().generate_code() + 
				", " + this.get_operator().generate_code() + ", " 
				+ this.get_operand().generate_code() + ")";
	}
}
