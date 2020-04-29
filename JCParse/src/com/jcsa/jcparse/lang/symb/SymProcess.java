package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;
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
	
}
