package com.jcsa.jcparse.lang.irlang.expr;

/**
 * logic_expression |-- {logic_not} expression
 * 					|-- {logic_and} expression expression
 * 					|-- {logic_ior} expression expression
 * 
 * @author yukimula
 *
 */
public interface CirLogicExpression extends CirComputeExpression {
}
