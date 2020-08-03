package com.jcsa.jcparse.lang.ir.expr.value;

import com.jcsa.jcparse.lang.ir.expr.CirUnaryExpression;
import com.jcsa.jcparse.lang.ir.expr.CirValueExpression;

/**
 * address_expression	|--	& operand
 * @author yukimula
 *
 */
public interface CirAddressOfExpression extends CirValueExpression, CirUnaryExpression {
}
