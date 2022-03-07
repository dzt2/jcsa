package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.lexical.CConstant;

public class SymbolConstant extends SymbolBasicExpression {
	
	/** the constant of this node's value **/
	private CConstant constant;
	
	/**
	 * @param constant		the value of this constant node
	 * @throws Exception
	 */
	private SymbolConstant(CConstant constant) throws Exception {
		super(SymbolClass.constant, constant.get_type());
		this.constant = constant;
	}
	
	/* value getters */
	/**
	 * @return the const-value of this constant node
	 */
	public CConstant get_constant() { return this.constant; }
	/**
	 * @return the constant when it is taken as boolean
	 */
	public Boolean get_bool() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return this.constant.get_bool();
		case c_char:		
		case c_uchar:		return Boolean.valueOf(this.constant.get_char() != '\0');
		case c_short:
		case c_ushort:		
		case c_int:
		case c_uint:		return Boolean.valueOf(this.constant.get_integer() != 0);
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Boolean.valueOf(this.constant.get_long() != 0L);
		case c_float:		return Boolean.valueOf(this.constant.get_float() != 0.0f);
		case c_double:
		case c_ldouble:		return Boolean.valueOf(this.constant.get_double() != 0.0);
		default:			return null;
		}
	}
	/**
	 * @return the constant when it is taken as character
	 */
	public Character get_char() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return this.constant.get_bool() ? '\1' : '\0';
		case c_char:
		case c_uchar:		return Character.valueOf(this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Character.valueOf((char) this.constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Character.valueOf((char) this.constant.get_long().longValue());
		case c_float:		return Character.valueOf((char) this.constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Character.valueOf((char) this.constant.get_double().doubleValue());
		default: 			return null;
		}
	}
	/**
	 * @return the constant when it is taken as short integer
	 */
	public Short get_short() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (short) (this.constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return Short.valueOf((short) this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Short.valueOf((short) this.constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Short.valueOf((short) this.constant.get_long().longValue());
		case c_float:		return Short.valueOf((short) this.constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Short.valueOf((short) this.constant.get_double().doubleValue());
		default: 			return null;
		}
	}
	/**
	 * @return the constant when it is taken as integer
	 */
	public Integer get_int() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (int) (this.constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return Integer.valueOf(this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Integer.valueOf(this.constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Integer.valueOf((int) this.constant.get_long().longValue());
		case c_float:		return Integer.valueOf((int) this.constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Integer.valueOf((int) this.constant.get_double().doubleValue());
		default: 			return null;
		}
	}
	/** 
	 * @return the constant when it is taken as long integer
	 */
	public Long get_long() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (long) (this.constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return Long.valueOf(this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Long.valueOf(this.constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Long.valueOf(this.constant.get_long().longValue());
		case c_float:		return Long.valueOf((long) this.constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Long.valueOf((long) this.constant.get_double().doubleValue());
		default: 			return null;
		}
	}
	/**
	 * @return the constant when it is taken as float
	 */
	public Float get_float() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (float) (this.constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return Float.valueOf(this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Float.valueOf(this.constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Float.valueOf(this.constant.get_long().longValue());
		case c_float:		return Float.valueOf(this.constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Float.valueOf((float) this.constant.get_double().doubleValue());
		default: 			return null;
		}
	}
	/**
	 * @return the constant when it is taken as double
	 */
	public Double get_double() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (double) (this.constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return Double.valueOf(this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Double.valueOf(this.constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Double.valueOf(this.constant.get_long().longValue());
		case c_float:		return Double.valueOf(this.constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Double.valueOf(this.constant.get_double().doubleValue());
		default: 			return null;
		}
	}
	/**
	 * @return Long | Double
	 */
	public Object get_number() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return Long.valueOf((this.constant.get_bool() ? 1 : 0));
		case c_char:
		case c_uchar:		return Long.valueOf(this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Long.valueOf(this.constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Long.valueOf(this.constant.get_long().longValue());
		case c_float:		return Double.valueOf(this.constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Double.valueOf(this.constant.get_double().doubleValue());
		default: 			return null;
		}
	}
	
	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolConstant(this.constant);
	}
	
	@Override
	protected String get_code(boolean simplified) throws Exception {
		return this.constant.toString();
	}
	
	@Override
	protected boolean is_refer_type() { return false; }
	
	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param constant	{bool|char|short|integer|long|float|double|CConstant}
	 * @return
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
	
}
