package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * constant	|-- {bool|char|int|long|float|double}
 * @author yukimula
 */
public class SymConstant extends SymBasicExpression {
	
	/**
	 * @param constant as the token of the node
	 */
	protected SymConstant(CConstant constant) {
		super(constant.get_type(), constant);
	}
	
	/**
	 * @return the constant that the node describes
	 */
	public CConstant get_constant() { return (CConstant) this.get_token(); }
	
	/**
	 * @return bool | char | int | long | float | double | null
	 */
	public Object get_constant_value() {
		CConstant constant = this.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool: 		return constant.get_bool();
		case c_char:
		case c_uchar:		return constant.get_char();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return constant.get_integer();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return constant.get_long();
		case c_float:		return constant.get_float();
		case c_double:	
		case c_ldouble:		return constant.get_double();
		default: 			return null;
		}
	}
	
	/**
	 * @return parsed as boolean
	 */
	public boolean get_boolean() {
		Object value = this.get_constant_value();
		if(value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}
		else if(value instanceof Character) {
			return ((Character) value).charValue() != '\0';
		}
		else if(value instanceof Integer) {
			return ((Integer) value).intValue() != 0;
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue() != 0L;
		}
		else if(value instanceof Float) {
			return ((Float) value).floatValue() != 0.0f;
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue() != 0.0;
		}
		else {
			throw new IllegalArgumentException("Invalid value: " + value);
		}
	}
	
	/**
	 * @return parsed as long integer
	 */
	public long get_integer() {
		Object value = this.get_constant_value();
		if(value instanceof Boolean) {
			if(((Boolean) value).booleanValue())
				return 1L;
			else
				return 0L;
		}
		else if(value instanceof Character) {
			return Long.valueOf(((Character) value).charValue());
		}
		else if(value instanceof Integer) {
			return Long.valueOf(((Integer) value).intValue());
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue();
		}
		else if(value instanceof Float) {
			return Long.valueOf((long) ((Float) value).floatValue());
		}
		else if(value instanceof Double) {
			return Long.valueOf((long) ((Double) value).doubleValue());
		}
		else {
			throw new IllegalArgumentException("Invalid value: " + value);
		}
	}
	
	/**
	 * @return parsed as long double
	 */
	public double get_double() {
		Object value = this.get_constant_value();
		if(value instanceof Boolean) {
			if(((Boolean) value).booleanValue())
				return 1.0;
			else
				return 0.0;
		}
		else if(value instanceof Character) {
			return Double.valueOf(((Character) value).charValue());
		}
		else if(value instanceof Integer) {
			return Double.valueOf(((Integer) value).intValue());
		}
		else if(value instanceof Long) {
			return Double.valueOf(((Long) value).longValue());
		}
		else if(value instanceof Float) {
			return Double.valueOf(((Float) value).floatValue());
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue();
		}
		else {
			throw new IllegalArgumentException("Invalid value: " + value);
		}
	}
	
	@Override
	protected SymNode clone_self() {
		return new SymConstant(this.get_constant());
	}

	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		Object value = this.get_constant_value();
		if(ast_code) {
			if(value instanceof Boolean) {
				if(((Boolean) value).booleanValue())
					return "1";
				else
					return "0";
			}
			else if(value instanceof Character) {
				if(Character.isAlphabetic(((Character) value).charValue())
						|| Character.isDigit(((Character) value).charValue())) {
					return "\'" + value.toString() + "\'";
				}
				else {
					return Integer.valueOf(((Character) value).charValue()).toString();
				}
			}
			else if(value instanceof Integer
					|| value instanceof Long
					|| value instanceof Float
					|| value instanceof Double) {
				return value.toString();
			}
			else {
				throw new IllegalArgumentException("Unable to generate: " + value);
			}
		}
		else {
			if(value instanceof Boolean) {
				return value.toString();
			}
			else if(value instanceof Character) {
				if(Character.isAlphabetic(((Character) value).charValue())
						|| Character.isDigit(((Character) value).charValue())) {
					return "\'" + value.toString() + "\'";
				}
				else {
					return Integer.valueOf(((Character) value).charValue()).toString();
				}
			}
			else if(value instanceof Integer
					|| value instanceof Long
					|| value instanceof Float
					|| value instanceof Double) {
				return value.toString();
			}
			else {
				throw new IllegalArgumentException("Unable to generate: " + value);
			}
		}
	}

}
