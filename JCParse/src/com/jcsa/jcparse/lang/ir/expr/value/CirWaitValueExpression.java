package com.jcsa.jcparse.lang.ir.expr.value;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.CirValueExpression;

/**
 * wait_value_expression	|--	wait expression
 * @author yukimula
 *
 */
public interface CirWaitValueExpression extends CirValueExpression {
	
	/** 
	 * @return the expression as callee to be called
	 */
	public CirExpression get_callee_expression();
	
}
