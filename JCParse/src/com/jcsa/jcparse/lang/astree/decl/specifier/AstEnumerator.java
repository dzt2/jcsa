package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;

/**
 * <code>enumerator --> identifier (=, const_expr)?</code>
 *
 * @author yukimula
 */
public interface AstEnumerator extends AstNode {
	/**
	 * get the name of enumerator
	 *
	 * @return
	 */
	public AstName get_name();

	/**
	 * get =
	 *
	 * @return : null when expression is not defined in enumerator
	 */
	public AstPunctuator get_assign();

	/**
	 * get the expression to assign enum
	 *
	 * @return : null when expression is not defined in enumerator
	 */
	public AstConstExpression get_expression();

	/**
	 * whether expression defined in enumerator
	 *
	 * @return
	 */
	public boolean has_expression();
}
