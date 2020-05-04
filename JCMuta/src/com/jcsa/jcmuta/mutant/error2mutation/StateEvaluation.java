package com.jcsa.jcmuta.mutant.error2mutation;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymAddress;
import com.jcsa.jcparse.lang.symb.SymConstant;
import com.jcsa.jcparse.lang.symb.SymEvaluator;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;
import com.jcsa.jcparse.lang.symb.SymLiteral;
import com.jcsa.jcparse.lang.symb.SymMultiExpression;
import com.jcsa.jcparse.lang.symb.impl.StandardSymEvaluator;

public class StateEvaluation {
	
	/** it represents the NULL address value **/
	public static final String NullPointer 		= "#NullPtr";
	/** it represents the Invalid address used **/
	public static final String InvalidPointer 	= "#InvAddr";
	/** it represents the address of variable with any integer value **/
	public static final String AnyIntPointer = "#AnyInt";
	
	/** used to construct data type for expressions **/
	private static final CTypeFactory tfactory = new CTypeFactory();
	/** used to simplify the symbolic expressions **/
	private static final SymEvaluator evaluator = StandardSymEvaluator.new_evaluator();
	
	/* constraints getter */
	/**
	 * get the conjunctions (empty)
	 * @return
	 */
	public static StateConstraints get_conjunctions() { return new StateConstraints(true); }
	/**
	 * get the disjunctions (empty)
	 * @return
	 */
	public static StateConstraints get_disjunctions() { return new StateConstraints(false); }
	/**
	 * add a new constraint in the tail of the sequence of conjunctions
	 * @param constraints
	 * @param statement 
	 * @param constraint
	 * @param optimized
	 * @throws Exception
	 */
	public static void add_constraint(StateConstraints constraints, 
			CirStatement statement, SymExpression constraint, boolean optimized) throws Exception {
		constraint = new_condition(constraint);
		if(optimized)
			constraint = evaluator.evaluate(constraint);
		constraints.add_constraint(statement, constraint);
	}
	
	/* constant matters */
	/**
	 * get the symbolic description of expression
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public static SymExpression get_symbol(CirExpression expression) throws Exception {
		return SymFactory.parse(expression);
	}
	/**
	 * get the constant that the instance represents
	 * @param constant
	 * @return boolean or long or double
	 * @throws Exception
	 */
	public static Object get_constant_value(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return constant.get_bool();
		case c_char:
		case c_uchar:
		{
			return Long.valueOf(constant.get_char().charValue());
		}
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
		{
			return Long.valueOf(constant.get_integer().intValue());
		}
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return constant.get_long();
		case c_float:
		{
			return Double.valueOf(constant.get_float().doubleValue());
		}
		case c_double:
		case c_ldouble:	return constant.get_double();
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	/**
	 * get the constant value of expression
	 * @param expression
	 * @return boolean | long | double | string (literal or address) | sym_expression
	 * @throws Exception
	 */
	public static Object get_constant_value(SymExpression expression) throws Exception {
		expression = evaluator.evaluate(expression);
		if(expression instanceof SymConstant) {
			return get_constant_value(((SymConstant) expression).get_constant());
		}
		else if(expression instanceof SymAddress) {
			return ((SymAddress) expression).get_address();
		}
		else if(expression instanceof SymLiteral) {
			return ((SymLiteral) expression).get_literal();
		}
		else {
			return expression;
		}
	}
	/**
	 * get the constant value of expression 
	 * @param expression
	 * @return boolean | long | double | string (literal or address) | sym_expression
	 * @throws Exception
	 */
	public static Object get_constant_value(CirExpression expression) throws Exception {
		return get_constant_value(SymFactory.parse(expression));
	}
	
	/* symbolic matters */
	/**
	 * *(addr) |-- data_type
	 * @param data_type
	 * @param address
	 * @return
	 * @throws Exception
	 */
	public static SymExpression new_variable(CType data_type, String address) throws Exception {
		SymAddress addr = SymFactory.new_address(address, tfactory.get_pointer_type(data_type));
		return SymFactory.new_unary_expression(data_type, COperator.dereference, addr);
	}
	/**
	 * boolean constant
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static SymExpression new_constant(boolean value) throws Exception {
		return SymFactory.new_constant(value);
	}
	/**
	 * integer constant
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static SymExpression new_constant(long value) throws Exception {
		return SymFactory.new_constant(value);
	}
	/**
	 * real constant
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static SymExpression new_constant(double value) throws Exception {
		return SymFactory.new_constant(value);
	}
	/**
	 * {boolean} --> condition or !condition
	 * {integer} --> condition == 0 or condition != 0
	 * {pointer} --> condition == null or condition != null
	 * @param condition
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static SymExpression new_condition(CirExpression condition, boolean value) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(condition.get_data_type());
		if(CTypeAnalyzer.is_boolean(type)) {
			if(!value) {
				return SymFactory.new_unary_expression(CBasicTypeImpl.
						bool_type, COperator.logic_not, SymFactory.parse(condition));
			}
			else {
				return SymFactory.parse(condition);
			}
		}
		else if(CTypeAnalyzer.is_number(type)) {
			if(value) {
				return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
						not_equals, SymFactory.parse(condition), SymFactory.new_constant(0L));
			}
			else {
				return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
						equal_with, SymFactory.parse(condition), SymFactory.new_constant(0L));
			}
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			if(value) {
				return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
						not_equals, SymFactory.parse(condition), SymFactory.new_address(NullPointer, type));
			}
			else {
				return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
						equal_with, SymFactory.parse(condition), SymFactory.new_address(NullPointer, type));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + type);
		}
	}
	/**
	 * translate the symbolic condition as true condition based on its type
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public static SymExpression new_condition(SymExpression condition) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(condition.get_data_type());
		if(CTypeAnalyzer.is_boolean(type)) {
			return condition;
		}
		else if(CTypeAnalyzer.is_number(type)) {
			return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
					not_equals, condition, SymFactory.new_constant(0L));
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
					not_equals, condition, SymFactory.new_address(NullPointer, type));
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + type);
		}
	}
	/**
	 * loperand == roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression equal_with(SymExpression loperand, SymExpression roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.equal_with, loperand, roperand);
	}
	/**
	 * loperand == roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression equal_with(CirExpression loperand, SymExpression roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.equal_with, SymFactory.parse(loperand), roperand);
	}
	/**
	 * loperand == roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression equal_with(CirExpression loperand, CirExpression roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				equal_with, SymFactory.parse(loperand), SymFactory.parse(roperand));
	}
	/**
	 * loperand == roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression equal_with(CirExpression loperand, long roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				equal_with, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand == roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression equal_with(CirExpression loperand, double roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				equal_with, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand == (void *) roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression equal_with(CirExpression loperand, String roperand) throws Exception {
		SymExpression value = new_variable(tfactory.
				get_pointer_type(CBasicTypeImpl.void_type), roperand);
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.equal_with, SymFactory.parse(loperand), value);
	}
	/**
	 * loperand != roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression not_equals(CirExpression loperand, CirExpression roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				not_equals, SymFactory.parse(loperand), SymFactory.parse(roperand));
	}
	/**
	 * loperand != roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression not_equals(SymExpression loperand, SymExpression roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				not_equals, loperand, roperand);
	}
	/**
	 * loperand != roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression not_equals(CirExpression loperand, long roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				not_equals, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand != roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression not_equals(CirExpression loperand, double roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				not_equals, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand == (void *) roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression not_equals(CirExpression loperand, String roperand) throws Exception {
		SymExpression value = new_variable(tfactory.
				get_pointer_type(CBasicTypeImpl.void_type), roperand);
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, SymFactory.parse(loperand), value);
	}
	/**
	 * loperand > roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression greater_tn(CirExpression loperand, long roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				greater_tn, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand > roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression greater_tn(CirExpression loperand, double roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				greater_tn, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand > roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression greater_tn(CirExpression loperand, CirExpression roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				greater_tn, SymFactory.parse(loperand), SymFactory.parse(roperand));
	}
	/**
	 * loperand < roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression smaller_tn(CirExpression loperand, long roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				smaller_tn, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand < roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression smaller_tn(CirExpression loperand, double roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				smaller_tn, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand < roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression smaller_tn(CirExpression loperand, CirExpression roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				smaller_tn, SymFactory.parse(loperand), SymFactory.parse(roperand));
	}
	/**
	 * loperand >= roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression greater_eq(CirExpression loperand, long roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				greater_eq, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand >= roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression greater_eq(CirExpression loperand, double roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				greater_eq, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand >= roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression greater_eq(CirExpression loperand, CirExpression roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				greater_eq, SymFactory.parse(loperand), SymFactory.parse(roperand));
	}
	/**
	 * loperand <= roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression smaller_eq(CirExpression loperand, long roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				smaller_eq, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand <= roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression smaller_eq(CirExpression loperand, double roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				smaller_eq, SymFactory.parse(loperand), SymFactory.new_constant(roperand));
	}
	/**
	 * loperand <= roperand
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression smaller_eq(CirExpression loperand, CirExpression roperand) throws Exception {
		return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, COperator.
				smaller_eq, SymFactory.parse(loperand), SymFactory.parse(roperand));
	}
	/**
	 * create unary expression
	 * @param data_type
	 * @param operator
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression unary_exression(CType data_type, COperator operator, CirExpression operand) throws Exception {
		return SymFactory.new_unary_expression(data_type, operator, SymFactory.parse(operand));
	}
	/**
	 * create unary expression
	 * @param data_type
	 * @param operator
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression unary_exression(CType data_type, COperator operator, SymExpression operand) throws Exception {
		return SymFactory.new_unary_expression(data_type, operator, operand);
	}
	/**
	 * 
	 * @param data_type
	 * @param operator
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression binary_expression(CType data_type, COperator operator, SymExpression loperand, SymExpression roperand) throws Exception {
		switch(operator) {
		case arith_add:
		case arith_mul:
		case bit_and:
		case bit_or:
		case bit_xor:
		case logic_and:
		case logic_or:
		{
			SymMultiExpression expr = SymFactory.new_multiple_expression(data_type, operator);
			expr.add_operand(loperand); expr.add_operand(roperand); return expr;
		}
		default:
		{
			return SymFactory.new_binary_expression(data_type, operator, loperand, roperand);
		}
		}
	}
	/**
	 * x + y
	 * @param data_type
	 * @param operator
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression binary_expression(CType data_type, COperator operator, CirExpression loperand, CirExpression roperand) throws Exception {
		return binary_expression(data_type, operator, SymFactory.parse(loperand), SymFactory.parse(roperand));
	}
	/**
	 * x * anyInt
	 * @param data_type
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	public static SymExpression multiply_expression(CType data_type, CirExpression operand) throws Exception {
		SymExpression loperand = SymFactory.parse(operand);
		SymExpression address = SymFactory.new_address(AnyIntPointer, 
				tfactory.get_pointer_type(CBasicTypeImpl.int_type));
		SymExpression roperand = SymFactory.new_unary_expression(
				CBasicTypeImpl.long_type, COperator.dereference, address);
		
		SymMultiExpression result = SymFactory.
				new_multiple_expression(data_type, COperator.arith_mul);
		result.add_operand(loperand); result.add_operand(roperand); 
		return result;
	}
	/**
	 * x * value
	 * @param data_type
	 * @param operand
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static SymExpression multiply_expression(CType data_type, CirExpression operand, long value) throws Exception {
		SymExpression loperand = SymFactory.parse(operand);
		SymExpression roperand = SymFactory.new_constant(value);
		
		SymMultiExpression result = SymFactory.
				new_multiple_expression(data_type, COperator.arith_mul);
		result.add_operand(loperand); result.add_operand(roperand); 
		return result;
	}
	/**
	 * x * value
	 * @param data_type
	 * @param operand
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static SymExpression multiply_expression(CType data_type, CirExpression operand, double value) throws Exception {
		SymExpression loperand = SymFactory.parse(operand);
		SymExpression roperand = SymFactory.new_constant(value);
		
		SymMultiExpression result = SymFactory.
				new_multiple_expression(data_type, COperator.arith_mul);
		result.add_operand(loperand); result.add_operand(roperand); 
		return result;
	}
	
}
