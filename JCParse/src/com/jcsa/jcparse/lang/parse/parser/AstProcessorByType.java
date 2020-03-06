package com.jcsa.jcparse.lang.parse.parser;

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
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
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
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstDeclarationList;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;

/**
 * Process the AstNode based on its type
 * @author yukimula
 */
public abstract class AstProcessorByType {
	
	protected AstProcessorByType() {}
	
	/**
	 * Process the AstNode based on its type
	 * @param node
	 * @return : null when type is unknown
	 * @throws Exception
	 */
	public Object process(AstNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else if(node instanceof AstTranslationUnit) 
			return translation_unit((AstTranslationUnit) node);
		else if(node instanceof AstFunctionDefinition)
			return function_definition((AstFunctionDefinition) node);
		else if(node instanceof AstDeclarationList)
			return declaration_list((AstDeclarationList) node);
		else if(node instanceof AstKeyword)
			return keyword((AstKeyword) node);
		else if(node instanceof AstPunctuator)
			return punctuator((AstPunctuator) node);
		else if(node instanceof AstOperator)
			return operator((AstOperator) node);
		else if(node instanceof AstDeclaration)
			return declaration((AstDeclaration) node);
		else if(node instanceof AstDeclarationSpecifiers)
			return declaration_specifiers((AstDeclarationSpecifiers) node);
		else if(node instanceof AstTypeName)
			return type_name((AstTypeName) node);
		else if(node instanceof AstSpecifierQualifierList)
			return specifier_qualifier_list((AstSpecifierQualifierList) node);
		else if(node instanceof AstStorageClass)
			return storage_class((AstStorageClass) node);
		else if(node instanceof AstTypeQualifier)
			return type_qualifier((AstTypeQualifier) node);
		else if(node instanceof AstFunctionQualifier)
			return function_qualifier((AstFunctionQualifier) node);
		else if(node instanceof AstTypeKeyword)
			return type_keyword((AstTypeKeyword) node);
		else if(node instanceof AstTypedefName)
			return typedef_name((AstTypedefName) node);
		else if(node instanceof AstStructSpecifier)
			return struct_specifier((AstStructSpecifier) node);
		else if(node instanceof AstUnionSpecifier)
			return union_specifier((AstUnionSpecifier) node);
		else if(node instanceof AstEnumSpecifier)
			return enum_specifier((AstEnumSpecifier) node);
		else if(node instanceof AstStructUnionBody)
			return struct_union_body((AstStructUnionBody) node);
		else if(node instanceof AstStructDeclarationList)
			return struct_declaration_list((AstStructDeclarationList) node);
		else if(node instanceof AstStructDeclaration)
			return struct_declaration((AstStructDeclaration) node);
		else if(node instanceof AstStructDeclaratorList)
			return struct_declarator_list((AstStructDeclaratorList) node);
		else if(node instanceof AstStructDeclarator)
			return struct_declarator((AstStructDeclarator) node);
		else if(node instanceof AstEnumeratorBody)
			return enumerator_body((AstEnumeratorBody) node);
		else if(node instanceof AstEnumeratorList)
			return enumerator_list((AstEnumeratorList) node);
		else if(node instanceof AstEnumerator)
			return enumerator((AstEnumerator) node);
		else if(node instanceof AstName)
			return ast_name((AstName) node);
		else if(node instanceof AstDeclarator)
			return declarator((AstDeclarator) node);
		else if(node instanceof AstAbsDeclarator)
			return abs_declarator((AstAbsDeclarator) node);
		else if(node instanceof AstDimension)
			return dimension((AstDimension) node);
		else if(node instanceof AstArrayQualifierList)
			return array_qualifier_list((AstArrayQualifierList) node);
		else if(node instanceof AstParameterBody)
			return parameter_body((AstParameterBody) node);
		else if(node instanceof AstParameterTypeList)
			return parameter_type_list((AstParameterTypeList) node);
		else if(node instanceof AstParameterList)
			return parameter_list((AstParameterList) node);
		else if(node instanceof AstParameterDeclaration)
			return parameter_declaration((AstParameterDeclaration) node);
		else if(node instanceof AstIdentifierList)
			return identifier_list((AstIdentifierList) node);
		else if(node instanceof AstPointer)
			return pointer((AstPointer) node);
		else if(node instanceof AstInitDeclaratorList)
			return init_declarator_list((AstInitDeclaratorList) node);
		else if(node instanceof AstInitDeclarator)
			return init_declarator((AstInitDeclarator) node);
		else if(node instanceof AstInitializer)
			return initializer((AstInitializer) node);
		else if(node instanceof AstInitializerBody)
			return initializer_body((AstInitializerBody) node);
		else if(node instanceof AstInitializerList)
			return initializer_list((AstInitializerList) node);
		else if(node instanceof AstFieldInitializer)
			return field_initializer((AstFieldInitializer) node);
		else if(node instanceof AstDesignatorList)
			return designator_list((AstDesignatorList) node);
		else if(node instanceof AstDesignator)
			return designator((AstDesignator) node);
		else if(node instanceof AstConstant)
			return constant((AstConstant) node);
		else if(node instanceof AstLiteral)
			return literal((AstLiteral) node);
		else if(node instanceof AstIdExpression)
			return id_expression((AstIdExpression) node);
		else if(node instanceof AstArithAssignExpression)
			return arith_assign_expression((AstArithAssignExpression) node);
		else if(node instanceof AstArithBinaryExpression)
			return arith_binary_expression((AstArithBinaryExpression) node);
		else if(node instanceof AstArithUnaryExpression)
			return arith_unary_expression((AstArithUnaryExpression) node);
		else if(node instanceof AstAssignExpression)
			return assign_expression((AstAssignExpression) node);
		else if(node instanceof AstBitwiseAssignExpression)
			return bitwise_assign_expression((AstBitwiseAssignExpression) node);
		else if(node instanceof AstBitwiseBinaryExpression)
			return bitwise_binary_expression((AstBitwiseBinaryExpression) node);
		else if(node instanceof AstBitwiseUnaryExpression)
			return bitwise_unary_expression((AstBitwiseUnaryExpression) node);
		else if(node instanceof AstIncrePostfixExpression)
			return increment_postfix_expression((AstIncrePostfixExpression) node);
		else if(node instanceof AstIncreUnaryExpression)
			return increment_unary_expression((AstIncreUnaryExpression) node);
		else if(node instanceof AstLogicBinaryExpression)
			return logic_binary_expression((AstLogicBinaryExpression) node);
		else if(node instanceof AstLogicUnaryExpression)
			return logic_unary_expression((AstLogicUnaryExpression) node);
		else if(node instanceof AstRelationExpression)
			return relational_expression((AstRelationExpression) node);
		else if(node instanceof AstShiftAssignExpression)
			return shift_assign_expression((AstShiftAssignExpression) node);
		else if(node instanceof AstShiftBinaryExpression)
			return shift_binary_expression((AstShiftBinaryExpression) node);
		else if(node instanceof AstArrayExpression)
			return array_expression((AstArrayExpression) node);
		else if(node instanceof AstCastExpression)
			return cast_expression((AstCastExpression) node);
		else if(node instanceof AstCommaExpression)
			return comma_expression((AstCommaExpression) node);
		else if(node instanceof AstConditionalExpression)
			return conditional_expression((AstConditionalExpression) node);
		else if(node instanceof AstConstExpression)
			return const_expression((AstConstExpression) node);
		else if(node instanceof AstFieldExpression)
			return field_expression((AstFieldExpression) node);
		else if(node instanceof AstField)
			return field((AstField) node);
		else if(node instanceof AstFunCallExpression)
			return fun_call_expression((AstFunCallExpression) node);
		else if(node instanceof AstArgumentList)
			return argument_list((AstArgumentList) node);
		else if(node instanceof AstParanthExpression)
			return parenthesis_expression((AstParanthExpression) node);
		else if(node instanceof AstSizeofExpression)
			return sizeof_expression((AstSizeofExpression) node);
		else if(node instanceof AstPointUnaryExpression)
			return pointer_unary_expression((AstPointUnaryExpression) node);
		else if(node instanceof AstExpressionStatement)
			return expression_statement((AstExpressionStatement) node);
		else if(node instanceof AstDeclarationStatement)
			return declaration_statement((AstDeclarationStatement) node);
		else if(node instanceof AstCompoundStatement)
			return compound_statement((AstCompoundStatement) node);
		else if(node instanceof AstStatementList)
			return statement_list((AstStatementList) node);
		else if(node instanceof AstBreakStatement)
			return break_statement((AstBreakStatement) node);
		else if(node instanceof AstContinueStatement)
			return continue_statement((AstContinueStatement) node);
		else if(node instanceof AstGotoStatement)
			return goto_statement((AstGotoStatement) node);
		else if(node instanceof AstLabel)
			return label((AstLabel) node);
		else if(node instanceof AstReturnStatement)
			return return_statement((AstReturnStatement) node);
		else if(node instanceof AstCaseStatement)
			return case_statement((AstCaseStatement) node);
		else if(node instanceof AstDefaultStatement)
			return default_statement((AstDefaultStatement) node);
		else if(node instanceof AstLabeledStatement)
			return labeled_statement((AstLabeledStatement) node);
		else if(node instanceof AstIfStatement)
			return if_statement((AstIfStatement) node);
		else if(node instanceof AstSwitchStatement)
			return switch_statement((AstSwitchStatement) node);
		else if(node instanceof AstWhileStatement)
			return while_statement((AstWhileStatement) node);
		else if(node instanceof AstDoWhileStatement)
			return do_while_statement((AstDoWhileStatement) node);
		else if(node instanceof AstForStatement)
			return for_statement((AstForStatement) node);
		else throw new IllegalArgumentException("Unknown type: " + node.getClass().getSimpleName());
	}
	
	/* unit and root */
	protected abstract Object translation_unit(AstTranslationUnit node) throws Exception;
	protected abstract Object function_definition(AstFunctionDefinition node) throws Exception;
	protected abstract Object declaration_list(AstDeclarationList node) throws Exception;
	/* basic node */
	protected abstract Object keyword(AstKeyword node) throws Exception;
	protected abstract Object punctuator(AstPunctuator node) throws Exception;
	protected abstract Object operator(AstOperator node) throws Exception;
	/* declaration */
	protected abstract Object declaration(AstDeclaration node) throws Exception;
	protected abstract Object type_name(AstTypeName node) throws Exception;
	/* specifier */
	protected abstract Object declaration_specifiers(AstDeclarationSpecifiers node) throws Exception;
	protected abstract Object specifier_qualifier_list(AstSpecifierQualifierList node) throws Exception;
	protected abstract Object storage_class(AstStorageClass node) throws Exception;
	protected abstract Object type_qualifier(AstTypeQualifier node) throws Exception;
	protected abstract Object function_qualifier(AstFunctionQualifier node) throws Exception;
	protected abstract Object type_keyword(AstTypeKeyword node) throws Exception;
	protected abstract Object typedef_name(AstTypedefName node) throws Exception;
	protected abstract Object struct_specifier(AstStructSpecifier node) throws Exception;
	protected abstract Object union_specifier(AstUnionSpecifier node) throws Exception;
	protected abstract Object enum_specifier(AstEnumSpecifier node) throws Exception;
	protected abstract Object struct_union_body(AstStructUnionBody node) throws Exception;
	protected abstract Object struct_declaration_list(AstStructDeclarationList node) throws Exception;
	protected abstract Object struct_declaration(AstStructDeclaration node) throws Exception;
	protected abstract Object struct_declarator_list(AstStructDeclaratorList node) throws Exception;
	protected abstract Object struct_declarator(AstStructDeclarator node) throws Exception;
	protected abstract Object enumerator_body(AstEnumeratorBody node) throws Exception;
	protected abstract Object enumerator_list(AstEnumeratorList node) throws Exception;
	protected abstract Object enumerator(AstEnumerator node) throws Exception;
	/* declarator */
	protected abstract Object ast_name(AstName node) throws Exception;
	protected abstract Object declarator(AstDeclarator node) throws Exception;
	protected abstract Object abs_declarator(AstAbsDeclarator node) throws Exception;
	protected abstract Object dimension(AstDimension node) throws Exception;
	protected abstract Object array_qualifier_list(AstArrayQualifierList node) throws Exception;
	protected abstract Object parameter_body(AstParameterBody node) throws Exception;
	protected abstract Object parameter_type_list(AstParameterTypeList node) throws Exception;
	protected abstract Object parameter_list(AstParameterList node) throws Exception;
	protected abstract Object parameter_declaration(AstParameterDeclaration node) throws Exception;
	protected abstract Object identifier_list(AstIdentifierList node) throws Exception;
	protected abstract Object pointer(AstPointer node) throws Exception;
	protected abstract Object init_declarator_list(AstInitDeclaratorList node) throws Exception;
	protected abstract Object init_declarator(AstInitDeclarator node) throws Exception;
	
	/* initializer */
	protected abstract Object initializer(AstInitializer node) throws Exception;
	protected abstract Object initializer_body(AstInitializerBody node) throws Exception;
	protected abstract Object initializer_list(AstInitializerList node) throws Exception;
	protected abstract Object field_initializer(AstFieldInitializer node) throws Exception;
	protected abstract Object designator_list(AstDesignatorList node) throws Exception;
	protected abstract Object designator(AstDesignator node) throws Exception;
	/* expression */
	protected abstract Object constant(AstConstant node) throws Exception;
	protected abstract Object literal(AstLiteral node) throws Exception;
	protected abstract Object id_expression(AstIdExpression node) throws Exception;
	protected abstract Object arith_assign_expression(AstArithAssignExpression node) throws Exception;
	protected abstract Object arith_binary_expression(AstArithBinaryExpression node) throws Exception;
	protected abstract Object arith_unary_expression(AstArithUnaryExpression node) throws Exception;
	protected abstract Object assign_expression(AstAssignExpression node) throws Exception;
	protected abstract Object bitwise_assign_expression(AstBitwiseAssignExpression node) throws Exception;
	protected abstract Object bitwise_binary_expression(AstBitwiseBinaryExpression node) throws Exception;
	protected abstract Object bitwise_unary_expression(AstBitwiseUnaryExpression node) throws Exception;
	protected abstract Object increment_postfix_expression(AstIncrePostfixExpression node) throws Exception;
	protected abstract Object increment_unary_expression(AstIncreUnaryExpression node) throws Exception;
	protected abstract Object logic_binary_expression(AstLogicBinaryExpression node) throws Exception;
	protected abstract Object logic_unary_expression(AstLogicUnaryExpression node) throws Exception;
	protected abstract Object pointer_unary_expression(AstPointUnaryExpression node) throws Exception;
	protected abstract Object relational_expression(AstRelationExpression node) throws Exception;
	protected abstract Object shift_assign_expression(AstShiftAssignExpression node) throws Exception;
	protected abstract Object shift_binary_expression(AstShiftBinaryExpression node) throws Exception;
	protected abstract Object array_expression(AstArrayExpression node) throws Exception;
	protected abstract Object cast_expression(AstCastExpression node) throws Exception;
	protected abstract Object comma_expression(AstCommaExpression node) throws Exception;
	protected abstract Object conditional_expression(AstConditionalExpression node) throws Exception;
	protected abstract Object const_expression(AstConstExpression node) throws Exception;
	protected abstract Object field_expression(AstFieldExpression node) throws Exception;
	protected abstract Object field(AstField node) throws Exception;
	protected abstract Object fun_call_expression(AstFunCallExpression node) throws Exception;
	protected abstract Object argument_list(AstArgumentList node) throws Exception;
	protected abstract Object parenthesis_expression(AstParanthExpression node) throws Exception;
	protected abstract Object sizeof_expression(AstSizeofExpression node) throws Exception;
	/* statement */
	protected abstract Object expression_statement(AstExpressionStatement node) throws Exception;
	protected abstract Object declaration_statement(AstDeclarationStatement node) throws Exception;
	protected abstract Object compound_statement(AstCompoundStatement node) throws Exception;
	protected abstract Object statement_list(AstStatementList node) throws Exception;
	protected abstract Object break_statement(AstBreakStatement node) throws Exception;
	protected abstract Object continue_statement(AstContinueStatement node) throws Exception;
	protected abstract Object goto_statement(AstGotoStatement node) throws Exception;
	protected abstract Object label(AstLabel node) throws Exception;
	protected abstract Object return_statement(AstReturnStatement node) throws Exception;
	protected abstract Object case_statement(AstCaseStatement node) throws Exception;
	protected abstract Object default_statement(AstDefaultStatement node) throws Exception;
	protected abstract Object labeled_statement(AstLabeledStatement node) throws Exception;
	protected abstract Object if_statement(AstIfStatement node) throws Exception;
	protected abstract Object switch_statement(AstSwitchStatement node) throws Exception;
	protected abstract Object while_statement(AstWhileStatement node) throws Exception;
	protected abstract Object do_while_statement(AstDoWhileStatement node) throws Exception;
	protected abstract Object for_statement(AstForStatement node) throws Exception;
	
}
