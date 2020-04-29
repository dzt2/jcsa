package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * constant ==> {constant}
 * @author yukimula
 *
 */
public class SymConstant extends SymBasicExpression {

	protected SymConstant(CConstant value) throws IllegalArgumentException {
		super(value.get_type(), value);
	}
	
	/**
	 * get the constant that the expression describes
	 * @return
	 */
	public CConstant get_constant() { return (CConstant) this.value; }
	
	@Override
	public String toString() { 
		CConstant constant = this.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:
			return constant.get_bool().toString();
		case c_char:
		case c_uchar:
			int value = constant.get_char().charValue();
			return "" + value;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
			return constant.get_integer().toString();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
			return constant.get_long().toString();
		case c_float:
			return constant.get_float().toString();
		case c_double:
		case c_ldouble:
			return constant.get_double().toString();
		default: throw new IllegalArgumentException("Unknown: " + constant.get_type());
		}
	}
	
}
