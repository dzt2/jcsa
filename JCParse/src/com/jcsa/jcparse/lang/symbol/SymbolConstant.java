package com.jcsa.jcparse.lang.symbol;

import java.util.Map;

import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * Numeric const value {bool|char|short|int|long|float|double}
 * 
 * @author yukimula
 *
 */
public class SymbolConstant extends SymbolBasicExpression {
	
	/** the constant value **/
	private CConstant constant;
	
	/**
	 * It creates a symbolic value to represent numeric constant
	 * @param constant
	 * @throws Exception
	 */
	private SymbolConstant(CConstant constant) throws Exception {
		super(SymbolClass.constant, constant.get_type());
		this.constant = constant;
	}
	
	/**
	 * It creates a symbolic value to represent numeric constant
	 * @param constant
	 * @throws Exception
	 */
	protected static SymbolConstant create(CConstant constant) throws Exception {
		if(constant == null) {
			throw new IllegalArgumentException("Invalid constant: null");
		}
		else {
			return new SymbolConstant(constant);
		}
	}

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolConstant(this.constant);
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		return this.constant.toString();
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/* value getters */
	/**
	 * @return the original constant object of the symbolic value
	 */
	public CConstant 	get_constant() { return this.constant; }
	/**
	 * @return taken as boolean value
	 */
	public Boolean		get_bool() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return this.constant.get_bool().booleanValue();
		case c_char:		return Boolean.valueOf(this.constant.get_char() != '\0');
		case c_uchar:		return Boolean.valueOf(this.constant.get_char() != '\0');
		case c_short:		return Boolean.valueOf(this.constant.get_integer() != 0);
		case c_ushort:		return Boolean.valueOf(this.constant.get_integer() != 0);
		case c_int:			return Boolean.valueOf(this.constant.get_integer() != 0);
		case c_uint:		return Boolean.valueOf(this.constant.get_integer() != 0);
		case c_long:		return Boolean.valueOf(this.constant.get_long() != 0L);
		case c_ulong:		return Boolean.valueOf(this.constant.get_long() != 0L);
		case c_llong:		return Boolean.valueOf(this.constant.get_long() != 0L);
		case c_ullong:		return Boolean.valueOf(this.constant.get_long() != 0L);
		case c_float:		return Boolean.valueOf(this.constant.get_float() != 0.0f);
		case c_double:		return Boolean.valueOf(this.constant.get_double() != 0.0);
		case c_ldouble:		return Boolean.valueOf(this.constant.get_double() != 0.0);
		default:			throw new IllegalArgumentException("Invalid: " + this.constant);
		}
	}
	/**
	 * @return taken as character
	 */
	public Character	get_char() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return this.constant.get_bool() ? '\1' : '\0';
		case c_char:		return Character.valueOf(this.constant.get_char());
		case c_uchar:		return Character.valueOf(this.constant.get_char());
		case c_short:		return Character.valueOf((char) this.constant.get_integer().intValue());
		case c_ushort:		return Character.valueOf((char) this.constant.get_integer().intValue());
		case c_int:			return Character.valueOf((char) this.constant.get_integer().intValue());
		case c_uint:		return Character.valueOf((char) this.constant.get_integer().intValue());
		case c_long:		return Character.valueOf((char) this.constant.get_long().longValue());
		case c_ulong:		return Character.valueOf((char) this.constant.get_long().longValue());
		case c_llong:		return Character.valueOf((char) this.constant.get_long().longValue());
		case c_ullong:		return Character.valueOf((char) this.constant.get_long().longValue());
		case c_float:		return Character.valueOf((char) this.constant.get_float().floatValue());
		case c_double:		return Character.valueOf((char) this.constant.get_double().doubleValue());
		case c_ldouble:		return Character.valueOf((char) this.constant.get_double().doubleValue());
		default:			throw new IllegalArgumentException("Invalid: " + this.constant);
		}
	}
	/**
	 * @return taken as short-int
	 */
	public Short		get_short() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (short) (this.constant.get_bool() ? 1 : 0);
		case c_char:		return Short.valueOf((short) this.constant.get_char().charValue());
		case c_uchar:		return Short.valueOf((short) this.constant.get_char().charValue());
		case c_short:		return Short.valueOf((short) this.constant.get_integer().intValue());
		case c_ushort:		return Short.valueOf((short) this.constant.get_integer().intValue());
		case c_int:			return Short.valueOf((short) this.constant.get_integer().intValue());
		case c_uint:		return Short.valueOf((short) this.constant.get_integer().intValue());
		case c_long:		return Short.valueOf((short) this.constant.get_long().longValue());
		case c_ulong:		return Short.valueOf((short) this.constant.get_long().longValue());
		case c_llong:		return Short.valueOf((short) this.constant.get_long().longValue());
		case c_ullong:		return Short.valueOf((short) this.constant.get_long().longValue());
		case c_float:		return Short.valueOf((short) this.constant.get_float().floatValue());
		case c_double:		return Short.valueOf((short) this.constant.get_double().doubleValue());
		case c_ldouble:		return Short.valueOf((short) this.constant.get_double().doubleValue());
		default:			throw new IllegalArgumentException("Invalid: " + this.constant);
		}
	}
	/**
	 * @return taken as normal int
	 */
	public Integer		get_int() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (int) (this.constant.get_bool() ? 1 : 0);
		case c_char:		return Integer.valueOf((int) this.constant.get_char().charValue());
		case c_uchar:		return Integer.valueOf((int) this.constant.get_char().charValue());
		case c_short:		return Integer.valueOf((int) this.constant.get_integer().intValue());
		case c_ushort:		return Integer.valueOf((int) this.constant.get_integer().intValue());
		case c_int:			return Integer.valueOf((int) this.constant.get_integer().intValue());
		case c_uint:		return Integer.valueOf((int) this.constant.get_integer().intValue());
		case c_long:		return Integer.valueOf((int) this.constant.get_long().longValue());
		case c_ulong:		return Integer.valueOf((int) this.constant.get_long().longValue());
		case c_llong:		return Integer.valueOf((int) this.constant.get_long().longValue());
		case c_ullong:		return Integer.valueOf((int) this.constant.get_long().longValue());
		case c_float:		return Integer.valueOf((int) this.constant.get_float().floatValue());
		case c_double:		return Integer.valueOf((int) this.constant.get_double().doubleValue());
		case c_ldouble:		return Integer.valueOf((int) this.constant.get_double().doubleValue());
		default:			throw new IllegalArgumentException("Invalid: " + this.constant);
		}
	}
	/**
	 * @return taken as long int
	 */
	public Long			get_long() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (long) (this.constant.get_bool() ? 1 : 0);
		case c_char:		return Long.valueOf((long) this.constant.get_char().charValue());
		case c_uchar:		return Long.valueOf((long) this.constant.get_char().charValue());
		case c_short:		return Long.valueOf((long) this.constant.get_integer().intValue());
		case c_ushort:		return Long.valueOf((long) this.constant.get_integer().intValue());
		case c_int:			return Long.valueOf((long) this.constant.get_integer().intValue());
		case c_uint:		return Long.valueOf((long) this.constant.get_integer().intValue());
		case c_long:		return Long.valueOf((long) this.constant.get_long().longValue());
		case c_ulong:		return Long.valueOf((long) this.constant.get_long().longValue());
		case c_llong:		return Long.valueOf((long) this.constant.get_long().longValue());
		case c_ullong:		return Long.valueOf((long) this.constant.get_long().longValue());
		case c_float:		return Long.valueOf((long) this.constant.get_float().floatValue());
		case c_double:		return Long.valueOf((long) this.constant.get_double().doubleValue());
		case c_ldouble:		return Long.valueOf((long) this.constant.get_double().doubleValue());
		default:			throw new IllegalArgumentException("Invalid: " + this.constant);
		}
	}
	/**
	 * @return taken as float
	 */
	public Float		get_float() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (float) (this.constant.get_bool() ? 1 : 0);
		case c_char:		return Float.valueOf((float) this.constant.get_char().charValue());
		case c_uchar:		return Float.valueOf((float) this.constant.get_char().charValue());
		case c_short:		return Float.valueOf((float) this.constant.get_integer().intValue());
		case c_ushort:		return Float.valueOf((float) this.constant.get_integer().intValue());
		case c_int:			return Float.valueOf((float) this.constant.get_integer().intValue());
		case c_uint:		return Float.valueOf((float) this.constant.get_integer().intValue());
		case c_long:		return Float.valueOf((float) this.constant.get_long().longValue());
		case c_ulong:		return Float.valueOf((float) this.constant.get_long().longValue());
		case c_llong:		return Float.valueOf((float) this.constant.get_long().longValue());
		case c_ullong:		return Float.valueOf((float) this.constant.get_long().longValue());
		case c_float:		return Float.valueOf((float) this.constant.get_float().floatValue());
		case c_double:		return Float.valueOf((float) this.constant.get_double().doubleValue());
		case c_ldouble:		return Float.valueOf((float) this.constant.get_double().doubleValue());
		default:			throw new IllegalArgumentException("Invalid: " + this.constant);
		}
	}
	/**
	 * @return taken as double
	 */
	public Double		get_double() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (double) (this.constant.get_bool() ? 1 : 0);
		case c_char:		return Double.valueOf((double) this.constant.get_char().charValue());
		case c_uchar:		return Double.valueOf((double) this.constant.get_char().charValue());
		case c_short:		return Double.valueOf((double) this.constant.get_integer().intValue());
		case c_ushort:		return Double.valueOf((double) this.constant.get_integer().intValue());
		case c_int:			return Double.valueOf((double) this.constant.get_integer().intValue());
		case c_uint:		return Double.valueOf((double) this.constant.get_integer().intValue());
		case c_long:		return Double.valueOf((double) this.constant.get_long().longValue());
		case c_ulong:		return Double.valueOf((double) this.constant.get_long().longValue());
		case c_llong:		return Double.valueOf((double) this.constant.get_long().longValue());
		case c_ullong:		return Double.valueOf((double) this.constant.get_long().longValue());
		case c_float:		return Double.valueOf((double) this.constant.get_float().floatValue());
		case c_double:		return Double.valueOf((double) this.constant.get_double().doubleValue());
		case c_ldouble:		return Double.valueOf((double) this.constant.get_double().doubleValue());
		default:			throw new IllegalArgumentException("Invalid: " + this.constant);
		}
	}
	/**
	 * @return taken as long or double
	 */
	public Object		get_number() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (long) (this.constant.get_bool() ? 1 : 0);
		case c_char:		return Long.valueOf((long) this.constant.get_char().charValue());
		case c_uchar:		return Long.valueOf((long) this.constant.get_char().charValue());
		case c_short:		return Long.valueOf((long) this.constant.get_integer().intValue());
		case c_ushort:		return Long.valueOf((long) this.constant.get_integer().intValue());
		case c_int:			return Long.valueOf((long) this.constant.get_integer().intValue());
		case c_uint:		return Long.valueOf((long) this.constant.get_integer().intValue());
		case c_long:		return Long.valueOf((long) this.constant.get_long().longValue());
		case c_ulong:		return Long.valueOf((long) this.constant.get_long().longValue());
		case c_llong:		return Long.valueOf((long) this.constant.get_long().longValue());
		case c_ullong:		return Long.valueOf((long) this.constant.get_long().longValue());
		case c_float:		return Double.valueOf((double) this.constant.get_float().floatValue());
		case c_double:		return Double.valueOf((double) this.constant.get_double().doubleValue());
		case c_ldouble:		return Double.valueOf((double) this.constant.get_double().doubleValue());
		default:			throw new IllegalArgumentException("Invalid: " + this.constant);
		}
	}

	
	@Override
	protected SymbolExpression symb_replace(Map<SymbolExpression, SymbolExpression> name_value_map) throws Exception {
		return this;
	}
	
}
