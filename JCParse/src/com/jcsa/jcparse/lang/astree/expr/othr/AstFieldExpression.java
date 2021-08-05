package com.jcsa.jcparse.lang.astree.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>FieldExpr |--> Expr (., ->) Field</code>
 *
 * @author yukimula
 *
 */
public interface AstFieldExpression extends AstExpression {
	/**
	 * get expression as struct-body
	 *
	 * @return
	 */
	public AstExpression get_body();

	/**
	 * get operator (. or ->)
	 *
	 * @return
	 */
	public AstPunctuator get_operator();

	/**
	 * get field
	 *
	 * @return
	 */
	public AstField get_field();
}
