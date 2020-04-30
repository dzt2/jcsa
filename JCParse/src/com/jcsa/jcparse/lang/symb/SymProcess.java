package com.jcsa.jcparse.lang.symb;

import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * Used to create symbolic node
 * @author yukimula
 *
 */
public class SymProcess {
	
	private static final CTypeFactory tfactory = new CTypeFactory();
	
	/* unit creator */
	/**
	 * each address refers to a variable of which address is determined in compiler-time
	 * @param identifier
	 * @param data_type
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymAddress new_address(String identifier, CType data_type) throws IllegalArgumentException {
		return new SymAddress(data_type, identifier);
	}
	/**
	 * create a new constant expression
	 * @param constant
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymConstant new_constant(CConstant constant) throws IllegalArgumentException {
		return new SymConstant(constant);
	}
	/**
	 * create a string literal node
	 * @param data_type
	 * @param literal
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymLiteral new_literal(String literal) throws Exception {
		return new SymLiteral(tfactory.get_array_type(CBasicTypeImpl.char_type, literal.length() + 1), literal);
	}
	/**
	 * default value contains unknown value
	 * @param data_type
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymDefaultValue new_default_value(CType data_type) throws IllegalArgumentException {
		return new SymDefaultValue(data_type);
	}
	/**
	 * unary expression contains one operator and one operand
	 * @param data_type
	 * @param operator {pos, neg, bit_not, log_not, addr_of, de_refer, assign}
	 * @param operand
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymUnaryExpression new_unary_expression(CType data_type, 
			COperator operator, SymExpression operand) throws IllegalArgumentException {
		return new SymUnaryExpression(data_type, operator, operand);
	}
	/**
	 * binary expression connects two operands with one operator
	 * @param data_type
	 * @param operator {+, -, *, /, %, &, |, ^, <<, >>, <=, <, >=, >, ==, !=, &&, ||}
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymBinaryExpression new_binary_expression(CType data_type, COperator operator, 
			SymExpression loperand, SymExpression roperand) throws IllegalArgumentException {
		return new SymBinaryExpression(data_type, operator, loperand, roperand);
	}
	/**
	 * multiple expression with operands of various size
	 * @param data_type
	 * @param operator {+, *, &, |, ^, &&, ||}
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymMultiExpression new_multiple_expression(
			CType data_type, COperator operator) throws IllegalArgumentException {
		return new SymMultiExpression(data_type, operator);
	}
	/**
	 * body.field
	 * @param data_type
	 * @param body
	 * @param field
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymFieldExpression new_field_expression(CType data_type, 
			SymExpression body, String field) throws IllegalArgumentException {
		return new SymFieldExpression(data_type, body, field);
	}
	/**
	 * function argument_list
	 * @param data_type
	 * @param function
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymInvocateExpression new_invocate_expression(
			CType data_type, SymExpression function) throws IllegalArgumentException {
		return new SymInvocateExpression(data_type, function);
	}
	/**
	 * empty sequence without elements
	 * @return
	 */
	public static SymSequenceExpression new_sequence_expression() {
		return new SymSequenceExpression();
	}
	
	/* constant generator */
	/**
	 * create a constant as boolean value
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymConstant new_constant(boolean value) throws IllegalArgumentException {
		CConstant constant = new CConstant();
		constant.set_bool(value);
		return new SymConstant(constant);
	}
	/**
	 * create a constant as charater
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymConstant new_constant(char value) throws IllegalArgumentException {
		CConstant constant = new CConstant();
		constant.set_char(value);
		return new SymConstant(constant);
	}
	/**
	 * create a constant as integer
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymConstant new_constant(int value) throws IllegalArgumentException {
		CConstant constant = new CConstant();
		constant.set_int(value);
		return new SymConstant(constant);
	}
	/**
	 * create a constant as long integer
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymConstant new_constant(long value) throws IllegalArgumentException {
		CConstant constant = new CConstant();
		constant.set_long(value);
		return new SymConstant(constant);
	}
	/**
	 * create a constant as floating value
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymConstant new_constant(float value) throws IllegalArgumentException {
		CConstant constant = new CConstant();
		constant.set_float(value);
		return new SymConstant(constant);
	}
	/**
	 * create a constant as double
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SymConstant new_constant(double value) throws IllegalArgumentException {
		CConstant constant = new CConstant();
		constant.set_double(value);
		return new SymConstant(constant);
	}
	
	/* parse from CIR expression */
	/**
	 * create a symblic expression from the CIR source node
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static SymExpression parse(CirNode source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof CirNameExpression) {
			SymAddress addr = new SymAddress(tfactory.get_pointer_type(
					((CirNameExpression) source).get_data_type()), 
					((CirNameExpression) source).get_unique_name());
			return new SymUnaryExpression((
					(CirNameExpression) source).get_data_type(),
					COperator.dereference, addr);
		}
		else if(source instanceof CirDeferExpression) {
			SymExpression operand = parse(((CirDeferExpression) source).get_address());
			return new SymUnaryExpression(((CirDeferExpression) source).get_data_type(),
					COperator.dereference, operand);
		}
		else if(source instanceof CirFieldExpression) {
			SymExpression body = parse(((CirFieldExpression) source).get_body());
			return new SymFieldExpression(
					((CirFieldExpression) source).get_data_type(), body, 
					((CirFieldExpression) source).get_field().get_name());
		}
		else if(source instanceof CirAddressExpression) {
			SymExpression operand = parse(((CirAddressExpression) source).get_operand());
			return new SymUnaryExpression(((CirAddressExpression) source).get_data_type(),
					COperator.address_of, operand);
		}
		else if(source instanceof CirCastExpression) {
			SymExpression operand = parse(((CirCastExpression) source).get_operand());
			return new SymUnaryExpression(((CirCastExpression) source).get_data_type(),
					COperator.assign, operand);
		}
		else if(source instanceof CirConstExpression) {
			return new SymConstant(((CirConstExpression) source).get_constant());
		}
		else if(source instanceof CirDefaultValue) {
			return new SymDefaultValue(((CirDefaultValue) source).get_data_type());
		}
		else if(source instanceof CirStringLiteral) {
			String literal = ((CirStringLiteral) source).get_literal();
			return new SymLiteral(tfactory.get_array_type(CBasicTypeImpl.char_type, literal.length() + 1), literal);
		}
		else if(source instanceof CirInitializerBody) {
			SymSequenceExpression expr = new SymSequenceExpression();
			for(int k = 0; k < source.number_of_children(); k++) {
				expr.add_element(parse(((CirInitializerBody) source).get_element(k)));
			}
			return expr;
		}
		else if(source instanceof CirComputeExpression) {
			CirComputeExpression expression = (CirComputeExpression) source;
			switch(expression.get_operator()) {
			case positive:
			case negative:
			case logic_not:
			case bit_not:	
			{
				SymExpression operand = parse(expression.get_operand(0));
				return new SymUnaryExpression(expression.get_data_type(), expression.get_operator(), operand);
			}
			case arith_add:
			case arith_mul:
			case bit_and:
			case bit_or:
			case bit_xor:
			case logic_and:
			case logic_or:
			{
				SymMultiExpression result = new SymMultiExpression(
						expression.get_data_type(), expression.get_operator());
				for(int k = 0; k < expression.number_of_operand(); k++) {
					SymExpression operand = parse(expression.get_operand(k));
					result.add_operand(operand);
				}
				return result;
			}
			case arith_sub:
			case arith_div:
			case arith_mod:
			case left_shift:
			case righ_shift:
			case greater_tn:
			case greater_eq:
			case smaller_tn:
			case smaller_eq:
			case equal_with:
			case not_equals:
			{
				SymExpression loperand = parse(expression.get_operand(0));
				SymExpression roperand = parse(expression.get_operand(1));
				return new SymBinaryExpression(expression.get_data_type(), 
						expression.get_operator(), loperand, roperand);
			}
			default: throw new IllegalArgumentException("Unsupport: " + expression.get_operator());
			}
		}
		else if(source instanceof CirWaitExpression) {
			CirTree tree = source.get_tree();
			CirFunction function = tree.get_function_call_graph().get_function(source);
			CirExecution wait_execution = function.get_flow_graph().
							get_execution(((CirWaitExpression) source).statement_of());
			CirExecution call_execution = 
					function.get_flow_graph().get_execution(wait_execution.get_id() - 1);
			CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
			
			SymExpression fexpr = parse(call_statement.get_function());
			SymInvocateExpression result = new SymInvocateExpression(
					((CirWaitExpression) source).get_data_type(), fexpr);
			
			CirArgumentList arguments = call_statement.get_arguments();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				SymExpression argument = parse(arguments.get_argument(k));
				result.get_argument_list().add_argument(argument);
			}
			return result;
		}
		else throw new IllegalArgumentException("Unsupport " + source);
	}
	
	/* standard symbolic evaluations */
	/**
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private static SymExpression get_answer(SymExpression expression, Map<String, Object> context) throws Exception {
		Object value = context.get(expression.toString());
		if(value instanceof Boolean) {
			return new_constant(((Boolean) value).booleanValue());
		}
		else if(value instanceof Character) {
			long lvalue = ((Character) value).charValue();
			return new_constant(lvalue);
		}
		else if(value instanceof Short) {
			long lvalue = ((Short) value).shortValue();
			return new_constant(lvalue);
		}
		else if(value instanceof Integer) {
			long lvalue = ((Integer) value).intValue();
			return new_constant(lvalue);
		}
		else if(value instanceof Long) {
			return new_constant(((Long) value).longValue());
		}
		else if(value instanceof Float) {
			double lvalue = ((Float) value).floatValue();
			return new_constant(lvalue);
		}
		else if(value instanceof Double) {
			return new_constant(((Double) value).doubleValue());
		}
		else if(value instanceof SymExpression) {
			return evaluate((SymExpression) value, context);
		}
		else {
			throw new IllegalArgumentException("Invalid solution: " + value);
		}
	}
	/**
	 * perform standard evaluation on symbolic expression(s)
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static SymExpression evaluate(SymExpression expression, Map<String, Object> context) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(context != null && context.containsKey(expression.toString())) 
			return get_answer(expression, context);
		else if(expression instanceof SymAddress)
			return evaluate_address((SymAddress) expression, context);
		else if(expression instanceof SymConstant)
			return evaluate_constant((SymConstant) expression, context);
		else if(expression instanceof SymLiteral)
			return evaluate_literal((SymLiteral) expression, context);
		else if(expression instanceof SymDefaultValue)
			return evaluate_default_value((SymDefaultValue) expression, context);
		else if(expression instanceof SymFieldExpression)
			return evaluate_field_expression((SymFieldExpression) expression, context);
		else if(expression instanceof SymInvocateExpression)
			return evaluate_invoc_expression((SymInvocateExpression) expression, context);
		else if(expression instanceof SymSequenceExpression)
			return evaluate_sequence((SymSequenceExpression) expression, context);
		else if(expression instanceof SymUnaryExpression) {
			switch(((SymUnaryExpression) expression).get_operator()) {
			case positive:		
				return evaluate_positive_expression((SymUnaryExpression) expression, context);
			case negative:
				return evaluate_negative_expression((SymUnaryExpression) expression, context);
			case logic_not:
				return evaluate_logic_not_expression((SymUnaryExpression) expression, context);
			case bit_not:
				return evaluate_bit_not_expression((SymUnaryExpression) expression, context);
			case address_of:
				return evaluate_address_of_expression((SymUnaryExpression) expression, context);
			case dereference:
				return evaluate_dereference_expression((SymUnaryExpression) expression, context);
			case assign: return evaluate_cast_expression((SymUnaryExpression) expression, context);
			default: throw new IllegalArgumentException("Invalid: " + expression);
			}
		}
		/* TODO implement the expression categories */
		else throw new IllegalArgumentException("Unsupport: " + expression);
	}
	
	/* basic expression evaluations */
	/**
	 * 
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_address(SymAddress expression, Map<String, Object> context) throws Exception {
		return (SymExpression) expression.copy();
	}
	/**
	 * constant --> bool | long | double
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_constant(SymConstant expression, Map<String, Object> context) throws Exception {
		CConstant constant = expression.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:		return new_constant(constant.get_bool().booleanValue());
		case c_char:
		case c_uchar:		return new_constant((long) constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return new_constant((long) constant.get_integer().intValue());
		case c_long:
		case c_llong:
		case c_ulong:
		case c_ullong:		return new_constant(constant.get_long().longValue());
		case c_float:		return new_constant((double) constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return new_constant(constant.get_double().doubleValue());
		default: throw new IllegalArgumentException("Invalid constant: " + constant.toString());
		}
	}
	/**
	 * literal ==> literal
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_literal(SymLiteral expression, Map<String, Object> context) throws Exception {
		return (SymExpression) expression.copy();
	}
	/**
	 * default_value ==> copy
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_default_value(SymDefaultValue expression, Map<String, Object> context) throws Exception {
		return (SymExpression) expression.copy();
	}
	/* special expression evaluation */
	/**
	 * expr.field
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_field_expression(SymFieldExpression expression, Map<String, Object> context) throws Exception {
		SymExpression body = evaluate(expression.get_body(), context);
		String field_name = expression.get_field().get_name();
		return new SymFieldExpression(expression.get_data_type(), body, field_name);
	}
	/**
	 * expr(e1, e2, ..., en)
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_invoc_expression(SymInvocateExpression expression, Map<String, Object> context) throws Exception {
		SymExpression fexpr = evaluate(expression.get_function(), context);
		SymInvocateExpression result = new 
					SymInvocateExpression(expression.get_data_type(), fexpr);
		
		SymArgumentList arguments = expression.get_argument_list();
		for(int k = 0; k < arguments.number_of_arguments(); k++) {
			SymExpression argument = evaluate(arguments.get_argument(k), context);
			result.get_argument_list().add_argument(argument);
		}
		
		return result;
	}
	/**
	 * 
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_sequence(SymSequenceExpression expression, Map<String, Object> context) throws Exception {
		SymSequenceExpression result = new SymSequenceExpression();
		
		for(int k = 0; k < expression.number_of_elements(); k++) {
			SymExpression element = evaluate(expression.get_element(k), context);
			result.add_element(element);
		}
		
		return result;
	}
	
	/* unary expression */
	/**
	 * 
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_positive_expression(SymUnaryExpression expression, Map<String, Object> context) throws Exception {
		return evaluate(expression.get_operand(), context);
	}
	/**
	 * expr ==> -1 * expr
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_negative_expression(SymUnaryExpression expression, Map<String, Object> context) throws Exception {
		SymExpression operand = evaluate(expression.get_operand(), context);
		if(operand instanceof SymConstant) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:	
				if(constant.get_bool().booleanValue())
					return new_constant(-1L);
				else return new_constant(0L);
			case c_long:
				return new_constant(-constant.get_long().longValue());
			case c_double:
				return new_constant(-constant.get_double().doubleValue());
			default: throw new IllegalArgumentException("Invalid constant: " + constant);
			}
		}
		else {
			SymConstant loperand = new_constant((long) -1);
			SymMultiExpression result = new SymMultiExpression(
					expression.get_data_type(), COperator.arith_mul);
			result.add_operand(loperand); result.add_operand(operand); 
			return result;
		}
	}
	/**
	 * expr ==> !expr
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_logic_not_expression(SymUnaryExpression expression, Map<String, Object> context) throws Exception {
		SymExpression operand = evaluate(expression.get_operand(), context);
		if(operand instanceof SymConstant) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:	return new_constant(!constant.get_bool().booleanValue());
			case c_long:	return new_constant(constant.get_long().longValue() == 0L);
			case c_double:	return new_constant(constant.get_double().doubleValue() == 0.0);
			default: throw new IllegalArgumentException("Invalid type");
			}
		}
		else if(operand instanceof SymUnaryExpression) {
			if(((SymUnaryExpression) operand).get_operator() == COperator.logic_not)
				return (SymExpression) ((SymUnaryExpression) operand).get_operand().copy();
			else 
				return new SymUnaryExpression(CBasicTypeImpl.bool_type, COperator.logic_not, operand);
		}
		else {
			return new SymUnaryExpression(CBasicTypeImpl.bool_type, COperator.logic_not, operand);
		}
	}
	/**
	 * expr ==> ~expr
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_bit_not_expression(SymUnaryExpression expression, Map<String, Object> context) throws Exception {
		SymExpression operand = evaluate(expression.get_operand(), context);
		if(operand instanceof SymConstant) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
				if(constant.get_bool().booleanValue())
						return new_constant(~1L);
				else 	return new_constant(~0L);
			case c_long:
				return new_constant(~constant.get_long().longValue());
			default: throw new IllegalArgumentException("Invalid constant: " + constant);
			}
		}
		else if(operand instanceof SymUnaryExpression) {
			if(((SymUnaryExpression) operand).get_operator() == COperator.bit_not)
				return (SymExpression) ((SymUnaryExpression) operand).get_operand().copy();
			else 
				return new SymUnaryExpression(expression.get_data_type(), COperator.bit_not, operand);
		}
		else {
			return new SymUnaryExpression(expression.get_data_type(), COperator.bit_not, operand);
		}
	}
	/**
	 * expr ==> &expr
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_address_of_expression(SymUnaryExpression expression, Map<String, Object> context) throws Exception {
		SymExpression operand = evaluate(expression.get_operand(), context);
		if(operand instanceof SymUnaryExpression) {
			if(((SymUnaryExpression) operand).get_operator() == COperator.dereference)
				return (SymExpression) ((SymUnaryExpression) operand).get_operand().copy();
			else 
				return new SymUnaryExpression(expression.get_data_type(), COperator.address_of, operand);
		}
		else {
			return new SymUnaryExpression(expression.get_data_type(), COperator.address_of, operand);
		}
	}
	/**
	 * expr ==> *expr
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_dereference_expression(SymUnaryExpression expression, Map<String, Object> context) throws Exception {
		SymExpression operand = evaluate(expression.get_operand(), context);
		if(operand instanceof SymUnaryExpression) {
			if(((SymUnaryExpression) operand).get_operator() == COperator.address_of)
				return (SymExpression) ((SymUnaryExpression) operand).get_operand().copy();
			else 
				return new SymUnaryExpression(expression.get_data_type(), COperator.dereference, operand);
		}
		else {
			return new SymUnaryExpression(expression.get_data_type(), COperator.dereference, operand);
		}
	}
	/**
	 * 
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymExpression evaluate_cast_expression(SymUnaryExpression expression, Map<String, Object> context) throws Exception {
		SymExpression operand = evaluate(expression.get_operand(), context);
		if(operand instanceof SymConstant) {
			CConstant constant = ((SymConstant) operand).get_constant();
			CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				if(CTypeAnalyzer.is_boolean(data_type)) {
					return new_constant(constant.get_bool().booleanValue());
				}
				else if(CTypeAnalyzer.is_integer(data_type)) {
					if(constant.get_bool().booleanValue())
						return new_constant(1L);
					else return new_constant(0L);
				}
				else if(CTypeAnalyzer.is_real(data_type)) {
					if(constant.get_bool().booleanValue())
						return new_constant(1.0);
					else return new_constant(0.0);
				}
				else {
					throw new IllegalArgumentException("Invalid type: " + data_type);
				}
			}
			case c_long:
			{
				if(CTypeAnalyzer.is_boolean(data_type)) {
					return new_constant(constant.get_long().longValue() != 0L);
				}
				else if(CTypeAnalyzer.is_integer(data_type)) {
					return new_constant(constant.get_long().longValue());
				}
				else if(CTypeAnalyzer.is_real(data_type)) {
					return new_constant(constant.get_long().doubleValue());
				}
				else if(CTypeAnalyzer.is_pointer(data_type)) {
					return new_constant(constant.get_long().longValue());
				}
				else {
					throw new IllegalArgumentException("Invalid type: " + data_type);
				}
			}
			case c_double:
			{
				if(CTypeAnalyzer.is_boolean(data_type)) {
					return new_constant(constant.get_double().doubleValue() != 0.0);
				}
				else if(CTypeAnalyzer.is_integer(data_type)) {
					return new_constant((long) constant.get_double().longValue());
				}
				else if(CTypeAnalyzer.is_real(data_type)) {
					return new_constant(constant.get_double().doubleValue());
				}
				else {
					throw new IllegalArgumentException("Invalid type: " + data_type);
				}
			}
			default: throw new IllegalArgumentException("Invalid: " + constant);
			}
		}
		else {
			return new SymUnaryExpression(expression.get_data_type(), COperator.assign, operand);
		}
	}
	
	/* binary expression */ 
	
}
