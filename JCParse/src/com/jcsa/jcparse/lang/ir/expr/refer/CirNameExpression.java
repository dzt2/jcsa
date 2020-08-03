package com.jcsa.jcparse.lang.ir.expr.refer;

/**
 * <code>
 * 	<i>name_expression</i>										<br>
 * 	|-- declarator_reference									<br>
 * 	|-- identifier_reference									<br>
 * 	|-- temporary_reference										<br>
 * 	|--	return_reference										<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirNameExpression extends CirReferExpression {
	
	/**
	 * @param complete whether to get the complete name of node
	 * @return the complete or simplified name of the expression
	 */
	public String get_name(boolean complete);
	
}
