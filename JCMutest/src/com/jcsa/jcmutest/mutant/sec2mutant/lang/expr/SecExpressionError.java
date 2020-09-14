package com.jcsa.jcmutest.mutant.sec2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * <code>
 * 	|--	SecExpressionError				{orig_expr: SecExpression}			<br>
 * 	|--	|--	SecSetExpressionError		set_expr(orig_expr, muta_expr)		<br>
 * 	|--	|--	SecAddExpressionError		add_expr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecInsExpressionError		ins_expr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecUnyExpressionError		uny_expr(orig_expr, oprt)			<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SecExpressionError extends SecStateError {

	public SecExpressionError(CirStatement statement, SecKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword);
		this.add_child(new SecExpression(SymFactory.parse(orig_expression)));
	}
	
	public SecExpression get_orig_expression() {
		return (SecExpression) this.get_child(2);
	}
	
	@Override
	public CirNode get_cir_location() {
		return this.get_orig_expression().get_expression().get_cir_source();
	}
	
}
