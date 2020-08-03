package com.jcsa.jcparse.lang.ir.expr.refer;

import com.jcsa.jcparse.lang.ir.unit.CirField;

/**
 * field_expression |-- refer_expression . field
 * @author yukimula
 *
 */
public interface CirFieldExpression extends CirReferExpression {
	
	/**
	 * @return the reference of the body
	 */
	public CirReferExpression get_body();
	
	/**
	 * @return the field to expend the body
	 */
	public CirField get_field();
	
}
