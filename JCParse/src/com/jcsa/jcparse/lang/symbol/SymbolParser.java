package com.jcsa.jcparse.lang.symbol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterList;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypedefName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
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
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstDeclarationList;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeclarator;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CScope;

/**
 * 	It implements the parsing from the object to SymbolNode based on the following translation rules.
 * 	<br>
 * 	<code>
 * 	[BASIC_GROUP]															<br>
 *		Boolean					-->	SymbolConstant(bool)					<br>
 *		Character				-->	SymbolConstant(char)					<br>
 *		Short|Integer			-->	SymbolConstant(int)						<br>
 *		Long					-->	SymbolConstant(long)					<br>
 *		Float					-->	SymbolConstant(float)					<br>
 *		Double					-->	SymbolConstant(double)					<br>
 *		CType					--> default_constant | default#data_type	<br>
 *		String					-->	SymbolLiteral							<br>
 *		CirExecution			-->	@stmt#execution							<br>
 *	[CIR_SYNTAXS]															<br>
 * 		CirArgumentList			-->	SymbolArgumentList						<br>
 * 		CirField				-->	SymbolField								<br>
 * 		CirType					-->	SymbolType								<br>
 * 		CirLabel				-->	@stmt#label.target.execution			<br>
 * 		CirAssignStatement		-->	loperand := roperand					<br>
 * 		CirIfStatement			-->	condition ? true_label : false_label	<br>
 * 		CirCaseStatement		-->	condition ? next_label : else_label		<br>
 * 		CirCallStatement		-->	SymbolCallExpression					<br>
 * 		CirGotoStatement		-->	next_pointer := goto_label				<br>
 * 		CirTagStatement			-->	@stmt#tag_stmt.execution				<br>
 * 		CirDeclarator			-->	identifier(cname.name, cname.scope)		<br>
 * 		CirIdentifier			-->	identifier(cname.name, cname.scope)		<br>
 * 		CirImplicator			--> parse_ast(expr.ast_source)				<br>
 * 		CirReturnPoint			-->	identifier("return", function_name)		<br>
 * 		CirConstExpression		-->	SymbolConstant							<br>
 * 		CirStringLiteral		-->	SymbolLiteral							<br>
 * 		CirDefaultValue			--> default_constant | default#data_type	<br>
 * 		CirAddressExpression	-->	SymbolUnaryExpression(&)				<br>
 * 		CirCastExpression		-->	SymbolCastExpression					<br>
 * 		CirDeferExpression		-->	SymbolUnaryExpression(*)				<br>
 * 		CirFieldExpression		-->	SymbolFieldExpression					<br>
 * 		CirWaitExpression		-->	SymbolCallExpression					<br>
 * 		CirInitializerBody		-->	SymbolInitializerList					<br>
 * 		CirComputeExpression	-->	SymbolUnaryExpr | SymbolBinaryExpr		<br>
 * 	[AST_SYNTAXS]															<br>
 * 		{AST-EXPRESSION}													<br>
 * 		AstIdExpression				-->	SymbolIdentifier {CName|name#scope}	<br>
 * 		AstIdExpression				-->	SymbolConstant 	{CEnumeratorName}	<br>
 * 		AstConstant					-->	SymbolConstant						<br>
 * 		AstLiteral					-->	SymbolLiteral	{literal: String}	<br>
 * 		AstConstExpression			-->	parse(expr.sub_expression)			<br>
 * 		AstParanthExpression		-->	parse(expr.sub_expression)			<br>
 * 		AstArithBinaryExpression	-->	SymbolArithExpression{+,-,*,/,%}	<br>
 * 		AstBitwsBinaryExpression	-->	SymbolBitwsExpression{&,|,^,<<,>>}	<br>
 * 		AstLogicBinaryExpression	-->	SymbolLogicExpression{&&,||}		<br>
 * 		AstRelationExpression		-->	SymbolRelationExpression{<,<=,>,>=}	<br>
 * 		AstXXXAssignExpression		-->	SymbolAssignExpression{:=, inc}		<br>
 * 		AstArrayExpression			-->	(defer (arith_add address index))	<br>
 * 		AstCastExpression			--> (cast_type) parse(operand)			<br>
 * 		AstConditionalExpression	-->	SymbolConditionalExpression			<br>
 * 		AstFieldExpression			-->	SymbolFieldExpression				<br>
 * 		AstFunCallExpression		-->	SymbolCallExpression				<br>
 * 		AstSizeofExpression			-->	SymbolConstant	[template != null]	<br>
 * 		AstSizeofExpression			--> SymbolIdentifier{sizeof#data_type}	<br>
 * 		AstInitializerBody			--> parse(initializer_list)				<br>
 * 		AstCommaExpression			-->	SymbolExpressionList				<br>
 * 	{AST-STATEMENTS}														<br>
 * 		AstBreakStatement			-->	next(break) := stmt(loop)			<br>
 * 		AstContinueStatement		-->	next(continue) := stmt(loop.cond)	<br>
 * 		AstGotoStatement			-->	next(goto) := stmt(label)			<br>
 * 		AstReturnStatement			-->	return#func_name := expression		<br>
 * 		AstSwitchStatement			-->	parse(switch.condition)				<br>
 * 		AstIfStatement				--> condition ? stmt(true) : stmt(false)<br>	
 * 		AstCaseStatement			-->	switch.condition == case.expression	<br>
 * 		AstWhileStatement			-->	condition ? stmt(body) : stmt(loop)	<br>
 * 		AstDoWhileStatement			-->	condition ? stmt(body) : stmt(loop)	<br>
 * 		AstForStatement				-->	condition ? stmt(body) : stmt(loop)	<br>
 * 		AstDefaultStatement			-->	stmt(source)						<br>
 * 		AstLabeledStatement			-->	stmt(source)						<br>
 * 		AstCompoundStatement		-->	SymbolExpressionList				<br>
 * 	{AST-OTHERWISE}															<br>
 * 		AstStatementList		-->	SymbolExpressionList					<br>
 * 		AstLabel				-->	stmt(labeled_stmt)						<br>
 * 		AstArgumentList			--> SymbolArgumentList						<br>
 * 		AstDeclaration			-->	SymbolExpressionList [init_decl_list]	<br>
 * 		AstDeclaration			-->	stmt(decl_stmt)							<br>
 * 		AstInitDeclaratorList	-->	SymbolExpressionList {decl := value}	<br>
 * 		AstInitDeclarator		-->	decl := parse(initializer)|default_value<br>
 * 		AstName					--> parse(CName) | SymbolIdentifier			<br>
 * 		AstInitializer			-->	parse(body|expression)					<br>
 * 		AstInitializerList		-->	SymbolInitializerList					<br>
 * 		AstFieldInitializer		-->	parse(initializer)						<br>
 * 		AstTypeName				-->	SymbolType								<br>
 * 		AstField				-->	SymbolField								<br>
 * 		AstTypedefName			-->	SymbolType								<br>
 * 		AstParameterDeclaration	-->	decl := default_value | stmt(source)	<br>
 * 		AstParamList			-->	SymbolExpressionList					<br>
 * 	</code>
 * 
 * 	@author yukimula
 *
 */
final class SymbolParser {
	
	/* singleton */ /** constructor **/ private SymbolParser() {}
	private static final SymbolParser parser = new SymbolParser();
	
	/* parsing interfaces */
	/**
	 * @param source	[Boolean|Character|Short|Integer|Long|Float|Double|CConstant]
	 * @return			the constant that the source represents
	 * @throws Exception
	 */
	protected static SymbolConstant 	parse_to_cons(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof Boolean || source instanceof Character 
				|| source instanceof Short || source instanceof Integer
				|| source instanceof Long || source instanceof Float
				|| source instanceof Double || source instanceof CConstant) {
			return (SymbolConstant) parser.parse_cons(source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/**
	 * @param source	[Boolean|Character|Short|Integer|Long|Float|Double|CConstant|String
	 * 					|SymbolExpression|AstNode|CirNode|CirExecution|CName|CType]
	 * @return
	 * @throws Exception
	 */
	protected static SymbolExpression 	parse_to_expr(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof Boolean || source instanceof Character 
				|| source instanceof Short || source instanceof Integer
				|| source instanceof Long || source instanceof Float
				|| source instanceof Double || source instanceof CConstant) {
			return parser.parse_cons(source);
		}
		else if(source instanceof String) {
			return SymbolLiteral.create(source.toString());
		}
		else if(source instanceof CName) {
			return (SymbolExpression) parser.parse_name((CName) source);
		}
		else if(source instanceof AstNode) {
			return (SymbolExpression) parser.parse_ast((AstNode) source);
		}
		else if(source instanceof CirNode) {
			return (SymbolExpression) parser.parse_cir((CirNode) source);
		}
		else if(source instanceof CirExecution) {
			return parser.parse_stmt(source);
		}
		else if(source instanceof CType) {
			return parser.get_default_value(SymbolFactory.is_optimized(), (CType) source);
		}
		else if(source instanceof SymbolExpression) {
			return (SymbolExpression) ((SymbolExpression) source).clone();
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/**
	 * @param source	[Boolean|Character|Short|Integer|Long|Float|Double|CConstant|String
	 * 					|SymbolExpression|AstNode|CirNode|CirExecution|CName|CType]
	 * @param value		the value being expected for the boolean representation
	 * @return
	 * @throws Exception
	 */
	protected static SymbolExpression 	parse_to_bool(Object source, boolean value) throws Exception {
		return parser.parse_cond(parse_to_expr(source), value);
	}
	
	/* basic translation */
	/**
	 * @param source	[Boolean|Character|Short|Integer|Long|Float|Double|CConstant|String]
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression parse_cons(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as: null");
		}
		else {
			CConstant constant = new CConstant();
			if(source instanceof Boolean) {
				constant.set_bool(((Boolean) source).booleanValue());
			}
			else if(source instanceof Character) {
				constant.set_char(((Character) source).charValue());
			}
			else if(source instanceof Short) {
				constant.set_int(((Short) source).intValue());
			}
			else if(source instanceof Integer) {
				constant.set_int(((Integer) source).intValue());
			}
			else if(source instanceof Long) {
				constant.set_long(((Long) source).longValue());
			}
			else if(source instanceof Float) {
				constant.set_float(((Float) source).floatValue());
			}
			else if(source instanceof Double) {
				constant.set_double(((Double) source).doubleValue());
			}
			else if(source instanceof CConstant) {
				constant = (CConstant) source;
			}
			else if(source instanceof String) {
				return SymbolLiteral.create(source.toString());
			}
			else {
				throw new IllegalArgumentException(source.getClass().getSimpleName());
			}
			return SymbolConstant.create(constant);
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return it translates the expression to boolean version
	 * @throws Exception
	 */
	private SymbolExpression parse_cond(SymbolExpression expression, boolean value) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(SymbolFactory.is_bool(expression)) {
			if(value) {
				return expression;
			}
			else {
				return SymbolUnaryExpression.create(CBasicTypeImpl.bool_type, COperator.logic_not, expression);
			}
		}
		else if(SymbolFactory.is_numb(expression) || 
				SymbolFactory.is_real(expression) || 
				SymbolFactory.is_addr(expression)) {
			SymbolExpression loperand = expression;
			SymbolExpression roperand = this.parse_cons(Integer.valueOf(0));
			if(value) {
				return SymbolRelationExpression.create(COperator.not_equals, loperand, roperand);
			}
			else {
				return SymbolRelationExpression.create(COperator.equal_with, loperand, roperand);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param source	[AstNode|CirStatement|CirExecution]
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression parse_stmt(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstNode) {
			String name = "@stmt";
			Object scope = source;
			SymbolIdentifier identifier = SymbolIdentifier.create(
					CBasicTypeImpl.int_type, name, scope);
			identifier.set_source(source);
			return identifier;
		}
		else if(source instanceof CirNode) {
			return this.parse_stmt(((CirNode) source).execution_of());
		}
		else if(source instanceof CirExecution) {
			String name = "@stmt";
			Object scope = source;
			SymbolIdentifier identifier = SymbolIdentifier.create(
					CBasicTypeImpl.int_type, name, scope);
			identifier.set_source(source);
			return identifier;
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode 	parse_name(CName source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CInstanceName) {
			CInstance instance = ((CInstanceName) source).get_instance();
			return SymbolIdentifier.create(instance.get_type(), source.get_name(), source.get_scope());
		}
		else if(source instanceof CParameterName) {
			CInstance instance = ((CParameterName) source).get_parameter();
			return SymbolIdentifier.create(instance.get_type(), source.get_name(), source.get_scope());
		}
		else if(source instanceof CEnumeratorName) {
			int value = ((CEnumeratorName) source).get_enumerator().get_value();
			CConstant constant = new CConstant();
			constant.set_int(value);
			return SymbolConstant.create(constant);
		}
		else {
			return SymbolIdentifier.create(CBasicTypeImpl.void_type, source.get_name(), source);
		}
	}
	/**
	 * @param source	AstNode CirNode CirExecution
	 * @return the return#function_name identifier
	 * @throws Exception
	 */
	private	SymbolExpression parse_retr(CType type, Object source) throws Exception {
		String func_name;
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstNode) {
			AstFunctionDefinition def = ((AstNode) source).get_function_of();
			AstDeclarator declarator = def.get_declarator();
			while(declarator.get_production() != DeclaratorProduction.identifier) {
				declarator = declarator.get_declarator();
			}
			func_name = declarator.get_identifier().get_name();
		}
		else if(source instanceof CirNode) {
			CirFunctionDefinition def = ((CirNode) source).function_of();
			func_name = def.get_declarator().get_name();
		}
		else if(source instanceof CirExecution) {
			func_name = ((CirExecution) source).get_graph().get_function().get_name();
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
		return SymbolIdentifier.create(type, "return", func_name);
	}
	/**
	 * It returns a control pointer that decides which statement is executed
	 * after the execution of source node.
	 * @param source
	 * @return do#source[source]
	 * @throws Exception
	 */
	private SymbolExpression parse_nexp(Object source) throws Exception {
		SymbolIdentifier id = SymbolIdentifier.create(
				CBasicTypeImpl.int_type, "do", source);
		id.set_source(source); return id;
	}
	/**
	 * @param optimize	whether to get default-constants
	 * @param data_type	the type of the default value created
	 * @return			the default value w.r.t. the given data type
	 * @throws Exception
	 */
	private	SymbolExpression get_default_value(boolean optimize, CType data_type) throws Exception {
		data_type = SymbolFactory.get_type(data_type);
		if(data_type == null) {
			throw new IllegalArgumentException("Invalid data_type: null");
		}
		else if(!optimize) {
			return SymbolIdentifier.create(data_type, "default", data_type);
		}
		else if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_bool:	return this.parse_cons(Boolean.FALSE);
			case c_char:	
			case c_uchar:	return this.parse_cons(Character.valueOf('\0'));
			case c_short:
			case c_ushort:	return this.parse_cons(Short.valueOf((short) 0));
			case c_int:
			case c_uint:	return this.parse_cons(Integer.valueOf(0));
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:	return this.parse_cons(Long.valueOf(0L));
			case c_float:	return this.parse_cons(Float.valueOf(0.0f));
			case c_double:
			case c_ldouble:	return this.parse_cons(Double.valueOf(0.0));
			default:		return this.get_default_value(false, data_type);
			}
		}
		else if(data_type instanceof CPointerType) {
			return this.parse_cons(Long.valueOf(0L));
		}
		else if(data_type instanceof CEnumType) {
			return this.parse_cons(Integer.valueOf(0));
		}
		else {
			return this.get_default_value(false, data_type);
		}
	}
	
	/* CIR-oriented parse */
	/**
	 * 	It parses the C-intermediate representation node to Symbolic node by the
	 * 	following syntax-directed transformation rules.
	 * 	<br>
	 * 	<code>
	 * 		CirArgumentList			-->	SymbolArgumentList						<br>
	 * 		CirField				-->	SymbolField								<br>
	 * 		CirType					-->	SymbolType								<br>
	 * 		CirLabel				-->	@stmt#label.target.execution			<br>
	 * 		CirAssignStatement		-->	loperand := roperand					<br>
	 * 		CirIfStatement			-->	condition ? true_label : false_label	<br>
	 * 		CirCaseStatement		-->	condition ? next_label : else_label		<br>
	 * 		CirCallStatement		-->	SymbolCallExpression					<br>
	 * 		CirGotoStatement		-->	next_pointer := goto_label				<br>
	 * 		CirTagStatement			-->	@stmt#tag_stmt.execution				<br>
	 * 		CirDeclarator			-->	identifier(cname.name, cname.scope)		<br>
	 * 		CirIdentifier			-->	identifier(cname.name, cname.scope)		<br>
	 * 		CirImplicator			--> parse_ast(expr.ast_source)				<br>
	 * 		CirReturnPoint			-->	identifier("return", function_name)		<br>
	 * 		CirConstExpression		-->	SymbolConstant							<br>
	 * 		CirStringLiteral		-->	SymbolLiteral							<br>
	 * 		CirDefaultValue			--> default_constant | default#data_type	<br>
	 * 		CirAddressExpression	-->	SymbolUnaryExpression(&)				<br>
	 * 		CirCastExpression		-->	SymbolCastExpression					<br>
	 * 		CirDeferExpression		-->	SymbolUnaryExpression(*)				<br>
	 * 		CirFieldExpression		-->	SymbolFieldExpression					<br>
	 * 		CirWaitExpression		-->	SymbolCallExpression					<br>
	 * 		CirInitializerBody		-->	SymbolInitializerList					<br>
	 * 		CirComputeExpression	-->	SymbolUnaryExpr | SymbolBinaryExpr		<br>
	 * 	</code>	
	 * 	
	 * 	@param source 	the CirNode to be parsed and linked by the SymbolNode
	 * 	@return			the SymbolNode parsed from CirNodes using above rules
	 * 	@throws Exception
	 */
	private	SymbolNode	parse_cir(CirNode source) throws Exception {
		SymbolNode target;
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirExpression) {
			target = this.parse_cir_expression((CirExpression) source);
		}
		else if(source instanceof CirStatement) {
			target = this.parse_cir_statement((CirStatement) source);
		}
		else {
			target = this.parse_cir_otherwise(source);
		}
		if(target.get_source() == null) {
			target.set_source(source); 
		}
		return target;
	}
	/* CIR-Element Classes */
	private SymbolNode	parse_cir_argument_list(CirArgumentList source) throws Exception {
		List<SymbolExpression> arguments = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			SymbolExpression argument = (SymbolExpression) this.parse_cir(source.get_argument(k));
			arguments.add(argument);
		}
		return SymbolArgumentList.create(arguments);
	}
	private	SymbolNode	parse_cir_field(CirField source) throws Exception {
		return SymbolField.create(source.get_name());
	}
	private SymbolNode	parse_cir_label(CirLabel source) throws Exception {
		CirStatement statement = (CirStatement) source.get_tree().get_node(source.get_target_node_id());
		return this.parse_stmt(statement);
	}
	private	SymbolNode	parse_cir_type(CirType source) throws Exception {
		return SymbolType.create(source.get_typename());
	}
	private	SymbolNode	parse_cir_otherwise(CirNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirArgumentList) {
			return this.parse_cir_argument_list((CirArgumentList) source);
		}
		else if(source instanceof CirField) {
			return this.parse_cir_field((CirField) source);
		}
		else if(source instanceof CirLabel) {
			return this.parse_cir_label((CirLabel) source);
		}
		else if(source instanceof CirType) {
			return this.parse_cir_type((CirType) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/* CIR-Statement Classes */
	private	SymbolNode	parse_cir_assign_stmt(CirAssignStatement source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_cir(source.get_lvalue());
		SymbolExpression roperand = (SymbolExpression) this.parse_cir(source.get_rvalue());
		return SymbolAssignExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	private SymbolNode	parse_cir_if_stmt(CirIfStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_cir(source.get_condition());
		condition = this.parse_cond(condition, true);
		SymbolExpression t_operand = (SymbolExpression) this.parse_cir(source.get_true_label());
		SymbolExpression f_operand = (SymbolExpression) this.parse_cir(source.get_false_label());
		return SymbolIfElseExpression.create(t_operand.get_data_type(), condition, t_operand, f_operand);
	}
	private	SymbolNode	parse_cir_case_stmt(CirCaseStatement source) throws Exception {
		CirExecution source_execution = source.execution_of();
		
		SymbolExpression t_operand = (SymbolExpression) this.parse_cir(
				source_execution.get_ou_flow(0).get_target().get_statement());
		SymbolExpression f_operand = (SymbolExpression) this.parse_cir(source.get_false_label());
		SymbolExpression condition = (SymbolExpression) this.parse_cir(source.get_condition());
		condition = this.parse_cond(condition, true);
		return SymbolIfElseExpression.create(t_operand.get_data_type(), condition, t_operand, f_operand);
	}
	private	SymbolNode	parse_cir_call_stmt(CirCallStatement source) throws Exception {
		CirExecution call_execution = source.execution_of();
		CirExecution wait_execution = call_execution.get_graph().
					get_execution(call_execution.get_id() + 1);
		CirWaitAssignStatement wait_statement = 
				(CirWaitAssignStatement) wait_execution.get_statement();
		CType data_type = wait_statement.get_rvalue().get_data_type();
		
		SymbolExpression function = (SymbolExpression) this.parse_cir(source.get_function());
		SymbolArgumentList arguments = (SymbolArgumentList) this.parse_cir(source.get_arguments());
		return SymbolCallExpression.create(data_type, function, arguments);
	}
	private	SymbolNode	parse_cir_goto_stmt(CirGotoStatement source) throws Exception {
		SymbolExpression roperand = (SymbolExpression) this.parse_cir(source.get_label());
		SymbolExpression loperand = this.parse_nexp(source.execution_of());
		return SymbolAssignExpression.create(loperand.get_data_type(), COperator.increment, loperand, roperand);
	}
	private	SymbolNode	parse_cir_tag_stmt(CirTagStatement source) throws Exception {
		return this.parse_stmt(source.execution_of());
	}
	private	SymbolNode	parse_cir_statement(CirStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirAssignStatement) {
			return this.parse_cir_assign_stmt((CirAssignStatement) source);
		}
		else if(source instanceof CirIfStatement) {
			return this.parse_cir_if_stmt((CirIfStatement) source);
		}
		else if(source instanceof CirCaseStatement) {
			return this.parse_cir_case_stmt((CirCaseStatement) source);
		}
		else if(source instanceof CirCallStatement) {
			return this.parse_cir_call_stmt((CirCallStatement) source);
		}
		else if(source instanceof CirGotoStatement) {
			return this.parse_cir_goto_stmt((CirGotoStatement) source);
		}
		else if(source instanceof CirTagStatement) {
			return this.parse_cir_tag_stmt((CirTagStatement) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/* CIR-Expression Classes */
	private	SymbolNode	parse_cir_name_expr(CirNameExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirDeclarator) {
			return this.parse_name(((CirDeclarator) source).get_cname());
		}
		else if(source instanceof CirIdentifier) {
			return this.parse_name(((CirIdentifier) source).get_cname());
		}
		else if(source instanceof CirImplicator) {
			// return this.parse_ast(source.get_ast_source());
			return SymbolIdentifier.create(source.get_data_type(), "@ast", source);
		}
		else if(source instanceof CirReturnPoint) {
			CType type = source.get_data_type();
			return this.parse_retr(type, source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	private	SymbolNode	parse_cir_cons_expr(CirConstExpression source) throws Exception {
		return SymbolConstant.create(source.get_constant());
	}
	private	SymbolNode	parse_cir_slit_expr(CirStringLiteral source) throws Exception {
		return SymbolLiteral.create(source.get_literal());
	}
	private	SymbolNode	parse_cir_defer_expr(CirDeferExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_cir(source.get_address());
		return SymbolUnaryExpression.create(source.get_data_type(), COperator.dereference, operand);
	}
	private SymbolNode 	parse_cir_field_expr(CirFieldExpression source) throws Exception {
		SymbolExpression body = (SymbolExpression) this.parse_cir(source.get_body());
		SymbolField field = (SymbolField) this.parse_cir(source.get_field());
		return SymbolFieldExpression.create(source.get_data_type(), body, field);
	}
	private	SymbolNode	parse_cir_default_value(CirDefaultValue source) throws Exception {
		return this.get_default_value(SymbolFactory.is_optimized(), source.get_data_type());
	}
	private	SymbolNode	parse_cir_addr_expr(CirAddressExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_cir(source.get_operand());
		return SymbolUnaryExpression.create(source.get_data_type(), COperator.address_of, operand);
	}
	private	SymbolNode	parse_cir_cast_expr(CirCastExpression source) throws Exception {
		SymbolType cast_type = (SymbolType) this.parse_cir(source.get_type());
		SymbolExpression operand = (SymbolExpression) this.parse_cir(source.get_operand());
		return SymbolCastExpression.create(cast_type, operand);
	}
	private	SymbolNode	parse_cir_init_body(CirInitializerBody source) throws Exception {
		List<SymbolExpression> elements = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_elements(); k++) {
			elements.add((SymbolExpression) this.parse_cir(source.get_element(k)));
		}
		return SymbolInitializerList.create(elements);
	}
	private	SymbolNode	parse_cir_wait_expr(CirWaitExpression source) throws Exception {
		CirExecution wait_execution = source.execution_of();
		CirExecution call_execution = wait_execution.get_graph().get_execution(wait_execution.get_id() - 1);
		CirCallStatement statement = (CirCallStatement) call_execution.get_statement();
		SymbolExpression function = (SymbolExpression) this.parse_cir(statement.get_function());
		SymbolArgumentList arguments = (SymbolArgumentList) this.parse_cir(statement.get_arguments());
		return SymbolCallExpression.create(source.get_data_type(), function, arguments);
	}
	private	SymbolNode	parse_cir_comp_expr(CirComputeExpression source) throws Exception {
		COperator operator = source.get_operator();
		if(source.number_of_operand() == 1) {
			SymbolExpression operand = (SymbolExpression) this.parse_cir(source.get_operand(0));
			switch(operator) {
			case positive:	return operand;
			case negative:	
			case bit_not:	
			case logic_not:	return SymbolUnaryExpression.create(source.get_data_type(), operator, operand);
			default:		throw new IllegalArgumentException("Invalid operator: " + operator.toString());
			}
		}
		else {
			SymbolExpression loperand = (SymbolExpression) this.parse_cir(source.get_operand(0));
			SymbolExpression roperand = (SymbolExpression) this.parse_cir(source.get_operand(1));
			switch(operator) {
			case arith_add:
			case arith_sub:
			case arith_mul:
			case arith_div:
			case arith_mod:		return SymbolArithExpression.create(source.get_data_type(), operator, loperand, roperand);
			case bit_and:
			case bit_or:
			case bit_xor:
			case left_shift:
			case righ_shift:	return SymbolBitwsExpression.create(source.get_data_type(), operator, loperand, roperand);
			case logic_and:
			case logic_or:		return SymbolLogicExpression.create(operator, loperand, roperand);
			case greater_tn:
			case greater_eq:
			case smaller_tn:
			case smaller_eq:
			case equal_with:
			case not_equals:	return SymbolRelationExpression.create(operator, loperand, roperand);
			default:			throw new IllegalArgumentException("Unsupported operator: " + operator.toString());
			}
		}
	}
	private	SymbolNode	parse_cir_expression(CirExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirNameExpression) {
			return this.parse_cir_name_expr((CirNameExpression) source);
		}
		else if(source instanceof CirConstExpression) {
			return this.parse_cir_cons_expr((CirConstExpression) source);
		}
		else if(source instanceof CirStringLiteral) {
			return this.parse_cir_slit_expr((CirStringLiteral) source);
		}
		else if(source instanceof CirDefaultValue) {
			return this.parse_cir_default_value((CirDefaultValue) source);
		}
		else if(source instanceof CirDeferExpression) {
			return this.parse_cir_defer_expr((CirDeferExpression) source);
		}
		else if(source instanceof CirFieldExpression) {
			return this.parse_cir_field_expr((CirFieldExpression) source);
		}
		else if(source instanceof CirAddressExpression) {
			return this.parse_cir_addr_expr((CirAddressExpression) source);
		}
		else if(source instanceof CirCastExpression) {
			return this.parse_cir_cast_expr((CirCastExpression) source);
		}
		else if(source instanceof CirWaitExpression) {
			return this.parse_cir_wait_expr((CirWaitExpression) source);
		}
		else if(source instanceof CirInitializerBody) {
			return this.parse_cir_init_body((CirInitializerBody) source);
		}
		else if(source instanceof CirComputeExpression) {
			return this.parse_cir_comp_expr((CirComputeExpression) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	
	/* AST-oriented parsing */
	/**
	 * @param source
	 * @return the scope where the source is defined
	 */
	private	CScope		find_scope(AstNode source) {
		while(source != null) {
			if(source instanceof AstScopeNode) {
				return ((AstScopeNode) source).get_scope();
			}
			else {
				source = source.get_parent();
			}
		}
		return null;
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode	parse_ast(AstNode source) throws Exception {
		SymbolNode target;
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstExpression) {
			target = this.parse_ast_expression((AstExpression) source);
		}
		else if(source instanceof AstStatement) {
			target = this.parse_ast_statement((AstStatement) source);
		}
		else {
			target = this.parse_ast_otherwise(source);
		}
		if(target.get_source() != null) {
			target.set_source(source);
		}
		return target;
	}
	/* AST-Specifier Class */
	private	SymbolNode	parse_ast_stmt_list(AstStatementList source) throws Exception {
		List<SymbolExpression> expressions = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_statements(); k++) {
			expressions.add((SymbolExpression) this.parse_ast(source.get_statement(k)));
		}
		return SymbolExpressionList.create(expressions);
	}
	private	SymbolNode	parse_ast_label(AstLabel source) throws Exception {
		AstFunctionDefinition function = source.get_function_of();
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(function.get_body()); AstNode parent;
		while(!queue.isEmpty()) {
			parent = queue.poll();
			if(parent instanceof AstLabeledStatement) {
				if(((AstLabeledStatement) parent).get_label().get_name().equals(source.get_name())) {
					return this.parse_stmt(parent);
				}
			}
			for(int k = 0; k < parent.number_of_children(); k++) {
				queue.add(parent.get_child(k));
			}
		}
		throw new IllegalArgumentException("No label found: " + source.get_name());
	}
	private	SymbolNode	parse_ast_argument_list(AstArgumentList source) throws Exception {
		List<SymbolExpression> arguments = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add((SymbolExpression) this.parse_ast(source.get_argument(k)));
		}
		return SymbolArgumentList.create(arguments);
	}
	private	SymbolNode	parse_ast_declaration(AstDeclaration source) throws Exception {
		if(source.has_declarator_list()) {
			return this.parse_ast(source.get_declarator_list());
		}
		else {
			return this.parse_stmt(source.get_parent());
		}
	}
	private	SymbolNode	parse_ast_declaration_list(AstDeclarationList source) throws Exception {
		List<SymbolExpression> expressions = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_declarations(); k++) {
			expressions.add((SymbolExpression) this.parse_ast(source.get_declaration(k)));
		}
		return SymbolExpressionList.create(expressions);
	}
	private	SymbolNode	parse_ast_declarator(AstDeclarator source) throws Exception {
		if(source.get_production() == DeclaratorProduction.identifier) {
			return this.parse_ast(source.get_identifier());
		}
		else {
			return this.parse_ast(source.get_declarator());
		}
	}
	private	SymbolNode	parse_ast_name(AstName source) throws Exception {
		CName cname = source.get_cname();
		if(cname == null) {
			CScope scope = this.find_scope(source);
			return SymbolIdentifier.create(CBasicTypeImpl.
					void_type, source.get_name(), scope);
		}
		else {
			return this.parse_name(cname);
		}
	}
	private	SymbolNode	parse_ast_init_declarator(AstInitDeclarator source) throws Exception {
		SymbolExpression lvalue = (SymbolExpression) this.parse_ast(source.get_declarator());
		SymbolExpression rvalue;
		if(source.has_initializer()) {
			rvalue = (SymbolExpression) this.parse_ast(source.get_initializer());
		}
		else {
			rvalue = this.get_default_value(SymbolFactory.is_optimized(), lvalue.get_data_type());
		}
		return SymbolAssignExpression.create(lvalue.get_data_type(), COperator.assign, lvalue, rvalue);
	}
	private	SymbolNode	parse_ast_initializer(AstInitializer source) throws Exception {
		if(source.is_body()) {
			return this.parse_ast(source.get_body());
		}
		else {
			return this.parse_ast(source.get_expression());
		}
	}
	private	SymbolNode	parse_ast_initializer_body(AstInitializerBody source) throws Exception {
		return this.parse_ast(source.get_initializer_list());
	}
	private	SymbolNode	parse_ast_initializer_list(AstInitializerList source) throws Exception {
		List<SymbolExpression> elements = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_initializer(); k++) {
			elements.add((SymbolExpression) this.parse_ast(source.get_initializer(k)));
		}
		return SymbolInitializerList.create(elements);
	}
	private	SymbolNode	parse_ast_field_initializer(AstFieldInitializer source) throws Exception {
		return this.parse_ast(source.get_initializer());
	}
	private	SymbolNode	parse_ast_type_name(AstTypeName source) throws Exception {
		return SymbolType.create(source.get_type());
	}
	private	SymbolNode	parse_ast_parameter_declaration(AstParameterDeclaration source) throws Exception {
		if(source.has_declarator()) {
			SymbolExpression lvalue = (SymbolExpression) this.parse_ast(source.get_declarator());
			SymbolExpression rvalue = this.get_default_value(false, lvalue.get_data_type());
			return SymbolAssignExpression.create(lvalue.get_data_type(), COperator.assign, lvalue, rvalue);
		}
		else {
			throw new IllegalArgumentException("Invalid access: " + source.generate_code());
		}
	}
	private	SymbolNode	parse_ast_field(AstField source) throws Exception {
		return SymbolField.create(source.get_name());
	}
	private	SymbolNode	parse_parameter_declaration_list(AstParameterList source) throws Exception {
		List<SymbolExpression> expressions = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_parameters(); k++) {
			if(source.get_parameter(k).has_declarator())
				expressions.add((SymbolExpression) this.parse_ast(source.get_parameter(k)));
		}
		return SymbolExpressionList.create(expressions);
	}
	private	SymbolNode	parse_typedef_name(AstTypedefName source) throws Exception {
		return this.parse_name(source.get_cname());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_init_declarator_list(AstInitDeclaratorList source) throws Exception {
		List<SymbolExpression> elements = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_init_declarators(); k++) {
			elements.add((SymbolExpression) this.parse_ast(source.get_init_declarator(k)));
		}
		return SymbolExpressionList.create(elements);
	}
	/**
	 * 	<code>
	 * 	{AST-OTHERWISE}															<br>
	 * 		AstStatementList		-->	SymbolExpressionList					<br>
	 * 		AstLabel				-->	stmt(labeled_stmt)						<br>
	 * 		AstArgumentList			--> SymbolArgumentList						<br>
	 * 		AstDeclaration			-->	SymbolExpressionList [init_decl_list]	<br>
	 * 		AstDeclaration			-->	stmt(decl_stmt)							<br>
	 * 		AstInitDeclaratorList	-->	SymbolExpressionList {decl := value}	<br>
	 * 		AstInitDeclarator		-->	decl := parse(initializer)|default_value<br>
	 * 		AstName					--> parse(CName) | SymbolIdentifier			<br>
	 * 		AstInitializer			-->	parse(body|expression)					<br>
	 * 		AstInitializerList		-->	SymbolInitializerList					<br>
	 * 		AstFieldInitializer		-->	parse(initializer)						<br>
	 * 		AstTypeName				-->	SymbolType								<br>
	 * 		AstField				-->	SymbolField								<br>
	 * 		AstTypedefName			-->	SymbolType								<br>
	 * 		AstParameterDeclaration	-->	decl := default_value | stmt(source)	<br>
	 * 		AstParamList			-->	SymbolExpressionList					<br>
	 * 	</code>
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_otherwise(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstField) {
			return this.parse_ast_field((AstField) source);
		}
		else if(source instanceof AstTypedefName) {
			return this.parse_typedef_name((AstTypedefName) source);
		}
		else if(source instanceof AstDeclarator) {
			return this.parse_ast_declarator((AstDeclarator) source);
		}
		else if(source instanceof AstArgumentList) {
			return this.parse_ast_argument_list((AstArgumentList) source);
		}
		else if(source instanceof AstDeclaration) {
			return this.parse_ast_declaration((AstDeclaration) source);
		}
		else if(source instanceof AstDeclarationList) {
			return this.parse_ast_declaration_list((AstDeclarationList) source);
		}
		else if(source instanceof AstName) {
			return this.parse_ast_name((AstName) source);
		}
		else if(source instanceof AstInitDeclarator) {
			return this.parse_ast_init_declarator((AstInitDeclarator) source);
		}
		else if(source instanceof AstInitializer) {
			return this.parse_ast_initializer((AstInitializer) source);
		}
		else if(source instanceof AstInitializerBody) {
			return this.parse_ast_initializer_body((AstInitializerBody) source);
		}
		else if(source instanceof AstInitializerList) {
			return this.parse_ast_initializer_list((AstInitializerList) source);
		}
		else if(source instanceof AstFieldInitializer) {
			return this.parse_ast_field_initializer((AstFieldInitializer) source);
		}
		else if(source instanceof AstParameterDeclaration) {
			return this.parse_ast_parameter_declaration((AstParameterDeclaration) source);
		}
		else if(source instanceof AstParameterList) {
			return this.parse_parameter_declaration_list((AstParameterList) source);
		}
		else if(source instanceof AstTypeName) {
			return this.parse_ast_type_name((AstTypeName) source);
		}
		else if(source instanceof AstField) {
			return this.parse_name(((AstField) source).get_cname());
		}
		else if(source instanceof AstLabel) {
			return this.parse_ast_label((AstLabel) source);
		}
		else if(source instanceof AstStatementList) {
			return this.parse_ast_stmt_list((AstStatementList) source);
		}
		else if(source instanceof AstInitDeclaratorList) {
			return this.parse_ast_init_declarator_list((AstInitDeclaratorList) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/* AST-Statement Class */
	private	SymbolNode	parse_ast_break_stmt(AstBreakStatement source) throws Exception {
		SymbolExpression lvalue = this.parse_nexp(source);
		AstNode parent = source.get_parent();
		while(parent != null) {
			if(parent instanceof AstSwitchStatement
				|| parent instanceof AstWhileStatement
				|| parent instanceof AstDoWhileStatement
				|| parent instanceof AstForStatement) {
				SymbolExpression rvalue = this.parse_stmt(parent);
				return SymbolAssignExpression.create(lvalue.
						get_data_type(), COperator.assign, lvalue, rvalue);
			}
			else {
				parent = parent.get_parent();
			}
		}
		throw new IllegalArgumentException("No loop-switch statement is found");
	}
	private	SymbolNode	parse_ast_continue_stmt(AstContinueStatement source) throws Exception {
		SymbolExpression lvalue = this.parse_nexp(source);
		AstNode parent = source.get_parent();
		while(parent != null) {
			SymbolExpression rvalue;
			if(parent instanceof AstWhileStatement) {
				rvalue = this.parse_stmt(((AstWhileStatement) parent).get_condition());
			}
			else if(parent instanceof AstForStatement) {
				rvalue = this.parse_stmt(((AstForStatement) parent).get_condition());
			}
			else if(parent instanceof AstDoWhileStatement) {
				rvalue = this.parse_stmt(((AstDoWhileStatement) parent).get_condition());
			}
			else {
				parent = parent.get_parent(); rvalue = null;
			}
			
			if(rvalue != null) {
				return SymbolAssignExpression.create(lvalue.
						get_data_type(), COperator.assign, lvalue, rvalue);
			}
		}
		throw new IllegalArgumentException("No loop-switch statement is found");
	}
	private	SymbolNode	parse_ast_goto_stmt(AstGotoStatement source) throws Exception {
		SymbolExpression lvalue = this.parse_nexp(source);
		SymbolExpression rvalue = (SymbolExpression) this.parse_ast(source.get_label());
		return SymbolAssignExpression.create(lvalue.get_data_type(), COperator.assign, lvalue, rvalue);
	}
	private	SymbolNode	parse_ast_default_stmt(AstDefaultStatement source) throws Exception {
		return this.parse_stmt(source);
	}
	private	SymbolNode	parse_ast_labeled_stmt(AstLabeledStatement source) throws Exception {
		return this.parse_stmt(source);
	}
	private	SymbolNode	parse_ast_declaration_stmt(AstDeclarationStatement source) throws Exception {
		return this.parse_ast(source.get_declaration());
	}
	private	SymbolNode	parse_ast_expression_stmt(AstExpressionStatement source) throws Exception {
		if(source.has_expression()) {
			return this.parse_ast(source.get_expression());
		}
		else {
			return this.parse_cons(Boolean.TRUE);
		}
	}
	private	SymbolNode	parse_ast_switch_stmt(AstSwitchStatement source) throws Exception {
		return this.parse_ast(source.get_condition());
	}
	private	SymbolNode	parse_ast_if_stmt(AstIfStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast(source.get_condition());
		condition = this.parse_cond(condition, true);
		SymbolExpression t_operand = this.parse_stmt(source.get_true_branch());
		SymbolExpression f_operand;
		if(source.has_else()) {
			f_operand = (SymbolExpression) this.parse_ast(source.get_false_branch());
		}
		else {
			f_operand = this.parse_stmt(source);
		}
		return SymbolIfElseExpression.create(t_operand.get_data_type(), condition, t_operand, f_operand);
	}
	private	SymbolNode	parse_ast_case_stmt(AstCaseStatement source) throws Exception {
		AstNode parent = source.get_parent();
		while(parent != null) {
			if(parent instanceof AstSwitchStatement) {
				SymbolExpression loperand = (SymbolExpression) this.
						parse_ast(((AstSwitchStatement) parent).get_condition());
				SymbolExpression roperand = (SymbolExpression) this.parse_ast(source.get_expression());
				return SymbolRelationExpression.create(COperator.equal_with, loperand, roperand);
			}
			else {
				parent = parent.get_parent();
			}
		}
		throw new IllegalArgumentException("No-switch-enclosing");
	}
	private	SymbolNode	parse_ast_while_stmt(AstWhileStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast(source.get_condition());
		condition = this.parse_cond(condition, true);
		SymbolExpression t_operand = this.parse_stmt(source.get_body());
		SymbolExpression f_operand = this.parse_stmt(source);
		return SymbolIfElseExpression.create(t_operand.get_data_type(), condition, t_operand, f_operand);
	}
	private	SymbolNode	parse_ast_do_while_stmt(AstDoWhileStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast(source.get_condition());
		condition = this.parse_cond(condition, true);
		SymbolExpression t_operand = this.parse_stmt(source.get_body());
		SymbolExpression f_operand = this.parse_stmt(source);
		return SymbolIfElseExpression.create(t_operand.get_data_type(), condition, t_operand, f_operand);
	}
	private	SymbolNode	parse_ast_for_stmt(AstForStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast(source.get_condition());
		condition = this.parse_cond(condition, true);
		SymbolExpression t_operand = this.parse_stmt(source.get_body());
		SymbolExpression f_operand = this.parse_stmt(source);
		return SymbolIfElseExpression.create(t_operand.get_data_type(), condition, t_operand, f_operand);
	}
	private	SymbolNode	parse_ast_return_stmt(AstReturnStatement source) throws Exception {
		SymbolExpression rvalue;
		if(source.has_expression()) {
			rvalue = (SymbolExpression) this.parse_ast(source.get_expression());
		}
		else {
			rvalue = this.get_default_value(false, CBasicTypeImpl.void_type);
		}
		SymbolExpression lvalue = this.parse_retr(rvalue.get_data_type(), source);
		return SymbolAssignExpression.create(lvalue.get_data_type(), COperator.assign, lvalue, rvalue);
	}
	private	SymbolNode	parse_ast_compound_stmt(AstCompoundStatement source) throws Exception {
		if(source.has_statement_list()) {
			return this.parse_ast(source.get_statement_list());
		}
		else {
			return SymbolExpressionList.create(new ArrayList<SymbolExpression>());
		}
		// return this.parse_stmt(source);
	}
	/**
	 * 	<code>
	 * 	{AST-STATEMENTS}														<br>
	 * 		AstBreakStatement			-->	next(break) := stmt(loop)			<br>
	 * 		AstContinueStatement		-->	next(continue) := stmt(loop.cond)	<br>
	 * 		AstGotoStatement			-->	next(goto) := stmt(label)			<br>
	 * 		AstReturnStatement			-->	return#func_name := expression		<br>
	 * 		AstSwitchStatement			-->	parse(switch.condition)				<br>
	 * 		AstIfStatement				--> condition ? stmt(true) : stmt(false)<br>	
	 * 		AstCaseStatement			-->	switch.condition == case.expression	<br>
	 * 		AstWhileStatement			-->	condition ? stmt(body) : stmt(loop)	<br>
	 * 		AstDoWhileStatement			-->	condition ? stmt(body) : stmt(loop)	<br>
	 * 		AstForStatement				-->	condition ? stmt(body) : stmt(loop)	<br>
	 * 		AstDefaultStatement			-->	stmt(source)						<br>
	 * 		AstLabeledStatement			-->	stmt(source)						<br>
	 * 		AstCompoundStatement		-->	SymbolExpressionList				<br>
	 * 	</code>
	 * 	
	 * 	@param source	the statement to be symbolized
	 * 	@return
	 * 	@throws Exception
	 */
	private	SymbolNode	parse_ast_statement(AstStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstCompoundStatement) {
			return this.parse_ast_compound_stmt((AstCompoundStatement) source);
		}
		else if(source instanceof AstBreakStatement) {
			return this.parse_ast_break_stmt((AstBreakStatement) source);
		}
		else if(source instanceof AstContinueStatement) {
			return this.parse_ast_continue_stmt((AstContinueStatement) source);
		}
		else if(source instanceof AstGotoStatement) {
			return this.parse_ast_goto_stmt((AstGotoStatement) source);
		}
		else if(source instanceof AstLabeledStatement) {
			return this.parse_ast_labeled_stmt((AstLabeledStatement) source);
		}
		else if(source instanceof AstReturnStatement) {
			return this.parse_ast_return_stmt((AstReturnStatement) source);
		}
		else if(source instanceof AstIfStatement) {
			return this.parse_ast_if_stmt((AstIfStatement) source);
		}
		else if(source instanceof AstSwitchStatement) {
			return this.parse_ast_switch_stmt((AstSwitchStatement) source);
		}
		else if(source instanceof AstCaseStatement) {
			return this.parse_ast_case_stmt((AstCaseStatement) source);
		}
		else if(source instanceof AstDefaultStatement) {
			return this.parse_ast_default_stmt((AstDefaultStatement) source);
		}
		else if(source instanceof AstDeclarationStatement) {
			return this.parse_ast_declaration_stmt((AstDeclarationStatement) source);
		}
		else if(source instanceof AstExpressionStatement) {
			return this.parse_ast_expression_stmt((AstExpressionStatement) source);
		}
		else if(source instanceof AstForStatement) {
			return this.parse_ast_for_stmt((AstForStatement) source);
		}
		else if(source instanceof AstWhileStatement) {
			return this.parse_ast_while_stmt((AstWhileStatement) source);
		}
		else if(source instanceof AstDoWhileStatement) {
			return this.parse_ast_do_while_stmt((AstDoWhileStatement) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/* AST-Expression Class */
	private	SymbolNode	parse_ast_id_expr(AstIdExpression source) throws Exception {
		if(source.get_cname() != null) {
			return this.parse_name(source.get_cname());
		}
		else {
			CScope scope = this.find_scope(source);
			String name = source.get_name();
			return SymbolIdentifier.create(source.get_value_type(), name, scope);
		}
	}
	private	SymbolNode	parse_ast_constant(AstConstant source) throws Exception {
		return SymbolConstant.create(source.get_constant());
	}
	private	SymbolNode	parse_ast_literal(AstLiteral source) throws Exception {
		return SymbolLiteral.create(source.get_literal());
	}
	private	SymbolNode	parse_ast_array_expr(AstArrayExpression source) throws Exception {
		SymbolExpression array = (SymbolExpression) this.parse_ast(source.get_array_expression());
		SymbolExpression index = (SymbolExpression) this.parse_ast(source.get_dimension_expression());
		SymbolExpression address = SymbolArithExpression.create(array.get_data_type(), COperator.arith_add, array, index);
		return SymbolUnaryExpression.create(source.get_value_type(), COperator.dereference, address);
	}
	private	SymbolNode	parse_ast_cast_expr(AstCastExpression source) throws Exception {
		SymbolType cast_type = (SymbolType) this.parse_ast(source.get_typename());
		SymbolExpression operand = (SymbolExpression) this.parse_ast(source.get_expression());
		return SymbolCastExpression.create(cast_type, operand);
	}
	private	SymbolNode	parse_ast_comma_expr(AstCommaExpression source) throws Exception {
		List<SymbolExpression> elements = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			elements.add((SymbolExpression) this.parse_ast(source.get_expression(k)));
		}
		return SymbolExpressionList.create(elements);
	}
	private	SymbolNode	parse_ast_cond_expr(AstConditionalExpression source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast(source.get_condition());
		condition = this.parse_cond(condition, true);
		SymbolExpression t_operand = (SymbolExpression) this.parse_ast(source.get_true_branch());
		SymbolExpression f_operand = (SymbolExpression) this.parse_ast(source.get_false_branch());
		return SymbolIfElseExpression.create(source.get_value_type(), condition, t_operand, f_operand);
	}
	private	SymbolNode	parse_ast_const_expr(AstConstExpression source) throws Exception {
		return this.parse_ast(source.get_expression());
	}
	private	SymbolNode	parse_ast_paranth_expr(AstParanthExpression source) throws Exception {
		return this.parse_ast(source.get_sub_expression());
	}
	private	SymbolNode	parse_ast_field_expr(AstFieldExpression source) throws Exception {
		SymbolExpression body = (SymbolExpression) this.parse_ast(source.get_body());
		SymbolField field = (SymbolField) this.parse_ast(source.get_field());
		return SymbolFieldExpression.create(source.get_value_type(), body, field);
	}
	private	SymbolNode	parse_ast_fun_call_expr(AstFunCallExpression source) throws Exception {
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
	private	SymbolNode	parse_ast_sizeof_expr(AstSizeofExpression source) throws Exception {
		CType data_type;
		if(source.is_expression()) {
			data_type = source.get_expression().get_value_type();
		}
		else {
			data_type = source.get_typename().get_type();
		}
		
		if(SymbolFactory.get_template() != null) {
			int size = SymbolFactory.get_template().sizeof(data_type);
			return this.parse_cons(Integer.valueOf(size));
		}
		else {
			return SymbolIdentifier.create(CBasicTypeImpl.int_type, "sizeof", data_type);
		}
	}
	private	SymbolNode	parse_ast_unary_expr(AstUnaryExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_ast(source.get_operand());
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case positive:		return operand;
		case negative:		
		case bit_not:
		case logic_not:
		case address_of:
		case dereference:	return SymbolUnaryExpression.create(source.get_value_type(), operator, operand);
		case increment:
		{
			SymbolExpression lvalue = operand;
			SymbolExpression rvalue = SymbolArithExpression.create(operand.get_data_type(), 
					COperator.arith_add, operand, this.parse_cons(Integer.valueOf(1)));
			return SymbolAssignExpression.create(lvalue.get_data_type(), COperator.assign, lvalue, rvalue);
		}
		case decrement:
		{
			SymbolExpression lvalue = operand;
			SymbolExpression rvalue = SymbolArithExpression.create(operand.get_data_type(), 
					COperator.arith_sub, operand, this.parse_cons(Integer.valueOf(1)));
			return SymbolAssignExpression.create(lvalue.get_data_type(), COperator.assign, lvalue, rvalue);
		}
		default:	throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	private	SymbolNode	parse_ast_postfix_expr(AstPostfixExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_ast(source.get_operand());
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case increment:
		{
			SymbolExpression lvalue = operand;
			SymbolExpression rvalue = SymbolArithExpression.create(operand.get_data_type(), 
					COperator.arith_add, operand, this.parse_cons(Integer.valueOf(1)));
			return SymbolAssignExpression.create(lvalue.get_data_type(), COperator.increment, lvalue, rvalue);
		}
		case decrement:
		{
			SymbolExpression lvalue = operand;
			SymbolExpression rvalue = SymbolArithExpression.create(operand.get_data_type(), 
					COperator.arith_sub, operand, this.parse_cons(Integer.valueOf(1)));
			return SymbolAssignExpression.create(lvalue.get_data_type(), COperator.increment, lvalue, rvalue);
		}
		default:	throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	private	SymbolNode	parse_ast_binary_expr(AstBinaryExpression source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_ast(source.get_loperand());
		SymbolExpression roperand = (SymbolExpression) this.parse_ast(source.get_roperand());
		COperator operator = source.get_operator().get_operator();
		CType type = source.get_value_type();
		
		switch(operator) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:		return SymbolArithExpression.create(type, operator, loperand, roperand);
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:	return SymbolBitwsExpression.create(type, operator, loperand, roperand);
		case logic_and:
		case logic_or:		return SymbolLogicExpression.create(operator, loperand, roperand);
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:	return SymbolRelationExpression.create(operator, loperand, roperand);
		case assign:		return SymbolAssignExpression.create(type, operator, loperand, roperand);
		case arith_add_assign:
		case arith_sub_assign:
		case arith_mul_assign:
		case arith_div_assign:
		case arith_mod_assign:
		{
			String name = operator.toString();
			name = name.substring(0, name.length() - 7).strip();
			operator = COperator.valueOf(name);
			SymbolExpression lvalue = loperand;
			SymbolExpression rvalue = SymbolArithExpression.create(type, operator, loperand, roperand);
			return SymbolAssignExpression.create(type, COperator.assign, lvalue, rvalue);
		}
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
		case left_shift_assign:
		case righ_shift_assign:
		{
			String name = operator.toString();
			name = name.substring(0, name.length() - 7).strip();
			operator = COperator.valueOf(name);
			SymbolExpression lvalue = loperand;
			SymbolExpression rvalue = SymbolBitwsExpression.create(type, operator, loperand, roperand);
			return SymbolAssignExpression.create(type, COperator.assign, lvalue, rvalue);
		}
		default:	throw new IllegalArgumentException("Unsupported operator: " + operator);
		}
	}
	/**
	 * 	<code>
	 * 	{AST-EXPRESSION}														<br>
	 * 		AstIdExpression				-->	SymbolIdentifier {CName|name#scope}	<br>
	 * 		AstIdExpression				-->	SymbolConstant 	{CEnumeratorName}	<br>
	 * 		AstConstant					-->	SymbolConstant						<br>
	 * 		AstLiteral					-->	SymbolLiteral	{literal: String}	<br>
	 * 		AstConstExpression			-->	parse(expr.sub_expression)			<br>
	 * 		AstParanthExpression		-->	parse(expr.sub_expression)			<br>
	 * 		AstArithBinaryExpression	-->	SymbolArithExpression{+,-,*,/,%}	<br>
	 * 		AstBitwsBinaryExpression	-->	SymbolBitwsExpression{&,|,^,<<,>>}	<br>
	 * 		AstLogicBinaryExpression	-->	SymbolLogicExpression{&&,||}		<br>
	 * 		AstRelationExpression		-->	SymbolRelationExpression{<,<=,>,>=}	<br>
	 * 		AstXXXAssignExpression		-->	SymbolAssignExpression{:=, inc}		<br>
	 * 		AstArrayExpression			-->	(defer (arith_add address index))	<br>
	 * 		AstCastExpression			--> (cast_type) parse(operand)			<br>
	 * 		AstConditionalExpression	-->	SymbolConditionalExpression			<br>
	 * 		AstFieldExpression			-->	SymbolFieldExpression				<br>
	 * 		AstFunCallExpression		-->	SymbolCallExpression				<br>
	 * 		AstSizeofExpression			-->	SymbolConstant	[template != null]	<br>
	 * 		AstSizeofExpression			--> SymbolIdentifier{sizeof#data_type}	<br>
	 * 		AstInitializerBody			--> parse(initializer_list)				<br>
	 * 		AstCommaExpression			-->	SymbolExpressionList				<br>
	 * 	</code>
	 * 	
	 * 	@param source	the expression to be symbolized
	 * 	@return
	 * 	@throws Exception
	 */
	private	SymbolNode	parse_ast_expression(AstExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstIdExpression) {
			return this.parse_ast_id_expr((AstIdExpression) source);
		}
		else if(source instanceof AstConstant) {
			return this.parse_ast_constant((AstConstant) source);
		}
		else if(source instanceof AstLiteral) {
			return this.parse_ast_literal((AstLiteral) source);
		}
		else if(source instanceof AstArrayExpression) {
			return this.parse_ast_array_expr((AstArrayExpression) source);
		}
		else if(source instanceof AstCastExpression) {
			return this.parse_ast_cast_expr((AstCastExpression) source);
		}
		else if(source instanceof AstCommaExpression) {
			return this.parse_ast_comma_expr((AstCommaExpression) source);
		}
		else if(source instanceof AstConditionalExpression) {
			return this.parse_ast_cond_expr((AstConditionalExpression) source);
		}
		else if(source instanceof AstConstExpression) {
			return this.parse_ast_const_expr((AstConstExpression) source);
		}
		else if(source instanceof AstParanthExpression) {
			return this.parse_ast_paranth_expr((AstParanthExpression) source);
		}
		else if(source instanceof AstFieldExpression) {
			return this.parse_ast_field_expr((AstFieldExpression) source);
		}
		else if(source instanceof AstInitializerBody) {
			return this.parse_ast_initializer_body((AstInitializerBody) source);
		}
		else if(source instanceof AstUnaryExpression) {
			return this.parse_ast_unary_expr((AstUnaryExpression) source);
		}
		else if(source instanceof AstBinaryExpression) {
			return this.parse_ast_binary_expr((AstBinaryExpression) source);
		}
		else if(source instanceof AstPostfixExpression) {
			return this.parse_ast_postfix_expr((AstPostfixExpression) source);
		}
		else if(source instanceof AstSizeofExpression) {
			return this.parse_ast_sizeof_expr((AstSizeofExpression) source);
		}
		else if(source instanceof AstFunCallExpression) {
			return this.parse_ast_fun_call_expr((AstFunCallExpression) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	
}
