package com.jcsa.jcmuta.mutant.sem2mutation.muta;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * The semantic assertions are connected with each other via the inference relationships.
 * @author yukimula
 *
 */
public class SemanticAssertions {
	
	/* constructor */
	/** to make sure the uniqueness of the assertions **/
	private Map<String, SemanticAssertion> assertions;
	/**
	 * create a semantic graph of which nodes are semantic assertions
	 * while the edges are the inference between them.
	 */
	protected SemanticAssertions() {
		this.assertions = new HashMap<String, SemanticAssertion>();
	}
	
	/* getters */
	/**
	 * get all the semantic assertions within the graph
	 * @return
	 */
	public Iterable<SemanticAssertion> get_assertions() { return assertions.values(); }
	/**
	 * get the semantic assertions in the graph with respect to the function specified
	 * @param function
	 * @return
	 * @throws Exception
	 */
	public Iterable<SemanticAssertion> get_assertions(Object function) throws Exception {
		if(function == null)
			throw new IllegalArgumentException("Invalid function: null");
		else {
			List<SemanticAssertion> match_assertions = new LinkedList<SemanticAssertion>();
			for(SemanticAssertion assertion : this.assertions.values()) {
				if(assertion.get_function() == function) 
					match_assertions.add(assertion);
			}
			return match_assertions;
		}
	}
	
	/* generator */
	/**
	 * extract the constant value
	 * @param constant
	 * @return boolean | long | double
	 * @throws Exception
	 */
	public static Object get_constant(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool: return constant.get_bool();
		case c_char:
		case c_uchar:	return Long.valueOf(constant.get_char());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return Long.valueOf(constant.get_integer());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return constant.get_long();
		case c_float:	return Double.valueOf(constant.get_float());
		case c_double:
		case c_ldouble:	return constant.get_double();
		default: throw new IllegalArgumentException("Invalid constant: null");
		}
	}
	/**
	 * get the constant that the expression represents in CIR code or null if it is not
	 * the constant expression
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected Object try_get_constant(CirExpression expression) throws Exception {
		if(expression instanceof CirConstExpression) {
			return get_constant(((CirConstExpression) expression).get_constant());
		}
		else { return null; }
	}
	private SemanticAssertion add_assertion(SemanticAssertion assertion) throws Exception {
		String key = assertion.toString();
		if(!this.assertions.containsKey(key)) {
			this.assertions.put(key, assertion);
		}
		return this.assertions.get(key);
	}
	
	/* constraints */
	/**
	 * cover(statement): covering a statement for any times
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion cover(CirStatement statement) throws Exception {
		SemanticAssertion constraint = new SemanticAssertion(this, ConstraintFunction.cover);
		constraint.add_operand(statement); 
		return this.add_assertion(constraint);
	}
	/**
	 * cover(statement, times): covering the statement for specified times
	 * @param statement
	 * @param times
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion cover_for(CirExpression condition, int times) throws Exception {
		SemanticAssertion constraint = new SemanticAssertion(this, ConstraintFunction.cover_for);
		constraint.add_operand(condition); constraint.add_operand(Integer.valueOf(times));
		return this.add_assertion(constraint);
	} 
	/**
	 * equal_with(expression, value) requires the expression equal with value specified
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion equal_with(CirExpression expression, Object value) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.equal_with);
		constraint.add_operand(expression); constraint.add_operand(value);
		return this.add_assertion(constraint);
	}
	/***
	 * not_equals(expression, value) requires the expression not equals with the value provided
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion not_equals(CirExpression expression, Object value) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.not_equals);
		constraint.add_operand(expression); constraint.add_operand(value);
		return this.add_assertion(constraint);
	}
	/**
	 * smaller_tn(expression, value) requires the expression is smaller than the value specified
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion smaller_tn(CirExpression expression, Object value) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.smaller_tn);
		constraint.add_operand(expression); constraint.add_operand(value);
		return this.add_assertion(constraint);
	}
	/**
	 * smaller_tn(value, expression) requires the expression is greater than the value specified
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion greater_tn(CirExpression expression, Object value) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.smaller_tn);
		constraint.add_operand(value); constraint.add_operand(expression);
		return this.add_assertion(constraint);
	}
	/**
	 * in_range(expr, string)
	 * @param expression
	 * @param range
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion in_range(CirExpression expression, String range) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.in_range);
		constraint.add_operand(expression); constraint.add_operand(range);
		return this.add_assertion(constraint);
	}
	/**
	 * not_in_range(expr, string)
	 * @param expression
	 * @param range
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion not_in_range(CirExpression expression, String range) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.not_in_range);
		constraint.add_operand(expression); constraint.add_operand(range);
		return this.add_assertion(constraint);
	}
	/**
	 * loperand & roperand != 0
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion bit_intersect(CirExpression loperand, Object roperand) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.bit_intersect);
		constraint.add_operand(loperand); constraint.add_operand(roperand);
		return this.add_assertion(constraint);
	}
	/**
	 * loperand & roperand == 0
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion bit_excluding(CirExpression loperand, Object roperand) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.bit_excluding);
		constraint.add_operand(loperand); constraint.add_operand(roperand);
		return this.add_assertion(constraint);
	}
	/**
	 * loperand & roperand == roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion bit_subsuming(CirExpression loperand, Object roperand) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.bit_subsuming);
		constraint.add_operand(loperand); constraint.add_operand(roperand);
		return this.add_assertion(constraint);
	}
	/**
	 * loperand & roperand != roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion not_subsuming(CirExpression loperand, Object roperand) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.not_subsuming);
		constraint.add_operand(loperand); constraint.add_operand(roperand);
		return this.add_assertion(constraint);
	}
	/**
	 * loperand & roperand != roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion is_negative(CirExpression loperand, CirExpression roperand) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.is_negative);
		constraint.add_operand(loperand); constraint.add_operand(roperand);
		return this.add_assertion(constraint);
	}
	/**
	 * loperand & roperand != roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion is_multiply(CirExpression loperand, CirExpression roperand) throws Exception {
		SemanticAssertion constraint;
		constraint = new SemanticAssertion(this, ConstraintFunction.is_multiply);
		constraint.add_operand(loperand); constraint.add_operand(roperand);
		return this.add_assertion(constraint);
	}
	
	/* state errors */
	/**
	 * active(statement): the statement is active to execute since it shall not be used
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion active(CirStatement statement) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.active);
		error.add_operand(statement); 
		return this.add_assertion(error);
	}
	/**
	 * disactive(statement): the statement is disactive not to execute since it shall
	 * be executed
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion disactive(CirStatement statement) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.disactive);
		error.add_operand(statement); return this.add_assertion(error);
	}
	/**
	 * trapping(): causes a trapping in the program.
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion trapping() throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.trapping);
		return this.add_assertion(error);
	}
	/**
	 * set_value(expr): set the value of the expression as different from original
	 * of which concrete value is not decidable
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion mut_value(CirExpression expression) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.mut_value);
		error.add_operand(expression); return this.add_assertion(error);
	}
	/**
	 * set_value(expr, value): set the value of the expression as specified value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion set_value(CirExpression expression, boolean value) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.set_bool);
		error.add_operand(expression); error.add_operand(value); return add_assertion(error);
	}
	/**
	 * set_value(expr, value): set the value of the expression as specified value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion set_value(CirExpression expression, long value) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.set_value);
		error.add_operand(expression); error.add_operand(value); 
		return this.add_assertion(error);
	}
	/**
	 * set_value(expr, value): set the value of the expression as specified value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion set_value(CirExpression expression, double value) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.set_value);
		error.add_operand(expression); error.add_operand(value); 
		return this.add_assertion(error);
	}
	/**
	 * not_value(expr): set the value of the boolean expression as logic_not
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion not_value(CirExpression expression) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.not_value);
		error.add_operand(expression); return this.add_assertion(error);
	}
	/**
	 * rsv_value(expr): set the value of the integer expression as bit-not
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion rsv_value(CirExpression expression) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.rsv_value);
		error.add_operand(expression); return this.add_assertion(error);
	}
	/**
	 * neg_value(expr): set the value of the integer or double expression as arith_neg
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion neg_value(CirExpression expression) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.neg_value);
		error.add_operand(expression); return this.add_assertion(error);
	}
	/**
	 * inc_value(expr, value): when value > 0
	 * dec_value(expr, -value): when value < 0
	 * 
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion diff_value(CirExpression expression, long value) throws Exception {
		if(value == 0)
			throw new IllegalArgumentException("Invalid value: 0");
		else {
			SemanticAssertion state_error = new SemanticAssertion(this, StateErrorFunction.dif_value);
			state_error.add_operand(expression); state_error.add_operand(Long.valueOf(value));
			return this.add_assertion(state_error);
		}
	}
	/**
	 * inc_value(expr, value): when value > 0
	 * dec_value(expr, -value): when value < 0
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion diff_value(CirExpression expression, double value) throws Exception {
		if(value == 0)
			throw new IllegalArgumentException("Invalid value: 0");
		else {
			SemanticAssertion state_error = new SemanticAssertion(this, StateErrorFunction.dif_value);
			state_error.add_operand(expression); state_error.add_operand(Double.valueOf(value));
			return this.add_assertion(state_error);
		}
	}
	/**
	 * inc_value(expr): value is increased with different value
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion inc_value(CirExpression expression) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.inc_value);
		error.add_operand(expression); return this.add_assertion(error);
	}
	/**
	 * dec_value(expr): value is increased with different value
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion dec_value(CirExpression expression) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.dec_value);
		error.add_operand(expression); return this.add_assertion(error);
	}
	/**
	 * mut_refer(lvalue in assignment)
	 * @param reference
	 * @return
	 * @throws Exception
	 */
	public SemanticAssertion mut_refer(CirReferExpression reference) throws Exception {
		SemanticAssertion error = new SemanticAssertion(this, StateErrorFunction.mut_refer);
		error.add_operand(reference); return this.add_assertion(error);
	}
	
	/* inference */
	/**
	 * generate the inference from assertions to another assertions
	 * @param prev_conditions
	 * @param post_conditions
	 * @return
	 * @throws Exception
	 */
	protected SemanticInference infer(
			Iterable<SemanticAssertion> prev_conditions, 
			Iterable<SemanticAssertion> post_conditions) throws Exception {
		SemanticInference inference = new SemanticInference();
		for(SemanticAssertion prev_condition : prev_conditions) 
			inference.add_prev_condition(prev_condition);
		for(SemanticAssertion post_condition : post_conditions) 
			inference.add_post_condition(post_condition);
		return inference;
	}
	
}
