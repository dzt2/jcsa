package com.jcsa.jcparse.lang.irlang.expr;

/**
 * value_expression |-- const_expression
 * 					|-- string_literal
 * 					|-- arith_expression
 * 					|-- bitws_expression
 * 					|-- logic_expression
 * 					|-- relation_expression
 * 					|-- address_expression
 * 					|-- cast_expression
 * 					|-- initializer_body
 * 					|-- default_value
 * @author yukimula
 *
 */
public interface CirValueExpression extends CirExpression {
}
