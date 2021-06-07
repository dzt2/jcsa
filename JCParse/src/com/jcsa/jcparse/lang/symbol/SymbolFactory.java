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
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * It implements both instance and static interfaces for constructing symbolic nodes.
 * 
 * @author yukimula
 *
 */
public class SymbolFactory {
	
	/* definitions */
	/** it is used to support the inference of data type in C language **/
	private static final CTypeFactory 	type_factory = new CTypeFactory();
	/** used to implement the computation for sizeof **/
	private CRunTemplate 				ast_template;
	/** true to parse CirDefaultValue into constants **/
	private boolean						cir_optimize;
	/**
	 * create a default factory for constructing symbolic node with ast_template as 
	 * null and C-intermediate representative optimization as closed configuration.
	 */
	public SymbolFactory() { this.ast_template = null; this.cir_optimize = false; }
	
	/* instance parameter getters and setter */
	/**
	 * @return used to implement the computation for sizeof
	 */
	public CRunTemplate get_ast_template() { return this.ast_template; }
	/**
	 * @return true to parse CirDefaultValue into constants
	 */
	public boolean		get_cir_optimize() { return this.cir_optimize; }
	/**
	 * @param ast_template used to implement the computation for sizeof
	 * @param cir_optimize true to parse CirDefaultValue into constants
	 */
	public void			set_configuration(CRunTemplate ast_template, boolean cir_optimize) {
		this.ast_template = ast_template;
		this.cir_optimize = cir_optimize;
	}
	
	/* instance parsers */
	/**
	 * 	@param 	source	Java Object to be parsed as SymbolConstant | SymbolLiteral	<br>
	 * 	<code>
	 * 	@return	Inference Rules.									<br>
	 * 			1.	{bool}			-->	SymbolConstant[Boolean]		<br>
	 * 			2.	{char}			-->	SymbolConstant[Character]	<br>
	 * 			3.	{short|int}		-->	SymbolConstant[Integer]		<br>
	 * 			4.	{long}			-->	SymbolConstant[Long]		<br>
	 * 			5.	{float}			-->	SymbolConstant[Float]		<br>
	 * 			6.	{double}		-->	SymbolConstant[Double]		<br>
	 * 			7.	{CConstant}		-->	SymbolConstant[Any]			<br>
	 * 			8.	{String}		-->	SymbolLiteral[String]		<br>
	 * 	</code>
	 * 	<br>
	 * 	@throws Exception
	 */
	public SymbolExpression parse_to_constant(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof Boolean || source instanceof Character || source instanceof Short ||
				source instanceof Integer || source instanceof Double || source instanceof Float) {
			return (SymbolConstant) SymbolParser.parser.parse_con(source);
		}
		else if(source instanceof String) {
			return (SymbolLiteral) SymbolParser.parser.parse_con(source);
		}
		else if(source instanceof CConstant) {
			return (SymbolExpression) SymbolParser.parser.parse_con(((CConstant) source).get_object());
		}
		else {
			throw new IllegalArgumentException("Invalid source: " + source.getClass().getSimpleName());
		}
	}
	/**
	 * 	@param 	source	Java Object to be parsed as SymbolConstant | SymbolLiteral	<br>
	 * 	<code>
	 * 	@return	Inference Rules.									<br>
	 * 			1.	{bool}						-->	SymbolConstant[Boolean]		<br>
	 * 			2.	{char}						-->	SymbolConstant[Character]	<br>
	 * 			3.	{short|int}					-->	SymbolConstant[Integer]		<br>
	 * 			4.	{long}						-->	SymbolConstant[Long]		<br>
	 * 			5.	{float}						-->	SymbolConstant[Float]		<br>
	 * 			6.	{double}					-->	SymbolConstant[Double]		<br>
	 * 			7.	{CConstant}					-->	SymbolConstant[Any]			<br>
	 * 			8.	{String}					-->	SymbolLiteral[String]		<br>
	 * 			9.	{AstNode}|[ast_template]	-->	SymbolExpression{AstNode}	<br>
	 * 			10.	{CirStatement|CirExecution}	-->	SymbolIdentifier{do#exec}	<br>
	 * 			11.	{CirNode}|[cir_optimize]	-->	SymbolExpression{CirNode}	<br>
	 * 			12.	{SymbolExpression}			-->	itself						<br>
	 * 			13.	{null|otherwise}			-->	throw Exception				<br>
	 * 	</code>
	 * 	<br>
	 * 	@throws Exception
	 */
	public SymbolExpression parse_to_expression(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof Boolean || source instanceof Character || source instanceof Short ||
				source instanceof Integer || source instanceof Double || source instanceof Float) {
			return (SymbolConstant) SymbolParser.parser.parse_con(source);
		}
		else if(source instanceof String) {
			return (SymbolLiteral) SymbolParser.parser.parse_con(source);
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
			throw new IllegalArgumentException("Invalid source: " + source.getClass().getSimpleName());
		}
	}
	/**
	 * @param source
	 * @param value
	 * @return symbolic condition (logical) as boolean requiring expression of source being the value
	 * @throws Exception
	 */
	public SymbolExpression parse_to_condition(Object source, boolean value) throws Exception {
		SymbolExpression expression = this.parse_to_expression(source);
		return (SymbolExpression) SymbolParser.parser.parse_cod(expression, value);
	}
	
	/* instance constructors */
	/**
	 * @param cname
	 * @return	1. CInstanceName	-->	{instance.type, 	cname.name#cname.scope}
	 * 			2. CParameterName	-->	{parameter.type,	cname.name#cname.scope}
	 * 			3. CEnumeratorName	-->	SymbolConstant[enumerator.value]
	 * @throws Exception
	 */
	public SymbolExpression new_identifier(CName cname) throws Exception {
		if(cname == null) {
			throw new IllegalArgumentException("Invalid cname: null");
		}
		else if(cname instanceof CInstanceName) {
			CInstance instance = ((CInstanceName) cname).get_instance();
			return SymbolIdentifier.create(instance.get_type(), cname);
		}
		else if(cname instanceof CParameterName) {
			CInstance instance = ((CParameterName) cname).get_parameter();
			return SymbolIdentifier.create(instance.get_type(), cname);
		}
		else if(cname instanceof CEnumeratorName) {
			return (SymbolExpression) SymbolParser.parser.parse_con(Integer.valueOf(
							((CEnumeratorName) cname).get_enumerator().get_value()));
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
	/**
	 * @param value
	 * @return constant[bool]
	 * @throws Exception
	 */
	public SymbolConstant new_constant(boolean value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_bool(value);
		return SymbolConstant.create(constant);
	}
	/**
	 * @param value
	 * @return constant[char]
	 * @throws Exception
	 */
	public SymbolConstant new_constant(char value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_char(value);
		return SymbolConstant.create(constant);
	}
	/**
	 * @param value
	 * @return constant[int]
	 * @throws Exception
	 */
	public SymbolConstant new_constant(int value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_int(value);
		return SymbolConstant.create(constant);
	}
	/**
	 * @param value
	 * @return constant[long]
	 * @throws Exception
	 */
	public SymbolConstant new_constant(long value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_long(value);
		return SymbolConstant.create(constant);
	}
	/**
	 * @param value
	 * @return constant[float]
	 * @throws Exception
	 */
	public SymbolConstant new_constant(float value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_float(value);
		return SymbolConstant.create(constant);
	}
	/**
	 * @param value
	 * @return constant[double]
	 * @throws Exception
	 */
	public SymbolConstant new_constant(double value) throws Exception {
		CConstant constant = new CConstant();
		constant.set_double(value);
		return SymbolConstant.create(constant);
	}
	/**
	 * @param literal
	 * @return literal[String]
	 * @throws Exception
	 */
	public SymbolLiteral  new_literal(String literal) throws Exception {
		return SymbolLiteral.create(literal);
	}
	/**
	 * @param operand
	 * @return -(operand) using type inference
	 * @throws Exception
	 */
	public SymbolUnaryExpression new_arith_neg(Object operand) throws Exception {
		SymbolExpression expression = this.parse_to_expression(operand);
		
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
		SymbolExpression expression = this.parse_to_expression(operand);
		
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
		SymbolExpression expression = this.parse_to_expression(operand);
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
		SymbolExpression expression = this.parse_to_expression(operand);
		if(expression.is_reference()) {
			CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			type = type_factory.get_pointer_type(type);
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
		SymbolExpression expression = this.parse_to_expression(operand);
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
		SymbolExpression expression = this.parse_to_expression(operand);
		return SymbolUnaryExpression.create(type, SymbolOperator.create(COperator.assign), expression);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand + roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_arith_add(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_add), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand - roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_arith_sub(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_sub), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand * roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_arith_mul(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_mul), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand / roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_arith_div(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_div), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand % roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_arith_mod(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.arith_mod), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand & roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_bitws_and(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.bit_and), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand | roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_bitws_ior(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.bit_or), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand ^ roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_bitws_xor(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.bit_xor), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand << roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_bitws_lsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.left_shift), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand >> roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_bitws_rsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(data_type, 
				SymbolOperator.create(COperator.righ_shift), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return bool(loperand) && bool(roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_logic_and(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.logic_and), 
				this.parse_to_condition(loperand, true), 
				this.parse_to_condition(roperand, true));
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return bool(loperand) || bool(roperand)
	 * @throws Exception
	 */
	public SymbolExpression new_logic_ior(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.logic_or), 
				this.parse_to_condition(loperand, true), 
				this.parse_to_condition(roperand, true));
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return roperand < loperand
	 * @throws Exception
	 */
	public SymbolExpression new_greater_tn(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_tn), 
				this.parse_to_expression(roperand), 
				this.parse_to_expression(loperand));
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return roperand <= loperand
	 * @throws Exception
	 */
	public SymbolExpression new_greater_eq(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_eq), 
				this.parse_to_expression(roperand), 
				this.parse_to_expression(loperand));
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand < roperand
	 * @throws Exception
	 */
	public SymbolExpression new_smaller_tn(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_tn), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand <= roperand
	 * @throws Exception
	 */
	public SymbolExpression new_smaller_eq(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.smaller_eq), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand == roperand
	 * @throws Exception
	 */
	public SymbolExpression new_equal_with(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.equal_with), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand != roperand
	 * @throws Exception
	 */
	public SymbolExpression new_not_equals(Object loperand, Object roperand) throws Exception {
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, 
				SymbolOperator.create(COperator.not_equals), 
				this.parse_to_expression(loperand), 
				this.parse_to_expression(roperand));
	}
	/**
	 * @param operands
	 * @return {x1, x2, ..., xn}
	 * @throws Exception
	 */
	public SymbolInitializerList new_initializer_list(Iterable<Object> elements) throws Exception {
		List<SymbolExpression> elist = new ArrayList<SymbolExpression>();
		for(Object element : elements) {
			elist.add(this.parse_to_expression(element));
		}
		return SymbolInitializerList.create(null, elist);
	}
	/**
	 * @param function
	 * @param arguments
	 * @return
	 * @throws Exception
	 */
	public SymbolCallExpression new_call_expression(Object function, Iterable<Object> arguments) throws Exception {
		SymbolExpression sfunction = parse_to_expression(function);
		List<SymbolExpression> elist = new ArrayList<SymbolExpression>();
		for(Object argument : arguments) {
			elist.add(parse_to_expression(argument));
		}
		
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
	 * @return body.field
	 * @throws Exception
	 */
	public SymbolFieldExpression new_field_expression(Object body, String field) throws Exception {
		SymbolExpression sbody = this.parse_to_expression(body);
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
	
	/* static singleton instance */
	/** singleton for static constructors **/
	public static final SymbolFactory factory = new SymbolFactory();
	/**
	 * @return used to implement the computation for sizeof
	 */
	public static CRunTemplate	ast_template() { return factory.ast_template; }
	/**
	 * @return true to parse CirDefaultValue into constant
	 */
	public static boolean		cir_optimize() { return factory.cir_optimize; }
	/**
	 * set the configuration parameters in the factory
	 * @param ast_template use to implement the computation on "sizeof"
	 * @param cir_optimize true to parse CirDefaultValue into constant
	 */
	public static void config(CRunTemplate ast_template, boolean cir_optimize) {
		factory.set_configuration(ast_template, cir_optimize);
	}
	
	/* static parser interfaces */
	/**
	 * 	@param 	source	Java Object to be parsed as SymbolConstant | SymbolLiteral	<br>
	 * 	<code>
	 * 	@return	Inference Rules.									<br>
	 * 			1.	{bool}			-->	SymbolConstant[Boolean]		<br>
	 * 			2.	{char}			-->	SymbolConstant[Character]	<br>
	 * 			3.	{short|int}		-->	SymbolConstant[Integer]		<br>
	 * 			4.	{long}			-->	SymbolConstant[Long]		<br>
	 * 			5.	{float}			-->	SymbolConstant[Float]		<br>
	 * 			6.	{double}		-->	SymbolConstant[Double]		<br>
	 * 			7.	{CConstant}		-->	SymbolConstant[Any]			<br>
	 * 			8.	{String}		-->	SymbolLiteral[String]		<br>
	 * 	</code>
	 * 	<br>
	 * 	@throws Exception
	 */
	public static SymbolExpression sym_constant(Object source) throws Exception {
		return factory.parse_to_constant(source);
	}
	/**
	 * 	@param 	source	Java Object to be parsed as SymbolConstant | SymbolLiteral	<br>
	 * 	<code>
	 * 	@return	Inference Rules.									<br>
	 * 			1.	{bool}						-->	SymbolConstant[Boolean]		<br>
	 * 			2.	{char}						-->	SymbolConstant[Character]	<br>
	 * 			3.	{short|int}					-->	SymbolConstant[Integer]		<br>
	 * 			4.	{long}						-->	SymbolConstant[Long]		<br>
	 * 			5.	{float}						-->	SymbolConstant[Float]		<br>
	 * 			6.	{double}					-->	SymbolConstant[Double]		<br>
	 * 			7.	{CConstant}					-->	SymbolConstant[Any]			<br>
	 * 			8.	{String}					-->	SymbolLiteral[String]		<br>
	 * 			9.	{AstNode}|[ast_template]	-->	SymbolExpression{AstNode}	<br>
	 * 			10.	{CirStatement|CirExecution}	-->	SymbolIdentifier{do#exec}	<br>
	 * 			11.	{CirNode}|[cir_optimize]	-->	SymbolExpression{CirNode}	<br>
	 * 			12.	{SymbolExpression}			-->	itself						<br>
	 * 			13.	{null|otherwise}			-->	throw Exception				<br>
	 * 	</code>
	 * 	<br>
	 * 	@throws Exception
	 */
	public static SymbolExpression sym_expression(Object source) throws Exception {
		return factory.parse_to_expression(source);
	}
	/**
	 * @param source
	 * @param value
	 * @return symbolic condition (logical) as boolean requiring expression of source being the value
	 * @throws Exception
	 */
	public static SymbolExpression sym_condition(Object source, boolean value) throws Exception {
		return factory.parse_to_condition(source, value);
	}
	
	/* static node constructors */
	/**
	 * @param cname
	 * @return name#scope
	 * @throws Exception
	 */
	public static SymbolExpression identifier(CName cname) throws Exception {
		return factory.new_identifier(cname);
	}
	/**
	 * @param ast_reference
	 * @return #ast.key
	 * @throws Exception
	 */
	public static SymbolExpression identifier(AstExpression ast_reference) throws Exception {
		return factory.new_identifier(ast_reference);
	}
	/**
	 * @param default_value
	 * @return default#cir_node.id
	 * @throws Exception
	 */
	public static SymbolExpression identifier(CirDefaultValue default_value) throws Exception {
		return factory.new_identifier(default_value);
	}
	/**
	 * @param data_type
	 * @param def
	 * @return return#function.id
	 * @throws Exception
	 */
	public static SymbolExpression identifier(CType data_type, CirFunctionDefinition def) throws Exception {
		return factory.new_identifier(data_type, def);
	}
	/**
	 * @param data_type
	 * @param def
	 * @return special name
	 * @throws Exception
	 */
	public static SymbolExpression identifier(CType data_type, String identifier) throws Exception {
		return factory.new_identifier(data_type, identifier);
	}
	/**
	 * @param value {bool|char|short|int|long|float|double}
	 * @return 
	 * @throws Exception
	 */
	public static SymbolConstant constant(Object source) throws Exception {
		if(source instanceof Boolean) {
			return factory.new_constant(((Boolean) source).booleanValue());
		}
		else if(source instanceof Character) {
			return factory.new_constant(((Character) source).charValue());
		}
		else if(source instanceof Short) {
			return factory.new_constant(((Short) source).shortValue());
		}
		else if(source instanceof Integer) {
			return factory.new_constant(((Integer) source).intValue());
		}
		else if(source instanceof Long) {
			return factory.new_constant(((Long) source).longValue());
		}
		else if(source instanceof Float) {
			return factory.new_constant(((Float) source).floatValue());
		}
		else if(source instanceof Double) {
			return factory.new_constant(((Double) source).doubleValue());
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/**
	 * @param literal
	 * @return SymbolLiteral[String]
	 * @throws Exception
	 */
	public static SymbolExpression literal(String literal) throws Exception {
		return factory.new_literal(literal);
	}
	/**
	 * @param elements
	 * @return {e1, e2, ..., en}
	 * @throws Exception
	 */
	public static SymbolExpression initializer_list(Iterable<Object> elements) throws Exception {
		return factory.new_initializer_list(elements);
	}
	/**
	 * @param body
	 * @param field
	 * @return body.field
	 * @throws Exception
	 */
	public static SymbolExpression field_expression(Object body, String field) throws Exception {
		return factory.new_field_expression(body, field);
	}
	/**
	 * @param function
	 * @param arguments
	 * @return function(arg1, arg2, ..., argn)
	 * @throws Exception
	 */
	public static SymbolExpression call_expression(Object function, Iterable<Object> arguments) throws Exception {
		return factory.new_call_expression(function, arguments);
	}
	/**
	 * @param operand
	 * @return -operand with type inference
	 * @throws Exception
	 */
	public static SymbolExpression arith_neg(Object operand) throws Exception {
		return factory.new_arith_neg(operand);
	}
	/**
	 * @param operand
	 * @return ~operand with type inference
	 * @throws Exception
	 */
	public static SymbolExpression bitws_rsv(Object operand) throws Exception {
		return factory.new_bitws_rsv(operand);
	}
	/**
	 * @param operand
	 * @return !operand
	 * @throws Exception
	 */
	public static SymbolExpression logic_not(Object operand) throws Exception {
		return factory.new_logic_not(operand);
	}
	/**
	 * @param operand
	 * @return &operand with type inference
	 * @throws Exception
	 */
	public static SymbolExpression address_of(Object operand) throws Exception {
		return factory.new_address_of(operand);
	}
	/**
	 * @param operand
	 * @return *operand with type inference
	 * @throws Exception
	 */
	public static SymbolExpression dereference(Object operand) throws Exception {
		return factory.new_dereference(operand);
	}
	/**
	 * @param data_type
	 * @param operand
	 * @return (type) expression
	 * @throws Exception
	 */
	public static SymbolExpression cast_expression(CType data_type, Object operand) throws Exception {
		return factory.new_type_casting(data_type, operand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand + roperand)
	 * @throws Exception
	 */
	public static SymbolExpression arith_add(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_arith_add(data_type, loperand, roperand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand - roperand)
	 * @throws Exception
	 */
	public static SymbolExpression arith_sub(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_arith_sub(data_type, loperand, roperand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand * roperand)
	 * @throws Exception
	 */
	public static SymbolExpression arith_mul(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_arith_mul(data_type, loperand, roperand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand / roperand)
	 * @throws Exception
	 */
	public static SymbolExpression arith_div(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_arith_div(data_type, loperand, roperand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand % roperand)
	 * @throws Exception
	 */
	public static SymbolExpression arith_mod(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_arith_mod(data_type, loperand, roperand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand & roperand)
	 * @throws Exception
	 */
	public static SymbolExpression bitws_and(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_bitws_and(data_type, loperand, roperand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand | roperand)
	 * @throws Exception
	 */
	public static SymbolExpression bitws_ior(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_bitws_ior(data_type, loperand, roperand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand ^ roperand)
	 * @throws Exception
	 */
	public static SymbolExpression bitws_xor(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_bitws_xor(data_type, loperand, roperand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand << roperand)
	 * @throws Exception
	 */
	public static SymbolExpression bitws_lsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_bitws_lsh(data_type, loperand, roperand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return (type) (loperand >> roperand)
	 * @throws Exception
	 */
	public static SymbolExpression bitws_rsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_bitws_rsh(data_type, loperand, roperand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return boolean(loperand) && boolean(roperand)
	 * @throws Exception
	 */
	public static SymbolExpression logic_and(Object loperand, Object roperand) throws Exception {
		return factory.new_logic_and(loperand, roperand);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return boolean(loperand) && boolean(roperand)
	 * @throws Exception
	 */
	public static SymbolExpression logic_ior(Object loperand, Object roperand) throws Exception {
		return factory.new_logic_ior(loperand, roperand);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return roperand < loperand
	 * @throws Exception
	 */
	public static SymbolExpression greater_tn(Object loperand, Object roperand) throws Exception {
		return factory.new_greater_tn(loperand, roperand);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return roperand <= loperand
	 * @throws Exception 
	 */
	public static SymbolExpression greater_eq(Object loperand, Object roperand) throws Exception {
		return factory.new_greater_eq(loperand, roperand);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand < roperand
	 * @throws Exception
	 */
	public static SymbolExpression smaller_tn(Object loperand, Object roperand) throws Exception {
		return factory.new_smaller_tn(loperand, roperand);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand <= roperand
	 * @throws Exception
	 */
	public static SymbolExpression smaller_eq(Object loperand, Object roperand) throws Exception {
		return factory.new_smaller_eq(loperand, roperand);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand == roperand
	 * @throws Exception
	 */
	public static SymbolExpression equal_with(Object loperand, Object roperand) throws Exception {
		return factory.new_equal_with(loperand, roperand);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return loperand != roperand
	 * @throws Exception
	 */
	public static SymbolExpression not_equals(Object loperand, Object roperand) throws Exception {
		return factory.new_not_equals(loperand, roperand);
	}
	
}
