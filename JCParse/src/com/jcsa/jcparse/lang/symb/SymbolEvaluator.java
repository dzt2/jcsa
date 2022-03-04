package com.jcsa.jcparse.lang.symb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It implements the (partial) evaluation of input symbolic expression.
 * 
 * @author yukimula
 *
 */
public class SymbolEvaluator {
	
	/* constructor and definitions */
	/** the list of method-invoking interfaces to proceed caller **/
	private List<SymbolMethodInvoke> 	inv_list;
	/** the maps from variables which are updated, to new values **/
	private Map<SymbolExpression, SymbolExpression> state_map;
	/**
	 * private constructor for singleton mode
	 */
	private SymbolEvaluator() {
		this.inv_list = new ArrayList<SymbolMethodInvoke>();
		this.state_map = null;
	}
	
	/* singleton mode and parameters setting */
	/** singleton instance **/
	private static final SymbolEvaluator evaluator = new SymbolEvaluator();
	/**
	 * @param invoke
	 */
	public static void add_invoke(SymbolMethodInvoke invoke) {
		if(invoke == null) {
			return;
		}
		else if(!evaluator.inv_list.contains(invoke)) {
			evaluator.inv_list.add(invoke);
		}
	}
	/**
	 * @param reference	the reference of which value is updated in the evaluation
	 * @param new_value	the new value to be updated and reset states of reference
	 * @throws Exception
	 */
	private void update_state(SymbolExpression reference, SymbolExpression new_value) throws Exception {
		if(reference == null) {
			throw new IllegalArgumentException("Invalid reference: null");
		}
		else if(new_value == null) {
			throw new IllegalArgumentException("Invalid new_value: null");
		}
		else if(this.state_map == null) { /* do nothing */ }
		else { this.state_map.put(reference, new_value); }
	}
	
	/* partial evaluation methods (recursive) */
	/**
	 * 	The recursive evaluation of symbolic expressions.
	 * 	@param expression
	 * 	@return
	 * 	@throws Exception
	 */
	private SymbolExpression eval(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolBasicExpression) {
			return this.eval_base_expr((SymbolBasicExpression) expression);
		}
		else if(expression instanceof SymbolCastExpression) {
			return this.eval_cast_expr((SymbolCastExpression) expression);
		}
		else if(expression instanceof SymbolFieldExpression) {
			return this.eval_field_exp((SymbolFieldExpression) expression);
		}
		else if(expression instanceof SymbolInitializerList) {
			return this.eval_init_list((SymbolInitializerList) expression);
		}
		else if(expression instanceof SymbolConditionExpression) {
			return this.eval_cond_expr((SymbolConditionExpression) expression);
		}
		else if(expression instanceof SymbolCallExpression) {
			return this.eval_call_expr((SymbolCallExpression) expression);
		}
		else if(expression instanceof SymbolUnaryExpression) {
			return this.eval_unary_exp((SymbolUnaryExpression) expression);
		}
		else if(expression instanceof SymbolBinaryExpression) {
			return this.eval_biny_expr((SymbolBinaryExpression) expression);
		}
		else {
			throw new IllegalArgumentException(expression.get_symbol_class().toString());
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression eval_base_expr(SymbolBasicExpression expression) throws Exception {
		return expression;
	}
	/**
	 * @param expression
	 * @return constant | cast_bool | otherwise
	 * @throws Exception
	 */
	private SymbolExpression eval_cast_expr(SymbolCastExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_casted_operand());
		CType type = CTypeAnalyzer.get_value_type(expression.get_cast_type().get_type());
		if(operand instanceof SymbolConstant) {
			return SymbolComputer.do_compute(type, (SymbolConstant) operand);
		}
		else if(CTypeAnalyzer.is_boolean(type)) {
			return SymbolFactory.sym_condition(operand, true);
		}
		else {
			return SymbolFactory.cast_expression(type, operand);
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression eval_field_exp(SymbolFieldExpression expression) throws Exception {
		SymbolExpression body = this.eval(expression.get_body());
		return SymbolFactory.field_expression(body, expression.get_field().get_name());
	}
	/**
	 * @param expression
	 * @return 
	 * @throws Exception
	 */
	private SymbolExpression eval_init_list(SymbolInitializerList expression) throws Exception {
		List<Object> elements = new ArrayList<Object>();
		for(int k = 0; k < expression.number_of_elements(); k++) {
			elements.add(this.eval(expression.get_element(k)));
		}
		return SymbolFactory.initializer_list(elements);
	}
	/**
	 * @param expression
	 * @return const_condition | equal_t_fvalue | cond_expr
	 * @throws Exception
	 */
	private SymbolExpression eval_cond_expr(SymbolConditionExpression expression) throws Exception {
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
		else if(SymbolComputer.is_equivalent(t_operand, f_operand)) {
			return t_operand;
		}
		else {
			return SymbolFactory.cond_expr(expression.get_data_type(), condition, t_operand, f_operand);
		}
	}
	/**
	 * @param expression
	 * @return invoke_list | func(arguments)
	 * @throws Exception
	 */
	private SymbolExpression eval_call_expr(SymbolCallExpression expression) throws Exception {
		SymbolExpression function = this.eval(expression.get_function());
		List<Object> arguments = new ArrayList<Object>();
		for(int k = 0; k < expression.get_argument_list().number_of_arguments(); k++) {
			arguments.add(this.eval(expression.get_argument_list().get_argument(k)));
		}
		SymbolCallExpression call_expr = SymbolFactory.call_expression(function, arguments);
		
		for(SymbolMethodInvoke invoke : this.inv_list) {
			SymbolExpression result = invoke.invoke(call_expr);
			if(result != null) {
				return result;
			}
		}
		return call_expr;
	}
	
	/* unary expression (+, -, ~, !, *, &, ++, --, p++, p--) */
	/**
	 * @param expression
	 * @return (+, -, ~, !, *, &, ++, --, p++, p--)
	 * @throws Exception
	 */
	private SymbolExpression eval_unary_exp(SymbolUnaryExpression expression) throws Exception {
		COperator operator = expression.get_operator().get_operator();
		switch(operator) {
		case positive:			return this.eval_arith_pos(expression);
		case negative:			return this.eval_arith_neg(expression);
		case bit_not:			return this.eval_bitws_rsv(expression);
		case logic_not:			return this.eval_logic_not(expression);
		case address_of:		return this.eval_addressOf(expression);
		case dereference:		return this.eval_derefered(expression);
		case increment:			return this.eval_increment(expression);
		case decrement:			return this.eval_decrement(expression);
		case arith_add_assign:	return this.eval_pos_incre(expression);
		case arith_sub_assign:	return this.eval_pos_decre(expression);
		default:				throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	/**
	 * @param expression
	 * @return +constant | boolean | otherwise
	 * @throws Exception
	 */
	private SymbolExpression eval_arith_pos(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolConstant) {
			return SymbolComputer.do_compute(COperator.positive, (SymbolConstant) operand);
		}
		else if(SymbolFactory.is_bool(operand)) {
			return operand;
		}
		else {
			return operand;
		}
	}
	/**
	 * @param expression
	 * @return -constant | arith_neg | bitws_rsv | arith_sub | boolean | conditional | otherwise
	 * @throws Exception
	 */
	private SymbolExpression eval_arith_neg(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolConstant) {	/* -constant */
			return SymbolComputer.do_compute(COperator.negative, (SymbolConstant) operand);
		}
		else if(operand instanceof SymbolConditionExpression) {
			SymbolExpression condition = ((SymbolConditionExpression) operand).get_condition();
			SymbolExpression t_operand = SymbolFactory.arith_neg(((SymbolConditionExpression) operand).get_toperand());
			SymbolExpression f_operand = SymbolFactory.arith_neg(((SymbolConditionExpression) operand).get_foperand());
			return this.eval(SymbolFactory.cond_expr(operand.get_data_type(), condition, t_operand, f_operand));
		}
		else if(SymbolFactory.is_bool(operand)) {	/* boolean is not negated */
			return operand;		
		}
		else if(operand instanceof SymbolUnaryExpression) { /* {-, ~, !} */
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			SymbolExpression u_operand = ((SymbolUnaryExpression) operand).get_operand();
			switch(operator) {
			case negative:						/* -(-x) = x */
			{
				return u_operand;				
			}
			case bit_not:						/* -(~u) --> u + 1 */
			{
				SymbolExpression loperand = u_operand;
				SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(1));
				SymbolExpression result = SymbolFactory.arith_add(operand.get_data_type(), loperand, roperand);
				return this.eval(result);
			}
			default:							/* general case */
			{
				return SymbolFactory.arith_neg(operand);
			}
			}
		}
		else if(operand instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) operand).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) operand).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) operand).get_roperand();
			if(operator == COperator.arith_sub) {
				return SymbolFactory.arith_sub(operand.get_data_type(), roperand, loperand);
			}
			else {
				return SymbolFactory.arith_neg(operand);
			}
		}
		else {
			return SymbolFactory.arith_neg(operand);
		}
	}
	/**
	 * @param expression
	 * @return ~const | arith_neg | bitws_rsv | boolean | otherwise 
	 * @throws Exception
	 */
	private SymbolExpression eval_bitws_rsv(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolConstant) {
			return SymbolComputer.do_compute(COperator.bit_not, (SymbolConstant) operand);
		}
		else if(operand instanceof SymbolConditionExpression) {
			SymbolExpression condition = ((SymbolConditionExpression) operand).get_condition();
			SymbolExpression t_operand = SymbolFactory.bitws_rsv(((SymbolConditionExpression) operand).get_toperand());
			SymbolExpression f_operand = SymbolFactory.bitws_rsv(((SymbolConditionExpression) operand).get_foperand());
			return this.eval(SymbolFactory.cond_expr(operand.get_data_type(), condition, t_operand, f_operand));
		}
		else if(SymbolFactory.is_bool(operand)) {
			return SymbolFactory.sym_constant(Boolean.TRUE);
		}
		else if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			SymbolExpression u_operand = ((SymbolUnaryExpression) operand).get_operand();
			if(operator == COperator.negative) {
				SymbolExpression loperand = u_operand;
				SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(1));
				SymbolExpression result = SymbolFactory.arith_sub(operand.get_data_type(), loperand, roperand);
				return this.eval(result);
			}
			else if(operator == COperator.bit_not) {
				return u_operand;
			}
			else {
				return SymbolFactory.bitws_rsv(operand);
			}
		}
		else {
			return SymbolFactory.bitws_rsv(operand);
		}
	}
	/**
	 * @param expression
	 * @return 	!const | logic_not | arith_neg | bitws_rsv | logic_and | logic_ior |
	 * 			greater_tn | greater_eq | smaller_tn | smaller_eq | not_equals |
	 * 			equal_with | boolean | numberic | pointer
	 * @throws Exception
	 */
	private SymbolExpression eval_logic_not(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolConstant) {
			return SymbolComputer.do_compute(COperator.logic_not, (SymbolConstant) operand);
		}
		else if(operand instanceof SymbolConditionExpression) {
			SymbolExpression condition = ((SymbolConditionExpression) operand).get_condition();
			SymbolExpression t_operand = ((SymbolConditionExpression) operand).get_toperand();
			SymbolExpression f_operand = ((SymbolConditionExpression) operand).get_foperand();
			condition = SymbolFactory.sym_condition(condition, false);
			return this.eval(SymbolFactory.cond_expr(operand.get_data_type(), condition, t_operand, f_operand));
		}
		else if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			SymbolExpression u_operand = ((SymbolUnaryExpression) operand).get_operand();
			if(operator == COperator.positive) {
				return SymbolFactory.equal_with(u_operand, Integer.valueOf(0));
			}
			else if(operator == COperator.negative) {
				return SymbolFactory.equal_with(u_operand, Integer.valueOf(0));
			}
			else if(operator == COperator.logic_not) {
				return u_operand;
			}
			else {
				return SymbolFactory.sym_condition(operand, false);
			}
		}
		else if(operand instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) operand).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) operand).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) operand).get_roperand();
			if(operator == COperator.logic_and) {
				loperand = SymbolFactory.sym_condition(loperand, false);
				roperand = SymbolFactory.sym_condition(roperand, false);
				return this.eval(SymbolFactory.logic_ior(loperand, roperand));
			}
			else if(operator == COperator.logic_or) {
				loperand = SymbolFactory.sym_condition(loperand, false);
				roperand = SymbolFactory.sym_condition(roperand, false);
				return this.eval(SymbolFactory.logic_and(loperand, roperand));
			}
			else if(operator == COperator.greater_tn) {
				return SymbolFactory.smaller_eq(loperand, roperand);
			}
			else if(operator == COperator.greater_eq) {
				return SymbolFactory.smaller_tn(loperand, roperand);
			}
			else if(operator == COperator.smaller_tn) {
				return SymbolFactory.greater_eq(loperand, roperand);
			}
			else if(operator == COperator.smaller_eq) {
				return SymbolFactory.greater_tn(loperand, roperand);
			}
			else if(operator == COperator.equal_with) {
				return SymbolFactory.not_equals(loperand, roperand);
			}
			else if(operator == COperator.not_equals) {
				return SymbolFactory.equal_with(loperand, roperand);
			}
			else {
				return SymbolFactory.sym_condition(operand, false);
			}
		}
		else {
			return SymbolFactory.sym_condition(operand, false);
		}
	}
	/**
	 * @param expression
	 * @return de_refer | otherwise
	 * @throws Exception
	 */
	private SymbolExpression eval_addressOf(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			SymbolExpression u_operand = ((SymbolUnaryExpression) operand).get_operand();
			if(operator == COperator.dereference) {
				return u_operand;
			}
			else {
				return SymbolFactory.address_of(operand);
			}
		}
		else {
			return SymbolFactory.address_of(operand);
		}
	}
	/**
	 * @param expression
	 * @return addr_of | otherwise
	 * @throws Exception
	 */
	private SymbolExpression eval_derefered(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			SymbolExpression u_operand = ((SymbolUnaryExpression) operand).get_operand();
			if(operator == COperator.address_of) {
				return u_operand;
			}
			else {
				return SymbolFactory.dereference(operand);
			}
		}
		else {
			return SymbolFactory.dereference(operand);
		}
	}
	/**
	 * @param expression
	 * @return ++operand
	 * @throws Exception
	 */
	private SymbolExpression eval_increment(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolConstant) {
			return SymbolComputer.do_compute(COperator.increment, (SymbolConstant) operand);
		}
		else if(operand.is_reference()) {
			SymbolExpression loperand = operand;
			SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(1));
			SymbolExpression new_value = SymbolFactory.arith_add(operand.get_data_type(), loperand, roperand);
			this.update_state(loperand, new_value);
			return new_value;
		}
		else {
			throw new IllegalArgumentException(expression.get_symbol_class().toString());
		}
	}
	/**
	 * @param expression
	 * @return --operand
	 * @throws Exception
	 */
	private SymbolExpression eval_decrement(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolConstant) {
			return SymbolComputer.do_compute(COperator.decrement, (SymbolConstant) operand);
		}
		else if(operand.is_reference()) {
			SymbolExpression loperand = operand;
			SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(1));
			SymbolExpression new_value = SymbolFactory.arith_sub(operand.get_data_type(), loperand, roperand);
			this.update_state(loperand, new_value);
			return new_value;
		}
		else {
			throw new IllegalArgumentException(expression.get_symbol_class().toString());
		}
	}
	/**
	 * @param expression
	 * @return operand++
	 * @throws Exception
	 */
	private SymbolExpression eval_pos_incre(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolConstant) {
			return SymbolComputer.do_compute(COperator.increment, (SymbolConstant) operand);
		}
		else if(operand.is_reference()) {
			SymbolExpression loperand = operand;
			SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(1));
			SymbolExpression new_value = SymbolFactory.arith_add(operand.get_data_type(), loperand, roperand);
			this.update_state(loperand, new_value);
			return operand;
		}
		else {
			throw new IllegalArgumentException(expression.get_symbol_class().toString());
		}
	}
	/**
	 * @param expression
	 * @return operand--
	 * @throws Exception
	 */
	private SymbolExpression eval_pos_decre(SymbolUnaryExpression expression) throws Exception {
		SymbolExpression operand = this.eval(expression.get_operand());
		if(operand instanceof SymbolConstant) {
			return SymbolComputer.do_compute(COperator.increment, (SymbolConstant) operand);
		}
		else if(operand.is_reference()) {
			SymbolExpression loperand = operand;
			SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(1));
			SymbolExpression new_value = SymbolFactory.arith_sub(operand.get_data_type(), loperand, roperand);
			this.update_state(loperand, new_value);
			return operand;
		}
		else {
			throw new IllegalArgumentException(expression.get_symbol_class().toString());
		}
	}
	
	/* binary expression {+, -, *, /, %, &, |, ^, <<, >>, &&, ||, <, <=, >, >=, ==, !=} */
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression eval_biny_expr(SymbolBinaryExpression expression) throws Exception {
		COperator operator = expression.get_operator().get_operator();
		// TODO implement the binary operator evaluation
		switch(operator) {
		case arith_add:		return this.eval_arith_add_and_sub(expression);
		case arith_sub:		return this.eval_arith_add_and_sub(expression);
		case arith_mul:		return this.eval_arith_mul_and_div(expression);
		case arith_div:		return this.eval_arith_mul_and_div(expression);
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
		default:			throw new IllegalArgumentException(operator.toString());
		}
	}
	/**
	 * @param operands
	 * @return the list of operands evaluated from the input operands
	 * @throws Exception
	 */
	private List<SymbolExpression> eval_operand_list(List<SymbolExpression> operands) throws Exception {
		List<SymbolExpression> outputs = new ArrayList<SymbolExpression>();
		for(SymbolExpression operand : operands) {
			outputs.add(this.eval(operand));
		}
		return outputs;
	}
	/**
	 * It updates the two lists by deleting operands that are equivalent to each other. 
	 * @param alist
	 * @param blist
	 * @throws Exception
	 */
	private void del_equivalent_operands(List<SymbolExpression> alist, List<SymbolExpression> blist) throws Exception {
		while(alist.size() > 0 && blist.size() > 0) {
			SymbolExpression arm = null, brm = null;
			for(SymbolExpression aop : alist) {
				for(SymbolExpression bop : blist) {
					if(SymbolComputer.is_equivalent(aop, bop)) {
						arm = aop; 
						brm = bop;
						break;
					}
				}
				if(arm != null) { break; }
			}
			
			if(arm != null) {
				alist.remove(arm);
				blist.remove(brm);
			}
			else {
				break;
			}
		}
	}
	
	/* evaluation on arithmetic binary expression [+, -] combination */
	/**
	 * @param expression	{+, -}
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression eval_arith_add_and_sub(SymbolExpression expression) throws Exception {
		/* 1. declarations */
		List<SymbolExpression> post_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> negt_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> pos_variables = new ArrayList<SymbolExpression>(); 
		List<SymbolExpression> neg_variables = new ArrayList<SymbolExpression>(); 
		SymbolConstant lconstant, rconstant, constant;
		SymbolExpression loperand, roperand; CType type = expression.get_data_type();
		
		/* 2. divide operands and evaluations */
		this.div_operands_in_pos_neg(expression, post_operands, negt_operands);
		post_operands = this.eval_operand_list(post_operands);
		negt_operands = this.eval_operand_list(negt_operands);
		
		/* 3. accumulate constants and variables */
		lconstant = this.acc_operands_in_arith_add(post_operands, pos_variables);
		rconstant = this.acc_operands_in_arith_add(negt_operands, neg_variables);
		constant = SymbolComputer.do_compute(COperator.arith_add, lconstant, rconstant);
		this.del_equivalent_operands(pos_variables, neg_variables);
		
		/* 4. generate the basic elements for construction */
		loperand = this.com_operands_in_arith_add(type, pos_variables);
		roperand = this.com_operands_in_arith_add(type, neg_variables);
		return this.construct_arith_add(type, constant, loperand, roperand);
	}
	/**
	 * It divides the [+, -, negt, post] expression into positive (+) and negative (-) operands
	 * @param expression		{positive, negative, arith_add, arith_sub, boolean, otherwise}
	 * @param post_operands		to preserve the operands that should be accounted in add_part
	 * @param negt_operands		to preserve the operands that should be accounted in sub_part
	 * @throws Exception
	 */
	private void div_operands_in_pos_neg(SymbolExpression expression, 
			List<SymbolExpression> post_operands,
			List<SymbolExpression> negt_operands) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException ("Invalid expression: null");
		}
		else if(SymbolFactory.is_bool(expression)) {
			post_operands.add(expression);
		}
		else if(expression instanceof SymbolUnaryExpression) {
			SymbolExpression operand = ((SymbolUnaryExpression) expression).get_operand();
			COperator operator = ((SymbolUnaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.positive) {
				this.div_operands_in_pos_neg(operand, post_operands, negt_operands);
			}
			else if(operator == COperator.negative) {
				this.div_operands_in_pos_neg(operand, negt_operands, post_operands);
			}
			else {
				post_operands.add(expression);
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.arith_add) {
				this.div_operands_in_pos_neg(loperand, post_operands, negt_operands);
				this.div_operands_in_pos_neg(roperand, post_operands, negt_operands);
			}
			else if(operator == COperator.arith_sub) {
				this.div_operands_in_pos_neg(loperand, post_operands, negt_operands);
				this.div_operands_in_pos_neg(roperand, negt_operands, post_operands);
			}
			else {
				post_operands.add(expression);
			}
		}
		else {
			post_operands.add(expression);
		}
	}
	/**
	 * It accumulates the constant in arithmetic addition and set others in variables
	 * @param operands		the operands being accumulated in + context
	 * @param variables		to preserve operands that are not constants
	 * @return				the constant accumulated from input operands
	 * @throws Exception
	 */
	private SymbolConstant acc_operands_in_arith_add(List<SymbolExpression> 
				operands, List<SymbolExpression> variables) throws Exception {
		SymbolConstant constant = SymbolFactory.sym_constant(Integer.valueOf(0));
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				SymbolConstant lconstant = constant;
				SymbolConstant rconstant = (SymbolConstant) operand;
				constant = SymbolComputer.do_compute(COperator.arith_add, lconstant, rconstant);
			}
			else {
				variables.add(operand);
			}
		}
		return constant;
	}
	/**
	 * It combines the operands into arithmetic add summary
	 * @param variables
	 * @return	null if none of variables is given
	 * @throws Exception
	 */
	private SymbolExpression com_operands_in_arith_add(CType type, List<SymbolExpression> variables) throws Exception {
		SymbolExpression result = null;
		for(SymbolExpression variable : variables) {
			if(result == null) {
				result = variable;
			}
			else {
				result = SymbolFactory.arith_add(type, result, variable);
			}
		}
		return result;
	}
	/**
	 * It constructs the arith-add expression from {C + L - R}
	 * @param type
	 * @param constant	the constant part
	 * @param loperand	the left-operand
	 * @param roperand	the right-operand
	 * @return			(type) (C + (L - R))
	 * @throws Exception
	 */
	private SymbolExpression construct_arith_add(CType type, SymbolConstant constant, 
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(loperand == null && roperand == null) {								
			if(SymbolComputer.do_compare(constant, Integer.valueOf(0))) {		/** 0 **/
				return SymbolFactory.sym_constant(Integer.valueOf(0));
			}
			else {																/** C **/
				return constant;
			}
		}
		else if(loperand == null) {
			if(SymbolComputer.do_compare(constant, Integer.valueOf(0))) {		/** -R **/
				return SymbolFactory.arith_neg(roperand);
			}
			else {																/** C - R **/
				return SymbolFactory.arith_sub(type, constant, roperand);
			}
		}
		else if(roperand == null) {
			if(SymbolComputer.do_compare(constant, Integer.valueOf(0))) {		/** L **/
				return loperand;
			}
			else {																/** C + L **/
				return SymbolFactory.arith_add(type, constant, loperand);
			}
		}
		else {
			if(SymbolComputer.do_compare(constant, Integer.valueOf(0))) {		/** L - R **/
				return SymbolFactory.arith_sub(type, loperand, roperand);
			}
			else {																/** C + (L - R) **/
				roperand = SymbolFactory.arith_sub(type, loperand, roperand);
				return SymbolFactory.arith_add(type, constant, roperand);
			}
		}
	}
	
	/* evaluation on arithmetic binary expression [*, /] combination */
	/**
	 * @param expression	{*, /}
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression eval_arith_mul_and_div(SymbolExpression expression) throws Exception {
		/* 1. declarations */
		List<SymbolExpression> did_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> div_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> did_variables = new ArrayList<SymbolExpression>(); 
		List<SymbolExpression> div_variables = new ArrayList<SymbolExpression>(); 
		SymbolConstant lconstant, rconstant;
		SymbolExpression loperand, roperand; CType type = expression.get_data_type();
		
		/* 2. divide the operands and evaluate each of them */
		this.div_operands_in_did_div(expression, did_operands, div_operands);
		did_operands = this.eval_operand_list(did_operands);
		div_operands = this.eval_operand_list(div_operands);
		
		/* 3. accumulate the constants and reduce variables */
		lconstant = this.acc_operands_in_arith_mul(did_operands, did_variables);
		rconstant = this.acc_operands_in_arith_mul(div_operands, div_variables);
		this.del_equivalent_operands(did_variables, div_variables);
		
		/* 4. partial evaluation and reduce constant */
		if(SymbolComputer.do_compare(rconstant, Integer.valueOf(0))) {
			throw new ArithmeticException("Divided by Zero.");
		}
		else if(SymbolComputer.do_compare(lconstant, Integer.valueOf(0))) { 
			return SymbolFactory.sym_constant(Integer.valueOf(0));
		}
		SymbolConstant[] xy = this.std_constants_in_arith_div(lconstant, rconstant);
		lconstant = xy[0]; rconstant = xy[1];
		
		/* 5. combine the left, right operands and expression */
		loperand = this.com_operands_in_arith_mul(type, lconstant, did_variables);
		roperand = this.com_operands_in_arith_mul(type, rconstant, div_variables);
		return this.construct_arith_div(type, loperand, roperand);
	}
	/**
	 * It divides the expression {positive, negative, boolean, arith_mul, arith_div}
	 * @param expression
	 * @param did_operands
	 * @param div_operands
	 * @throws Exception
	 */
	private void div_operands_in_did_div(SymbolExpression expression,
			List<SymbolExpression> did_operands,
			List<SymbolExpression> div_operands) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException ("Invalid expression: null");
		}
		else if(SymbolFactory.is_bool(expression)) {
			did_operands.add(expression);
		}
		else if(expression instanceof SymbolUnaryExpression) {
			SymbolExpression operand = ((SymbolUnaryExpression) expression).get_operand();
			COperator operator = ((SymbolUnaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.positive) {
				this.div_operands_in_did_div(operand, did_operands, div_operands);
			}
			else if(operator == COperator.negative) {
				did_operands.add(SymbolFactory.sym_constant(Integer.valueOf(-1)));
				this.div_operands_in_did_div(operand, did_operands, div_operands);
			}
			else {
				did_operands.add(expression);
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.arith_mul) {
				this.div_operands_in_did_div(loperand, did_operands, div_operands);
				this.div_operands_in_did_div(roperand, did_operands, div_operands);
			}
			else if(operator == COperator.arith_div) {
				this.div_operands_in_did_div(loperand, did_operands, div_operands);
				this.div_operands_in_did_div(roperand, div_operands, did_operands);
			}
			else {
				did_operands.add(expression);
			}
		}
		else {
			did_operands.add(expression);
		}
	}
	/**
	 * It accumulates the constant in multiply and collect non-constant ones
	 * @param operands
	 * @param variables	to preserve operands that are not constants
	 * @return			the constant to accumulate the multiply production
	 * @throws Exception
	 */
	private SymbolConstant acc_operands_in_arith_mul(List<SymbolExpression> 
			operands, List<SymbolExpression> variables) throws Exception {
		SymbolConstant constant = SymbolFactory.sym_constant(Integer.valueOf(1));
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				SymbolConstant lconstant = constant;
				SymbolConstant rconstant = (SymbolConstant) operand;
				constant = SymbolComputer.do_compute(COperator.arith_mul, lconstant, rconstant);
			}
			else {
				variables.add(operand);
			}
		}
		return constant;
	}
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	private long[] gcd_solution(long x, long y) {
		long sign = 1, t;
		if(x * y < 0L) {
			sign = -1;
			x = Math.abs(x);
			y = Math.abs(y);
		}
		
		long a = Math.max(x, y);
		long b = Math.max(x, y);
		while(b != 0) {
			t = b;
			b = a % b;
			a = t;
		}
		x = sign * (x / a);
		y = (y / a);
		return new long[] { x, y };
	}
	/**
	 * It normalizes the constants used in arithmetic division
	 * @param lconstant
	 * @param rconstant
	 * @return
	 * @throws Exception
	 */
	private SymbolConstant[] std_constants_in_arith_div(SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		Object lvalue = lconstant.get_number(), rvalue = rconstant.get_number();
		if(lvalue instanceof Integer) {
			int x = ((Integer) lvalue).intValue();
			if(rvalue instanceof Integer) {
				int y = ((Integer) rvalue).intValue();
				long[] results = this.gcd_solution(x, y);
				return new SymbolConstant[] {
						SymbolFactory.sym_constant(Long.valueOf(results[0])),
						SymbolFactory.sym_constant(Long.valueOf(results[1]))
				};
			}
			else if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				long[] results = this.gcd_solution(x, y);
				return new SymbolConstant[] {
						SymbolFactory.sym_constant(Long.valueOf(results[0])),
						SymbolFactory.sym_constant(Long.valueOf(results[1]))
				};
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return new SymbolConstant[] {
						SymbolFactory.sym_constant(Double.valueOf(x / y)),
						SymbolFactory.sym_constant(Integer.valueOf(1))
				};
			}
		}
		else if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Integer) {
				int y = ((Integer) rvalue).intValue();
				long[] results = this.gcd_solution(x, y);
				return new SymbolConstant[] {
						SymbolFactory.sym_constant(Long.valueOf(results[0])),
						SymbolFactory.sym_constant(Long.valueOf(results[1]))
				};
			}
			else if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				long[] results = this.gcd_solution(x, y);
				return new SymbolConstant[] {
						SymbolFactory.sym_constant(Long.valueOf(results[0])),
						SymbolFactory.sym_constant(Long.valueOf(results[1]))
				};
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return new SymbolConstant[] {
						SymbolFactory.sym_constant(Double.valueOf(x / y)),
						SymbolFactory.sym_constant(Integer.valueOf(1))
				};
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Integer) {
				int y = ((Integer) rvalue).intValue();
				return new SymbolConstant[] {
						SymbolFactory.sym_constant(Double.valueOf(x / y)),
						SymbolFactory.sym_constant(Integer.valueOf(1))
				};
			}
			else if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return new SymbolConstant[] {
						SymbolFactory.sym_constant(Double.valueOf(x / y)),
						SymbolFactory.sym_constant(Integer.valueOf(1))
				};
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return new SymbolConstant[] {
						SymbolFactory.sym_constant(Double.valueOf(x / y)),
						SymbolFactory.sym_constant(Integer.valueOf(1))
				};
			}
		}
	}
	/**
	 * It combines the variables into a multiply production
	 * @param type
	 * @param variables
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression com_operands_in_arith_mul(CType type, SymbolConstant 
			constant, List<SymbolExpression> variables) throws Exception {
		SymbolExpression result = null;
		for(SymbolExpression variable : variables) {
			if(result == null) {
				result = variable;
			}
			else {
				result = SymbolFactory.arith_mul(type, result, variable);
			}
		}
		
		if(result == null) {
			return constant;
		}
		else if(SymbolComputer.do_compare(constant, Integer.valueOf(1))) {
			return result;
		}
		else if(SymbolComputer.do_compare(constant, Integer.valueOf(-1))) {
			return SymbolFactory.arith_neg(result);
		}
		else {
			return SymbolFactory.arith_mul(type, constant, result);
		}
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return 
	 * @throws Exception
	 */
	private SymbolExpression construct_arith_div(CType type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(loperand instanceof SymbolConstant) {
			if(roperand instanceof SymbolConstant) {
				return SymbolComputer.do_compute(COperator.arith_div, 
						(SymbolConstant) loperand, (SymbolConstant) roperand);
			}
			else {
				return SymbolFactory.arith_div(type, loperand, roperand);
			}
		}
		else {
			if(roperand instanceof SymbolConstant) {
				if(SymbolComputer.do_compare((SymbolConstant) roperand, Integer.valueOf(1))) {
					return loperand;
				}
				else if(SymbolComputer.do_compare((SymbolConstant) roperand, Integer.valueOf(-1))) {
					return SymbolFactory.arith_neg(loperand);
				}
				else {
					return SymbolFactory.arith_div(type, loperand, roperand);
				}
			}
			else {
				return SymbolFactory.arith_div(type, loperand, roperand);
			}
		}
	}
	
	
	
	
}
