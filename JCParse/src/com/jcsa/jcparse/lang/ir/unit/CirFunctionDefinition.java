package com.jcsa.jcparse.lang.ir.unit;

import com.jcsa.jcparse.lang.ir.expr.refer.CirDeclaratorReference;

/**
 * function_definition |-- type declarator statement_list
 * @author yukimula
 *
 */
public interface CirFunctionDefinition extends CirUnit {
	
	/**
	 * @return specifiers that defines the function type
	 */
	public CirType get_specifiers();
	
	/**
	 * @return the declarator of the function name
	 */
	public CirDeclaratorReference get_declarator();
	
	/**
	 * @return the statement body that defines the function
	 */
	public CirStatementList get_body();
	
}
