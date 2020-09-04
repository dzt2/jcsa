package com.jcsa.jcmutest.sedlang.util;

import com.jcsa.jcmutest.sedlang.lang.expr.SedBinaryExpression;
import com.jcsa.jcmutest.sedlang.lang.expr.SedCallExpression;
import com.jcsa.jcmutest.sedlang.lang.expr.SedConstant;
import com.jcsa.jcmutest.sedlang.lang.expr.SedDefaultValue;
import com.jcsa.jcmutest.sedlang.lang.expr.SedFieldExpression;
import com.jcsa.jcmutest.sedlang.lang.expr.SedIdExpression;
import com.jcsa.jcmutest.sedlang.lang.expr.SedInitializerList;
import com.jcsa.jcmutest.sedlang.lang.expr.SedLiteral;
import com.jcsa.jcmutest.sedlang.lang.expr.SedUnaryExpression;
import com.jcsa.jcmutest.sedlang.lang.token.SedArgumentList;
import com.jcsa.jcmutest.sedlang.lang.token.SedField;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides interfaces to create SedNode.
 * @author yukimula
 *
 */
public class SedFactory {
	
	private static final CTypeFactory factory = new CTypeFactory();
	
	/* expression creators */
	public static SedIdExpression id_expression(CType type, String name) throws Exception {
		return new SedIdExpression(null, type, name);
	}
	/**
	 * @param source
	 * @return bool|char|short|int|long|float|double
	 * @throws Exception
	 */
	public static SedConstant constant(Object source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof Boolean) {
			CConstant constant = new CConstant();
			constant.set_bool(((Boolean) source).booleanValue());
			return new SedConstant(null, CBasicTypeImpl.bool_type, constant);
		}
		else if(source instanceof Character) {
			CConstant constant = new CConstant();
			constant.set_char(((Character) source).charValue());
			return new SedConstant(null, CBasicTypeImpl.char_type, constant);
		}
		else if(source instanceof Short) {
			CConstant constant = new CConstant();
			constant.set_int(((Short) source).shortValue());
			return new SedConstant(null, CBasicTypeImpl.short_type, constant);
		}
		else if(source instanceof Integer) {
			CConstant constant = new CConstant();
			constant.set_int(((Integer) source).intValue());
			return new SedConstant(null, CBasicTypeImpl.int_type, constant);
		}
		else if(source instanceof Long) {
			CConstant constant = new CConstant();
			constant.set_long(((Long) source).longValue());
			return new SedConstant(null, CBasicTypeImpl.long_type, constant);
		}
		else if(source instanceof Float) {
			CConstant constant = new CConstant();
			constant.set_float(((Float) source).floatValue());
			return new SedConstant(null, CBasicTypeImpl.float_type, constant);
		}
		else if(source instanceof Double) {
			CConstant constant = new CConstant();
			constant.set_double(((Double) source).doubleValue());
			return new SedConstant(null, CBasicTypeImpl.double_type, constant);
		}
		else {
			throw new IllegalArgumentException("Unsupport " + source.getClass());
		}
	}
	public static SedLiteral string_literal(String literal) throws Exception {
		return new SedLiteral(null, factory.get_array_type(
				CBasicTypeImpl.char_type, literal.length() + 1),
				literal);
	}
	public static SedDefaultValue default_value(CType type, String name) throws Exception {
		return new SedDefaultValue(null, type, name);
	}
	public static SedUnaryExpression arith_neg(CType type, Object operand) throws Exception {
		SedUnaryExpression expression = 
				new SedUnaryExpression(null, type, COperator.negative);
		expression.add_child(SedParser.fetch(operand));
		return expression;
	}
	public static SedUnaryExpression bitws_rsv(CType type, Object operand) throws Exception {
		SedUnaryExpression expression = 
				new SedUnaryExpression(null, type, COperator.bit_not);
		expression.add_child(SedParser.fetch(operand));
		return expression;
	}
	public static SedUnaryExpression logic_not(Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.logic_not);
		expression.add_child(SedParser.fetch(operand));
		return expression;
	}
	public static SedUnaryExpression address_of(CType type, Object operand) throws Exception {
		SedUnaryExpression expression = 
				new SedUnaryExpression(null, type, COperator.address_of);
		expression.add_child(SedParser.fetch(operand));
		return expression;
	}
	public static SedUnaryExpression dereference(CType type, Object operand) throws Exception {
		SedUnaryExpression expression = 
				new SedUnaryExpression(null, type, COperator.dereference);
		expression.add_child(SedParser.fetch(operand));
		return expression;
	}
	public static SedUnaryExpression type_cast(CType type, Object operand) throws Exception {
		SedUnaryExpression expression = 
				new SedUnaryExpression(null, type, COperator.assign);
		expression.add_child(SedParser.fetch(operand));
		return expression;
	}
	public static SedBinaryExpression arith_add(CType type, 
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new 
				SedBinaryExpression(null, type, COperator.arith_add);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_sub(CType type, 
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new 
				SedBinaryExpression(null, type, COperator.arith_sub);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_mul(CType type, 
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new 
				SedBinaryExpression(null, type, COperator.arith_mul);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_div(CType type, 
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new 
				SedBinaryExpression(null, type, COperator.arith_div);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_mod(CType type, 
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new 
				SedBinaryExpression(null, type, COperator.arith_mod);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_and(CType type, 
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new 
				SedBinaryExpression(null, type, COperator.bit_and);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_ior(CType type, 
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new 
				SedBinaryExpression(null, type, COperator.bit_or);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_xor(CType type, 
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new 
				SedBinaryExpression(null, type, COperator.bit_xor);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_lsh(CType type, 
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new 
				SedBinaryExpression(null, type, COperator.left_shift);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_rsh(CType type, 
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new 
				SedBinaryExpression(null, type, COperator.righ_shift);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression logic_and(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.logic_and);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression logic_ior(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.logic_or);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression greater_tn(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.greater_tn);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression smaller_tn(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.smaller_tn);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression greater_eq(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.greater_eq);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression smaller_eq(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.smaller_eq);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression equal_with(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.equal_with);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression not_equals(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.not_equals);
		expression.add_child(SedParser.fetch(loperand));
		expression.add_child(SedParser.fetch(roperand));
		return expression;
	}
	public static SedCallExpression call_expression(CType type, 
			Object function, Iterable<Object> arguments) throws Exception {
		SedCallExpression expression = new SedCallExpression(null, type);
		expression.add_child(SedParser.fetch(function));
		SedArgumentList argument_list = new SedArgumentList();
		for(Object argument : arguments) {
			argument_list.add_child(SedParser.fetch(argument));
		}
		expression.add_child(argument_list); return expression;
	}
	public static SedFieldExpression field_expression(CType type, 
			Object body, String field) throws Exception {
		SedFieldExpression expression = new SedFieldExpression(null, type);
		expression.add_child(SedParser.fetch(body));
		expression.add_child(new SedField(field));
		return expression;
	}
	public static SedInitializerList initializer_list(
			CType type, Iterable<Object> elements) throws Exception {
		SedInitializerList list = new SedInitializerList(null, type);
		for(Object element : elements) {
			list.add_child(SedParser.fetch(element));
		}
		return list;
	}
	
}
