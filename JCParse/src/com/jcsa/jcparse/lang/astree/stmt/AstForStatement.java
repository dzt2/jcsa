package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code><b>for</b> ( (decl_stmt | expr_stmt) expr_stmt (expr)? ) stmt</code>
 * 
 * @author yukimula
 */
public interface AstForStatement extends AstStatement {
	public AstKeyword get_for();

	public AstPunctuator get_lparanth();

	public AstStatement get_initializer();

	public AstExpressionStatement get_condition();

	public boolean has_increment();

	public AstExpression get_increment();

	public AstPunctuator get_rparanth();

	public AstStatement get_body();
}
