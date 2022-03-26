package com.jcsa.jcparse.parse.parser3;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It defines the value-domain of SymbolExpression.
 * 
 * @author yukimula
 *
 */
public class SymbolDomain {
	
	/* definitions */
	/** the minimal value hold in the domain (include) **/
	private SymbolConstant min_value;
	/** the maximal value hold in the domain (include) **/
	private SymbolConstant max_value;
	/**
	 * It creates a domain of minimal and maximal value defining domain
	 * @param min_value	the minimal value hold in the domain (included)
	 * @param max_value	the maximal value hold in the domain (included)
	 * @throws Exception
	 */
	protected SymbolDomain(Object min_value, Object max_value) throws Exception {
		if(min_value == null) {
			throw new IllegalArgumentException("Invalid min_value: null");
		}
		else if(max_value == null) {
			throw new IllegalArgumentException("Invalid max_value: null");
		}
		else {
			this.min_value = SymbolFactory.sym_constant(min_value);
			this.max_value = SymbolFactory.sym_constant(max_value);
		}
	}
	
	/* getters */
	/**
	 * @return	the minimal value hold in the domain (include)
	 */
	public Object get_min_value() { return this.min_value.get_number(); }
	/**
	 * @return	the maximal value hold in the domain (include)
	 */
	public Object get_max_value() { return this.max_value.get_number(); }
	/**
	 * @return whether the min_value is infinite
	 */
	public boolean is_min_infinite() {
		Object min_value = this.get_min_value();
		if(min_value instanceof Long) {
			long value = ((Long) min_value).longValue();
			return value <= Long.MIN_VALUE;
		}
		else {
			double value = ((Double) min_value).doubleValue();
			return value <= -Double.MAX_VALUE;
		}
	}
	/**
	 * @return whether the max_value is infinite
	 */
	public boolean is_max_infinite() {
		Object max_value = this.get_max_value();
		if(max_value instanceof Long) {
			long value = ((Long) max_value).longValue();
			return value >= Long.MAX_VALUE;
		}
		else {
			double value = ((Double) max_value).doubleValue();
			return value >= Double.MAX_VALUE;
		}
	}
	
	/* general methods */
	@Override
	public SymbolDomain clone() { 
		try {
			return new SymbolDomain(this.get_min_value(), this.get_max_value());
		}
		catch(Exception ex) {
			return null;
		}
	}
	@Override
	public String toString() { 
		String lvalue, rvalue;
		if(this.is_min_infinite()) {
			lvalue = "-INF";
		}
		else {
			lvalue = this.min_value.toString();
		}
		if(this.is_max_infinite()) {
			rvalue = "INF";
		}
		else {
			rvalue = this.max_value.toString();
		}
		return "[" + lvalue + ", " + rvalue + "]";
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof SymbolDomain) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the constant is in the domain
	 */
	public boolean in_domain(SymbolConstant expression) {
		if(expression == null) {
			return false;
		}
		else {
			Object number = expression.get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				long lvalue = this.min_value.get_long();
				long rvalue = this.max_value.get_long();
				return (value >= lvalue) && (value <= rvalue);
			}
			else {
				double value = ((Double) number).doubleValue();
				double lvalue = this.min_value.get_double();
				double rvalue = this.max_value.get_double();
				return (value >= lvalue) && (value <= rvalue);
			}
		}
	}
	
}
