package com.jcsa.jcparse.parse.symbolic;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
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
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;


/**
 * It implements the construction and generation of symbolic expressions.
 * 
 * @author yukimula
 *
 */
public class SymbolFactory {
	
	/* definitions */
	/** the template used to support sizeof-operation **/
	private CRunTemplate 	template;
	/** true if to transform the default value based on their data types **/
	private boolean			optimize;
	/**
	 * private constructor for the symbol node generation and parsing
	 */
	private SymbolFactory() { this.template = null; this.optimize = false; }
	
	/* singleton mode */
	/** the factory is used to create data type in symbolic expression **/
	public static final CTypeFactory type_factory = new CTypeFactory();
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
	
	/* basic data getters */
	/**
	 * @param declarator
	 * @return it derives the identifier in the declarator
	 * @throws Exception
	 */
	private AstName 			get_ast_name(AstDeclarator declarator) throws Exception {
		if(declarator == null) {
			throw new IllegalArgumentException("Invalid declarator: null");
		}
		else {
			while(declarator.get_production() != DeclaratorProduction.identifier) {
				declarator = declarator.get_declarator();
			}
			return declarator.get_identifier();
		}
	}
	/**
	 * @param source
	 * @return it derives the name of the function where the source is defined
	 * @throws Exception
	 */
	private AstName 			get_func_name(AstNode source) throws Exception {
		if(source == null || source.get_function_of() == null) {
			throw new IllegalArgumentException("Invalid source: " + source);
		}
		else {
			AstFunctionDefinition def = source.get_function_of();
			return this.get_ast_name(def.get_declarator());
		}
	}
	/**
	 * @param source
	 * @return the scope where the source belongs to
	 * @throws Exception
	 */
	private AstScopeNode 		get_scope_of(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			while(source != null) {
				if(source instanceof AstScopeNode) {
					return (AstScopeNode) source;
				}
				else {
					source = source.get_parent();
				}
			}
			return null;
		}
	}
	/**
	 * 	<code>
	 * 		BOOL	-->		FALSE
	 * 		CHAR	--> 	'\0'
	 * 		SHORT	-->		0
	 * 		INT		-->		0
	 * 		LONG	-->		0L
	 * 		FLOAT	-->		0.0f
	 * 		DOUBLE	-->		0.0
	 * 		OTHER	-->		default@type
	 * 	</code>
	 * 	
	 * 	@param type
	 * 	@return
	 * 	@throws Exception
	 */
	private SymbolExpression 	get_default_value(CType type) throws Exception {
		type = CTypeAnalyzer.get_value_type(type);
		if(this.optimize) {
			if(type instanceof CBasicType) {
				switch(((CBasicType) type).get_tag()) {
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
				default:		break;
				}
			}
			else if(type instanceof CArrayType) {
				return this.parse_cons(Long.valueOf(0L));
			}
			else if(type instanceof CPointerType) {
				return this.parse_cons(Long.valueOf(0L));
			}
			else if(type instanceof CEnumType) {
				return this.parse_cons(Integer.valueOf(0));
			}
		}
		SymbolExpression expression = SymbolIdentifier.
				create(type, "default", type.generate_code());
		expression.set_source(type); return expression;
	}
	
	/* basic parsing */
	/**
	 * It parses the source to a symbolic constant expression.
	 * @param source	[Boolean|Character|Short|Integer|Long|Float|Double|CConstant]
	 * @return			the constant parsed from the input value
	 * @throws Exception
	 */
	private SymbolConstant 		parse_cons(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
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
				constant.set_int(((Short) source).shortValue());
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
			else {
				throw new IllegalArgumentException(source.getClass().getSimpleName());
			}
			return SymbolConstant.create(constant);
		}
	}
	/**
	 * It transforms the symbolic expression to a boolean version
	 * @param expression	the expression to be transformed to boolean
	 * @param value			the value that expression is expected to hold
	 * @return				expr | !expr | expr != 0 | expr == 0
	 * @throws Exception
	 */
	private SymbolExpression	parse_bool(SymbolExpression expression, boolean value) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			if(CTypeAnalyzer.is_boolean(data_type)) {	/** expression **/
				if(value) {
					return (SymbolExpression) expression.clone();
				}
				else {									/** !expression **/
					return SymbolUnaryExpression.create(CBasicTypeImpl.
							bool_type, COperator.logic_not, expression);
				}
			}
			else {
				SymbolExpression loperand = expression;
				SymbolExpression roperand = this.parse_cons(Integer.valueOf(0));
				if(value) {								/** expression != 0 **/
					return SymbolBinaryExpression.create(CBasicTypeImpl.
							bool_type, COperator.not_equals, loperand, roperand);
				}
				else {									/** expression == 0 **/
					return SymbolBinaryExpression.create(CBasicTypeImpl.
							bool_type, COperator.equal_with, loperand, roperand);
				}
			}
		}
	}
	/**
	 * It transforms the CirStatement | CirExecution to generate id-expression
	 * @param source	CirStatement || CirExecution
	 * @return			(@exec#exec_id: int)
	 * @throws Exception
	 */
	private SymbolExpression	parse_exec(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirStatement) {
			CirExecution execution = ((CirStatement) source).execution_of();
			SymbolExpression expression = SymbolIdentifier.
						create(CBasicTypeImpl.int_type, "@exec", execution);
			expression.set_source(execution); return expression;
		}
		else if(source instanceof CirExecution) {
			CirExecution execution = (CirExecution) source;
			SymbolExpression expression = SymbolIdentifier.
					create(CBasicTypeImpl.int_type, "@exec", execution);
			expression.set_source(execution); return expression;
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	
	/* AST-based parsing */
	/**
	 * 	<code>
	 * 	TODO implement this comment...
	 * 	</code>
	 * 	@param source	the AST-node to be parsed from the factory
	 * 	@return			the symbolic node parsed from the AST-node
	 * 	@throws Exception
	 */
	private	SymbolNode	parse_ast_node(AstNode source) throws Exception {
		SymbolNode target;
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstStatement) {
			target = this.parse_ast_statement((AstStatement) source);
		}
		else if(source instanceof AstExpression) {
			target = this.parse_ast_expression((AstExpression) source);
		}
		else {
			target = this.parse_ast_otherwise(source);
		}
		if(!target.has_source()) { target.set_source(source); }
		return target;
	}
	/* AST-SPECIFIERS */
	/**
	 * @param source
	 * @return decl --> parse(decl.sub_decl()) | parse(decl.name())
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_declarator(AstDeclarator source) throws Exception {
		if(source.get_production() == DeclaratorProduction.identifier) {
			return this.parse_ast_node(source.get_identifier());
		}
		else {
			return this.parse_ast_node(source.get_declarator());
		}
	}
	/**
	 * @param source
	 * @return	identifier(name#scope: type) | constant(enumerator: int)
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_name(AstName source) throws Exception {
		/* 0. declarations and initialization */
		CName cname = source.get_cname(); String name = source.get_name();
		
		/* 1. when no cname is defined, return void-identifier */
		if(cname == null) {
			AstScopeNode scope = this.get_scope_of(source);
			CBasicType data_type = CBasicTypeImpl.void_type;
			if(scope == null) {
				return SymbolIdentifier.create(data_type, name, null);
			}
			else {
				return SymbolIdentifier.create(data_type, name, scope.get_scope().hashCode());
			}
		}
		/* 2. variable or parameter declaration name */
		else if(cname instanceof CInstanceName) {
			CInstance instance = ((CInstanceName) cname).get_instance();
			return SymbolIdentifier.create(instance.get_type(), name, cname.get_scope().hashCode());
		}
		else if(cname instanceof CParameterName) {
			CInstance instance = ((CParameterName) cname).get_parameter();
			return SymbolIdentifier.create(instance.get_type(), name, cname.get_scope().hashCode());
		}
		/* 3. enumerator name --> constant of integer */
		else if(cname instanceof CEnumeratorName) {
			int value = ((CEnumeratorName) cname).get_enumerator().get_value();
			return this.parse_cons(Integer.valueOf(value));
		}
		else {
			throw new IllegalArgumentException("Invalid-class " + cname.getClass().getSimpleName());
		}
	}
	/**
	 * @param source
	 * @return initializer --> parse(source.body|source.expression)
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_initializer(AstInitializer source) throws Exception {
		if(source.is_body()) {
			return this.parse_ast_node(source.get_body());
		}
		else {
			return this.parse_ast_node(source.get_expression());
		}
	}
	/**
	 * @param source
	 * @return init_body --> parse(init_body.init_list())
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_initializer_body(AstInitializerBody source) throws Exception {
		return this.parse_ast_node(source.get_initializer_list());
	}
	/**
	 * @param source
	 * @return init_list --> init_list{parse(element[k...])+}
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_initializer_list(AstInitializerList source) throws Exception {
		List<SymbolExpression> elements = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_initializer(); k++) {
			elements.add((SymbolExpression) this.parse_ast_node(source.get_initializer(k)));
		}
		return SymbolInitializerList.create(elements);
	}
	/**
	 * @param source
	 * @return field_initializer --> parse(initializer)
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_field_initializer(AstFieldInitializer source) throws Exception {
		return this.parse_ast_node(source.get_initializer());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_field(AstField source) throws Exception {
		return SymbolField.create(source.get_name());
	}
	/**
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_operator(AstOperator source) throws Exception {
		COperator operator;
		switch(source.get_operator()) {
		case arith_add_assign:	operator = COperator.arith_add;		break;
		case arith_sub_assign:	operator = COperator.arith_sub;		break;
		case arith_mul_assign:	operator = COperator.arith_mul;		break;
		case arith_div_assign:	operator = COperator.arith_div;		break;
		case arith_mod_assign:	operator = COperator.arith_mod;		break;
		case bit_and_assign:	operator = COperator.bit_and;		break;
		case bit_or_assign:		operator = COperator.bit_or;		break;
		case bit_xor_assign:	operator = COperator.bit_xor;		break;
		case left_shift_assign:	operator = COperator.left_shift;	break;
		case righ_shift_assign:	operator = COperator.righ_shift;	break;
		default:				operator = source.get_operator();	break;
		}
		return SymbolOperator.create(operator);
	}
	/**
	 * @param source
	 * @return SymbolType
	 * @throws Exception 
	 */
	private	SymbolNode	parse_ast_typename(AstTypeName source) throws Exception {
		return SymbolType.create(source.get_type());
	}
	/**
	 * @param source
	 * @return arg_list --> (parse(expression)+)
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_argument_list(AstArgumentList source) throws Exception {
		List<SymbolExpression> arguments = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add((SymbolExpression) this.parse_ast_node(source.get_argument(k)));
		}
		return SymbolArgumentList.create(arguments);
	}
	/**
	 * @param source
	 * @return init_declarator --> {declarator := initializer|default_value}
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_init_declarator(AstInitDeclarator source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_ast_node(source.get_declarator());
		SymbolExpression roperand;
		if(source.has_initializer()) {
			roperand = (SymbolExpression) this.parse_ast_node(source.get_initializer());
		}
		else {
			roperand = this.get_default_value(loperand.get_data_type());
		}
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * @param source
	 * @return init_list({declarator := initializer|default_value}+)
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_init_declarator_list(AstInitDeclaratorList source) throws Exception {
		List<SymbolExpression> elements = new ArrayList<SymbolExpression>();
		for(int k = 0; k < source.number_of_init_declarators(); k++) {
			elements.add((SymbolExpression) this.parse_ast_node(source.get_init_declarator(k)));
		}
		return SymbolInitializerList.create(elements);
	}
	/**
	 * @param source
	 * @return @stmt#ast_id := init_list({declarator := initializer | default_value})
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_declaration(AstDeclaration source) throws Exception {
		SymbolExpression roperand, loperand = SymbolIdentifier.create(CBasicTypeImpl.int_type, "@stmt", source.get_key());
		if(source.has_declarator_list()) {
			roperand = (SymbolExpression) this.parse_ast_node(source.get_declarator_list());
		}
		else {
			roperand = SymbolInitializerList.create(new ArrayList<SymbolExpression>());
		}
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * @param source
	 * @return	(@keys#keyword: int) [AstKeyword]
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_keyword(AstKeyword source) throws Exception {
		if(source.get_keyword() == CKeyword.c89_return) {
			AstName func_name = this.get_func_name(source);
			SymbolExpression reference = (SymbolExpression) this.parse_ast_node(func_name);
			return SymbolIdentifier.create(reference.get_data_type(), "return", func_name.get_name());
		}
		return SymbolIdentifier.create(CBasicTypeImpl.bool_type, "@keys", source.get_keyword());
	}
	/**
	 * @param source
	 * @return @stmt#ast_key 
	 * @throws Exception
	 */
	private SymbolNode	parse_ast_statement_list(AstStatementList source) throws Exception {
		return SymbolIdentifier.create(CBasicTypeImpl.int_type, "@stmt", source.get_key());
	}
	/**
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_otherwise(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstDeclaration) {
			return this.parse_ast_declaration((AstDeclaration) source);
		}
		else if(source instanceof AstInitDeclaratorList) {
			return this.parse_ast_init_declarator_list((AstInitDeclaratorList) source);
		}
		else if(source instanceof AstInitDeclarator) {
			return this.parse_ast_init_declarator((AstInitDeclarator) source);
		}
		else if(source instanceof AstDeclarator) {
			return this.parse_ast_declarator((AstDeclarator) source);
		}
		else if(source instanceof AstName) {
			return this.parse_ast_name((AstName) source);
		}
		else if(source instanceof AstOperator) {
			return this.parse_ast_operator((AstOperator) source);
		}
		else if(source instanceof AstTypeName) {
			return this.parse_ast_typename((AstTypeName) source);
		}
		else if(source instanceof AstKeyword) {
			return this.parse_ast_keyword((AstKeyword) source);
		}
		else if(source instanceof AstStatementList) {
			return this.parse_ast_statement_list((AstStatementList) source);
		}
		else if(source instanceof AstInitializer) {
			return this.parse_ast_initializer((AstInitializer) source);
		}
		else if(source instanceof AstInitializerList) {
			return this.parse_ast_initializer_list((AstInitializerList) source);
		}
		else if(source instanceof AstFieldInitializer) {
			return this.parse_ast_field_initializer((AstFieldInitializer) source);
		}
		else if(source instanceof AstField) {
			return this.parse_ast_field((AstField) source);
		}
		else if(source instanceof AstArgumentList) {
			return this.parse_ast_argument_list((AstArgumentList) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/* AST-STATEMENTS */
	/**
	 * @param source
	 * @return @stmt#ast_key: int
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_break_statement(AstBreakStatement source) throws Exception {
		return SymbolIdentifier.create(CBasicTypeImpl.int_type, "@stmt", source.get_key());
	}
	/**
	 * @param source
	 * @return @stmt#ast_key: int
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_continue_statement(AstContinueStatement source) throws Exception {
		return SymbolIdentifier.create(CBasicTypeImpl.int_type, "@stmt", source.get_key());
	}
	/**
	 * @param source
	 * @return decl_stmt --> parse(decl_stmt.declaration)
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_declaration_statement(AstDeclarationStatement source) throws Exception {
		return this.parse_ast_node(source.get_declaration());
	}
	/**
	 * @param source
	 * @return @stmt#ast_key: int
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_default_statement(AstDefaultStatement source) throws Exception {
		return SymbolIdentifier.create(CBasicTypeImpl.int_type, "@stmt", source.get_key());
	}
	/**
	 * @param source
	 * @return @stmt#ast_key: int
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_goto_statement(AstGotoStatement source) throws Exception {
		return SymbolIdentifier.create(CBasicTypeImpl.int_type, "@stmt", source.get_key());
	}
	/**
	 * @param source
	 * @return @stmt#ast_key: int
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_labeled_statement(AstLabeledStatement source) throws Exception {
		return SymbolIdentifier.create(CBasicTypeImpl.int_type, "@stmt", source.get_key());
	}
	/**
	 * @param source
	 * @return @keys#switch := switch.condition
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_switch_statement(AstSwitchStatement source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_ast_node(source.get_switch());
		SymbolExpression roperand = (SymbolExpression) this.parse_ast_node(source.get_condition());
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * @param source
	 * @return case_stmt --> case := (switch_loperand == case_constant)
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_case_statement(AstCaseStatement source) throws Exception {
		SymbolExpression roperand = (SymbolExpression) this.parse_ast_node(source.get_expression());
		AstNode node = source; 
		while(node != null) {
			if(node instanceof AstSwitchStatement) {
				AstSwitchStatement statement = (AstSwitchStatement) node;
				SymbolExpression loperand = (SymbolExpression) this.parse_ast_node(statement.get_condition());
				SymbolExpression condition = 
						SymbolBinaryExpression.create(CBasicTypeImpl.bool_type, COperator.equal_with, loperand, roperand);
				SymbolExpression reference = (SymbolExpression) this.parse_ast_node(source.get_case());
				return SymbolBinaryExpression.create(reference.get_data_type(), COperator.assign, reference, condition);
			}
			else {
				node = node.get_parent();
			}
		}
		throw new IllegalArgumentException("Unable to find in switch statement");
	}
	/**
	 * @param source
	 * @return keyword := condition
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_if_statement(AstIfStatement source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_ast_node(source.get_if());
		SymbolExpression roperand = (SymbolExpression) this.parse_ast_node(source.get_condition());
		roperand = this.parse_bool(roperand, true);
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * @param source
	 * @return keyword := condition
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_while_statement(AstWhileStatement source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_ast_node(source.get_while());
		SymbolExpression roperand = (SymbolExpression) this.parse_ast_node(source.get_condition());
		roperand = this.parse_bool(roperand, true);
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * @param source
	 * @return keyword := condition
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_do_while_statement(AstDoWhileStatement source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_ast_node(source.get_do());
		SymbolExpression roperand = (SymbolExpression) this.parse_ast_node(source.get_condition());
		roperand = this.parse_bool(roperand, true);
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_for_statement(AstForStatement source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_ast_node(source.get_for());
		SymbolExpression roperand = (SymbolExpression) this.parse_ast_node(source.get_condition());
		roperand = this.parse_bool(roperand, true);
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * @param source
	 * @return @stmt#key
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_compound_statement(AstCompoundStatement source) throws Exception {
		return SymbolIdentifier.create(CBasicTypeImpl.int_type, "@stmt", source.get_key());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_return_statement(AstReturnStatement source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_ast_node(source.get_return());
		SymbolExpression roperand;
		if(source.has_expression()) {
			roperand = (SymbolExpression) this.parse_ast_node(source.get_expression());
		}
		else {
			roperand = this.get_default_value(loperand.get_data_type());
		}
		return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
	}
	/**
	 * @param source
	 * @return parse(expression) | true
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_expression_statement(AstExpressionStatement source) throws Exception {
		if(source.has_expression()) {
			return this.parse_ast_node(source.get_expression());
		}
		else {
			return this.parse_cons(Boolean.TRUE);
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_statement(AstStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstBreakStatement) {
			return this.parse_ast_break_statement((AstBreakStatement) source);
		}
		else if(source instanceof AstCaseStatement) {
			return this.parse_ast_case_statement((AstCaseStatement) source);
		}
		else if(source instanceof AstContinueStatement) {
			return this.parse_ast_continue_statement((AstContinueStatement) source);
		}
		else if(source instanceof AstCompoundStatement) {
			return this.parse_ast_compound_statement((AstCompoundStatement) source);
		}
		else if(source instanceof AstDeclarationStatement) {
			return this.parse_ast_declaration_statement((AstDeclarationStatement) source);
		}
		else if(source instanceof AstDefaultStatement) {
			return this.parse_ast_default_statement((AstDefaultStatement) source);
		}
		else if(source instanceof AstDoWhileStatement) {
			return this.parse_ast_do_while_statement((AstDoWhileStatement) source);
		}
		else if(source instanceof AstExpressionStatement) {
			return this.parse_ast_expression_statement((AstExpressionStatement) source);
		}
		else if(source instanceof AstForStatement) {
			return this.parse_ast_for_statement((AstForStatement) source);
		}
		else if(source instanceof AstGotoStatement) {
			return this.parse_ast_goto_statement((AstGotoStatement) source);
		}
		else if(source instanceof AstIfStatement) {
			return this.parse_ast_if_statement((AstIfStatement) source);
		}
		else if(source instanceof AstLabeledStatement) {
			return this.parse_ast_labeled_statement((AstLabeledStatement) source);
		}
		else if(source instanceof AstReturnStatement) {
			return this.parse_ast_return_statement((AstReturnStatement) source);
		}
		else if(source instanceof AstSwitchStatement) {
			return this.parse_ast_switch_statement((AstSwitchStatement) source);
		}
		else if(source instanceof AstWhileStatement) {
			return this.parse_ast_while_statement((AstWhileStatement) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/* AST-EXPRESSION */
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode	parse_ast_expression(AstExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstArrayExpression) {
			return this.parse_ast_array_expression((AstArrayExpression) source);
		}
		else if(source instanceof AstIdExpression) {
			return this.parse_ast_id_expression((AstIdExpression) source);
		}
		else if(source instanceof AstConstant) {
			return this.parse_ast_constant((AstConstant) source);
		}
		else if(source instanceof AstLiteral) {
			return this.parse_ast_literal((AstLiteral) source);
		}
		else if(source instanceof AstBinaryExpression) {
			return this.parse_ast_binary_expression((AstBinaryExpression) source);
		}
		else if(source instanceof AstUnaryExpression) {
			return this.parse_ast_unary_expression((AstUnaryExpression) source);
		}
		else if(source instanceof AstPostfixExpression) {
			return this.parse_ast_postfix_expression((AstPostfixExpression) source);
		}
		else if(source instanceof AstCastExpression) {
			return this.parse_ast_cast_expression((AstCastExpression) source);
		}
		else if(source instanceof AstCommaExpression) {
			return this.parse_ast_comma_expression((AstCommaExpression) source);
		}
		else if(source instanceof AstConditionalExpression) {
			return this.parse_ast_conditional_expression((AstConditionalExpression) source);
		}
		else if(source instanceof AstConstExpression) {
			return this.parse_ast_const_expression((AstConstExpression) source);
		}
		else if(source instanceof AstFieldExpression) {
			return this.parse_ast_field_expression((AstFieldExpression) source);
		}
		else if(source instanceof AstFunCallExpression) {
			return this.parse_ast_func_call_expression((AstFunCallExpression) source);
		}
		else if(source instanceof AstInitializerBody) {
			return this.parse_ast_initializer_body((AstInitializerBody) source);
		}
		else if(source instanceof AstParanthExpression) {
			return this.parse_ast_paranth_expression((AstParanthExpression) source);
		}
		else if(source instanceof AstSizeofExpression) {
			return this.parse_ast_sizeof_expression((AstSizeofExpression) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/**
	 * @param source
	 * @return *(array + dimension)
	 * @throws Exception
	 */
	private SymbolNode	parse_ast_array_expression(AstArrayExpression source) throws Exception {
		SymbolExpression base_addr = (SymbolExpression) this.parse_ast_node(source.get_array_expression());
		SymbolExpression dimension = (SymbolExpression) this.parse_ast_node(source.get_dimension_expression());
		
		CType addr_type = CTypeAnalyzer.get_value_type(base_addr.get_data_type());
		if(addr_type instanceof CArrayType) {
			addr_type = ((CArrayType) addr_type).get_element_type();
			addr_type = type_factory.get_pointer_type(addr_type);
		}
		SymbolExpression address = SymbolBinaryExpression.
				create(addr_type, COperator.arith_add, base_addr, dimension);
		
		CType data_type;
		if(addr_type instanceof CArrayType) {
			data_type = ((CArrayType) addr_type).get_element_type();
		}
		else if(addr_type instanceof CPointerType) {
			data_type = ((CPointerType) addr_type).get_pointed_type();
		}
		else {
			throw new IllegalArgumentException(addr_type.generate_code());
		}
		return SymbolUnaryExpression.create(data_type, COperator.dereference, address);
	}
	/**
	 * @param source
	 * @return 	CInstanceName	|--	(name#scope)
	 * 			CParameterName	|--	(name#scope)
	 * 			CEnumeratorName	|--	(const#intv)
	 * 			Otherwise		|--	(name#scope|null)
	 * @throws Exception
	 */
	private SymbolNode	parse_ast_id_expression(AstIdExpression source) throws Exception {
		CName cname = source.get_cname();
		if(cname == null) {
			AstScopeNode scope = this.get_scope_of(source);
			CBasicType data_type = CBasicTypeImpl.void_type;
			if(scope == null) {
				return SymbolIdentifier.create(data_type, source.get_name(), null);
			}
			else {
				return SymbolIdentifier.create(data_type, source.get_name(), scope.get_scope().hashCode());
			}
		}
		else if(cname instanceof CInstanceName) {
			CInstance instance = ((CInstanceName) cname).get_instance();
			return SymbolIdentifier.create(instance.get_type(), cname.get_name(), cname.get_scope().hashCode());
		}
		else if(cname instanceof CParameterName) {
			CInstance instance = ((CParameterName) cname).get_parameter();
			return SymbolIdentifier.create(instance.get_type(), cname.get_name(), cname.get_scope().hashCode());
		}
		else if(cname instanceof CEnumeratorName) {
			int value = ((CEnumeratorName) cname).get_enumerator().get_value();
			return this.parse_cons(Integer.valueOf(value));
		}
		else {
			throw new IllegalArgumentException(cname.getClass().getSimpleName());
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_constant(AstConstant source) throws Exception {
		return this.parse_cons(source.get_constant());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_literal(AstLiteral source) throws Exception {
		return SymbolLiteral.create(source.get_literal());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_cast_expression(AstCastExpression source) throws Exception {
		SymbolType cast_type = (SymbolType) this.parse_ast_node(source.get_typename());
		SymbolExpression operand = (SymbolExpression) this.parse_ast_node(source.get_expression());
		return SymbolCastExpression.create(cast_type, operand);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_binary_expression(AstBinaryExpression source) throws Exception {
		SymbolExpression loperand = (SymbolExpression) this.parse_ast_node(source.get_loperand());
		SymbolExpression roperand = (SymbolExpression) this.parse_ast_node(source.get_roperand());
		SymbolOperator operator = (SymbolOperator) this.parse_ast_node(source.get_operator());
		SymbolExpression value = SymbolBinaryExpression.
					create(source.get_value_type(), operator.get_operator(), loperand, roperand);
		
		if(source instanceof AstArithAssignExpression
				|| source instanceof AstBitwiseAssignExpression) {
			return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, value);
		}
		else { return value; }
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_unary_expression(AstUnaryExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_ast_node(source.get_operand());
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case positive:		return operand;
		case negative:		return SymbolUnaryExpression.create(source.get_value_type(), COperator.negative, operand);
		case bit_not:		return SymbolUnaryExpression.create(source.get_value_type(), COperator.bit_not, operand);
		case logic_not:		return SymbolUnaryExpression.create(CBasicTypeImpl.bool_type, COperator.logic_not, this.parse_bool(operand, true));
		case address_of:	return SymbolUnaryExpression.create(source.get_value_type(), COperator.address_of, operand);
		case dereference:	return SymbolUnaryExpression.create(source.get_value_type(), COperator.dereference, operand);
		case increment:		
		{
			SymbolExpression loperand = operand;
			SymbolExpression roperand = SymbolBinaryExpression.create(operand.get_data_type(), 
							COperator.arith_add, loperand, this.parse_cons(Integer.valueOf(1)));
			return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
		}
		case decrement:
		{
			SymbolExpression loperand = operand;
			SymbolExpression roperand = SymbolBinaryExpression.create(operand.get_data_type(), 
							COperator.arith_sub, loperand, this.parse_cons(Integer.valueOf(1)));
			return SymbolBinaryExpression.create(loperand.get_data_type(), COperator.assign, loperand, roperand);
		}
		default:	
		{
			throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode	parse_ast_postfix_expression(AstPostfixExpression source) throws Exception {
		SymbolExpression operand = (SymbolExpression) this.parse_ast_node(source.get_operand());
		switch(source.get_operator().get_operator()) {
		case increment:	return SymbolUnaryExpression.create(source.get_value_type(), COperator.increment, operand);
		case decrement:	return SymbolUnaryExpression.create(source.get_value_type(), COperator.decrement, operand);
		default:	throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_comma_expression(AstCommaExpression source) throws Exception {
		return this.parse_ast_node(source.get_expression(source.number_of_arguments() - 1));
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode	parse_ast_conditional_expression(AstConditionalExpression source) throws Exception {
		SymbolExpression condition = (SymbolExpression) this.parse_ast_node(source.get_condition());
		SymbolExpression t_operand = (SymbolExpression) this.parse_ast_node(source.get_true_branch());
		SymbolExpression f_operand = (SymbolExpression) this.parse_ast_node(source.get_false_branch());
		condition = this.parse_bool(condition, true);
		return SymbolConditionExpression.create(source.get_value_type(), condition, t_operand, f_operand);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_const_expression(AstConstExpression source) throws Exception {
		return this.parse_ast_node(source.get_expression());
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymbolNode	parse_ast_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse_ast_node(source.get_sub_expression());
	}
	/**
	 * @param source
	 * @return constant(int) | sizeof#type
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_sizeof_expression(AstSizeofExpression source) throws Exception {
		CType data_type;
		if(source.is_expression()) {
			data_type = source.get_expression().get_value_type();
		}
		else {
			data_type = source.get_typename().get_type();
		}
		data_type = CTypeAnalyzer.get_value_type(data_type);
		
		if(this.template != null) {
			int value = this.template.sizeof(data_type);
			return this.parse_cons(Integer.valueOf(value));
		}
		else {
			SymbolExpression expression = SymbolIdentifier.
					create(CBasicTypeImpl.int_type, "@sizeof", data_type.generate_code());
			expression.set_source(data_type); return expression;
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_field_expression(AstFieldExpression source) throws Exception {
		SymbolExpression body = (SymbolExpression) this.parse_ast_node(source.get_body());
		SymbolField field = (SymbolField) this.parse_ast_node(source.get_field());
		return SymbolFieldExpression.create(source.get_value_type(), body, field);
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	SymbolNode	parse_ast_func_call_expression(AstFunCallExpression source) throws Exception {
		SymbolExpression function = (SymbolExpression) this.parse_ast_node(source.get_function());
		SymbolArgumentList arguments;
		if(source.has_argument_list()) {
			arguments = (SymbolArgumentList) this.parse_ast_node(source.get_argument_list());
		}
		else {
			arguments = SymbolArgumentList.create(new ArrayList<SymbolExpression>());
		}
		return SymbolCallExpression.create(source.get_value_type(), function, arguments);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
