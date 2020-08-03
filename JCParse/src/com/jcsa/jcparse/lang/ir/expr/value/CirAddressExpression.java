package com.jcsa.jcparse.lang.ir.expr.value;

import com.jcsa.jcparse.lang.ir.expr.CirUnaryExpression;

/**
 * address_expression |-- & expression
 * @author yukimula
 *
 */
public interface CirAddressExpression extends CirValueExpression, CirUnaryExpression {
}
