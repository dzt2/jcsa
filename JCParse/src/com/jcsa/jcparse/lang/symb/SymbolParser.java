package com.jcsa.jcparse.lang.symb;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
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
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * It implements the parsel-algorithm for generating SymbolNode from Boolean,
 * Char, Short, Integer, Long, FLoat, Double, AstNode, CirNode, CirExecution.
 * 
 * @author yukimula
 *
 */
public class SymbolParser {
	
	/* definitions */
	/** the factory to create data type **/
	protected static final CTypeFactory type_factory = new CTypeFactory();
	/** used to parse sizeof(expression)**/
	private CRunTemplate	template;
	/** used to parse CirDefaultValue 	**/
	private	boolean			optimize;
	/**
	 * It creates an instance for parsing
	 */
	protected SymbolParser() {
		this.template = null;
		this.optimize = false;
	}
	/**
	 * It sets the parameters used in parsing algorithms
	 * @param template
	 * @param optimize
	 */
	protected void set(CRunTemplate template, boolean optimize) {
		this.template = template;
		this.optimize = optimize;
	}
	
	/* BAS-PARSE */
	/**
	 * @param value	{bool|char|short|int|long|float|double}
	 * @return		constant that represents the input value
	 * @throws Exception
	 */
	protected SymbolConstant parse_cons(Object value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}
		else {
			CConstant constant = new CConstant();
			if(value instanceof Boolean) {
				constant.set_bool(((Boolean) value).booleanValue());
			}
			else if(value instanceof Character) {
				constant.set_char(((Character) value).charValue());
			}
			else if(value instanceof Short) {
				constant.set_int(((Short) value).intValue());
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
			else {
				throw new IllegalArgumentException(value.getClass().getName());
			}
			return SymbolConstant.create(constant);
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return the boolean value of the expression in specified value-context
	 * @throws Exception
	 */
	protected SymbolExpression parse_bool(SymbolExpression expression, boolean value) throws Exception {
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
					return SymbolUnaryExpression.create(CBasicTypeImpl.
							bool_type, COperator.logic_not, expression);
				}
			}
			else {
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
		}
	}
	/**
	 * @param execution	{CirStatement|CirExecution}
	 * @return			do#execution_ID: int
	 * @throws Exception
	 */
	protected SymbolIdentifier parse_exec(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return SymbolIdentifier.create_exec(execution);
		}
	}
	
	/* AST-PARSE */
	/**
	 * 	<code>
	 * 	[SPECIFIERS]															<br>
	 * 		Declarator			|--	SymbolIdentifier(name)						<br>
	 * 		InitDeclarator		|--	SymbolBinaryExpression(:=)					<br>
	 * 		Initializer			|--	SymbolExpression(recursive)					<br>
	 * 		InitializerList		|--	SymbolInitializerList						<br>
	 * 		ArgumentList		|--	SymbolArgumentList							<br>
	 * 		TypeName			|--	SymbolType									<br>
	 * 		Operator			|--	SymbolOperator								<br>
	 * 		Field				|--	SymbolField									<br>
	 * 		Name				|--	SymbolIdentifier(name)						<br>
	 * 	[STATEMENTS]															<br>
	 * 		SwitchStatement		|--	SymbolBinaryExpression(switch := input)		<br>
	 * 		CaseStatement		|--	SymbolBinaryExpression(switch == value)		<br>
	 * 		DoWhileStatement	|--	SymbolBinaryExpression(while := condition)	<br>
	 * 		ExressionStatement	|--	SymbolBinaryExpression(#_ := expression)	<br>
	 * 							|--	SymbolBinaryExpression(for := condition)	<br>
	 * 		ForStatement		|--	SymbolBinaryExpression(for := condition)	<br>
	 * 		IfStatement			|--	SymbolBinaryExpression(if := condition)		<br>
	 * 		ReturnStatement		|--	SymbolBinaryExpression(return := condition)	<br>
	 * 		WhileStatement		|--	SymbolBinaryExpression(while := condition)	<br>
	 * 	[EXPRESSION]															<br>
	 * 		IdExpression		|--	SymbolIdentifier(cname|name)				<br>
	 * 							|--	SymbolConstant(enumerator)					<br>
	 * 		Constant			|--	SymbolConstant(constant)					<br>
	 * 		Literal				|--	SymbolLiteral(literal)						<br>
	 * 		ArithBinaryExpr		|--	SymbolBinaryExpression(+, -, *, /, %)		<br>
	 * 		ArithAssignExpr		|--	SymbolBinaryExpression(:=)					<br>
	 * 		BitwiseBinaryExpr	|--	SymbolBinaryExpression(&, |, ^, <<, >>)		<br>
	 * 		BitwiseAssignExpr	|--	SymbolBinaryExpression(:=)					<br>
	 * 		LogicBinaryExpr		|--	SymbolBinaryExpression(&&, ||)				<br>
	 * 		ShiftBinaryExpr		|--	SymbolBinaryExpression(<<, >>)				<br>
	 * 		ShiftAssignExpr		|--	SymbolBinaryExpression(:=)					<br>
	 * 		RelationExpression	|--	SymbolBinaryExpression(<,<=,>,>=,==,!=)		<br>
	 * 		AssignExpression	|--	SymbolBinaryExpression(:=)					<br>
	 * 		ArithUnaryExpr		|--	SymbolUnaryExpression(+, -)					<br>
	 * 		PointUnaryExpr		|--	SymbolUnaryExpression(*)					<br>
	 * 		LogicUnaryExpr		|--	SymbolUnaryExpression(!)					<br>
	 * 		BitwsUnaryExpr		|--	SymbolBinaryExpression(~)					<br>
	 * 		IncreUnaryExpr		|--	SymbolBinaryExpression(++, --)				<br>
	 * 		IncrePostExpr		|--	SymbolBinaryExpression(p++,p--)				<br>
	 * 		ParanthExpression	|--	SymbolExpression(recursive)					<br>
	 * 		ConstExpression		|--	SymbolExpression(recursive)					<br>
	 * 		SizeofExpression	|--	SymbolConstant(int: sizeof)					<br>
	 * 		InitializerBody		|--	SymbolInitializerList						<br>
	 * 		FunCallExpression	|--	SymbolCallExpression						<br>
	 * 		FieldExpression		|--	SymbolFieldExpression						<br>
	 * 		ArrayExpression		|--	SymbolUnaryExpression(*,+)					<br>
	 * 		CastExpression		|--	SymbolCastExpression						<br>
	 * 		ConditionalExpr		|--	SymbolConditionExpression					<br>
	 * 	[OTHERWISE]																<br>
	 * 		Report Unsupported Class Information Errors							<br>
	 * 	</code>
	 * @param source
	 * @return	
	 * @throws Exception
	 */
	private SymbolNode parse_ast(AstNode source) throws Exception {
		SymbolNode target;
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstDeclarator) {
			target = this.parse_ast_declarator((AstDeclarator) source);
		}
		else if(source instanceof AstName) {
			target = this.parse_ast_name((AstName) source);
		}
		else if(source instanceof AstField) {
			target = this.parse_field((AstField) source);
		}
		else if(source instanceof AstTypeName) {
			target = this.parse_ast_type_name((AstTypeName) source);
		}
		else if(source instanceof AstOperator) {
			target = this.parse_ast_operator((AstOperator) source);
		}
		else if(source instanceof AstInitializer) {
			target = this.parse_ast_initializer((AstInitializer) source);
		}
		else if(source instanceof AstInitializerBody) {
			target = this.parse_ast_initializer_body((AstInitializerBody) source);
		}
		else if(source instanceof AstInitializerList) {
			target = this.parse_ast_initializer_list((AstInitializerList) source);
		}
		else if(source instanceof AstFieldInitializer) {
			target = this.parse_ast_field_initializer((AstFieldInitializer) source);
		}
		else if(source instanceof AstInitDeclarator) {
			target = this.parse_ast_init_declarator((AstInitDeclarator) source);
		}
		else if(source instanceof AstArgumentList) {
			target = this.parse_ast_argument_list((AstArgumentList) source);
		}
		else if(source instanceof AstExpressionStatement) {
			target = this.parse_ast_expression_statement((AstExpressionStatement) source);
		}
		else if(source instanceof AstIfStatement) {
			target = this.parse_ast_if_statement((AstIfStatement) source);
		}
		else if(source instanceof AstCaseStatement) {
			target = this.parse_ast_case_statement((AstCaseStatement) source);
		}
		else if(source instanceof AstForStatement) {
			target = this.parse_ast_for_statement((AstForStatement) source);
		}
		else if(source instanceof AstDoWhileStatement) {
			target = this.parse_ast_do_while_statement((AstDoWhileStatement) source);
		}
		else if(source instanceof AstWhileStatement) {
			target = this.parse_ast_while_statement((AstWhileStatement) source);
		}
		else if(source instanceof AstSwitchStatement) {
			target = this.parse_ast_switch_statement((AstSwitchStatement) source);
		}
		else if(source instanceof AstReturnStatement) {
			target = this.parse_ast_return_statement((AstReturnStatement) source);
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
		else if(source instanceof AstUnaryExpression) {
			return this.parse_ast_unary_expression((AstUnaryExpression) source);
		}
		else if(source instanceof AstPostfixExpression) {
			target = this.parse_ast_postfix_expression((AstPostfixExpression) source);
		}
		else if(source instanceof AstBinaryExpression) {
			target = this.parse_ast_binary_expression((AstBinaryExpression) source);
		}
		else if(source instanceof AstParanthExpression) {
			target = this.parse_ast_paranth_expression((AstParanthExpression) source);
		}
		else if(source instanceof AstConstExpression) {
			target = this.parse_ast_const_expression((AstConstExpression) source);
		}
		else if(source instanceof AstArrayExpression) {
			target = this.parse_ast_array_expression((AstArrayExpression) source);
		}
		else if(source instanceof AstCastExpression) {
			target = this.parse_ast_cast_expression((AstCastExpression) source);
		}
		else if(source instanceof AstCommaExpression) {
			target = this.parse_ast_comma_expression((AstCommaExpression) source);
		}
		else if(source instanceof AstConditionalExpression) {
			target = this.parse_ast_conditional_expression((AstConditionalExpression) source);
		}
		else if(source instanceof AstFieldExpression) {
			target = this.parse_ast_field_expression((AstFieldExpression) source);
		}
		else if(source instanceof AstFunCallExpression) {
			target = this.parse_ast_fun_call_expression((AstFunCallExpression) source);
		}
		else if(source instanceof AstSizeofExpression) {
			target = this.parse_ast_sizeof_expression((AstSizeofExpression) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getName());
		}
		if(!target.has_source()) target.set_source(source);
		return target;
	}
	/* specifiers */
	/**
	 * Declarator	|--	SymbolIdentifier(name)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_declarator(AstDeclarator source) throws Exception {
		while(source.get_production() != DeclaratorProduction.identifier) {
			source = source.get_declarator();
		}
		return this.parse_ast(source.get_identifier());
	}
	/**
	 * Name			|--	SymbolIdentifier(name#scope, type)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_name(AstName source) throws Exception {
		CName cname = source.get_cname();
		if(cname == null) {
			/* find the lexical scope */
			AstNode node = source; 
			AstScopeNode scope = null;
			while(node != null) {
				if(node instanceof AstScopeNode) {
					scope = (AstScopeNode) node;
					break;
				}
				else {
					node = node.get_parent();
				}
			}
			
			if(scope == null)
				return SymbolIdentifier.create(CBasicTypeImpl.void_type, source.get_name(), "null");
			else 
				return SymbolIdentifier.create(CBasicTypeImpl.void_type, source.get_name(), scope.hashCode());
		}
		else if(cname instanceof CInstanceName) {
			CType type = ((CInstanceName) cname).get_instance().get_type();
			return SymbolIdentifier.create_name(type, cname);
		}
		else if(cname instanceof CParameterName) {
			CType type = ((CParameterName) cname).get_parameter().get_type();
			return SymbolIdentifier.create_name(type, cname);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + cname.getClass().getSimpleName());
		}
	}
	/**
	 * initializer	|--	SymbolExpression(recursive)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_initializer(AstInitializer source) throws Exception {
		if(source.is_body()) {
			return this.parse_ast(source.get_body());
		}
		else {
			return this.parse_ast(source.get_expression());
		}
	}
	/**
	 * initializer_body |-- SymbolInitializerList
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_initializer_body(AstInitializerBody source) throws Exception {
		return this.parse_ast(source.get_initializer_list());
	}
	/**
	 * initializer_list |-- {parse(field_initializer)+}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_initializer_list(AstInitializerList source) throws Exception {
		List<SymbolExpression> elements = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_initializer(); k++) {
			elements.add((SymbolExpression) this.parse_ast(source.get_initializer(k)));
		}
		return SymbolInitializerList.create(CBasicTypeImpl.void_type, elements);
	}
	/**
	 * field_initializer |-- parse(initializer)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_field_initializer(AstFieldInitializer source) throws Exception {
		return this.parse_ast(source.get_initializer());
	}
	/**
	 * field |-- SymbolField(name)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_field(AstField source) throws Exception {
		return SymbolField.create(source.get_name());
	}
	/**
	 * type_name |-- SymbolType
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_type_name(AstTypeName source) throws Exception {
		return SymbolType.create(source.get_type());
	}
	/**
	 * operator |-- SymbolOperator
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_operator(AstOperator source) throws Exception {
		return SymbolOperator.create(source.get_operator());
	}
	/**
	 * arg_list |-- (parse(argument)+)
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
	/* statements */
	/**
	 * switch_statement |-- if#switch_id := condition
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_switch_statement(AstSwitchStatement source) throws Exception {
		SymbolExpression roperand = (SymbolExpression) this.parse_ast(source.get_condition());
		SymbolExpression loperand = SymbolIdentifier.create(roperand.get_data_type(), "if", source.get_key());
		loperand.set_source(source);
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * case_statement |-- {switch.condition == value}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_case_statement(AstCaseStatement source) throws Exception {
		AstNode node = source;
		AstSwitchStatement switch_node = null;
		while(node != null) {
			if(node instanceof AstSwitchStatement) {
				switch_node = (AstSwitchStatement) node;
				break;
			}
			else {
				node = node.get_parent();
			}
		}
		
		if(switch_node == null) {
			throw new IllegalArgumentException("Switch-scope is not found");
		}
		else {
			SymbolExpression roperand = (SymbolExpression) this.parse_ast(source.get_expression());
			SymbolExpression loperand = (SymbolExpression) this.parse_ast(switch_node.get_condition());
			return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, COperator.equal_with, loperand, roperand);
		}
	}
	/**
	 * do_while_statement |-- (while#ast_key := condition)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_do_while_statement(AstDoWhileStatement source) throws Exception {
		SymbolExpression loperand = SymbolIdentifier.create_astn(CBasicTypeImpl.bool_type, source);
		SymbolExpression roperand = this.parse_bool((SymbolExpression) this.parse_ast(source.get_condition()), true);
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * expr_statement |-- # := expression | default_value
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_expression_statement(AstExpressionStatement source) throws Exception {
		SymbolExpression loperand, roperand;
		if(source.has_expression()) {
			roperand = (SymbolExpression) this.parse_ast(source.get_expression());
			loperand = SymbolIdentifier.create_astn(roperand.get_data_type(), source);
		}
		else {
			roperand = this.parse_cons(Boolean.TRUE);
			loperand = SymbolIdentifier.create_astn(roperand.get_data_type(), source);
		}
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * for_statement |-- for := condition
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_for_statement(AstForStatement source) throws Exception {
		SymbolBinaryExpression condition = (SymbolBinaryExpression) this.parse_ast(source.get_condition());
		SymbolExpression roperand = this.parse_bool(condition.get_roperand(), true);
		SymbolExpression loperand = SymbolIdentifier.create_astn(CBasicTypeImpl.bool_type, source);
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * while_statement |-- while#key := condition
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_while_statement(AstWhileStatement source) throws Exception {
		SymbolExpression roperand = this.parse_bool((SymbolExpression) this.parse_ast(source.get_condition()), true);
		SymbolExpression loperand = SymbolIdentifier.create_astn(CBasicTypeImpl.bool_type, source);
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * if_statement |-- if#key := condition
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_if_statement(AstIfStatement source) throws Exception {
		SymbolExpression roperand = this.parse_bool((SymbolExpression) this.parse_ast(source.get_condition()), true);
		SymbolExpression loperand = SymbolIdentifier.create_astn(CBasicTypeImpl.bool_type, source);
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * ret_statement |-- ret#func_name	:= expression|void
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_return_statement(AstReturnStatement source) throws Exception {
		/* generate loperand and roperand */
		SymbolExpression loperand, roperand;
		if(source.has_expression()) {
			roperand = (SymbolExpression) this.parse_ast(source.get_expression());
			loperand = SymbolIdentifier.create_astn(roperand.get_data_type(), source);
		}
		else {
			roperand = this.parse_cons(Integer.valueOf(0));
			loperand = SymbolIdentifier.create_astn(CBasicTypeImpl.void_type, source);
		}
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * init_declarator |-- loperand := default_value
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_init_declarator(AstInitDeclarator source) throws Exception {
		SymbolExpression loperand, roperand;
		loperand = (SymbolExpression) this.parse_ast(source.get_declarator());
		if(source.has_initializer()) {
			roperand = (SymbolExpression) this.parse_ast(source.get_initializer());
		}
		else {
			roperand = loperand;
		}
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/* expression */
	/**
	 * id_expr |-- SymbolIdentifier | SymbolConstant
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_id_expression(AstIdExpression source) throws Exception {
		CName cname = source.get_cname();
		if(cname == null) {
			AstNode node = source; AstScopeNode scope = null;
			while(node != null) {
				if(node instanceof AstScopeNode) {
					scope = (AstScopeNode) node;
					break;
				}
				else {
					node = node.get_parent();
				}
			}
			
			if(scope != null) {
				return SymbolIdentifier.create(source.get_value_type(), source.get_name(), scope.hashCode());
			}
			else {
				return SymbolIdentifier.create(source.get_value_type(), source.get_name(), "null");
			}
		}
		else if(cname instanceof CInstanceName) {
			return SymbolIdentifier.create_name(source.get_value_type(), cname);
		}
		else if(cname instanceof CParameterName) {
			return SymbolIdentifier.create_name(source.get_value_type(), cname);
		}
		else if(cname instanceof CEnumeratorName) {
			CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
			return this.parse_cons(Integer.valueOf(enumerator.get_value()));
		}
		else {
			throw new IllegalArgumentException("Invalid cname: " + cname);
		}
	}
	/**
	 * constant |-- SymbolConstant
	 * @param source
	 * @return
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
	private SymbolNode parse_ast_unary_expression(AstUnaryExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_ast(source.get_operand());
		CType type = source.get_value_type();
		switch(source.get_operator().get_operator()) {
		case positive:		return operand;
		case negative:		return SymbolUnaryExpression.create(type, COperator.negative, operand);
		case bit_not:		return SymbolUnaryExpression.create(type, COperator.bit_not, operand);
		case logic_not:		return this.parse_bool(operand, false);
		case address_of:	return SymbolUnaryExpression.create(type, COperator.address_of, operand);
		case dereference:	return SymbolUnaryExpression.create(type, COperator.dereference, operand);
		case increment:		return SymbolUnaryExpression.create(type, COperator.increment, operand);
		case decrement:		return SymbolUnaryExpression.create(type, COperator.decrement, operand);
		default:	throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_binary_expression(AstBinaryExpression source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_ast(source.get_loperand());
		SymbolExpression roperand = (SymbolExpression) this.parse_ast(source.get_roperand());
		COperator operator = source.get_operator().get_operator(); CType type = source.get_value_type();
		
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
		case righ_shift:	return SymbolBinaryExpression.create(type, operator, loperand, roperand);
		case logic_and:
		case logic_or:		loperand = this.parse_bool(loperand, true); roperand = this.parse_bool(roperand, true);
							return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, operator, loperand, roperand);
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:	return SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, operator, loperand, roperand);
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
							String name = operator.name();
							name = name.substring(0, name.length() - 7).strip();
							operator = COperator.valueOf(name);
							roperand = SymbolBinaryExpression.create(type, operator, loperand, roperand);
							return SymbolBinaryExpression.create(type, COperator.assign, loperand, roperand);
		default:	throw new IllegalArgumentException(source.generate_code());
		}
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
	private SymbolNode parse_ast_cast_expression(AstCastExpression source) throws Exception {
		SymbolType type = (SymbolType) this.parse_ast(source.get_typename());
		SymbolExpression value = (SymbolExpression) this.parse_ast(source.get_expression());
		return SymbolCastExpression.create(type, value);
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
	private SymbolNode parse_ast_comma_expression(AstCommaExpression source) throws Exception {
		AstExpression expression = source.get_expression(source.number_of_arguments() - 1);
		return this.parse_ast(expression);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_conditional_expression(AstConditionalExpression source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast(source.get_condition());
		SymbolExpression toperand = (SymbolExpression) this.parse_ast(source.get_true_branch());
		SymbolExpression foperand = (SymbolExpression) this.parse_ast(source.get_false_branch());
		condition = this.parse_bool(condition, true);
		return SymbolConditionExpression.create(source.get_value_type(), condition, toperand, foperand);
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
	private SymbolNode parse_ast_fun_call_expression(AstFunCallExpression source) throws Exception {
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
	private SymbolNode parse_ast_postfix_expression(AstPostfixExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_ast(source.get_operand());
		COperator operator = source.get_operator().get_operator();
		CType type = source.get_value_type();
		switch(operator) {
		case increment:	return SymbolUnaryExpression.create(type, COperator.arith_add_assign, operand);
		case decrement:	return SymbolUnaryExpression.create(type, COperator.arith_sub_assign, operand);
		default:		throw new IllegalArgumentException("Invalid operator: " + operator.toString());
		}
	}
	/**
	 * sizeof(expression) --> SymbolConstant | SymbolIdentifier(sizeof)
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_ast_sizeof_expression(AstSizeofExpression source) throws Exception {
		CType data_type;
		if(source.is_expression())
			data_type = source.get_expression().get_value_type();
		else
			data_type = source.get_typename().get_type();
		data_type = CTypeAnalyzer.get_value_type(data_type);
		
		/* constant if template is set */
		if(this.template != null) {
			return this.parse_cons(Integer.valueOf(this.template.sizeof(data_type)));
		}
		/* call_expression in case of no-template */
		else {
			return SymbolIdentifier.create_astn(CBasicTypeImpl.int_type, source);
		}
	}
	/**
	 * @param source
	 * @param template
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression parse_astn(AstNode source) throws Exception {
		return (SymbolExpression) this.parse_ast(source);
	}
	
	/* CIR-PARSE */
	/**
	 * 	<code>
	 * 	[SPECIFIERS]															<br>
	 * 		CirArgumentList			|--	SymbolArgumentList						<br>
	 * 		CirType					|--	SymbolType								<br>
	 * 		CirField				|--	SymbolField								<br>
	 * 		CirLabel				|--	SymbolIdentifier(execution)				<br>
	 *	[STATEMENTS]															<br>
	 *		CirAssignStatement		|--	SymbolBinaryExpression(:=)				<br>
	 *		CirCallStatement		|--	SymbolCallExpression					<br>
	 *		CirIfStatement			|--	SymbolConditionExpression				<br>
	 *		CirCaseStatement		|--	SymbolConditionExpression				<br>
	 *		CirGotoStatement		|--	SymbolIdentifier(execution)				<br>
	 *		CirTagStatement			|--	SymbolIdentifier(execution)				<br>
	 *	[EXPRESSION]															<br>
	 *		CirNameExpression		|--	SymbolIdentifier(name)					<br>
	 *		CirDeferExpression		|--	SymbolUnaryExpression(*)				<br>
	 *		CirFieldExpression		|--	SymbolFieldExpression					<br>
	 *		CirConstExpression		|--	SymbolConstant							<br>
	 *		CirStringLiteral		|--	SymbolLiteral							<br>
	 *		CirAddressExpression	|--	SymbolUnaryExpression(&)				<br>
	 *		CirDefaultValue			|--	SymbolConstant|SymbolIdentifier			<br>
	 *		CirCastExpression		|--	SymbolCastExpression					<br>
	 *		CirInitializerBody		|--	SymbolInitializerList					<br>
	 *		CirWaitExpression		|--	SymbolCallExpression					<br>
	 *	[OTHERWISE]																<br>
	 *		return unsupported class exception error							<br>
	 * 	</code>
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir(CirNode source) throws Exception {
		SymbolNode target;
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirField) {
			target = this.parse_cir_field((CirField) source);
		}
		else if(source instanceof CirType) {
			target = this.parse_cir_type((CirType) source);
		}
		else if(source instanceof CirLabel) {
			target = this.parse_cir_label((CirLabel) source);
		}
		else if(source instanceof CirArgumentList) {
			target = this.parse_cir_argument_list((CirArgumentList) source);
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
		else if(source instanceof CirCallStatement) {
			target = this.parse_cir_call_statement((CirCallStatement) source);
		}
		else if(source instanceof CirGotoStatement) {
			target = this.parse_cir_goto_statement((CirGotoStatement) source);
		}
		else if(source instanceof CirTagStatement) {
			target = this.parse_cir_tag_statement((CirTagStatement) source);
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
		else if(source instanceof CirWaitExpression) {
			target = this.parse_cir_wait_expression((CirWaitExpression) source);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + source.getClass());
		}
		if(!target.has_source()) { target.set_source(source); }
		return target;
	}
	/* specifiers */
	private SymbolNode parse_cir_field(CirField source) throws Exception {
		return SymbolField.create(source.get_name());
	}
	private SymbolNode parse_cir_type(CirType source) throws Exception {
		return SymbolType.create(source.get_typename());
	}
	private SymbolNode parse_cir_label(CirLabel source) throws Exception {
		int target = source.get_target_node_id();
		CirNode node = source.get_tree().get_node(target);
		return this.parse_exec(node.execution_of());
	}
	private SymbolNode parse_cir_argument_list(CirArgumentList source) throws Exception {
		List<SymbolExpression> arguments = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add((SymbolExpression) this.parse_cir(source.get_argument(k)));
		}
		return SymbolArgumentList.create(arguments);
	}
	/* statements */
	private SymbolNode parse_cir_assign_statement(CirAssignStatement source) throws Exception {
		SymbolExpression lvalue = (SymbolExpression) this.parse_cir(source.get_lvalue());
		SymbolExpression rvalue = (SymbolExpression) this.parse_cir(source.get_rvalue());
		return SymbolBinaryExpression.create(lvalue.get_data_type(), COperator.assign, lvalue, rvalue);
	}
	private SymbolNode parse_cir_call_statement(CirCallStatement source) throws Exception {
		SymbolExpression function = (SymbolExpression) this.parse_cir(source.get_function());
		SymbolArgumentList arguments = (SymbolArgumentList) this.parse_cir(source.get_arguments());
		
		CType func_type = CTypeAnalyzer.get_value_type(function.get_data_type());
		if(func_type instanceof CPointerType) {
			func_type = CTypeAnalyzer.get_value_type(((CPointerType) func_type).get_pointed_type());
		}
		CType type;
		if(func_type instanceof CFunctionType) {
			type = ((CFunctionType) func_type).get_return_type();
		}
		else {
			throw new IllegalArgumentException(func_type.generate_code());
		}
		
		return SymbolCallExpression.create(type, function, arguments);
	}
	private SymbolNode parse_cir_if_statement(CirIfStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_cir(source.get_condition());
		condition = this.parse_bool(condition, true);
		SymbolExpression t_value = (SymbolExpression) this.parse_cir(source.get_true_label());
		SymbolExpression f_value = (SymbolExpression) this.parse_cir(source.get_false_label());
		return SymbolConditionExpression.create(t_value.get_data_type(), condition, t_value, f_value);
	}
	private SymbolNode parse_cir_case_statement(CirCaseStatement source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_cir(source.get_condition());
		condition = this.parse_bool(condition, true);
		CirExecution next = source.execution_of();
		next = next.get_graph().get_execution(next.get_id() + 1);
		SymbolExpression t_value = (SymbolExpression) this.parse_exec(next);
		SymbolExpression f_value = (SymbolExpression) this.parse_cir(source.get_false_label());
		return SymbolConditionExpression.create(t_value.get_data_type(), condition, t_value, f_value);
	}
	private SymbolNode parse_cir_goto_statement(CirGotoStatement source) throws Exception {
		return this.parse_exec(source.execution_of());
	}
	private SymbolNode parse_cir_tag_statement(CirTagStatement source) throws Exception {
		return this.parse_exec(source.execution_of());
	}
	/* expression */
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode parse_cir_name_expression(CirNameExpression source) throws Exception {
		if(source instanceof CirDeclarator) {
			CName cname = ((CirDeclarator) source).get_cname();
			return SymbolIdentifier.create_name(source.get_data_type(), cname);
		}
		else if(source instanceof CirIdentifier) {
			CName cname = ((CirIdentifier) source).get_cname();
			return SymbolIdentifier.create_name(source.get_data_type(), cname);
		}
		else if(source instanceof CirImplicator) {
			AstNode ast_source = source.get_ast_source();
			return SymbolIdentifier.create_astn(source.get_data_type(), ast_source);
		}
		else if(source instanceof CirReturnPoint) {
			return SymbolIdentifier.create_cirn(source.get_data_type(), source);
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
		if(this.optimize) {
			CType type = CTypeAnalyzer.get_value_type(source.get_data_type());
			if(type instanceof CBasicType) {
				switch(((CBasicType) type).get_tag()) {
				case c_bool:	return this.parse_cons(Boolean.FALSE);
				case c_char:
				case c_uchar:	return this.parse_cons(Character.valueOf('\0'));
				case c_short:
				case c_ushort:
				case c_int:
				case c_uint:	return this.parse_cons(Integer.valueOf(0));
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:	return this.parse_cons(Long.valueOf(0L));
				case c_float:	return this.parse_cons(Float.valueOf(0.0f));
				case c_double:
				case c_ldouble:	return this.parse_cons(Double.valueOf(0.0));
				default:		
					return SymbolIdentifier.create_cirn(source.get_data_type(), source);
				}
			}
			else if(type instanceof CPointerType) {
				return this.parse_cons(Integer.valueOf(0));
			}
			else if(type instanceof CEnumType) {
				return this.parse_cons(Integer.valueOf(0));
			}
			else {
				return SymbolIdentifier.create_cirn(source.get_data_type(), source);
			}
		}
		else {
			return SymbolIdentifier.create_cirn(source.get_data_type(), source);
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
	protected SymbolExpression parse_cirn(CirNode source) throws Exception {
		return (SymbolExpression) this.parse_cir(source);
	}
	
}
