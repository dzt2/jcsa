package com.jcsa.jcparse.lang.sym;

/**
 * It provides computation on the symbolic expressions.
 * 
 * @author yukimula
 *
 */
public class SymComputation {
	
	/* constant value getters */
	public static SymConstant arith_neg(SymConstant operand) throws Exception {
		Object number = operand.get_number();
		if(number instanceof Long) {
			number = Long.valueOf(-((Long) number).longValue());
		}
		else {
			number = Double.valueOf(-((Double) number).doubleValue());
		}
		return SymFactory.new_constant(number);
	}
	public static SymConstant bitws_rsv(SymConstant operand) throws Exception {
		long value = operand.get_long();
		return SymFactory.new_constant(Long.valueOf(~value));
	}
	public static SymConstant logic_not(SymConstant operand) throws Exception {
		boolean value = operand.get_bool();
		return SymFactory.new_constant(Boolean.valueOf(value));
	}
	public static SymConstant arith_add(
			SymConstant loperand, SymConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Long.valueOf(x + y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Double.valueOf(x + y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Double.valueOf(x + y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Double.valueOf(x + y));
			}
		}
	}
	public static SymConstant arith_sub(
			SymConstant loperand, SymConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Long.valueOf(x - y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Double.valueOf(x - y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Double.valueOf(x - y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Double.valueOf(x - y));
			}
		}
	}
	public static SymConstant arith_mul(
			SymConstant loperand, SymConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Long.valueOf(x * y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Double.valueOf(x * y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Double.valueOf(x * y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Double.valueOf(x * y));
			}
		}
	}
	public static SymConstant arith_div(
			SymConstant loperand, SymConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Long.valueOf(x / y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Double.valueOf(x / y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Double.valueOf(x / y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Double.valueOf(x / y));
			}
		}
	}
	public static SymConstant arith_mod(
			SymConstant loperand, SymConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return SymFactory.new_constant(Long.valueOf(x % y));
	}
	public static SymConstant bitws_and(
			SymConstant loperand, SymConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return SymFactory.new_constant(Long.valueOf(x & y));
	}
	public static SymConstant bitws_ior(
			SymConstant loperand, SymConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return SymFactory.new_constant(Long.valueOf(x | y));
	}
	public static SymConstant bitws_xor(
			SymConstant loperand, SymConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return SymFactory.new_constant(Long.valueOf(x ^ y));
	}
	public static SymConstant bitws_lsh(
			SymConstant loperand, SymConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return SymFactory.new_constant(Long.valueOf(x << y));
	}
	public static SymConstant bitws_rsh(
			SymConstant loperand, SymConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return SymFactory.new_constant(Long.valueOf(x >> y));
	}
	public static SymConstant logic_and(
			SymConstant loperand, SymConstant roperand) throws Exception {
		boolean x = loperand.get_bool(), y = roperand.get_bool();
		return SymFactory.new_constant(Boolean.valueOf(x && y));
	}
	public static SymConstant logic_ior(
			SymConstant loperand, SymConstant roperand) throws Exception {
		boolean x = loperand.get_bool(), y = roperand.get_bool();
		return SymFactory.new_constant(Boolean.valueOf(x || y));
	}
	public static SymConstant greater_tn(
			SymConstant loperand, SymConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x > y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x > y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x > y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x > y));
			}
		}
	}
	public static SymConstant greater_eq(
			SymConstant loperand, SymConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x >= y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x >= y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x >= y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x >= y));
			}
		}
	}
	public static SymConstant smaller_tn(
			SymConstant loperand, SymConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x < y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x < y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x < y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x < y));
			}
		}
	}
	public static SymConstant smaller_eq(
			SymConstant loperand, SymConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x <= y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x <= y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x <= y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x <= y));
			}
		}
	}
	public static SymConstant equal_with(
			SymConstant loperand, SymConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x == y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x == y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x == y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x == y));
			}
		}
	}
	public static SymConstant not_equals(
			SymConstant loperand, SymConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x != y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x != y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return SymFactory.new_constant(Boolean.valueOf(x != y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return SymFactory.new_constant(Boolean.valueOf(x != y));
			}
		}
	}
	public static boolean compare(SymConstant operand, long value) throws Exception {
		Object number = operand.get_number();
		if(number instanceof Long) {
			return ((Long) number).longValue() == value;
		}
		else {
			return ((Double) number).doubleValue() == value;
		}
	}
	
}
