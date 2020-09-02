package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.abs;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * add_expr(orig_expr, add_oprt, add_expr) :
 * 	orig_expr := orig_expr add_oprt add_expr
 * @author yukimula
 *
 */
public class SedAddExpressionError extends SedAbstExpressionError {

	public SedAddExpressionError(CirStatement location, 
			SedExpression orig_expression, COperator operator,
			SedExpression add_expression) {
		super(location, orig_expression);
		this.add_child(new SedOperator(operator));
		this.add_child(add_expression);
	}
	
	/**
	 * @return binary operator being appended following original expression
	 */
	public SedOperator get_add_operator() {
		return (SedOperator) this.get_child(2);
	}
	
	/**
	 * @return the expression being appended following binary operator being added
	 */
	public SedExpression get_add_operand() {
		return (SedExpression) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
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
