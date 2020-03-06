package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>while_stmt --> <b>while</b> ( expr ) stmt </code>
 * 
 * @author yukimula
 *
 */
public interface AstWhileStatement extends AstStatement {
	public AstKeyword get_while();

	public AstPunctuator get_lparanth();

	public AstExpression get_condition();

	public AstPunctuator get_rparanth();

	public AstStatement get_body();
}
