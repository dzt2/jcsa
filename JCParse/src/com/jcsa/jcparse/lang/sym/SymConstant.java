package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class SymConstant extends SymBasicExpression {

	private CConstant constant;
	protected SymConstant(CType data_type, CConstant constant) throws IllegalArgumentException {
		super(data_type);
		if(constant == null)
			throw new IllegalArgumentException("Invalid constant");
		else
			this.constant = constant;
	}
	
	/**
	 * @return the constant of this node
	 */
	public CConstant get_constant() { return this.constant; }

	@Override
	protected SymNode construct() throws Exception {
		return new SymConstant(this.get_data_type(), this.constant);
	}

	@Override
	public String generate_code() throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool().toString();
		case c_char:
		case c_uchar:		return "" + ((int) constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return constant.get_integer().toString();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return constant.get_long().toString();
		case c_float:		return constant.get_float().toString();
		case c_double:
		case c_ldouble:		return constant.get_double().toString();
		default: throw new IllegalArgumentException("Invalid: " + constant.get_type());
		}
	}
	
	/* constant value */
	public boolean get_bool() {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return constant.get_bool().booleanValue();
		case c_char:
		case c_uchar:	return constant.get_char().charValue() != '\0';
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return constant.get_integer().intValue() != 0;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return constant.get_long().longValue() != 0L;
		case c_float:	return constant.get_float().floatValue() != 0.0f;
		case c_double:
		case c_ldouble:	return constant.get_double().doubleValue() != 0.0;
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	public char get_char() {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return (char) (constant.get_bool().booleanValue()?1:0);
		case c_char:
		case c_uchar:	return (char) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return (char) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return (char) constant.get_long().longValue();
		case c_float:	return (char) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:	return (char) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	public short get_short() {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return (short) (constant.get_bool().booleanValue()?1:0);
		case c_char:
		case c_uchar:	return (short) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return (short) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return (short) constant.get_long().longValue();
		case c_float:	return (short) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:	return (short) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	public int get_int() {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return (int) (constant.get_bool().booleanValue()?1:0);
		case c_char:
		case c_uchar:	return (int) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return (int) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return (int) constant.get_long().longValue();
		case c_float:	return (int) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:	return (int) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	public long get_long() {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return (long) (constant.get_bool().booleanValue()?1:0);
		case c_char:
		case c_uchar:	return (long) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return (long) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return (long) constant.get_long().longValue();
		case c_float:	return (long) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:	return (long) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	public float get_float() {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return (float) (constant.get_bool().booleanValue()?1:0);
		case c_char:
		case c_uchar:	return (float) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return (float) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return (float) constant.get_long().longValue();
		case c_float:	return (float) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:	return (float) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	public double get_double() {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return (double) (constant.get_bool().booleanValue()?1:0);
		case c_char:
		case c_uchar:	return (double) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return (double) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return (double) constant.get_long().longValue();
		case c_float:	return (double) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:	return (double) constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	/**
	 * @return Long|Double
	 */
	public Object get_number() {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return Long.valueOf((constant.get_bool().booleanValue()?1:0));
		case c_char:
		case c_uchar:	return Long.valueOf(constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return Long.valueOf(constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return Long.valueOf(constant.get_long().longValue());
		case c_float:	return Double.valueOf(constant.get_float().floatValue());
		case c_double:
		case c_ldouble:	return Double.valueOf(constant.get_double().doubleValue());
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	
}
