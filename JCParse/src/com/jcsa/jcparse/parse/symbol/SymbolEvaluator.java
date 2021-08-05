package com.jcsa.jcparse.parse.symbol;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolArgumentList;
import com.jcsa.jcparse.lang.symbol.SymbolBasicExpression;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolCallExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolFieldExpression;
import com.jcsa.jcparse.lang.symbol.SymbolInitializerList;
import com.jcsa.jcparse.lang.symbol.SymbolUnaryExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It implements the evaluation module for simplifying or computing symbolic expression based on
 * contextual information provided from memory of symbolic process or none to simplify expression
 * @author yukimula
 *
 */
public class SymbolEvaluator {

	/* definitions */
	/** the context in which the evaluation was performed **/
	private SymbolProcess symbol_process;
	/** the computational unit for simplifying expression  **/
	protected SymbolComputer computer_unit;
	/**
	 * construct an empty non-initialized evaluator
	 */
	public SymbolEvaluator() {
		this.set_symbol_process(null);
		this.computer_unit = new SymbolComputer(this);
	}

	/* parameters */
	/**
	 * @return the context in which the evaluation was performed
	 */
	public SymbolProcess get_symbol_process() { return this.symbol_process; }
	/**
	 * @return the factory used to construct symbolic expression
	 */
	public SymbolFactory get_symbol_factory() {
		if(this.symbol_process == null) {
			return SymbolFactory.factory;
		}
		else {
			return this.symbol_process.get_symbol_factory();
		}
	}
	/**
	 * @param process context to be established for evaluation
	 */
	public void set_symbol_process(SymbolProcess process) { this.symbol_process = process; }

	/* evaluation methods */
	/**
	 * @param expression
	 * @return whether there is solution to replace the expression
	 * @throws Exception
	 */
	private boolean has_solution(SymbolExpression expression) throws Exception {
		if(this.symbol_process == null) {
			return false;
		}
		else {
			return this.symbol_process.get_data_stack().load(expression) != null;
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression get_solution(SymbolExpression expression) throws Exception {
		if(this.symbol_process == null) {
			return null;
		}
		else {
			return this.symbol_process.get_data_stack().load(expression);
		}
	}
	/**
	 * @param expression to be evaluated
	 * @param loaded whether to load value to replace
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression recur_eval(SymbolExpression expression, boolean loaded) throws Exception {
		SymbolExpression result;
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression as null");
		}
		else if(loaded && this.has_solution(expression)) {
			return this.get_solution(expression);
		}
		else if(expression instanceof SymbolBasicExpression) {
			result = this.computer_unit.compute_basic(expression);
		}
		else if(expression instanceof SymbolInitializerList) {
			SymbolInitializerList ilist = (SymbolInitializerList) expression;
			List<SymbolExpression> elements = new ArrayList<>();
			for(int k = 0; k < ilist.number_of_elements(); k++) {
				elements.add(this.recur_eval(ilist.get_element(k), loaded));
			}
			result = this.computer_unit.compute_initializer_list(elements);
		}
		else if(expression instanceof SymbolFieldExpression) {
			SymbolExpression body = this.recur_eval(((SymbolFieldExpression) expression).get_body(), loaded);
			result = this.computer_unit.compute_field_expression(body, ((SymbolFieldExpression) expression).get_field().get_name());
		}
		else if(expression instanceof SymbolCallExpression) {
			SymbolExpression function = this.recur_eval(((SymbolCallExpression) expression).get_function(), loaded);
			SymbolArgumentList arguments = ((SymbolCallExpression) expression).get_argument_list();
			List<SymbolExpression> alist = new ArrayList<>();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				alist.add(this.recur_eval(arguments.get_argument(k), loaded));
			}
			result = this.computer_unit.compute_call_expression(function, alist);
		}
		else if(expression instanceof SymbolUnaryExpression) {
			SymbolExpression operand = this.recur_eval(((SymbolUnaryExpression) expression).get_operand(), loaded);
			COperator operator = ((SymbolUnaryExpression) expression).get_operator().get_operator();
			switch(operator) {
			case negative:		result = this.computer_unit.compute_arith_neg(operand);		break;
			case bit_not:		result = this.computer_unit.compute_bitws_rsv(operand);		break;
			case logic_not:		result = this.computer_unit.compute_logic_not(operand);		break;
			case address_of:	result = this.computer_unit.compute_address_of(operand);	break;
			case dereference:	result = this.computer_unit.compute_dereference(operand);	break;
			case assign:		result = this.computer_unit.compute_type_cast(expression.get_data_type(), operand);	break;
			default: 			throw new IllegalArgumentException(operator.toString());
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			loperand = this.recur_eval(loperand, loaded);
			roperand = this.recur_eval(roperand, loaded);
			CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			switch(operator) {
			case arith_add:		result = this.computer_unit.compute_arith_add(data_type, loperand, roperand);	break;
			case arith_sub:		result = this.computer_unit.compute_arith_sub(data_type, loperand, roperand);	break;
			case arith_mul:		result = this.computer_unit.compute_arith_mul(data_type, loperand, roperand);	break;
			case arith_div:		result = this.computer_unit.compute_arith_div(data_type, loperand, roperand);	break;
			case arith_mod:		result = this.computer_unit.compute_arith_mod(data_type, loperand, roperand);	break;
			case bit_and:		result = this.computer_unit.compute_bitws_and(data_type, loperand, roperand);	break;
			case bit_or:		result = this.computer_unit.compute_bitws_ior(data_type, loperand, roperand);	break;
			case bit_xor:		result = this.computer_unit.compute_bitws_xor(data_type, loperand, roperand);	break;
			case left_shift:	result = this.computer_unit.compute_bitws_lsh(data_type, loperand, roperand);	break;
			case righ_shift:	result = this.computer_unit.compute_bitws_rsh(data_type, loperand, roperand);	break;
			case logic_and:		result = this.computer_unit.compute_logic_and(loperand, roperand);				break;
			case logic_or:		result = this.computer_unit.compute_logic_ior(loperand, roperand);				break;
			case smaller_tn:	result = this.computer_unit.compute_smaller_tn(loperand, roperand);				break;
			case smaller_eq:	result = this.computer_unit.compute_smaller_eq(loperand, roperand);				break;
			case greater_tn:	result = this.computer_unit.compute_smaller_tn(roperand, loperand);				break;
			case greater_eq:	result = this.computer_unit.compute_smaller_eq(roperand, loperand);				break;
			case equal_with:	result = this.computer_unit.compute_equal_with(loperand, roperand);				break;
			case not_equals:	result = this.computer_unit.compute_not_equals(loperand, roperand);				break;
			default: 			throw new IllegalArgumentException("Invalid operator: " + operator.toString());
			}
		}
		else {
			throw new IllegalArgumentException(expression.getClass().getSimpleName());
		}

		if(loaded && this.has_solution(result)) {
			result = this.get_solution(result);
		}
		return result;
	}
	/**
	 * @param expression
	 * @param loaded whether to use state to replace
	 * @return
	 * @throws Exception
	 */
	public SymbolExpression evaluate(SymbolExpression expression, boolean loaded) throws Exception {
		return this.recur_eval(expression, loaded);
	}

	/* static interfaces */
	private static final SymbolEvaluator evaluator = new SymbolEvaluator();
	/**
	 * @param expression
	 * @param process
	 * @param loaded whether to load memory value to replace
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression evaluate_on(SymbolExpression expression, SymbolProcess process, boolean loaded) throws Exception {
		evaluator.set_symbol_process(process);
		return evaluator.evaluate(expression, loaded);
	}
	/**
	 * @param expression
	 * @param process
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression evaluate_on(SymbolExpression expression, SymbolProcess process) throws Exception {
		evaluator.set_symbol_process(process);
		return evaluator.evaluate(expression, true);
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression evaluate_on(SymbolExpression expression) throws Exception {
		evaluator.set_symbol_process(null);
		return evaluator.evaluate(expression, false);
	}

}
