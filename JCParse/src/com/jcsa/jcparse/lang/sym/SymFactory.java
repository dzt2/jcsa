package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SymFactory {
	
	/* data generator */
	/**
	 * @param source [bool|char|short|int|long|float|double|
	 * 				  CConstant|AstNode|CirNode|CirStatement
	 * 				  |CirExecution|SymExpression]
	 * @return SymExpression
	 * @throws Exception
	 */
	public static SymExpression parse(Object source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof AstExpression) {
			return SymParser.parse((AstExpression) source, null);
		}
		else if(source instanceof CirExpression) {
			return SymParser.parse((CirExpression) source);
		}
		else if(source instanceof CirStatement) {
			return SymFactory.sym_statement((CirStatement) source);
		}
		else if(source instanceof CirExecution) {
			return SymFactory.sym_statement(((CirExecution) source).get_statement());
		}
		else if(source instanceof SymExpression) {
			return ((SymExpression) source);
		}
		else
			return SymFactory.new_constant(source);
	}
	
	/* factory methods */
	public static SymIdentifier new_identifier(CType type, String name) throws Exception {
		return new SymIdentifier(type, name);
	}
	/**
	 * @param value {bool|char|short|int|long|float|double|CConstant}
	 * @return
	 * @throws Exception
	 */
	public static SymConstant new_constant(Object value) throws Exception {
		CConstant constant = new CConstant();
		if(value instanceof Boolean) {
			constant.set_bool(((Boolean) value).booleanValue());
			return new SymConstant(CBasicTypeImpl.bool_type, constant);
		}
		else if(value instanceof Character) {
			constant.set_char(((Character) value).charValue());
			return new SymConstant(CBasicTypeImpl.char_type, constant);
		}
		else if(value instanceof Short) {
			constant.set_int(((Short) value).shortValue());
			return new SymConstant(CBasicTypeImpl.short_type, constant);
		}
		else if(value instanceof Integer) {
			constant.set_int(((Integer) value).intValue());
			return new SymConstant(CBasicTypeImpl.int_type, constant);
		}
		else if(value instanceof Long) {
			constant.set_long(((Long) value).longValue());
			return new SymConstant(CBasicTypeImpl.long_type, constant);
		}
		else if(value instanceof Float) {
			constant.set_float(((Float) value).floatValue());
			return new SymConstant(CBasicTypeImpl.float_type, constant);
		}
		else if(value instanceof Double) {
			constant.set_double(((Double) value).doubleValue());
			return new SymConstant(CBasicTypeImpl.double_type, constant);
		}
		else if(value instanceof CConstant) {
			constant = (CConstant) value;
			return new SymConstant(constant.get_type(), constant);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + value.getClass().getSimpleName());
		}
	}
	public static SymLiteral new_literal(CType type, String literal) throws Exception {
		return new SymLiteral(type, literal);
	}
	public static SymUnaryExpression arith_neg(CType type, Object operand) throws Exception {
		SymUnaryExpression expression = new SymUnaryExpression(type);
		expression.add_child(new SymOperator(COperator.negative));
		expression.add_child(SymFactory.parse(operand));
		return expression;
	}
	public static SymUnaryExpression bitws_rsv(CType type, Object operand) throws Exception {
		SymUnaryExpression expression = new SymUnaryExpression(type);
		expression.add_child(new SymOperator(COperator.bit_not));
		expression.add_child(SymFactory.parse(operand));
		return expression;
	}
	public static SymUnaryExpression logic_not(Object operand) throws Exception {
		CType type = CBasicTypeImpl.bool_type;
		SymUnaryExpression expression = new SymUnaryExpression(type);
		expression.add_child(new SymOperator(COperator.logic_not));
		expression.add_child(SymFactory.parse(operand));
		return expression;
	}
	public static SymUnaryExpression address_of(CType type, Object operand) throws Exception {
		SymUnaryExpression expression = new SymUnaryExpression(type);
		expression.add_child(new SymOperator(COperator.address_of));
		expression.add_child(SymFactory.parse(operand));
		return expression;
	}
	public static SymUnaryExpression dereference(CType type, Object operand) throws Exception {
		SymUnaryExpression expression = new SymUnaryExpression(type);
		expression.add_child(new SymOperator(COperator.dereference));
		expression.add_child(SymFactory.parse(operand));
		return expression;
	}
	public static SymUnaryExpression type_cast(CType type, Object operand) throws Exception {
		SymUnaryExpression expression = new SymUnaryExpression(type);
		expression.add_child(new SymOperator(COperator.assign));
		expression.add_child(SymFactory.parse(operand));
		return expression;
	}
	public static SymBinaryExpression arith_add(CType type, 
			Object loperand, Object roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.arith_add));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression arith_sub(CType type, 
			Object loperand, Object roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.arith_sub));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression arith_mul(CType type, 
			Object loperand, Object roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.arith_mul));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression arith_div(CType type, 
			Object loperand, Object roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.arith_div));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression arith_mod(CType type, 
			Object loperand, Object roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.arith_mod));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression bitws_and(CType type, 
			Object loperand, Object roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.bit_and));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression bitws_ior(CType type, 
			Object loperand, Object roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.bit_or));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression bitws_xor(CType type, 
			Object loperand, Object roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.bit_xor));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression bitws_lsh(CType type, 
			Object loperand, Object roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.left_shift));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression bitws_rsh(CType type, 
			Object loperand, Object roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.righ_shift));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression logic_and(Object 
			loperand, Object roperand) throws Exception {
		CType type = CBasicTypeImpl.bool_type;
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.logic_and));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression logic_ior(Object 
			loperand, Object roperand) throws Exception {
		CType type = CBasicTypeImpl.bool_type;
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.logic_or));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression greater_tn(Object 
			loperand, Object roperand) throws Exception {
		CType type = CBasicTypeImpl.bool_type;
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.greater_tn));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression greater_eq(Object 
			loperand, Object roperand) throws Exception {
		CType type = CBasicTypeImpl.bool_type;
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.greater_eq));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression smaller_tn(Object 
			loperand, Object roperand) throws Exception {
		CType type = CBasicTypeImpl.bool_type;
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.smaller_tn));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression smaller_eq(Object 
			loperand, Object roperand) throws Exception {
		CType type = CBasicTypeImpl.bool_type;
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.smaller_eq));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression equal_with(Object 
			loperand, Object roperand) throws Exception {
		CType type = CBasicTypeImpl.bool_type;
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.equal_with));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymBinaryExpression not_equals(Object 
			loperand, Object roperand) throws Exception {
		CType type = CBasicTypeImpl.bool_type;
		SymBinaryExpression expression = new SymBinaryExpression(type);
		expression.add_child(new SymOperator(COperator.not_equals));
		expression.add_child(SymFactory.parse(loperand));
		expression.add_child(SymFactory.parse(roperand));
		return expression;
	}
	public static SymCallExpression call_expression(CType type, 
			Object function, Iterable<Object> arguments) throws Exception {
		SymCallExpression expression = new SymCallExpression(type);
		expression.add_child(SymFactory.parse(function));
		SymArgumentList list = new SymArgumentList();
		for(Object argument : arguments) {
			list.add_child(SymFactory.parse(argument));
		}
		expression.add_child(list); return expression;
	}
	public static SymFieldExpression field_expression(
			CType type, Object body, String name) throws Exception {
		SymFieldExpression expression = new SymFieldExpression(type);
		expression.add_child(SymFactory.parse(body));
		expression.add_child(new SymField(name));
		return expression;
	}
	public static SymInitializerList initializer_list(CType data_type, Iterable<Object> elements) throws Exception {
		if(data_type == null) data_type = CBasicTypeImpl.void_type;
		SymInitializerList list = new SymInitializerList(data_type);
		for(Object element : elements) {
			list.add_child(SymFactory.parse(element));
		}
		return list;
	}
	/**
	 * @param statement
	 * @return symbolic identifier that describes the statement pointer
	 * @throws Exception
	 */
	public static SymExpression sym_statement(CirStatement statement) throws Exception {
		String name = statement.get_tree().get_localizer().get_execution(statement).toString();
		return SymFactory.new_identifier(CBasicTypeImpl.int_type, "@" + name);
	}
	
}
