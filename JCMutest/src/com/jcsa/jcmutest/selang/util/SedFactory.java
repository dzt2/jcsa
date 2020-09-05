package com.jcsa.jcmutest.selang.util;

import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcmutest.selang.lang.expr.SedBinaryExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedCallExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedConstant;
import com.jcsa.jcmutest.selang.lang.expr.SedDefaultValue;
import com.jcsa.jcmutest.selang.lang.expr.SedFieldExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedIdExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedInitializerList;
import com.jcsa.jcmutest.selang.lang.expr.SedLiteral;
import com.jcsa.jcmutest.selang.lang.expr.SedUnaryExpression;
import com.jcsa.jcmutest.selang.lang.tokn.SedField;
import com.jcsa.jcmutest.selang.lang.tokn.SedStatement;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides interface to create SedNode, from which the user obtains
 * the initial instance of SedNode and use clone() to create more ones.
 * @author yukimula
 *
 */
public class SedFactory {
	
	/* parsing from AstNode or CirNode or normal instance */
	/**
	 * @param source
	 * @param sizeof_template
	 * @return initial SedNode created from AstNode
	 * @throws Exception
	 */
	public static SedNode parse(AstNode source, 
			CRunTemplate sizeof_template) throws Exception {
		return SedParser.parse(source, sizeof_template);
	}
	/**
	 * @param source
	 * @return initial SedNode created from CirNode
	 * @throws Exception
	 */
	public static SedNode parse(CirNode source) throws Exception {
		return SedParser.parse(source);
	}
	/**
	 * @param source [bool|char|short|int|long|float|double|AstNode|CirNode|SedNode]
	 * @return initial SedNode parsed from normal instance
	 * @throws Exception
	 */
	public static SedNode fetch(Object source) throws Exception {
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
		else if(source instanceof AstNode) {
			return parse((AstNode) source, null);
		}
		else if(source instanceof CirNode) {
			return parse((CirNode) source);
		}
		else if(source instanceof SedNode) {
			return (SedNode) source;
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	
	/* cir-source & non-cir-source creation for SedNode */
	public static SedStatement statement(CirStatement statement) throws Exception {
		return new SedStatement(statement);
	}
	public static SedIdExpression id_expression(CType type, String name) throws Exception {
		return new SedIdExpression(null, type, name);
	}
	public static SedConstant constant(CType type, CConstant constant) throws Exception {
		return new SedConstant(null, type, constant);
	}
	public static SedLiteral literal(CType type, String literal) throws Exception {
		return new SedLiteral(null, type, literal);
	}
	public static SedDefaultValue default_value(CType type, String name) throws Exception {
		return new SedDefaultValue(null, type, name);
	}
	public static SedCallExpression call_expression(CType data_type,
			Object function, Iterable<Object> arguments) throws Exception {
		SedCallExpression expression = new SedCallExpression(null, data_type);
		expression.add_child(fetch(function));
		for(Object argument : arguments) expression.add_child(fetch(argument));
		return expression;
	}
	public static SedInitializerList initializer_list(CType 
			data_type, Iterable<Object> elements) throws Exception {
		SedInitializerList list = new SedInitializerList(null,
				data_type == null ? CBasicTypeImpl.void_type : data_type);
		for(Object element : elements) list.add_child(fetch(element));
		return list;
	}
	public static SedFieldExpression field_expression(CType 
			type, Object body, String name) throws Exception {
		SedFieldExpression expression = new SedFieldExpression(null, type);
		expression.add_child(fetch(body)); 
		expression.add_child(new SedField(name));
		return expression;
	}
	public static SedUnaryExpression arith_neg(CType data_type,
			Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, data_type, COperator.negative);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedUnaryExpression bitws_rsv(CType data_type,
			Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, data_type, COperator.bit_not);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedUnaryExpression logic_not(Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.logic_not);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedUnaryExpression address_of(CType data_type,
			Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, data_type, COperator.address_of);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedUnaryExpression dereference(CType data_type,
			Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, data_type, COperator.dereference);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedUnaryExpression type_cast(CType data_type,
			Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, data_type, COperator.assign);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedBinaryExpression arith_add(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.arith_add);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_sub(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.arith_sub);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_mul(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.arith_mul);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_div(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.arith_div);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_mod(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.arith_mod);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_and(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.bit_and);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_ior(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.bit_or);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_xor(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.bit_xor);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_lsh(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.left_shift);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_rsh(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.righ_shift);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression logic_and(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.logic_and);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression logic_ior(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.logic_or);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression greater_tn(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.greater_tn);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression greater_eq(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.greater_eq);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression smaller_tn(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.smaller_tn);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression smaller_eq(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.smaller_eq);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression equal_with(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.equal_with);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression not_equals(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.not_equals);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	
	/* description creators */
	
	
}
