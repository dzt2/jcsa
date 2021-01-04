package com.jcsa.jcparse.lang.symbol;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * It is used to create SymbolNode.
 * 
 * @author yukimula
 *
 */
public class SymbolFactory {
	
	/* parameter configuration */
	/** to generate data type **/
	private static final CTypeFactory t_factory = new CTypeFactory();
	/** to support sizeof computation in parsing AstNode **/
	public static CRunTemplate 	ast_template 	= null;
	/** to support translation from CirDefaultValue to constant **/
	public static boolean 		cir_optimize	= false;
	public static void config(CRunTemplate ast_template, boolean cir_optimize) {
		SymbolFactory.ast_template = ast_template;
		SymbolFactory.cir_optimize = cir_optimize;
	}
	
	/* translation methods */
	/**
	 * @param source
	 * @return	(1) Boolean|Character|Short|Integer|Long|Float|Double	==> SymbolConstant
	 * 			(2) String												==>	SymbolLiteral
	 * 			(3) AstNode			{ast_template}						==> SymbolExpression
	 * 			(4)	CirNode			{cir_optimize}						==>	SymbolExpression
	 * 			(5)	CirExecution										==> SymbolIdentifier
	 * @throws Exception
	 */
	public static SymbolExpression sym_expression(Object source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source as null");
		else if(source instanceof Boolean
				|| source instanceof Character
				|| source instanceof Short
				|| source instanceof Integer
				|| source instanceof Long
				|| source instanceof Float
				|| source instanceof Double
				|| source instanceof String)
			return (SymbolExpression) SymbolParser.parser.parse_con(source);
		else if(source instanceof AstNode)
			return (SymbolExpression) SymbolParser.parser.parse_ast((AstNode) source, ast_template);
		else if(source instanceof CirNode)
			return (SymbolExpression) SymbolParser.parser.parse_cir((CirNode) source, cir_optimize);
		else if(source instanceof CirExecution)
			return (SymbolExpression) SymbolParser.parser.parse_exe((CirExecution) source);
		else
			throw new IllegalArgumentException("Invalid: " + source.getClass().getSimpleName());
	}
	/**
	 * @param source
	 * @param value false to negate the original expression
	 * @return 
	 * @throws Exception
	 */
	public static SymbolExpression sym_condition(Object source, boolean value) throws Exception {
		SymbolExpression expression = sym_expression(source);
		return (SymbolExpression) SymbolParser.parser.parse_cod(expression, value);
	}
	
	/* non-source creators */
	/**
	 * @param cname
	 * @return name#scope
	 * @throws Exception
	 */
	public static SymbolExpression identifier(CName cname) throws Exception {
		if(cname instanceof CInstanceName) {
			return SymbolIdentifier.create(((CInstanceName) cname).get_instance().get_type(), cname);
		}
		else if(cname instanceof CParameterName) {
			return SymbolIdentifier.create(((CParameterName) cname).get_parameter().get_type(), cname);
		}
		else if(cname instanceof CEnumeratorName) {
			return sym_expression(Integer.valueOf(((CEnumeratorName) cname).get_enumerator().get_value()));
		}
		else {
			throw new IllegalArgumentException("Invalid cname: null");
		}
	}
	/**
	 * @param ast_reference
	 * @return #ast.key
	 * @throws Exception
	 */
	public static SymbolExpression identifier(AstExpression ast_reference) throws Exception {
		return SymbolIdentifier.create(ast_reference.get_value_type(), ast_reference);
	}
	/**
	 * @param default_value
	 * @return default#cir_node.id
	 * @throws Exception
	 */
	public static SymbolExpression identifier(CirDefaultValue default_value) throws Exception {
		return SymbolIdentifier.create(default_value.get_data_type(), default_value);
	}
	/**
	 * @param data_type
	 * @param def
	 * @return return#function.id
	 * @throws Exception
	 */
	public static SymbolExpression identifier(CType data_type, CirFunctionDefinition def) throws Exception {
		return SymbolIdentifier.create(data_type, def);
	}
	/**
	 * @param value 
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression constant(Object source) throws Exception {
		if(source instanceof Boolean
				|| source instanceof Character
				|| source instanceof Short
				|| source instanceof Integer
				|| source instanceof Long
				|| source instanceof Float
				|| source instanceof Double)
			return (SymbolExpression) SymbolParser.parser.parse_con(source);
		else
			throw new IllegalArgumentException("Invalid: " + source);
	}
	/**
	 * @param literal
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression literal(String literal) throws Exception {
		return SymbolLiteral.create(literal);
	}
	/**
	 * @param elements
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression initializer_list(Iterable<Object> elements) throws Exception {
		List<SymbolExpression> elist = new ArrayList<SymbolExpression>();
		for(Object element : elements) elist.add(sym_expression(element));
		return SymbolInitializerList.create(null, elist);
	}
	public static SymbolExpression field_expression(Object body, String field) throws Exception {
		SymbolExpression sbody = sym_expression(body);
		SymbolField sfield = SymbolField.create(field);
		
		CType data_type = CTypeAnalyzer.get_value_type(sbody.get_data_type());
		if(data_type instanceof CStructType) {
			data_type = ((CStructType) data_type).get_fields().get_field(field).get_type();
		}
		else if(data_type instanceof CUnionType) {
			data_type = ((CUnionType) data_type).get_fields().get_field(field).get_type();
		}
		else {
			throw new IllegalArgumentException("Invalid data_type");
		}
		
		return SymbolFieldExpression.create(data_type, sbody, sfield);
	}
	public static SymbolExpression call_expression(Object function, Iterable<Object> arguments) throws Exception {
		SymbolExpression sfunction = sym_expression(function);
		List<SymbolExpression> elist = new ArrayList<SymbolExpression>();
		for(Object argument : arguments) elist.add(sym_expression(argument));
		
		CType data_type = CTypeAnalyzer.get_value_type(sfunction.get_data_type());
		if(data_type instanceof CPointerType) {
			data_type = CTypeAnalyzer.get_value_type(((CPointerType) data_type).get_pointed_type());
		}
		if(data_type instanceof CFunctionType) {
			data_type = ((CFunctionType) data_type).get_return_type();
		}
		else {
			throw new IllegalArgumentException("Invalid data_type");
		}
		
		return SymbolCallExpression.create(data_type, sfunction, SymbolArgumentList.create(elist));
	}
	public static SymbolExpression negative(Object operand) throws Exception {
		SymbolExpression soperand = sym_expression(operand);
		CType data_type = CTypeAnalyzer.get_value_type(soperand.get_data_type());
		
		if(CTypeAnalyzer.is_boolean(data_type)) {
			data_type = CBasicTypeImpl.int_type;
		}
		else if(CTypeAnalyzer.is_integer(data_type) || CTypeAnalyzer.is_real(data_type)) {
			/* remain the original data type */
		}
		else {
			throw new IllegalArgumentException(data_type.generate_code());
		}
		
		return SymbolUnaryExpression.create(data_type, SymbolOperator.create(COperator.negative), sym_expression(operand));
	}
	public static SymbolExpression bitws_rsv(Object operand) throws Exception {
		SymbolExpression soperand = sym_expression(operand);
		CType data_type = CTypeAnalyzer.get_value_type(soperand.get_data_type());
		
		if(CTypeAnalyzer.is_boolean(data_type)) {
			data_type = CBasicTypeImpl.int_type;
		}
		else if(CTypeAnalyzer.is_integer(data_type)) {
			/* remain the original data type */
		}
		else {
			throw new IllegalArgumentException(data_type.generate_code());
		}
		
		return SymbolUnaryExpression.create(data_type, SymbolOperator.create(COperator.bit_not), sym_expression(operand));
	}
	public static SymbolExpression logic_not(Object operand) throws Exception {
		return sym_condition(operand, false);
	}
	public static SymbolExpression address_of(Object operand) throws Exception {
		SymbolExpression soperand = sym_expression(operand);
		CType data_type = CTypeAnalyzer.get_value_type(soperand.get_data_type());
		data_type = t_factory.get_pointer_type(data_type);
		return SymbolUnaryExpression.create(data_type, SymbolOperator.create(COperator.address_of), soperand);
	}
	public static SymbolExpression dereference(Object operand) throws Exception {
		SymbolExpression soperand = sym_expression(operand);
		CType data_type = CTypeAnalyzer.get_value_type(soperand.get_data_type());
		if(data_type instanceof CPointerType) {
			data_type = ((CPointerType) data_type).get_pointed_type();
		}
		else if(data_type instanceof CArrayType) {
			data_type = ((CArrayType) data_type).get_element_type();
		}
		else {
			throw new IllegalArgumentException(data_type.generate_code());
		}
		return SymbolUnaryExpression.create(data_type, SymbolOperator.create(COperator.dereference), soperand);
	}
	public static SymbolExpression cast_expression(CType data_type, Object operand) throws Exception {
		return SymbolUnaryExpression.create(data_type, SymbolOperator.create(COperator.assign), sym_expression(operand));
	}
	public static SymbolExpression arith_add(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_add), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression arith_sub(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_sub), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression arith_mul(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_mul), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression arith_div(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_div), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression arith_mod(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_mod), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression bitws_and(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.bit_and), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression bitws_ior(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.bit_or), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression bitws_xor(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.bit_xor), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression bitws_lsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.left_shift), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression bitws_rsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.righ_shift), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression logic_and(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.logic_and), 
				sym_condition(loperand, true), sym_condition(roperand, true));
	}
	public static SymbolExpression logic_ior(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.logic_or), 
				sym_condition(loperand, true), sym_condition(roperand, true));
	}
	public static SymbolExpression greater_tn(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_tn), 
				sym_expression(roperand), sym_expression(loperand));
	}
	public static SymbolExpression greater_eq(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_eq), 
				sym_expression(roperand), sym_expression(loperand));
	}
	public static SymbolExpression smaller_tn(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_tn), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression smaller_eq(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_eq), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression equal_with(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.equal_with), 
				sym_expression(loperand), sym_expression(roperand));
	}
	public static SymbolExpression not_equals(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.not_equals), 
				sym_expression(loperand), sym_expression(roperand));
	}
	
}
