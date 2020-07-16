package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * Constant	|--	bool | char | int | long | float | double
 * @author yukimula
 *
 */
public class SymConstant extends SymBasicExpression {
	
	private CConstant constant;
	
	protected SymConstant(CType data_type, CConstant constant) {
		super(data_type);
		this.constant = constant;
	}
	
	/**
	 * @return boolean | character | integer | long | float | double
	 */
	public Object get_value() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:	return this.constant.get_bool();
		case c_char:	return this.constant.get_char();
		case c_uchar:	return this.constant.get_char();
		case c_short:	return this.constant.get_integer();
		case c_ushort:	return this.constant.get_integer();
		case c_int:		return this.constant.get_integer();
		case c_uint:	return this.constant.get_integer();
		case c_long:	return this.constant.get_long();
		case c_ulong:	return this.constant.get_long();
		case c_llong:	return this.constant.get_long();
		case c_ullong:	return this.constant.get_long();
		case c_float:	return this.constant.get_float();
		case c_double:	return this.constant.get_double();
		case c_ldouble:	return this.constant.get_double();
		default: 		return null;	// unknown value
		}
	}
	/**
	 * @return boolean of the value or null
	 */
	public Boolean boolean_of() {
		Object value = this.get_value();
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
			return null;
		}
	}
	/**
	 * @return integer or null
	 */
	public Long integer_of() {
		Object value = this.get_value();
		if(value instanceof Boolean) {
			if(((Boolean) value).booleanValue())
				return Long.valueOf(1L);
			else
				return Long.valueOf(0L);
		}
		else if(value instanceof Character) {
			return Long.valueOf(((Character) value).charValue());
		}
		else if(value instanceof Integer) {
			return Long.valueOf(((Integer) value).intValue());
		}
		else if(value instanceof Long) {
			return Long.valueOf(((Long) value).longValue());
		}
		else if(value instanceof Float) {
			return Long.valueOf(((Float) value).longValue());
		}
		else if(value instanceof Double) {
			return Long.valueOf(((Double) value).longValue());
		}
		else {
			return null;
		}
	}
	/**
	 * @return double or null
	 */
	public Double double_of() {
		Object value = this.get_value();
		if(value instanceof Boolean) {
			if(((Boolean) value).booleanValue())
				return Double.valueOf(1.0);
			else
				return Double.valueOf(0.0);
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
			return Double.valueOf(((Float) value).longValue());
		}
		else if(value instanceof Double) {
			return Double.valueOf(((Double) value).longValue());
		}
		else {
			return null;
		}
	}
	/**
	 * @return long or double or null
	 */
	public Object number_of() {
		Object value = this.get_value();
		if(value instanceof Boolean) {
			if(((Boolean) value).booleanValue())
				return Long.valueOf(1L);
			else
				return Long.valueOf(0L);
		}
		else if(value instanceof Character) {
			return Long.valueOf(((Character) value).charValue());
		}
		else if(value instanceof Integer) {
			return Long.valueOf(((Integer) value).intValue());
		}
		else if(value instanceof Long) {
			return Long.valueOf(((Long) value).longValue());
		}
		else if(value instanceof Float) {
			return Double.valueOf(((Float) value).longValue());
		}
		else if(value instanceof Double) {
			return Double.valueOf(((Double) value).longValue());
		}
		else {
			return null;
		}
	}
	
	@Override
	protected SymNode new_self() {
		return new SymConstant(this.get_data_type(), constant);
	}
	
	@Override
	protected String generate_code(boolean ast_style) throws Exception {
		return this.number_of().toString();
	}

}
