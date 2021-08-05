package com.jcsa.jcparse.lang.astree.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>SizeofExpr |--> <b>sizeof</b> Expr | ( TypeName )</code>
 *
 * @author yukimula
 *
 */
public interface AstSizeofExpression extends AstExpression {
	/**
	 * get <b>sizeof</b>
	 *
	 * @return
	 */
	public AstKeyword get_sizeof();

	/**
	 * get expression
	 *
	 * @return : null when production is not: <code><b>sizeof</b> Expr</code>
	 */
	public AstExpression get_expression();

	/**
	 * get (
	 *
	 * @return : null when production is not:
	 *         <code><b>sizeof</b> ( TypeName )</code>
	 */
	public AstPunctuator get_lparanth();

	/**
	 * get typename
	 *
	 * @return : null when production is not:
	 *         <code><b>sizeof</b> ( TypeName )</code>
	 */
	public AstTypeName get_typename();

	/**
	 * get )
	 *
	 * @return : null when production is not:
	 *         <code><b>sizeof</b> ( TypeName )</code>
	 */
	public AstPunctuator get_rparanth();

	/**
	 * is the production as: <code><b>sizeof</b> ( TypeName )</code>
	 *
	 * @return
	 */
	public boolean is_typename();

	/**
	 * is the production as: <code><b>sizeof</b> Expr</code>
	 *
	 * @return
	 */
	public boolean is_expression();
}
