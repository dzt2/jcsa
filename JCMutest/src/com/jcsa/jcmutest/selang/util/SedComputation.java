package com.jcsa.jcmutest.selang.util;

import com.jcsa.jcmutest.selang.lang.expr.SedConstant;
import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * It implements the computation over SedConstant.
 * 
 * @author yukimula
 *
 */
public class SedComputation {
	
	/* constant getters */
	public static boolean get_bool(SedConstant source) throws Exception {
		CConstant constant = source.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool().booleanValue();
		case c_char:
		case c_uchar:		return constant.get_char().charValue() != 0;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return constant.get_integer().intValue() != 0;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return constant.get_long().longValue() != 0L;
		case c_float:		return constant.get_float().floatValue() != 0.0f;
		case c_double:
		case c_ldouble:		return constant.get_double().doubleValue() != 0.0;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	public static char get_char(SedConstant source) throws Exception {
		CConstant constant = source.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:		return (char) (constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return (char) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return (char) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return (char) constant.get_long().longValue();
		case c_float:		return (char) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:		return (char) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	public static short get_short(SedConstant source) throws Exception {
		CConstant constant = source.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:		return (short) (constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return (short) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return (short) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return (short) constant.get_long().longValue();
		case c_float:		return (short) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:		return (short) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	public static int get_int(SedConstant source) throws Exception {
		CConstant constant = source.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:		return (int) (constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return (int) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return (int) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return (int) constant.get_long().longValue();
		case c_float:		return (int) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:		return (int) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	public static long get_long(SedConstant source) throws Exception {
		CConstant constant = source.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:		return (long) (constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return (long) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return (long) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return (long) constant.get_long().longValue();
		case c_float:		return (long) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:		return (long) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	public static float get_float(SedConstant source) throws Exception {
		CConstant constant = source.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:		return (float) (constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return (float) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return (float) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return (float) constant.get_long().longValue();
		case c_float:		return (float) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:		return (float) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	public static double get_double(SedConstant source) throws Exception {
		CConstant constant = source.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:		return (double) (constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return (double) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return (double) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return (double) constant.get_long().longValue();
		case c_float:		return (double) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:		return (double) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	/**
	 * @param source
	 * @return Long | Double
	 * @throws Exception
	 */
	public static Object get_number(SedConstant source) throws Exception {
		CConstant constant = source.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:		return Long.valueOf((constant.get_bool() ? 1 : 0));
		case c_char:
		case c_uchar:		return Long.valueOf(constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Long.valueOf(constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Long.valueOf(constant.get_long().longValue());
		case c_float:		return Float.valueOf(constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Double.valueOf(constant.get_double().doubleValue());
		default: throw new IllegalArgumentException("Invalid constant.");
		}
	}
	
	/* arithmetic computations */
	public static SedConstant arith_neg(SedConstant operand) throws Exception {
		Object number = get_number(operand);
		Object result;
		if(number instanceof Long) {
			result = Long.valueOf(-((Long) number).longValue());
		}
		else {
			result = Double.valueOf(-((Double) number).doubleValue());
		}
		return (SedConstant) SedFactory.fetch(result);
	}
	public static SedConstant bitws_rsv(SedConstant operand) throws Exception {
		long number = get_long(operand);
		return (SedConstant) SedFactory.fetch(Long.valueOf(~number));
	}
	public static SedConstant logic_not(SedConstant operand) throws Exception {
		boolean value = get_bool(operand);
		return (SedConstant) SedFactory.fetch(Boolean.valueOf(!value));
	}
	public static SedConstant arith_add(SedConstant loperand, SedConstant roperand) throws Exception {
		Object lvalue = get_number(loperand);
		Object rvalue = get_number(roperand);
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Long.valueOf(x + y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x + y));
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x + y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x + y));
			}
		}
	}
	public static SedConstant arith_sub(SedConstant loperand, SedConstant roperand) throws Exception {
		Object lvalue = get_number(loperand);
		Object rvalue = get_number(roperand);
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Long.valueOf(x - y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x - y));
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x - y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x - y));
			}
		}
	}
	public static SedConstant arith_mul(SedConstant loperand, SedConstant roperand) throws Exception {
		Object lvalue = get_number(loperand);
		Object rvalue = get_number(roperand);
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Long.valueOf(x * y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x * y));
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x * y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x * y));
			}
		}
	}
	public static SedConstant arith_div(SedConstant loperand, SedConstant roperand) throws Exception {
		Object lvalue = get_number(loperand);
		Object rvalue = get_number(roperand);
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Long.valueOf(x / y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x / y));
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x / y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Double.valueOf(x / y));
			}
		}
	}
	public static SedConstant arith_mod(SedConstant loperand, SedConstant roperand) throws Exception {
		long x = get_long(loperand);
		long y = get_long(roperand);
		return (SedConstant) SedFactory.fetch(Long.valueOf(x % y));
	}
	public static SedConstant bitws_and(SedConstant loperand, SedConstant roperand) throws Exception {
		long x = get_long(loperand);
		long y = get_long(roperand);
		return (SedConstant) SedFactory.fetch(Long.valueOf(x & y));
	}
	public static SedConstant bitws_ior(SedConstant loperand, SedConstant roperand) throws Exception {
		long x = get_long(loperand);
		long y = get_long(roperand);
		return (SedConstant) SedFactory.fetch(Long.valueOf(x | y));
	}
	public static SedConstant bitws_xor(SedConstant loperand, SedConstant roperand) throws Exception {
		long x = get_long(loperand);
		long y = get_long(roperand);
		return (SedConstant) SedFactory.fetch(Long.valueOf(x ^ y));
	}
	public static SedConstant bitws_lsh(SedConstant loperand, SedConstant roperand) throws Exception {
		long x = get_long(loperand);
		long y = get_long(roperand);
		return (SedConstant) SedFactory.fetch(Long.valueOf(x << y));
	}
	public static SedConstant bitws_rsh(SedConstant loperand, SedConstant roperand) throws Exception {
		long x = get_long(loperand);
		long y = get_long(roperand);
		return (SedConstant) SedFactory.fetch(Long.valueOf(x >> y));
	}
	public static SedConstant logic_and(SedConstant loperand, SedConstant roperand) throws Exception {
		boolean x = get_bool(loperand);
		boolean y = get_bool(roperand);
		return (SedConstant) SedFactory.fetch(Boolean.valueOf(x && y));
	}
	public static SedConstant logic_ior(SedConstant loperand, SedConstant roperand) throws Exception {
		boolean x = get_bool(loperand);
		boolean y = get_bool(roperand);
		return (SedConstant) SedFactory.fetch(Boolean.valueOf(x || y));
	}
	public static SedConstant greater_tn(SedConstant loperand, SedConstant roperand) throws Exception {
		Object lvalue = get_number(loperand);
		Object rvalue = get_number(roperand);
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x > y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x > y));
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x > y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x > y));
			}
		}
	}
	public static SedConstant greater_eq(SedConstant loperand, SedConstant roperand) throws Exception {
		Object lvalue = get_number(loperand);
		Object rvalue = get_number(roperand);
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x >= y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x >= y));
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x >= y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x >= y));
			}
		}
	}
	public static SedConstant smaller_tn(SedConstant loperand, SedConstant roperand) throws Exception {
		Object lvalue = get_number(loperand);
		Object rvalue = get_number(roperand);
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x < y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x < y));
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x < y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x < y));
			}
		}
	}
	public static SedConstant smaller_eq(SedConstant loperand, SedConstant roperand) throws Exception {
		Object lvalue = get_number(loperand);
		Object rvalue = get_number(roperand);
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x <= y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x <= y));
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x <= y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x <= y));
			}
		}
	}
	public static SedConstant not_equals(SedConstant loperand, SedConstant roperand) throws Exception {
		Object lvalue = get_number(loperand);
		Object rvalue = get_number(roperand);
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x != y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x != y));
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x != y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x != y));
			}
		}
	}
	public static SedConstant equal_with(SedConstant loperand, SedConstant roperand) throws Exception {
		Object lvalue = get_number(loperand);
		Object rvalue = get_number(roperand);
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x == y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x == y));
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x == y));
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (SedConstant) SedFactory.fetch(Boolean.valueOf(x == y));
			}
		}
	}
	
	/* value compare */
	public static boolean compare(SedConstant expression, long value) throws Exception {
		Object number = get_number(expression);
		if(number instanceof Long) {
			return ((Long) number).longValue() == value;
		}
		else {
			return ((Double) number).doubleValue() == value;
		}
	}
	
}
