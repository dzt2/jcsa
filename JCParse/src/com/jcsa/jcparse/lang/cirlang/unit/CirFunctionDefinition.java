package com.jcsa.jcparse.lang.cirlang.unit;

import com.jcsa.jcparse.lang.cirlang.expr.refer.CirDeclaratorExpression;

/**
 * function_definition |-- type declarator statement_list
 * @author yukimula
 *
 */
public interface CirFunctionDefinition extends CirUnit {
	
	/**
	 * @return the specifier describes the return-type
	 */
	public CirType get_specifiers();
	
	/**
	 * @return the declarator of the function name
	 */
	public CirDeclaratorExpression get_declarator();
	
	/**
	 * @return the statement body of the definition
	 */
	public CirStatementList get_body();
	
}
