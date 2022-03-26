package com.jcsa.jcparse.lang.symbol;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFieldBody;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	It implements the creation and parsing of SymbolNode.
 * 	
 * 	@author yukimula
 *
 */
public final class SymbolFactory {
	
	/* parameters */
	/** the type factory to create array type and the others **/
	public static final CTypeFactory type_factory = new CTypeFactory();
	
	/* definitions */
	/** the template used to support sizeof-operation **/
	protected CRunTemplate 	template;
	/** true if to transform the default value based on their data types **/
	protected boolean		optimize;
	/**
	 * private constructor for the symbol node generation and parsing
	 */
	private SymbolFactory() { this.template = null; this.optimize = false; }
	
	/* singleton mode */
	/** the factory instance for singleton mode **/
	private static final SymbolFactory symb_factory = new SymbolFactory();
	/**
	 * It sets the parameters used to parsing algorithm of this instance
	 * @param template	the template used to support sizeof-operation
	 * @param optimize	true if to transform the default value based on their data types
	 */
	private void configure(CRunTemplate template, boolean optimize) {
		this.template = template; this.optimize = optimize;
	}
	/**
	 * It sets the parameters used to parsing algorithm of this singleton instance
	 * @param template	the template used to support sizeof-operation
	 * @param optimize	true if to transform the default value based on their data types
	 */
	public static void set_config(CRunTemplate template, boolean optimize) {
		symb_factory.configure(template, optimize);
	}
	/**
	 * @return the template used to support sizeof-operation
	 */
	public static CRunTemplate 	get_template() 	{ return symb_factory.template; }
	/**
	 * @return true if to transform the default value based on their data types
	 */
	public static boolean		is_optimized()	{ return symb_factory.optimize; }
	
	/* type classifier */
	/**
	 * @param type
	 * @return the value type without qualifiers
	 */
	public static CType   get_type(CType type) {
		if(type == null) {
			return CBasicTypeImpl.void_type;
		}
		else {
			while(type instanceof CQualifierType) {
				type = ((CQualifierType) type).get_reference();
			}
			return type;
		}
	}
	/**
	 * @param type
	 * @return void | null
	 */
	public static boolean is_void(CType type) {
		type = get_type(type);
		if(type == null) {
			return true;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_void:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return bool
	 */
	public static boolean is_bool(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return char | uchar
	 */
	public static boolean is_char(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_char:
			case c_uchar:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return char|short|int|long|llong|enum
	 */
	public static boolean is_sign(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_char:
			case c_short:
			case c_int:
			case c_long:
			case c_llong:	return true;
			default:		return false;
			}
		}
		else if(type instanceof CEnumType) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return uchar|ushort|uint|ulong|ullong
	 */
	public static boolean is_usig(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_uchar:
			case c_ushort:
			case c_uint:
			case c_ulong:
			case c_ullong:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return char|uchar|short|ushort|int|uint|long|ulong|llong|ullong
	 */
	public static boolean is_numb(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:	return true;
			default:		return false;
			}
		}
		else if(type instanceof CEnumType) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return float | double | ldouble
	 */
	public static boolean is_real(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_float:
			case c_double:
			case c_ldouble:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return array | pointer
	 */
	public static boolean is_addr(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CArrayType || type instanceof CPointerType) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return struct | union | function
	 */
	public static boolean is_auto(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CStructType || 
				type instanceof CUnionType ||
				type instanceof CFunctionType) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return void
	 */
	public static boolean is_void(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_void(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return void | logic | relational
	 */
	public static boolean is_bool(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_bool(expression.get_data_type());		
		}
	}
	/**
	 * @param expression
	 * @return char | uchar
	 */
	public static boolean is_char(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_char(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return char|short|int|long|llong
	 */
	public static boolean is_sign(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_sign(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return uchar|ushort|uint|ulong|ullong
	 */
	public static boolean is_usig(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_usig(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return char|uchar|short|ushort|int|uint|long|ulong|llong|ullong
	 */
	public static boolean is_numb(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_numb(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return float | double | ldouble
	 */
	public static boolean is_real(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_real(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return array | pointer
	 */
	public static boolean is_addr(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_addr(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return function | struct | union
	 */
	public static boolean is_auto(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_auto(expression.get_data_type());
		}
	}
	
	/* parsing methods */
	/**
	 * @param source	[Boolean|Character|Short|Integer|Long|Float|Double|CConstant]
	 * @return			the constant that the source represents
	 * @throws Exception
	 */
	public static SymbolConstant 	sym_constant(Object source) throws Exception {
		return SymbolParser.parse_to_cons(source);
	}
	/**
	 * @param source	[Boolean|Character|Short|Integer|Long|Float|Double|CConstant|String
	 * 					|SymbolExpression|AstNode|CirNode|CirExecution|CName|CType]
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression	sym_expression(Object source) throws Exception {
		return SymbolParser.parse_to_expr(source);
	}
	/**
	 * @param source	[Boolean|Character|Short|Integer|Long|Float|Double|CConstant|String
	 * 					|SymbolExpression|AstNode|CirNode|CirExecution|CName|CType]
	 * @param value		the value being expected for the boolean representation
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression	sym_condition(Object source, boolean value) throws Exception {
		return SymbolParser.parse_to_bool(source, value);
	}
	
	/* factory methods */
	/**
	 * @param type	the data type of the identifier expression
	 * @param name	the simple name of the identifier
	 * @param scope	the scope where the name is defined
	 * @return		name#scope
	 * @throws Exception
	 */
	public static SymbolIdentifier			identifier(CType type, String name, Object scope) throws Exception {
		return SymbolIdentifier.create(type, name, scope);
	}
	/**
	 * @param constant	[Boolean|Character|Short|Integer|Long|Float|Double|CConstant]
	 * @return
	 * @throws Exception
	 */
	public static SymbolConstant			constant(Object constant) throws Exception {
		return sym_constant(constant);
	}
	/**
	 * @param literal
	 * @return
	 * @throws Exception
	 */
	public static SymbolLiteral				literal(Object literal) throws Exception {
		return SymbolLiteral.create(literal.toString());
	}
	/**
	 * @param cast_type	the type to cast the sub-operand
	 * @param operand	the unary operand
	 * @return
	 * @throws Exception
	 */
	public static SymbolCastExpression 		cast_expression(CType cast_type, Object operand) throws Exception {
		return SymbolCastExpression.create(SymbolType.create(cast_type), sym_expression(operand));
	}
	/**	
	 * @param body	the symbolic expression as the body of field-expression
	 * @param field	the field to derive the body's expression
	 * @return
	 * @throws Exception
	 */
	public static SymbolFieldExpression 	field_expression(Object body, String field) throws Exception {
		/* recursively translate the body-field */
		SymbolExpression sbody = sym_expression(body);
		SymbolField sfield = SymbolField.create(field);
		
		/* derive the struct-union data type */
		CType data_type = sbody.get_data_type();
		if(data_type instanceof CPointerType) {
			data_type = ((CPointerType) data_type).get_pointed_type();
		}
		else if(data_type instanceof CArrayType) {
			data_type = ((CArrayType) data_type).get_element_type();
		}
		data_type = SymbolFactory.get_type(data_type);
		
		/* derive the field-body and field type */
		CFieldBody fields;
		if(data_type instanceof CStructType) {
			fields = ((CStructType) data_type).get_fields();
		}
		else if(data_type instanceof CUnionType) {
			fields = ((CUnionType) data_type).get_fields();
		}
		else {
			throw new IllegalArgumentException(data_type.generate_code());
		}
		data_type = fields.get_field(field).get_type();
		
		return SymbolFieldExpression.create(data_type, sbody, sfield);
	}
	/**
	 * @param condition
	 * @param t_operand
	 * @param f_operand
	 * @return
	 * @throws Exception
	 */
	public static SymbolIfElseExpression 	ifte_expression(CType data_type, 
			Object condition, Object t_operand, Object f_operand) throws Exception {
		SymbolExpression cond = sym_condition(condition, true);
		SymbolExpression tval = sym_expression(t_operand);
		SymbolExpression fval = sym_expression(f_operand);
		return SymbolIfElseExpression.create(data_type, cond, tval, fval);
	}
	/**
	 * @param elements
	 * @return
	 * @throws Exception
	 */
	public static SymbolInitializerList 	initializer_list(Iterable<Object> elements) throws Exception {
		List<SymbolExpression> elist = new ArrayList<SymbolExpression>();
		if(elements != null) {
			for(Object element : elements) {
				elist.add(sym_expression(element));
			}
		}
		return SymbolInitializerList.create(elist);
	}
	/**
	 * @param elements
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpressionList 		expression_list(Iterable<Object> elements) throws Exception {
		List<SymbolExpression> elist = new ArrayList<SymbolExpression>();
		if(elements != null) {
			for(Object element : elements) {
				elist.add(sym_expression(element));
			}
		}
		return SymbolExpressionList.create(elist);
	}
	/**
	 * @param function
	 * @param arguments
	 * @return
	 * @throws Exception
	 */
	public static SymbolCallExpression 		call_expression(Object function, Iterable<Object> arguments) throws Exception {
		/* recursively construct function-arguments */
		SymbolExpression func = sym_expression(function);
		List<SymbolExpression> list = new ArrayList<SymbolExpression>();
		if(arguments != null) {
			for(Object argument : arguments) {
				list.add(sym_expression(argument));
			}
		}
		SymbolArgumentList alist = SymbolArgumentList.create(list);
		
		/* derive the function type and its return type */
		CType data_type = func.get_data_type();
		if(data_type instanceof CArrayType) {
			data_type = ((CArrayType) data_type).get_element_type();
		}
		else if(data_type instanceof CPointerType) {
			data_type = ((CPointerType) data_type).get_pointed_type();
		}
		data_type = SymbolFactory.get_type(data_type);
		if(data_type instanceof CFunctionType) {
			data_type = ((CFunctionType) data_type).get_return_type();
		}
		else {
			throw new IllegalArgumentException(data_type.generate_code());
		}
		
		/* construct the calling-expression with type finally */
		return SymbolCallExpression.create(data_type, func, alist);
	}
	/**
	 * @param operand
	 * @return -operand
	 * @throws Exception
	 */
	public static SymbolUnaryExpression		arith_neg(Object operand) throws Exception {
		SymbolExpression uoperand = sym_expression(operand);
		CType data_type = uoperand.get_data_type();
		if(SymbolFactory.is_bool(data_type)) {
			data_type = CBasicTypeImpl.int_type;
			uoperand = SymbolFactory.cast_expression(data_type, uoperand);
			return SymbolUnaryExpression.create(data_type, COperator.negative, uoperand);
		}
		else if(SymbolFactory.is_numb(data_type) || 
				SymbolFactory.is_real(data_type) ||
				SymbolFactory.is_addr(data_type)) {
			return SymbolUnaryExpression.create(data_type, COperator.negative, uoperand);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + data_type.generate_code());
		}
	}
	/**
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	public static SymbolUnaryExpression		bitws_rsv(Object operand) throws Exception {
		SymbolExpression uoperand = sym_expression(operand);
		CType data_type = uoperand.get_data_type();
		if(SymbolFactory.is_bool(data_type)) {
			data_type = CBasicTypeImpl.int_type;
			uoperand = SymbolFactory.cast_expression(data_type, uoperand);
			return SymbolUnaryExpression.create(data_type, COperator.bit_not, uoperand); 
		}
		else if(SymbolFactory.is_numb(data_type)) {
			return SymbolUnaryExpression.create(data_type, COperator.bit_not, uoperand);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + data_type.generate_code());
		}
	}
	/**
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	public static SymbolUnaryExpression		logic_not(Object operand) throws Exception {
		SymbolExpression uoperand = sym_condition(operand, true);
		return SymbolUnaryExpression.create(CBasicTypeImpl.bool_type, COperator.logic_not, uoperand);
	}
	/**
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	public static SymbolUnaryExpression		address_of(Object operand) throws Exception {
		SymbolExpression uoperand = sym_expression(operand);
		CType data_type = uoperand.get_data_type();
		data_type = type_factory.get_pointer_type(data_type);
		return SymbolUnaryExpression.create(data_type, COperator.address_of, uoperand);
	}
	/**
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	public static SymbolUnaryExpression		dereference(Object operand) throws Exception {
		SymbolExpression uoperand = sym_expression(operand);
		CType data_type = uoperand.get_data_type();
		if(data_type instanceof CArrayType) {
			data_type = ((CArrayType) data_type).get_element_type();
		}
		else if(data_type instanceof CPointerType) {
			data_type = ((CPointerType) data_type).get_pointed_type();
		}
		else {
			throw new IllegalArgumentException(data_type.generate_code());
		}
		return SymbolUnaryExpression.create(data_type, COperator.dereference, uoperand);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolArithExpression		arith_add(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolArithExpression.create(type, COperator.arith_add, lvalue, rvalue);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolArithExpression		arith_sub(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolArithExpression.create(type, COperator.arith_sub, lvalue, rvalue);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolArithExpression		arith_mul(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolArithExpression.create(type, COperator.arith_mul, lvalue, rvalue);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolArithExpression		arith_div(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolArithExpression.create(type, COperator.arith_div, lvalue, rvalue);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolArithExpression		arith_mod(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolArithExpression.create(type, COperator.arith_mod, lvalue, rvalue);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolBitwsExpression		bitws_and(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolBitwsExpression.create(type, COperator.bit_and, lvalue, rvalue);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolBitwsExpression		bitws_ior(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolBitwsExpression.create(type, COperator.bit_or, lvalue, rvalue);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolBitwsExpression		bitws_xor(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolBitwsExpression.create(type, COperator.bit_xor, lvalue, rvalue);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolBitwsExpression		bitws_lsh(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolBitwsExpression.create(type, COperator.left_shift, lvalue, rvalue);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolBitwsExpression		bitws_rsh(CType type, Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolBitwsExpression.create(type, COperator.righ_shift, lvalue, rvalue);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolLogicExpression		logic_and(Object loperand, Object roperand) throws Exception {
		SymbolExpression lcondition = sym_condition(loperand, true);
		SymbolExpression rcondition = sym_condition(roperand, true);
		return SymbolLogicExpression.create(COperator.logic_and, lcondition, rcondition);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolLogicExpression		logic_ior(Object loperand, Object roperand) throws Exception {
		SymbolExpression lcondition = sym_condition(loperand, true);
		SymbolExpression rcondition = sym_condition(roperand, true);
		return SymbolLogicExpression.create(COperator.logic_or, lcondition, rcondition);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolLogicExpression		logic_imp(Object loperand, Object roperand) throws Exception {
		SymbolExpression lcondition = sym_condition(loperand, true);
		SymbolExpression rcondition = sym_condition(roperand, true);
		return SymbolLogicExpression.create(COperator.positive, lcondition, rcondition);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolRelationExpression	greater_tn(Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolRelationExpression.create(COperator.greater_tn, lvalue, rvalue);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolRelationExpression	greater_eq(Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolRelationExpression.create(COperator.greater_eq, lvalue, rvalue);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolRelationExpression	smaller_tn(Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolRelationExpression.create(COperator.smaller_tn, lvalue, rvalue);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolRelationExpression	smaller_eq(Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolRelationExpression.create(COperator.smaller_eq, lvalue, rvalue);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolRelationExpression	equal_with(Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolRelationExpression.create(COperator.equal_with, lvalue, rvalue);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	public static SymbolRelationExpression	not_equals(Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand), rvalue = sym_expression(roperand);
		return SymbolRelationExpression.create(COperator.not_equals, lvalue, rvalue);
	}
	/**
	 * @param data_type
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static SymbolIdentifier			variable(CType data_type, String name) {
		return SymbolIdentifier.create(data_type, name, null);	
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand := roperand
	 * @throws Exception
	 */
	public static SymbolAssignExpression	exp_assign(Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand);
		SymbolExpression rvalue = sym_expression(roperand);
		return SymbolAssignExpression.create(lvalue.get_data_type(), COperator.assign, lvalue, rvalue);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand <- roperand
	 * @throws Exception
	 */
	public static SymbolAssignExpression	imp_assign(Object loperand, Object roperand) throws Exception {
		SymbolExpression lvalue = sym_expression(loperand);
		SymbolExpression rvalue = sym_expression(roperand);
		return SymbolAssignExpression.create(lvalue.get_data_type(), COperator.increment, lvalue, rvalue);
	}
	
}
