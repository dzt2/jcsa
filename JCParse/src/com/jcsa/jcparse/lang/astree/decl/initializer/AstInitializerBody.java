package com.jcsa.jcparse.lang.astree.decl.initializer;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>{ initializer_list (,)? }</code>
 *
 * @author yukimula
 */
public interface AstInitializerBody extends AstExpression {
	public AstPunctuator get_lbrace();

	public AstInitializerList get_initializer_list();

	public boolean has_tail_comma();

	public AstPunctuator get_tail_comma();

	public AstPunctuator get_rbrace();
}
