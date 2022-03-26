package com.jcsa.jcparse.parse.parser3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolArgumentList;
import com.jcsa.jcparse.lang.symbol.SymbolBasicExpression;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolCallExpression;
import com.jcsa.jcparse.lang.symbol.SymbolCastExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpressionList;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolFieldExpression;
import com.jcsa.jcparse.lang.symbol.SymbolIfElseExpression;
import com.jcsa.jcparse.lang.symbol.SymbolInitializerList;
import com.jcsa.jcparse.lang.symbol.SymbolUnaryExpression;


/**
 * It implements the evaluation of SymbolExpression using state-driven approach.
 * 
 * @author yukimula
 *
 */
public class SymbolEvaluator {
	
	/* definition */
	/** the list of invokers to invoke call-expressions **/
	private	List<SymbolMethodInvoker> 				invokers;
	/** to preserve the input-output state values in current state **/
	private	SymbolContext							in_state;
	/** it preserves the states as output of side-effect operation **/
	private	SymbolContext							ou_state;
	/**
	 * private constructor for singleton mode
	 */
	private	SymbolEvaluator() {
		this.invokers = new ArrayList<SymbolMethodInvoker>();
		this.in_state = null; this.ou_state = null;
		this.invokers.add(new SymbolDefaultInvoker());
	}
	
	/* configuration */
	/**
	 * @param reference
	 * @return it derives the value of the reference from the state-context
	 * @throws Exception
	 */
	private	SymbolExpression get_state_value(SymbolExpression reference) throws Exception {
		if(reference == null || this.in_state == null) {
			return null;
		}
		else {
			return this.in_state.get_value(reference);
		}
	}
	/**
	 * It saves the reference-value to output state 
	 * @param reference
	 * @param value
	 * @return false if the reference-value fails to be saved
	 * @throws Exception
	 */
	private void set_state_value(SymbolExpression reference, SymbolExpression value) throws Exception {
		if(reference == null) {
			throw new IllegalArgumentException("Invalid reference: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as null");
		}
		else if(!reference.is_reference() && !(reference instanceof SymbolCallExpression)) {
			throw new IllegalArgumentException("Invalid reference: " + reference);
		}
		else if(this.ou_state != null) {this.ou_state.put_value(reference, value);}
		else { /* no state map is specified and thus no update arises here */ }
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
	 * It sets the state-contexts as input and output
	 * @param in_state
	 * @param ou_state
	 */
	private	void set_io_states(SymbolContext in_state, SymbolContext ou_state) {
		this.in_state = in_state; this.ou_state = ou_state;
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
	
	/* recursively evaluation methods */
	/**
	 * @param expression	the symbolic expression to be evaluated
	 * @param in_state		the state-context to provide the inputs
	 * @param ou_state		the state-context to preserve an output
	 * @return				the resulting expression from the input
	 * @throws Exception
	 */
	private	SymbolExpression	eval_on(SymbolExpression expression, 
			SymbolContext in_state, SymbolContext ou_state) throws Exception {
		this.set_io_states(in_state, ou_state);
		return this.eval(expression);
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
		else if(this.get_state_value(expression) != null) {
			return this.get_state_value(expression);
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
		else if(expression instanceof SymbolIfElseExpression) {
			return this.eval_cond_expr((SymbolIfElseExpression) expression);
		}
		else if(expression instanceof SymbolFieldExpression) {
			return this.eval_fiel_expr((SymbolFieldExpression) expression);
		}
		else if(expression instanceof SymbolInitializerList) {
			return this.eval_init_list((SymbolInitializerList) expression);
		}
		else if(expression instanceof SymbolExpressionList) {
			return this.eval_expr_list((SymbolExpressionList) expression);
		}
		else {
			throw new IllegalArgumentException(expression.get_symbol_class().toString());
		}
	}
	/**
	 * @param expression	the symbolic expression to be evaluated
	 * @param in_state		the state-context to provide the inputs
	 * @param ou_state		the state-context to preserve an output
	 * @return				the resulting expression from the input
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression,
			SymbolContext in_state, SymbolContext ou_state) throws Exception {
		return evaluator.eval_on(expression, in_state, ou_state);
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
		SymbolExpression output = result;
		for(SymbolMethodInvoker invoker : this.invokers) {
			SymbolExpression new_result = invoker.invoke(result, this.in_state, this.ou_state);
			if(new_result != null) {
				output = new_result; break;
			}
		}
		
		/* 3. it saves the function-calling state in output-state */
		this.set_state_value(expression, output); return output;
	}
	/**
	 * @param expression
	 * @return	[const-condition; equal-t-fvalue; otherwise]
	 * @throws Exception
	 */
	private	SymbolExpression	eval_cond_expr(SymbolIfElseExpression expression) throws Exception {
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
			return SymbolFactory.ifte_expression(expression.get_data_type(), condition, t_operand, f_operand);
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
		switch(expression.get_operator().get_operator()) {
		case negative:			return this.eval_arith_neg(expression);
		case bit_not:			return this.eval_bitws_rsv(expression);
		case logic_not:			return this.eval_logic_not(expression);
		case address_of:		return this.eval_addr_of(expression);
		case dereference:		return this.eval_de_refer(expression);
		default:				throw new IllegalArgumentException(expression.get_simple_code());
		}
	}
	/**
	 * @param expression	{+, -, *, /, %, &, |, ^, imp(pos), <<, >>, &&, ||, <, <=, >, >=, ==, !=, :=, <-(inc)}
	 * @return				
	 * @throws Exception
	 */
	private SymbolExpression	eval_biny_expr(SymbolBinaryExpression expression) throws Exception {
		switch(expression.get_operator().get_operator()) {
		case arith_add:		return this.eval_arith_add(expression);
		case arith_sub:		return this.eval_arith_sub(expression);
		case arith_mul:		return this.eval_arith_mul(expression);
		case arith_div:		return this.eval_arith_div(expression);
		case arith_mod:		return this.eval_arith_mod(expression);
		case bit_and:		return this.eval_bitws_and(expression);
		case bit_or:		return this.eval_bitws_ior(expression);
		case bit_xor:		return this.eval_bitws_xor(expression);
		case left_shift:	return this.eval_bitws_lsh(expression);
		case righ_shift:	return this.eval_bitws_rsh(expression);
		case logic_and:		return this.eval_logic_and(expression);
		case logic_or:		return this.eval_logic_ior(expression);
		case positive:		/* logical implication */
							return this.eval_logic_imp(expression);
		case equal_with:	return this.eval_equal_with(expression);
		case not_equals:	return this.eval_not_equals(expression);
		case greater_tn:	return this.eval_greater_tn(expression);
		case greater_eq:	return this.eval_greater_eq(expression);
		case smaller_tn:	return this.eval_smaller_tn(expression);
		case smaller_eq:	return this.eval_smaller_eq(expression);
		case assign:		/* explicit assignment */
							return this.eval_ex_assign(expression);
		case increment:		/* implicit assignment */
							return this.eval_im_assign(expression);
		default:			throw new IllegalArgumentException(expression.get_simple_code());
		}
	}
	/**
	 * It extends the elements in expression list to flat form
	 * @param expression
	 * @param elist
	 * @throws Exception
	 */
	private	void	extend_expr_list(SymbolExpression expression, List<SymbolExpression> elist) throws Exception {
		if(expression instanceof SymbolExpressionList) {
			SymbolExpressionList list = (SymbolExpressionList) expression;
			for(int k = 0; k < list.number_of_expressions(); k++) {
				this.extend_expr_list(list.get_expression(k), elist);
			}
		}
		else {
			elist.add(expression);
		}
	}
	/**
	 * It evaluates the expression-list by extending its elements in flat-forms.
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_expr_list(SymbolExpressionList expression) throws Exception {
		/* 1. evaluate sub-expressions in evaluation list */
		List<SymbolExpression> alist = new ArrayList<SymbolExpression>();
		this.extend_expr_list(expression, alist);
		List<Object> blist = new ArrayList<Object>();
		for(SymbolExpression element : alist) {
			blist.add(this.eval(element));
		}
		
		/* 2. construct the expression list for recording */
		SymbolExpressionList rvalue = SymbolFactory.expression_list(blist);
		SymbolExpression lvalue = SymbolFactory.
						identifier(rvalue.get_data_type(), "@eval", rvalue);
		this.set_state_value(lvalue, rvalue);	return rvalue;
		/* 3. return the final expression as evaluating result */
		/*
		if(rvalue.number_of_expressions() > 0) {
			return rvalue.get_expression(rvalue.number_of_expressions() - 1);
		}
		else {
			return rvalue;
		}
		*/
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
	 * @param operator	[add, mul, and, ior, xor, eqv]
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
			case equal_with:constant = SymbolFactory.sym_constant(Long.valueOf(0));	break;
			case logic_and:	constant = SymbolFactory.sym_constant(Boolean.TRUE);	break;
			case logic_or:	constant = SymbolFactory.sym_constant(Boolean.FALSE);	break;
			default:		throw new IllegalArgumentException("Unsupported " + operator);
			}
			
			/* 2. traverse the expressions in elist to one constant */
			List<SymbolExpression> vlist = new ArrayList<SymbolExpression>();
			for(SymbolExpression ei : elist) {
				if(ei instanceof SymbolConstant) {
					if(operator == COperator.equal_with) {
						SymbolConstant value = (SymbolConstant) ei;
						value = SymbolComputer.do_compute(COperator.bit_not, value);
						constant = SymbolComputer.do_compute(COperator.bit_xor, constant, value);
					}
					else {
						constant = SymbolComputer.do_compute(operator, constant, (SymbolConstant) ei);
					}
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
	 * Whether there are equivalent operands in two list
	 * @param alist
	 * @param blist
	 * @return
	 * @throws Exception
	 */
	private	boolean				heqv_expression_list(List<SymbolExpression> alist, List<SymbolExpression> blist) throws Exception {
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
			else if(SymbolComputer.is_negative(constant)) {
				roperand = SymbolFactory.arith_add(type, constant, roperand);
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
			else if(SymbolComputer.is_negative(constant)) {
				constant = SymbolComputer.do_compute(COperator.negative, constant);
				return SymbolFactory.arith_sub(type, loperand, constant);
			}
			else {
				return SymbolFactory.arith_add(type, constant, loperand);
			}
		}
		else {
			if(SymbolComputer.compare_values(constant, Integer.valueOf(0))) {
				return SymbolFactory.arith_sub(type, loperand, roperand);
			}
			else if(SymbolComputer.is_negative(constant)) {
				constant = SymbolComputer.do_compute(COperator.negative, constant);
				loperand = SymbolFactory.arith_sub(type, loperand, roperand);
				return SymbolFactory.arith_sub(type, loperand, constant);
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
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_neg(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolConstant) {
			return SymbolComputer.do_compute(COperator.negative, (SymbolConstant) operand);
		}
		else if(operand instanceof SymbolUnaryExpression) {
			SymbolExpression uoperand = ((SymbolUnaryExpression) operand).get_operand();
			COperator operator = ((SymbolUnaryExpression) operand).get_coperator();
			if(operator == COperator.negative) {
				return uoperand;
			}
			else if(operator == COperator.bit_not) {
				return SymbolFactory.arith_add(expression.get_data_type(), uoperand, Integer.valueOf(1));
			}
			else { return SymbolFactory.arith_neg(operand); }
		}
		else if(operand instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) operand).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) operand).get_roperand();
			COperator operator = ((SymbolBinaryExpression) operand).get_coperator();
			if(operator == COperator.arith_sub) {
				return SymbolFactory.arith_sub(expression.get_data_type(), roperand, loperand);
			}
			else { return SymbolFactory.arith_neg(operand); }
		}
		else { return SymbolFactory.arith_neg(operand); }
	}
	/* symbolic evaluation on arithmetic operations (mod) */
	/**
	 * It divides the operands in expression as multiply and serves to arith_mod
	 * @param expression
	 * @param ulist
	 * @throws Exception
	 */
	private	void				div_operands_in_arith_mod_by_mul(SymbolExpression 
					expression, List<SymbolExpression> ulist) throws Exception {
		if(expression == null)	{ return; }										/** NULL-NONE **/
		else if(expression instanceof SymbolConstant) {							/** LONG-CONS **/
			Long value = ((SymbolConstant) expression).get_long();
			ulist.add(SymbolFactory.sym_constant(value));
		}
		else if(expression instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) expression).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			if(operator == COperator.negative) {								/** ARITH_NEG **/
				ulist.add(SymbolFactory.sym_constant(Long.valueOf(-1)));
				this.div_operands_in_arith_mod_by_mul(uoperand, ulist);
			}
			else { ulist.add(expression); }										/** OTHERWISE **/
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_coperator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(operator == COperator.arith_mul) {								/** ARITH_MUL **/
				this.div_operands_in_arith_mod_by_mul(loperand, ulist);
				this.div_operands_in_arith_mod_by_mul(roperand, ulist);
			}
			else { ulist.add(expression); }										/** OTHERWISE **/
		}
		else 	{ ulist.add(expression); }										/** OTHERWISE **/
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_arith_mod(SymbolBinaryExpression expression) throws Exception {
		/* 1. declarations and initialization */
		List<SymbolExpression> ulist = new ArrayList<SymbolExpression>();
		List<SymbolExpression> dlist = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant;
		SymbolExpression loperand, roperand;
		CType type = SymbolFactory.get_type(expression.get_data_type());
		
		/* 2. divide the loperands and roperands */
		this.div_operands_in_arith_mod_by_mul(expression.get_loperand(), ulist);
		this.div_operands_in_arith_mod_by_mul(expression.get_roperand(), dlist);
		this.eval_expression_list(ulist); this.eval_expression_list(dlist);
		
		/* 3. accumulate the constant results */
		lconstant = this.cacc_expression_list(COperator.arith_mul, ulist);
		rconstant = this.cacc_expression_list(COperator.arith_mul, dlist);
		if(SymbolComputer.compare_values(rconstant, Long.valueOf(0))) {
			throw new ArithmeticException("Divided by zero.");
		}
		else if(this.heqv_expression_list(ulist, dlist)) { 
			return SymbolFactory.sym_constant(Long.valueOf(0));
		}
		else if(!SymbolComputer.compare_values(rconstant, Long.valueOf(1))
				&& !SymbolComputer.compare_values(rconstant, Long.valueOf(-1))
				&& lconstant.get_long() % rconstant.get_long() == 0) {
			return SymbolFactory.sym_constant(Long.valueOf(0));
		}
		else {
			SymbolConstant[] slr = this.normalize_operands_in_arith_div(
						CBasicTypeImpl.long_type, lconstant, rconstant);
			boolean sign = slr[0].get_bool().booleanValue();
			long lvalue = slr[1].get_long().longValue();
			long rvalue = slr[2].get_long().longValue();
			if(sign) lvalue = -lvalue;
			lconstant = SymbolFactory.sym_constant(Long.valueOf(lvalue));
			rconstant = SymbolFactory.sym_constant(Long.valueOf(rvalue));
		}
		
		/* 4. accumulate to operands */
		loperand = this.vacc_expression_list(type, COperator.arith_mul, ulist);
		roperand = this.vacc_expression_list(type, COperator.arith_mul, dlist);
		if(loperand == null) { loperand = lconstant; }
		else if(lconstant.get_long() == -1) {
			loperand = SymbolFactory.arith_neg(loperand);
		}
		else if(lconstant.get_long() != 1) {
			loperand = SymbolFactory.arith_mul(type, lconstant, loperand);
		}
		else { /* no more modify for lconstant == 1 */ }
		if(roperand == null) { roperand = rconstant; }
		else if(rconstant.get_long() == -1) {
			roperand = SymbolFactory.arith_neg(roperand);
		}
		else if(rconstant.get_long() != 1) {
			roperand = SymbolFactory.arith_mul(type, rconstant, roperand);
		}
		else { /* no more modify for rconstant == 1 */ }
		
		/* 6. partial evaluation on mod */
		if(loperand instanceof SymbolConstant) {
			long lvalue = ((SymbolConstant) loperand).get_long();
			if(roperand instanceof SymbolConstant) {
				long rvalue = ((SymbolConstant) roperand).get_long();
				return SymbolFactory.sym_constant(Long.valueOf(lvalue % rvalue));
			}
			else {
				if(lvalue == 0) {
					return SymbolFactory.sym_constant(Long.valueOf(0));
				}
				else {
					return SymbolFactory.arith_mod(type, loperand, roperand);
				}
			}
		}
		else {
			if(roperand instanceof SymbolConstant) {
				long rvalue = ((SymbolConstant) roperand).get_long();
				if(rvalue == 1 || rvalue == -1) {
					return SymbolFactory.sym_constant(Long.valueOf(0));
				}
				else {
					return SymbolFactory.arith_mod(type, loperand, roperand);
				}
			}
			else {
				return SymbolFactory.arith_mod(type, loperand, roperand);
			}
		}
	}
	
	/* symbolic evaluation on bitwise operations {&, |, ^} */
	/**
	 * @param expression	the expression or suboperand in bitws_and
	 * @param plist			to preserve the operands in not-rsv parts
	 * @param nlist			to preserve the operands in bit-rsv parts
	 * @throws Exception
	 */
	private	void				div_operands_in_bitws_and(SymbolExpression expression,
			List<SymbolExpression> plist, List<SymbolExpression> nlist) throws Exception {
		if(expression == null) { return; }										/** NULL-NONE **/
		else if(expression instanceof SymbolConstant) {							/** LONG-CONS **/
			Long value = ((SymbolConstant) expression).get_long();
			plist.add(SymbolFactory.sym_constant(value));
		}
		else if(expression instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) expression).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			if(operator == COperator.bit_not) {									/** BITWS_RSV **/
				this.div_operands_in_bitws_ior(uoperand, nlist, plist);
			}
			else { plist.add(expression); }										/** OTHERWISE **/
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_coperator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(operator == COperator.bit_and) {									/** BITWS_AND **/
				this.div_operands_in_bitws_and(loperand, plist, nlist);
				this.div_operands_in_bitws_and(roperand, plist, nlist);
			}
			else { plist.add(expression); }										/** OTHERWISE **/
		}
		else 	{ plist.add(expression); }										/** OTHERWISE **/
	}
	/**
	 * @param expression	the expression or suboperand in bitws_ior
	 * @param plist			to preserve the operands in not-rsv parts
	 * @param nlist			to preserve the operands in bit-rsv parts
	 * @throws Exception
	 */
	private	void				div_operands_in_bitws_ior(SymbolExpression expression,
			List<SymbolExpression> plist, List<SymbolExpression> nlist) throws Exception {
		if(expression == null) { return; }										/** NULL-NONE **/
		else if(expression instanceof SymbolConstant) {							/** LONG-CONS **/
			Long value = ((SymbolConstant) expression).get_long();
			plist.add(SymbolFactory.sym_constant(value));
		}
		else if(expression instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) expression).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			if(operator == COperator.bit_not) {									/** BITWS_RSV **/
				this.div_operands_in_bitws_and(uoperand, nlist, plist);
			}
			else { plist.add(expression); }										/** OTHERWISE **/
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_coperator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(operator == COperator.bit_or) {									/** BITWS_IOR **/
				this.div_operands_in_bitws_ior(loperand, plist, nlist);
				this.div_operands_in_bitws_ior(roperand, plist, nlist);
			}
			else { plist.add(expression); }										/** OTHERWISE **/
		}
		else 	{ plist.add(expression); }										/** OTHERWISE **/
	}
	/**
	 * @param expression	the expression or suboperand in bitws_xor
	 * @param plist			to preserve the operands in not-rsv parts
	 * @param nlist			to preserve the operands in bit-rsv parts
	 * @throws Exception
	 */
	private	void				div_operands_in_bitws_xor(SymbolExpression expression,
			List<SymbolExpression> plist, List<SymbolExpression> nlist) throws Exception {
		if(expression == null) { return; }										/** NULL-NONE **/
		else if(expression instanceof SymbolConstant) {							/** LONG-CONS **/
			Long value = ((SymbolConstant) expression).get_long();
			plist.add(SymbolFactory.sym_constant(value));
		}
		else if(expression instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) expression).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			if(operator == COperator.bit_not) {	nlist.add(uoperand); }			/** BITWS_RSV **/
			else { plist.add(expression); }										/** OTHERWISE **/
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_coperator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(operator == COperator.bit_xor) {									/** BITWS_XOR **/
				this.div_operands_in_bitws_xor(loperand, plist, nlist);
				this.div_operands_in_bitws_xor(roperand, plist, nlist);
			}
			else { plist.add(expression); }										/** OTHERWISE **/
		}
		else 	{ plist.add(expression); }										/** OTHERWISE **/
	}
	/**
	 * the simplified form of variables (no-duplicated)
	 * @param type
	 * @param elist
	 * @throws Exception
	 */
	private	SymbolExpression	sim_operands_in_bitws_and(CType type, List<SymbolExpression> elist) throws Exception {
		Set<SymbolExpression> operands = new HashSet<SymbolExpression>();
		for(SymbolExpression operand : elist) { operands.add(operand); }
		elist.clear();
		for(SymbolExpression operand : operands) { elist.add(operand); }
		return this.vacc_expression_list(type, COperator.bit_and, elist);
	}
	/**
	 * the simplified form of variables (no-duplicated)
	 * @param type
	 * @param elist
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	sim_operands_in_bitws_ior(CType type, List<SymbolExpression> elist) throws Exception {
		Set<SymbolExpression> operands = new HashSet<SymbolExpression>();
		for(SymbolExpression operand : elist) { operands.add(operand); }
		elist.clear();
		for(SymbolExpression operand : operands) { elist.add(operand); }
		return this.vacc_expression_list(type, COperator.bit_or, elist);
	}
	/**
	 * @param type
	 * @param plist
	 * @param nlist
	 * @return the simplified version of operands
	 * @throws Exception
	 */
	private	SymbolExpression	sim_operands_in_bitws_xor(CType type, 
			List<SymbolExpression> plist, List<SymbolExpression> nlist) throws Exception {
		Map<SymbolExpression, Integer> counters = new HashMap<SymbolExpression, Integer>();
		
		/* count the operand's numbers by positive as pos */
		for(SymbolExpression operand : plist) {
			if(!counters.containsKey(operand)) {
				counters.put(operand, Integer.valueOf(0));
			}
			int counter = counters.get(operand) + 1;
			counters.put(operand, Integer.valueOf(counter));
		}
		
		/* count the operand's numbers by negative as rsv */
		for(SymbolExpression operand : nlist) {
			if(!counters.containsKey(operand)) {
				counters.put(operand, Integer.valueOf(0));
			}
			int counter = counters.get(operand) - 1;
			counters.put(operand, Integer.valueOf(counter));
		}
		
		/* generate the operands list */
		List<SymbolExpression> operands = new ArrayList<SymbolExpression>();
		for(SymbolExpression operand : counters.keySet()) {
			int counter = counters.get(operand).intValue();
			if(counter % 2 != 0) {
				if(counter < 0) {
					operands.add(SymbolFactory.bitws_rsv(operand));
				}
				else {
					operands.add(operand);
				}
			}
		}
		
		/* return */	
		return this.vacc_expression_list(type, COperator.bit_xor, operands);
	}
	/**
	 * @param expression	{&}
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_bitws_and(SymbolBinaryExpression expression) throws Exception {
		/* 1. declaration and initialization */
		List<SymbolExpression> plist = new ArrayList<SymbolExpression>();
		List<SymbolExpression> nlist = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant, constant;
		SymbolExpression operand;
		CType type = SymbolFactory.get_type(expression.get_data_type());
		
		/* 2. divide the operands in positive and reversed */
		this.div_operands_in_bitws_and(expression, plist, nlist);
		this.eval_expression_list(plist); this.eval_expression_list(nlist);
		lconstant = this.cacc_expression_list(COperator.bit_and, plist);
		rconstant = this.cacc_expression_list(COperator.bit_or, nlist);
		rconstant = SymbolComputer.do_compute(COperator.bit_not, rconstant);
		constant = SymbolComputer.do_compute(COperator.bit_and, lconstant, rconstant);
		
		/* 3. partial evaluation on constants and operands */
		if(constant.get_long() == 0) {
			return SymbolFactory.sym_constant(Long.valueOf(0));
		}
		else if(this.heqv_expression_list(plist, nlist)) {
			return SymbolFactory.sym_constant(Long.valueOf(0));
		}
		else {
			for(SymbolExpression nexp : nlist) {
				plist.add(SymbolFactory.bitws_rsv(nexp));
			}
			operand = this.sim_operands_in_bitws_and(type, plist);
		}
		
		/* 4. return */	
		if(operand == null) {
			return constant;
		}
		else if(SymbolComputer.compare_values(constant, Long.valueOf(~0))) {
			return operand;
		}
		else {
			return SymbolFactory.bitws_and(type, constant, operand);
		}
	}
	/**
	 * @param expression	{|}
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_bitws_ior(SymbolBinaryExpression expression) throws Exception {
		/* 1. declaration and initialization */
		List<SymbolExpression> plist = new ArrayList<SymbolExpression>();
		List<SymbolExpression> nlist = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant, constant; SymbolExpression operand;
		CType type = SymbolFactory.get_type(expression.get_data_type());
		
		/* 2. divide the operands in positive and reversed */
		this.div_operands_in_bitws_ior(expression, plist, nlist);
		this.eval_expression_list(plist); this.eval_expression_list(nlist);
		lconstant = this.cacc_expression_list(COperator.bit_or, plist);
		rconstant = this.cacc_expression_list(COperator.bit_and, nlist);
		rconstant = SymbolComputer.do_compute(COperator.bit_not, rconstant);
		constant = SymbolComputer.do_compute(COperator.bit_or, lconstant, rconstant);
		
		/* 3. partial evaluation on constants and operands */
		if(SymbolComputer.compare_values(constant, Long.valueOf(~0))) {
			return SymbolFactory.sym_constant(Long.valueOf(~0));
		}
		else if(this.heqv_expression_list(plist, nlist)) {
			return SymbolFactory.sym_constant(Long.valueOf(~0));
		}
		else {
			for(SymbolExpression nexp : nlist) {
				plist.add(SymbolFactory.bitws_rsv(nexp));
			}
			operand = this.sim_operands_in_bitws_ior(type, plist);
		}
		
		/* 5. return */
		if(operand == null) {
			return constant;
		}
		else if(SymbolComputer.compare_values(constant, Long.valueOf(0))) {
			return operand;
		}
		else {
			return SymbolFactory.bitws_ior(type, constant, operand);
		}
	}
	/**
	 * @param expression	{^}
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_bitws_xor(SymbolBinaryExpression expression) throws Exception {
		/* 1. declaration and initialization */
		List<SymbolExpression> plist = new ArrayList<SymbolExpression>();
		List<SymbolExpression> nlist = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant, constant; SymbolExpression operand;
		CType type = SymbolFactory.get_type(expression.get_data_type());
		
		/* 2. divide the operands in {xor} */
		this.div_operands_in_bitws_xor(expression, plist, nlist);
		this.eval_expression_list(plist); this.eval_expression_list(nlist);
		
		/* 3. constant evaluation and partial */
		lconstant = this.cacc_expression_list(COperator.bit_xor, plist);
		rconstant = this.cacc_expression_list(COperator.equal_with, nlist);
		constant = SymbolComputer.do_compute(COperator.bit_xor, lconstant, rconstant);
		operand = this.sim_operands_in_bitws_xor(type, plist, nlist);
		
		/* 4. construct the expression by constant and variable */
		if(operand == null) {
			return constant;
		}
		else {
			if(SymbolComputer.compare_values(constant, Long.valueOf(0))) {
				return operand;
			}
			else if(SymbolComputer.compare_values(constant, Long.valueOf(~0))) {
				return SymbolFactory.bitws_rsv(operand);
			}
			else {
				return SymbolFactory.bitws_xor(type, constant, operand);
			}
		}
	}
	
	/* symbolic evaluation on {<<, >>, ~} */
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_bitws_lsh(SymbolBinaryExpression expression) throws Exception {
		SymbolExpression loperand = this.eval(expression.get_loperand());
		SymbolExpression roperand = this.eval(expression.get_roperand());
		CType type = SymbolFactory.get_type(expression.get_data_type());
		
		if(loperand instanceof SymbolConstant) {
			long lvalue = ((SymbolConstant) loperand).get_long();
			if(roperand instanceof SymbolConstant) {
				long rvalue = ((SymbolConstant) roperand).get_long();
				return SymbolFactory.sym_constant(Long.valueOf(lvalue << rvalue));
			}
			else if(lvalue == 0) {
				return SymbolFactory.sym_constant(Long.valueOf(0));
			}
			else {
				return SymbolFactory.bitws_lsh(type, lvalue, roperand);
			}
		}
		else {
			if(roperand instanceof SymbolConstant) {
				long rvalue = ((SymbolConstant) roperand).get_long();
				if(rvalue == 0) {
					return loperand;
				}
				else {
					return SymbolFactory.bitws_lsh(type, loperand, rvalue);
				}
			}
			else {
				return SymbolFactory.bitws_lsh(type, loperand, roperand);
			}
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_bitws_rsh(SymbolBinaryExpression expression) throws Exception {
		SymbolExpression loperand = this.eval(expression.get_loperand());
		SymbolExpression roperand = this.eval(expression.get_roperand());
		CType type = SymbolFactory.get_type(expression.get_data_type());
		
		if(loperand instanceof SymbolConstant) {
			long lvalue = ((SymbolConstant) loperand).get_long();
			if(roperand instanceof SymbolConstant) {
				long rvalue = ((SymbolConstant) roperand).get_long();
				return SymbolFactory.sym_constant(Long.valueOf(lvalue >> rvalue));
			}
			else if(lvalue == 0) {
				return SymbolFactory.sym_constant(Long.valueOf(0));
			}
			else {
				return SymbolFactory.bitws_rsh(type, lvalue, roperand);
			}
		}
		else {
			if(roperand instanceof SymbolConstant) {
				long rvalue = ((SymbolConstant) roperand).get_long();
				if(rvalue == 0) {
					return loperand;
				}
				else {
					return SymbolFactory.bitws_rsh(type, loperand, rvalue);
				}
			}
			else {
				return SymbolFactory.bitws_rsh(type, loperand, roperand);
			}
		}
	}
	/**
	 * @param rsv			true to reverse or not
	 * @param expression	the expression to be reversed in bitwise operation
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_bitws_rsv(boolean rsv, SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolConstant) {
			Long value = ((SymbolConstant) expression).get_long();
			if(rsv) { value = Long.valueOf(~value); }
			return SymbolFactory.sym_constant(value);
		}
		else if(expression instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) expression).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			if(operator == COperator.negative) {
				return this.eval(SymbolFactory.arith_sub(uoperand.get_data_type(), uoperand, Integer.valueOf(1)));
			}
			else if(operator == COperator.bit_not) {
				return this.eval_bitws_rsv(!rsv, uoperand);
			}
			else {
				if(rsv) {
					return SymbolFactory.bitws_rsv(expression);
				}
				else {
					return expression;
				}
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_coperator();
			List<SymbolExpression> plist = new ArrayList<SymbolExpression>();
			List<SymbolExpression> nlist = new ArrayList<SymbolExpression>();
			CType type = SymbolFactory.get_type(expression.get_data_type());
			
			if(operator == COperator.bit_and) {
				this.div_operands_in_bitws_and(expression, plist, nlist);
				for(SymbolExpression pexp : plist) {
					nlist.add(SymbolFactory.bitws_rsv(pexp));
				}
				return this.vacc_expression_list(type, COperator.bit_or, nlist);
			}
			else if(operator == COperator.bit_or) {
				this.div_operands_in_bitws_ior(expression, plist, nlist);
				for(SymbolExpression pexp : plist) {
					nlist.add(SymbolFactory.bitws_rsv(pexp));
				}
				return this.vacc_expression_list(type, COperator.bit_and, nlist);
			}
			else {
				if(rsv) {
					return SymbolFactory.bitws_rsv(expression);
				}
				else {
					return expression;
				}
			}
		}
		else {
			if(rsv) {
				return SymbolFactory.bitws_rsv(expression);
			}
			else {
				return expression;
			}
		}
	}
	/**
	 * @param expression {~}
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_bitws_rsv(SymbolUnaryExpression expression) throws Exception {
		return this.eval_bitws_rsv(true, expression);
	}
	
	/* symbolic evaluation on {&&, ||, ->, not} */
	/**
	 * @param expression	{&&}
	 * @param plist			to preserve the operands in non-not part
	 * @param nlist			to preserve the operands in log_not part
	 * @throws Exception
	 */
	private	void				div_operands_in_logic_and(SymbolExpression expression,
			List<SymbolExpression> plist, List<SymbolExpression> nlist) throws Exception {
		if(expression == null) { return; }										/** NULL-NONE **/
		else if(expression instanceof SymbolConstant) {							/** BOOL-CONS **/
			Boolean value = ((SymbolConstant) expression).get_bool();
			plist.add(SymbolFactory.sym_constant(value));
		}
		else if(expression instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) expression).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			if(operator == COperator.logic_not) {								/** LOGIC_NOT **/
				this.div_operands_in_logic_ior(uoperand, nlist, plist);
			}
			else { plist.add(SymbolFactory.sym_condition(expression, true)); }	/** OTHERWISE **/
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_coperator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(operator == COperator.logic_and) {								/** LOGIC_AND **/
				this.div_operands_in_logic_and(loperand, plist, nlist);
				this.div_operands_in_logic_and(roperand, plist, nlist);
			}
			else { plist.add(SymbolFactory.sym_condition(expression, true)); }	/** OTHERWISE **/
		}
		else { plist.add(SymbolFactory.sym_condition(expression, true)); }		/** OTHERWISE **/
	}
	/**
	 * @param expression	{||}
	 * @param plist			to preserve the operands in non-not part
	 * @param nlist			to preserve the operands in log_not part
	 * @throws Exception
	 */
	private	void				div_operands_in_logic_ior(SymbolExpression expression,
			List<SymbolExpression> plist, List<SymbolExpression> nlist) throws Exception {
		if(expression == null) { return; }										/** NULL-NONE **/
		else if(expression instanceof SymbolConstant) {							/** BOOL-CONS **/
			Boolean value = ((SymbolConstant) expression).get_bool();
			plist.add(SymbolFactory.sym_constant(value));
		}
		else if(expression instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) expression).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			if(operator == COperator.logic_not) {								/** LOGIC_NOT **/
				this.div_operands_in_logic_and(uoperand, nlist, plist);
			}
			else { plist.add(SymbolFactory.sym_condition(expression, true)); }	/** OTHERWISE **/
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_coperator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(operator == COperator.logic_or) {								/** LOGIC_IOR **/
				this.div_operands_in_logic_ior(loperand, plist, nlist);
				this.div_operands_in_logic_ior(roperand, plist, nlist);
			}
			else { plist.add(SymbolFactory.sym_condition(expression, true)); }	/** OTHERWISE **/
		}
		else { plist.add(SymbolFactory.sym_condition(expression, true)); }		/** OTHERWISE **/
	}
	/**
	 * @param elist
	 * @return	it removes the duplicated operands
	 * @throws Exception
	 */
	private	SymbolExpression	sim_operands_in_logic_and(List<SymbolExpression> elist) throws Exception {
		Set<SymbolExpression> operands = new HashSet<SymbolExpression>();
		for(SymbolExpression operand : elist) { operands.add(operand); }
		elist.clear();
		for(SymbolExpression operand : operands) { elist.add(operand); }
		return this.vacc_expression_list(CBasicTypeImpl.bool_type, COperator.logic_and, elist);
	}
	/**
	 * @param elist
	 * @return	it removes the duplicated operands
	 * @throws Exception
	 */
	private	SymbolExpression	sim_operands_in_logic_ior(List<SymbolExpression> elist) throws Exception {
		Set<SymbolExpression> operands = new HashSet<SymbolExpression>();
		for(SymbolExpression operand : elist) { operands.add(operand); }
		elist.clear();
		for(SymbolExpression operand : operands) { elist.add(operand); }
		return this.vacc_expression_list(CBasicTypeImpl.bool_type, COperator.logic_or, elist);
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_logic_and(SymbolBinaryExpression expression) throws Exception {
		/* 1. declaration and initialization */
		List<SymbolExpression> plist = new ArrayList<SymbolExpression>();
		List<SymbolExpression> nlist = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant, constant;
		SymbolExpression operand;
		
		/* 2. divide the operands in positive and reversed */
		this.div_operands_in_logic_and(expression, plist, nlist);
		this.eval_expression_list(plist); this.eval_expression_list(nlist);
		
		/* 3. accumulate the left-constant and right-constant */
		lconstant = this.cacc_expression_list(COperator.logic_and, plist);
		rconstant = this.cacc_expression_list(COperator.logic_or, nlist);
		rconstant = SymbolComputer.do_compute(COperator.logic_not, rconstant);
		constant = SymbolComputer.do_compute(COperator.logic_and, lconstant, rconstant);
		
		/* 4. partial evaluation */
		if(!constant.get_bool()) {
			return SymbolFactory.sym_constant(Boolean.FALSE);
		}
		else if(this.heqv_expression_list(plist, nlist)) {
			return SymbolFactory.sym_constant(Boolean.FALSE);
		}
		else {
			for(SymbolExpression nexp : nlist) {
				plist.add(SymbolFactory.sym_condition(nexp, false));
			}
			operand = this.sim_operands_in_logic_and(plist);
		}
		
		if(operand != null) {
			return operand;
		}
		else {
			return constant;
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_logic_ior(SymbolBinaryExpression expression) throws Exception {
		/* 1. declaration and initialization */
		List<SymbolExpression> plist = new ArrayList<SymbolExpression>();
		List<SymbolExpression> nlist = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant, constant;
		SymbolExpression operand;
		
		/* 2. divide the operands in positive and reversed */
		this.div_operands_in_logic_ior(expression, plist, nlist);
		this.eval_expression_list(plist); this.eval_expression_list(nlist);
		
		/* 3. accumulate the left-constant and right-constant */
		lconstant = this.cacc_expression_list(COperator.logic_or, plist);
		rconstant = this.cacc_expression_list(COperator.logic_and, nlist);
		rconstant = SymbolComputer.do_compute(COperator.logic_not, rconstant);
		constant = SymbolComputer.do_compute(COperator.logic_or, lconstant, rconstant);
		
		/* 4. partial evaluation */
		if(constant.get_bool()) {
			return SymbolFactory.sym_constant(Boolean.TRUE);
		}
		else if(this.heqv_expression_list(plist, nlist)) {
			return SymbolFactory.sym_constant(Boolean.TRUE);
		}
		else {
			for(SymbolExpression nexp : nlist) {
				plist.add(SymbolFactory.sym_condition(nexp, false));
			}
			operand = this.sim_operands_in_logic_ior(plist);
		}
		if(operand != null) {
			return operand;
		}
		else {
			return constant;
		}
	}
	/**
	 * @param expression
	 * @return !X || Y
	 * @throws Exception
	 */
	private	SymbolExpression	eval_logic_imp(SymbolBinaryExpression expression) throws Exception {
		SymbolExpression loperand = this.eval(expression.get_loperand());
		SymbolExpression roperand = this.eval(expression.get_roperand());
		loperand = SymbolFactory.sym_condition(loperand, false);
		roperand = SymbolFactory.sym_condition(roperand, true);
		expression = SymbolFactory.logic_ior(loperand, roperand);
		return this.eval(expression);
	}
	/**
	 * @param not
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_logic_not(boolean not, SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolConstant) {
			Boolean value = ((SymbolConstant) expression).get_bool();
			if(not) { value = Boolean.valueOf(!value); }
			return SymbolFactory.sym_constant(value);
		}
		else if(expression instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) expression).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) expression).get_operand();
			if(operator == COperator.logic_not) {
				return this.eval_logic_not(!not, uoperand);
			}
			else {
				if(not) {
					return SymbolFactory.sym_condition(expression, false);
				}
				else {
					return SymbolFactory.sym_condition(expression, true);
				}
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_coperator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			List<SymbolExpression> plist = new ArrayList<SymbolExpression>();
			List<SymbolExpression> nlist = new ArrayList<SymbolExpression>();
			if(operator == COperator.logic_and) {
				this.div_operands_in_logic_and(expression, plist, nlist);
				this.eval_expression_list(plist); this.eval_expression_list(nlist);
				for(SymbolExpression pexp : plist) {
					nlist.add(SymbolFactory.sym_condition(pexp, false));
				}
				return this.vacc_expression_list(CBasicTypeImpl.bool_type, COperator.logic_or, nlist);
			}
			else if(operator == COperator.logic_or) {
				this.div_operands_in_logic_ior(expression, plist, nlist);
				this.eval_expression_list(plist); this.eval_expression_list(nlist);
				for(SymbolExpression pexp : plist) {
					nlist.add(SymbolFactory.sym_condition(pexp, false));
				}
				return this.vacc_expression_list(CBasicTypeImpl.bool_type, COperator.logic_and, nlist);
			}
			else if(operator == COperator.greater_eq) {
				if(not) {
					return SymbolFactory.smaller_tn(loperand, roperand);
				}
				else {
					return expression;
				}
			}
			else if(operator == COperator.greater_tn) {
				if(not) {
					return SymbolFactory.smaller_eq(loperand, roperand);
				}
				else {
					return expression;
				}
			}
			else if(operator == COperator.smaller_eq) {
				if(not) {
					return SymbolFactory.greater_tn(loperand, roperand);
				}
				else {
					return expression;
				}
			}
			else if(operator == COperator.smaller_tn) {
				if(not) {
					return SymbolFactory.greater_eq(loperand, roperand);
				}
				else {
					return expression;
				}
			}
			else if(operator == COperator.equal_with) {
				if(not) {
					return SymbolFactory.not_equals(loperand, roperand);
				}
				else {
					return expression;
				}
			}
			else if(operator == COperator.not_equals) {
				if(not) {
					return SymbolFactory.equal_with(loperand, roperand);
				}
				else {
					return expression;
				}
			}
			else {
				if(not) {
					return SymbolFactory.sym_condition(expression, false);
				}
				else {
					return SymbolFactory.sym_condition(expression, true);
				}
			}
		}
		else {
			if(not) {
				return SymbolFactory.sym_condition(expression, false);
			}
			else {
				return SymbolFactory.sym_condition(expression, true);
			}
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_logic_not(SymbolUnaryExpression expression) throws Exception {
		return this.eval_logic_not(true, expression);
	}
	
	/* symbolic evaluation on {&, *, :=, <-} */
	private	SymbolExpression	eval_addr_of(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) operand).get_operand();
			if(operator == COperator.dereference) {
				return uoperand;
			}
			else {
				return SymbolFactory.address_of(operand);
			}
		}
		else {
			return SymbolFactory.address_of(operand);
		}
	}
	private	SymbolExpression	eval_de_refer(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) operand).get_operand();
			if(operator == COperator.address_of) {
				return uoperand;
			}
			else {
				return SymbolFactory.dereference(operand);
			}
		}
		else {
			return SymbolFactory.dereference(operand);
		}
	}
	private	SymbolExpression	eval_ex_assign(SymbolBinaryExpression expression) throws Exception {
		SymbolExpression loperand = this.eval(expression.get_loperand());
		SymbolExpression roperand = this.eval(expression.get_roperand());
		this.set_state_value(loperand, roperand);
		return roperand;
	}
	private	SymbolExpression	eval_im_assign(SymbolBinaryExpression expression) throws Exception {
		SymbolExpression loperand = this.eval(expression.get_loperand());
		SymbolExpression roperand = this.eval(expression.get_roperand());
		this.set_state_value(loperand, roperand);
		return loperand;
	}
	
	/* symbolic evaluation on {<, <=, >, >=, ==, !=} */
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_equal_with(SymbolBinaryExpression expression) throws Exception {
		SymbolExpression loperand = this.eval(expression.get_loperand());
		SymbolExpression roperand = this.eval(expression.get_roperand());
		loperand = SymbolFactory.arith_sub(loperand.get_data_type(), loperand, roperand);
		loperand = this.eval(loperand);
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			SymbolConstant rconstant = SymbolFactory.sym_constant(Long.valueOf(0));
			return SymbolComputer.do_compute(COperator.equal_with, lconstant, rconstant);
		}
		else if(loperand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) loperand).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) loperand).get_operand();
			if(operator == COperator.negative) {
				return SymbolFactory.equal_with(uoperand, Long.valueOf(0));
			}
			else {
				return SymbolFactory.equal_with(loperand, Long.valueOf(0));
			}
		}
		else {
			return SymbolFactory.equal_with(loperand, Long.valueOf(0));
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_not_equals(SymbolBinaryExpression expression) throws Exception {
		SymbolExpression loperand = this.eval(expression.get_loperand());
		SymbolExpression roperand = this.eval(expression.get_roperand());
		loperand = SymbolFactory.arith_sub(loperand.get_data_type(), loperand, roperand);
		loperand = this.eval(loperand);
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			SymbolConstant rconstant = SymbolFactory.sym_constant(Long.valueOf(0));
			return SymbolComputer.do_compute(COperator.not_equals, lconstant, rconstant);
		}
		else if(loperand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) loperand).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) loperand).get_operand();
			if(operator == COperator.negative) {
				return SymbolFactory.not_equals(uoperand, Long.valueOf(0));
			}
			else {
				return SymbolFactory.not_equals(loperand, Long.valueOf(0));
			}
		}
		else {
			return SymbolFactory.not_equals(loperand, Long.valueOf(0));
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_greater_tn(SymbolBinaryExpression expression) throws Exception {
		SymbolExpression loperand = this.eval(expression.get_loperand());
		SymbolExpression roperand = this.eval(expression.get_roperand());
		loperand = SymbolFactory.arith_sub(loperand.get_data_type(), loperand, roperand);
		loperand = this.eval(loperand);
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			SymbolConstant rconstant = SymbolFactory.sym_constant(Long.valueOf(0));
			return SymbolComputer.do_compute(COperator.greater_tn, lconstant, rconstant);
		}
		else if(loperand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) loperand).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) loperand).get_operand();
			if(operator == COperator.negative) {
				return SymbolFactory.smaller_tn(uoperand, Long.valueOf(0));
			}
			else {
				return SymbolFactory.greater_tn(loperand, Long.valueOf(0));
			}
		}
		else {
			return SymbolFactory.greater_tn(loperand, Long.valueOf(0));
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_greater_eq(SymbolBinaryExpression expression) throws Exception {
		SymbolExpression loperand = this.eval(expression.get_loperand());
		SymbolExpression roperand = this.eval(expression.get_roperand());
		loperand = SymbolFactory.arith_sub(loperand.get_data_type(), loperand, roperand);
		loperand = this.eval(loperand);
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			SymbolConstant rconstant = SymbolFactory.sym_constant(Long.valueOf(0));
			return SymbolComputer.do_compute(COperator.greater_eq, lconstant, rconstant);
		}
		else if(loperand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) loperand).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) loperand).get_operand();
			if(operator == COperator.negative) {
				return SymbolFactory.smaller_eq(uoperand, Long.valueOf(0));
			}
			else {
				return SymbolFactory.greater_eq(loperand, Long.valueOf(0));
			}
		}
		else {
			return SymbolFactory.greater_eq(loperand, Long.valueOf(0));
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_smaller_tn(SymbolBinaryExpression expression) throws Exception {
		SymbolExpression loperand = this.eval(expression.get_loperand());
		SymbolExpression roperand = this.eval(expression.get_roperand());
		loperand = SymbolFactory.arith_sub(loperand.get_data_type(), loperand, roperand);
		loperand = this.eval(loperand);
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			SymbolConstant rconstant = SymbolFactory.sym_constant(Long.valueOf(0));
			return SymbolComputer.do_compute(COperator.smaller_tn, lconstant, rconstant);
		}
		else if(loperand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) loperand).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) loperand).get_operand();
			if(operator == COperator.negative) {
				return SymbolFactory.greater_tn(uoperand, Long.valueOf(0));
			}
			else {
				return SymbolFactory.smaller_tn(loperand, Long.valueOf(0));
			}
		}
		else {
			return SymbolFactory.smaller_tn(loperand, Long.valueOf(0));
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	eval_smaller_eq(SymbolBinaryExpression expression) throws Exception {
		SymbolExpression loperand = this.eval(expression.get_loperand());
		SymbolExpression roperand = this.eval(expression.get_roperand());
		loperand = SymbolFactory.arith_sub(loperand.get_data_type(), loperand, roperand);
		loperand = this.eval(loperand);
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			SymbolConstant rconstant = SymbolFactory.sym_constant(Long.valueOf(0));
			return SymbolComputer.do_compute(COperator.smaller_eq, lconstant, rconstant);
		}
		else if(loperand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) loperand).get_coperator();
			SymbolExpression uoperand = ((SymbolUnaryExpression) loperand).get_operand();
			if(operator == COperator.negative) {
				return SymbolFactory.greater_eq(uoperand, Long.valueOf(0));
			}
			else {
				return SymbolFactory.smaller_eq(loperand, Long.valueOf(0));
			}
		}
		else {
			return SymbolFactory.smaller_eq(loperand, Long.valueOf(0));
		}
	}
	
}
