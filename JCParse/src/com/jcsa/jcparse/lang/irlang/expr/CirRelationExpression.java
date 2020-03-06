package com.jcsa.jcparse.lang.irlang.expr;

/**
 * relation_expression	|--	{greater_tn} expression expression
 * 						|-- {greater_eq} expression expression
 * 						|-- {smaller_tn} expression expression
 * 						|-- {smaller_eq} expression expression
 * 						|-- {equal_with} expression expression
 * 						|-- {not_equals} expression expression
 * @author yukimula
 *
 */
public interface CirRelationExpression extends CirComputeExpression {
}
