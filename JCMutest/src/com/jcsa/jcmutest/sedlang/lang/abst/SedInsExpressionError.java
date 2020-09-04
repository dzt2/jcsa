package com.jcsa.jcmutest.sedlang.lang.abst;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.expr.SedExpression;
import com.jcsa.jcmutest.sedlang.lang.token.SedOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * ins_expr(orig_expr, operator, muta_expr)
 * 	defined as: orig_expr :==> muta_expr operator orig_expr
 * 
 * @author yukimula
 *
 */
public class SedInsExpressionError extends SedAbstractValueError {

	public SedInsExpressionError(CirStatement statement, 
			CirExpression orig_expression, COperator 
			operator, SedExpression app_expression) throws Exception {
		super(statement, SedKeywords.ins_expr, orig_expression);
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator");
		else {
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
				this.add_child(new SedOperator(operator));
				this.add_child(app_expression); break;
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
		
	}
	
	public SedOperator get_ins_operator() {
		return (SedOperator) this.get_child(3);
	}
	
	public SedExpression get_ins_operand() {
		return (SedExpression) this.get_child(4);
	}

	@Override
	protected String generate_content() throws Exception {
		return this.get_orig_expression().generate_code() + ", "
				+ this.get_ins_operator().generate_code() + ", "
				+ this.get_ins_operand().generate_code();
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedInsExpressionError(
				this.get_statement().get_cir_statement(),
				this.get_orig_expression().get_cir_expression(),
				this.get_ins_operator().get_operator(),
				this.get_ins_operand());
	}

}
