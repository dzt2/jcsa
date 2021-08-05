package com.jcsa.jcparse.lang.astree.pline;

import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;

/**
 * <code><b>#if</b> const_expr \n</code>
 *
 * @author yukimula
 *
 */
public interface AstPreprocessIfLine extends AstPreprocessLine {
	/**
	 * get the condition of #if
	 *
	 * @return
	 */
	public AstConstExpression get_if_condition();
}
