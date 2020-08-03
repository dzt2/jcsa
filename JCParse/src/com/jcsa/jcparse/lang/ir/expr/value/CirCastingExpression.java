package com.jcsa.jcparse.lang.ir.expr.value;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.CirValueExpression;
import com.jcsa.jcparse.lang.ir.unit.CirType;

/**
 * casting_expression	|-- (type) expression
 * 
 * @author yukimula
 *
 */
public interface CirCastingExpression extends CirValueExpression {
	
	/**
	 * @return the data type that the expression is casted
	 */
	public CirType get_type();
	
	/**
	 * @return the expression to be casted for its values
	 */
	public CirExpression get_operand();
	
}
