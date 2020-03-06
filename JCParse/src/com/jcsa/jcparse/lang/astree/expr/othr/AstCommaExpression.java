package com.jcsa.jcparse.lang.astree.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>CommaExpr |--> Expr (, Expr)+</code>
 * 
 * @author yukimula
 *
 */
public interface AstCommaExpression extends AstExpression {
	/**
	 * get the number of arguments in the list
	 * 
	 * @return
	 */
	public int number_of_arguments();

	/**
	 * get the expression for kth argument in the list
	 * 
	 * @param k
	 * @return
	 */
	public AstExpression get_expression(int k);

	/**
	 * get the punctuator to the kth comma
	 * 
	 * @param k
	 * @return
	 */
	public AstPunctuator get_comma(int k);

	/**
	 * append new argument in the tail of the list
	 * 
	 * @param arg
	 * @param comma
	 * @throws Exception
	 *             : arg = null or comma is not comma, or structure is closed
	 */
	public void append(AstPunctuator comma, AstExpression arg) throws Exception;
}
