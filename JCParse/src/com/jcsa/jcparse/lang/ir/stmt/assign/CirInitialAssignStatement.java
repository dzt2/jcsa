package com.jcsa.jcparse.lang.ir.stmt.assign;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.refer.CirDeclaratorExpression;

/**
 * init_assign_statement |-- declarator := expression 
 * 										{default_value|expression|initializer_list}
 * @author yukimula
 *
 */
public interface CirInitialAssignStatement extends CirAssignStatement {
	
	/**
	 * @return the variable to be declared and initialized in the statement
	 */
	public CirDeclaratorExpression get_declarator();
	
	/**
	 * @return expression that initializes the declared name
	 */
	public CirExpression get_initializer();
	
}
