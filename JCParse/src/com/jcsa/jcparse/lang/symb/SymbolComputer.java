package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;


/**
 * It implements the constant computation, partial evaluation, domain analysis.
 * @author yukimula
 *
 */
final class SymbolComputer {
	
	/* singleton mode */ /** constructor **/ private SymbolComputer(){ }
	private static final SymbolComputer computer = new SymbolComputer();
	
	/* constant computation */
	/**
	 * @param operand
	 * @return (int|long|double) (operand)
	 * @throws Exception
	 */
	private SymbolConstant compute_arith_pos(SymbolConstant operand) throws Exception {
		if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			Object result = operand.get_number();
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param operand
	 * @return (- (int|long|double) operand)
	 * @throws Exception
	 */
	private SymbolConstant compute_arith_neg(SymbolConstant operand) throws Exception {
		if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			Object value = operand.get_number(), result;
			if(value instanceof Integer) {
				result = Integer.valueOf(-((Integer) value).intValue());
			}
			else if(value instanceof Long) {
				result = Long.valueOf(-((Long) value).longValue());
			}
			else {
				result = Double.valueOf(-((Double) value).doubleValue());
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param operand
	 * @return (- (int|long) operand)
	 * @throws Exception
	 */
	private SymbolConstant compute_bitws_rsv(SymbolConstant operand) throws Exception {
		if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			Object value = operand.get_number(), result;
			if(value instanceof Integer) {
				result = Integer.valueOf(~((Integer) value).intValue());
			}
			else if(value instanceof Long) {
				result = Long.valueOf(~((Long) value).longValue());
			}
			else {
				result = Long.valueOf(~((Double) value).longValue());
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param operand
	 * @return (! (boolean) operand)
	 * @throws Exception
	 */
	private SymbolConstant compute_logic_not(SymbolConstant operand) throws Exception {
		if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			Boolean result = operand.get_bool().booleanValue();
			return SymbolFactory.sym_constant(!result);
		}
	}
	/**
	 * @param operand
	 * @return (int|long|double) (operand + 1)
	 * @throws Exception
	 */
	private SymbolConstant compute_increment(SymbolConstant operand) throws Exception {
		if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			Object value = operand.get_number(), result;
			if(value instanceof Integer) {
				result = Integer.valueOf(((Integer) value).intValue() + 1);
			}
			else if(value instanceof Long) {
				result = Long.valueOf(((Long) value).longValue() + 1L);
			}
			else {
				result = Double.valueOf(((Double) value).doubleValue() + 1.0);
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param operand
	 * @return (int|long|double) (operand - 1)
	 * @throws Exception
	 */
	private SymbolConstant compute_decrement(SymbolConstant operand) throws Exception {
		if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			Object value = operand.get_number(), result;
			if(value instanceof Integer) {
				result = Integer.valueOf(((Integer) value).intValue() - 1);
			}
			else if(value instanceof Long) {
				result = Long.valueOf(((Long) value).longValue() - 1L);
			}
			else {
				result = Double.valueOf(((Double) value).doubleValue() - 1.0);
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param cast_type
	 * @param operand
	 * @return (cast_type) (constant)
	 * @throws Exception
	 */
	private SymbolConstant compute_type_cast(CType cast_type, SymbolConstant operand) throws Exception {
		if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else if(cast_type == null) {
			throw new IllegalArgumentException("Invalid cast_type: null");
		}
		else {
			Object result;
			if(cast_type instanceof CBasicType) {
				switch(((CBasicType) cast_type).get_tag()) {
				case c_bool:	result = operand.get_bool();		break;
				case c_char:	
				case c_uchar:	result = operand.get_char();		break;
				case c_short:
				case c_ushort:	result = operand.get_short();		break;
				case c_int:
				case c_uint:	result = operand.get_int();			break;
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:	result = operand.get_long();		break;
				case c_float:	result = operand.get_float();		break;
				case c_double:
				case c_ldouble:	result = operand.get_double();		break;
				default:		throw new IllegalArgumentException(cast_type.generate_code());
				}
			}
			else if(cast_type instanceof CArrayType || 
					cast_type instanceof CPointerType) {
				result = operand.get_long();
			}
			else if(cast_type instanceof CEnumType) {
				result = operand.get_int();
			}
			else {
				throw new IllegalArgumentException(cast_type.generate_code());
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (int|long|double) (loperand + roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_arith_add(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Object result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Integer.valueOf(x + y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x + y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x + y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x + y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x + y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x + y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Double.valueOf(x + y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Double.valueOf(x + y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x + y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (int|long|double) (loperand - roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_arith_sub(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Object result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Integer.valueOf(x - y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x - y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x - y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x - y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x - y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x - y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Double.valueOf(x - y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Double.valueOf(x - y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x - y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (int|long|double) (loperand * roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_arith_mul(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Object result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Integer.valueOf(x * y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x * y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x * y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x * y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x * y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x * y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Double.valueOf(x * y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Double.valueOf(x * y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x * y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (int|long|double) (loperand / roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_arith_div(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Object result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Integer.valueOf(x / y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x / y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x / y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x / y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x / y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x / y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Double.valueOf(x / y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Double.valueOf(x / y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Double.valueOf(x / y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (int|long) (loperand % roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_arith_mod(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Object result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Integer.valueOf(x % y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x % y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x % y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x % y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x % y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x % y);
				}
			}
			else {
				long x = ((Double) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x % y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x % y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x % y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (int|long) (loperand & roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_bitws_and(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Object result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Integer.valueOf(x & y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x & y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x & y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x & y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x & y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x & y);
				}
			}
			else {
				long x = ((Double) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x & y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x & y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x & y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (int|long) (loperand | roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_bitws_ior(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Object result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Integer.valueOf(x | y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x | y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x | y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x | y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x | y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x | y);
				}
			}
			else {
				long x = ((Double) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x | y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x | y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x | y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (int|long) (loperand ^ roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_bitws_xor(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Object result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Integer.valueOf(x ^ y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x ^ y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x ^ y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x ^ y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x ^ y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x ^ y);
				}
			}
			else {
				long x = ((Double) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x ^ y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x ^ y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x ^ y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (int|long) (loperand << roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_bitws_lsh(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Object result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Integer.valueOf(x << y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x << y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x << y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x << y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x << y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x << y);
				}
			}
			else {
				long x = ((Double) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x << y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x << y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x << y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (int|long) (loperand >> roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_bitws_rsh(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Object result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Integer.valueOf(x >> y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x >> y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x >> y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x >> y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x >> y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x >> y);
				}
			}
			else {
				long x = ((Double) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Long.valueOf(x >> y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Long.valueOf(x >> y);
				}
				else {
					long y = ((Double) rvalue).longValue();
					result = Long.valueOf(x >> y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (boolean) (loperand && roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_logic_and(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Boolean lvalue = loperand.get_bool();
			Boolean rvalue = roperand.get_bool();
			return SymbolFactory.sym_constant(lvalue && rvalue);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (boolean) (loperand || roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_logic_ior(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Boolean lvalue = loperand.get_bool();
			Boolean rvalue = roperand.get_bool();
			return SymbolFactory.sym_constant(lvalue || rvalue);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (boolean) (loperand > roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_greater_tn(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Boolean result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x > y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x > y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x > y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x > y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x > y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x > y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x > y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x > y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x > y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (boolean) (loperand >= roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_greater_eq(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Boolean result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x >= y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x >= y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x >= y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x >= y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x >= y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x >= y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x >= y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x >= y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x >= y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (boolean) (loperand < roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_smaller_tn(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Boolean result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x < y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x < y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x < y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x < y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x < y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x < y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x < y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x < y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x < y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (boolean) (loperand <= roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_smaller_eq(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Boolean result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x <= y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x <= y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x <= y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x <= y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x <= y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x <= y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x <= y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x <= y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x <= y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (boolean) (loperand == roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_equal_with(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Boolean result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x == y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x == y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x == y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x == y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x == y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x == y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x == y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x == y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x == y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return (boolean) (loperand != roperand)
	 * @throws Exception
	 */
	private SymbolConstant compute_not_equals(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			Object lvalue = loperand.get_number();
			Object rvalue = roperand.get_number();
			Boolean result;
			if(lvalue instanceof Integer) {
				int x = ((Integer) lvalue).intValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x != y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x != y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x != y);
				}
			}
			else if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x != y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x != y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x != y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Integer) {
					int y = ((Integer) rvalue).intValue();
					result = Boolean.valueOf(x != y);
				}
				else if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result = Boolean.valueOf(x != y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result = Boolean.valueOf(x != y);
				}
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param operator	{-, ~, !, ++, --}
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	private SymbolConstant compute(COperator operator, SymbolConstant operand) throws Exception {
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			switch(operator) {
			case positive:	return this.compute_arith_pos(operand);
			case negative:	return this.compute_arith_neg(operand);
			case bit_not:	return this.compute_bitws_rsv(operand);
			case logic_not:	return this.compute_logic_not(operand);
			case increment:	return this.compute_increment(operand);
			case decrement:	return this.compute_decrement(operand);
			default:		throw new IllegalArgumentException(operator.toString());
			}
		}
	}
	/**
	 * @param operator	{+, -, *, /, %, &, ||, ^, <<, >>, &&, ||, <, <=, >, >=, ==, !=}
	 * @param loperand	left operand
	 * @param roperand	right operand
	 * @return			binary constant result of computation
	 * @throws Exception
	 */
	private SymbolConstant compute(COperator operator, SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			switch(operator) {
			case arith_add:		return this.compute_arith_add(loperand, roperand);
			case arith_sub:		return this.compute_arith_sub(loperand, roperand);
			case arith_mul:		return this.compute_arith_mul(loperand, roperand);
			case arith_div:		return this.compute_arith_div(loperand, roperand);
			case arith_mod:		return this.compute_arith_mod(loperand, roperand);
			case bit_and:		return this.compute_bitws_and(loperand, roperand);
			case bit_or:		return this.compute_bitws_ior(loperand, roperand);
			case bit_xor:		return this.compute_bitws_xor(loperand, roperand);
			case left_shift:	return this.compute_bitws_lsh(loperand, roperand);
			case righ_shift:	return this.compute_bitws_rsh(loperand, roperand);
			case logic_and:		return this.compute_logic_and(loperand, roperand);
			case logic_or:		return this.compute_logic_ior(loperand, roperand);
			case greater_tn:	return this.compute_greater_tn(loperand, roperand);
			case greater_eq:	return this.compute_greater_eq(loperand, roperand);
			case smaller_tn:	return this.compute_smaller_tn(loperand, roperand);
			case smaller_eq:	return this.compute_smaller_eq(loperand, roperand);
			case equal_with:	return this.compute_equal_with(loperand, roperand);
			case not_equals:	return this.compute_not_equals(loperand, roperand);
			default:			throw new IllegalArgumentException(operator.toString());
			}
		}
	}
	/**
	 * @param operator	{-, ~, !, ++, --}
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	protected static SymbolConstant do_compute(COperator operator, SymbolConstant operand) throws Exception {
		return computer.compute(operator, operand);
	}
	/**
	 * @param operator	{+, -, *, /, %, &, ||, ^, <<, >>, &&, ||, <, <=, >, >=, ==, !=}
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	protected static SymbolConstant do_compute(COperator operator, 
			SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		return computer.compute(operator, loperand, roperand);
	}
	/**
	 * @param cast_type
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	protected static SymbolConstant do_compute(CType cast_type, SymbolConstant operand) throws Exception {
		return computer.compute_type_cast(cast_type, operand);
	}
	
	/* domain-determination */
	/**
	 * @param expression
	 * @return true {zero} | false {nzro} | null {unknown}
	 * @throws Exception
	 */
	protected static Boolean is_zero_domain(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolConstant) {
			Object value = ((SymbolConstant) expression).get_number();
			if(value instanceof Integer) {
				return Boolean.valueOf(((Integer) value).intValue() == 0);
			}
			else if(value instanceof Long) {
				return Boolean.valueOf(((Long) value).longValue() == 0L);
			}
			else {
				return Boolean.valueOf(((Double) value).doubleValue() == 0.0);
			}
		}
		else {
			return null;	/** null to denote 'unknown' **/
		}
	}
	/**
	 * @param expression
	 * @return true {nzro} | false {zero} | null {unknown}
	 * @throws Exception
	 */
	protected static Boolean is_nzro_domain(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolConstant) {
			Object value = ((SymbolConstant) expression).get_number();
			if(value instanceof Integer) {
				return Boolean.valueOf(((Integer) value).intValue() != 0);
			}
			else if(value instanceof Long) {
				return Boolean.valueOf(((Long) value).longValue() != 0L);
			}
			else {
				return Boolean.valueOf(((Double) value).doubleValue() != 0.0);
			}
		}
		else {
			return null;	/** null to denote 'unknown' **/
		}
	}
	/**
	 * @param expression
	 * @return true {post} false {npos} null {unknown}
	 * @throws Exception
	 */
	protected static Boolean is_post_domain(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolConstant) {
			Object value = ((SymbolConstant) expression).get_number();
			if(value instanceof Integer) {
				return Boolean.valueOf(((Integer) value).intValue() > 0);
			}
			else if(value instanceof Long) {
				return Boolean.valueOf(((Long) value).longValue() > 0L);
			}
			else {
				return Boolean.valueOf(((Double) value).doubleValue() > 0.0);
			}
		}
		else {
			return null;	/** null to denote 'unknown' **/
		}
	}
	/**
	 * @param expression
	 * @return true {negt} false {nneg} null {unknown}
	 * @throws Exception
	 */
	protected static Boolean is_negt_domain(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolConstant) {
			Object value = ((SymbolConstant) expression).get_number();
			if(value instanceof Integer) {
				return Boolean.valueOf(((Integer) value).intValue() < 0);
			}
			else if(value instanceof Long) {
				return Boolean.valueOf(((Long) value).longValue() < 0L);
			}
			else {
				return Boolean.valueOf(((Double) value).doubleValue() < 0.0);
			}
		}
		else {
			return null;	/** null to denote 'unknown' **/
		}
	}
	/**
	 * @param expression
	 * @return true {npos} false {post} null {unknown}
	 * @throws Exception
	 */
	protected static Boolean is_npos_domain(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolConstant) {
			Object value = ((SymbolConstant) expression).get_number();
			if(value instanceof Integer) {
				return Boolean.valueOf(((Integer) value).intValue() <= 0);
			}
			else if(value instanceof Long) {
				return Boolean.valueOf(((Long) value).longValue() <= 0L);
			}
			else {
				return Boolean.valueOf(((Double) value).doubleValue() <= 0.0);
			}
		}
		else {
			return null;	/** null to denote 'unknown' **/
		}
	}
	/**
	 * @param expression
	 * @return true {nneg} false {negt} null {unknown}
	 * @throws Exception
	 */
	protected static Boolean is_nneg_domain(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolConstant) {
			Object value = ((SymbolConstant) expression).get_number();
			if(value instanceof Integer) {
				return Boolean.valueOf(((Integer) value).intValue() < 0);
			}
			else if(value instanceof Long) {
				return Boolean.valueOf(((Long) value).longValue() < 0L);
			}
			else {
				return Boolean.valueOf(((Double) value).doubleValue() < 0.0);
			}
		}
		else {
			return SymbolFactory.is_usig(expression) || SymbolFactory.is_addr(expression);
		}
	}
	/**
	 * @param x
	 * @param y
	 * @return
	 * @throws Exception
	 */
	protected static boolean is_equivalence(SymbolExpression x, SymbolExpression y) throws Exception {
		return x.equals(y);
	}
	
}
