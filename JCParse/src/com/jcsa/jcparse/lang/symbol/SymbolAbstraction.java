package com.jcsa.jcparse.lang.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	It represents the abstraction of a symbolic expression in lambda format:
 * 	<br>
 * 	<code>
 * 		lambda{(x1, x2, x3, ..., xk) Expression (y1, y2, y3, ..., yl)}
 * 	</code>
 * 	@author yukimula
 *
 */
public class SymbolAbstraction {
	
	/* definitions */
	/** the list of parameters used as inputs **/
	private	SymbolArgumentList 	in_parameters;
	/** the list of parameters to be assigned **/
	private	SymbolArgumentList	ou_parameters;
	/** the body of expression for evaluation **/
	private	SymbolExpression	expression_body;
	/**
	 * It generates an abstraction from the input expression
	 * @param body
	 * @throws Exception
	 */
	private SymbolAbstraction(SymbolExpression expression_body, 
			SymbolArgumentList in_parameters,
			SymbolArgumentList ou_parameters) throws IllegalArgumentException {
		if(expression_body == null) {
			throw new IllegalArgumentException("Invalid expression_body: null");
		}
		else if(in_parameters == null) {
			throw new IllegalArgumentException("Invalid in_parameters as null");
		}
		else if(ou_parameters == null) {
			throw new IllegalArgumentException("Invalid ou_parameters as null");
		}
		else {
			this.in_parameters = in_parameters;
			this.ou_parameters = ou_parameters;
			this.expression_body = expression_body;
		}
	}
	
	/* constructor */
	/**
	 * To collect the reference-expressions used in root
	 * @param root
	 * @param in_parameters	to preserve references used
	 * @param ou_parameters	to preserve references being defined
	 * @throws Exception
	 */
	private static void derive_inputs_outputs(SymbolNode root, 
			List<SymbolExpression> in_parameters,
			List<SymbolExpression> ou_parameters) {
		if(root != null) {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(root); 
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				for(SymbolNode child : parent.get_children()) {
					queue.add(child);
				}
				
				if(parent instanceof SymbolExpression && parent.is_refer_type()) {
					SymbolExpression expression = (SymbolExpression) parent;
					if(expression.get_parent() instanceof SymbolAssignExpression) {
						SymbolAssignExpression statement = (SymbolAssignExpression) expression.get_parent();
						if(statement.get_loperand() == expression) {
							if(!ou_parameters.contains(expression)) {
								ou_parameters.add(expression);
							}
						}
						else {
							if(!in_parameters.contains(expression)) {
								in_parameters.add(expression);
							}
						}
					}
					else if(expression.get_parent() instanceof SymbolCallExpression) { 
						/* function name is not input|output in the first-order language */ 
					}
					else {
						if(!in_parameters.contains(expression)) {
							in_parameters.add(expression);
						}
					}
				}
			}
		}
	}
	/**
	 * It constructs a naive abstraction of input expression
	 * @param expression
	 * @throws Exception
	 */
	protected static SymbolAbstraction naive_abstraction(SymbolExpression expression) throws IllegalArgumentException {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			List<SymbolExpression> in_parameters = new ArrayList<SymbolExpression>();
			List<SymbolExpression> ou_parameters = new ArrayList<SymbolExpression>();
			derive_inputs_outputs(expression, in_parameters, ou_parameters);
			return new SymbolAbstraction(expression, 
					SymbolArgumentList.create(in_parameters), 
					SymbolArgumentList.create(ou_parameters));
		}
	}
	
	/* getters */
	/**
	 * @return the list of parameters used as inputs 
	 */
	public SymbolExpression get_expression_body() { return this.expression_body; }
	/**
	 * @return the list of parameters to be assigned
	 */
	public SymbolArgumentList get_in_parameters() { return this.in_parameters; }
	/**
	 * @return the body of expression for evaluation
	 */
	public SymbolArgumentList get_ou_parameters() { return this.ou_parameters; }
	
	/* conversion */
	/**
	 * It replaces the operands in expression w.r.t. parameter-argument maps
	 * @param root
	 * @param pa_maps
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression replace(SymbolExpression root, 
			Map<SymbolExpression, SymbolExpression> pa_maps) throws Exception {
		if(root == null) {
			throw new IllegalArgumentException("Invalid root: null");
		}
		else if(pa_maps.containsKey(root)) { return pa_maps.get(root); }
		else if(root instanceof SymbolBasicExpression) { return root; }
		else if(root instanceof SymbolUnaryExpression) {
			SymbolExpression operand = ((SymbolUnaryExpression) root).get_operand();
			operand = this.replace(operand, pa_maps); 
			COperator operator = ((SymbolUnaryExpression) root).get_coperator();
			switch(operator) {
			case negative: 		return SymbolFactory.arith_neg(operand);
			case bit_not:		return SymbolFactory.bitws_rsv(operand);
			case logic_not:		return SymbolFactory.logic_not(operand);
			case address_of:	return SymbolFactory.address_of(operand);
			case dereference:	return SymbolFactory.dereference(operand);
			default:	throw new IllegalArgumentException("Invalid: " + operator);
			}
		}
		else if(root instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) root).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) root).get_roperand();
			loperand = this.replace(loperand, pa_maps); 
			roperand = this.replace(roperand, pa_maps); CType type = root.get_data_type();
			COperator operator = ((SymbolBinaryExpression) root).get_coperator();
			
			switch(operator) {
			case arith_add:		return SymbolFactory.arith_add(type, loperand, roperand);
			case arith_sub:		return SymbolFactory.arith_sub(type, loperand, roperand);
			case arith_mul:		return SymbolFactory.arith_mul(type, loperand, roperand);
			case arith_div:		return SymbolFactory.arith_div(type, loperand, roperand);
			case arith_mod:		return SymbolFactory.arith_mod(type, loperand, roperand);
			case bit_and:		return SymbolFactory.bitws_and(type, loperand, roperand);
			case bit_or:		return SymbolFactory.bitws_ior(type, loperand, roperand);
			case bit_xor:		return SymbolFactory.bitws_xor(type, loperand, roperand);
			case left_shift:	return SymbolFactory.bitws_lsh(type, loperand, roperand);
			case righ_shift:	return SymbolFactory.bitws_rsh(type, loperand, roperand);
			case logic_and:		return SymbolFactory.logic_and(loperand, roperand);
			case logic_or:		return SymbolFactory.logic_ior(loperand, roperand);
			case positive:		return SymbolFactory.logic_imp(loperand, roperand);
			case greater_tn:	return SymbolFactory.greater_tn(loperand, roperand);
			case greater_eq:	return SymbolFactory.greater_eq(loperand, roperand);
			case smaller_tn:	return SymbolFactory.smaller_tn(loperand, roperand);
			case smaller_eq:	return SymbolFactory.smaller_eq(loperand, roperand);
			case equal_with:	return SymbolFactory.equal_with(loperand, roperand);
			case not_equals:	return SymbolFactory.not_equals(loperand, roperand);
			case assign:		return SymbolFactory.exp_assign(loperand, roperand);
			case increment:		return SymbolFactory.imp_assign(loperand, roperand);
			default:			throw new IllegalArgumentException("Invalid: " + operator);
			}
		}
		else if(root instanceof SymbolCallExpression) {
			SymbolExpression function = ((SymbolCallExpression) root).get_function();
			SymbolArgumentList arguments = ((SymbolCallExpression) root).get_argument_list();
			List<Object> alist = new ArrayList<Object>();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				alist.add(this.replace(arguments.get_argument(k), pa_maps));
			}
			return SymbolFactory.call_expression(function, alist);
		}
		else if(root instanceof SymbolCastExpression) {
			SymbolExpression operand = ((SymbolCastExpression) root).get_operand();
			operand = this.replace(operand, pa_maps);
			return SymbolFactory.cast_expression(root.get_data_type(), operand);
		}
		else if(root instanceof SymbolFieldExpression) {
			SymbolExpression body = ((SymbolFieldExpression) root).get_body();
			body = this.replace(body, pa_maps);
			String field = ((SymbolFieldExpression) root).get_field().get_name();
			return SymbolFactory.field_expression(body, field);
		}
		else if(root instanceof SymbolIfElseExpression) {
			SymbolExpression condition = ((SymbolIfElseExpression) root).get_condition();
			SymbolExpression t_operand = ((SymbolIfElseExpression) root).get_t_operand();
			SymbolExpression f_operand = ((SymbolIfElseExpression) root).get_f_operand();
			condition = this.replace(condition, pa_maps);
			t_operand = this.replace(t_operand, pa_maps);
			f_operand = this.replace(f_operand, pa_maps);
			CType type = root.get_data_type();
			return SymbolFactory.ifte_expression(type, condition, t_operand, f_operand);
		}
		else if(root instanceof SymbolInitializerList) {
			List<Object> elements = new ArrayList<Object>();
			SymbolInitializerList list = (SymbolInitializerList) root;
			for(int k = 0; k < list.number_of_elements(); k++) {
				elements.add(this.replace(list.get_element(k), pa_maps));
			}
			return SymbolFactory.initializer_list(elements);
		}
		else if(root instanceof SymbolExpressionList) {
			List<Object> elements = new ArrayList<Object>();
			SymbolExpressionList list = (SymbolExpressionList) root;
			for(int k = 0; k < list.number_of_expressions(); k++) {
				elements.add(this.replace(list.get_expression(k), pa_maps));
			}
			return SymbolFactory.expression_list(elements);
		}
		else {
			throw new IllegalArgumentException(root.getClass().getSimpleName());
		}
	}
	/**
	 * @param arguments
	 * @return it applies the arguments on the lambda to create a new function
	 * @throws Exception
	 */
	public SymbolAbstraction replace(SymbolArgumentList arguments) throws Exception {
		if(arguments != null) {
			/* 1. create the mapping from parameter to argument */
			Map<SymbolExpression, SymbolExpression> pa_maps = 
					new HashMap<SymbolExpression, SymbolExpression>();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				if(k < this.in_parameters.number_of_arguments()) {
					SymbolExpression parameter = this.in_parameters.get_argument(k);
					SymbolExpression argument = arguments.get_argument(k);
					pa_maps.put(parameter, argument);
				}
			}
			
			/* 2. create the new lambda-abstraction */
			SymbolExpression expression = this.expression_body;
			expression = this.replace(expression, pa_maps);
			return SymbolAbstraction.naive_abstraction(expression);
		}
		else {
			return this;
		}
	}
	/**
	 * @return the simplified version of the lambda version (removing useless variable)
	 * @throws Exception
	 */
	public SymbolAbstraction simplify() throws Exception {
		return SymbolAbstraction.naive_abstraction(this.expression_body.evaluate(null, null));
	}
	
}
