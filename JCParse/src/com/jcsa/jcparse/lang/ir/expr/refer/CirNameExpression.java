package com.jcsa.jcparse.lang.ir.expr.refer;

import com.jcsa.jcparse.lang.ir.expr.CirReferExpression;

/**
 * 	|--	|--	<i>name_expression</i>									<br>
 * 	|--	|--	|--	declarator_expression								<br>
 * 	|--	|--	|--	identifier_expression								<br>
 * 	|--	|--	|--	implicator_expression								<br>
 * 	|--	|--	|--	return_ref_expression								<br>
 * 
 * @author yukimula
 *
 */
public interface CirNameExpression extends CirReferExpression {
	
	/**
	 * @param complete true if the method returns the complete name
	 * @return (complete or simplified) name of the expression code
	 */
	public String get_name(boolean complete);
	
}
