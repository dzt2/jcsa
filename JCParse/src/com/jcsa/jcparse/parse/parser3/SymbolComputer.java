package com.jcsa.jcparse.parse.parser3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CEnumeratorList;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It implements the constant-evaluation and the domain-evaluation.
 * 
 * @author yukimula
 *
 */
public final class SymbolComputer {
	
	/* singleton */	 /** construction **/  private SymbolComputer() { }
	private static final SymbolComputer computer = new SymbolComputer();
	
	/* constant-computation */
	/**
	 * @param operand	the numeric constant for being arithmetically negated
	 * @return			-((long|double) operand.number)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_arith_neg(SymbolConstant operand) throws Exception {
		if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			Object number = operand.get_number(), result;
			if(number instanceof Long) {
				result = Long.valueOf(-((Long) number).longValue());
			}
			else {
				result = Double.valueOf(-((Double) number).doubleValue());
			}
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param operand	the integer constant for being bit-wise reserved
	 * @return			~((long) operand.number)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_bitws_rsv(SymbolConstant operand) throws Exception {
		if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			long value = operand.get_long().longValue();
			Object result = Long.valueOf(~value);
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param operand	the boolean constant for being logically negated
	 * @return			!((boolean) operand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_logic_not(SymbolConstant operand) throws Exception {
		if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			boolean value = operand.get_bool().booleanValue();
			Boolean result = Boolean.valueOf(!value);
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param cast_type	the type to cast the operand
	 * 					{bool|char|short|int|long|float|double|enum|pointer}
	 * @param operand	the operand to be casted to the given type
	 * @return			((type) operand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_type_cast(CType cast_type, SymbolConstant operand) throws Exception {
		if(cast_type == null) {
			throw new IllegalArgumentException("Invalid cast_type: null");
		}
		else if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			cast_type = SymbolFactory.get_type(cast_type);
			Object result;
			
			if(cast_type instanceof CBasicType) {
				switch(((CBasicType) cast_type).get_tag()) {
				case c_bool:	result = operand.get_bool();	break;
				case c_char:	result = operand.get_char();	break;
				case c_uchar:	result = operand.get_char();	break;
				case c_short:	result = operand.get_short();	break;
				case c_ushort:	result = operand.get_short();	break;
				case c_int:		result = operand.get_int();		break;
				case c_uint:	result = operand.get_int();		break;
				case c_long:	result = operand.get_long();	break;
				case c_ulong:	result = operand.get_long();	break;
				case c_llong:	result = operand.get_long();	break;
				case c_ullong:	result = operand.get_long();	break;
				case c_float:	result = operand.get_float();	break;
				case c_double:	result = operand.get_double();	break;
				case c_ldouble:	result = operand.get_double();	break;
				default:		throw new IllegalArgumentException(cast_type.generate_code());
				}
			}
			else if(cast_type instanceof CEnumType) {
				result = operand.get_int();
			}
			else if(cast_type instanceof CPointerType) {
				result = operand.get_long();
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
	 * @return				((long|double) loperand + roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_arith_add(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
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
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
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
				if(rvalue instanceof Long) {
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
	 * @return				((long|double) loperand - roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_arith_sub(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
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
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
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
				if(rvalue instanceof Long) {
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
	 * @return				((long|double) loperand * roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_arith_mul(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
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
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
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
				if(rvalue instanceof Long) {
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
	 * @return				((long|double) loperand / roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_arith_div(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
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
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
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
				if(rvalue instanceof Long) {
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
	 * @return				((long) loperand % roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_arith_mod(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			long x = loperand.get_long().longValue();
			long y = roperand.get_long().longValue();
			Object result = Long.valueOf(x % y);
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand	
	 * @param roperand
	 * @return				((long) loperand & roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_bitws_and(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			long x = loperand.get_long().longValue();
			long y = roperand.get_long().longValue();
			Object result = Long.valueOf(x & y);
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand	
	 * @param roperand
	 * @return				((long) loperand | roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_bitws_ior(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			long x = loperand.get_long().longValue();
			long y = roperand.get_long().longValue();
			Object result = Long.valueOf(x | y);
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand	
	 * @param roperand
	 * @return				((long) loperand ^ roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_bitws_xor(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			long x = loperand.get_long().longValue();
			long y = roperand.get_long().longValue();
			Object result = Long.valueOf(x ^ y);
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand	
	 * @param roperand
	 * @return				((long) loperand << roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_bitws_lsh(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			long x = loperand.get_long().longValue();
			long y = roperand.get_long().longValue();
			Object result = Long.valueOf(x << y);
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand	
	 * @param roperand
	 * @return				((long) loperand >> roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_bitws_rsh(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			long x = loperand.get_long().longValue();
			long y = roperand.get_long().longValue();
			Object result = Long.valueOf(x >> y);
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand	
	 * @param roperand
	 * @return				((bool) loperand && roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_logic_and(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			boolean x = loperand.get_bool().booleanValue();
			boolean y = roperand.get_bool().booleanValue();
			Boolean result = Boolean.valueOf(x && y);
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand	
	 * @param roperand
	 * @return				((bool) loperand || roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_logic_ior(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			boolean x = loperand.get_bool().booleanValue();
			boolean y = roperand.get_bool().booleanValue();
			Boolean result = Boolean.valueOf(x || y);
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand --> roperand
	 * @throws Exception
	 */
	private	SymbolConstant	compute_logic_imp(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			boolean x = loperand.get_bool().booleanValue();
			boolean y = roperand.get_bool().booleanValue();
			Boolean result = Boolean.valueOf(!x || y);
			return SymbolFactory.sym_constant(result);
		}
	}
	/**
	 * @param loperand	
	 * @param roperand
	 * @return				((long|double) loperand > roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_greater_tn(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
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
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
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
				if(rvalue instanceof Long) {
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
	 * @return				((long|double) loperand >= roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_greater_eq(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
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
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
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
				if(rvalue instanceof Long) {
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
	 * @return				((long|double) loperand < roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_smaller_tn(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
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
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
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
				if(rvalue instanceof Long) {
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
	 * @return				((long|double) loperand <= roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_smaller_eq(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
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
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
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
				if(rvalue instanceof Long) {
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
	 * @return				((long|double) loperand == roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_equal_with(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
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
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
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
				if(rvalue instanceof Long) {
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
	 * @return				((long|double) loperand != roperand)
	 * @throws Exception
	 */
	private	SymbolConstant	compute_not_equals(SymbolConstant loperand, SymbolConstant roperand) throws Exception {
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
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
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
				if(rvalue instanceof Long) {
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
	 * @param operator	[pos, neg, not]
	 * @param operand	
	 * @return			
	 * @throws Exception
	 */
	private	SymbolConstant	compute(COperator operator, SymbolConstant operand) throws Exception {
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			switch(operator) {
			case negative:	return this.compute_arith_neg(operand);
			case bit_not:	return this.compute_bitws_rsv(operand);
			case logic_not:	return this.compute_logic_not(operand);
			default:		throw new IllegalArgumentException(operator.toString());
			}
		}
	}
	/**
	 * @param operator	[+, -, *, /, %, &, |, ^, <<, >>, &&, ||, imp, <, <=, >, >=, ==, !=]
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	private	SymbolConstant	compute(COperator operator, SymbolConstant loperand, SymbolConstant roperand) throws Exception {
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
			case positive:		return this.compute_logic_imp(loperand, roperand);
			case greater_tn:	return this.compute_greater_tn(loperand, roperand);
			case greater_eq:	return this.compute_greater_eq(loperand, roperand);
			case smaller_tn:	return this.compute_smaller_tn(loperand, roperand);
			case smaller_eq:	return this.compute_smaller_eq(loperand, roperand);
			case equal_with:	return this.compute_equal_with(loperand, roperand);
			case not_equals:	return this.compute_not_equals(loperand, roperand);
			default:		throw new IllegalArgumentException(operator.toString());
			}
		}
	}
	/**
	 * unary operator compuation
	 * @param operator	[pos, neg, not, inc, dec]
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	public static SymbolConstant 	do_compute(COperator operator, SymbolConstant operand) throws Exception {
		return computer.compute(operator, operand);
	}
	/**
	 * binary operator cmoputation
	 * @param operator
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolConstant 	do_compute(COperator operator, SymbolConstant loperand, SymbolConstant roperand) throws Exception {
		return computer.compute(operator, loperand, roperand);
	}
	/**
	 * type cast computation
	 * @param cast_type
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	public static SymbolConstant 	do_compute(CType cast_type, SymbolConstant operand) throws Exception {
		return computer.compute_type_cast(cast_type, operand);
	}
	
	/* domain-comparison */
	/**
	 * @param constant	
	 * @param value		
	 * @return			whether the constant equals with input value (translated as constant)
	 * @throws Exception
	 */
	public static boolean	compare_values(SymbolConstant constant, Object value) throws Exception {
		if(constant == null) {
			throw new IllegalArgumentException("Invalid constant: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as null");
		}
		else {
			return computer.compute_equal_with(constant, SymbolFactory.
					sym_constant(value)).get_bool().booleanValue();
		}
	}
	/**
	 * @param loperand
	 * @param roperand	
	 * @return	True if loperand == roperand must hold; False if it cannot be decided.
	 * @throws Exception
	 */
	public static boolean	is_equivalence(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			if(loperand instanceof SymbolConstant) {
				if(roperand instanceof SymbolConstant) {
					return computer.compute_equal_with((SymbolConstant) loperand, 
							(SymbolConstant) roperand).get_bool().booleanValue();
				}
				else {
					return false;
				}
			}
			else {
				if(roperand instanceof SymbolConstant) {
					return false;
				}
				else {
					return loperand.equals(roperand);
				}
			}
		}
	}
	/**
	 * @param expression
	 * @return the domain of numeric expression or null if it is non-numeric
	 * @throws Exception
	 */
	public static SymbolDomain get_domain(SymbolExpression expression) throws Exception {
		if(expression == null) {
			return null;
		}
		else if(expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) expression).get_number();
			return new SymbolDomain(number, number);
		}
		else {
			CType type = SymbolFactory.get_type(expression.get_data_type());
			Object lvalue, rvalue;
			
			if(type instanceof CBasicType) {
				switch(((CBasicType) type).get_tag()) {
				case c_bool:
					lvalue = Long.valueOf(0); 
					rvalue = Long.valueOf(1);
					break;
				case c_char:
				case c_uchar:
					lvalue = Long.valueOf(0);
					rvalue = Long.valueOf(2 * Character.MAX_VALUE);
					break;
				case c_short:
					lvalue = Long.valueOf(Short.MIN_VALUE);
					rvalue = Long.valueOf(Short.MAX_VALUE);
					break;
				case c_ushort:
					lvalue = Long.valueOf(0);
					rvalue = Long.valueOf(2 * Short.MAX_VALUE);
					break;
				case c_int:
					lvalue = Long.valueOf(Integer.MIN_VALUE);
					rvalue = Long.valueOf(Integer.MAX_VALUE);
					break;
				case c_uint:
					lvalue = Long.valueOf(0);
					rvalue = Long.valueOf(2 * Integer.MAX_VALUE);
					break;
				case c_long:
				case c_llong:
					lvalue = Long.MIN_VALUE;
					rvalue = Long.MAX_VALUE;
					break;
				case c_ulong:
				case c_ullong:
					lvalue = Long.valueOf(0);
					rvalue = Long.MAX_VALUE;
					break;
				case c_float:
					lvalue = Double.valueOf(-Float.MAX_VALUE);
					rvalue = Double.valueOf(Float.MAX_VALUE);
					break;
				case c_double:
				case c_ldouble:
					lvalue = Double.valueOf(-Double.MAX_VALUE);
					rvalue = Double.valueOf(Double.MAX_VALUE);
					break;
				default:		return null;
				}
			}
			else if(type instanceof CPointerType) {
				lvalue = Long.valueOf(0);
				rvalue = Long.MAX_VALUE;
			}
			else if(type instanceof CEnumType) {
				List<Integer> values = new ArrayList<Integer>();
				CEnumeratorList elist = ((CEnumType) type).get_enumerator_list();
				for(int k = 0; k < elist.size(); k++) {
					values.add(elist.get_enumerator(k).get_value());
				}
				Collections.sort(values);
				lvalue = Long.valueOf(values.get(0));
				rvalue = Long.valueOf(values.get(values.size() - 1));
			}
			else {
				return null;
			}
			return new SymbolDomain(lvalue, rvalue);
		}
	}
	/**
	 * @param expression
	 * @return whether the expression must be in the domain of positive value
	 * @throws Exception
	 */
	public static boolean	is_positive(SymbolExpression expression) throws Exception {
		SymbolDomain domain = get_domain(expression);
		Object min_value = domain.get_min_value();
		if(min_value instanceof Long) {
			return ((Long) min_value).longValue() > 0;
		}
		else {
			return ((Double) min_value).doubleValue() > 0;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression must be in the domain of negative value
	 * @throws Exception
	 */
	public static boolean	is_negative(SymbolExpression expression) throws Exception {
		SymbolDomain domain = get_domain(expression);
		Object max_value = domain.get_max_value();
		if(max_value instanceof Long) {
			return ((Long) max_value).longValue() < 0;
		}
		else {
			return ((Double) max_value).doubleValue() < 0;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression must be in domain of non-zero value
	 * @throws Exception
	 */
	public static boolean 	is_non_zero(SymbolExpression expression) throws Exception {
		return is_positive(expression) || is_negative(expression);
	}
	
}

