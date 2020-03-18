package com.jcsa.jcmuta.mutant.sem2mutation.sem;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationUtil;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

/**
 * It is used to process the error propagation in program.
 * 
 * @author yukimula
 *
 */
public abstract class StateErrorProcess {
	
	protected List<SemanticAssertion> const_assertions;
	protected List<SemanticAssertion> error_assertions;
	protected StateErrorProcess() {
		this.const_assertions = new ArrayList<SemanticAssertion>();
		this.error_assertions = new ArrayList<SemanticAssertion>();
	}
	
	/**
	 * process the source error to generate the next error in propagation at specified target
	 * @param source_error
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public List<List<SemanticAssertion>> process(Iterable<SemanticAssertion> assertions, CirNode target) throws Exception {
		if(assertions == null)
			throw new IllegalArgumentException("Invalid source_error: null");
		else {
			this.const_assertions.clear(); this.error_assertions.clear();
			
			for(SemanticAssertion source_assertion : assertions) {
				switch(source_assertion.get_state_error_function()) {
				case trapping:	break;
				case active:	this.process_active(source_assertion, target); 		break;
				case disactive:	this.process_disactive(source_assertion, target);	break;
				case mut_value:	this.process_mut_value(source_assertion, target); 	break;
				case mut_refer:	this.process_mut_refer(source_assertion, target); 	break;
				case not_value:	this.process_not_value(source_assertion, target); 	break;
				case inc_value:	this.process_inc_value(source_assertion, target); 	break;
				case dec_value:	this.process_dec_value(source_assertion, target); 	break;
				case neg_value: this.process_neg_value(source_assertion, target); 	break;
				case rsv_value: this.process_rsv_value(source_assertion, target); 	break;
				case set_bool: 	this.process_set_bool(source_assertion, target); 	break;
				case set_value:	this.process_set_value(source_assertion, target); 	break;
				case dif_value:	this.process_dif_value(source_assertion, target); 	break;
				default: throw new IllegalArgumentException("Unknown: " + source_assertion.get_state_error_function());
				}
			}
			
			List<SemanticAssertion> const_assertions = new ArrayList<SemanticAssertion>();
			for(SemanticAssertion assertion : this.const_assertions) {
				const_assertions.add(assertion);
			}
			this.const_assertions.clear();
			
			List<SemanticAssertion> error_assertions = new ArrayList<SemanticAssertion>();
			for(SemanticAssertion assertion : this.error_assertions) {
				error_assertions.add(assertion);
			}
			this.error_assertions.clear();
			
			List<List<SemanticAssertion>> cons_errors = new ArrayList<List<SemanticAssertion>>();
			cons_errors.add(const_assertions); cons_errors.add(error_assertions); return cons_errors;
		}
	}
	
	protected abstract void process_active(SemanticAssertion source_assertion, CirNode target) throws Exception;
	protected abstract void process_disactive(SemanticAssertion source_assertion, CirNode target) throws Exception;
	protected abstract void process_mut_value(SemanticAssertion source_assertion, CirNode target) throws Exception;
	protected abstract void process_mut_refer(SemanticAssertion source_assertion, CirNode target) throws Exception;
	protected abstract void process_not_value(SemanticAssertion source_assertion, CirNode target) throws Exception;
	protected abstract void process_inc_value(SemanticAssertion source_assertion, CirNode target) throws Exception;
	protected abstract void process_dec_value(SemanticAssertion source_assertion, CirNode target) throws Exception;
	protected abstract void process_neg_value(SemanticAssertion source_assertion, CirNode target) throws Exception;
	protected abstract void process_rsv_value(SemanticAssertion source_assertion, CirNode target) throws Exception;
	protected abstract void process_set_bool(SemanticAssertion source_assertion, CirNode target) throws Exception;
	protected abstract void process_set_value(SemanticAssertion source_assertion, CirNode target) throws Exception;
	protected abstract void process_dif_value(SemanticAssertion source_assertion, CirNode target) throws Exception;
	
	protected void throw_error_propagation(SemanticAssertion source_assertion, CirNode target) {
		throw new IllegalArgumentException("Invalid error propagation: " + source_assertion + " to " + target);
	}
	protected Object cast_to_numeric_value(Object value) throws Exception {
		if(value == null) {
			return null;
		}
		else if(value instanceof Boolean) {
			if(((Boolean) value).booleanValue())
				return Long.valueOf(1);
			else return Long.valueOf(0);
		}
		else if(value instanceof Integer) {
			return Long.valueOf(((Integer) value).intValue());
		}
		else if(value instanceof Long) {
			return (Long) value;
		}
		else if(value instanceof Float) {
			return Double.valueOf(((Float) value).floatValue());
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue();
		}
		else {
			throw new IllegalArgumentException("INvalid value: " + value);
		}
	}
	protected boolean is_zero(Object value) throws Exception {
		if(value == null) {
			return false;
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue() == 0;
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue() == 0;
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	protected boolean is_positive(Object value) throws Exception {
		if(value == null) {
			return false;
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue() > 0;
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue() > 0;
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	protected boolean is_negative(Object value) throws Exception {
		if(value == null) {
			return false;
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue() < 0;
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue() < 0;
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * get the long or double value hold by the expression
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected Object get_constant(CirExpression expression) throws Exception {
		Object value = SemanticMutationUtil.get_constant(expression);
		return this.cast_to_numeric_value(value);
	}
	protected boolean is_one(Object value) throws Exception {
		if(value == null) {
			return false;
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue() == 1;
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue() == 1;
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	protected boolean is_neg_one(Object value) throws Exception {
		if(value == null) {
			return false;
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue() == -1;
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue() == -1;
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	protected boolean is_odd_number(Object value) throws Exception {
		if(value == null) {
			return false;
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue() % 2 == 1;
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue() % 2 == 1;
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	protected boolean is_even_number(Object value) throws Exception {
		if(value == null) {
			return false;
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue() % 2 == 0;
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue() % 2 == 0;
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	protected boolean is_big_number(Object value) throws Exception {
		if(value == null) {
			return false;
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue() >= SemanticMutationParser.max_shifting * 2;
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue() >= SemanticMutationParser.max_shifting * 2;
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	
	protected Object add(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(lvalue == null || rvalue == null) return null;
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Long.valueOf(x + y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return Double.valueOf(x + y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Double.valueOf(x + y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return Double.valueOf(x + y);
			}
		}
	}
	protected Object sub(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		if(lvalue == null || rvalue == null) return null;
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Long.valueOf(x - y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return Double.valueOf(x - y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Double.valueOf(x - y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return Double.valueOf(x - y);
			}
		}
	}
	protected Object mul(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(this.is_zero(lvalue) || this.is_zero(rvalue)) {
			return Long.valueOf(0);
		}
		else if(lvalue == null || rvalue == null) {
			return null;
		}
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Long.valueOf(x * y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return Double.valueOf(x * y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Double.valueOf(x * y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return Double.valueOf(x * y);
			}
		}
	}
	protected Object div(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(this.is_zero(lvalue)) {
			return Long.valueOf(0);
		}
		else if(lvalue == null || rvalue == null) {
			return null;
		}
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Long.valueOf(x / y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return Double.valueOf(x / y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Double.valueOf(x / y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return Double.valueOf(x / y);
			}
		}
	}
	protected Object mod(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(this.is_zero(lvalue)) {
			return Long.valueOf(0);
		}
		else if(lvalue == null || rvalue == null) {
			return null;
		}
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Long.valueOf(x % y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return Double.valueOf(x % y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Double.valueOf(x % y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return Double.valueOf(x % y);
			}
		}
	}
	protected Object and(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(this.is_zero(lvalue) || this.is_zero(rvalue)) {
			return Long.valueOf(0);
		}
		else if(lvalue == null || rvalue == null) {
			return null;
		}
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Long.valueOf(x & y);
			}
			else {
				throw new IllegalArgumentException("Invalid operation");
			}
		}
		else {
			throw new IllegalArgumentException("Invalid operation");
		}
	}
	protected Object ior(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(lvalue == null || rvalue == null) {
			return null;
		}
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Long.valueOf(x | y);
			}
			else {
				throw new IllegalArgumentException("Invalid operation");
			}
		}
		else {
			throw new IllegalArgumentException("Invalid operation");
		}
	}
	protected Object xor(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(lvalue == null || rvalue == null) {
			return null;
		}
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Long.valueOf(x ^ y);
			}
			else {
				throw new IllegalArgumentException("Invalid operation");
			}
		}
		else {
			throw new IllegalArgumentException("Invalid operation");
		}
	}
	protected Object lsh(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(this.is_zero(lvalue)) {
			return Long.valueOf(0);
		}
		else if(lvalue == null || rvalue == null) {
			return null;
		}
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Long.valueOf(x << y);
			}
			else {
				throw new IllegalArgumentException("Invalid operation");
			}
		}
		else {
			throw new IllegalArgumentException("Invalid operation");
		}
	}
	protected Object rsh(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(this.is_zero(lvalue)) {
			return Long.valueOf(0);
		}
		else if(lvalue == null || rvalue == null) {
			return null;
		}
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return Long.valueOf(x >> y);
			}
			else {
				throw new IllegalArgumentException("Invalid operation");
			}
		}
		else {
			throw new IllegalArgumentException("Invalid operation");
		}
	}
	
	protected Boolean grt(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(lvalue == null || rvalue == null) return null;
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x > y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x > y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x > y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x > y;
			}
		}
	}
	protected Boolean gre(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(lvalue == null || rvalue == null) return null;
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x >= y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x >= y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x >= y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x >= y;
			}
		}
	}
	protected Boolean smt(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(lvalue == null || rvalue == null) return null;
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x < y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x < y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x < y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x < y;
			}
		}
	}
	protected Boolean sme(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(lvalue == null || rvalue == null) return null;
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x <= y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x <= y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x <= y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x <= y;
			}
		}
	}
	protected Boolean eqv(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(lvalue == null || rvalue == null) return null;
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x == y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x == y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x == y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x == y;
			}
		}
	}
	protected Boolean neq(Object lvalue, Object rvalue) throws Exception {
		lvalue = this.cast_to_numeric_value(lvalue);
		rvalue = this.cast_to_numeric_value(rvalue);
		
		if(lvalue == null || rvalue == null) return null;
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x != y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x != y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x != y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x != y;
			}
		}
	}
	
}
