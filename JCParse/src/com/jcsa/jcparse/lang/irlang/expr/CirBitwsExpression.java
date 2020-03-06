package com.jcsa.jcparse.lang.irlang.expr;

/**
 * bitws_expression	|-- {bitws_rsv} expression
 * 					|-- {bitws_and} expression expression
 * 					|-- {bitws_ior} expression expression
 * 					|-- {bitws_xor} expression expression
 * 					|-- {bitws_lsh} expression expression
 * 					|-- {bitws_rsh} expression expression
 * 
 * @author yukimula
 *
 */
public interface CirBitwsExpression extends CirComputeExpression {
}
