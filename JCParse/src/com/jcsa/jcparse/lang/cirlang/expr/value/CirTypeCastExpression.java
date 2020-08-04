package com.jcsa.jcparse.lang.cirlang.expr.value;

import com.jcsa.jcparse.lang.cirlang.expr.CirExpression;
import com.jcsa.jcparse.lang.cirlang.unit.CirType;

/**
 * <code>
 * 	type_cast_expression |-- ( type ) expression
 * </code>
 * @author yukimula
 *
 */
public interface CirTypeCastExpression extends CirValueExpression {
	
	/**
	 * @return the type to cast the operand
	 */
	public CirType get_cast_type();
	
	/**
	 * @return the operand to be casted in
	 */
	public CirExpression get_operand();
	
}
