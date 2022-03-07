package com.jcsa.jcparse.lang.symb.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.SymbolArgumentList;
import com.jcsa.jcparse.lang.symb.SymbolBasicExpression;
import com.jcsa.jcparse.lang.symb.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symb.SymbolCallExpression;
import com.jcsa.jcparse.lang.symb.SymbolCastExpression;
import com.jcsa.jcparse.lang.symb.SymbolConditionExpression;
import com.jcsa.jcparse.lang.symb.SymbolConstant;
import com.jcsa.jcparse.lang.symb.SymbolExpression;
import com.jcsa.jcparse.lang.symb.SymbolFactory;
import com.jcsa.jcparse.lang.symb.SymbolFieldExpression;
import com.jcsa.jcparse.lang.symb.SymbolInitializerList;
import com.jcsa.jcparse.lang.symb.SymbolUnaryExpression;

/**
 * It implements the evaluation on SymbolExpression.
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
		SymbolExpression t_operand = this.eval(expression.get_toperand());
		SymbolExpression f_operand = this.eval(expression.get_foperand());
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
		SymbolExpression operand = this.eval(expression.get_casted_operand());
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
		case positive:
		case negative:
		case bit_not:
		case logic_not:
		case address_of:
		case dereference:
		case increment:
		case decrement:
		case arith_add_assign:
		case arith_sub_assign:
		default:		throw new IllegalArgumentException(expression.generate_code(false));
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
		case arith_add:		return this.eval_arith_add(expression);
		case arith_sub:		return this.eval_arith_sub(expression);
		case arith_mul:		return this.eval_arith_mul(expression);
		case arith_div:		return this.eval_arith_div(expression);
		case arith_mod:		
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		case logic_and:
		case logic_or:
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		default:			throw new IllegalArgumentException(expression.generate_code(false));
		}
	}
	
	/* structural syntax construct */
	/**
	 * It evaluates the symbolic expressions in elist and update the list finally
	 * @param elist
	 * @throws Exception
	 */
	private	void				eval_expression_list(List<SymbolExpression> elist) throws Exception {
		List<SymbolExpression> results = new ArrayList<SymbolExpression>();
		for(SymbolExpression ei : elist) { results.add(this.eval(ei)); }
		elist.clear(); elist.addAll(results);
	}
	/**
	 * It accumulates the constants in elist and update elist into variables parts
	 * @param operator	[arith_add, arith_mul, bitws_and, bitws_ior, bitws_xor, logic_and, logic_ior]
	 * @param elist		the list of symbolic expressions being accumulated by constants
	 * @return	the constants being accumulated
	 * @throws Exception
	 */
	private	SymbolConstant		cacc_expression_list(COperator operator, List<SymbolExpression> elist) throws Exception {
		/* 1. initial constant used for accumulation */
		SymbolConstant constant;
		switch(operator) {
		case arith_add:	constant = SymbolFactory.sym_constant(Integer.valueOf(0));	break;
		case arith_mul:	constant = SymbolFactory.sym_constant(Integer.valueOf(1));	break;
		case bit_and:	constant = SymbolFactory.sym_constant(Integer.valueOf(~0));	break;
		case bit_or:	constant = SymbolFactory.sym_constant(Integer.valueOf(0));	break;
		case bit_xor:	constant = SymbolFactory.sym_constant(Integer.valueOf(0));	break;
		case logic_and:	constant = SymbolFactory.sym_constant(Boolean.TRUE);		break;
		case logic_or:	constant = SymbolFactory.sym_constant(Boolean.FALSE);		break;
		default:		throw new IllegalArgumentException("Invalid: " + operator.toString());
		}
		
		/* 2. accumulate the constants and reset variables */
		List<SymbolExpression> vlist = new ArrayList<SymbolExpression>();
		for(SymbolExpression ei : elist) {
			if(ei instanceof SymbolConstant) {
				constant = SymbolComputer.do_compute(operator, constant, (SymbolConstant) ei);
			}
			else {
				vlist.add(ei);
			}
		}
		
		/* 3. reset the variable-parts and return constant */
		elist.clear();	elist.addAll(vlist); return constant;
	}
	/**
	 * It implements the comparator of sorting symbolic expressions.
	 * 
	 * @author yukimula
	 *
	 */
	private static final class	SymbolComparator implements	Comparator<SymbolExpression> {
		@Override
		public int compare(SymbolExpression o1, SymbolExpression o2) {
			return o1.hashCode() - o2.hashCode();
		}
	}
	/**
	 * It accumulates the variable expressions in elist into a single expression
	 * @param type		data type of the output expression
	 * @param operator	[arith_add, arith_mul. bitws_and, bitws_ior, bitws_xor, logic_and, logic_ior]
	 * @param elist		the list of symbolic expressions to be accumulated into a single expression
	 * @return			the accumulated variables of expression or null if the elist is empty
	 * @throws Exception
	 */
	private	SymbolExpression	vacc_expression_list(CType type, COperator operator, List<SymbolExpression> elist) throws Exception {
		SymbolExpression expression = null;
		elist.sort(new SymbolComparator());
		for(SymbolExpression operand : elist) {
			if(expression == null) {
				expression = operand;
			}
			else {
				switch(operator) {
				case arith_add:	expression = SymbolFactory.arith_add(type, expression, operand);	break;
				case arith_mul:	expression = SymbolFactory.arith_mul(type, expression, operand);	break;
				case bit_and:	expression = SymbolFactory.bitws_and(type, expression, operand);	break;
				case bit_or:	expression = SymbolFactory.bitws_ior(type, expression, operand);	break;
				case bit_xor:	expression = SymbolFactory.bitws_xor(type, expression, operand);	break;
				case logic_and:	expression = SymbolFactory.logic_and(expression, operand);			break;
				case logic_or:	expression = SymbolFactory.logic_ior(expression, operand);			break;
				default:		throw new IllegalArgumentException("Unsupported: " + operator.toString());
				}
			}
		}
		return expression;
	}
	/**
	 * Whether there exist operands in alist and blist that equal with each other
	 * @param alist
	 * @param blist
	 * @return
	 * @throws Exception
	 */
	private	boolean				has_equivalent_value(List<SymbolExpression> alist, List<SymbolExpression> blist) throws Exception {
		for(SymbolExpression aexp : alist) {
			for(SymbolExpression bexp : blist) {
				if(SymbolComputer.is_equivalence(aexp, bexp)) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * It removes all the operands that equal with any others and remains only one	{a, a, b, b, c, b, a} --> {a, b, c}
	 * @param elist
	 * @throws Exception
	 */
	private	void				deqv_expression_list(List<SymbolExpression> elist) throws Exception {
		for(int i = 0; i < elist.size(); i++) {
			SymbolExpression aexp = elist.get(i);
			for(int j = elist.size() - 1; j > i; j--) {
				SymbolExpression bexp = elist.get(j);
				if(SymbolComputer.is_equivalence(aexp, bexp)) {
					elist.remove(j);
				}
			}
		}
	}
	/**
	 * It removes the equivalent operands in both lists
	 * @param alist
	 * @param blist
	 * @throws Exception
	 */
	private	void				deqv_expression_list(List<SymbolExpression> alist, List<SymbolExpression> blist) throws Exception {
		while(alist.size() > 0 && blist.size() > 0) {
			SymbolExpression lcom = null, rcom = null;
			for(SymbolExpression aexp : alist) {
				for(SymbolExpression bexp : blist) {
					if(SymbolComputer.is_equivalence(aexp, bexp)) {
						lcom = aexp;
						rcom = bexp;
						break;
					}
				}
				if(lcom != null && rcom != null) {
					break;
				}
			}
			
			if(lcom != null && rcom != null) {
				alist.remove(lcom); blist.remove(rcom);
			}
			else {
				break;
			}
		}
	}
	
	/* evaluation on arithmetic binary operation [+, -] */
	/**
	 * It divides the operands in arithmetic expression by [+, -]
	 * @param expression	[arith_pos, arith_neg, arith_add, arith_sub, const]
	 * @param plist			to preserve the operands in positive expression
	 * @param nlist			to preserve the operands in negative expression
	 * @throws Exception
	 */
	private	void				div_operands_in_arith_add_and_sub(SymbolExpression expression, 
			List<SymbolExpression> plist, List<SymbolExpression> nlist) throws Exception {
		if(expression == null) { return; }
		else if(expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) expression).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				if(value > 0) {
					plist.add(SymbolFactory.sym_constant(Long.valueOf(value)));
				}
				else if(value < 0) {
					nlist.add(SymbolFactory.sym_constant(Long.valueOf(-value)));
				}
				else { return; }
			}
			else {
				double value = ((Double) number).doubleValue();
				if(value > 0) {
					plist.add(SymbolFactory.sym_constant(Double.valueOf(value)));
				}
				else if(value < 0) {
					nlist.add(SymbolFactory.sym_constant(Double.valueOf(-value)));
				}
				else { return; }
			}
		}
		else if(expression instanceof SymbolUnaryExpression) {
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			COperator operator = ((SymbolUnaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.positive) {
				this.div_operands_in_arith_add_and_sub(uoperand, plist, nlist);
			}
			else if(operator == COperator.negative) {
				this.div_operands_in_arith_add_and_sub(uoperand, nlist, plist);
			}
			else { plist.add(expression); }
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.arith_add) {
				this.div_operands_in_arith_add_and_sub(loperand, plist, nlist);
				this.div_operands_in_arith_add_and_sub(roperand, plist, nlist);
			}
			else if(operator == COperator.arith_sub) {
				this.div_operands_in_arith_add_and_sub(loperand, plist, nlist);
				this.div_operands_in_arith_add_and_sub(roperand, nlist, plist);
			}
			else { plist.add(expression); }
		}
		else { plist.add(expression); }
	}
	/**
	 * It constructs the arithmetic add and subtract algorithms.
	 * @param type
	 * @param constant
	 * @param loperand
	 * @param roperand
	 * @return	C + (L - R)
	 * @throws Exception
	 */
	private	SymbolExpression	construct_for_arith_add_and_sub(CType type, 
			SymbolConstant constant,
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(loperand == null) {
			if(roperand == null) {
				if(SymbolComputer.compare_values(constant, Integer.valueOf(0))) {	/* ZERO */
					return SymbolFactory.sym_constant(Integer.valueOf(0));
				}
				else {																/* CONS */
					return SymbolFactory.sym_constant(constant.get_number());
				}
			}
			else {
				if(SymbolComputer.compare_values(constant, Integer.valueOf(0))) {	/* -R */
					return SymbolFactory.arith_neg(roperand);
				}
				else {																/* C - R */
					return SymbolFactory.arith_sub(type, constant, roperand);
				}
			}
		}
		else {
			if(roperand == null) {
				if(SymbolComputer.compare_values(constant, Integer.valueOf(0))) {	/* L */
					return loperand;
				}
				else {																/* C + L */
					return SymbolFactory.arith_add(type, constant, loperand);
				}
			}
			else {
				if(SymbolComputer.compare_values(constant, Integer.valueOf(0))) {	/* L - R */
					return SymbolFactory.arith_sub(type, loperand, roperand);
				}
				else {																/* C + (L - R) */
					roperand = SymbolFactory.arith_sub(type, loperand, roperand);
					return SymbolFactory.arith_sub(type, constant, roperand);
				}
			}
		}
	}
	/**
	 * @param expression	[arith_add, arith_sub]
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_add_or_sub(SymbolBinaryExpression expression) throws Exception {
		/* 1. declarations and initialization */
		List<SymbolExpression> plist = new ArrayList<SymbolExpression>();
		List<SymbolExpression> nlist = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant, constant;
		SymbolExpression loperand, roperand;
		CType type = SymbolFactory.get_type(expression.get_data_type());
		
		/* 2. it divides the expression into post and negt operands */
		this.div_operands_in_arith_add_and_sub(expression, plist, nlist);
		this.eval_expression_list(plist); this.eval_expression_list(nlist);
		
		/* 3. extract the constants part and update variables part */
		lconstant = this.cacc_expression_list(COperator.arith_add, plist);
		rconstant = this.cacc_expression_list(COperator.arith_add, nlist);
		constant = SymbolComputer.do_compute(COperator.arith_sub, lconstant, rconstant);
		
		/* 4. accumulate the variables part to loperand & roperand */
		this.deqv_expression_list(plist, nlist);	/** remove (X - X) == 0 **/
		loperand = this.vacc_expression_list(type, COperator.arith_add, plist);
		roperand = this.vacc_expression_list(type, COperator.arith_add, nlist);
		
		/* 5. generate results */
		return this.construct_for_arith_add_and_sub(type, constant, loperand, roperand);
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_add(SymbolBinaryExpression expression) throws Exception {
		return this.eval_arith_add_or_sub(expression);
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_sub(SymbolBinaryExpression expression) throws Exception {
		return this.eval_arith_add_or_sub(expression);
	}
	
	/* evaluation on arithmetic binary operation [*, /] */
	/**
	 * It divides the operands in arithmetic expression by [*, /]
	 * @param expression	[constant, arith_pos, arith_neg, arith_mul, arith_div]
	 * @param ulist			to preserve the operands in upper (divided) expression
	 * @param dlist			to preserve the operands in down (divisor) expression
	 * @throws Exception
	 */
	private	void				div_operands_in_arith_mul_and_div(SymbolExpression expression,
				List<SymbolExpression> ulist, List<SymbolExpression> dlist) throws Exception {
		if(expression == null) { return; }										/** Null-None **/
		else if(expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) expression).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				if(value == 1) { return; }										/** POST_ONES **/
				else { 															/** NOT_A_ONE **/
					ulist.add(SymbolFactory.sym_constant(Long.valueOf(value))); 
				}
			}
			else {
				double value = ((Double) number).doubleValue();
				if(value == 1) { return; }										/** POST_ONES **/
				else { 															/** NOT_A_ONE **/
					ulist.add(SymbolFactory.sym_constant(Double.valueOf(value))); 
				}
			}
		}
		else if(expression instanceof SymbolUnaryExpression) {					
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			COperator operator = ((SymbolUnaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.positive) {								/** ARITH_POS **/
				this.div_operands_in_arith_mul_and_div(uoperand, ulist, dlist);
			}
			else if(operator == COperator.negative) {							/** ARITH_NEG **/
				ulist.add(SymbolFactory.sym_constant(Long.valueOf(-1)));
				this.div_operands_in_arith_mul_and_div(uoperand, ulist, dlist);
			}
			else { ulist.add(expression); }										/** otherwise **/
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.arith_mul) {								/** ARITH_MUL **/
				this.div_operands_in_arith_mul_and_div(loperand, ulist, dlist);
				this.div_operands_in_arith_mul_and_div(roperand, ulist, dlist);
			}
			else if(operator == COperator.arith_div) {							/** ARITH_DIV **/
				this.div_operands_in_arith_mul_and_div(loperand, ulist, dlist);
				this.div_operands_in_arith_mul_and_div(roperand, dlist, ulist);
			}
			else { ulist.add(expression); }										/** otherwise **/
		}
		else { ulist.add(expression); }											/** otherwise **/
	}
	/**
	 * @param lconstant
	 * @param rconstant
	 * @return	[sign, lconstant, rconstant]
	 * @throws Exception
	 */
	private	SymbolConstant[]	eval_constants_in_arith_mul_and_div(CType type, 
			SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		if(SymbolFactory.is_real(type)) {
			Double lvalue = lconstant.get_double();
			Double rvalue = rconstant.get_double();
			Boolean sign = Boolean.valueOf(lvalue * rvalue < 0);
			lvalue = Math.abs(lvalue); rvalue = Math.abs(rvalue);
			return new SymbolConstant[] {
				SymbolFactory.sym_constant(sign),
				SymbolFactory.sym_constant(lvalue / rvalue),
				SymbolFactory.sym_constant(Integer.valueOf(1))
			};
		}
		else {
			Long lvalue = lconstant.get_long();
			Long rvalue = rconstant.get_long();
			Boolean sign = Boolean.valueOf(lvalue * rvalue < 0);
			
			lvalue = Math.abs(lvalue); 
			rvalue = Math.abs(rvalue);
			long a = Math.max(lvalue, rvalue);
			long b = Math.min(lvalue, rvalue);
			while(b != 0) {
				long t = b;
				b = a % b;
				a = t;
			}
			lvalue = lvalue % a; rvalue = rvalue % a;
			
			return new SymbolConstant[] {
					SymbolFactory.sym_constant(sign),
					SymbolFactory.sym_constant(lvalue),
					SymbolFactory.sym_constant(rvalue)
			};
		}
	}
	/**
	 * It constructs the expression into a unified form
	 * @param sign
	 * @param type
	 * @param lconstant
	 * @param rconstant
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	construct_for_arith_mul_and_div(boolean sign, CType type,
			SymbolConstant lconstant, SymbolConstant rconstant,
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		SymbolExpression expression;
		if(loperand == null && roperand == null) {
			expression = SymbolComputer.do_compute(COperator.arith_div, lconstant, rconstant);
		}
		else if(loperand == null && roperand != null) {
			loperand = lconstant;
			if(!SymbolComputer.compare_values(rconstant, Integer.valueOf(1))) {
				roperand = SymbolFactory.arith_mul(type, rconstant, roperand);
			}
			expression = SymbolFactory.arith_div(type, loperand, roperand);
		}
		else if(loperand != null && roperand == null) {
			if(!SymbolComputer.compare_values(lconstant, Integer.valueOf(1))) {
				loperand = SymbolFactory.arith_mul(type, lconstant, loperand);
			}
			
			if(SymbolComputer.compare_values(rconstant, Integer.valueOf(1))) {
				expression = loperand;
			}
			else {
				expression = SymbolFactory.arith_div(type, loperand, rconstant);
			}
		}
		else {
			if(!SymbolComputer.compare_values(lconstant, Integer.valueOf(1))) {
				loperand = SymbolFactory.arith_mul(type, lconstant, loperand);
			}
			if(!SymbolComputer.compare_values(rconstant, Integer.valueOf(1))) {
				roperand = SymbolFactory.arith_mul(type, rconstant, roperand);
			}
			expression = SymbolFactory.arith_div(type, loperand, roperand);
		}
		
		if(sign) {
			if(expression instanceof SymbolConstant) {
				return SymbolComputer.do_compute(COperator.negative, (SymbolConstant) expression);
			}
			return SymbolFactory.arith_neg(expression);
		}
		else {
			return expression;
		}
	}
	private	SymbolExpression	eval_arith_mul_or_div(SymbolBinaryExpression expression) throws Exception {
		/* 1. declarations and initialization */
		List<SymbolExpression> ulist = new ArrayList<SymbolExpression>();
		List<SymbolExpression> dlist = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant; boolean sign;
		SymbolExpression loperand, roperand;
		CType type = SymbolFactory.get_type(expression.get_data_type());
		
		/* 2. divide the operands within the [arith_mul, arith_div] */
		this.div_operands_in_arith_mul_and_div(expression, ulist, dlist);
		this.eval_expression_list(ulist); this.eval_expression_list(dlist);
		
		/* 3. accumulate the constants into lconstant, rconstant */
		lconstant = this.cacc_expression_list(COperator.arith_mul, ulist);
		rconstant = this.cacc_expression_list(COperator.arith_mul, dlist);
		if(SymbolComputer.compare_values(rconstant, Integer.valueOf(0))) {
			throw new ArithmeticException("Divided by zero.");
		}
		else if(SymbolComputer.compare_values(lconstant, Integer.valueOf(0))) {
			return SymbolFactory.sym_constant(Integer.valueOf(0));
		}
		else {
			SymbolConstant[] slr = this.eval_constants_in_arith_mul_and_div(type, lconstant, rconstant);
			sign = slr[0].get_bool().booleanValue(); lconstant = slr[1]; rconstant = slr[2];
		}
		
		/* 4. accumulate variale parts into loperand and roperand */
		this.deqv_expression_list(ulist, dlist);
		loperand = this.vacc_expression_list(type, COperator.arith_mul, ulist);
		roperand = this.vacc_expression_list(type, COperator.arith_mul, dlist);
		
		/* 5. construction */
		return this.construct_for_arith_mul_and_div(sign, type, lconstant, rconstant, loperand, roperand);
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_mul(SymbolBinaryExpression expression) throws Exception {
		return this.eval_arith_mul_or_div(expression);
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_div(SymbolBinaryExpression expression) throws Exception {
		return this.eval_arith_mul_or_div(expression);
	}
	
	
	
	
}
