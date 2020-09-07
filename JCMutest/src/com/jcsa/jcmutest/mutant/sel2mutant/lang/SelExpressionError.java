package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelExpressionError					{orig_expr: SelExpression}			<br>
 * 	|--	SelSetExpressionError			set_expr(expr, expr)				<br>
 * 	|--	SelInsExpressionError			ins_expr(expr, oprt)				<br>
 * 	|--	SelAddExpressionError			add_expr(expr, oprt, expr)			<br>
 * 	|--	SelPutExpressionError			put_expr(expr, oprt, expr)			<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SelExpressionError extends SelDescription {

	protected SelExpressionError(CirStatement statement, SelKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword);
		this.add_child(new SelExpression(SymFactory.parse(orig_expression)));
	}
	
	/**
	 * @return the original expression being mutated
	 */
	public SelExpression get_orig_expression() {
		return (SelExpression) this.get_child(2);
	}
	
}
