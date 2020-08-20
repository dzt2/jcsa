package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * The factory to create symbolic node.
 * 
 * @author yukimula
 *
 */
public class SymFactory {
	
	private static final CTypeFactory tfactory = new CTypeFactory();
	
	/**
	 * @param data_type
	 * @param name
	 * @return identifier |-- {name}
	 * @throws Exception
	 */
	public static SymIdentifier new_identifier(CType data_type, String name) throws Exception {
		return new SymIdentifier(data_type, name);
	}
	/**
	 * @param value
	 * @return constant |-- {boolean}
	 * @throws Exception
	 */
	public static SymConstant new_constant(boolean value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_bool(value);
		return new SymConstant(constant.get_type(), constant);
	}
	/**
	 * @param value
	 * @return constant |-- {char}
	 * @throws Exception
	 */
	public static SymConstant new_constant(char value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_char(value);;
		return new SymConstant(constant.get_type(), constant);
	}
	/**
	 * @param value
	 * @return constant |-- {int}
	 * @throws Exception
	 */
	public static SymConstant new_constant(int value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_int(value);;
		return new SymConstant(constant.get_type(), constant);
	}
	/**
	 * @param value
	 * @return constant |-- {boolean}
	 * @throws Exception
	 */
	public static SymConstant new_constant(long value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_long(value);;
		return new SymConstant(constant.get_type(), constant);
	}
	/**
	 * @param value
	 * @return constant |-- {boolean}
	 * @throws Exception
	 */
	public static SymConstant new_constant(float value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_float(value);;
		return new SymConstant(constant.get_type(), constant);
	}
	/**
	 * @param value
	 * @return constant |-- {boolean}
	 * @throws Exception
	 */
	public static SymConstant new_constant(double value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_double(value);;
		return new SymConstant(constant.get_type(), constant);
	}
	/**
	 * @param literal
	 * @return literal |-- {String}
	 * @throws Exception
	 */
	public static SymLiteral new_literal(String literal) throws Exception {
		return new SymLiteral(tfactory.get_array_type(
				CBasicTypeImpl.char_type, literal.length() + 1), literal);
	}
	/**
	 * @param data_type
	 * @param operand
	 * @return -operand
	 * @throws Exception
	 */
	public static SymUnaryExpression new_arith_neg(CType data_type, SymExpression operand) throws Exception {
		SymUnaryExpression expression = 
				new SymUnaryExpression(data_type, COperator.negative);
		expression.add_child(operand); return expression;
	}
	/**
	 * @param data_type
	 * @param operand
	 * @return ~operand
	 * @throws Exception
	 */
	public static SymUnaryExpression new_bitws_rsv(CType data_type, SymExpression operand) throws Exception {
		SymUnaryExpression expression = 
				new SymUnaryExpression(data_type, COperator.bit_not);
		expression.add_child(operand); return expression;
	}
	/**
	 * @param data_type
	 * @param operand
	 * @return !operand
	 * @throws Exception
	 */
	public static SymUnaryExpression new_logic_not(CType data_type, SymExpression operand) throws Exception {
		SymUnaryExpression expression = 
				new SymUnaryExpression(data_type, COperator.logic_not);
		expression.add_child(operand); return expression;
	}
	/**
	 * @param data_type
	 * @param operand
	 * @return &operand
	 * @throws Exception
	 */
	public static SymUnaryExpression new_address_of(CType data_type, SymExpression operand) throws Exception {
		SymUnaryExpression expression = 
				new SymUnaryExpression(data_type, COperator.address_of);
		expression.add_child(operand); return expression;
	}
	/**
	 * @param data_type
	 * @param operand
	 * @return *operand
	 * @throws Exception
	 */
	public static SymUnaryExpression new_dereference(CType data_type, SymExpression operand) throws Exception {
		SymUnaryExpression expression = 
				new SymUnaryExpression(data_type, COperator.dereference);
		expression.add_child(operand); return expression;
	}
	/**
	 * @param data_type
	 * @param operand
	 * @return *operand
	 * @throws Exception
	 */
	public static SymUnaryExpression new_cast_expression(CType data_type, SymExpression operand) throws Exception {
		SymUnaryExpression expression = 
				new SymUnaryExpression(data_type, COperator.assign);
		expression.add_child(operand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand + roperand
	 * @throws Exception
	 */
	public static SymMultiExpression new_arith_add(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymMultiExpression expression = new SymMultiExpression(data_type, COperator.arith_add);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand * roperand
	 * @throws Exception
	 */
	public static SymMultiExpression new_arith_mul(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymMultiExpression expression = new SymMultiExpression(data_type, COperator.arith_mul);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand & roperand
	 * @throws Exception
	 */
	public static SymMultiExpression new_bitws_and(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymMultiExpression expression = new SymMultiExpression(data_type, COperator.bit_and);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand | roperand
	 * @throws Exception
	 */
	public static SymMultiExpression new_bitws_ior(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymMultiExpression expression = new SymMultiExpression(data_type, COperator.bit_or);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand ^ roperand
	 * @throws Exception
	 */
	public static SymMultiExpression new_bitws_xor(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymMultiExpression expression = new SymMultiExpression(data_type, COperator.bit_xor);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand && roperand
	 * @throws Exception
	 */
	public static SymMultiExpression new_logic_and(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymMultiExpression expression = new SymMultiExpression(data_type, COperator.logic_and);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand || roperand
	 * @throws Exception
	 */
	public static SymMultiExpression new_logic_ior(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymMultiExpression expression = new SymMultiExpression(data_type, COperator.logic_or);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand - roperand
	 * @throws Exception
	 */
	public static SymBinaryExpression new_arith_sub(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(data_type, COperator.arith_sub);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand / roperand
	 * @throws Exception
	 */
	public static SymBinaryExpression new_arith_div(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(data_type, COperator.arith_div);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand % roperand
	 * @throws Exception
	 */
	public static SymBinaryExpression new_arith_mod(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(data_type, COperator.arith_mod);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand << roperand
	 * @throws Exception
	 */
	public static SymBinaryExpression new_bitws_lsh(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(data_type, COperator.left_shift);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand >> roperand
	 * @throws Exception
	 */
	public static SymBinaryExpression new_bitws_rsh(CType data_type, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(data_type, COperator.righ_shift);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand < roperand
	 * @throws Exception
	 */
	public static SymBinaryExpression new_smaller_tn(SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new 
				SymBinaryExpression(CBasicTypeImpl.bool_type, COperator.smaller_tn);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand <= roperand
	 * @throws Exception
	 */
	public static SymBinaryExpression new_smaller_eq(SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new 
				SymBinaryExpression(CBasicTypeImpl.bool_type, COperator.smaller_eq);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand > roperand
	 * @throws Exception
	 */
	public static SymBinaryExpression new_greater_tn(SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new 
				SymBinaryExpression(CBasicTypeImpl.bool_type, COperator.greater_tn);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand >= roperand
	 * @throws Exception
	 */
	public static SymBinaryExpression new_greater_eq(SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new 
				SymBinaryExpression(CBasicTypeImpl.bool_type, COperator.greater_eq);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand == roperand
	 * @throws Exception
	 */
	public static SymBinaryExpression new_equal_with(SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new 
				SymBinaryExpression(CBasicTypeImpl.bool_type, COperator.equal_with);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand != roperand
	 * @throws Exception
	 */
	public static SymBinaryExpression new_not_euqals(SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new 
				SymBinaryExpression(CBasicTypeImpl.bool_type, COperator.not_equals);
		expression.add_child(loperand); expression.add_child(roperand); return expression;
	}
	/**
	 * @param data_type
	 * @param body
	 * @param field
	 * @return body.field
	 * @throws Exception
	 */
	public static SymFieldExpression new_field_expression(CType data_type, SymExpression body, String field) throws Exception {
		SymFieldExpression expression = new SymFieldExpression(data_type);
		expression.add_child(body);
		expression.add_child(new SymField(field));
		return expression;
	}
	/**
	 * @param operands
	 * @return {operand+}
	 * @throws Exception
	 */
	public static SymInitializerList new_initializer_list(Iterable<SymExpression> operands) throws Exception {
		SymInitializerList list = new SymInitializerList();
		for(SymExpression operand : operands) {
			list.add_child(operand);
		}
		return list;
	}
	/**
	 * @param data_type
	 * @param function
	 * @param arguments
	 * @return function(argument*)
	 * @throws Exception
	 */
	public static SymFunCallExpression new_fun_call_expression(CType data_type, SymExpression function, Iterable<SymExpression> arguments) throws Exception {
		SymFunCallExpression expression = new SymFunCallExpression(data_type);
		expression.add_child(function);
		SymArgumentList list = new SymArgumentList();
		for(SymExpression argument : arguments) {
			list.add_child(argument);
		}
		expression.add_child(list);
		return expression;
	}
	
}
