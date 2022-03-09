package com.jcsa.jcparse.parse.symbolic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It supports the evaluation of SymbolExpression.
 * 
 * @author yukimula
 *
 */
public class SymbolEvaluator {
	
	/* definition */
	/** the list of invokers to invoke call-expressions **/
	private	List<SymbolMethodInvoker> 				invokers;
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
	 * @param expression	{-, ~, !, &, *, ++, --}
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_unay_expr(SymbolUnaryExpression expression) throws Exception {
		// TODO implement here more
		switch(expression.get_operator().get_operator()) {
		case negative:			
		case bit_not:			
		case logic_not:				
		case address_of:		
		case dereference:		
		default:				throw new IllegalArgumentException(expression.generate_code(false));
		}
	}
	/**
	 * @param expression	{+, -, *, /, %, &, |, ^, imp(pos), <<, >>, &&, ||, <, <=, >, >=, ==, !=, :=, <-(inc)}
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
		case positive:		/* logical implication */
		case equal_with:	
		case not_equals:	
		case greater_tn:	
		case greater_eq:	
		case smaller_tn:	
		case smaller_eq:	
		case assign:		/* explicit assignment */
		case increment:		/* implicit assignment */
		default:			throw new IllegalArgumentException(expression.generate_code(false));
		}
	}
	
	/* structural analysis based on composite expression */
	/**
	 * It is used to support the sorting of symbolic expressions.
	 * @author yukimula
	 *
	 */
	private static final class 	SymbolComparator implements Comparator<SymbolExpression> {
		@Override
		public int compare(SymbolExpression o1, SymbolExpression o2) { return o1.hashCode() - o2.hashCode(); }
	}
	/**
	 * It evaluates each expression in elist and update the elist's elements
	 * @param elist	the symbolic expressions to be evaluated and update list
	 * @throws Exception
	 */
	private	void				eval_expression_list(List<SymbolExpression> elist) throws Exception {
		if(elist == null) {
			throw new IllegalArgumentException("Invalid elist: null");
		}
		else {
			List<SymbolExpression> olist = new ArrayList<SymbolExpression>();
			for(SymbolExpression ei : elist) { olist.add(this.eval(ei)); }
			elist.clear(); elist.addAll(olist);
		}
	}
	/**
	 * @param operator	[add, mul, and, ior, xor]
	 * @param elist		the expressions being evaluated and to accumulate constant
	 * @return			the constant to be accumulated from constants in the elist
	 * @throws Exception
	 */
	private	SymbolConstant		cacc_expression_list(COperator operator, List<SymbolExpression> elist) throws Exception {
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(elist == null) {
			throw new IllegalArgumentException("Invalid elist: null");
		}
		else {
			/* 1. derive the initialized constant value */
			SymbolConstant constant;
			switch(operator) {
			case arith_add:	constant = SymbolFactory.sym_constant(Long.valueOf(0));	break;
			case arith_mul:	constant = SymbolFactory.sym_constant(Long.valueOf(1));	break;
			case bit_and:	constant = SymbolFactory.sym_constant(Long.valueOf(~0));break;
			case bit_or:	constant = SymbolFactory.sym_constant(Long.valueOf(0));	break;
			case bit_xor:	constant = SymbolFactory.sym_constant(Long.valueOf(0));	break;
			case logic_and:	constant = SymbolFactory.sym_constant(Boolean.TRUE);	break;
			case logic_or:	constant = SymbolFactory.sym_constant(Boolean.FALSE);	break;
			default:		throw new IllegalArgumentException("Unsupported " + operator);
			}
			
			/* 2. traverse the expressions in elist to one constant */
			List<SymbolExpression> vlist = new ArrayList<SymbolExpression>();
			for(SymbolExpression ei : elist) {
				if(ei instanceof SymbolConstant) {
					constant = SymbolComputer.do_compute(operator, constant, (SymbolConstant) ei);
				}
				else {
					vlist.add(ei);
				}
			}
			
			/* 3. update the vlist to elist and return the constant */
			elist.clear(); elist.addAll(vlist); return constant;
		}
	}
	/**
	 * @param type		the data type of the output expression
	 * @param operator	[add, mul, and, ior, xor, and, ior]
	 * @param elist		the list of operands used to accumulate the expression
	 * @return			the single expression or null if the elist is empty
	 * @throws Exception
	 */
	private	SymbolExpression	vacc_expression_list(CType type, COperator operator, List<SymbolExpression> elist) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(elist == null) {
			throw new IllegalArgumentException("Invalid elist as null");
		}
		else {
			SymbolExpression expression = null;
			elist.sort(new SymbolComparator());
			for(SymbolExpression ei : elist) {
				if(expression == null) {
					expression = ei;
				}
				else {
					switch(operator) {
					case arith_add:	expression = SymbolFactory.arith_add(type, expression, ei);	break;
					case arith_mul:	expression = SymbolFactory.arith_mul(type, expression, ei);	break;
					case bit_and:	expression = SymbolFactory.bitws_and(type, expression, ei);	break;
					case bit_or:	expression = SymbolFactory.bitws_ior(type, expression, ei);	break;
					case bit_xor:	expression = SymbolFactory.bitws_xor(type, expression, ei);	break;
					case logic_and:	expression = SymbolFactory.logic_and(expression, ei);		break;
					case logic_or:	expression = SymbolFactory.logic_ior(expression, ei);		break;
					default:		throw new IllegalArgumentException("Unsupported: " + operator);
					}
				}
			}
			return expression;
		}
	}
	/**
	 * It reduces (cancels) the equivalent-pairs (peq) in both lists
	 * @param alist		the operands of which operand equal with other in blist will be removed
	 * @param blist
	 * @throws Exception
	 */
	private	void				dpeq_expression_lists(List<SymbolExpression> alist, List<SymbolExpression> blist) throws Exception {
		while(!alist.isEmpty() && !blist.isEmpty()) {
			SymbolExpression acom = null, bcom = null;
			for(SymbolExpression aexp : alist) {
				for(SymbolExpression bexp : blist) {
					if(SymbolComputer.is_equivalence(aexp, bexp)) {
						acom = aexp; bcom = bexp; break;
					}
				}
				if(acom != null || bcom != null) { break; }
			}
			if(acom != null || bcom != null) {
				alist.remove(acom); blist.remove(bcom);
			}
			else { break; }
		}
	}
	
	// TODO implement unary and binary composite expressions as following...
	/* symbolic evaluation on arithmetic operations {add, sub} */
	/**
	 * It divides the expression into positive part and negative part
	 * @param expression	[constant, airth_neg, arith_add, arith_sub]
	 * @param plist			to preserve the operands used in positives
	 * @param nlist			to preserve the operands used in negatives
	 * @throws Exception
	 */
	private	void				div_operands_in_arith_add_and_sub(SymbolExpression expression,
			List<SymbolExpression> plist, List<SymbolExpression> nlist) throws Exception {
		if(expression == null) { return; }										/** NULL-NONE **/
		else if(expression instanceof SymbolConstant) {							/** NUMB-CONS **/
			Object number = ((SymbolConstant) expression).get_number();
			plist.add(SymbolFactory.sym_constant(number));
		}
		else if(expression instanceof SymbolUnaryExpression) {
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			COperator operator = ((SymbolUnaryExpression) expression).get_coperator();
			if(operator == COperator.negative) {								/** ARITH_NEG **/
				this.div_operands_in_arith_add_and_sub(uoperand, nlist, plist);
			}
			else if(operator == COperator.bit_not) {							/** BITWS_RSV **/
				this.div_operands_in_arith_add_and_sub(uoperand, nlist, plist);
				plist.add(SymbolFactory.sym_constant(Integer.valueOf(-1)));
			}
			else { plist.add(expression); }										/** OTHERWISE **/
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_coperator();
			if(operator == COperator.arith_add) {								/** ARITH_ADD **/
				this.div_operands_in_arith_add_and_sub(loperand, plist, nlist);
				this.div_operands_in_arith_add_and_sub(roperand, plist, nlist);
			}
			else if(operator == COperator.arith_sub) {							/** ARITH_SUB **/
				this.div_operands_in_arith_add_and_sub(loperand, plist, nlist);
				this.div_operands_in_arith_add_and_sub(roperand, nlist, plist);
			}
			else { plist.add(expression); }										/** OTHERWISE **/
		}
		else {	plist.add(expression); 	}										/** OTHERWISE **/
	}
	/**
	 * It constructs the arithmetic add or sub expressions into one unified format
	 * @param type
	 * @param constant
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	construct_for_arith_add_and_sub(CType type, SymbolConstant constant,
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(loperand == null && roperand == null) {
			if(SymbolComputer.compare_values(constant, Integer.valueOf(0))) {
				return SymbolFactory.sym_constant(Long.valueOf(0));
			}
			else {
				return constant;
			}
		}
		else if(loperand == null && roperand != null) {
			if(SymbolComputer.compare_values(constant, Integer.valueOf(0))) {
				return SymbolFactory.arith_neg(roperand);
			}
			else {
				return SymbolFactory.arith_sub(type, constant, roperand);
			}
		}
		else if(loperand != null && roperand == null) {
			if(SymbolComputer.compare_values(constant, Integer.valueOf(0))) {
				return loperand;
			}
			else {
				return SymbolFactory.arith_add(type, constant, loperand);
			}
		}
		else {
			if(SymbolComputer.compare_values(constant, Integer.valueOf(0))) {
				return SymbolFactory.arith_sub(type, loperand, roperand);
			}
			else {
				roperand = SymbolFactory.arith_sub(type, loperand, roperand);
				return SymbolFactory.arith_add(type, constant, roperand);
			}
		}
	}
	/**
	 * @param expression	[neg, add, sub]
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_add_and_sub(SymbolExpression expression) throws Exception {
		/* 1. declarations and initializations */
		List<SymbolExpression> plist = new ArrayList<SymbolExpression>();
		List<SymbolExpression> nlist = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant, constant;
		SymbolExpression loperand, roperand;
		CType type = SymbolFactory.get_type(expression.get_data_type());
		
		/* 2. it divides expressions into plist and nlist */
		this.div_operands_in_arith_add_and_sub(expression, plist, nlist);
		this.eval_expression_list(plist); this.eval_expression_list(nlist);
		
		/* 3. accumulates the constants and subtract into one */
		lconstant = this.cacc_expression_list(COperator.arith_add, plist);
		rconstant = this.cacc_expression_list(COperator.arith_add, nlist);
		constant = SymbolComputer.do_compute(COperator.arith_sub, lconstant, rconstant);
		
		/* 4. accumulates the variables parts to operand */
		this.dpeq_expression_lists(plist, nlist);
		loperand = this.vacc_expression_list(type, COperator.arith_add, plist);
		roperand = this.vacc_expression_list(type, COperator.arith_add, nlist);
		
		/* 5. construct and return */
		return this.construct_for_arith_add_and_sub(type, constant, loperand, roperand);
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_add(SymbolBinaryExpression expression) throws Exception {
		return this.eval_arith_add_and_sub(expression);
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_sub(SymbolBinaryExpression expression) throws Exception {
		return this.eval_arith_add_and_sub(expression);
	}
	/* symbolic evaluation on arithmetic operations {mul, div} */
	/**
	 * It divides the expression into upper and down part for division
	 * @param expression	[neg, mul, div]
	 * @param ulist			to preserve the operands in the divided part
	 * @param dlist			to preserve the operands in the divisor part
	 * @throws Exception
	 */
	private	void				div_operands_in_arith_mul_and_div(SymbolExpression expression,
				List<SymbolExpression> ulist, List<SymbolExpression> dlist) throws Exception {
		if(expression == null)	{ return; }										/** NULL-NONE **/
		else if(expression instanceof SymbolConstant) {							/** NUMB-CONS **/
			Object number = ((SymbolConstant) expression).get_number();
			ulist.add(SymbolFactory.sym_constant(number));
		}
		else if(expression instanceof SymbolUnaryExpression) {
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			COperator operator = ((SymbolUnaryExpression) expression).get_coperator();
			if(operator == COperator.negative) {								/** ARITH_NEG **/
				ulist.add(SymbolFactory.sym_constant(Long.valueOf(-1)));
				this.div_operands_in_arith_mul_and_div(uoperand, ulist, dlist);
			}
			else { ulist.add(expression); }										/** OTHERWISE **/
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_coperator();
			if(operator == COperator.arith_mul) {								/** ARITH_MUL **/
				this.div_operands_in_arith_mul_and_div(loperand, ulist, dlist);
				this.div_operands_in_arith_mul_and_div(roperand, ulist, dlist);
			}
			else if(operator == COperator.arith_div) {							/** ARITH_DIV **/
				this.div_operands_in_arith_mul_and_div(loperand, ulist, dlist);
				this.div_operands_in_arith_mul_and_div(roperand, dlist, ulist);
			}
			else { ulist.add(expression); }										/** OTHERWISE **/
		}
		else { ulist.add(expression); }											/** OTHERWISE **/
	}
	/**
	 * @param lconstant
	 * @param rconstant
	 * @return	[sign, lconstant, rconstant]
	 * @throws Exception
	 */
	private	SymbolConstant[]	normalize_operands_in_arith_div(CType type, SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		if(SymbolFactory.is_real(type)) {
			double lvalue = lconstant.get_double().doubleValue();
			double rvalue = rconstant.get_double().doubleValue();
			Boolean sign = Boolean.valueOf(lvalue * rvalue < 0);
			lvalue = Math.abs(lvalue); rvalue = Math.abs(rvalue);
			return new SymbolConstant[] {
				SymbolFactory.sym_constant(sign),
				SymbolFactory.sym_constant(Double.valueOf(lvalue / rvalue)),
				SymbolFactory.sym_constant(Long.valueOf(1))
			};
		}
		else {
			long lvalue = lconstant.get_long().longValue();
			long rvalue = rconstant.get_long().longValue();
			Boolean sign = Boolean.valueOf(lvalue * rvalue < 0);
			lvalue = Math.abs(lvalue); rvalue = Math.abs(rvalue);
			
			long a = Math.max(lvalue, rvalue);
			long b = Math.min(lvalue, rvalue);
			while(b != 0) {
				long t = b; b = a % b; a = t;
			}
			lvalue = lvalue / a; rvalue = rvalue / a;
			
			return new SymbolConstant[] {
				SymbolFactory.sym_constant(sign),
				SymbolFactory.sym_constant(Long.valueOf(lvalue)),
				SymbolFactory.sym_constant(Long.valueOf(rvalue))
			};
		}
	}
	/**
	 * @param type		the data type of final expression
	 * @param sign		whether the constant part has negative
	 * @param lconstant	the left constant
	 * @param rconstant	the down constant
	 * @param loperand	the left operand
	 * @param roperand	the down operand
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	construct_for_arith_mul_and_div(CType type, boolean sign,
			SymbolConstant lconstant, SymbolConstant rconstant,
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(loperand == null && roperand == null) {
			SymbolConstant constant = SymbolComputer.do_compute(COperator.arith_div, lconstant, rconstant);
			if(sign) {
				return SymbolComputer.do_compute(COperator.negative, constant);
			}
			else {
				return constant;
			}
		}
		else if(loperand == null && roperand != null) {
			loperand = lconstant;
			if(!SymbolComputer.compare_values(rconstant, Integer.valueOf(1))) {
				roperand = SymbolFactory.arith_mul(type, rconstant, roperand);
			}
			
			SymbolExpression expression = SymbolFactory.arith_div(type, loperand, roperand);
			if(sign) {
				return SymbolFactory.arith_neg(expression);
			}
			else {
				return expression;
			}
		}
		else if(loperand != null && roperand == null) {
			if(!SymbolComputer.compare_values(lconstant, Integer.valueOf(1))) {
				loperand = SymbolFactory.arith_mul(type, lconstant, loperand);
			}
			
			SymbolExpression expression;
			if(SymbolComputer.compare_values(rconstant, Integer.valueOf(1))) {
				expression = loperand;
			}
			else {
				expression = SymbolFactory.arith_div(type, loperand, rconstant);
			}
			
			if(sign) {
				return SymbolFactory.arith_neg(expression);
			}
			else {
				return expression;
			}
		}
		else {
			if(!SymbolComputer.compare_values(lconstant, Integer.valueOf(1))) {
				loperand = SymbolFactory.arith_mul(type, lconstant, loperand);
			}
			if(!SymbolComputer.compare_values(rconstant, Integer.valueOf(1))) {
				roperand = SymbolFactory.arith_mul(type, rconstant, roperand);
			}
			
			SymbolExpression expression = SymbolFactory.arith_div(type, loperand, roperand);
			if(sign) {
				return SymbolFactory.arith_neg(expression);
			}
			else {
				return expression;
			}
		}
	}
	/**
	 * @param expression	[neg, mul, div]
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_mul_and_div(SymbolExpression expression) throws Exception {
		/* 1. declarations and initializations */
		List<SymbolExpression> ulist = new ArrayList<SymbolExpression>();
		List<SymbolExpression> dlist = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant; boolean sign;
		SymbolExpression loperand, roperand;
		CType type = SymbolFactory.get_type(expression.get_data_type());
		
		/* 2. divide the operands of mul or div */
		this.div_operands_in_arith_mul_and_div(expression, ulist, dlist);
		this.eval_expression_list(ulist); this.eval_expression_list(dlist);
		
		/* 3. accumulate the constants into one single */
		lconstant = this.cacc_expression_list(COperator.arith_mul, ulist);
		rconstant = this.cacc_expression_list(COperator.arith_mul, dlist);
		if(SymbolComputer.compare_values(rconstant, Integer.valueOf(0))) {
			throw new ArithmeticException("Divided by zero.");
		}
		else if(SymbolComputer.compare_values(lconstant, Integer.valueOf(0))) {
			return SymbolFactory.sym_constant(Long.valueOf(0));
		}
		else {
			SymbolConstant[] slr = this.normalize_operands_in_arith_div(type, lconstant, rconstant);
			sign = slr[0].get_bool().booleanValue(); lconstant = slr[1]; rconstant = slr[2];
		}
		
		/* 4. accumulate the variables part and remove equivalent-pairs */
		this.dpeq_expression_lists(ulist, dlist);
		loperand = this.vacc_expression_list(type, COperator.arith_mul, ulist);
		roperand = this.vacc_expression_list(type, COperator.arith_mul, dlist);
		
		/* 5. return */	
		return this.construct_for_arith_mul_and_div(type, sign, lconstant, rconstant, loperand, roperand);
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_mul(SymbolBinaryExpression expression) throws Exception {
		return this.eval_arith_mul_and_div(expression);
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_div(SymbolBinaryExpression expression) throws Exception {
		return this.eval_arith_mul_and_div(expression);
	}
	/* symbolic evaluation on arithmetic operations {neg} */
	
	
	
	
}
