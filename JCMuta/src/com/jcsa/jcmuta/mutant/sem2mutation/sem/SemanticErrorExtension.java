package com.jcsa.jcmuta.mutant.sem2mutation.sem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationUtil;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;

public class SemanticErrorExtension {
	
	/* constructor */
	/** the set of error assertions generated **/
	private Set<SemanticAssertion> error_set;
	/** the error assertions that have been parsed **/
	private Set<SemanticAssertion> visit_set;
	/** the singleton constructor **/
	private SemanticErrorExtension() {
		this.error_set = new HashSet<SemanticAssertion>();
		this.visit_set = new HashSet<SemanticAssertion>();
	}
	/** the singleton instance for extending the state error assertions **/
	private static final SemanticErrorExtension extension = new SemanticErrorExtension();
	
	/* verification methods */
	private CType get_data_type(CirExpression expression) throws Exception {
		return CTypeAnalyzer.get_value_type(expression.get_data_type());
	}
	private boolean in_boolean_context(CirExpression expression) throws Exception {
		CType data_type = this.get_data_type(expression);
		CirNode parent = expression.get_parent();
		
		if(CTypeAnalyzer.is_boolean(data_type)) {
			return true;
		}
		else if(parent instanceof CirIfStatement) {
			return ((CirIfStatement) parent).get_condition() == expression;
		}
		else if(parent instanceof CirCaseStatement) {
			return ((CirCaseStatement) parent).get_condition() == expression;
		}
		else {
			return false;
		}
	}
	private boolean in_numeric_context(CirExpression expression) throws Exception {
		return CTypeAnalyzer.is_number(this.get_data_type(expression));
	}
	private boolean is_definition_context(CirExpression expression) throws Exception {
		CirNode parent = expression.get_parent();
		
		if(parent instanceof CirAssignStatement) {
			return ((CirAssignStatement) parent).get_lvalue() == expression;
		}
		else {
			return false;
		}
	}
	private boolean cast_to_boolean(Object value) throws Exception {
		if(value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}
		else if(value instanceof Integer) {
			return ((Integer) value).intValue() != 0;
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue() != 0;
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue() != 0;
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private Object get_difference(Object source, Object target) throws Exception {
		if(source instanceof Boolean) {
			int src = (((Boolean) source).booleanValue()) ? 1 : 0;
			if(target instanceof Boolean) {
				int trg = ((Boolean) target).booleanValue() ? 1 : 0;
				return Long.valueOf(trg - src);
			}
			else if(target instanceof Integer) {
				int trg = ((Integer) target).intValue();
				return Long.valueOf(trg - src);
			}
			else if(target instanceof Long) {
				long trg = ((Long) target).longValue();
				return Long.valueOf(trg - src);
			}
			else if(target instanceof Double) {
				double trg = ((Double) target).doubleValue();
				return Double.valueOf(trg - src);
			}
			else {
				throw new IllegalArgumentException("Invalid target: " + target);
			}
		}
		else if(source instanceof Integer) {
			int src = ((Integer) source).intValue();
			if(target instanceof Boolean) {
				int trg = ((Boolean) target).booleanValue() ? 1 : 0;
				return Long.valueOf(trg - src);
			}
			else if(target instanceof Integer) {
				int trg = ((Integer) target).intValue();
				return Long.valueOf(trg - src);
			}
			else if(target instanceof Long) {
				long trg = ((Long) target).longValue();
				return Long.valueOf(trg - src);
			}
			else if(target instanceof Double) {
				double trg = ((Double) target).doubleValue();
				return Double.valueOf(trg - src);
			}
			else {
				throw new IllegalArgumentException("Invalid target: " + target);
			}
		}
		else if(source instanceof Long) {
			long src = ((Long) source).longValue();
			if(target instanceof Boolean) {
				int trg = ((Boolean) target).booleanValue() ? 1 : 0;
				return Long.valueOf(trg - src);
			}
			else if(target instanceof Integer) {
				int trg = ((Integer) target).intValue();
				return Long.valueOf(trg - src);
			}
			else if(target instanceof Long) {
				long trg = ((Long) target).longValue();
				return Long.valueOf(trg - src);
			}
			else if(target instanceof Double) {
				double trg = ((Double) target).doubleValue();
				return Double.valueOf(trg - src);
			}
			else {
				throw new IllegalArgumentException("Invalid target: " + target);
			}
		}
		else if(source instanceof Double) {
			double src = ((Double) source).doubleValue();
			if(target instanceof Boolean) {
				int trg = ((Boolean) target).booleanValue() ? 1 : 0;
				return Double.valueOf(trg - src);
			}
			else if(target instanceof Integer) {
				int trg = ((Integer) target).intValue();
				return Double.valueOf(trg - src);
			}
			else if(target instanceof Long) {
				long trg = ((Long) target).longValue();
				return Double.valueOf(trg - src);
			}
			else if(target instanceof Double) {
				double trg = ((Double) target).doubleValue();
				return Double.valueOf(trg - src);
			}
			else {
				throw new IllegalArgumentException("Invalid target: " + target);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid source: " + source);
		}
	}
	private Object get_summary(Object source, Object target) throws Exception {
		if(source instanceof Boolean) {
			int src = (((Boolean) source).booleanValue()) ? 1 : 0;
			if(target instanceof Boolean) {
				int trg = ((Boolean) target).booleanValue() ? 1 : 0;
				return Long.valueOf(trg + src);
			}
			else if(target instanceof Integer) {
				int trg = ((Integer) target).intValue();
				return Long.valueOf(trg + src);
			}
			else if(target instanceof Long) {
				long trg = ((Long) target).longValue();
				return Long.valueOf(trg + src);
			}
			else if(target instanceof Double) {
				double trg = ((Double) target).doubleValue();
				return Double.valueOf(trg + src);
			}
			else {
				throw new IllegalArgumentException("Invalid target: " + target);
			}
		}
		else if(source instanceof Integer) {
			int src = ((Integer) source).intValue();
			if(target instanceof Boolean) {
				int trg = ((Boolean) target).booleanValue() ? 1 : 0;
				return Long.valueOf(trg + src);
			}
			else if(target instanceof Integer) {
				int trg = ((Integer) target).intValue();
				return Long.valueOf(trg + src);
			}
			else if(target instanceof Long) {
				long trg = ((Long) target).longValue();
				return Long.valueOf(trg + src);
			}
			else if(target instanceof Double) {
				double trg = ((Double) target).doubleValue();
				return Double.valueOf(trg + src);
			}
			else {
				throw new IllegalArgumentException("Invalid target: " + target);
			}
		}
		else if(source instanceof Long) {
			long src = ((Long) source).longValue();
			if(target instanceof Boolean) {
				int trg = ((Boolean) target).booleanValue() ? 1 : 0;
				return Long.valueOf(trg + src);
			}
			else if(target instanceof Integer) {
				int trg = ((Integer) target).intValue();
				return Long.valueOf(trg + src);
			}
			else if(target instanceof Long) {
				long trg = ((Long) target).longValue();
				return Long.valueOf(trg + src);
			}
			else if(target instanceof Double) {
				double trg = ((Double) target).doubleValue();
				return Double.valueOf(trg + src);
			}
			else {
				throw new IllegalArgumentException("Invalid target: " + target);
			}
		}
		else if(source instanceof Double) {
			double src = ((Double) source).doubleValue();
			if(target instanceof Boolean) {
				int trg = ((Boolean) target).booleanValue() ? 1 : 0;
				return Double.valueOf(trg + src);
			}
			else if(target instanceof Integer) {
				int trg = ((Integer) target).intValue();
				return Double.valueOf(trg + src);
			}
			else if(target instanceof Long) {
				long trg = ((Long) target).longValue();
				return Double.valueOf(trg + src);
			}
			else if(target instanceof Double) {
				double trg = ((Double) target).doubleValue();
				return Double.valueOf(trg + src);
			}
			else {
				throw new IllegalArgumentException("Invalid target: " + target);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid source: " + source);
		}
	}
	
	/* extension methods */
	/**
	 * extend on the error assertion if it has not been extended
	 * @param assertion
	 * @throws Exception
	 */
	private void extend(SemanticAssertion assertion) throws Exception {
		if(assertion == null || !assertion.is_state_error())
			throw new IllegalArgumentException("Not state error");
		else if(!this.visit_set.contains(assertion)) {
			this.visit_set.add(assertion);
			
			switch(assertion.get_state_error_function()) {
			case trapping:	this.extend_trapping(assertion);	break;
			case active:	this.extend_active(assertion);		break;
			case disactive:	this.extend_disactive(assertion);	break;
			case mut_value:	this.extend_mut_value(assertion); 	break;
			case mut_refer: this.extend_mut_refer(assertion);	break;
			case not_value:	this.extend_not_value(assertion); 	break;
			case inc_value:	this.extend_inc_value(assertion);	break;
			case dec_value:	this.extend_dec_value(assertion);	break;
			case neg_value:	this.extend_neg_value(assertion); 	break;
			case rsv_value:	this.extend_rsv_value(assertion); 	break;
			case set_bool:	this.extend_set_bool(assertion);	break;
			case set_value:	this.extend_set_value(assertion); 	break;
			case dif_value:	this.extend_dif_value(assertion);	break;
			default: throw new IllegalArgumentException(
					"Unsupport: " + assertion.get_state_error_function());
			}
		}
	}
	/**
	 * <trapping> --> trapping
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_trapping(SemanticAssertion assertion) throws Exception {
		this.error_set.add(assertion);
	}
	/**
	 * <active> --> active
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_active(SemanticAssertion assertion) throws Exception {
		this.error_set.add(assertion);
	}
	/**
	 * <disactive> --> disactive
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_disactive(SemanticAssertion assertion) throws Exception {
		this.error_set.add(assertion);
	}
	/**
	 * <inc_value> --> inc_value <mut_value>
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_inc_value(SemanticAssertion assertion) throws Exception {
		this.error_set.add(assertion);
		CirExpression expression = (CirExpression) assertion.get_location();
		this.extend(assertion.get_assertions().mut_value(expression));
	}
	/**
	 * <dec_value> --> dec_value <mut_value>
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_dec_value(SemanticAssertion assertion) throws Exception {
		this.error_set.add(assertion);
		CirExpression expression = (CirExpression) assertion.get_location();
		this.extend(assertion.get_assertions().mut_value(expression));
	}
	/**
	 * <neg_value> --> neg_value <mut_value>
	 * 			   --> neg_value <set_value>
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_neg_value(SemanticAssertion assertion) throws Exception {
		CirExpression expression = (CirExpression) assertion.get_location();
		Object expr_value = SemanticMutationUtil.get_constant(expression);
		
		/** <neg_value> --> neg_value <set_value> **/
		if(expr_value != null) {
			if(expr_value instanceof Boolean) {
				boolean value = ((Boolean) expr_value).booleanValue();
				if(value) {
					this.error_set.add(assertion);
					this.extend(assertion.get_assertions().set_value(expression, -1));
				}
				else {
					/** equivalent mutation does not generate any errors **/
				}
			}
			else if(expr_value instanceof Integer) {
				int value = ((Integer) expr_value).intValue();
				if(value != 0) {
					this.error_set.add(assertion);
					this.extend(assertion.get_assertions().set_value(expression, -value));
				}
				else {
					/** equivalent mutation does not generate any errors **/
				}
			}
			else if(expr_value instanceof Long) {
				long value = ((Long) expr_value).longValue();
				if(value != 0) {
					this.error_set.add(assertion);
					this.extend(assertion.get_assertions().set_value(expression, -value));
				}
				else {
					/** equivalent mutation does not generate any errors **/
				}
			}
			else if(expr_value instanceof Double) {
				double value = ((Double) expr_value).doubleValue();
				if(value != 0) {
					this.error_set.add(assertion);
					this.extend(assertion.get_assertions().set_value(expression, -value));
				}
				else {
					/** equivalent mutation does not generate any errors **/
				}
			}
			else {
				throw new IllegalArgumentException("Invalid expr_value: " + expr_value);
			}
		}
		/** <neg_value> --> neg_value <mut_value> **/
		else {
			this.error_set.add(assertion);
			this.extend(assertion.get_assertions().mut_value(expression));
		}
	}
	/**
	 * <rsv_value> --> rsv_value <mut_value>
	 * 			   --> rsv_value <set_value>
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_rsv_value(SemanticAssertion assertion) throws Exception {
		CirExpression expression = (CirExpression) assertion.get_location();
		Object expr_value = SemanticMutationUtil.get_constant(expression);
		
		/** <rsv_value> --> rsv_value <set_value> **/
		if(expr_value != null) {
			if(expr_value instanceof Boolean) {
				boolean value = ((Boolean) expr_value).booleanValue();
				if(value) {
					this.error_set.add(assertion);
					this.extend(assertion.get_assertions().set_value(expression, ~1));
				}
				else {
					this.error_set.add(assertion);
					this.extend(assertion.get_assertions().set_value(expression, ~0));
				}
			}
			else if(expr_value instanceof Integer) {
				int value = ((Integer) expr_value).intValue();
				this.error_set.add(assertion);
				this.extend(assertion.get_assertions().set_value(expression, ~value));
			}
			else if(expr_value instanceof Long) {
				long value = ((Long) expr_value).longValue();
				this.error_set.add(assertion);
				this.extend(assertion.get_assertions().set_value(expression, ~value));
			}
			else {
				throw new IllegalArgumentException("Invalid expr_value: " + expr_value);
			}
		}
		/** <rsv_value> --> rsv_value <mut_value> **/
		else {
			this.error_set.add(assertion);
			this.extend(assertion.get_assertions().mut_value(expression));
		}
	}
	/**
	 * <set_bool> --> set_bool, <not_value>
	 * 			  --> <set_value>
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_set_bool(SemanticAssertion assertion) throws Exception {
		CirExpression expression = (CirExpression) assertion.get_location();
		if(this.in_boolean_context(expression)) {
			this.error_set.add(assertion);
			this.extend(assertion.get_assertions().not_value(expression));
		}
		else if(this.in_numeric_context(expression)) {
			boolean value = (boolean) assertion.get_operand(1);
			if(value)
				this.extend(assertion.get_assertions().set_value(expression, 1));
			else
				this.extend(assertion.get_assertions().set_value(expression, 0));
		}
		else {
			throw new IllegalArgumentException("Invalid context");
		}
	}
	/**
	 * <not_value> --> not_value
	 * 			   --> <mut_value>
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_not_value(SemanticAssertion assertion) throws Exception {
		CirExpression expression = (CirExpression) assertion.get_location();
		if(this.in_boolean_context(expression)) {
			this.error_set.add(assertion);
		}
		else if(this.in_numeric_context(expression)) {
			this.extend(assertion.get_assertions().mut_value(expression));
		}
		else {
			throw new IllegalArgumentException("Invalid context");
		}
	}
	/**
	 * <set_value>	--> <set_bool>
	 * 				--> set_value <dif_value> <neg|rsv>?
	 * 				--> set_value <mut_value>
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_set_value(SemanticAssertion assertion) throws Exception {
		CirExpression expression = (CirExpression) assertion.get_location();
		Object expr_value = SemanticMutationUtil.get_constant(expression);
		Object muta_value = assertion.get_operand(1);
		
		/** <set_value> --> <set_bool> **/
		if(this.in_boolean_context(expression)) {
			if(expr_value == null) {
				this.extend(assertion.get_assertions().set_value(
						expression, this.cast_to_boolean(muta_value)));
			}
			else {
				boolean source = this.cast_to_boolean(expr_value);
				boolean target = this.cast_to_boolean(muta_value);
				if(source != target) {
					this.extend(assertion.get_assertions().set_value(
							expression, this.cast_to_boolean(muta_value)));
				}
				else { /** equivalent mutation as it is **/ }
			}
		}
		/** <set_value> --> <dif_value> | <mut_value> **/
		else if(this.in_numeric_context(expression)) {
			if(expr_value == null) {
				this.error_set.add(assertion);
				this.extend(assertion.get_assertions().mut_value(expression));
			}
			else {
				Object difference = this.get_difference(expr_value, muta_value);
				if(difference instanceof Long) {
					long diff = ((Long) difference).longValue();
					if(diff != 0) {
						this.error_set.add(assertion);
						this.extend(assertion.get_assertions().diff_value(expression, diff));
					}
					else {
						/** equivalent mutation **/
					}
				}
				else {
					double diff = ((Double) difference).doubleValue();
					if(diff != 0) {
						this.error_set.add(assertion);
						this.extend(assertion.get_assertions().diff_value(expression, diff));
					}
					else {
						/** equivalent mutation **/
					}
				}
			}
		}
		else {
			throw new IllegalArgumentException("Invalid context");
		}
	}
	/**
	 * <dif_value> --> inc_value | dec_value
	 * 			   --> <set_value>
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_dif_value(SemanticAssertion assertion) throws Exception {
		CirExpression expression = (CirExpression) assertion.get_location();
		Object expr_value = SemanticMutationUtil.get_constant(expression);
		Object diff_value = assertion.get_operand(1);
		
		if(diff_value instanceof Long) {
			long diff = ((Long) diff_value).longValue();
			if(diff == 0) { return; /** equivalent mutant **/ }
			else if(diff > 0) {
				this.error_set.add(assertion);
				this.extend(assertion.get_assertions().inc_value(expression));
			}
			else {
				this.error_set.add(assertion);
				this.extend(assertion.get_assertions().dec_value(expression));
			}
		}
		else {
			double diff = ((Double) diff_value).doubleValue();
			if(diff == 0) { return; /** equivalent mutant **/ }
			else if(diff > 0) {
				this.error_set.add(assertion);
				this.extend(assertion.get_assertions().inc_value(expression));
			}
			else {
				this.error_set.add(assertion);
				this.extend(assertion.get_assertions().dec_value(expression));
			}
		}
		
		/** --> <set_value> **/
		if(expr_value != null) {
			Object sum = this.get_summary(expr_value, diff_value);
			if(sum instanceof Long) {
				this.extend(assertion.get_assertions().set_value(expression, ((Long) sum).longValue()));
			}
			else if(sum instanceof Double) {
				this.extend(assertion.get_assertions().set_value(expression, ((Double) sum).doubleValue()));
			}
		}
	}
	/**
	 * 
	 * @param assertion
	 * @throws Exception
	 */
	private void extend_mut_value(SemanticAssertion assertion) throws Exception {
		CirExpression expression = (CirExpression) assertion.get_location();
		if(this.in_boolean_context(expression)) {
			this.extend(assertion.get_assertions().not_value(expression));
		}
		else {
			this.error_set.add(assertion);
		}
	}
	private void extend_mut_refer(SemanticAssertion assertion) throws Exception {
		CirExpression expression = (CirExpression) assertion.get_location();
		this.error_set.add(assertion);
		if(!this.is_definition_context(expression)) {
			this.extend(assertion.get_assertions().mut_value(expression));
		}
	}
	/**
	 * extend all the error assertions in the same location
	 * @param assertions
	 * @throws Exception
	 */
	private void extend_all(Iterable<SemanticAssertion> assertions) throws Exception {
		this.visit_set.clear(); this.error_set.clear();
		for(SemanticAssertion assertion : assertions) {
			if(assertion.is_state_error()) {
				this.extend(assertion);
			}
		}
	}
	
	/**
	 * extend the error assertions within one CIR node
	 * @param error_node
	 * @throws Exception
	 */
	protected static List<SemanticAssertion> extend(
			Iterable<SemanticAssertion> error_assertions) throws Exception {
		extension.extend_all(error_assertions);
		List<SemanticAssertion> all_assertions = new ArrayList<SemanticAssertion>();
		for(SemanticAssertion assertion : extension.error_set) {
			all_assertions.add(assertion);
		}
		return all_assertions;
	}
	
}
