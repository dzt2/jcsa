package com.jcsa.jcparse.lang.symb;

import java.util.Map;

/**
 * Used to evaluate the symbolic expression into concrete value or its simplified description.
 * 
 * @author yukimula
 *
 */
public abstract class SymEvaluator {
	
	/* attribute & constructor */
	/** mapping from identifier to its value **/
	protected Map<String, Object> context;
	/** construct an abstract evaluator **/
	protected SymEvaluator() { context = null; }
	
	/* implementation methods */
	/**
	 * get the solution from given context
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression find_in_context(SymExpression expression) throws Exception;
	/**
	 * symbolic address
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression address_expression(SymAddress expression) throws Exception;
	/**
	 * constant ==> bool | char | int | long | float | double
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression constant_expression(SymConstant expression) throws Exception;
	/**
	 * default_value
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression default_expression(SymDefaultValue expression) throws Exception;
	/**
	 * literal 
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression literal_expression(SymLiteral expression) throws Exception;
	/**
	 * field_expr ==> expression.field
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression field_expression(SymFieldExpression expression) throws Exception;
	/**
	 * invoc_expr ==> expression argument_list
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression invocate_expression(SymInvocateExpression expression) throws Exception;
	/**
	 * sequence_expr ==> { (expression)+ }
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression sequence_expression(SymSequenceExpression expression) throws Exception;
	/**
	 * x + y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression arith_add_expression(SymMultiExpression expression) throws Exception;
	/**
	 * x * y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression arith_mul_expression(SymMultiExpression expression) throws Exception;
	/**
	 * x & y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression bitws_and_expression(SymMultiExpression expression) throws Exception;
	/**
	 * x | y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression bitws_ior_expression(SymMultiExpression expression) throws Exception;
	/**
	 * x ^ y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression bitws_xor_expression(SymMultiExpression expression) throws Exception;
	/**
	 * x && y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression logic_and_expression(SymMultiExpression expression) throws Exception;
	/**
	 * x || y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression logic_ior_expression(SymMultiExpression expression) throws Exception;
	/**
	 * x - y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression arith_sub_expression(SymBinaryExpression expression) throws Exception;
	/**
	 * x / y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression arith_div_expression(SymBinaryExpression expression) throws Exception;
	/**
	 * x % y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression arith_mod_expression(SymBinaryExpression expression) throws Exception;
	/**
	 * x << y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression bitws_lsh_expression(SymBinaryExpression expression) throws Exception;
	/**
	 * x >> y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression bitws_rsh_expression(SymBinaryExpression expression) throws Exception;
	/**
	 * x > y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression greater_tn_expression(SymBinaryExpression expression) throws Exception;
	/**
	 * x >= y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression greater_eq_expression(SymBinaryExpression expression) throws Exception;
	/**
	 * x < y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression smaller_tn_expression(SymBinaryExpression expression) throws Exception;
	/**
	 * x <= y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression smaller_eq_expression(SymBinaryExpression expression) throws Exception;
	/**
	 * x == y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression equal_with_expression(SymBinaryExpression expression) throws Exception;
	/**
	 * x != y
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression not_equals_expression(SymBinaryExpression expression) throws Exception;
	/**
	 * +x
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression positive_expression(SymUnaryExpression expression) throws Exception;
	/**
	 * -x
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression negative_expression(SymUnaryExpression expression) throws Exception;
	/**
	 * ~x
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression bitws_rsv_expression(SymUnaryExpression expression) throws Exception;
	/**
	 * !x
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression logic_not_expression(SymUnaryExpression expression) throws Exception;
	/**
	 * &x
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression address_of_expression(SymUnaryExpression expression) throws Exception;
	/**
	 * *x
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression de_reference_expression(SymUnaryExpression expression) throws Exception;
	/**
	 * 
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected abstract SymExpression cast_expression(SymUnaryExpression expression) throws Exception;
	
	/* main evaluation */
	/***
	 * set the context to interpret the symbolic expression
	 * @param context
	 */
	public void set_context(Map<String, Object> context) { this.context = context; }
	/**
	 * evaluate the symbolic expression with given context
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public SymExpression evaluate(SymExpression expression) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(context != null && context.containsKey(expression.toString())) {
			return this.find_in_context(expression);
		}
		else if(expression instanceof SymAddress) {
			return this.address_expression((SymAddress) expression);
		}
		else if(expression instanceof SymConstant) {
			return this.constant_expression((SymConstant) expression);
		}
		else if(expression instanceof SymDefaultValue) {
			return this.default_expression((SymDefaultValue) expression);
		}
		else if(expression instanceof SymLiteral) {
			return this.literal_expression((SymLiteral) expression);
		}
		else if(expression instanceof SymFieldExpression) {
			return this.field_expression((SymFieldExpression) expression);
		}
		else if(expression instanceof SymInvocateExpression) {
			return this.invocate_expression((SymInvocateExpression) expression);
		}
		else if(expression instanceof SymSequenceExpression) {
			return this.sequence_expression((SymSequenceExpression) expression);
		}
		else if(expression instanceof SymUnaryExpression) {
			switch(((SymUnaryExpression) expression).get_operator()) {
			case positive:		return this.positive_expression((SymUnaryExpression) expression);
			case negative:		return this.negative_expression((SymUnaryExpression) expression);
			case bit_not:		return this.bitws_rsv_expression((SymUnaryExpression) expression);
			case logic_not:		return this.logic_not_expression((SymUnaryExpression) expression);
			case address_of:	return this.address_of_expression((SymUnaryExpression) expression);
			case dereference:	return this.de_reference_expression((SymUnaryExpression) expression);
			case assign:		return this.cast_expression((SymUnaryExpression) expression);
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
		else if(expression instanceof SymMultiExpression) {
			switch(((SymMultiExpression) expression).get_operator()) {
			case arith_add:	return this.arith_add_expression((SymMultiExpression) expression);
			case arith_mul:	return this.arith_mul_expression((SymMultiExpression) expression);
			case bit_and:	return this.bitws_and_expression((SymMultiExpression) expression);
			case bit_or:	return this.bitws_ior_expression((SymMultiExpression) expression);
			case bit_xor:	return this.bitws_xor_expression((SymMultiExpression) expression);
			case logic_and:	return this.logic_and_expression((SymMultiExpression) expression);
			case logic_or:	return this.logic_ior_expression((SymMultiExpression) expression);
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
		else if(expression instanceof SymBinaryExpression) {
			switch(((SymBinaryExpression) expression).get_operator()) {
			case arith_sub:		return this.arith_sub_expression((SymBinaryExpression) expression);
			case arith_div:		return this.arith_div_expression((SymBinaryExpression) expression);
			case arith_mod:		return this.arith_mod_expression((SymBinaryExpression) expression);
			case left_shift:	return this.bitws_lsh_expression((SymBinaryExpression) expression);
			case righ_shift:	return this.bitws_rsh_expression((SymBinaryExpression) expression);
			case greater_tn:	return this.greater_tn_expression((SymBinaryExpression) expression);
			case greater_eq:	return this.greater_eq_expression((SymBinaryExpression) expression);
			case smaller_tn:	return this.smaller_tn_expression((SymBinaryExpression) expression);
			case smaller_eq:	return this.smaller_eq_expression((SymBinaryExpression) expression);
			case not_equals:	return this.not_equals_expression((SymBinaryExpression) expression);
			case equal_with:	return this.equal_with_expression((SymBinaryExpression) expression);
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
		else {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
	}
	
}
