package com.jcsa.jcparse.parse.symbol;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It computes the symbolic expression into constant.
 * 
 * @author yukimula
 *
 */
public class SymbolComputation {
	
	/* constant value getters */
	public static SymbolConstant arith_neg(SymbolConstant operand) throws Exception {
		Object number = operand.get_number();
		if(number instanceof Long) {
			number = Long.valueOf(-((Long) number).longValue());
		}
		else {
			number = Double.valueOf(-((Double) number).doubleValue());
		}
		return (SymbolConstant) SymbolFactory.sym_expression(number);
	}
	public static SymbolConstant bitws_rsv(SymbolConstant operand) throws Exception {
		long value = operand.get_long();
		return (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(~value));
	}
	public static SymbolConstant logic_not(SymbolConstant operand) throws Exception {
		boolean value = operand.get_bool();
		return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(!value));
	}
	public static SymbolConstant arith_add(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(x + y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x + y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x + y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x + y));
			}
		}
	}
	public static SymbolConstant arith_sub(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(x - y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x - y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x - y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x - y));
			}
		}
	}
	public static SymbolConstant arith_mul(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(x * y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x * y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x * y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x * y));
			}
		}
	}
	public static SymbolConstant arith_div(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(x / y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x / y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x / y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x / y));
			}
		}
	}
	public static SymbolConstant arith_mod(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(x % y));
	}
	public static SymbolConstant bitws_and(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(x & y));
	}
	public static SymbolConstant bitws_ior(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(x | y));
	}
	public static SymbolConstant bitws_xor(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(x ^ y));
	}
	public static SymbolConstant bitws_lsh(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(x << y));
	}
	public static SymbolConstant bitws_rsh(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		long x = loperand.get_long(), y = roperand.get_long();
		return (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(x >> y));
	}
	public static SymbolConstant logic_and(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		boolean x = loperand.get_bool(), y = roperand.get_bool();
		return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x && y));
	}
	public static SymbolConstant logic_ior(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		boolean x = loperand.get_bool(), y = roperand.get_bool();
		return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x || y));
	}
	public static SymbolConstant greater_tn(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x > y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x > y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x > y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x > y));
			}
		}
	}
	public static SymbolConstant greater_eq(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x >= y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x >= y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x >= y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x >= y));
			}
		}
	}
	public static SymbolConstant smaller_tn(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x < y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x < y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x < y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x < y));
			}
		}
	}
	public static SymbolConstant smaller_eq(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x <= y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x <= y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x <= y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x <= y));
			}
		}
	}
	public static SymbolConstant equal_with(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x == y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x == y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x == y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x == y));
			}
		}
	}
	public static SymbolConstant not_equals(
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		Object lnumber = loperand.get_number();
		Object rnumber = roperand.get_number();
		if(lnumber instanceof Long) {
			long x = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x != y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x != y));
			}
		}
		else {
			double x = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long y = ((Long) rnumber).longValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x != y));
			}
			else {
				double y = ((Double) rnumber).doubleValue();
				return (SymbolConstant) SymbolFactory.sym_expression(Boolean.valueOf(x != y));
			}
		}
	}
	public static boolean compare(SymbolConstant operand, long value) throws Exception {
		Object number = operand.get_number();
		if(number instanceof Long) {
			return ((Long) number).longValue() == value;
		}
		else {
			return ((Double) number).doubleValue() == value;
		}
	}
	
}
