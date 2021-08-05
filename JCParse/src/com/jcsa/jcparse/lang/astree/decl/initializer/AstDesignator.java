package com.jcsa.jcparse.lang.astree.decl.initializer;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;

/**
 * <code>designator --> [ const_expr ] | . field</code>
 *
 * @author yukimula
 *
 */
public interface AstDesignator extends AstNode {
	public boolean is_dimension();

	public boolean is_field();

	public AstPunctuator get_lbracket();

	public AstConstExpression get_dimension_expression();

	public AstPunctuator get_rbracket();

	public AstPunctuator get_dot();

	public AstField get_field();
}
