package com.jcsa.jcparse.lang.ir.expr.refer;

import com.jcsa.jcparse.lang.ir.expr.CirReferExpression;
import com.jcsa.jcparse.lang.ir.unit.CirField;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * field_expression	|--	refer_expression . field
 * @author yukimula
 *
 */
public interface CirFieldExpression extends CirReferExpression {
	
	/**
	 * @return the punctuate that represents .
	 */
	public CPunctuator get_operator();
	
	/**
	 * @return the reference expression as the body
	 */
	public CirReferExpression get_body();
	
	/**
	 * @return the field to de-reference the body
	 */
	public CirField get_field();
	
}
