package com.jcsa.jcparse.lang.code;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstAbsDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstArrayQualifierList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDimension;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstIdentifierList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterBody;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterTypeList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstPointer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstDesignator;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstDesignatorList;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstDeclarationSpecifiers;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumerator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumeratorBody;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumeratorList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstFunctionQualifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifierQualifierList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStorageClass;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaration;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarationList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructUnionBody;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypeKeyword;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypeQualifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypedefName;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstUnionSpecifier;
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
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;

/**
 * Used to generate the code that describes the abstract structure of syntactic tree.
 * It applies no normalization and optimization and generate the original code being parsed.
 * 
 * @author yukimula
 *
 */
public class AstCodeGenerator {
	
	/* attributes */
	/** the number of tabs at the beginning of the new line **/
	private int tabs;
	/** to preserve the code that has been generated by now **/
	private StringBuilder buffer;
	
	/* constructor & singleton */
	/**
	 * private constructor for singleton mode
	 */
	private AstCodeGenerator() {
		this.tabs = 0;
		this.buffer = new StringBuilder();
	}
	/** the singleton to generate the code that describes the structure of AST node **/
	private static final AstCodeGenerator generator = new AstCodeGenerator();
	
	/* utility methods */
	/**
	 * initialize the code generator
	 */
	private void init() {
		this.tabs = 0;
		this.buffer.setLength(0);
	}
	/**
	 * \n\t\t\t...\t
	 */
	private void new_line() {
		buffer.append("\n");
		for(int k = 0; k < this.tabs; k++) {
			buffer.append("\t");
		}
	}
	/**
	 * @param statement
	 * @return whether the statement is empty
	 */
	private boolean is_empty_statement(AstStatement statement) {
		if(statement instanceof AstExpressionStatement) {
			return !((AstExpressionStatement) statement).has_expression();
		}
		else {
			return false;
		}
	}
	/**
	 * @param node
	 * @return the source code generated from the generator based on abstract structure
	 * 			of the syntactic node
	 * @throws Exception
	 */
	public static String generate(AstNode node) throws Exception {
		generator.init();
		generator.gen(node);
		return generator.buffer.toString();
	}
	
	/* recursive generation method */
	/**
	 * It generates the code of the AST node in the buffer recursively.
	 * @param node
	 * @throws Exception
	 */
	private void gen(AstNode node) throws Exception {
		if(node == null) {
			throw new IllegalArgumentException("Invalid node: null");
		}
		else if(node instanceof AstKeyword) {
			this.gen_keyword((AstKeyword) node);
		}
		else if(node instanceof AstPunctuator) {
			this.gen_punctuator((AstPunctuator) node);
		}
		else if(node instanceof AstOperator) {
			this.gen_operator((AstOperator) node);
		}
		else if(node instanceof AstTypeKeyword) {
			this.gen_type_keyword((AstTypeKeyword) node);
		}
		else if(node instanceof AstStorageClass) {
			this.gen_storage_class((AstStorageClass) node);
		}
		else if(node instanceof AstTypeQualifier) {
			this.gen_type_qualifier((AstTypeQualifier) node);
		}
		else if(node instanceof AstFunctionQualifier) {
			this.gen_function_qualifier((AstFunctionQualifier) node);
		}
		else if(node instanceof AstStructSpecifier) {
			this.gen_struct_specifier((AstStructSpecifier) node);
		}
		else if(node instanceof AstUnionSpecifier) {
			this.gen_union_specifier((AstUnionSpecifier) node);
		}
		else if(node instanceof AstEnumSpecifier) {
			this.gen_enum_specifier((AstEnumSpecifier) node);
		}
		else if(node instanceof AstTypedefName) {
			this.gen_typedef_name((AstTypedefName) node);
		}
		else if(node instanceof AstEnumerator) {
			this.gen_enumerator((AstEnumerator) node);
		}
		else if(node instanceof AstEnumeratorList) {
			this.gen_enumerator_list((AstEnumeratorList) node);
		}
		else if(node instanceof AstEnumeratorBody) {
			this.gen_enumerator_body((AstEnumeratorBody) node);
		}
		else if(node instanceof AstStructUnionBody) {
			this.gen_struct_union_body((AstStructUnionBody) node);
		}
		else if(node instanceof AstStructDeclarationList) {
			this.gen_struct_declaration_list((AstStructDeclarationList) node);
		}
		else if(node instanceof AstStructDeclaration) {
			this.gen_struct_declaration((AstStructDeclaration) node);
		}
		else if(node instanceof AstSpecifierQualifierList) {
			this.gen_specifier_qualifier_list((AstSpecifierQualifierList) node);
		}
		else if(node instanceof AstStructDeclaratorList) {
			this.gen_struct_declarator_list((AstStructDeclaratorList) node);
		}
		else if(node instanceof AstStructDeclarator) {
			this.gen_struct_declarator((AstStructDeclarator) node);
		}
		else if(node instanceof AstDeclarationSpecifiers) {
			this.gen_declaration_specifiers((AstDeclarationSpecifiers) node);
		}
		else if(node instanceof AstName) {
			this.gen_name((AstName) node);
		}
		else if(node instanceof AstPointer) {
			this.gen_pointer((AstPointer) node);
		}
		else if(node instanceof AstDimension) {
			this.gen_dimension((AstDimension) node);
		}
		else if(node instanceof AstIdentifierList) {
			this.gen_identifier_list((AstIdentifierList) node);
		}
		else if(node instanceof AstParameterBody) {
			this.gen_parameter_body((AstParameterBody) node);
		}
		else if(node instanceof AstParameterTypeList) {
			this.gen_parameter_type_list((AstParameterTypeList) node);
		}
		else if(node instanceof AstParameterList) {
			this.gen_parameter_list((AstParameterList) node);
		}
		else if(node instanceof AstParameterDeclaration) {
			this.gen_parameter_declaration((AstParameterDeclaration) node);
		}
		else if(node instanceof AstArrayQualifierList) {
			this.gen_array_qualifier_list((AstArrayQualifierList) node);
		}
		else if(node instanceof AstDeclarator) {
			this.gen_declarator((AstDeclarator) node);
		}
		else if(node instanceof AstAbsDeclarator) {
			this.gen_abs_declarator((AstAbsDeclarator) node);
		}
		else if(node instanceof AstInitDeclarator) {
			this.gen_init_declarator((AstInitDeclarator) node);
		}
		else if(node instanceof AstInitDeclaratorList) {
			this.gen_init_declarator_list((AstInitDeclaratorList) node);
		}
		else if(node instanceof AstInitializer) {
			this.gen_initializer((AstInitializer) node);
		}
		else if(node instanceof AstInitializerBody) {
			this.gen_initializer_body((AstInitializerBody) node);
		}
		else if(node instanceof AstInitializerList) {
			this.gen_initializer_list((AstInitializerList) node);
		}
		else if(node instanceof AstFieldInitializer) {
			this.gen_field_initializer((AstFieldInitializer) node);
		}
		else if(node instanceof AstDesignator) {
			this.gen_designator((AstDesignator) node);
		}
		else if(node instanceof AstDesignatorList) {
			this.gen_designator_list((AstDesignatorList) node);
		}
		else if(node instanceof AstDeclaration) {
			this.gen_declaration((AstDeclaration) node);
		}
		else if(node instanceof AstTypeName) {
			this.gen_type_name((AstTypeName) node);
		}
		else if(node instanceof AstIdExpression) {
			this.gen_id_expression((AstIdExpression) node);
		}
		else if(node instanceof AstConstant) {
			this.gen_constant((AstConstant) node);
		}
		else if(node instanceof AstLiteral) {
			this.gen_string_literal((AstLiteral) node);
		}
		else if(node instanceof AstUnaryExpression) {
			this.gen_unary_expression((AstUnaryExpression) node);
		}
		else if(node instanceof AstBinaryExpression) {
			this.gen_binary_expression((AstBinaryExpression) node);
		}
		else if(node instanceof AstPostfixExpression) {
			this.gen_postfix_expression((AstPostfixExpression) node);
		}
		else if(node instanceof AstArrayExpression) {
			this.gen_array_expression((AstArrayExpression) node);
		}
		else if(node instanceof AstFieldExpression) {
			this.gen_field_expression((AstFieldExpression) node);
		}
		else if(node instanceof AstField) {
			this.gen_field((AstField) node);
		}
		else if(node instanceof AstCastExpression) {
			this.gen_cast_expression((AstCastExpression) node);
		}
		else if(node instanceof AstCommaExpression) {
			this.gen_comma_expression((AstCommaExpression) node);
		}
		else if(node instanceof AstConditionalExpression) {
			this.gen_conditional_expression((AstConditionalExpression) node);
		}
		else if(node instanceof AstFunCallExpression) {
			this.gen_fun_call_expression((AstFunCallExpression) node);
		}
		else if(node instanceof AstArgumentList) {
			this.gen_argument_list((AstArgumentList) node);
		}
		else if(node instanceof AstSizeofExpression) {
			this.gen_sizeof_expression((AstSizeofExpression) node);
		}
		else if(node instanceof AstParanthExpression) {
			this.gen_paranth_expression((AstParanthExpression) node);
		}
		else if(node instanceof AstConstExpression) {
			this.gen_const_expression((AstConstExpression) node);
		}
		else if(node instanceof AstExpressionStatement) {
			this.gen_expression_statement((AstExpressionStatement) node);
		}
		else if(node instanceof AstDeclarationStatement) {
			this.gen_declaration_statement((AstDeclarationStatement) node);
		}
		else if(node instanceof AstCompoundStatement) {
			this.gen_compound_statement((AstCompoundStatement) node);
		}
		else if(node instanceof AstBreakStatement) {
			this.gen_break_statement((AstBreakStatement) node);
		}
		else if(node instanceof AstContinueStatement) {
			this.gen_continue_statement((AstContinueStatement) node);
		}
		else if(node instanceof AstGotoStatement) {
			this.gen_goto_statement((AstGotoStatement) node);
		}
		else if(node instanceof AstReturnStatement) {
			this.gen_return_statement((AstReturnStatement) node);
		}
		else if(node instanceof AstLabel) {
			this.gen_label((AstLabel) node);
		}
		else if(node instanceof AstLabeledStatement) {
			this.gen_labeled_statement((AstLabeledStatement) node);
		}
		else if(node instanceof AstCaseStatement) {
			this.gen_case_statement((AstCaseStatement) node);
		}
		else if(node instanceof AstDefaultStatement) {
			this.gen_default_statement((AstDefaultStatement) node);
		}
		else if(node instanceof AstStatementList) {
			this.gen_statement_list((AstStatementList) node);
		}
		else if(node instanceof AstIfStatement) {
			this.gen_if_statement((AstIfStatement) node);
		}
		else if(node instanceof AstSwitchStatement) {
			this.gen_switch_statement((AstSwitchStatement) node);
		}
		else if(node instanceof AstForStatement) {
			this.gen_for_statement((AstForStatement) node);
		}
		else if(node instanceof AstWhileStatement) {
			this.gen_while_statement((AstWhileStatement) node);
		}
		else if(node instanceof AstDoWhileStatement) {
			this.gen_do_while_statement((AstDoWhileStatement) node);
		}
		else if(node instanceof AstFunctionDefinition) {
			this.gen_function_definition((AstFunctionDefinition) node);
		}
		else if(node instanceof AstTranslationUnit) {
			this.gen_translation_unit((AstTranslationUnit) node);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + node);
		}
	}
	
	/* syntax-directed translation */
	/* basic syntactic structure as leaf */
	private void gen_keyword(AstKeyword node) throws Exception {
		switch(node.get_keyword()) {
		case c89_auto:					buffer.append("auto"); 		break;
		case c89_break:					buffer.append("break"); 	break;
		case c89_case:					buffer.append("case");		break;
		case c89_char:					buffer.append("char");		break;
		case c89_const:					buffer.append("const");		break;
		case c89_continue:				buffer.append("continue");	break;
		case c89_default:				buffer.append("default");	break;
		case c89_do:					buffer.append("do");		break;
		case c89_double:				buffer.append("double");	break;
		case c89_else:					buffer.append("else");		break;
		case c89_enum:					buffer.append("enum");		break;
		case c89_extern:				buffer.append("extern");	break;
		case c89_float:					buffer.append("float");		break;
		case c89_for:					buffer.append("for");		break;
		case c89_goto:					buffer.append("goto");		break;
		case c89_if:					buffer.append("if");		break;
		case c89_int:					buffer.append("int");		break;
		case c89_long:					buffer.append("long");		break;
		case c89_register:				buffer.append("register");	break;
		case c89_return:				buffer.append("return");	break;
		case c89_short:					buffer.append("short");		break;
		case c89_signed:				buffer.append("signed");	break;
		case c89_sizeof:				buffer.append("sizeof");	break;
		case c89_static:				buffer.append("static");	break;
		case c89_struct:				buffer.append("struct");	break;
		case c89_switch:				buffer.append("switch");	break;
		case c89_typedef:				buffer.append("typedef");	break;
		case c89_union:					buffer.append("union");		break;
		case c89_unsigned:				buffer.append("unsigned");	break;
		case c89_void:					buffer.append("void");		break;
		case c89_volatile:				buffer.append("volatile");	break;
		case c89_while:					buffer.append("while");		break;
		case c99_inline:				buffer.append("inline");	break;
		case c99_restrict:				buffer.append("restrict");	break;
		case c99_bool:					buffer.append("_Bool");		break;
		case c99_complex:				buffer.append("_Complex");	break;
		case c99_imaginary:				buffer.append("_Imaginary");break;
		/* gnu-special keyword */
		case gnu_function:				buffer.append("__FUNCTION__");	break;
		case gnu_pretty_function:		buffer.append("__PRETTY_FUNCTION__");	break;
		case gnu_alignof:				buffer.append("__alignof__");	break;
		case gnu_asm:					buffer.append("__asm__");	break;
		case gnu_attribute:				buffer.append("__attribute__");	break;
		case gnu_builtin_offsetof:		buffer.append("__builtin_offsetof");	break;
		case gnu_builtin_va_arg:		buffer.append("__builtin_va_arg");	break;
		case gnu_builtin_va_list:		buffer.append("__builtin_va_list");	break;
		case gnu_extension:				buffer.append("__extension__");	break;
		case gnu_func:					buffer.append("__func__");	break;
		case gnu_label:					buffer.append("__label__");	break;
		case gnu_null:					buffer.append("__null");	break;
		case gnu_real:					buffer.append("__real__");	break;
		case gnu_typeof:				buffer.append("__typeof");	break;
		case gnu_thread:				buffer.append("__thread");	break;
		default: throw new IllegalArgumentException("Unsupport keyword: " + node.get_keyword());
		}
	}
	private void gen_punctuator(AstPunctuator node) throws Exception {
		switch(node.get_punctuator()) {
		case left_bracket:				buffer.append("[");		break;
		case right_bracket:				buffer.append("]");		break;
		case left_paranth:				buffer.append("(");		break;
		case right_paranth:				buffer.append(")");		break;
		case left_brace:				buffer.append("{");		break;
		case right_brace:				buffer.append("}");		break;
		case dot:						buffer.append(".");		break;
		case arrow:						buffer.append("->");	break;
		case increment:					buffer.append("++");	break;
		case decrement:					buffer.append("--");	break;
		case bit_not:					buffer.append("~");		break;
		case bit_and:					buffer.append("&");		break;
		case bit_or:					buffer.append("|");		break;
		case bit_xor:					buffer.append("^");		break;
		case ari_add:					buffer.append("+");		break;
		case ari_sub:					buffer.append("-");		break;
		case ari_mul:					buffer.append("*");		break;
		case ari_div:					buffer.append("/");		break;
		case ari_mod:					buffer.append("%");		break;
		case log_and:					buffer.append("&&");	break;
		case log_or:					buffer.append("||");	break;
		case log_not:					buffer.append("!");		break;
		case left_shift:				buffer.append("<<");	break;
		case right_shift:				buffer.append(">>");	break;
		case left_shift_assign:			buffer.append("<<=");	break;
		case right_shift_assign:		buffer.append(">>=");	break;
		case greater_tn:				buffer.append(">");		break;
		case greater_eq:				buffer.append(">=");	break;
		case smaller_tn:				buffer.append("<");		break;
		case smaller_eq:				buffer.append("<=");	break;
		case equal_with:				buffer.append("==");	break;
		case not_equals:				buffer.append("!=");	break;
		case ari_add_assign:			buffer.append("+=");	break;
		case ari_sub_assign:			buffer.append("-=");	break;
		case ari_mul_assign:			buffer.append("*=");	break;
		case ari_div_assign:			buffer.append("/=");	break;
		case ari_mod_assign:			buffer.append("%=");	break;
		case bit_and_assign:			buffer.append("&=");	break;
		case bit_or_assign:				buffer.append("|=");	break;
		case bit_xor_assign:			buffer.append("^=");	break;
		case comma:						buffer.append(",");		break;
		case semicolon:					buffer.append(";");		break;
		case colon:						buffer.append(":");		break;
		case ellipsis:					buffer.append("...");	break;
		case question:					buffer.append("?");		break;
		case assign:					buffer.append("=");		break;
		case hash:						buffer.append("#");		break;
		case hash_hash:					buffer.append("##");	break;
		default: throw new IllegalArgumentException("Unsupport: " + node.get_punctuator());
		}
	}
	private void gen_operator(AstOperator node) throws Exception {
		switch(node.get_operator()) {
		case assign:				buffer.append("=");		break;
		case positive:				buffer.append("+");		break;
		case negative:				buffer.append("-");		break;
		case arith_add:				buffer.append("+");		break;
		case arith_sub:				buffer.append("-");		break;
		case arith_mul:				buffer.append("*");		break;
		case arith_div:				buffer.append("/");		break;
		case arith_mod:				buffer.append("%");		break;
		case arith_add_assign:		buffer.append("+=");	break;
		case arith_sub_assign:		buffer.append("-=");	break;
		case arith_mul_assign:		buffer.append("*=");	break;
		case arith_div_assign:		buffer.append("/=");	break;
		case arith_mod_assign:		buffer.append("%=");	break;
		case bit_not:				buffer.append("~");		break;
		case bit_and:				buffer.append("&");		break;
		case bit_or:				buffer.append("|");		break;
		case bit_xor:				buffer.append("^");		break;
		case left_shift:			buffer.append("<<");	break;
		case righ_shift:			buffer.append(">>");	break;
		case bit_and_assign:		buffer.append("&=");	break;
		case bit_or_assign:			buffer.append("|=");	break;
		case bit_xor_assign:		buffer.append("^=");	break;
		case left_shift_assign:		buffer.append("<<=");	break;
		case righ_shift_assign:		buffer.append(">>=");	break;
		case logic_not:				buffer.append("!");		break;
		case logic_and:				buffer.append("&&");	break;
		case logic_or:				buffer.append("||");	break;
		case address_of:			buffer.append("&");		break;
		case dereference:			buffer.append("*");		break;
		case increment:				buffer.append("++");	break;
		case decrement:				buffer.append("--");	break;
		case greater_tn:			buffer.append(">");		break;
		case greater_eq:			buffer.append(">=");	break;
		case smaller_tn:			buffer.append("<");		break;
		case smaller_eq:			buffer.append("<=");	break;
		case equal_with:			buffer.append("==");	break;
		case not_equals:			buffer.append("!=");	break;
		default: throw new IllegalArgumentException("Invalid operator: " + node.get_operator());
		}
	}
	/* declaration.specifier package */ 
	private void gen_type_keyword(AstTypeKeyword node) throws Exception {
		this.gen(node.get_keyword());
	}
	private void gen_storage_class(AstStorageClass node) throws Exception {
		this.gen(node.get_keyword());
	}
	private void gen_type_qualifier(AstTypeQualifier node) throws Exception {
		this.gen(node.get_keyword());
	}
	private void gen_function_qualifier(AstFunctionQualifier node) throws Exception {
		this.gen(node.get_keyword());
	}
	private void gen_struct_specifier(AstStructSpecifier node) throws Exception {
		buffer.append("struct ");
		if(node.has_name()) {
			this.gen(node.get_name());
		}
		if(node.has_body()) {
			this.new_line();
			this.gen(node.get_body());
			this.new_line();
		}
	}
	private void gen_union_specifier(AstUnionSpecifier node) throws Exception {
		buffer.append("union ");
		if(node.has_name()) {
			this.gen(node.get_name());
		}
		if(node.has_body()) {
			this.new_line();
			this.gen(node.get_body());
			this.new_line();
		}
	}
	private void gen_enum_specifier(AstEnumSpecifier node) throws Exception {
		buffer.append("enum ");
		if(node.has_name()) {
			this.gen(node.get_name());
		}
		if(node.has_body()) {
			this.new_line();
			this.gen(node.get_body());
			this.new_line();
		}
	}
	private void gen_typedef_name(AstTypedefName node) throws Exception {
		buffer.append(node.get_name());
	}
	private void gen_enumerator_body(AstEnumeratorBody node) throws Exception {
		buffer.append("{");
		
		this.tabs++;
		this.new_line();
		
		this.gen(node.get_enumerator_list());
		if(node.has_comma()) {
			buffer.append(",");
		}
		
		this.tabs--;
		this.new_line();
		
		buffer.append("}");
	}
	private void gen_enumerator_list(AstEnumeratorList node) throws Exception {
		for(int k = 0; k < node.number_of_enumerators(); k++) {
			this.gen(node.get_enumerator(k));
			if(k < node.number_of_enumerators() - 1) {
				buffer.append(",");
				this.new_line();
			}
		}
	}
	private void gen_enumerator(AstEnumerator node) throws Exception {
		this.gen(node.get_name());
		if(node.has_expression()) {
			buffer.append(" = ");
			this.gen(node.get_expression());
		}
	}
	private void gen_struct_union_body(AstStructUnionBody node) throws Exception {
		buffer.append("{");
		
		this.tabs++;
		this.new_line();
		
		if(node.has_declaration_list()) {
			this.gen(node.get_declaration_list());
		}
		
		this.tabs--;
		this.new_line();
		buffer.append("}");
	}
	private void gen_struct_declaration_list(AstStructDeclarationList node) throws Exception {
		for(int k = 0; k < node.number_of_declarations(); k++) {
			this.gen(node.get_declaration(k));
			if(k < node.number_of_declarations() - 1) {
				this.new_line();
			}
		}
	}
	private void gen_struct_declaration(AstStructDeclaration node) throws Exception {
		this.gen(node.get_specifiers());
		buffer.append(" ");
		this.gen(node.get_declarators());
		buffer.append(";");
	}
	private void gen_specifier_qualifier_list(AstSpecifierQualifierList node) throws Exception {
		for(int k = 0; k < node.number_of_specifiers(); k++) {
			this.gen(node.get_specifier(k));
			if(k < node.number_of_specifiers() - 1) {
				buffer.append(" ");
			}
		}
	}
	private void gen_struct_declarator_list(AstStructDeclaratorList node) throws Exception {
		for(int k = 0; k < node.number_of_declarators(); k++) {
			this.gen(node.get_declarator(k));
			if(k < node.number_of_declarators() - 1) {
				buffer.append(", ");
			}
		}
	}
	private void gen_struct_declarator(AstStructDeclarator node) throws Exception {
		if(node.has_declarator()) {
			this.gen(node.get_declarator());
		}
		if(node.has_expression()) {
			buffer.append(" : ");
			this.gen(node.get_expression());
		}
	}
	private void gen_declaration_specifiers(AstDeclarationSpecifiers node) throws Exception {
		for(int k = 0; k < node.number_of_specifiers(); k++) {
			this.gen(node.get_specifier(k));
			if(k < node.number_of_specifiers() - 1) {
				buffer.append(" ");
			}
		}
	}
	/* declaration.declarator package */
	private void gen_name(AstName node) throws Exception {
		String name = node.get_name();
		
		int index;
		if(name.startsWith("struct ")) {
			index = "struct ".length();
		}
		else if(name.startsWith("union ")) {
			index = "union ".length();
		}
		else if(name.startsWith("enum ")) {
			index = "enum ".length();
		}
		else {
			index = 0;
		}
		
		this.buffer.append(name.substring(index).strip());
	}
	private void gen_pointer(AstPointer node) throws Exception {
		for(int k = 0; k < node.number_of_keywords(); k++) {
			this.gen(node.get_specifier(k));
			if(k < node.number_of_keywords() - 1) {
				this.buffer.append(" ");
			}
		}
	}
	private void gen_dimension(AstDimension node) throws Exception {
		buffer.append("[");
		if(node.has_array_qualifier_list()) {
			this.gen(node.get_array_qualifier_list());
		}
		if(node.has_array_qualifier_list() && node.has_expression()) {
			this.buffer.append(" ");
		}
		if(node.has_expression()) {
			this.gen(node.get_expression());
		}
		buffer.append("]");
	}
	private void gen_identifier_list(AstIdentifierList node) throws Exception {
		for(int k = 0; k < node.number_of_identifiers(); k++) {
			this.gen(node.get_identifier(k));
			if(k < node.number_of_identifiers() - 1) {
				this.buffer.append(", ");
			}
		}
	}
	private void gen_parameter_body(AstParameterBody node) throws Exception {
		this.buffer.append("(");
		
		if(node.has_identifier_list()) {
			this.gen(node.get_identifier_list());
		}
		else if(node.has_parameter_type_list()) {
			this.gen(node.get_parameter_type_list());
		}
		
		this.buffer.append(")");
	}
	private void gen_parameter_type_list(AstParameterTypeList node) throws Exception {
		this.gen(node.get_parameter_list());
		if(node.has_ellipsis()) {
			this.buffer.append(", ...");
		}
	}
	private void gen_parameter_list(AstParameterList node) throws Exception {
		for(int k = 0; k < node.number_of_parameters(); k++) {
			this.gen(node.get_parameter(k));
			if(k < node.number_of_parameters() - 1) {
				this.buffer.append(", ");
			}
		}
	}
	private void gen_parameter_declaration(AstParameterDeclaration node) throws Exception {
		this.gen(node.get_specifiers());
		if(node.has_abs_declarator()) {
			this.buffer.append(" ");
			this.gen(node.get_abs_declarator());
		}
		else if(node.has_declarator()) {
			this.buffer.append(" ");
			this.gen(node.get_declarator());
		}
	}
	private void gen_array_qualifier_list(AstArrayQualifierList node) throws Exception {
		for(int k = 0; k < node.number_of_keywords(); k++) {
			this.gen(node.get_keyword(k));
			if(k < node.number_of_keywords() - 1) {
				this.buffer.append(" ");
			}
		}
	}
	private void gen_declarator(AstDeclarator node) throws Exception {
		switch(node.get_production()) {
		case pointer_declarator:
		{
			this.gen(node.get_pointer());
			this.buffer.append(" ");
			this.gen(node.get_declarator());
			break;
		}
		case declarator_dimension:
		{
			this.gen(node.get_declarator());
			this.gen(node.get_dimension());
			break;
		}
		case declarator_parambody:
		{
			this.gen(node.get_declarator());
			this.gen(node.get_parameter_body());
			break;
		}
		case lp_declarator_rp:
		{
			this.buffer.append("(");
			this.gen(node.get_declarator());
			this.buffer.append(")");
			break;
		}
		case identifier:
		{
			this.gen(node.get_identifier()); 
			break;
		}
		default: throw new IllegalArgumentException("Unsupport: " + node.get_production());
		}
	}
	private void gen_abs_declarator(AstAbsDeclarator node) throws Exception {
		switch(node.get_production()) {
		case pointer_declarator:
		{
			this.gen(node.get_pointer());
			if(node.get_declarator() != null) {
				this.buffer.append(" ");
				this.gen(node.get_declarator());
			}
		}
		break;
		case declarator_dimension:
		{
			if(node.get_declarator() != null) {
				this.gen(node.get_declarator());
			}
			this.gen(node.get_dimension());
		}
		break;
		case declarator_parambody:
		{
			if(node.get_declarator() != null) {
				this.gen(node.get_declarator());
			}
			this.gen(node.get_parameter_body());
		}
		break;
		case lp_declarator_rp:
		{
			this.buffer.append("(");
			this.gen(node.get_declarator());
			this.buffer.append(")");
		}
		break;
		default: throw new IllegalArgumentException("Unsupport: " + node.get_production());
		}
	}
	private void gen_init_declarator(AstInitDeclarator node) throws Exception {
		this.gen(node.get_declarator());
		if(node.has_initializer()) {
			this.buffer.append(" = ");
			this.gen(node.get_initializer());
		}
	}
	private void gen_init_declarator_list(AstInitDeclaratorList node) throws Exception {
		for(int k = 0; k < node.number_of_init_declarators(); k++) {
			this.gen(node.get_init_declarator(k));
			if(k < node.number_of_init_declarators() - 1) {
				this.buffer.append(", ");
			}
		}
	}
	/* declaration.initializer package */
	private void gen_initializer(AstInitializer node) throws Exception {
		if(node.is_body())
			this.gen(node.get_body());
		else
			this.gen(node.get_expression());
	}
	private void gen_initializer_body(AstInitializerBody node) throws Exception {
		this.buffer.append("{");
		
		this.gen(node.get_initializer_list());
		if(node.has_tail_comma()) {
			this.buffer.append(", ");
		}
		
		this.buffer.append("}");
	}
	private void gen_initializer_list(AstInitializerList node) throws Exception {
		for(int k = 0; k < node.number_of_initializer(); k++) {
			this.gen(node.get_initializer(k));
			if(k < node.number_of_initializer() - 1) {
				this.buffer.append(", ");
			}
		}
	}
	private void gen_field_initializer(AstFieldInitializer node) throws Exception {
		if(node.has_designator_list()) {
			this.gen(node.get_designator_list());
			this.buffer.append(" = ");
		}
		this.gen(node.get_initializer());
	}
	private void gen_designator_list(AstDesignatorList node) throws Exception {
		for(int k = 0; k < node.number_of_designators(); k++) {
			this.gen(node.get_designator(k));
		}
	}
	private void gen_designator(AstDesignator node) throws Exception {
		if(node.is_dimension()) {
			this.buffer.append("[");
			this.gen(node.get_dimension_expression());
			this.buffer.append("]");
		}
		else {
			this.buffer.append(".");
			this.gen(node.get_field());
		}
	}
	/* declaration package */
	private void gen_declaration(AstDeclaration node) throws Exception {
		this.gen(node.get_specifiers());
		if(node.has_declarator_list()) {
			this.buffer.append(" ");
			this.gen(node.get_declarator_list());
		}
	}
	private void gen_type_name(AstTypeName node) throws Exception {
		this.gen(node.get_specifiers());
		if(node.has_declarator()) {
			this.buffer.append(" ");
			this.gen(node.get_declarator());
		}
	}
	/* expression package */
	private void gen_id_expression(AstIdExpression node) throws Exception {
		this.buffer.append(node.get_name());
	}
	private void gen_constant(AstConstant node) throws Exception {
		this.buffer.append(node.get_code());
	}
	private void gen_string_literal(AstLiteral node) throws Exception {
		this.buffer.append(node.get_code());
	}
	private void gen_binary_expression(AstBinaryExpression node) throws Exception {
		this.gen(node.get_loperand());
		this.buffer.append(" ");
		this.gen(node.get_operator());
		this.buffer.append(" ");
		this.gen(node.get_roperand());
	}
	private void gen_unary_expression(AstUnaryExpression node) throws Exception {
		this.gen(node.get_operator());
		this.gen(node.get_operand());
	}
	private void gen_postfix_expression(AstPostfixExpression node) throws Exception {
		this.gen(node.get_operand());
		this.gen(node.get_operator());
	}
	private void gen_argument_list(AstArgumentList node) throws Exception {
		for(int k = 0; k < node.number_of_arguments(); k++) {
			this.gen(node.get_argument(k));
			if(k < node.number_of_arguments() - 1) {
				this.buffer.append(", ");
			}
		}
	}
	private void gen_array_expression(AstArrayExpression node) throws Exception {
		this.gen(node.get_array_expression());
		this.buffer.append("[");
		this.gen(node.get_dimension_expression());
		this.buffer.append("]");
	}
	private void gen_cast_expression(AstCastExpression node) throws Exception {
		this.buffer.append("(");
		this.gen(node.get_typename());
		this.buffer.append(") ");
		this.gen(node.get_expression());
	}
	private void gen_comma_expression(AstCommaExpression node) throws Exception {
		for(int k = 0; k < node.number_of_arguments(); k++) {
			this.gen(node.get_expression(k));
			if(k < node.number_of_arguments() - 1) {
				this.buffer.append(", ");
			}
		}
	}
	private void gen_conditional_expression(AstConditionalExpression node) throws Exception {
		this.gen(node.get_condition());
		this.buffer.append(" ? ");
		this.gen(node.get_true_branch());
		this.buffer.append(" : ");
		this.gen(node.get_false_branch());
	}
	private void gen_field(AstField node) throws Exception {
		this.buffer.append(node.get_name());
	}
	private void gen_field_expression(AstFieldExpression node) throws Exception {
		this.gen(node.get_body());
		this.gen(node.get_operator());
		this.gen(node.get_field());
	}
	private void gen_fun_call_expression(AstFunCallExpression node) throws Exception {
		this.gen(node.get_function());
		this.buffer.append("(");
		if(node.has_argument_list())
			this.gen(node.get_argument_list());
		this.buffer.append(")");
	}
	private void gen_paranth_expression(AstParanthExpression node) throws Exception {
		this.buffer.append("(");
		this.gen(node.get_sub_expression());
		this.buffer.append(")");
	}
	private void gen_const_expression(AstConstExpression node) throws Exception {
		this.gen(node.get_expression());
	}
	private void gen_sizeof_expression(AstSizeofExpression node) throws Exception {
		this.buffer.append("sizeof(");
		if(node.is_expression()) {
			AstExpression expression = node.get_expression();
			if(expression instanceof AstParanthExpression) {
				expression = ((AstParanthExpression) expression).get_sub_expression();
			}
			this.gen(expression);
		}
		else {
			this.gen(node.get_typename());
		}
		this.buffer.append(")");
	}
	/* statement package */
	private void gen_break_statement(AstBreakStatement node) throws Exception {
		this.buffer.append("break;");
	}
	private void gen_continue_statement(AstContinueStatement node) throws Exception {
		this.buffer.append("break;");
	}
	private void gen_goto_statement(AstGotoStatement node) throws Exception {
		this.buffer.append("goto ");
		this.gen(node.get_label());
		this.buffer.append(";");
	}
	private void gen_return_statement(AstReturnStatement node) throws Exception {
		this.buffer.append("return");
		if(node.has_expression()) {
			this.buffer.append(" ");
			this.gen(node.get_expression());
		}
		this.buffer.append(";");
	}
	private void gen_label(AstLabel node) throws Exception {
		this.buffer.append(node.get_name());
	}
	private void gen_labeled_statement(AstLabeledStatement node) throws Exception {
		this.gen(node.get_label());
		this.buffer.append(":");
	}
	private void gen_case_statement(AstCaseStatement node) throws Exception {
		this.buffer.append("case ");
		this.gen(node.get_expression());
		this.buffer.append(":");
	}
	private void gen_default_statement(AstDefaultStatement node) throws Exception {
		this.buffer.append("default:");
	}
	private void gen_expression_statement(AstExpressionStatement node) throws Exception {
		if(node.has_expression()) {
			this.gen(node.get_expression());
		}
		this.buffer.append(";");
	}
	private void gen_declaration_statement(AstDeclarationStatement node) throws Exception {
		this.gen(node.get_declaration());
		this.buffer.append(";");
	}
	private void gen_compound_statement(AstCompoundStatement node) throws Exception {
		this.buffer.append("{");
		if(node.has_statement_list()) {
			this.tabs++;
			this.gen(node.get_statement_list());
			this.tabs--;
		}
		this.new_line();
		this.buffer.append("}");
	}
	private void gen_statement_list(AstStatementList node) throws Exception {
		for(int k = 0; k < node.number_of_statements(); k++) {
			AstStatement statement = node.get_statement(k);
			if(statement instanceof AstLabeledStatement
				|| statement instanceof AstCaseStatement
				|| statement instanceof AstDefaultStatement) {
				this.tabs--;
				this.new_line();
				this.gen(statement);
				this.tabs++;
			}
			else if(!this.is_empty_statement(statement)) {
				this.new_line();
				this.gen(statement);
			}
		}
	}
	private void gen_block(AstStatement statement) throws Exception {
		if(statement instanceof AstCompoundStatement) {
			this.new_line();
			this.gen(statement);
		}
		else {
			this.tabs++;
			this.new_line();
			this.gen(statement);
			this.tabs--;
		}
	}
	private void gen_if_statement(AstIfStatement node) throws Exception {
		/* if(condition) */
		this.buffer.append("if(");
		this.gen(node.get_condition());
		this.buffer.append(")");
		
		/* true-block */
		this.gen_block(node.get_true_branch());
		
		/* false-block */
		if(node.has_else()) {
			this.new_line();
			this.buffer.append("else");
			this.gen_block(node.get_false_branch());
		}
	}
	private void gen_switch_statement(AstSwitchStatement node) throws Exception {
		this.buffer.append("switch(");
		this.gen(node.get_condition());
		this.buffer.append(")");
		this.gen_block(node.get_body());
	}
	private void gen_while_statement(AstWhileStatement node) throws Exception {
		this.buffer.append("while(");
		this.gen(node.get_condition());
		this.buffer.append(")");
		this.gen_block(node.get_body());
	}
	private void gen_do_while_statement(AstDoWhileStatement node) throws Exception {
		this.buffer.append("do");
		this.gen_block(node.get_body());
		this.new_line();
		this.buffer.append("while(");
		this.gen(node.get_condition());
		this.buffer.append(");");
	}
	private void gen_for_statement(AstForStatement node) throws Exception {
		this.buffer.append("for(");
		this.gen(node.get_initializer());
		this.buffer.append(" ");
		this.gen(node.get_condition());
		if(node.has_increment()) {
			this.buffer.append(" ");
			this.gen(node.get_increment());
		}
		this.buffer.append(")");
		this.gen_block(node.get_body());
	}
	/* external-unit package */
	private String get_declarator_name(AstDeclarator declarator) throws Exception {
		while(declarator != null) {
			switch(declarator.get_production()) {
			case identifier: return declarator.get_identifier().get_name();
			default: declarator = declarator.get_declarator(); break;
			}
		}
		return null;
	}
	private void gen_parameter_declaration(String name, AstDeclarationList dlist) throws Exception {
		for(int i = 0; i < dlist.number_of_declarations(); i++) {
			AstDeclaration declaration = dlist.get_declaration(i).get_declaration();
			AstDeclarationSpecifiers specifiers = declaration.get_specifiers();
			AstInitDeclaratorList list = declaration.get_declarator_list();
			for(int j = 0; j < list.number_of_init_declarators(); j++) {
				String dname = this.get_declarator_name(list.get_init_declarator(j).get_declarator());
				if(name.equals(dname)) {
					this.gen(specifiers);
					this.buffer.append(" ");
					this.gen(list.get_init_declarator(j));
				}
			}
		}
	}
	private void gen_function_declarator(AstDeclarator declarator, AstDeclarationList dlist) throws Exception {
		switch(declarator.get_production()) {
		case pointer_declarator:
		{
			this.gen(declarator.get_pointer());
			this.buffer.append(" ");
			this.gen_function_declarator(declarator.get_declarator(), dlist);
		}
		break;
		case declarator_dimension:
		{
			this.gen_function_declarator(declarator.get_declarator(), dlist);
			this.gen(declarator.get_dimension());
		}
		break;
		case declarator_parambody:
		{
			this.gen_function_declarator(declarator.get_declarator(), dlist);
			
			AstIdentifierList ilist = declarator.get_parameter_body().get_identifier_list();
			this.buffer.append("(");
			for(int k = 0; k < ilist.number_of_identifiers(); k++) {
				String name = ilist.get_identifier(k).get_name();
				this.gen_parameter_declaration(name, dlist);
				if(k < ilist.number_of_identifiers() - 1) {
					this.buffer.append(", ");
				}
			}
			this.buffer.append(")");
		}
		break;
		case lp_declarator_rp:
		{
			this.buffer.append("(");
			this.gen_function_declarator(declarator.get_declarator(), dlist);
			this.buffer.append(")");
		}
		break;
		case identifier:
		{
			this.gen(declarator.get_identifier());
		}
		break;
		default: throw new IllegalArgumentException("Invalid production: " + declarator.get_production());
		}
	}
	private void gen_function_definition(AstFunctionDefinition node) throws Exception {
		this.gen(node.get_specifiers());
		this.buffer.append(" ");
		if(node.has_declaration_list()) {
			this.gen_function_declarator(node.get_declarator(), node.get_declaration_list());
		}
		else {
			this.gen(node.get_declarator());
		}
		this.gen_block(node.get_body());
	}
	private void gen_translation_unit(AstTranslationUnit node) throws Exception {
		for(int k = 0; k < node.number_of_units(); k++) {
			AstExternalUnit unit = node.get_unit(k);
			if(unit instanceof AstFunctionDefinition) {
				this.new_line();
				this.gen(unit);
				this.new_line();
			}
			else if(unit instanceof AstDeclarationStatement) {
				this.gen(unit);
				this.new_line();
			}
		}
	}
	
}
