package com.jcsa.jcparse.lang.symbol;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
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
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * It provides instance-based implementation to construct symbolic node.
 * 
 * @author yukimula
 *
 */
public class SymbolNodeFactory {
	
	/* definitions */
	/** used to generate type for implied expression **/
	private CTypeFactory 	type_factory;
	/** used to implement the computation for sizeof **/
	private CRunTemplate 	ast_template;
	/** true to parse CirDefaultValue into constant **/
	private boolean			cir_optimize;
	
	/* constructor */
	/**
	 * create a factory instance to create symbolic expression in given context parameters
	 * @param ast_template
	 * @param cir_optimize
	 */
	public SymbolNodeFactory() {
		this.type_factory = new CTypeFactory();
		this.ast_template = null;
		this.cir_optimize = false;
	}
	
	/* configuration getters */
	/**
	 * @return used to generate type for implied expression
	 */
	public CTypeFactory get_type_factory() { return this.type_factory; }
	/**
	 * @return used to implement the computation for sizeof
	 */
	public CRunTemplate get_ast_template() { return this.ast_template; }
	/**
	 * @return true to parse CirDefaultValue into constant
	 */
	public boolean      get_cir_optimize() { return this.cir_optimize; }
	/**
	 * set the configuration parameters in the factory
	 * @param ast_template use to implement the computation on "sizeof"
	 * @param cir_optimize true to parse CirDefaultValue into constant
	 */
	public void config(CRunTemplate ast_template, boolean cir_optimize) {
		this.ast_template = ast_template;
		this.cir_optimize = cir_optimize;
	}
	
	
	/* parsing interfaces */
	/**
	 * @param source	{bool|char|short|int|long|float|double|String|CConstant}
	 * @return			{SymbolConstant|SymbolLiteral[for String input]}
	 * @throws Exception
	 */
	public SymbolExpression obj2constant(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof Boolean || source instanceof Character ||
				source instanceof Short || source instanceof Integer ||
				source instanceof Long || source instanceof Float ||
				source instanceof Double || source instanceof String) {
			return (SymbolExpression) SymbolParser.parser.parse_con(source);
		}
		else if(source instanceof CConstant) {
			return (SymbolExpression) SymbolParser.parser.parse_con(((CConstant) source).get_object());
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/**
	 * 	@param source
	 * 	@return	It parses the Java Object source to SymbolicExpression based on:<br>
	 * 			<br>
	 * 			(1)	{Boolean|Character|Short|Integer|Long|FLoat|Double}		==>	SymbolConstant		<br>
	 * 			(2)	{String}												==>	SymbolLiteral		<br>
	 * 			(3)	{CConstant}												==>	SymbolConstant		<br>
	 * 			(4)	{AstNode as expression}	+ {this.ast_template}			==>	SymbolExpression	<br>
	 * 			(5)	{CirStatement|CirExecution}								==>	SymbolIdentifier	<br>
	 * 			(6)	{CirNode as expression} + {this.cir_optimize}			==>	SymbolExpression	<br>
	 * 			(7)	{SymbolExpression}										==>	itself				<br>
	 * 			(8)	null or others											==> throw Exception		<br>
	 *	
	 * 	@throws Exception
	 */
	public SymbolExpression obj2expression(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof Boolean || source instanceof Character ||
				source instanceof Short || source instanceof Integer ||
				source instanceof Long || source instanceof Float ||
				source instanceof Double || source instanceof String) {
			return (SymbolExpression) SymbolParser.parser.parse_con(source);
		}
		else if(source instanceof CConstant) {
			return (SymbolExpression) SymbolParser.parser.parse_con(((CConstant) source).get_object());
		}
		else if(source instanceof AstNode) {
			return (SymbolExpression) SymbolParser.parser.parse_ast((AstNode) source, this.ast_template);
		}
		else if(source instanceof CirExecution) {
			return (SymbolExpression) SymbolParser.parser.parse_exe((CirExecution) source);
		}
		else if(source instanceof CirStatement) {
			CirStatement statement = (CirStatement) source;
			CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
			return (SymbolExpression) SymbolParser.parser.parse_exe(execution);
		}
		else if(source instanceof CirNode) {
			return (SymbolExpression) SymbolParser.parser.parse_cir((CirNode) source, this.cir_optimize);
		}
		else if(source instanceof SymbolExpression) {
			return (SymbolExpression) source;
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/**
	 * @param source
	 * @param value
	 * @return condition for ensuring expression of source as value [true|false]
	 * @throws Exception
	 */
	public SymbolExpression obj2condition(Object source, boolean value) throws Exception {
		SymbolExpression expression = this.obj2expression(source);
		return (SymbolExpression) SymbolParser.parser.parse_cod(expression, value);
	}
	
	/* basic expression */
	/**
	 * @param cname
	 * @return	{instance|parameter} --> identifier	[name#scope]
	 * 			{enumerator}		 --> constant	[integer]
	 * @throws Exception
	 */
	public SymbolExpression new_identifier(CName cname) throws Exception {
		if(cname == null) {
			throw new IllegalArgumentException("Invalid cname: null");
		}
		else if(cname instanceof CInstanceName) {
			return SymbolIdentifier.create(((CInstanceName) cname).get_instance().get_type(), cname);
		}
		else if(cname instanceof CParameterName) {
			return SymbolIdentifier.create(((CParameterName) cname).get_parameter().get_type(), cname);
		}
		else if(cname instanceof CEnumeratorName) {
			CConstant constant = new CConstant();
			constant.set_int(((CEnumeratorName) cname).get_enumerator().get_value());
			return SymbolConstant.create(constant);
		}
		else {
			throw new IllegalArgumentException(cname.getClass().getSimpleName());
		}
	}
	/**
	 * @param ast_source
	 * @return #ast.key
	 * @throws Exception
	 */
	public SymbolIdentifier new_identifier(AstExpression ast_source) throws Exception {
		return SymbolIdentifier.create(ast_source.get_value_type(), ast_source);
	}
	/**
	 * @param default_value
	 * @return default#cir_node.id
	 * @throws Exception
	 */
	public SymbolIdentifier new_identifier(CirDefaultValue default_value) throws Exception {
		return SymbolIdentifier.create(default_value.get_data_type(), default_value);
	}
	/**
	 * @param data_type
	 * @param def
	 * @return return#function.id
	 * @throws Exception
	 */
	public SymbolIdentifier new_identifier(CType data_type, CirFunctionDefinition def) throws Exception {
		return SymbolIdentifier.create(data_type, def);
	}
	/**
	 * @param data_type
	 * @param def
	 * @return special name
	 * @throws Exception
	 */
	public SymbolIdentifier new_identifier(CType data_type, String identifier) throws Exception {
		return SymbolIdentifier.create(data_type, identifier);
	}
	public SymbolConstant new_constant(boolean value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_bool(value);
		return SymbolConstant.create(constant);
	}
	public SymbolConstant new_constant(char value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_char(value);
		return SymbolConstant.create(constant);
	}
	public SymbolConstant new_constant(int value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_int(value);
		return SymbolConstant.create(constant);
	}
	public SymbolConstant new_constant(long value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_long(value);
		return SymbolConstant.create(constant);
	}
	public SymbolConstant new_constant(float value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_float(value);
		return SymbolConstant.create(constant);
	}
	public SymbolConstant new_constant(double value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_double(value);
		return SymbolConstant.create(constant);
	}
	public SymbolLiteral  new_literal(String literal) throws Exception {
		return SymbolLiteral.create(literal);
	}
	
	/* unary expression */
	/**
	 * @param operand
	 * @return -(operand) using type inference
	 * @throws Exception
	 */
	public SymbolUnaryExpression new_arith_neg(Object operand) throws Exception {
		SymbolExpression expression = this.obj2expression(operand);
		
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
		else if(type instanceof CUnionType) {
			
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
		
		return SymbolUnaryExpression.create(type, SymbolOperator.create(COperator.negative), expression);
	}
	/**
	 * @param operand
	 * @return ~(operand) with type inference
	 * @throws Exception
	 */
	public SymbolUnaryExpression new_bitws_rsv(Object operand) throws Exception {
		SymbolExpression expression = this.obj2expression(operand);
		
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
		else if(type instanceof CUnionType) {
			
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
		
		return SymbolUnaryExpression.create(type, SymbolOperator.create(COperator.bit_not), expression);
	}
	/**
	 * @param operand
	 * @return !(operand) [bool]; (operand == 0) [int|pointer]; (operand == 0.0) [real]
	 * @throws Exception
	 */
	public SymbolExpression new_logic_not(Object operand) throws Exception {
		SymbolExpression expression = this.obj2expression(operand);
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(type)) {
			return SymbolUnaryExpression.create(CBasicTypeImpl.bool_type, 
					SymbolOperator.create(COperator.logic_not), expression);
		}
		else if(CTypeAnalyzer.is_integer(type) || CTypeAnalyzer.is_pointer(type)) {
			CConstant constant = new CConstant(); constant.set_int(0);
			return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
					SymbolOperator.create(COperator.equal_with), expression, 
					SymbolConstant.create(constant));
		}
		else if(CTypeAnalyzer.is_real(type)) {
			CConstant constant = new CConstant(); constant.set_double(0);
			return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
					SymbolOperator.create(COperator.equal_with), expression, 
					SymbolConstant.create(constant));
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
	}
	/**
	 * @param operand
	 * @return &operand with type inference and checking
	 * @throws Exception
	 */
	public SymbolUnaryExpression new_address_of(Object operand) throws Exception {
		SymbolExpression expression = this.obj2expression(operand);
		if(expression.is_reference()) {
			CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			type = this.type_factory.get_pointer_type(type);
			return SymbolUnaryExpression.create(type, SymbolOperator.create(COperator.address_of), expression);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.getClass().getSimpleName());
		}
	}
	/**
	 * @param operand 
	 * @return *operand with type inference
	 * @throws Exception 
	 */
	public SymbolUnaryExpression new_dereference(Object operand) throws Exception {
		SymbolExpression expression = this.obj2expression(operand);
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(type instanceof CArrayType) {
			type = ((CArrayType) type).get_element_type();
		}
		else if(type instanceof CPointerType) {
			type = ((CPointerType) type).get_pointed_type();
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
		type = CTypeAnalyzer.get_value_type(type);
		return SymbolUnaryExpression.create(type, SymbolOperator.
				create(COperator.dereference), expression);
	}
	/**
	 * @param type
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	public SymbolUnaryExpression new_type_casting(CType type, Object operand) throws Exception {
		SymbolExpression expression = this.obj2expression(operand);
		return SymbolUnaryExpression.create(type, SymbolOperator.create(COperator.assign), expression);
	}
	
	/* binary expression */
	public SymbolExpression new_arith_add(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_add), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_arith_sub(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_sub), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_arith_mul(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_mul), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_arith_div(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_div), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_arith_mod(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_mod), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_bitws_and(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.bit_and), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_bitws_ior(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.bit_or), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_bitws_xor(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.bit_xor), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_bitws_lsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.left_shift), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_bitws_rsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.righ_shift), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_logic_and(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.logic_and), 
				this.obj2condition(loperand, true), this.obj2condition(roperand, true));
	}
	public SymbolExpression new_logic_ior(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.logic_or), 
				this.obj2condition(loperand, true), this.obj2condition(roperand, true));
	}
	public SymbolExpression new_greater_tn(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_tn), 
				this.obj2expression(roperand), this.obj2expression(loperand));
	}
	public SymbolExpression new_greater_eq(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_eq), 
				this.obj2expression(roperand), this.obj2expression(loperand));
	}
	public SymbolExpression new_smaller_tn(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_tn), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_smaller_eq(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_eq), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_equal_with(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.equal_with), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	public SymbolExpression new_not_equals(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.not_equals), 
				this.obj2expression(loperand), this.obj2expression(roperand));
	}
	
	/* special expressions */
	/**
	 * @param operands
	 * @return
	 * @throws Exception
	 */
	public SymbolInitializerList new_initializer_list(Iterable<Object> elements) throws Exception {
		List<SymbolExpression> elist = new ArrayList<SymbolExpression>();
		for(Object element : elements) elist.add(this.obj2expression(element));
		return SymbolInitializerList.create(null, elist);
	}
	/**
	 * @param function
	 * @param arguments
	 * @return
	 * @throws Exception
	 */
	public SymbolCallExpression new_call_expression(Object function, Iterable<Object> arguments) throws Exception {
		SymbolExpression sfunction = obj2expression(function);
		List<SymbolExpression> elist = new ArrayList<SymbolExpression>();
		for(Object argument : arguments) elist.add(obj2expression(argument));
		
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
	/**
	 * @param body
	 * @param field
	 * @return
	 * @throws Exception
	 */
	public SymbolFieldExpression new_field_expression(Object body, String field) throws Exception {
		SymbolExpression sbody = obj2expression(body);
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
	
}
