package com.jcsa.jcparse.lang.cirlang.expr.refer;

import com.jcsa.jcparse.lang.cirlang.unit.CirField;

/**
 * <code>
 * 	field_expression |-- expression . field
 * </code>
 * @author yukimula
 *
 */
public interface CirFieldExpression extends CirReferExpression {
	
	/**
	 * @return the body to be de-referenced
	 */
	public CirReferExpression get_body();
	
	/**
	 * @return field used to interpret the body
	 */
	public CirField get_field();
	
}
