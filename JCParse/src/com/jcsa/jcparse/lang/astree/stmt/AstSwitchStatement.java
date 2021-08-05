package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>switch_stmt --> <b>switch</b> ( expr ) stmt </code>
 *
 * @author yukimula
 */
public interface AstSwitchStatement extends AstStatement {
	public AstKeyword get_switch();

	public AstPunctuator get_lparanth();

	public AstExpression get_condition();

	public AstPunctuator get_rparanth();

	public AstStatement get_body();
}
