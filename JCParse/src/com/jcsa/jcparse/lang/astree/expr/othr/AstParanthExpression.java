package com.jcsa.jcparse.lang.astree.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>ParanthExpr |--> ( Expr )</code>
 * 
 * @author yukimula
 */
public interface AstParanthExpression extends AstExpression {
	/**
	 * get the left paranth
	 * 
	 * @return
	 */
	public AstPunctuator get_lparanth();

	/**
	 * get the sub-expression within ( and )
	 * 
	 * @return
	 */
	public AstExpression get_sub_expression();

	/**
	 * get the right paranth
	 * 
	 * @return
	 */
	public AstPunctuator get_rparanth();
}
