package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class SymbolConstant extends SymbolBasicExpression {
	
	/** constant of the value of the expression node **/
	private CConstant constant;
	
	private SymbolConstant(CType data_type, CConstant constant) throws IllegalArgumentException {
		super(data_type);
		if(constant == null)
			throw new IllegalArgumentException("Invalid constant: null");
		else 
			this.constant = constant;
	}
	
	/* value getter */
	/**
	 * @return constant of the value of the expression node
	 */
	public CConstant get_constant() { return this.constant; }
	public Boolean get_bool() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return this.constant.get_bool();
		case c_char:
		case c_uchar:		return Boolean.valueOf(this.constant.get_char().charValue() != '\0');
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Boolean.valueOf(this.constant.get_integer().intValue() != 0);
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Boolean.valueOf(this.constant.get_long().longValue() != 0L);
		case c_float:		return Boolean.valueOf(this.constant.get_float().floatValue() != 0.0f);
		case c_double:
		case c_ldouble:		return Boolean.valueOf(this.constant.get_double().doubleValue() != 0.0);
		default: 			return null;
		}
	}
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
	public Integer get_int() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (int) (this.constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return Integer.valueOf((int) this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Integer.valueOf((int) this.constant.get_integer().intValue());
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
	public Long get_long() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (long) (this.constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return Long.valueOf((long) this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Long.valueOf((long) this.constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Long.valueOf((long) this.constant.get_long().longValue());
		case c_float:		return Long.valueOf((long) this.constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Long.valueOf((long) this.constant.get_double().doubleValue());
		default: 			return null;
		}
	}
	public Float get_float() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (float) (this.constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return Float.valueOf((float) this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Float.valueOf((float) this.constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Float.valueOf((float) this.constant.get_long().longValue());
		case c_float:		return Float.valueOf((float) this.constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Float.valueOf((float) this.constant.get_double().doubleValue());
		default: 			return null;
		}
	}
	public Double get_double() {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:		return (double) (this.constant.get_bool() ? 1 : 0);
		case c_char:
		case c_uchar:		return Double.valueOf((double) this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Double.valueOf((double) this.constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Double.valueOf((double) this.constant.get_long().longValue());
		case c_float:		return Double.valueOf((double) this.constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Double.valueOf((double) this.constant.get_double().doubleValue());
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
		case c_uchar:		return Long.valueOf((long) this.constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Long.valueOf((long) this.constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Long.valueOf((long) this.constant.get_long().longValue());
		case c_float:		return Double.valueOf((double) this.constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Double.valueOf((double) this.constant.get_double().doubleValue());
		default: 			return null;
		}
	}
	
	@Override
	protected SymbolNode construct() throws Exception {
		return new SymbolConstant(this.get_data_type(), this.get_constant());
	}
	
	/**
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	protected static SymbolConstant create(CConstant constant) throws Exception {
		return new SymbolConstant(constant.get_type(), constant);
	}
	
}
