package com.jcsa.jcparse.lang.astree.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>CastExpr --> ( TypeName ) Expr </code>
 * 
 * @author yukimula
 *
 */
public interface AstCastExpression extends AstExpression {
	/**
	 * get (
	 * 
	 * @return
	 */
	public AstPunctuator get_lparanth();

	/**
	 * get typename for which the expression is casted
	 * 
	 * @return
	 */
	public AstTypeName get_typename();

	/**
	 * get )
	 * 
	 * @return
	 */
	public AstPunctuator get_rparanth();

	/**
	 * get the expression to be casted
	 * 
	 * @return
	 */
	public AstExpression get_expression();
}
