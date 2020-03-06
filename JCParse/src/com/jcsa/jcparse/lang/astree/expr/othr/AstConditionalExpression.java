package com.jcsa.jcparse.lang.astree.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>CondExpr --> Expr ? Expr : Expr</code>
 * 
 * @author yukimula
 */
public interface AstConditionalExpression extends AstExpression {
	public AstExpression get_condition();

	/**
	 * get ?
	 * 
	 * @return
	 */
	public AstPunctuator get_question();

	public AstExpression get_true_branch();

	/**
	 * get :
	 * 
	 * @return
	 */
	public AstPunctuator get_colon();

	public AstExpression get_false_branch();
}
