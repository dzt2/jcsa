package com.jcsa.jcparse.lang.astree.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>FunCallExpr |--> Expr ( ArgList? )</code>
 * 
 * @author yukimula
 *
 */
public interface AstFunCallExpression extends AstExpression {
	/**
	 * get the expression to function
	 * 
	 * @return
	 */
	public AstExpression get_function();

	/**
	 * get (
	 * 
	 * @return
	 */
	public AstPunctuator get_lparanth();

	/**
	 * whether there is argument in the call-expr
	 * 
	 * @return
	 */
	public boolean has_argument_list();

	/**
	 * get the argument list
	 * 
	 * @return : null when no arguments in the list
	 */
	public AstArgumentList get_argument_list();

	/**
	 * get )
	 * 
	 * @return
	 */
	public AstPunctuator get_rparanth();
}
