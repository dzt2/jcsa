package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>expr_stmt --> (expr)? ;</code>
 * 
 * @author yukimula
 *
 */
public interface AstExpressionStatement extends AstStatement {
	public boolean has_expression();

	public AstExpression get_expression();

	public AstPunctuator get_semicolon();
}
