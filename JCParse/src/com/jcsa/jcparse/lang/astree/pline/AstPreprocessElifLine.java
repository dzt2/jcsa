package com.jcsa.jcparse.lang.astree.pline;

import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;

/**
 * <code><b># elif</b> const_expr \n</code>
 *
 * @author yukimula
 *
 */
public interface AstPreprocessElifLine extends AstPreprocessLine {
	public AstConstExpression get_condition();
}
