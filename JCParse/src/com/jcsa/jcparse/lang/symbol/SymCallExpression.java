package com.jcsa.jcparse.lang.symbol;

/**
 * call_expression	|--	expression	argument_list
 * @author yukimula
 *
 */
public interface SymCallExpression extends SymExpression {
	
	/**
	 * get the function to be invocated
	 * @return
	 */
	public SymExpression get_function();
	
	/**
	 * get the list of arguments in expression
	 * @return
	 */
	public SymArgumentList get_arguments();
	
}
