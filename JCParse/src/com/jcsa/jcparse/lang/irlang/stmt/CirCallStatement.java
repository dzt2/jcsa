package com.jcsa.jcparse.lang.irlang.stmt;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

/**
 * CirCallStatement	|-- call CirExpression CirArgumentList
 * @author yukimula
 *
 */
public interface CirCallStatement extends CirStatement {
	
	public CirExpression get_function();
	public CirArgumentList get_arguments();
	public void set_function(CirExpression function) throws IllegalArgumentException;
	public void set_arguments(CirArgumentList arguments) throws IllegalArgumentException;
	
}
