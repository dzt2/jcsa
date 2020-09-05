package com.jcsa.jcmutest.selang.lang.abst;

import com.jcsa.jcmutest.selang.SedKeywords;
import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.lang.tokn.SedOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * app_expr(orig_expr, operator, app_expr):
 *  	orig_expr :==> orig_expr operator app_expr;
 * @author yukimula
 *
 */
public class SedAppExpressionError extends SedAbstractValueError {

	public SedAppExpressionError(CirStatement statement, 
			CirExpression orig_expression, COperator 
			operator, SedExpression app_expression) throws Exception {
		super(statement, SedKeywords.app_expr, orig_expression);
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
	
	public SedOperator get_app_operator() {
		return (SedOperator) this.get_child(3);
	}
	
	public SedExpression get_app_operand() {
		return (SedExpression) this.get_child(4);
	}

	@Override
	protected String generate_content() throws Exception {
		return "::" + this.get_app_operator().get_operator() + "("
				+ this.get_orig_expression().generate_code() + ", "
				+ this.get_app_operand().generate_code() + ")";
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedAppExpressionError(
				this.get_statement().get_cir_statement(),
				this.get_orig_expression().get_cir_expression(),
				this.get_app_operator().get_operator(),
				this.get_app_operand());
	}

}
