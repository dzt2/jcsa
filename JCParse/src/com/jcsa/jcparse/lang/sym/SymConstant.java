package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * constant	|-- {bool|char|int|long|float|double}
 * @author yukimula
 *
 */
public class SymConstant extends SymBasicExpression {

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

	@Override
	protected SymNode clone_self() {
		return new SymConstant(this.get_constant());
	}

	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		Object value = this.get_constant_value();
		if(value instanceof Boolean) {
			if(ast_code) {
				if(((Boolean) value).booleanValue()) {
					return "1";
				}
				else {
					return "0";
				}
			}
			else {
				return value.toString();
			}
		}
		else if(value instanceof Character) {
			return Integer.valueOf(((Character) value).charValue()).toString();
		}
		else if(value instanceof Integer) {
			return value.toString();
		}
		else if(value instanceof Long) {
			return value.toString();
		}
		else if(value instanceof Float) {
			return value.toString();
		}
		else if(value instanceof Double) {
			return value.toString();
		}
		else {
			throw new IllegalArgumentException("Invalid code: " + this.get_data_type());
		}
	}

}
