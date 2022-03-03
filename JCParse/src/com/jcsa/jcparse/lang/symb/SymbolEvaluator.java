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
	
	/* evaluation for arithmetic addition (+) and arithmetic subtract (-) */
	/**
	 * It proceeds {arith_add, arith_sub, positive, negative, boolean}
	 * @param expression	the expression, in which the operands are collected
	 * @param pos_operands	to preserve the operands that are taken as positive
	 * @param neg_operands	to preserve the operands that are taken as negative
	 * @throws Exception
	 */
	private void div_operands_in_add_sub(SymbolExpression expression, 
			List<SymbolExpression> pos_operands,
			List<SymbolExpression> neg_operands) throws Exception {
		if(expression == null) { return; }						/* 1. None */
		else if(expression instanceof SymbolUnaryExpression) {	
			SymbolExpression u_operand = ((SymbolUnaryExpression) expression).get_operand();
			COperator operator = ((SymbolUnaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.positive) {				/* 2. post */
				this.div_operands_in_add_sub(u_operand, pos_operands, neg_operands);
			}
			else if(operator == COperator.negative) {			/* 3. negt */
				this.div_operands_in_add_sub(u_operand, neg_operands, pos_operands);
			}
			else {												/* 4. unary */
				pos_operands.add(expression);
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.arith_add) {				/* 5. add */
				this.div_operands_in_add_sub(loperand, pos_operands, neg_operands);
				this.div_operands_in_add_sub(roperand, pos_operands, neg_operands);
			}
			else if(operator == COperator.arith_sub) {			/* 6. sub */
				this.div_operands_in_add_sub(loperand, pos_operands, neg_operands);
				this.div_operands_in_add_sub(roperand, neg_operands, pos_operands);
			}
			else {												/* 7. binary */
				pos_operands.add(expression);
			}
		}
		else {													/* 8. other */
			pos_operands.add(expression);
		}
	}
	/**
	 * @param operands
	 * @param variables	to preserve the set of expressions that are variable
	 * @return			the constant accumulated from input constants in list
	 * @throws Exception
	 */
	private SymbolConstant com_operands_in_add_sub(
			List<SymbolExpression> operands,
			List<SymbolExpression> variables) throws Exception {
		SymbolConstant constant = SymbolFactory.sym_constant(Integer.valueOf(0));
		variables.clear();
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				constant = SymbolComputer.do_compute(COperator.arith_add, constant, (SymbolConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		return constant;
	}
	/**
	 * @param variables
	 * @return null if the variables are empty
	 * @throws Exception
	 */
	private SymbolExpression acc_operands_in_add_sub(CType type, List<SymbolExpression> variables) throws Exception {
		SymbolExpression output = null;
		for(SymbolExpression variable : variables) {
			if(output == null) {
				output = variable;
			}
			else {
				output = SymbolFactory.arith_add(type, output, variable);
			}
		}
		return output;
	}
	/**
	 * @param expression {+, -}
	 * @return 
	 * @throws Exception
	 */
	private SymbolExpression eval_arith_add_and_sub(SymbolExpression expression) throws Exception {
		/* 1. declarations */
		List<SymbolExpression> p_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> n_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> p_variables = new ArrayList<SymbolExpression>();
		List<SymbolExpression> n_variables = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant, constant; 
		SymbolExpression loperand, roperand; CType type = expression.get_data_type();
		
		/* 2. divide expression into operands */
		this.div_operands_in_add_sub(expression, p_operands, n_operands);
		p_operands = this.eval_operand_list(p_operands);
		n_operands = this.eval_operand_list(n_operands);
		
		/* 3. accumulate constant and collect variables */
		lconstant = this.com_operands_in_add_sub(p_operands, p_variables);
		rconstant = this.com_operands_in_add_sub(n_operands, n_variables);
		this.del_equivalent_operands(p_variables, n_variables);
		constant = SymbolComputer.do_compute(COperator.arith_add, lconstant, rconstant);
		
		/* 4. accumulate the variables at left and right */
		loperand = this.acc_operands_in_add_sub(expression.get_data_type(), p_variables);
		roperand = this.acc_operands_in_add_sub(expression.get_data_type(), n_variables);
		
		/* 5. reconstruction algorithm based on syntax */
		if(loperand == null && roperand == null) {				/* constant */
			return constant;
		}
		else if(loperand == null) {
			if(SymbolComputer.do_compare(constant, 0)) {		/* -roperand */
				return SymbolFactory.arith_neg(roperand);
			}
			else {												/* C - roperand */
				return SymbolFactory.arith_sub(type, constant, roperand);
			}
		}
		else if(roperand == null) {
			if(SymbolComputer.do_compare(constant, 0)) {		/* loperand */
				return loperand;
			}
			else {												/* C + loperand */
				return SymbolFactory.arith_add(type, constant, loperand);
			}
		}
		else {
			expression = SymbolFactory.arith_sub(type, loperand, roperand);
			if(!SymbolComputer.do_compare(constant, 0)) {
				return SymbolFactory.arith_add(type, constant, expression);
			}
			else {
				return expression;
			}
		}
	}
	
	/* evaluation for arithmetic multiply (*) and arithmetic division (/) */
	/**
	 * @param expression	the expression in which the operands are collected.
	 * @param did_operands	to preserve the operands in divided part
	 * @param div_operands	to preserve the operands in division part
	 * @throws Exception
	 */
	private void div_operands_in_mul_div(SymbolExpression expression,
			List<SymbolExpression> did_operands,
			List<SymbolExpression> div_operands) throws Exception {
		if(expression == null) { return; }							/* 1. null */
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.arith_mul) {					/* 2. mul */
				this.div_operands_in_mul_div(loperand, did_operands, div_operands);
				this.div_operands_in_mul_div(roperand, did_operands, div_operands);
			}
			else if(operator == COperator.arith_div) {				/* 3. div */
				this.div_operands_in_mul_div(loperand, did_operands, div_operands);
				this.div_operands_in_mul_div(roperand, div_operands, did_operands);
			}
			else {													/* 4. bin */
				did_operands.add(expression);
			}
		}
		else {														/* others */
			did_operands.add(expression);
		}
	}
	/**
	 * @param operands	the list of operands in arithmetic multiply
	 * @param variables	the set of operands that are non-constants
	 * @return			the constant accumulated by const-operands
	 * @throws Exception
	 */
	private SymbolConstant com_operands_in_mul_div(
			List<SymbolExpression> operands,
			List<SymbolExpression> variables) throws Exception {
		SymbolConstant constant = SymbolFactory.sym_constant(Integer.valueOf(1));
		variables.clear();
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				constant = SymbolComputer.do_compute(COperator.
						arith_mul, constant, (SymbolConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		return constant;
	}
	/**
	 * @param type
	 * @param variables
	 * @return x1 * x2 * x3 * ... * xN
	 * @throws Exception
	 */
	private SymbolExpression acc_operands_in_mul_div(CType type, List<SymbolExpression> variables) throws Exception {
		SymbolExpression result = null;
		for(SymbolExpression variable : variables) {
			if(result == null) {
				result = variable;
			}
			else {
				result = SymbolFactory.arith_mul(type, result, variable);
			}
		}
		return result;
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression eval_arith_mul_and_div(SymbolExpression expression) throws Exception {
		/* 1. declarations */
		List<SymbolExpression> did_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> div_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> did_variables = new ArrayList<SymbolExpression>();
		List<SymbolExpression> div_variables = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant, rconstant, constant; 
		SymbolExpression loperand, roperand; CType type = expression.get_data_type();
		
		/* 2. divide the operands and evaluate them */
		this.div_operands_in_mul_div(expression, did_operands, div_operands);
		did_operands = this.eval_operand_list(did_operands);
		div_operands = this.eval_operand_list(div_operands);
		
		/* 3. accumulate constant and collect variables */
		lconstant = this.com_operands_in_mul_div(did_operands, did_variables);
		rconstant = this.com_operands_in_mul_div(div_operands, div_variables);
		if(SymbolComputer.do_compare(lconstant, 0)) { return SymbolFactory.sym_constant(0); }
		
		/* 4. accumulate the variables at left and right */
		loperand = this.acc_operands_in_mul_div(type, did_variables);
		roperand = this.acc_operands_in_mul_div(type, div_variables);
		
		/* 5. construction of TODO */
		return null;
	}
	
	
	
	
	
	
	
}
