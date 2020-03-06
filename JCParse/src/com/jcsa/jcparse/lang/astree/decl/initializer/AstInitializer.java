package com.jcsa.jcparse.lang.astree.decl.initializer;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>initializer --> expr | initializer_body </code>
 * 
 * @author yukimula
 */
public interface AstInitializer extends AstNode {
	public boolean is_expression();

	public boolean is_body();

	public AstExpression get_expression();

	public AstInitializerBody get_body();
}
