package com.jcsa.jcparse.lang.irlang.expr;

/**
 * arith_expression |-- {arith_neg} expression
 * 					|-- {increment}	expression
 * 					|-- {decrement} expression
 * 					|--	{arith_add} expression expression
 * 					|--	{arith_sub} expression expression
 * 					|--	{arith_mul} expression expression
 * 					|--	{arith_div} expression expression
 * 					|--	{arith_mod} expression expression
 * @author yukimula
 *
 */
public interface CirArithExpression extends CirComputeExpression {
}
