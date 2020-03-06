package com.jcsa.jcmuta.mutant.sem2mutation;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SemanticMutationUtil {
	
	/* evaluator */
	/**
	 * get the value of the constant
	 * @param constant
	 * @return either Boolean, Long or Double
	 * @throws Exception
	 */
	public static Object get_const_value(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool: return constant.get_bool();
		case c_char:
		case c_uchar:	return Long.valueOf(constant.get_char());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return Long.valueOf(constant.get_integer());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return constant.get_long();
		case c_float:	return Double.valueOf(constant.get_float());
		case c_double:
		case c_ldouble:	return constant.get_double();
		default: throw new IllegalArgumentException("Invalid constant: null");
		}
	}
	/**
	 * cast_to expression
	 * @param casted_type
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static Object cast_to(CType casted_type, Object value) throws Exception {
		if(value == null) {
			return null;
		}
		else {
			casted_type = CTypeAnalyzer.get_value_type(casted_type);
			if(CTypeAnalyzer.is_boolean(casted_type)) {
				if(value instanceof Boolean) {
					return value;
				}
				else if(value instanceof Long) {
					return Boolean.valueOf(((Long) value).longValue() != 0);
				}
				else if(value instanceof Double) {
					return Boolean.valueOf(((Double) value).doubleValue() != 0);
				}
				else {
					throw new IllegalArgumentException("Invalid value");
				}
			}
			else if(CTypeAnalyzer.is_integer(casted_type)) {
				if(value instanceof Boolean) {
					if(((Boolean) value).booleanValue())
						return Long.valueOf(1);
					else return Long.valueOf(0);
				}
				else if(value instanceof Long) {
					return value;
				}
				else if(value instanceof Double) {
					return Long.valueOf(((Double) value).longValue());
				}
				else {
					throw new IllegalArgumentException("Invalid value");
				}
			}
			else if(CTypeAnalyzer.is_real(casted_type)) {
				if(value instanceof Boolean) {
					if(((Boolean) value).booleanValue())
						return Double.valueOf(1);
					else return Double.valueOf(0);
				}
				else if(value instanceof Long) {
					return Double.valueOf(((Long) value).longValue());
				}
				else if(value instanceof Double) {
					return value;
				}
				else {
					throw new IllegalArgumentException("Invalid value");
				}
			}
			else {
				return null;	/** cannot be computed **/
			}
		}
	}
	/**
	 * negative + bit_not + logic_not
	 * @param operator
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	public static Object compute(COperator operator, Object operand) throws Exception {
		if(operand == null) return null;
		else {
			if(operand instanceof Boolean) {
				if(((Boolean) operand).booleanValue())
					operand = Long.valueOf(1);
				else operand = Long.valueOf(0);
			}
			
			if(operand instanceof Long) {
				switch(operator) {
				case negative:		return Long.valueOf(-((Long) operand).longValue());
				case bit_not:		return Long.valueOf(~((Long) operand).longValue());
				case logic_not:		return Boolean.valueOf(((Long) operand).longValue() == 0);
				default: throw new IllegalArgumentException("invalid operator");
				}
			}
			else if(operand instanceof Double) {
				switch(operator) {
				case negative:		return Double.valueOf(-((Double) operand).doubleValue());
				case logic_not:		return Boolean.valueOf(((Double) operand).doubleValue() == 0);
				default: throw new IllegalArgumentException("invalid operator");
				}
			}
			else {
				return null;
			}
		}
	}
	/**
	 * compute the value based on operator with respect to the given operands
	 * @param operator
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static Object compute(COperator operator, Object loperand, Object roperand) throws Exception {
		if(loperand == null || roperand == null) {
			return null;
		}
		else {
			if(loperand instanceof Boolean) {
				if(((Boolean) loperand).booleanValue())
					loperand = Long.valueOf(1);
				else loperand = Long.valueOf(0);
			}
			
			if(roperand instanceof Boolean) {
				if(((Boolean) roperand).booleanValue())
					roperand = Long.valueOf(1);
				else roperand = Long.valueOf(0);
			}
			
			if(loperand instanceof Long) {
				long lvalue = ((Long) loperand).longValue();
				if(roperand instanceof Long) {
					long rvalue = ((Long) roperand).longValue();
					
					switch(operator) {
					case arith_add:		return Long.valueOf(lvalue + rvalue);
					case arith_sub:		return Long.valueOf(lvalue - rvalue);
					case arith_mul:		return Long.valueOf(lvalue * rvalue);
					case arith_div:		return Long.valueOf(lvalue / rvalue);
					case arith_mod:		return Long.valueOf(lvalue % rvalue);
					case bit_and:		return Long.valueOf(lvalue & rvalue);
					case bit_or:		return Long.valueOf(lvalue | rvalue);
					case bit_xor:		return Long.valueOf(lvalue ^ rvalue);
					case left_shift:	return Long.valueOf(lvalue <<rvalue);
					case righ_shift:	return Long.valueOf(lvalue >>rvalue);
					case logic_and:		return Boolean.valueOf((lvalue != 0) && (rvalue != 0));
					case logic_or:		return Boolean.valueOf((lvalue != 0) || (rvalue != 0));
					case greater_tn:	return Boolean.valueOf(lvalue > rvalue);
					case greater_eq:	return Boolean.valueOf(lvalue >=rvalue);
					case smaller_tn:	return Boolean.valueOf(lvalue < rvalue);
					case smaller_eq:	return Boolean.valueOf(lvalue <=rvalue);
					case equal_with:	return Boolean.valueOf(lvalue ==rvalue);
					case not_equals:	return Boolean.valueOf(lvalue !=rvalue);
					default: throw new IllegalArgumentException("Invalid: " + operator);
					}
				}
				else if(roperand instanceof Double) {
					double rvalue = ((Double) roperand).doubleValue();
					switch(operator) {
					case arith_add:		return Double.valueOf(lvalue + rvalue);
					case arith_sub:		return Double.valueOf(lvalue - rvalue);
					case arith_mul:		return Double.valueOf(lvalue * rvalue);
					case arith_div:		return Double.valueOf(lvalue / rvalue);
					case logic_and:		return Boolean.valueOf((lvalue != 0) && (rvalue != 0));
					case logic_or:		return Boolean.valueOf((lvalue != 0) || (rvalue != 0));
					case greater_tn:	return Boolean.valueOf(lvalue > rvalue);
					case greater_eq:	return Boolean.valueOf(lvalue >=rvalue);
					case smaller_tn:	return Boolean.valueOf(lvalue < rvalue);
					case smaller_eq:	return Boolean.valueOf(lvalue <=rvalue);
					case equal_with:	return Boolean.valueOf(lvalue ==rvalue);
					case not_equals:	return Boolean.valueOf(lvalue !=rvalue);
					default: throw new IllegalArgumentException("Invalid operator");
					}
				}
				else {
					throw new IllegalArgumentException("Invalid roperand");
				}
			}
			else if(loperand instanceof Double) {
				double lvalue = ((Double) loperand).doubleValue();
				if(roperand instanceof Long) {
					long rvalue = ((Long) roperand).longValue();
					switch(operator) {
					case arith_add:		return Double.valueOf(lvalue + rvalue);
					case arith_sub:		return Double.valueOf(lvalue - rvalue);
					case arith_mul:		return Double.valueOf(lvalue * rvalue);
					case arith_div:		return Double.valueOf(lvalue / rvalue);
					case logic_and:		return Boolean.valueOf((lvalue != 0) && (rvalue != 0));
					case logic_or:		return Boolean.valueOf((lvalue != 0) || (rvalue != 0));
					case greater_tn:	return Boolean.valueOf(lvalue > rvalue);
					case greater_eq:	return Boolean.valueOf(lvalue >=rvalue);
					case smaller_tn:	return Boolean.valueOf(lvalue < rvalue);
					case smaller_eq:	return Boolean.valueOf(lvalue <=rvalue);
					case equal_with:	return Boolean.valueOf(lvalue ==rvalue);
					case not_equals:	return Boolean.valueOf(lvalue !=rvalue);
					default: throw new IllegalArgumentException("Invalid operator");
					}
				}
				else if(roperand instanceof Double) {
					double rvalue = ((Double) roperand).doubleValue();
					switch(operator) {
					case arith_add:		return Double.valueOf(lvalue + rvalue);
					case arith_sub:		return Double.valueOf(lvalue - rvalue);
					case arith_mul:		return Double.valueOf(lvalue * rvalue);
					case arith_div:		return Double.valueOf(lvalue / rvalue);
					case logic_and:		return Boolean.valueOf((lvalue != 0) && (rvalue != 0));
					case logic_or:		return Boolean.valueOf((lvalue != 0) || (rvalue != 0));
					case greater_tn:	return Boolean.valueOf(lvalue > rvalue);
					case greater_eq:	return Boolean.valueOf(lvalue >=rvalue);
					case smaller_tn:	return Boolean.valueOf(lvalue < rvalue);
					case smaller_eq:	return Boolean.valueOf(lvalue <=rvalue);
					case equal_with:	return Boolean.valueOf(lvalue ==rvalue);
					case not_equals:	return Boolean.valueOf(lvalue !=rvalue);
					default: throw new IllegalArgumentException("Invalid operator");
					}
				}
				else {
					throw new IllegalArgumentException("Invalid roperand");
				}
			}
			else {
				throw new IllegalArgumentException("Invalid loperand: " + loperand);
			}
		}
	}
	/**
	 * get the constant hold as the value of the expression
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public static Object get_constant(CirExpression expression) throws Exception {
		if(expression instanceof CirConstExpression) {
			return get_const_value(((CirConstExpression) expression).get_constant());
		}
		else if(expression instanceof CirCastExpression) {
			Object operand = get_constant(((CirCastExpression) expression).get_operand());
			return cast_to(((CirCastExpression) expression).get_type().get_typename(), operand);
		}
		else if(expression instanceof CirComputeExpression) {
			if(((CirComputeExpression) expression).number_of_operand() == 1) {
				Object operand = get_constant(((CirComputeExpression) expression).get_operand(0));
				return compute(((CirComputeExpression) expression).get_operator(), operand);
			}
			else if(((CirComputeExpression) expression).number_of_operand() == 2) {
				Object loperand = get_constant(((CirComputeExpression) expression).get_operand(0));
				Object roperand = get_constant(((CirComputeExpression) expression).get_operand(1));
				return compute(((CirComputeExpression) expression).get_operator(), loperand, roperand);
			}
			else throw new IllegalArgumentException("Not support: " + expression);
			
		}
		else {
			return null;	/** not constant **/
		}
	}
	
}
