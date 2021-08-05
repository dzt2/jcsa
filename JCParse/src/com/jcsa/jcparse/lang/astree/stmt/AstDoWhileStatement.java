package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>do_while_stmt |--> <b>do</b> stmt <b>while</b> ( expr ) ;</code>
 *
 * @author yukimula
 *
 */
public interface AstDoWhileStatement extends AstStatement {
	public AstKeyword get_do();

	public AstStatement get_body();

	public AstKeyword get_while();

	public AstPunctuator get_lparanth();

	public AstExpression get_condition();

	public AstPunctuator get_rparanth();

	public AstPunctuator get_semicolon();
}
