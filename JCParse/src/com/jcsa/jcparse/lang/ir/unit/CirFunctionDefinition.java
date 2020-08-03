package com.jcsa.jcparse.lang.ir.unit;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.expr.refer.CirDeclaratorExpression;

/**
 * function_definition |-- type declarator statement_list
 * @author yukimula
 *
 */
public interface CirFunctionDefinition extends CirNode {
	
	/**
	 * @return the type of the function as specifiers
	 */
	public CirType get_specifier();
	
	/**
	 * @return the declaration of the function name
	 */
	public CirDeclaratorExpression get_declarator();
	
	/**
	 * @return the statement body that defines the function
	 */
	public CirStatementList get_body();
	
}
