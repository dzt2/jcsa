package com.jcsa.jcparse.lang.symb;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * It provides interface to construct SymbolNode.
 * 
 * @author yukimula
 *
 */
public class SymbolFactory {
	
	/* singleton mode */
	/** the parser used to parse Java-Instance to SymbolNode **/
	private SymbolParser parser;
	/**
	 * create a default factory for constructing symbolic node with ast_template as
	 * null and C-intermediate representative optimization as closed configuration.
	 */
	private SymbolFactory() { this.parser = new SymbolParser(); }
	/** the singleton instance of the symbolic node factory to construct **/
	private static final SymbolFactory factory = new SymbolFactory();
	/**
	 * It establishes the parameters used in factory of symbolic node
	 * @param ast_template	used to support the sizeof-computation
	 * @param cir_optimize	used to implement default-value parses
	 */
	public static void set_config(CRunTemplate ast_template, boolean cir_optimize) {
		factory.parser.set(ast_template, cir_optimize);
	}
	
	/* parse methods */
	/**
	 * @param value	{Boolean|Character|Short|Integer|Long|Float|Double|CConstant}
	 * @return		
	 * @throws Exception
	 */
	public static SymbolConstant sym_constant(Object value) throws Exception {
		return factory.parser.parse_cons(value);
	}
	/**
	 * @param source	{Boolean|Character|Short|Integer|Long|Float|Double|CConstant
	 * 					|AstNode|CirNode|CirExecution|SymbolExpression}
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression sym_expression(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof Boolean || source instanceof Character || source instanceof Short ||
				source instanceof Integer || source instanceof Long || source instanceof Float ||
				source instanceof Double || source instanceof CConstant) {
			return factory.parser.parse_cons(source);
		}
		else if(source instanceof CirExecution) {
			return factory.parser.parse_exec((CirExecution) source);
		}
		else if(source instanceof AstNode) {
			return factory.parser.parse_astn((AstNode) source);
		}
		else if(source instanceof CirNode) {
			return factory.parser.parse_cirn((CirNode) source);
		}
		else if(source instanceof SymbolExpression) {
			return (SymbolExpression) source;
		}
		else if(source instanceof String) {
			return SymbolLiteral.create((String) source);
		}
		else {
			throw new IllegalArgumentException("Invalid source: " + source.getClass().getSimpleName());
		}
	}
	/**
	 * @param source	{Boolean|Character|Short|Integer|Long|Float|Double|CConstant
	 * 					|AstNode|CirNode|CirExecution|SymbolExpression}
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression sym_condition(Object source, boolean value) throws Exception {
		SymbolExpression expression = sym_expression(source);
		return factory.parser.parse_bool(expression, value);
	}
	
	/* node creators */
	/**
	 * @param cname		{CInstanceName|CEnumeratorName|CParameterName}
	 * @return			
	 * @throws Exception
	 */
	public static SymbolExpression	identifier(CName cname) throws Exception {
		if(cname == null) {
			throw new IllegalArgumentException("Invalid cname: null");
		}
		else if(cname instanceof CInstanceName) {
			CInstance instance = ((CInstanceName) cname).get_instance();
			return SymbolIdentifier.create(instance.get_type(), 
					cname.get_name() + "#" + cname.get_scope().hashCode());
		}
		else if(cname instanceof CParameterName) {
			CInstance instance = ((CParameterName) cname).get_parameter();
			return SymbolIdentifier.create(instance.get_type(), 
					cname.get_name() + "#" + cname.get_scope().hashCode());
		}
		else if(cname instanceof CEnumeratorName) {
			int value = ((CEnumeratorName) cname).get_enumerator().get_value();
			return factory.parser.parse_cons(Integer.valueOf(value));
		}
		else {
			throw new IllegalArgumentException(cname.getClass().getSimpleName());
		}
	}
	/**
	 * @param source
	 * @return	(ast#key: type)
	 * @throws Exception
	 */
	public static SymbolExpression	identifier(AstExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			return SymbolIdentifier.create(source.get_value_type(), "ast#" + source.get_key());
		}
	}
	/**
	 * @param source
	 * @return	(cir#key: type)
	 * @throws Exception
	 */
	public static SymbolExpression	identifier(CirExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			return SymbolIdentifier.create(source.get_data_type(), "cir#" + source.get_node_id());
		}
	}
	/**
	 * @param source
	 * @return	(return#name: type)
	 * @throws Exception
	 */
	public static SymbolExpression	identifier(CType type, AstFunctionDefinition source) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			AstDeclarator declarator = source.get_declarator();
			while(declarator.get_production() != DeclaratorProduction.identifier) {
				declarator = declarator.get_declarator();
			}
			String name = "return#" + declarator.get_identifier().get_name();
			return SymbolIdentifier.create(type, name);
		}
	}
	/**
	 * @param type
	 * @param source
	 * @return	(return#name: type)
	 * @throws Exception
	 */
	public static SymbolExpression	identifier(CType type, CirFunctionDefinition source) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			String name = "return#" + source.get_declarator().get_name();
			return SymbolIdentifier.create(type, name);
		}
	}
	/**
	 * @param type
	 * @param name
	 * @return (name: type)
	 * @throws Exception
	 */
	public static SymbolExpression	identifier(CType type, String name) throws Exception {
		return SymbolIdentifier.create(type, name);
	}
	/**
	 * @param execution
	 * @return (do#execution: int)
	 * @throws Exception
	 */
	public static SymbolExpression	identifier(CirExecution execution) throws Exception {
		return factory.parser.parse_exec(execution);
	}
	/**
	 * @param value
	 * @return bool constant
	 * @throws Exception
	 */
	public static SymbolExpression 	constant(boolean value) throws Exception {
		return factory.parser.parse_cons(Boolean.valueOf(value));
	}
	/**
	 * @param value
	 * @return char constant
	 * @throws Exception
	 */
	public static SymbolExpression 	constant(char value) throws Exception {
		return factory.parser.parse_cons(Character.valueOf(value));
	}
	/**
	 * @param value
	 * @return short constant
	 * @throws Exception
	 */
	public static SymbolExpression 	constant(short value) throws Exception {
		return factory.parser.parse_cons(Integer.valueOf(value));
	}
	/**
	 * @param value
	 * @return int constant
	 * @throws Exception
	 */
	public static SymbolExpression 	constant(int value) throws Exception {
		return factory.parser.parse_cons(Integer.valueOf(value));
	}
	/**
	 * @param value
	 * @return long constant
	 * @throws Exception
	 */
	public static SymbolExpression 	constant(long value) throws Exception {
		return factory.parser.parse_cons(Long.valueOf(value));
	}
	/**
	 * @param value
	 * @return float constant
	 * @throws Exception
	 */
	public static SymbolExpression 	constant(float value) throws Exception {
		return factory.parser.parse_cons(Float.valueOf(value));
	}
	/**
	 * @param value
	 * @return short constant
	 * @throws Exception
	 */
	public static SymbolExpression 	constant(double value) throws Exception {
		return factory.parser.parse_cons(Double.valueOf(value));
	}
	/**
	 * @param literal
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression	literal(String literal) throws Exception {
		return SymbolLiteral.create(literal);
	}
	/**
	 * @param operand
	 * @return -operand
	 * @throws Exception
	 */
	public static SymbolExpression	arith_neg(Object operand) throws Exception {
		SymbolExpression expression = sym_expression(operand);
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:	type = CBasicTypeImpl.int_type;		break;
			case c_int:
			case c_long:
			case c_llong:
			case c_float:
			case c_double:
			case c_ldouble:
			case c_uint:
			case c_ulong:
			case c_ullong:	break;
			default: throw new IllegalArgumentException(type.generate_code());
			}
		}
		else if(type instanceof CEnumType) {
			type = CBasicTypeImpl.int_type;
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
		return SymbolUnaryExpression.create(type, COperator.negative, expression);
	}
	/**
	 * @param operand
	 * @return ~operand
	 * @throws Exception
	 */
	public static SymbolExpression	bitws_rsv(Object operand) throws Exception {
		SymbolExpression expression = sym_expression(operand);
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:	type = CBasicTypeImpl.int_type;		break;
			case c_int:
			case c_long:
			case c_llong:
			case c_uint:
			case c_ulong:
			case c_ullong:	break;
			default: throw new IllegalArgumentException(type.generate_code());
			}
		}
		else if(type instanceof CEnumType) {
			type = CBasicTypeImpl.int_type;
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
		return SymbolUnaryExpression.create(type, COperator.bit_not, expression);
	}
	/**
	 * @param operand
	 * @return !operand
	 * @throws Exception
	 */
	public static SymbolExpression 	logic_not(Object operand) throws Exception {
		return sym_condition(operand, false);
	}
	/**
	 * @param operand
	 * @return &operand
	 * @throws Exception
	 */
	public static SymbolExpression	address_of(Object operand) throws Exception {
		SymbolExpression expression = sym_expression(operand);
		if(expression.is_reference()) {
			CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			type = SymbolParser.type_factory.get_pointer_type(type);
			return SymbolUnaryExpression.create(type, COperator.address_of, expression);
		}
		else {
			throw new IllegalArgumentException("Not reference: " + expression);
		}
	}
	/**
	 * @param operand
	 * @return *operand
	 * @throws Exception
	 */
	public static SymbolExpression	dereference(Object operand) throws Exception {
		SymbolExpression expression = sym_expression(operand);
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(type instanceof CArrayType) {
			type = ((CArrayType) type).get_element_type();
		}
		else {
			type = ((CPointerType) type).get_pointed_type();
		}
		return SymbolUnaryExpression.create(type, COperator.dereference, expression);
	}
	/**
	 * @param type
	 * @param operand
	 * @return (type) operand
	 * @throws Exception
	 */
	public static SymbolExpression	type_casting(CType type, Object operand) throws Exception {
		SymbolType cast_type = SymbolType.create(type);
		SymbolExpression expression = sym_expression(operand);
		return SymbolCastExpression.create(cast_type, expression);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return loperand + roperand
	 * @throws Exception
	 */
	public static SymbolExpression	arith_add(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(type, COperator.arith_add, lexpression, rexpression);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return loperand - roperand
	 * @throws Exception
	 */
	public static SymbolExpression	arith_sub(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(type, COperator.arith_sub, lexpression, rexpression);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return loperand * roperand
	 * @throws Exception
	 */
	public static SymbolExpression	arith_mul(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(type, COperator.arith_mul, lexpression, rexpression);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return loperand / roperand
	 * @throws Exception
	 */
	public static SymbolExpression	arith_div(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(type, COperator.arith_div, lexpression, rexpression);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return loperand % roperand
	 * @throws Exception
	 */
	public static SymbolExpression	arith_mod(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(type, COperator.arith_mod, lexpression, rexpression);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return loperand & roperand
	 * @throws Exception
	 */
	public static SymbolExpression	bitws_and(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(type, COperator.bit_and, lexpression, rexpression);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return loperand | roperand
	 * @throws Exception
	 */
	public static SymbolExpression	bitws_ior(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(type, COperator.bit_or, lexpression, rexpression);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return loperand ^ roperand
	 * @throws Exception
	 */
	public static SymbolExpression	bitws_xor(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(type, COperator.bit_xor, lexpression, rexpression);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return loperand << roperand
	 * @throws Exception
	 */
	public static SymbolExpression	bitws_lsh(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(type, COperator.left_shift, lexpression, rexpression);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return loperand & roperand
	 * @throws Exception
	 */
	public static SymbolExpression	bitws_rsh(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(type, COperator.righ_shift, lexpression, rexpression);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand && roperand
	 * @throws Exception
	 */
	public static SymbolExpression 	logic_and(Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_condition(loperand, true);
		rexpression = sym_condition(roperand, true);
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
						COperator.logic_and, lexpression, rexpression);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand || roperand
	 * @throws Exception
	 */
	public static SymbolExpression 	logic_ior(Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_condition(loperand, true);
		rexpression = sym_condition(roperand, true);
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
						COperator.logic_or, lexpression, rexpression);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand > roperand
	 * @throws Exception
	 */
	public static SymbolExpression	greater_tn(Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(CBasicTypeImpl.
				bool_type, COperator.smaller_tn, rexpression, lexpression);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand >= roperand
	 * @throws Exception
	 */
	public static SymbolExpression	greater_eq(Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(CBasicTypeImpl.
				bool_type, COperator.smaller_eq, rexpression, lexpression);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand < roperand
	 * @throws Exception
	 */
	public static SymbolExpression	smaller_tn(Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(CBasicTypeImpl.
				bool_type, COperator.smaller_tn, lexpression, rexpression);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand <= roperand
	 * @throws Exception
	 */
	public static SymbolExpression	smaller_eq(Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(CBasicTypeImpl.
				bool_type, COperator.smaller_eq, lexpression, rexpression);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand == roperand
	 * @throws Exception
	 */
	public static SymbolExpression	equal_with(Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(CBasicTypeImpl.
				bool_type, COperator.equal_with, lexpression, rexpression);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand != roperand
	 * @throws Exception
	 */
	public static SymbolExpression	not_equals(Object loperand, Object roperand) throws Exception {
		SymbolExpression lexpression, rexpression;
		lexpression = sym_expression(loperand);
		rexpression = sym_expression(roperand);
		return SymbolBinaryExpression.create(CBasicTypeImpl.
				bool_type, COperator.not_equals, lexpression, rexpression);
	}
	/**
	 * @param function
	 * @param arguments
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression	call_expression(Object function, Iterable<Object> arguments) throws Exception {
		SymbolExpression func = sym_expression(function);
		List<SymbolExpression> list = new ArrayList<SymbolExpression>();
		for(Object argument : arguments) {
			list.add(sym_expression(argument));
		}
		CType data_type = CTypeAnalyzer.get_value_type(func.get_data_type());
		if(data_type instanceof CPointerType) {
			data_type = CTypeAnalyzer.get_value_type(((CPointerType) data_type).get_pointed_type());
		}
		if(data_type instanceof CFunctionType) {
			data_type = ((CFunctionType) data_type).get_return_type();
		}
		else {
			throw new IllegalArgumentException("Invalid data_type");
		}
		return SymbolCallExpression.create(data_type, func, SymbolArgumentList.create(list));
	}
	/**
	 * @param body
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression	field_expression(Object body, String name) throws Exception {
		SymbolExpression sbody = sym_expression(body);
		SymbolField field = SymbolField.create(name);
		
		CType data_type = CTypeAnalyzer.get_value_type(sbody.get_data_type());
		if(data_type instanceof CStructType) {
			data_type = ((CStructType) data_type).get_fields().get_field(name).get_type();
		}
		else if(data_type instanceof CUnionType) {
			data_type = ((CUnionType) data_type).get_fields().get_field(name).get_type();
		}
		else {
			throw new IllegalArgumentException("Invalid data_type");
		}
		
		return SymbolFieldExpression.create(data_type, sbody, field);
	}
	/**
	 * @param condition
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression 	condition_expression(Object condition, Object loperand, Object roperand) throws Exception {
		SymbolExpression cond = sym_condition(condition, true);
		SymbolExpression tval = sym_expression(loperand);
		SymbolExpression fval = sym_expression(roperand);
		return SymbolConditionExpression.create(tval.get_data_type(), cond, tval, fval);
	}
	/**
	 * @param lvalue
	 * @param rvalue
	 * @return lvalue := rvalue
	 * @throws Exception
	 */
	public static SymbolExpression 	assign_to(Object lvalue, Object rvalue) throws Exception {
		SymbolExpression loperand = sym_expression(lvalue);
		SymbolExpression roperand = sym_expression(rvalue);
		if(loperand.is_reference()) {
			return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + loperand);
		}
	}
	/**
	 * @param elements
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression	init_list(Iterable<Object> elements) throws Exception {
		List<SymbolExpression> list = new ArrayList<SymbolExpression>();
		for(Object element : elements) {
			list.add(sym_expression(element));
		}
		return SymbolInitializerList.create(CBasicTypeImpl.void_type, list);
	}
	/**
	 * @param operand
	 * @return ++operand
	 * @throws Exception
	 */
	public static SymbolExpression	prev_inc(Object operand) throws Exception {
		SymbolExpression expression = sym_expression(operand);
		if(expression.is_reference()) {
			CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			if(type instanceof CBasicType) {
				switch(((CBasicType) type).get_tag()) {
				case c_bool:
				case c_char:
				case c_uchar:
				case c_short:
				case c_ushort:	type = CBasicTypeImpl.int_type;	break;
				case c_int:
				case c_uint:
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:
				case c_float:
				case c_double:
				case c_ldouble:	break;
				default:	throw new IllegalArgumentException(type.generate_code());
				}
			}
			else if(type instanceof CEnumType) {
				type = CBasicTypeImpl.int_type;
			}
			else if(type instanceof CPointerType) { }
			else if(type instanceof CArrayType) {
				type = SymbolParser.type_factory.get_pointer_type(
							((CArrayType) type).get_element_type());
			}
			else {
				throw new IllegalArgumentException(type.generate_code());
			}
			return SymbolUnaryExpression.create(type, COperator.increment, expression);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param operand
	 * @return --operand
	 * @throws Exception
	 */
	public static SymbolExpression	prev_dec(Object operand) throws Exception {
		SymbolExpression expression = sym_expression(operand);
		if(expression.is_reference()) {
			CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			if(type instanceof CBasicType) {
				switch(((CBasicType) type).get_tag()) {
				case c_bool:
				case c_char:
				case c_uchar:
				case c_short:
				case c_ushort:	type = CBasicTypeImpl.int_type;	break;
				case c_int:
				case c_uint:
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:
				case c_float:
				case c_double:
				case c_ldouble:	break;
				default:	throw new IllegalArgumentException(type.generate_code());
				}
			}
			else if(type instanceof CEnumType) {
				type = CBasicTypeImpl.int_type;
			}
			else if(type instanceof CPointerType) { }
			else if(type instanceof CArrayType) {
				type = SymbolParser.type_factory.get_pointer_type(
							((CArrayType) type).get_element_type());
			}
			else {
				throw new IllegalArgumentException(type.generate_code());
			}
			return SymbolUnaryExpression.create(type, COperator.decrement, expression);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param operand
	 * @return operand++
	 * @throws Exception
	 */
	public static SymbolExpression	post_inc(Object operand) throws Exception {
		SymbolExpression expression = sym_expression(operand);
		if(expression.is_reference()) {
			CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			if(type instanceof CBasicType) {
				switch(((CBasicType) type).get_tag()) {
				case c_bool:
				case c_char:
				case c_uchar:
				case c_short:
				case c_ushort:	type = CBasicTypeImpl.int_type;	break;
				case c_int:
				case c_uint:
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:
				case c_float:
				case c_double:
				case c_ldouble:	break;
				default:	throw new IllegalArgumentException(type.generate_code());
				}
			}
			else if(type instanceof CEnumType) {
				type = CBasicTypeImpl.int_type;
			}
			else if(type instanceof CPointerType) { }
			else if(type instanceof CArrayType) {
				type = SymbolParser.type_factory.get_pointer_type(
							((CArrayType) type).get_element_type());
			}
			else {
				throw new IllegalArgumentException(type.generate_code());
			}
			return SymbolUnaryExpression.create(type, COperator.arith_add_assign, expression);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param operand
	 * @return operand--
	 * @throws Exception
	 */
	public static SymbolExpression	post_dec(Object operand) throws Exception {
		SymbolExpression expression = sym_expression(operand);
		if(expression.is_reference()) {
			CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			if(type instanceof CBasicType) {
				switch(((CBasicType) type).get_tag()) {
				case c_bool:
				case c_char:
				case c_uchar:
				case c_short:
				case c_ushort:	type = CBasicTypeImpl.int_type;	break;
				case c_int:
				case c_uint:
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:
				case c_float:
				case c_double:
				case c_ldouble:	break;
				default:	throw new IllegalArgumentException(type.generate_code());
				}
			}
			else if(type instanceof CEnumType) {
				type = CBasicTypeImpl.int_type;
			}
			else if(type instanceof CPointerType) { }
			else if(type instanceof CArrayType) {
				type = SymbolParser.type_factory.get_pointer_type(
							((CArrayType) type).get_element_type());
			}
			else {
				throw new IllegalArgumentException(type.generate_code());
			}
			return SymbolUnaryExpression.create(type, COperator.arith_sub_assign, expression);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	
}
