package com.jcsa.jcparse.lang.lexical;

import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * Constant in C program could be a <i>number</i>: <br>
 * 1. character: 'a', '\036', '\xff', L'1' <br>
 * 2. integer: 0, 037, 150, 0xa0ff, 647L, 4218258ul <br>
 * 3. floating: 0., .125, 5.486e-3, 28E32 <br>
 * 
 * @author yukimula
 */
public class CConstant {

	/** data represented by this constant **/
	private Object data;
	/** type of this data **/
	private CBasicType type;

	/** create a null constant **/
	public CConstant() {
		data = null;
	}

	/**
	 * whether there is data in the constant
	 * 
	 * @return
	 */
	public boolean has_value() {
		return data != null;
	}

	/**
	 * get the basic type of this constant
	 * 
	 * @return
	 */
	public CBasicType get_type() {
		return type;
	}
	
	/**
	 * set as <b>_Bool</b>
	 * @param val
	 */
	public void set_bool(boolean val) {
		type = CBasicTypeImpl.bool_type;
		data = Boolean.valueOf(val);
	}
	
	/**
	 * set as <b>char</b>
	 * 
	 * @param ch
	 */
	public void set_char(char ch) {
		type = CBasicTypeImpl.char_type;
		data = Character.valueOf(ch);
	}

	/**
	 * set as <b>wchar_t</b>
	 * 
	 * @param ch
	 */
	public void set_wchar(char ch) {
		type = CBasicTypeImpl.uchar_type;
		data = Character.valueOf(ch);
	}

	/**
	 * set as <b>int</b>
	 * 
	 * @param val
	 */
	public void set_int(int val) {
		type = CBasicTypeImpl.int_type;
		data = Integer.valueOf(val);
	}

	/**
	 * set as <b>unsigned</b>
	 * 
	 * @param val
	 */
	public void set_uint(int val) {
		type = CBasicTypeImpl.uint_type;
		data = Integer.valueOf(val);
	}

	/**
	 * set as <b>long</b>
	 * 
	 * @param val
	 */
	public void set_long(long val) {
		type = CBasicTypeImpl.long_type;
		data = Long.valueOf(val);
	}

	/**
	 * set as <b>unsigned long</b>
	 * 
	 * @param val
	 */
	public void set_ulong(long val) {
		type = CBasicTypeImpl.ulong_type;
		data = Long.valueOf(val);
	}

	/**
	 * set as <b>long long</b>
	 * 
	 * @param val
	 */
	public void set_llong(long val) {
		type = CBasicTypeImpl.llong_type;
		data = Long.valueOf(val);
	}

	/**
	 * set as <b>unsigned long long</b>
	 * 
	 * @param val
	 */
	public void set_ullong(long val) {
		type = CBasicTypeImpl.ullong_type;
		data = Long.valueOf(val);
	}

	/**
	 * set as <b>float</b>
	 * 
	 * @param val
	 */
	public void set_float(float val) {
		type = CBasicTypeImpl.float_type;
		data = Float.valueOf(val);
	}

	/**
	 * set as <b>double</b>
	 * 
	 * @param val
	 */
	public void set_double(double val) {
		type = CBasicTypeImpl.double_type;
		data = Double.valueOf(val);
	}

	/**
	 * set as <b>long double</b>
	 * 
	 * @param val
	 */
	public void set_ldouble(double val) {
		type = CBasicTypeImpl.ldouble_type;
		data = Double.valueOf(val);
	}
	
	/**
	 * get the boolean value hold by the constant
	 * @return
	 */
	public Boolean get_bool() { return (Boolean) data; }
	
	/**
	 * get as char or wchar_t
	 * 
	 * @return
	 */
	public Character get_char() {
		return (Character) data;
	}

	/**
	 * get as int or unsigned
	 * 
	 * @return
	 */
	public Integer get_integer() {
		return (Integer) data;
	}

	/**
	 * get as long, long long, unsigned long, unsigned long long
	 * 
	 * @return
	 */
	public Long get_long() {
		return (Long) data;
	}

	/**
	 * get as float
	 * 
	 * @return
	 */
	public Float get_float() {
		return (Float) data;
	}

	/**
	 * get as double | long double
	 * 
	 * @return
	 */
	public Double get_double() {
		return (Double) data;
	}

	@Override
	public String toString() {
		if (data == null)
			return "null";
		else
			return data.toString();
	}
}
