package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code><b>return</b> (expr)? ;</code>
 * 
 * @author yukimula
 *
 */
public interface AstReturnStatement extends AstStatement {
	public AstKeyword get_return();

	public boolean has_expression();

	public AstExpression get_expression();

	public AstPunctuator get_semicolon();
}
