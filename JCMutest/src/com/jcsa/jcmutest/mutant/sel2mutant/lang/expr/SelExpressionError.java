package com.jcsa.jcmutest.mutant.sel2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelDescription;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelExpressionError				{orig_expression: SelExpression}		<br>
 * 	|--	SelNebExpressionError		nev_expr(expr, oprt)					<br>
 * 	|--	SelSetExpressionError		set_expr(expr, expr)					<br>
 * 	|--	SelAddExpressionError		add_expr(expr, oprt, expr)				<br>
 * 	|--	SelInsExpressionError		ins_expr(expr, oprt, expr)				<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SelExpressionError extends SelDescription {

	public SelExpressionError(CirStatement statement, SelKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword);
		this.add_child(new SelExpression(SymFactory.parse(orig_expression)));
	}
	
	/**
	 * @return the expression where the error is seeded
	 */
	public SelExpression get_orig_expression() {
		return (SelExpression) this.get_child(2);
	}
	
}
