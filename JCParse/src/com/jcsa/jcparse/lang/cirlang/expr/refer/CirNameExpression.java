package com.jcsa.jcparse.lang.cirlang.expr.refer;

/**
 * <code>
 * 	<i>name_expression</i>				{get_name(boolean)}		<br>
 * 	|-- identifier_expression									<br>
 * 	|--	declarator_expression									<br>
 * 	|--	temporary_expression									<br>
 * 	|--	return_refer_expression									<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirNameExpression extends CirReferExpression {
	
	/**
	 * @param complete whether to get the complete name
	 * @return the simplified or complete name of the expression
	 */
	public String get_name(boolean complete);
	
}
