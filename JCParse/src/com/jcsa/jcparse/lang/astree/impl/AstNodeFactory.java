package com.jcsa.jcparse.lang.astree.impl;

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
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifier;
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
import com.jcsa.jcparse.lang.astree.impl.base.AstDirectiveImpl;
import com.jcsa.jcparse.lang.astree.impl.base.AstFieldImpl;
import com.jcsa.jcparse.lang.astree.impl.base.AstKeywordImpl;
import com.jcsa.jcparse.lang.astree.impl.base.AstOperatorImpl;
import com.jcsa.jcparse.lang.astree.impl.base.AstPunctuatorImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.AstDeclarationImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.AstTypeNameImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstAbsDeclaratorImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstArrayQualifierListImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstDeclaratorImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstDimensionImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstIdentifierListImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstInitDeclaratorImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstInitDeclaratorListImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstNameImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstParameterBodyImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstParameterDeclarationImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstParameterListImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstParameterTypeListImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.declarator.AstPointerImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.initializer.AstDesignatorImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.initializer.AstDesignatorListImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.initializer.AstFieldInitializerImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.initializer.AstInitializerBodyImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.initializer.AstInitializerImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.initializer.AstInitializerListImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstDeclarationSpecifiersImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstEnumSpecifierImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstEnumeratorBodyImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstEnumeratorImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstEnumeratorListImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstFunctionQualifierImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstSpecifierQualifierListImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstStorageClassImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstStructDeclarationImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstStructDeclarationListImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstStructDeclaratorImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstStructDeclaratorListImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstStructSpecifierImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstStructUnionBodyImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstTypeKeywordImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstTypeQualifierImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstTypedefNameImpl;
import com.jcsa.jcparse.lang.astree.impl.decl.specifier.AstUnionSpecifierImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.base.AstConstantImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.base.AstIdExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.base.AstLiteralImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstArithAssignExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstArithBinaryExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstArithUnaryExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstAssignExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstBitwiseAssignExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstBitwiseBinaryExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstBitwiseUnaryExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstIncrePostfixExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstIncreUnaryExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstLogicBinaryExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstLogicUnaryExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstPointUnaryExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstRelationExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstShiftAssignExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.oprt.AstShiftBinaryExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.othr.AstArgumentListImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.othr.AstArrayExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.othr.AstCastExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.othr.AstCommaExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.othr.AstConditionalExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.othr.AstConstExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.othr.AstFieldExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.othr.AstFunCallExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.othr.AstParanthExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.expr.othr.AstSizeofExpressionImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstHeaderImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstMacroBodyImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstMacroImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstMacroListImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessDefineLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessElifLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessElseLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessEndifLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessErrorLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessIfLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessIfdefLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessIfndefLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessIncludeLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessLineLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessNoneLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessPragmaLineImpl;
import com.jcsa.jcparse.lang.astree.impl.pline.AstPreprocessUndefLineImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstBreakStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstCaseStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstCompoundStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstContinueStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstDeclarationStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstDefaultStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstDoWhileStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstExpressionStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstForStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstGotoStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstIfStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstLabelImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstLabeledStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstReturnStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstStatementListImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstSwitchStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.stmt.AstWhileStatementImpl;
import com.jcsa.jcparse.lang.astree.impl.unit.AstDeclarationListImpl;
import com.jcsa.jcparse.lang.astree.impl.unit.AstFunctionDefinitionImpl;
import com.jcsa.jcparse.lang.astree.impl.unit.AstTranslationUnitImpl;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstHeader;
import com.jcsa.jcparse.lang.astree.pline.AstMacro;
import com.jcsa.jcparse.lang.astree.pline.AstMacroBody;
import com.jcsa.jcparse.lang.astree.pline.AstMacroList;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessDefineLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessElifLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessElseLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessEndifLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessErrorLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessIfLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessIfdefLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessIfndefLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessIncludeLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessLineLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessNoneLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessPragmaLine;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessUndefLine;
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
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.CDirective;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * To produce AST-node in a regular way
 *
 * @author yukimula
 */
public class AstNodeFactory {

	/**
	 * constructor
	 */
	public AstNodeFactory() {
	}

	// basic node
	public AstKeyword new_keyword(CKeyword keyword) throws Exception {
		return new AstKeywordImpl(keyword);
	}

	public AstOperator new_operator(COperator operator) throws Exception {
		return new AstOperatorImpl(operator);
	}

	public AstPunctuator new_punctuator(CPunctuator punc) throws Exception {
		return new AstPunctuatorImpl(punc);
	}

	// expression
	public AstIdExpression new_id_expression(String id) throws Exception {
		return new AstIdExpressionImpl(id);
	}

	public AstConstant new_constant(CConstant constant) throws Exception {
		return new AstConstantImpl(constant);
	}

	public AstLiteral new_literal(String literal) throws Exception {
		return new AstLiteralImpl(literal);
	}

	public AstArithAssignExpression new_arith_assign_expression(AstExpression lop, AstOperator op, AstExpression rop)
			throws Exception {
		return new AstArithAssignExpressionImpl(lop, op, rop);
	}

	public AstArithBinaryExpression new_arith_binary_expression(AstExpression lop, AstOperator op, AstExpression rop)
			throws Exception {
		return new AstArithBinaryExpressionImpl(lop, op, rop);
	}

	public AstArithUnaryExpression new_arith_unary_expression(AstOperator op, AstExpression expr) throws Exception {
		return new AstArithUnaryExpressionImpl(op, expr);
	}

	public AstAssignExpression new_assign_expression(AstExpression lop, AstOperator op, AstExpression rop)
			throws Exception {
		return new AstAssignExpressionImpl(lop, op, rop);
	}

	public AstBitwiseAssignExpression new_bitwise_assign_expression(AstExpression lop, AstOperator op,
			AstExpression rop) throws Exception {
		return new AstBitwiseAssignExpressionImpl(lop, op, rop);
	}

	public AstBitwiseBinaryExpression new_bitwise_binary_expression(AstExpression lop, AstOperator op,
			AstExpression rop) throws Exception {
		return new AstBitwiseBinaryExpressionImpl(lop, op, rop);
	}

	public AstBitwiseUnaryExpression new_bitwise_unary_expression(AstOperator op, AstExpression expr) throws Exception {
		return new AstBitwiseUnaryExpressionImpl(op, expr);
	}

	public AstIncrePostfixExpression new_increment_postfix_expression(AstExpression expr, AstOperator op)
			throws Exception {
		return new AstIncrePostfixExpressionImpl(expr, op);
	}

	public AstIncreUnaryExpression new_increment_unary_expression(AstOperator op, AstExpression expr) throws Exception {
		return new AstIncreUnaryExpressionImpl(op, expr);
	}

	public AstLogicBinaryExpression new_logic_binary_expression(AstExpression lop, AstOperator op, AstExpression rop)
			throws Exception {
		return new AstLogicBinaryExpressionImpl(lop, op, rop);
	}

	public AstLogicUnaryExpression new_logic_unary_expression(AstOperator op, AstExpression expr) throws Exception {
		return new AstLogicUnaryExpressionImpl(op, expr);
	}

	public AstPointUnaryExpression new_pointer_unary_expression(AstOperator op, AstExpression expr) throws Exception {
		return new AstPointUnaryExpressionImpl(op, expr);
	}

	public AstRelationExpression new_relation_expression(AstExpression lop, AstOperator op, AstExpression rop)
			throws Exception {
		return new AstRelationExpressionImpl(lop, op, rop);
	}

	public AstShiftAssignExpression new_shift_assign_expression(AstExpression lop, AstOperator op, AstExpression rop)
			throws Exception {
		return new AstShiftAssignExpressionImpl(lop, op, rop);
	}

	public AstShiftBinaryExpression new_shift_binary_expression(AstExpression lop, AstOperator op, AstExpression rop)
			throws Exception {
		return new AstShiftBinaryExpressionImpl(lop, op, rop);
	}

	public AstArgumentList new_argument_list(AstExpression arg) throws Exception {
		return new AstArgumentListImpl(arg);
	}

	public AstFunCallExpression new_funcall_expression(AstExpression func, AstPunctuator lparanth,
			AstPunctuator rparanth) throws Exception {
		return new AstFunCallExpressionImpl(func, lparanth, rparanth);
	}

	public AstFunCallExpression new_funcall_expression(AstExpression func, AstPunctuator lparanth, AstArgumentList list,
			AstPunctuator rparanth) throws Exception {
		return new AstFunCallExpressionImpl(func, lparanth, list, rparanth);
	}

	public AstArrayExpression new_array_expression(AstExpression array, AstPunctuator lbracket, AstExpression dimension,
			AstPunctuator rbracket) throws Exception {
		return new AstArrayExpressionImpl(array, lbracket, dimension, rbracket);
	}

	public AstCastExpression new_cast_expression(AstPunctuator lparanth, AstTypeName typename, AstPunctuator rparanth,
			AstExpression expr) throws Exception {
		return new AstCastExpressionImpl(lparanth, typename, rparanth, expr);
	}

	public AstCommaExpression new_comma_expression(AstExpression expr) throws Exception {
		return new AstCommaExpressionImpl(expr);
	}

	public AstConditionalExpression new_conditional_expression(AstExpression condition, AstPunctuator question,
			AstExpression tbranch, AstPunctuator colon, AstExpression fbranch) throws Exception {
		return new AstConditionalExpressionImpl(condition, question, tbranch, colon, fbranch);
	}

	public AstConstExpression new_const_expression(AstExpression expr) throws Exception {
		return new AstConstExpressionImpl(expr);
	}

	public AstFieldExpression new_field_expression(AstExpression body, AstPunctuator op, AstField field)
			throws Exception {
		return new AstFieldExpressionImpl(body, op, field);
	}

	public AstField new_field(String name) throws Exception {
		return new AstFieldImpl(name);
	}

	public AstParanthExpression new_paranth_expression(AstPunctuator lparanth, AstExpression expr,
			AstPunctuator rparanth) throws Exception {
		return new AstParanthExpressionImpl(lparanth, expr, rparanth);
	}

	public AstSizeofExpression new_sizeof_expression(AstKeyword sizeof, AstPunctuator lparanth, AstTypeName typename,
			AstPunctuator rparanth) throws Exception {
		return new AstSizeofExpressionImpl(sizeof, lparanth, typename, rparanth);
	}

	public AstSizeofExpression new_sizeof_expression(AstKeyword sizeof, AstExpression expr) throws Exception {
		return new AstSizeofExpressionImpl(sizeof, expr);
	}

	// pr_line
	public AstDirective new_directive(CDirective dir) throws Exception {
		return new AstDirectiveImpl(dir);
	}

	public AstHeader new_system_header(String path) throws Exception {
		return new AstHeaderImpl(true, path);
	}

	public AstHeader new_user_header(String path) throws Exception {
		return new AstHeaderImpl(false, path);
	}

	public AstMacro new_macro(String name) throws Exception {
		return new AstMacroImpl(name);
	}

	public AstMacroList new_macro_list(AstPunctuator lparanth, AstPunctuator rparanth) throws Exception {
		return new AstMacroListImpl(lparanth, rparanth);
	}

	public AstMacroList new_macro_list(AstPunctuator lparanth, AstIdentifierList list, AstPunctuator rparanth)
			throws Exception {
		return new AstMacroListImpl(lparanth, list, rparanth);
	}

	public AstMacroList new_macro_list(AstPunctuator lparanth, AstPunctuator ellipsis, AstPunctuator rparanth)
			throws Exception {
		return new AstMacroListImpl(lparanth, ellipsis, rparanth);
	}

	public AstMacroBody new_macro_body() throws Exception {
		return new AstMacroBodyImpl();
	}

	public AstPreprocessElifLine new_elif_line(AstDirective elif, AstConstExpression expr) throws Exception {
		return new AstPreprocessElifLineImpl(elif, expr);
	}

	public AstPreprocessElseLine new_else_line(AstDirective _else) throws Exception {
		return new AstPreprocessElseLineImpl(_else);
	}

	public AstPreprocessEndifLine new_endif_line(AstDirective endif) throws Exception {
		return new AstPreprocessEndifLineImpl(endif);
	}

	public AstPreprocessErrorLine new_error_line(AstDirective error, AstMacroBody body) throws Exception {
		return new AstPreprocessErrorLineImpl(error, body);
	}

	public AstPreprocessIfdefLine new_ifdef_line(AstDirective ifdef, AstMacro macro) throws Exception {
		return new AstPreprocessIfdefLineImpl(ifdef, macro);
	}

	public AstPreprocessIfLine new_if_line(AstDirective _if, AstConstExpression expr) throws Exception {
		return new AstPreprocessIfLineImpl(_if, expr);
	}

	public AstPreprocessIfndefLine new_ifndef_line(AstDirective ifndef, AstMacro macro) throws Exception {
		return new AstPreprocessIfndefLineImpl(ifndef, macro);
	}

	public AstPreprocessDefineLine new_define_line(AstDirective define, AstMacro macro, AstMacroBody body)
			throws Exception {
		return new AstPreprocessDefineLineImpl(define, macro, body);
	}

	public AstPreprocessDefineLine new_define_line(AstDirective define, AstMacro macro, AstMacroList list,
			AstMacroBody body) throws Exception {
		return new AstPreprocessDefineLineImpl(define, macro, list, body);
	}

	public AstPreprocessIncludeLine new_include_line(AstDirective include, AstHeader header) throws Exception {
		return new AstPreprocessIncludeLineImpl(include, header);
	}

	public AstPreprocessLineLine new_line_line(AstDirective line, AstConstant expr) throws Exception {
		return new AstPreprocessLineLineImpl(line, expr);
	}

	public AstPreprocessLineLine new_line_line(AstDirective line, AstConstant expr, AstLiteral path) throws Exception {
		return new AstPreprocessLineLineImpl(line, expr, path);
	}

	public AstPreprocessPragmaLine new_pragma_line(AstDirective pragma, AstMacroBody body) throws Exception {
		return new AstPreprocessPragmaLineImpl(pragma, body);
	}

	public AstPreprocessUndefLine new_undef_line(AstDirective undef, AstMacro macro) throws Exception {
		return new AstPreprocessUndefLineImpl(undef, macro);
	}

	public AstPreprocessNoneLine new_hash_line(AstPunctuator hash) throws Exception {
		return new AstPreprocessNoneLineImpl(hash);
	}

	// statement
	public AstLabel new_label(String label) throws Exception {
		return new AstLabelImpl(label);
	}

	public AstBreakStatement new_break_statement(AstKeyword _break, AstPunctuator semicolon) throws Exception {
		return new AstBreakStatementImpl(_break, semicolon);
	}

	public AstContinueStatement new_continue_statement(AstKeyword _continue, AstPunctuator semicolon) throws Exception {
		return new AstContinueStatementImpl(_continue, semicolon);
	}

	public AstGotoStatement new_goto_statement(AstKeyword _goto, AstLabel label, AstPunctuator semicolon)
			throws Exception {
		return new AstGotoStatementImpl(_goto, label, semicolon);
	}

	public AstReturnStatement new_return_statement(AstKeyword _return, AstPunctuator semicolon) throws Exception {
		return new AstReturnStatementImpl(_return, semicolon);
	}

	public AstReturnStatement new_return_statement(AstKeyword _return, AstExpression expression,
			AstPunctuator semicolon) throws Exception {
		return new AstReturnStatementImpl(_return, expression, semicolon);
	}

	public AstCaseStatement new_case_statement(AstKeyword _case, AstConstExpression expr, AstPunctuator colon)
			throws Exception {
		return new AstCaseStatementImpl(_case, expr, colon);
	}

	public AstLabeledStatement new_labeled_statement(AstLabel label, AstPunctuator colon) throws Exception {
		return new AstLabeledStatementImpl(label, colon);
	}

	public AstDefaultStatement new_default_statement(AstKeyword _default, AstPunctuator colon) throws Exception {
		return new AstDefaultStatementImpl(_default, colon);
	}

	public AstDeclarationStatement new_declaration_statement(AstDeclaration declaration, AstPunctuator semicolon)
			throws Exception {
		return new AstDeclarationStatementImpl(declaration, semicolon);
	}

	public AstExpressionStatement new_expression_statement(AstExpression expr, AstPunctuator semicolon)
			throws Exception {
		return new AstExpressionStatementImpl(expr, semicolon);
	}

	public AstExpressionStatement new_expression_statement(AstPunctuator semicolon) throws Exception {
		return new AstExpressionStatementImpl(semicolon);
	}

	public AstDoWhileStatement new_do_while_statement(AstKeyword _do, AstStatement body, AstKeyword _while,
			AstPunctuator lparanth, AstExpression expr, AstPunctuator rparanth, AstPunctuator semicolon)
			throws Exception {
		return new AstDoWhileStatementImpl(_do, body, _while, lparanth, expr, rparanth, semicolon);
	}

	public AstWhileStatement new_while_statement(AstKeyword _while, AstPunctuator lparanth, AstExpression expr,
			AstPunctuator rparanth, AstStatement body) throws Exception {
		return new AstWhileStatementImpl(_while, lparanth, expr, rparanth, body);
	}

	public AstForStatement new_for_statement(AstKeyword _for, AstPunctuator lparanth,
			AstDeclarationStatement initializer, AstExpressionStatement condition, AstExpression increment,
			AstPunctuator rparanth, AstStatement body) throws Exception {
		return new AstForStatementImpl(_for, lparanth, initializer, condition, increment, rparanth, body);
	}

	public AstForStatement new_for_statement(AstKeyword _for, AstPunctuator lparanth,
			AstExpressionStatement initializer, AstExpressionStatement condition, AstExpression increment,
			AstPunctuator rparanth, AstStatement body) throws Exception {
		return new AstForStatementImpl(_for, lparanth, initializer, condition, increment, rparanth, body);
	}

	public AstForStatement new_for_statement(AstKeyword _for, AstPunctuator lparanth,
			AstDeclarationStatement initializer, AstExpressionStatement condition, AstPunctuator rparanth,
			AstStatement body) throws Exception {
		return new AstForStatementImpl(_for, lparanth, initializer, condition, rparanth, body);
	}

	public AstForStatement new_for_statement(AstKeyword _for, AstPunctuator lparanth,
			AstExpressionStatement initializer, AstExpressionStatement condition, AstPunctuator rparanth,
			AstStatement body) throws Exception {
		return new AstForStatementImpl(_for, lparanth, initializer, condition, rparanth, body);
	}

	public AstCompoundStatement new_compound_statement(AstPunctuator lbrace, AstPunctuator rbrace) throws Exception {
		return new AstCompoundStatementImpl(lbrace, rbrace);
	}

	public AstCompoundStatement new_compound_statement(AstPunctuator lbrace, AstStatementList list,
			AstPunctuator rbrace) throws Exception {
		return new AstCompoundStatementImpl(lbrace, list, rbrace);
	}

	public AstStatementList new_statement_list(AstStatement stmt) throws Exception {
		return new AstStatementListImpl(stmt);
	}

	public AstSwitchStatement new_switch_statement(AstKeyword _switch, AstPunctuator lparanth, AstExpression expr,
			AstPunctuator rparanth, AstStatement body) throws Exception {
		return new AstSwitchStatementImpl(_switch, lparanth, expr, rparanth, body);
	}

	public AstIfStatement new_if_statement(AstKeyword _if, AstPunctuator lparanth, AstExpression expr,
			AstPunctuator rparanth, AstStatement tbranch) throws Exception {
		return new AstIfStatementImpl(_if, lparanth, expr, rparanth, tbranch);
	}

	public AstIfStatement new_if_statement(AstKeyword _if, AstPunctuator lparanth, AstExpression expr,
			AstPunctuator rparanth, AstStatement tbranch, AstKeyword _else, AstStatement fbranch) throws Exception {
		return new AstIfStatementImpl(_if, lparanth, expr, rparanth, tbranch, _else, fbranch);
	}

	// declaration
	public AstDeclaration new_declaration(AstDeclarationSpecifiers specifiers, AstInitDeclaratorList declarators)
			throws Exception {
		return new AstDeclarationImpl(specifiers, declarators);
	}

	public AstDeclaration new_declaration(AstDeclarationSpecifiers specifiers) throws Exception {
		return new AstDeclarationImpl(specifiers);
	}

	public AstTypeName new_typename(AstSpecifierQualifierList specifiers, AstAbsDeclarator declarator)
			throws Exception {
		return new AstTypeNameImpl(specifiers, declarator);
	}

	public AstTypeName new_typename(AstSpecifierQualifierList specifiers) throws Exception {
		return new AstTypeNameImpl(specifiers);
	}

	public AstDeclarationSpecifiers new_declaration_specifiers(AstSpecifier specifier) throws Exception {
		return new AstDeclarationSpecifiersImpl(specifier);
	}

	public AstStorageClass new_storage_class(AstKeyword keyword) throws Exception {
		return new AstStorageClassImpl(keyword);
	}

	public AstTypeQualifier new_type_qualifier(AstKeyword keyword) throws Exception {
		return new AstTypeQualifierImpl(keyword);
	}

	public AstFunctionQualifier new_function_qualifier(AstKeyword keyword) throws Exception {
		return new AstFunctionQualifierImpl(keyword);
	}

	public AstTypeKeyword new_type_keyword(AstKeyword keyword) throws Exception {
		return new AstTypeKeywordImpl(keyword);
	}

	public AstStructSpecifier new_struct_specifier(AstKeyword struct, AstName name) throws Exception {
		return new AstStructSpecifierImpl(struct, name);
	}

	public AstStructSpecifier new_struct_specifier(AstKeyword struct, AstStructUnionBody body) throws Exception {
		return new AstStructSpecifierImpl(struct, body);
	}

	public AstStructSpecifier new_struct_specifier(AstKeyword struct, AstName name, AstStructUnionBody body)
			throws Exception {
		return new AstStructSpecifierImpl(struct, name, body);
	}

	public AstUnionSpecifier new_union_specifier(AstKeyword union, AstName name) throws Exception {
		return new AstUnionSpecifierImpl(union, name);
	}

	public AstUnionSpecifier new_union_specifier(AstKeyword union, AstStructUnionBody body) throws Exception {
		return new AstUnionSpecifierImpl(union, body);
	}

	public AstUnionSpecifier new_union_specifier(AstKeyword union, AstName name, AstStructUnionBody body)
			throws Exception {
		return new AstUnionSpecifierImpl(union, name, body);
	}

	public AstEnumSpecifier new_enum_specifier(AstKeyword _enum, AstName name) throws Exception {
		return new AstEnumSpecifierImpl(_enum, name);
	}

	public AstEnumSpecifier new_enum_specifier(AstKeyword _enum, AstEnumeratorBody body) throws Exception {
		return new AstEnumSpecifierImpl(_enum, body);
	}

	public AstEnumSpecifier new_enum_specifier(AstKeyword _enum, AstName name, AstEnumeratorBody body)
			throws Exception {
		return new AstEnumSpecifierImpl(_enum, name, body);
	}

	public AstEnumeratorBody new_enumerator_body(AstPunctuator lbrace, AstEnumeratorList elist, AstPunctuator rbrace)
			throws Exception {
		return new AstEnumeratorBodyImpl(lbrace, elist, rbrace);
	}

	public AstEnumeratorBody new_enumerator_body(AstPunctuator lbrace, AstEnumeratorList elist, AstPunctuator comma,
			AstPunctuator rbrace) throws Exception {
		return new AstEnumeratorBodyImpl(lbrace, elist, comma, rbrace);
	}

	public AstEnumeratorList new_enumerator_list(AstEnumerator enumerator) throws Exception {
		return new AstEnumeratorListImpl(enumerator);
	}

	public AstEnumerator new_enumerator(AstName name) throws Exception {
		return new AstEnumeratorImpl(name);
	}

	public AstEnumerator new_enumerator(AstName name, AstPunctuator assign, AstConstExpression expr) throws Exception {
		return new AstEnumeratorImpl(name, assign, expr);
	}

	public AstSpecifierQualifierList new_specifier_qualifier_list(AstSpecifier specifier) throws Exception {
		return new AstSpecifierQualifierListImpl(specifier);
	}

	public AstStructUnionBody new_struct_union_body(AstPunctuator lbrace, AstStructDeclarationList list,
			AstPunctuator rbrace) throws Exception {
		return new AstStructUnionBodyImpl(lbrace, list, rbrace);
	}

	public AstStructUnionBody new_struct_union_body(AstPunctuator lbrace, AstPunctuator rbrace) throws Exception {
		return new AstStructUnionBodyImpl(lbrace, rbrace);
	}

	public AstStructDeclarationList new_struct_declaration_list(AstStructDeclaration decl) throws Exception {
		return new AstStructDeclarationListImpl(decl);
	}

	public AstStructDeclaration new_struct_declaration(AstSpecifierQualifierList specifiers,
			AstStructDeclaratorList declarators, AstPunctuator semicolon) throws Exception {
		return new AstStructDeclarationImpl(specifiers, declarators, semicolon);
	}

	public AstStructDeclaratorList new_struct_declarator_list(AstStructDeclarator declarator) throws Exception {
		return new AstStructDeclaratorListImpl(declarator);
	}

	public AstStructDeclarator new_struct_declarator(AstDeclarator declarator, AstPunctuator colon,
			AstConstExpression expr) throws Exception {
		return new AstStructDeclaratorImpl(declarator, colon, expr);
	}

	public AstStructDeclarator new_struct_declarator(AstPunctuator colon, AstConstExpression expr) throws Exception {
		return new AstStructDeclaratorImpl(colon, expr);
	}

	public AstStructDeclarator new_struct_declarator(AstDeclarator declarator) throws Exception {
		return new AstStructDeclaratorImpl(declarator);
	}

	public AstTypedefName new_typedef_name(String name) throws Exception {
		return new AstTypedefNameImpl(name);
	}

	public AstDeclarator new_declarator(AstPointer pointer, AstDeclarator declarator) throws Exception {
		return new AstDeclaratorImpl(pointer, declarator);
	}

	public AstDeclarator new_declarator(AstDeclarator declarator, AstDimension dimension) throws Exception {
		return new AstDeclaratorImpl(declarator, dimension);
	}

	public AstDeclarator new_declarator(AstDeclarator declarator, AstParameterBody parambody) throws Exception {
		return new AstDeclaratorImpl(declarator, parambody);
	}

	public AstDeclarator new_declarator(AstPunctuator lparanth, AstDeclarator declarator, AstPunctuator rparanth)
			throws Exception {
		return new AstDeclaratorImpl(lparanth, declarator, rparanth);
	}

	public AstDeclarator new_declarator(AstName name) throws Exception {
		return new AstDeclaratorImpl(name);
	}

	public AstAbsDeclarator new_abs_declarator(AstPointer pointer, AstAbsDeclarator declarator) throws Exception {
		if (declarator == null)
			return new AstAbsDeclaratorImpl(pointer);
		else
			return new AstAbsDeclaratorImpl(pointer, declarator);
	}

	public AstAbsDeclarator new_abs_declarator(AstAbsDeclarator declarator, AstDimension dimension) throws Exception {
		if (declarator == null)
			return new AstAbsDeclaratorImpl(dimension);
		else
			return new AstAbsDeclaratorImpl(declarator, dimension);
	}

	public AstAbsDeclarator new_abs_declarator(AstAbsDeclarator declarator, AstParameterBody parambody)
			throws Exception {
		if (declarator == null)
			return new AstAbsDeclaratorImpl(parambody);
		else
			return new AstAbsDeclaratorImpl(declarator, parambody);
	}

	public AstAbsDeclarator new_abs_declarator(AstPunctuator lparanth, AstAbsDeclarator declarator,
			AstPunctuator rparanth) throws Exception {
		return new AstAbsDeclaratorImpl(lparanth, declarator, rparanth);
	}

	public AstArrayQualifierList new_array_qualifier_list(AstKeyword keyword) throws Exception {
		return new AstArrayQualifierListImpl(keyword);
	}

	public AstDimension new_dimension(AstPunctuator lbracket, AstPunctuator rbracket) throws Exception {
		return new AstDimensionImpl(lbracket, rbracket);
	}

	public AstDimension new_dimension(AstPunctuator lbracket, AstArrayQualifierList specifiers, AstPunctuator rbracket)
			throws Exception {
		return new AstDimensionImpl(lbracket, specifiers, rbracket);
	}

	public AstDimension new_dimension(AstPunctuator lbracket, AstConstExpression expr, AstPunctuator rbracket)
			throws Exception {
		return new AstDimensionImpl(lbracket, expr, rbracket);
	}

	public AstDimension new_dimension(AstPunctuator lbracket, AstArrayQualifierList specifiers, AstConstExpression expr,
			AstPunctuator rbracket) throws Exception {
		return new AstDimensionImpl(lbracket, specifiers, expr, rbracket);
	}

	public AstIdentifierList new_identifier_list(AstName name) throws Exception {
		return new AstIdentifierListImpl(name);
	}

	public AstInitDeclarator new_init_declarator(AstDeclarator declarator, AstPunctuator assign,
			AstInitializer initializer) throws Exception {
		return new AstInitDeclaratorImpl(declarator, assign, initializer);
	}

	public AstInitDeclarator new_init_declarator(AstDeclarator declarator) throws Exception {
		return new AstInitDeclaratorImpl(declarator);
	}

	public AstInitDeclaratorList new_init_declarator_list(AstInitDeclarator decl) throws Exception {
		return new AstInitDeclaratorListImpl(decl);
	}

	public AstName new_name(String name) throws Exception {
		return new AstNameImpl(name);
	}

	public AstParameterBody new_parameter_body(AstPunctuator lparanth, AstParameterTypeList tlist,
			AstPunctuator rparanth) throws Exception {
		return new AstParameterBodyImpl(lparanth, tlist, rparanth);
	}

	public AstParameterBody new_parameter_body(AstPunctuator lparanth, AstIdentifierList ilist, AstPunctuator rparanth)
			throws Exception {
		return new AstParameterBodyImpl(lparanth, ilist, rparanth);
	}

	public AstParameterBody new_parameter_body(AstPunctuator lparanth, AstPunctuator rparanth) throws Exception {
		return new AstParameterBodyImpl(lparanth, rparanth);
	}

	public AstParameterDeclaration new_parameter_declaration(AstDeclarationSpecifiers specifiers,
			AstDeclarator declarator) throws Exception {
		return new AstParameterDeclarationImpl(specifiers, declarator);
	}

	public AstParameterDeclaration new_parameter_declaration(AstDeclarationSpecifiers specifiers,
			AstAbsDeclarator declarator) throws Exception {
		return new AstParameterDeclarationImpl(specifiers, declarator);
	}

	public AstParameterDeclaration new_parameter_declaration(AstDeclarationSpecifiers specifiers) throws Exception {
		return new AstParameterDeclarationImpl(specifiers);
	}

	public AstParameterTypeList new_parameter_type_list(AstParameterList plist, AstPunctuator comma,
			AstPunctuator ellipsis) throws Exception {
		return new AstParameterTypeListImpl(plist, comma, ellipsis);
	}

	public AstParameterTypeList new_parameter_type_list(AstParameterList plist) throws Exception {
		return new AstParameterTypeListImpl(plist);
	}

	public AstParameterList new_parameter_list(AstParameterDeclaration param) throws Exception {
		return new AstParameterListImpl(param);
	}

	public AstPointer new_pointer(AstKeyword keyword) throws Exception {
		return new AstPointerImpl(keyword);
	}

	public AstPointer new_pointer(AstPunctuator ptr) throws Exception {
		return new AstPointerImpl(ptr);
	}

	public AstDesignator new_designator(AstPunctuator lbracket, AstConstExpression expr, AstPunctuator rbracket)
			throws Exception {
		return new AstDesignatorImpl(lbracket, expr, rbracket);
	}

	public AstDesignator new_designator(AstPunctuator dot, AstField field) throws Exception {
		return new AstDesignatorImpl(dot, field);
	}

	public AstDesignatorList new_designator_list(AstDesignator designator) throws Exception {
		return new AstDesignatorListImpl(designator);
	}

	public AstFieldInitializer new_field_initializer(AstDesignatorList designators, AstPunctuator assign,
			AstInitializer initializer) throws Exception {
		return new AstFieldInitializerImpl(designators, assign, initializer);
	}

	public AstFieldInitializer new_field_initializer(AstInitializer initializer) throws Exception {
		return new AstFieldInitializerImpl(initializer);
	}

	public AstInitializerBody new_initializer_body(AstPunctuator lbrace, AstInitializerList list, AstPunctuator comma,
			AstPunctuator rbrace) throws Exception {
		return new AstInitializerBodyImpl(lbrace, list, comma, rbrace);
	}

	public AstInitializerBody new_initializer_body(AstPunctuator lbrace, AstInitializerList list, AstPunctuator rbrace)
			throws Exception {
		return new AstInitializerBodyImpl(lbrace, list, rbrace);
	}

	public AstInitializerList new_initializer_list(AstFieldInitializer initializer) throws Exception {
		return new AstInitializerListImpl(initializer);
	}

	public AstInitializer new_initializer(AstExpression expr) throws Exception {
		return new AstInitializerImpl(expr);
	}

	public AstInitializer new_initializer(AstInitializerBody body) throws Exception {
		return new AstInitializerImpl(body);
	}

	// unit
	public AstDeclarationList new_declaration_list(AstDeclarationStatement stmt) throws Exception {
		return new AstDeclarationListImpl(stmt);
	}

	public AstFunctionDefinition new_function_definition(AstDeclarationSpecifiers specifiers, AstDeclarator declarator,
			AstCompoundStatement body) throws Exception {
		return new AstFunctionDefinitionImpl(specifiers, declarator, body);
	}

	public AstFunctionDefinition new_function_definition(AstDeclarationSpecifiers specifiers, AstDeclarator declarator,
			AstDeclarationList list, AstCompoundStatement body) throws Exception {
		return new AstFunctionDefinitionImpl(specifiers, declarator, list, body);
	}

	public AstTranslationUnit new_translation_unit() throws Exception {
		return new AstTranslationUnitImpl();
	}

}
