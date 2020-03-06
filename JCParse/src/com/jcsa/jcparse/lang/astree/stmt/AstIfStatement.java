package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>if_stmt --> <b>if</b> ( expr ) stmt (<b>else</b> stmt)?</code>
 * 
 * @author yukimula
 *
 */
public interface AstIfStatement extends AstStatement {
	public AstKeyword get_if();

	public AstPunctuator get_lparanth();

	public AstExpression get_condition();

	public AstPunctuator get_rparanth();

	public AstStatement get_true_branch();

	public AstKeyword get_else();

	public AstStatement get_false_branch();

	public boolean has_else();
}
