package com.jcsa.jcparse.lang.ir.stmt.assign;

import com.jcsa.jcparse.lang.ir.expr.refer.CirDeclaratorExpression;
import com.jcsa.jcparse.lang.ir.expr.value.CirDefaultValueExpression;

/**
 * declarator := default_value
 * @author yukimula
 *
 */
public interface CirParameterAssignStatement extends CirAssignStatement {
	
	/**
	 * @return it declares the parameter to be initialized
	 */
	public CirDeclaratorExpression get_lreference();
	
	/**
	 * @return the default-value that represents the argument
	 * 			to be assigned to the declaration reference.
	 */
	public CirDefaultValueExpression get_rexpression();
	
}
