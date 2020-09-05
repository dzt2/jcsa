package com.jcsa.jcmutest.selang.lang.abst;

import com.jcsa.jcmutest.selang.SedKeywords;
import com.jcsa.jcmutest.selang.lang.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	|--	SedAbstractValueError			{orig_expression: SedExpression}	<br>
 * 	|--	|--	SedAppExpressionError		app_expr(expr, oprt, expr)			<br>
 * 	|--	|--	SedInsExpressionError		ins_expr(expr, oprt, expr)			<br>
 * 	|--	|--	SedMutExpressionError		mut_expr(expr, expr)				<br>
 * 	|--	|--	SedNevExpressionError		nev_expr(expr, oprt)				<br>
 * 	@author yukimula
 *
 */
public abstract class SedAbstractValueError extends SedDescription {

	public SedAbstractValueError(CirStatement statement, SedKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword);
		this.add_child(SedFactory.parse(orig_expression));
	}
	
	/**
	 * @return the original expression where the error is seeded
	 */
	public SedExpression get_orig_expression() {
		return (SedExpression) this.get_child(2);
	}

}
