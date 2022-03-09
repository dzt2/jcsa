package com.jcsa.jcparse.parse.symbolic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.jcsa.jcparse.lang.ctype.CType;

/**
 * It supports the evaluation of SymbolExpression.
 * 
 * @author yukimula
 *
 */
public class SymbolEvaluator {
	
	/* definition */
	/** the list of invokers to invoke call-expressions **/
	private	List<SymbolMethodInvoker> invokers;
	/** the map from reference to the new symbolic value after evaluation **/
	private	Map<SymbolExpression, SymbolExpression> state_map;
	/**
	 * private constructor for singleton mode
	 */
	private	SymbolEvaluator() {
		this.invokers = new ArrayList<SymbolMethodInvoker>();
		this.state_map = null;
	}
	
	/* configuration */
	/**
	 * It sets the reference with a new value
	 * @param reference
	 * @param value
	 * @throws Exception
	 */
	private void set_state(SymbolExpression reference, SymbolExpression value) throws Exception {
		if(reference == null) {
			throw new IllegalArgumentException("Invalid reference: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as null");
		}
		else if(!reference.is_reference()) {
			throw new IllegalArgumentException("Invalid reference: " + reference);
		}
		else if(this.state_map == null) { this.state_map.put(reference, value); }
		else { /* no state map is specified and thus no updating arises here */ }
	}
	/**
	 * It adds a new invoker into the evaluator memory-set
	 * @param invoker
	 * @throws Exception
	 */
	private void add_invoker(SymbolMethodInvoker invoker) throws Exception {
		if(invoker == null) {
			throw new IllegalArgumentException("Invalid invoker: null");
		}
		else if(!this.invokers.contains(invoker)) { this.invokers.add(invoker); }
	}
	/**
	 * It resets the state map for evaluating with side-effects
	 * @param state_map
	 */
	private void set_state_map(Map<SymbolExpression, SymbolExpression> state_map) {
		this.state_map = state_map;
	}
	
	/* singleton mode and methods */
	/** singleton **/ private static final SymbolEvaluator evaluator = new SymbolEvaluator();
	/**
	 * It adds a new invoker into the evaluator memory-set
	 * @param invoker
	 * @throws Exception
	 */
	public static void add_method_invoker(SymbolMethodInvoker invoker) throws Exception {
		evaluator.add_invoker(invoker);
	}
	
	/* syntax-directed evaluation algorithms */
	/**
	 * It evaluates the expression to a uniform simplification resulting
	 * @param expression	the expression to be evaluated to uniform style.
	 * @param state_map		to preserve the side-effect of evaluation 
	 * @return				the resulting expression being evaluated from input.
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression, 
			Map<SymbolExpression, SymbolExpression> state_map) throws Exception {
		evaluator.set_state_map(state_map);
		return evaluator.eval(expression);
	}
	/**
	 * It evaluates the expression to a uniform simplification resulting (without recording the side-effects)
	 * @param expression	the expression to be evaluated to uniform style.
	 * @return				the resulting expression being evaluated from input.
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression) throws Exception {
		evaluator.set_state_map(null);
		return evaluator.eval(expression);
	}
	/**
	 * It recursively evaluates the expression to a uniform simplification form.
	 * @param expression	the expression to be evaluated to uniform style.
	 * @return				the resulting expression being evaluated from input.
	 * @throws Exception
	 */
	private	SymbolExpression	eval(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolBasicExpression) {
			return this.eval_base_expr((SymbolBasicExpression) expression);
		}
		else if(expression instanceof SymbolBinaryExpression) {
			return this.eval_biny_expr((SymbolBinaryExpression) expression);
		}
		else if(expression instanceof SymbolUnaryExpression) {
			return this.eval_unay_expr((SymbolUnaryExpression) expression);
		}
		else if(expression instanceof SymbolCallExpression) {
			return this.eval_call_expr((SymbolCallExpression) expression);
		}
		else if(expression instanceof SymbolCastExpression) {
			return this.eval_cast_expr((SymbolCastExpression) expression);
		}
		else if(expression instanceof SymbolConditionExpression) {
			return this.eval_cond_expr((SymbolConditionExpression) expression);
		}
		else if(expression instanceof SymbolFieldExpression) {
			return this.eval_fiel_expr((SymbolFieldExpression) expression);
		}
		else if(expression instanceof SymbolInitializerList) {
			return this.eval_init_list((SymbolInitializerList) expression);
		}
		else {
			throw new IllegalArgumentException(expression.generate_code(true));
		}
	}
	
	/* basic expression evaluation */
	/**
	 * @param expression
	 * @return	base_expression --> base_expression
	 * @throws Exception
	 */
	private	SymbolExpression	eval_base_expr(SymbolBasicExpression expression) throws Exception { 
		return (SymbolExpression) expression.clone();
	}
	/**
	 * @param expression
	 * @return init_list	-->	{ eval(ei) }
	 * @throws Exception
	 */
	private	SymbolExpression	eval_init_list(SymbolInitializerList expression) throws Exception {
		List<Object> elements = new ArrayList<Object>();
		for(int k = 0; k < expression.number_of_elements(); k++) {
			elements.add(this.eval(expression.get_element(k)));
		}
		return SymbolFactory.initializer_list(elements);
	}
	/**
	 * @param expression
	 * @return	it evaluates the arguments and apply on method-invoker interface
	 * @throws Exception
	 */
	private	SymbolExpression	eval_call_expr(SymbolCallExpression expression) throws Exception {
		/* 1. evaluate the arguments */
		SymbolExpression function = this.eval(expression.get_function());
		List<Object> arguments = new ArrayList<Object>();
		SymbolArgumentList alist = expression.get_argument_list();
		for(int k = 0; k < alist.number_of_arguments(); k++) {
			arguments.add(this.eval(alist.get_argument(k)));
		}
		SymbolCallExpression result = SymbolFactory.call_expression(function, arguments);
		
		/* 2. it tries to invoke the function calls */
		for(SymbolMethodInvoker invoker : this.invokers) {
			SymbolExpression new_result = invoker.invoke(result);
			if(new_result != null) {
				return new_result;
			}
		}
		return result;
	}
	/**
	 * @param expression
	 * @return	[const-condition; equal-t-fvalue; otherwise]
	 * @throws Exception
	 */
	private	SymbolExpression	eval_cond_expr(SymbolConditionExpression expression) throws Exception {
		SymbolExpression condition = this.eval(expression.get_condition());
		SymbolExpression t_operand = this.eval(expression.get_t_operand());
		SymbolExpression f_operand = this.eval(expression.get_f_operand());
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				return t_operand;
			}
			else {
				return f_operand;
			}
		}
		else if(SymbolComputer.is_equivalence(t_operand, f_operand)) {
			return t_operand;
		}
		else {
			return SymbolFactory.cond_expr(expression.get_data_type(), condition, t_operand, f_operand);
		}
	}
	/**
	 * @param expression
	 * @return eval(body).field
	 * @throws Exception
	 */
	private	SymbolExpression	eval_fiel_expr(SymbolFieldExpression expression) throws Exception {
		SymbolExpression body = this.eval(expression.get_body());
		return SymbolFactory.field_expression(body, expression.get_field().get_name());
	}
	/**
	 * @param expression
	 * @return	(operand-const | otherwise)
	 * @throws Exception
	 */
	private	SymbolExpression	eval_cast_expr(SymbolCastExpression expression) throws Exception {
		CType cast_type = SymbolFactory.get_type(expression.get_cast_type().get_type());
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolConstant) {
			return SymbolComputer.do_compute(cast_type, (SymbolConstant) operand);
		}
		else {
			return SymbolFactory.cast_expression(cast_type, operand);
		}
	}
	/**
	 * @param expression	{+, -, ~, !, &, *, ++, --, p++, p--}
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_unay_expr(SymbolUnaryExpression expression) throws Exception {
		// TODO implement here more
		switch(expression.get_operator().get_operator()) {
		case negative:			
		case bit_not:			
		case logic_not:		
		case increment:			
		case decrement:			
		case address_of:		
		case dereference:		
		default:				throw new IllegalArgumentException(expression.generate_code(false));
		}
	}
	/**
	 * @param expression	{+, -, *, /, %, &, |, ^, <<, >>, &&, ||, <, <=, >, >=, ==, !=, :=}
	 * @return				
	 * @throws Exception
	 */
	private SymbolExpression	eval_biny_expr(SymbolBinaryExpression expression) throws Exception {
		// TODO implement here more
		switch(expression.get_operator().get_operator()) {
		case arith_add:		
		case arith_sub:		
		case arith_mul:		
		case arith_div:		
		case arith_mod:		
		case bit_and:		
		case bit_or:		
		case bit_xor:		
		case left_shift:	
		case righ_shift:	
		case logic_and:		
		case logic_or:		
		case positive:		
		case equal_with:	
		case not_equals:	
		case greater_tn:	
		case greater_eq:	
		case smaller_tn:	
		case smaller_eq:	
		default:			throw new IllegalArgumentException(expression.generate_code(false));
		}
	}
	
	// TODO implement unary and binary composite expressions as following...
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
