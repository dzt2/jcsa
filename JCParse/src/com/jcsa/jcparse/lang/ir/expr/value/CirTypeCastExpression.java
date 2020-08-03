package com.jcsa.jcparse.lang.ir.expr.value;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.unit.CirType;

/**
 * cast_expression |-- ( type ) expression
 * @author yukimula
 *
 */
public interface CirTypeCastExpression extends CirValueExpression {
	
	/**
	 * @return the data type to be casted
	 */
	public CirType get_cast_type();
	
	/**
	 * @return the operand to be casted
	 */
	public CirExpression get_operand();
	
}
