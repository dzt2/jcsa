package com.jcsa.jcparse.lang.symb;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeclarator;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirIdentifier;
import com.jcsa.jcparse.lang.irlang.expr.CirImplicator;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirType;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * It implements the parse of SymbolNode from other Java-Objects.
 * 
 * @author yukimula
 *
 */
final class SymbolNodeParser {
	
	/* definitions */
	/** the factory to create data type **/
	protected static final CTypeFactory type_factory = new CTypeFactory();
	/** used to support sizeof operation **/
	private CRunTemplate ast_run_template;
	/** the CIR is optimized for default-value **/
	private boolean cir_optimize_switch;
	/**
	 * private construction for singleton mode
	 */
	private SymbolNodeParser() { 
		this.ast_run_template = null; 
		this.cir_optimize_switch = false; 
	}
	/** the singleton instance of symbolic parser algorithm **/
	protected static final SymbolNodeParser parser = new SymbolNodeParser();
	
	/* BAS PARSE */
	/**
	 * @param value	{Boolean|Character|Short|Integer|Long|Float|Double|CConstant}
	 * @return
	 * @throws Exception
	 */
	protected SymbolConstant parse_cons(Object value) throws Exception {
		CConstant constant = new CConstant();
		if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}
		else if(value instanceof Boolean) {
			constant.set_bool(((Boolean) value).booleanValue());
		}
		else if(value instanceof Character) {
			constant.set_char(((Character) value).charValue());
		}
		else if(value instanceof Short) {
			constant.set_int(((Short) value).shortValue());
		}
		else if(value instanceof Integer) {
			constant.set_int(((Integer) value).intValue());
		}
		else if(value instanceof Long) {
			constant.set_long(((Long) value).longValue());
		}
		else if(value instanceof Float) {
			constant.set_float(((Float) value).floatValue());
		}
		else if(value instanceof Double) {
			constant.set_double(((Double) value).doubleValue());
		}
		else if(value instanceof CConstant) {
			constant = (CConstant) value;
		}
		else {
			throw new IllegalArgumentException(value.getClass().getSimpleName());
		}
		return SymbolConstant.create(constant);
	}
	/**
	 * @param expression
	 * @param value
	 * @return	the boolean version of the expression
	 * @throws Exception
	 */
	protected SymbolExpression parse_cond(SymbolExpression expression, boolean value) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			if(CTypeAnalyzer.is_boolean(type)) {
				if(value) {
					return expression;
				}
				else {
					return SymbolUnaryExpression.create(CBasicTypeImpl.bool_type, COperator.logic_not, expression);
				}
			}
			else if(CTypeAnalyzer.is_number(type) || CTypeAnalyzer.is_pointer(type)) {
				SymbolExpression loperand = expression;
				SymbolExpression roperand = this.parse_cons(Integer.valueOf(0));
				if(value) {
					return SymbolBinaryExpression.create(CBasicTypeImpl.
							bool_type, COperator.not_equals, loperand, roperand);
				}
				else {
					return SymbolBinaryExpression.create(CBasicTypeImpl.
							bool_type, COperator.equal_with, loperand, roperand);
				}
			}
			else {
				throw new IllegalArgumentException("Invalid: " + type.generate_code());
			}
		}
	}
	/**
	 * @param execution
	 * @return (do#execution: int)
	 * @throws Exception
	 */
	protected SymbolIdentifier parse_exec(CirExecution execution) throws Exception {
		return SymbolIdentifier.create(execution);
	}
	
	/* AST PARSE */
	/**
	 * @param source
	 * @return it recursively parses the abstract syntax node to symbolic node
	 * @throws Exception
	 */
	private SymbolNode parse_ast(AstNode source) throws Exception {
		SymbolNode target;
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstIdExpression) {
			target = this.parse_ast_id_expression((AstIdExpression) source);
		}
		else if(source instanceof AstConstant) {
			target = this.parse_ast_constant((AstConstant) source);
		}
		else if(source instanceof AstLiteral) {
			target = this.parse_ast_literal((AstLiteral) source);
		}
		else if(source instanceof AstOperator) {
			target = this.parse_ast_operator((AstOperator) source);
		}
		else if(source instanceof AstUnaryExpression) {
			target = this.parse_ast_unary_expression((AstUnaryExpression) source);
		}
		else if(source instanceof AstPostfixExpression) {
			target = this.parse_ast_postfix_expression((AstPostfixExpression) source);
		}
		else if(source instanceof AstBinaryExpression) {
			target = this.parse_ast_binary_expression((AstBinaryExpression) source);
		}
		else if(source instanceof AstField) {
			target = this.parse_ast_field((AstField) source);
		}
		else if(source instanceof AstFieldExpression) {
			target = this.parse_ast_field_expression((AstFieldExpression) source);
		}
		else if(source instanceof AstArrayExpression) {
			target = this.parse_ast_array_expression((AstArrayExpression) source);
		}
		else if(source instanceof AstTypeName) {
			target = this.parse_ast_typename((AstTypeName) source);
		}
		else if(source instanceof AstCastExpression) {
			target = this.parse_cast_expression((AstCastExpression) source);
		}
		else if(source instanceof AstParanthExpression) {
			target = this.parse_ast_paranth_expression((AstParanthExpression) source);
		}
		else if(source instanceof AstConstExpression) {
			target = this.parse_ast_const_expression((AstConstExpression) source);
		}
		else if(source instanceof AstArgumentList) {
			target = this.parse_ast_argument_list((AstArgumentList) source);
		}
		else if(source instanceof AstFunCallExpression) {
			target = this.parse_ast_call_expression((AstFunCallExpression) source);
		}
		else if(source instanceof AstConditionalExpression) {
			target = this.parse_ast_condition_expression((AstConditionalExpression) source);
		}
		else if(source instanceof AstInitializerBody) {
			target = this.parse_initializer_body((AstInitializerBody) source);
		}
		else if(source instanceof AstInitializer) {
			target = this.parse_initializer((AstInitializer) source);
		}
		else if(source instanceof AstCommaExpression) {
			target = this.parse_comma_expression((AstCommaExpression) source);
		}
		else if(source instanceof AstSizeofExpression) {
			target = this.parse_sizeof_expression((AstSizeofExpression) source);
		}
		else if(source instanceof AstExpressionStatement) {
			target = this.parse_expression_statement((AstExpressionStatement) source);
		}
		else if(source instanceof AstDeclarator) {
			target = this.parse_declarator((AstDeclarator) source);
		}
		else if(source instanceof AstInitDeclarator) {
			target = this.parse_init_declarator((AstInitDeclarator) source);
		}
		else if(source instanceof AstIfStatement) {
			target = this.parse_if_statement((AstIfStatement) source);
		}
		else if(source instanceof AstCaseStatement) {
			target = this.parse_case_statement((AstCaseStatement) source);
		}
		else if(source instanceof AstWhileStatement) {
			target = this.parse_while_statement((AstWhileStatement) source);
		}
		else if(source instanceof AstDoWhileStatement) {
			target = this.parse_do_while_statement((AstDoWhileStatement) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
		if(!target.has_source()) { target.set_source(source); }
		return target;
	}
	/**
	 * @param source
	 * @return id_expression --> identifier | constant [enumerator]
	 * @throws Exception
	 */
	private SymbolNode parse_ast_id_expression(AstIdExpression source) throws Exception {
		CName cname = source.get_cname();
		if(cname == null) {
			return SymbolIdentifier.create(source.get_value_type(), source.get_name());
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
			CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
			CConstant constant = new CConstant();
			constant.set_int(enumerator.get_value());
			return SymbolConstant.create(constant);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source.generate_code());
		}
	}
	/**
	 * @param source
	 * @return constant --> constant
	 * @throws Exception
	 */
	private SymbolNode parse_ast_constant(AstConstant source) throws Exception {
		return SymbolConstant.create(source.get_constant());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_literal(AstLiteral source) throws Exception {
		return SymbolLiteral.create(source.get_literal());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_operator(AstOperator source) throws Exception {
		COperator operator = source.get_operator();
		switch(operator) {
		case arith_add_assign:
		case arith_sub_assign:
		case arith_mul_assign:
		case arith_div_assign:
		case arith_mod_assign:
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
		case left_shift_assign:
		case righ_shift_assign:
		{
			String name = operator.toString();
			name = name.substring(0, name.length() - 7).trim();
			operator = COperator.valueOf(name);
			break;
		}
		default:	break;
		}
		return SymbolOperator.create(operator);
	}
	/**
	 * 	+x	--> {x}
	 * 	-x	--> -{x}
	 * 	*x	--> defer{x}
	 * 	&x	--> addrs{x}
	 * 	~x	--> ~{x}
	 * 	++x	--> ++{x}
	 * 	--x	--> --{x}
	 * 	!x	--> !{x}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_unary_expression(AstUnaryExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_ast(source.get_operand());
		COperator operator = source.get_operator().get_operator();
		CType type = source.get_value_type();
		SymbolUnaryExpression expression;
		switch(operator) {
		case positive:		expression = SymbolUnaryExpression.create(type, COperator.positive, operand); break;
		case negative:		expression = SymbolUnaryExpression.create(type, COperator.negative, operand);	break;
		case address_of:	expression = SymbolUnaryExpression.create(type, COperator.address_of, operand);	break;
		case dereference:	expression = SymbolUnaryExpression.create(type, COperator.dereference, operand);	break;
		case bit_not:		expression = SymbolUnaryExpression.create(type, COperator.bit_not, operand);	break;
		case logic_not:		expression = SymbolUnaryExpression.create(type, COperator.logic_not, this.parse_cond(operand, true));	break;
		case increment:		expression = SymbolUnaryExpression.create(type, COperator.increment, operand);	break;
		case decrement:		expression = SymbolUnaryExpression.create(type, COperator.decrement, operand);	break;
		default:			throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		expression.get_operator().set_source(source.get_operator());
		return expression;
	}
	/**
	 * x++	-->	{x}++
	 * x--	-->	{x}--
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_postfix_expression(AstPostfixExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_ast(source.get_operand());
		COperator operator = source.get_operator().get_operator();
		CType type = source.get_value_type();
		SymbolUnaryExpression expression;
		switch(operator) {
		case increment:	expression = SymbolUnaryExpression.create(type, COperator.arith_add_assign, operand);	break;
		case decrement:	expression = SymbolUnaryExpression.create(type, COperator.arith_sub_assign, operand);	break;
		default:		throw new IllegalArgumentException("Invalid operator: " + operator.toString());
		}
		expression.get_operator().set_source(source.get_operator()); 
		return expression;
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_binary_expression(AstBinaryExpression source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_ast(source.get_loperand());
		SymbolExpression roperand = (SymbolExpression) this.parse_ast(source.get_roperand());
		CType type = source.get_value_type(); COperator operator = source.get_operator().get_operator();
		SymbolBinaryExpression expression;
		switch(operator) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:	
			expression = SymbolBinaryExpression.create(type, operator, loperand, roperand); break;
		case logic_and:
		case logic_or:		
			expression =  SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, operator, 
					this.parse_cond(loperand, true), this.parse_cond(roperand, true)); break;
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
			expression = SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, operator, loperand, roperand);
			break;
		case assign:
			expression = SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
			break;
		case arith_add_assign:
			roperand = SymbolBinaryExpression.create(type, COperator.arith_add, loperand, roperand);
			expression = SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
			break;
		case arith_sub_assign:
			roperand = SymbolBinaryExpression.create(type, COperator.arith_sub, loperand, roperand);
			roperand.set_source(source);
			expression = SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
			break;
		case arith_mul_assign:
			roperand = SymbolBinaryExpression.create(type, COperator.arith_mul, loperand, roperand);
			roperand.set_source(source);
			expression = SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
			break;
		case arith_div_assign:
			roperand = SymbolBinaryExpression.create(type, COperator.arith_div, loperand, roperand);
			roperand.set_source(source);
			expression = SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
			break;
		case arith_mod_assign:
			roperand = SymbolBinaryExpression.create(type, COperator.arith_mod, loperand, roperand);
			roperand.set_source(source);
			expression = SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
			break;
		case bit_and_assign:
			roperand = SymbolBinaryExpression.create(type, COperator.bit_and, loperand, roperand);
			roperand.set_source(source);
			expression = SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
			break;
		case bit_or_assign:
			roperand = SymbolBinaryExpression.create(type, COperator.bit_or, loperand, roperand);
			roperand.set_source(source);
			expression = SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
			break;
		case bit_xor_assign:
			roperand = SymbolBinaryExpression.create(type, COperator.bit_xor, loperand, roperand);
			roperand.set_source(source);
			expression = SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
			break;
		case left_shift_assign:
			roperand = SymbolBinaryExpression.create(type, COperator.left_shift, loperand, roperand);
			roperand.set_source(source);
			expression = SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
			break;
		case righ_shift_assign:
			roperand = SymbolBinaryExpression.create(type, COperator.righ_shift, loperand, roperand);
			roperand.set_source(source);
			expression = SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
			break;
		default:	throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		expression.get_operator().set_source(source.get_operator());
		return expression;
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_array_expression(AstArrayExpression source) throws Exception {
		SymbolExpression array = (SymbolExpression) this.parse_ast(source.get_array_expression());
		SymbolExpression index = (SymbolExpression) this.parse_ast(source.get_dimension_expression());
		CType array_type = CTypeAnalyzer.get_value_type(array.get_data_type());
		if(array_type instanceof CArrayType) {
			array_type = type_factory.get_pointer_type(
					((CArrayType) array_type).get_element_type());
		}
		SymbolExpression address = SymbolBinaryExpression.create(
					array_type, COperator.arith_add, array, index);
		
		CType defer_type;
		if(array_type instanceof CPointerType) {
			defer_type = ((CPointerType) array_type).get_pointed_type();
		}
		else {
			throw new IllegalArgumentException("Invalid: " + array_type);
		}
		return SymbolUnaryExpression.create(defer_type, COperator.dereference, address);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_typename(AstTypeName source) throws Exception {
		return SymbolType.create(source.get_type());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cast_expression(AstCastExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_ast(source.get_expression());
		SymbolType cast_type = (SymbolType) this.parse_ast(source.get_typename());
		SymbolCastExpression expression = SymbolCastExpression.create(cast_type, operand);
		expression.get_cast_type().set_source(source.get_typename());
		return expression;
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_field(AstField source) throws Exception {
		return SymbolField.create(source.get_name());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_field_expression(AstFieldExpression source) throws Exception {
		SymbolExpression body = (SymbolExpression) this.parse_ast(source.get_body());
		SymbolField field = (SymbolField) this.parse_ast(source.get_field());
		return SymbolFieldExpression.create(source.get_value_type(), body, field);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse_ast(source.get_sub_expression());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_const_expression(AstConstExpression source) throws Exception {
		return this.parse_ast(source.get_expression());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_argument_list(AstArgumentList source) throws Exception {
		List<SymbolExpression> arguments = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add((SymbolExpression) this.parse_ast(source.get_argument(k)));
		}
		return SymbolArgumentList.create(arguments);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_call_expression(AstFunCallExpression source) throws Exception {
		SymbolExpression function = (SymbolExpression) this.parse_ast(source.get_function());
		SymbolArgumentList arguments;
		if(source.has_argument_list()) {
			arguments = (SymbolArgumentList) this.parse_ast(source.get_argument_list());
		}
		else {
			arguments = SymbolArgumentList.create(new ArrayList<SymbolExpression>());
		}
		return SymbolCallExpression.create(source.get_value_type(), function, arguments);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_condition_expression(AstConditionalExpression source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast(source.get_condition());
		SymbolExpression toperand = (SymbolExpression) this.parse_ast(source.get_true_branch());
		SymbolExpression foperand = (SymbolExpression) this.parse_ast(source.get_false_branch());
		condition = this.parse_cond(condition, true);
		return SymbolConditionExpression.create(source.get_value_type(), condition, toperand, foperand);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_initializer(AstInitializer source) throws Exception {
		if(source.is_body()) {
			return this.parse_ast(source.get_body());
		}
		else {
			return this.parse_ast(source.get_expression());
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_initializer_body(AstInitializerBody source) throws Exception {
		List<SymbolExpression> elements = new ArrayList<SymbolExpression>();
		if(source.get_initializer_list() != null) {
			AstInitializerList list = source.get_initializer_list();
			for(int k = 0; k < list.number_of_initializer(); k++) {
				AstFieldInitializer initializer = list.get_initializer(k);
				elements.add((SymbolExpression) this.parse_ast(initializer.get_initializer()));
			}
		}
		return SymbolInitializerList.create(CBasicTypeImpl.void_type, elements);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_comma_expression(AstCommaExpression source) throws Exception {
		int k = source.number_of_arguments() - 1;
		return this.parse_ast(source.get_expression(k));
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_sizeof_expression(AstSizeofExpression source) throws Exception {
		CType data_type;
		if(source.is_expression())
			data_type = source.get_expression().get_value_type();
		else
			data_type = source.get_typename().get_type();
		data_type = CTypeAnalyzer.get_value_type(data_type);

		CConstant constant = new CConstant();
		if(this.ast_run_template == null)
			throw new IllegalArgumentException("Not support sizeof operator");
		else
			constant.set_int(this.ast_run_template.sizeof(data_type));
		
		return SymbolConstant.create(constant);
	}
	/**
	 * @param source
	 * @return whether the source is the condition of FOR-statement
	 */
	private boolean is_for_condition(AstExpressionStatement source) {
		AstNode parent = source.get_parent();
		if(parent instanceof AstForStatement) {
			return ((AstForStatement) parent).get_condition() == source;
		}
		else {
			return false;
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_expression_statement(AstExpressionStatement source) throws Exception {
		if(source.has_expression()) {
			SymbolExpression rvalue = (SymbolExpression) this.parse_ast(source.get_expression());
			if(this.is_for_condition(source)) {
				SymbolIdentifier lvalue = SymbolIdentifier.create(CBasicTypeImpl.bool_type, source.get_parent());
				rvalue = this.parse_cond(rvalue, true);
				return SymbolBinaryExpression.create(lvalue.get_data_type(), COperator.assign, lvalue, rvalue);
			}
			else {
				SymbolIdentifier lvalue = SymbolIdentifier.create(rvalue.get_data_type());
				return SymbolBinaryExpression.create(rvalue.get_data_type(), COperator.assign, lvalue, rvalue);
			}
		}
		else {
			SymbolExpression rvalue = SymbolConstant.create(Boolean.TRUE);
			if(this.is_for_condition(source)) {
				SymbolIdentifier lvalue = SymbolIdentifier.create(CBasicTypeImpl.bool_type, source.get_parent());
				return SymbolBinaryExpression.create(lvalue.get_data_type(), COperator.assign, lvalue, rvalue);
			}
			else {
				SymbolIdentifier lvalue = SymbolIdentifier.create(rvalue.get_data_type());
				return SymbolBinaryExpression.create(rvalue.get_data_type(), COperator.assign, lvalue, rvalue);
			}
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_declarator(AstDeclarator source) throws Exception {
		while(source != null) {
			switch(source.get_production()) {
			case identifier:
						AstName id = source.get_identifier();
						CName cname = id.get_cname();
						if(cname == null) {
							return SymbolIdentifier.create(CBasicTypeImpl.void_type, id.get_name());
						}
						else if(cname instanceof CInstanceName) {
							CType type = ((CInstanceName) cname).get_instance().get_type();
							return SymbolIdentifier.create(type, cname);
						}
						else if(cname instanceof CParameterName) {
							CType type = ((CParameterName) cname).get_parameter().get_type();
							return SymbolIdentifier.create(type, cname);
						}
						else {
							throw new IllegalArgumentException("Invalid cname");
						}
			default:	source = source.get_declarator(); break;
			}
		}
		throw new IllegalArgumentException("No identifier found.");
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_init_declarator(AstInitDeclarator source) throws Exception {
		if(source.has_initializer()) {
			SymbolExpression rvalue = (SymbolExpression) this.parse_ast(source.get_initializer());
			SymbolExpression lvalue = (SymbolExpression) this.parse_ast(source.get_declarator());
			return SymbolBinaryExpression.create(rvalue.get_data_type(), COperator.assign, lvalue, rvalue);
		}
		else {
			SymbolExpression lvalue = (SymbolExpression) this.parse_ast(source.get_declarator());
			SymbolExpression rvalue = SymbolIdentifier.create(lvalue.get_data_type(), source);
			return SymbolBinaryExpression.create(rvalue.get_data_type(), COperator.assign, lvalue, rvalue);
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_case_statement(AstCaseStatement source) throws Exception {
		AstSwitchStatement statement = null; AstNode node = source;
		SymbolExpression rvalue = (SymbolExpression) this.parse_ast(source.get_expression());
		while(node != null) {
			if(node instanceof AstSwitchStatement) {
				statement = (AstSwitchStatement) node;
			}
			else {
				node = node.get_parent();
			}
		}
		SymbolExpression lvalue = (SymbolExpression) this.parse_ast(statement.get_condition());
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, COperator.equal_with, lvalue, rvalue);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_if_statement(AstIfStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast(source.get_condition());
		condition = this.parse_cond(condition, true);
		SymbolIdentifier lvalue = SymbolIdentifier.create(CBasicTypeImpl.bool_type, source);
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, COperator.equal_with, lvalue, condition);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_while_statement(AstWhileStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast(source.get_condition());
		condition = this.parse_cond(condition, true);
		SymbolIdentifier lvalue = SymbolIdentifier.create(CBasicTypeImpl.bool_type, source);
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, COperator.equal_with, lvalue, condition);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_do_while_statement(AstDoWhileStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast(source.get_condition());
		condition = this.parse_cond(condition, true);
		SymbolIdentifier lvalue = SymbolIdentifier.create(CBasicTypeImpl.bool_type, source);
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, COperator.equal_with, lvalue, condition);
	}
	/**
	 * @param source
	 * @param ast_run_template
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression parse_astn(AstNode source, CRunTemplate ast_run_template) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			this.ast_run_template = ast_run_template;
			return (SymbolExpression) this.parse_ast(source);
		}
	}
	
	/* CIR parse */
	/**
	 * @param source
	 * @return recursively parse from CIR node
	 * @throws Exception
	 */
	private SymbolNode parse_cir(CirNode source) throws Exception {
		SymbolNode target;
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirArgumentList) {
			target = this.parse_cir_argument_list((CirArgumentList) source);
		}
		else if(source instanceof CirType) {
			target = this.parse_cir_type((CirType) source);
		}
		else if(source instanceof CirField) {
			target = this.parse_cir_field((CirField) source);
		}
		else if(source instanceof CirNameExpression) {
			target = this.parse_cir_name_expression((CirNameExpression) source);
		}
		else if(source instanceof CirConstExpression) {
			target = this.parse_cir_const_expression((CirConstExpression) source);
		}
		else if(source instanceof CirStringLiteral) {
			target = this.parse_cir_string_literal((CirStringLiteral) source);
		}
		else if(source instanceof CirDefaultValue) {
			target = this.parse_cir_default_expression((CirDefaultValue) source);
		}
		else if(source instanceof CirFieldExpression) {
			target = this.parse_cir_field_expression((CirFieldExpression) source);
		}
		else if(source instanceof CirAddressExpression) {
			target = this.parse_cir_address_expression((CirAddressExpression) source);
		}
		else if(source instanceof CirDeferExpression) {
			target = this.parse_cir_defer_expression((CirDeferExpression) source);
		}
		else if(source instanceof CirCastExpression) {
			target = this.parse_cir_cast_expression((CirCastExpression) source);
		}
		else if(source instanceof CirInitializerBody) {
			target = this.parse_cir_initializer_body((CirInitializerBody) source);
		}
		else if(source instanceof CirComputeExpression) {
			target = this.parse_cir_compute_expression((CirComputeExpression) source);
		}
		else if(source instanceof CirCallStatement) {
			target = this.parse_cir_call_statement((CirCallStatement) source);
		}
		else if(source instanceof CirWaitExpression) {
			target = this.parse_cir_wait_expression((CirWaitExpression) source);
		}
		else if(source instanceof CirAssignStatement) {
			target = this.parse_cir_assign_statement((CirAssignStatement) source);
		}
		else if(source instanceof CirIfStatement) {
			target = this.parse_cir_if_statement((CirIfStatement) source);
		}
		else if(source instanceof CirCaseStatement) {
			target = this.parse_cir_case_statement((CirCaseStatement) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
		
		if(!target.has_source()) { target.set_source(source); }
		return target;
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_argument_list(CirArgumentList source) throws Exception {
		List<SymbolExpression> arguments = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add((SymbolExpression) this.parse_cir(source.get_argument(k)));
		}
		return SymbolArgumentList.create(arguments);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_type(CirType source) throws Exception {
		return SymbolType.create(source.get_typename());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_field(CirField source) throws Exception {
		return SymbolField.create(source.get_name());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_name_expression(CirNameExpression source) throws Exception {
		if(source instanceof CirDeclarator) {
			return SymbolIdentifier.create(source.get_data_type(), ((CirDeclarator) source).get_cname());
		}
		else if(source instanceof CirIdentifier) {
			return SymbolIdentifier.create(source.get_data_type(), ((CirIdentifier) source).get_cname());
		}
		else if(source instanceof CirImplicator) {
			return SymbolIdentifier.create(source.get_data_type(), source.get_ast_source());
		}
		else if(source instanceof CirReturnPoint) {
			return SymbolIdentifier.create(source.get_data_type(), source.function_of());
		}
		else {
			throw new IllegalArgumentException(source.generate_code(true));
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_const_expression(CirConstExpression source) throws Exception {
		return SymbolConstant.create(source.get_constant());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_string_literal(CirStringLiteral source) throws Exception {
		return SymbolLiteral.create(source.get_literal());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_field_expression(CirFieldExpression source) throws Exception {
		SymbolExpression body = (SymbolExpression) this.parse_cir(source.get_body());
		SymbolField field = (SymbolField) this.parse_cir(source.get_field());
		return SymbolFieldExpression.create(source.get_data_type(), body, field);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_defer_expression(CirDeferExpression source) throws Exception {
		SymbolExpression address = (SymbolExpression) this.parse_cir(source.get_address());
		return SymbolUnaryExpression.create(source.get_data_type(), COperator.dereference, address);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_default_expression(CirDefaultValue source) throws Exception {
		if(this.cir_optimize_switch) {
			CType type = CTypeAnalyzer.get_value_type(source.get_data_type());
			if(type instanceof CBasicType) {
				switch(((CBasicType) type).get_tag()) {
				case c_bool:	return SymbolConstant.create(Boolean.FALSE);
				case c_char:
				case c_uchar:	return SymbolConstant.create(Character.valueOf('\0'));
				case c_short:
				case c_ushort:
				case c_int:
				case c_uint:	return SymbolConstant.create(Integer.valueOf(0));
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:	return SymbolConstant.create(Long.valueOf(0L));
				case c_float:	return SymbolConstant.create(Float.valueOf(0.0f));
				case c_double:
				case c_ldouble:	return SymbolConstant.create(Double.valueOf(0.0));
				default:		return SymbolIdentifier.create(source.get_data_type(), source);
				}
			}
			else if(type instanceof CPointerType) {
				return SymbolConstant.create(Integer.valueOf(0));
			}
			else if(type instanceof CEnumType) {
				return SymbolConstant.create(Integer.valueOf(0));
			}
			else {
				return SymbolIdentifier.create(source.get_data_type(), source);
			}
		}
		else {
			return SymbolIdentifier.create(source.get_data_type(), source);
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_cast_expression(CirCastExpression source) throws Exception {
		SymbolType cast_type = (SymbolType) this.parse_cir(source.get_type());
		SymbolExpression operand = (SymbolExpression) this.parse_cir(source.get_operand());
		return SymbolCastExpression.create(cast_type, operand);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_address_expression(CirAddressExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_cir(source.get_operand());
		return SymbolUnaryExpression.create(source.get_data_type(), COperator.address_of, operand);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_initializer_body(CirInitializerBody source) throws Exception {
		List<SymbolExpression> elements = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_elements(); k++) {
			elements.add((SymbolExpression) this.parse_cir(source.get_element(k)));
		}
		return SymbolInitializerList.create(CBasicTypeImpl.void_type, elements);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_compute_expression(CirComputeExpression source) throws Exception {
		COperator operator = source.get_operator();
		if(source.number_of_operand() == 1) {
			SymbolExpression operand = (SymbolExpression) this.parse_cir(source.get_operand(0));
			return SymbolUnaryExpression.create(source.get_data_type(), operator, operand);
		}
		else {
			SymbolExpression loperand = (SymbolExpression) this.parse_cir(source.get_operand(0));
			SymbolExpression roperand = (SymbolExpression) this.parse_cir(source.get_operand(1));
			return SymbolBinaryExpression.create(source.get_data_type(), operator, loperand, roperand);
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_call_statement(CirCallStatement source) throws Exception {
		SymbolExpression function = (SymbolExpression) this.parse_cir(source.get_function());
		SymbolArgumentList arguments = (SymbolArgumentList) this.parse_cir(source.get_arguments());
		
		CType func_type = CTypeAnalyzer.get_value_type(function.get_data_type());
		if(func_type instanceof CPointerType) {
			func_type = ((CPointerType) func_type).get_pointed_type();
		}
		if(func_type instanceof CFunctionType) {
			func_type = ((CFunctionType) func_type).get_return_type();
		}
		else {
			func_type = CBasicTypeImpl.void_type;
		}
		
		return SymbolCallExpression.create(func_type, function, arguments);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_wait_expression(CirWaitExpression source) throws Exception {
		CirExecution wait_execution = source.execution_of();
		CirExecution call_execution = wait_execution.get_graph().get_execution(wait_execution.get_id() - 1);
		CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
		return this.parse_cir(call_statement);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_assign_statement(CirAssignStatement source) throws Exception {
		SymbolExpression lvalue = (SymbolExpression) this.parse_cir(source.get_lvalue());
		SymbolExpression rvalue = (SymbolExpression) this.parse_cir(source.get_rvalue());
		return SymbolBinaryExpression.create(lvalue.get_data_type(), COperator.assign, lvalue, rvalue);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_if_statement(CirIfStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_cir(source.get_condition());
		condition = this.parse_cond(condition, true);
		SymbolExpression lvalue = SymbolIdentifier.create(source.execution_of());
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, COperator.assign, lvalue, condition);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_case_statement(CirCaseStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_cir(source.get_condition());
		condition = this.parse_cond(condition, true);
		SymbolExpression lvalue = SymbolIdentifier.create(source.execution_of());
		return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, COperator.assign, lvalue, condition);
	}
	/**
	 * @param source
	 * @param cir_optimize_switch
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression parse_cirn(CirNode source, boolean cir_optimize_switch) throws Exception {
		this.cir_optimize_switch = cir_optimize_switch;
		return (SymbolExpression) this.parse_cir(source);
	}
	
}
