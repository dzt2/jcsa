package com.jcsa.jcparse.parse.symbol;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CEnumeratorList;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * It provides value domain analysis on symbolic evaluation.
 *
 * @author yukimula
 *
 */
class SymbolValueDomain {

	/* definitions */
	/** the minimal value in the domain or null if it represents negative infinite **/
	private Double minimal_value;
	/** the maximal value in the domain or null if it represents positive infinite  **/
	private Double maximal_value;
	/**
	 * create a domain
	 * @param minimal_value
	 * @param maximal_value
	 */
	protected SymbolValueDomain(Double minimal_value, Double maximal_value) {
		if(minimal_value == null || minimal_value <= -Double.MAX_VALUE) {
			this.minimal_value = null;
		}
		else {
			this.minimal_value = minimal_value;
		}
		if(maximal_value == null || maximal_value >= Double.MAX_VALUE) {
			this.maximal_value = null;
		}
		else {
			this.maximal_value = maximal_value;
		}
	}
	/**
	 * create a value domain based on data type
	 * @param type
	 * @return value domain of the type or null if the type is not numeric
	 * @throws Exception
	 */
	protected static SymbolValueDomain domain_of_type(CType type) throws Exception {
		if(type == null) {
			return null;
		}
		else {
			type = CTypeAnalyzer.get_value_type(type);
			Double minimal_value = null, maximal_value = null;
			if(type instanceof CBasicType) {
				switch(((CBasicType) type).get_tag()) {
				case c_bool:
				{
					minimal_value = (double) 0;
					maximal_value = (double) 1;
					break;
				}
				case c_char:
				{
					minimal_value = (double) -128;
					maximal_value = (double) 127;
					break;
				}
				case c_uchar:
				{
					minimal_value = (double) 0;
					maximal_value = (double) 255;
					break;
				}
				case c_short:
				{
					minimal_value = (double) Short.MIN_VALUE;
					maximal_value = (double) Short.MAX_VALUE;
					break;
				}
				case c_ushort:
				{
					minimal_value = (double) 0;
					maximal_value = 2.0 * Short.MAX_VALUE + 1.0;
					break;
				}
				case c_int:
				{
					minimal_value = (double) Integer.MIN_VALUE;
					maximal_value = (double) Integer.MAX_VALUE;
					break;
				}
				case c_uint:
				{
					minimal_value = (double) 0;
					maximal_value = 2.0 * Integer.MAX_VALUE + 1.0;
					break;
				}
				case c_long:
				case c_llong:
				{
					minimal_value = (double) Long.MIN_VALUE;
					maximal_value = (double) Long.MAX_VALUE;
					break;
				}
				case c_ulong:
				case c_ullong:
				{
					minimal_value = (double) 0;
					maximal_value = 2.0 * Long.MAX_VALUE + 1.0;
					break;
				}
				case c_float:
				{
					minimal_value = (double) -Float.MAX_VALUE;
					maximal_value = (double) Float.MAX_VALUE;
					break;
				}
				case c_double:
				case c_ldouble:
				{
					minimal_value = (double) -Double.MAX_VALUE;
					maximal_value = (double) Double.MAX_VALUE;
					break;
				}
				default:
				{
					return null;
				}
				}
				return new SymbolValueDomain(minimal_value, maximal_value);
			}
			else if(type instanceof CArrayType || type instanceof CPointerType) {
				minimal_value = (double) 0;
				maximal_value = (double) Long.MAX_VALUE;
				return new SymbolValueDomain(minimal_value, maximal_value);
			}
			else if(type instanceof CEnumType) {
				CEnumeratorList elist = ((CEnumType) type).get_enumerator_list();
				for(int k = 0; k < elist.size(); k++) {
					int value = elist.get_enumerator(k).get_value();
					if(minimal_value == null || value < minimal_value) {
						minimal_value = (double) value;
					}
					if(maximal_value == null || value > maximal_value) {
						maximal_value = (double) value;
					}
				}
				if(minimal_value == null) { minimal_value = (double) Integer.MIN_VALUE; }
				if(maximal_value == null) { maximal_value = (double) Integer.MAX_VALUE; }
				return new SymbolValueDomain(minimal_value, maximal_value);
			}
			else {
				return null;
			}
		}
	}

	/* getters */
	protected boolean is_minimal_infinite() { return this.minimal_value == null; }
	protected boolean is_maximal_infinite() { return this.maximal_value == null; }
	protected Double get_minimal_value() { return this.minimal_value; }
	protected Double get_maximal_value() { return this.maximal_value; }
	protected boolean in_domain(double value) {
		if(this.minimal_value == null) {
			if(this.maximal_value == null) {
				return true;
			}
			else {
				return value <= this.maximal_value.doubleValue();
			}
		}
		else {
			if(this.maximal_value == null) {
				return value >= this.minimal_value.doubleValue();
			}
			else {
				return value >= this.minimal_value.doubleValue() && value <= this.maximal_value.doubleValue();
			}
		}
	}
	@Override
	public String toString() {
		String left, right;
		if(this.minimal_value == null) {
			left = "-INF";
		}
		else {
			left = this.minimal_value.toString();
		}
		if(this.maximal_value == null) {
			right = "+INF";
		}
		else {
			right = this.maximal_value.toString();
		}
		return "[" + left + ", " + right + "]";
	}

}
