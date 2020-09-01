package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * add_expr(expr, oprt, expr)
 * @author dzt2
 *
 */
public class SedAddExpressionError extends SedExpressionError {

	public SedAddExpressionError(CirStatement location, 
			SedExpression orig_expression, 
			COperator add_operator, SedExpression add_operand) {
		super(location, orig_expression);
		this.add_child(new SedOperator(add_operator));
		this.add_child(add_operand);
	}
	
	/**
	 * @return the operator being added in the tail of mutation
	 */
	public SedOperator get_add_operator() {
		return (SedOperator) this.get_child(2);
	}
	
	/**
	 * @return the operand being added in the tail of mutation
	 */
	public SedExpression get_add_operand() {
		return (SedExpression) this.get_child(3);
	}

	@Override
	public String generate_content() throws Exception {
		return "add_expr(" + this.get_orig_expression().generate_code()
				+ ", " + this.get_add_operator().generate_code() + ", "
				+ this.get_add_operand().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedAddExpressionError(
				this.get_location().get_cir_statement(),
				this.get_orig_expression(),
				this.get_add_operator().get_operator(),
				this.get_add_operand());
	}

}
