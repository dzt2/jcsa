package com.jcsa.jcparse.parse.parser1;

import java.util.Iterator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstAbsDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstArrayQualifierList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
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
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifierQualifierList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStorageClass;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaration;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarationList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructUnionBody;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypedefName;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstUnionSpecifier;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.impl.AstNodeFactory;
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
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessLine;
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
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.ctoken.CConstantToken;
import com.jcsa.jcparse.lang.ctoken.CDirectiveToken;
import com.jcsa.jcparse.lang.ctoken.CHeaderToken;
import com.jcsa.jcparse.lang.ctoken.CIdentifierToken;
import com.jcsa.jcparse.lang.ctoken.CKeywordToken;
import com.jcsa.jcparse.lang.ctoken.CLiteralToken;
import com.jcsa.jcparse.lang.ctoken.CNewlineToken;
import com.jcsa.jcparse.lang.ctoken.CPunctuatorToken;
import com.jcsa.jcparse.lang.ctoken.CToken;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.CDirective;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CEnumTypeName;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CLabelName;
import com.jcsa.jcparse.lang.scope.CMacroName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.scope.CStructTypeName;
import com.jcsa.jcparse.lang.scope.CTypeName;
import com.jcsa.jcparse.lang.scope.CTypedefName;
import com.jcsa.jcparse.lang.scope.CUnionTypeName;
import com.jcsa.jcparse.lang.scope.impl.CScopeImpl;
import com.jcsa.jcparse.lang.text.CLocation;
import com.jcsa.jcparse.lang.text.CText;
import com.jcsa.jcparse.parse.tokenizer.CTokenStream;

public class C99_Parser implements CParser {
	/** factory to produce AstNode **/
	protected AstNodeFactory factory;

	/**
	 * create a parser to match tokens for C99 standard syntax
	 */
	public C99_Parser() {
		factory = new AstNodeFactory();
	}

	// primary expression
	/**
	 * <code>prim_expr |--> identifier | constant | literal | ( expression ) </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_primary_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr;
		if ((expr = this.match_identifier_expression(stream, scope)) == null) {
			if ((expr = this.match_constant(stream)) == null) {
				if ((expr = this.match_literal(stream)) == null) {
					expr = this.match_paranth_expression(stream, scope);
				}
			}
		}

		return expr;
	}

	/**
	 * <code> prim_expr |--> identifier </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstIdExpression match_identifier_expression(CTokenStream stream, CScope scope) throws Exception {
		CToken token = stream.get_token();
		if (token instanceof CIdentifierToken) {
			String idname = ((CIdentifierToken) token).get_name();
			if (scope.has_name(idname)) {
				CName cname = scope.get_name(idname);
				if (cname instanceof CInstanceName || cname instanceof CMacroName
						|| cname instanceof CEnumeratorName) { /*
																 * must be
																 * pre-declared
																 */
					AstIdExpression expr = factory.new_id_expression(idname);
					expr.set_cname(cname);
					expr.set_location(token.get_location());
					stream.consume();
					return expr;
				}
			}
		}
		return null;
	}

	/**
	 * <code>prim_expr |--> constant</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstConstant match_constant(CTokenStream stream) throws Exception {
		CToken token = stream.get_token();
		if (token instanceof CConstantToken) {
			CConstant constant = ((CConstantToken) token).get_constant();
			AstConstant csnode = factory.new_constant(constant);
			csnode.set_location(token.get_location());
			stream.consume();
			return csnode;
		}
		return null;
	}

	/**
	 * <code>prim_expr |--> (literal)+</code>
	 *
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	protected AstLiteral match_literal(CTokenStream stream) throws Exception {
		int beg = -1, end = -1;
		String literal = "", lit;

		CToken token = stream.get_token();
		CText text = null;
		while (token instanceof CLiteralToken) {
			CLocation loc = token.get_location();
			lit = ((CLiteralToken) token).get_exec_literal();
			literal = literal + lit;
			text = loc.get_source();

			if (beg == -1)
				beg = loc.get_bias();
			end = loc.get_bias() + loc.get_length();

			stream.consume();
			token = stream.get_token();
		}

		if (text == null)
			return null;
		else {
			AstLiteral litnode = factory.new_literal(literal);
			litnode.set_location(text.get_location(beg, end - beg));
			return litnode;
		}
	}

	/**
	 * <code>prim_expr |--> ( expression )</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstParanthExpression match_paranth_expression(CTokenStream stream, CScope scope) throws Exception {
		int cursor = stream.get_cursor();

		AstPunctuator lparanth, rparanth;
		AstExpression subexpr;
		if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
			if ((subexpr = this.match_expression(stream, scope)) != null) {
				if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
					return factory.new_paranth_expression(lparanth, subexpr, rparanth);
				}
			}
		}

		stream.recover(cursor);
		return null;
	}

	// postfix expression
	/**
	 * <code>
	 * 	post_expr |--> prim_expr ( [ expression ] | ( argument_expression_list? ) | {. ->} identifier | {++, --} )* <br>
	 * 	post_expr |--> ( type_name ) initializer_body <br>
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_postfix_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;

		if ((next = this.match_primary_expression(stream, scope)) != null) {
			do {
				expr = next;
				CToken head = stream.get_token();
				if (head instanceof CPunctuatorToken) {
					switch (((CPunctuatorToken) head).get_punctuator()) {
					case left_bracket:
						next = this.match_postfix_dimension(stream, scope, next);
						break;
					case left_paranth:
						next = this.match_postfix_argument(stream, scope, next);
						break;
					case dot:
					case arrow:
						next = this.match_postfix_field(stream, next);
						break;
					case increment:
					case decrement:
						next = this.match_postfix_increment(stream, next);
						break;
					default:
						next = null;
					}
				} else
					next = null;
			} while (next != null);
		} else
			expr = this.match_postfix_cast(stream, scope);

		return expr;
	}

	/**
	 * <code>
	 * 	post_expr |--> post_expr [ expression ]
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @param child
	 * @return
	 * @throws Exception
	 */
	protected AstArrayExpression match_postfix_dimension(CTokenStream stream, CScope scope, AstExpression child)
			throws Exception {
		int cursor = stream.get_cursor();

		AstPunctuator lbracket, rbracket;
		AstExpression dimension;
		if ((lbracket = this.match_punctuator(stream, CPunctuator.left_bracket)) != null) {
			if ((dimension = this.match_expression(stream, scope)) != null) {
				if ((rbracket = this.match_punctuator(stream, CPunctuator.right_bracket)) != null) {
					return factory.new_array_expression(child, lbracket, dimension, rbracket);
				}
			}
		}

		stream.recover(cursor);
		return null;
	}

	/**
	 * <code>
	 * 	post_expr |--> post_expr ( argument_expr_list? )
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @param child
	 * @return
	 * @throws Exception
	 */
	protected AstFunCallExpression match_postfix_argument(CTokenStream stream, CScope scope, AstExpression child)
			throws Exception {
		int cursor = stream.get_cursor();

		AstPunctuator lparanth, rparanth;
		if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
			AstArgumentList arg_list = this.match_argument_expression_list(stream, scope);
			if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
				if (arg_list == null)
					return factory.new_funcall_expression(child, lparanth, rparanth);
				else
					return factory.new_funcall_expression(child, lparanth, arg_list, rparanth);
			}
		}

		stream.recover(cursor);
		return null;
	}

	/**
	 * <code>
	 * 	argument_expression_list |--> assignment_expression (, assignment_expression)*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstArgumentList match_argument_expression_list(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr;
		AstArgumentList arg_list;
		if ((expr = this.match_assignment_expression(stream, scope)) != null) {
			arg_list = factory.new_argument_list(expr);

			AstPunctuator comma;
			while ((comma = this.match_punctuator(stream, CPunctuator.comma)) != null) {
				if ((expr = this.match_assignment_expression(stream, scope)) != null) {
					arg_list.append_argument(comma, expr);
				} else
					throw new RuntimeException("Invalid argument-list at line " + this.current_line(stream)
							+ "\n\texpected as: , assignment-expression" + "\n\tat: " + this.get_text_from(stream));
			}

			return arg_list;
		}
		return null;
	}

	/**
	 * <code>
	 * 	post_expr |--> post_expr {. ->} field
	 * </code>
	 *
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	protected AstFieldExpression match_postfix_field(CTokenStream stream, AstExpression child) throws Exception {
		int cursor = stream.get_cursor();

		AstPunctuator operator;
		AstField field;
		if ((operator = this.match_punctuator(stream, CPunctuator.dot)) == null) {
			operator = this.match_punctuator(stream, CPunctuator.arrow);
		}

		AstFieldExpression expr = null;
		if (operator != null) {
			CToken token = stream.get_token();
			if (token instanceof CIdentifierToken) {
				field = factory.new_field(((CIdentifierToken) token).get_name());
				field.set_location(token.get_location());
				stream.consume();
				expr = factory.new_field_expression(child, operator, field);
			} else {
				throw new RuntimeException("Unexpected token: " + token + "\n\tExpected as identifier for Field");
			}
		}

		if (expr == null)
			stream.recover(cursor);
		return expr;

	}

	/**
	 * <code>post_expr |--> post_expr (++, --)</code>
	 *
	 * @param stream
	 * @param child
	 * @return
	 * @throws Exception
	 */
	protected AstIncrePostfixExpression match_postfix_increment(CTokenStream stream, AstExpression child)
			throws Exception {
		CToken head = stream.get_token();
		if (head instanceof CPunctuatorToken) {

			AstOperator operator = null;
			switch (((CPunctuatorToken) head).get_punctuator()) {
			case increment:
				operator = factory.new_operator(COperator.increment);
				operator.set_location(head.get_location());
				break;
			case decrement:
				operator = factory.new_operator(COperator.decrement);
				operator.set_location(head.get_location());
				break;
			default:
				operator = null;
				break;
			}

			if (operator != null) {
				stream.consume();
				return factory.new_increment_postfix_expression(child, operator);
			}
		}

		return null;
	}

	/**
	 * <code>post_expr |--> ( type_name ) initial_body</code>
	 *
	 * @param stream
	 * @param scope
	 * @param child
	 * @return
	 * @throws Exception
	 */
	protected AstCastExpression match_postfix_cast(CTokenStream stream, CScope scope) throws Exception {
		AstPunctuator lparanth, rparanth;
		AstTypeName typename;
		AstInitializerBody body;
		int cursor = stream.get_cursor();

		if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
			if ((typename = this.match_type_name(stream, scope)) != null) {
				if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
					if ((body = this.match_initializer_body(stream, scope)) != null)
						return factory.new_cast_expression(lparanth, typename, rparanth, body);
				}
			}
		}

		stream.recover(cursor);
		return null;
	}

	// unary expression
	/**
	 * <code>
	 * 	unary_expr |--> post_expr <br>
	 * 	unary_expr |--> (++, --) unary_expr <br>
	 * 	unary_expr |--> {+, -, ~, !, &, *} cast_expr <br>
	 * 	unary_expr |--> <b>sizeof</b> unary_expr | ( type_name ) <br>
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_unary_expression(CTokenStream stream, CScope scope) throws Exception {
		CToken head = stream.get_token();

		AstExpression expr;
		if (head instanceof CPunctuatorToken) {
			switch (((CPunctuatorToken) head).get_punctuator()) {
			case increment:
			case decrement:
				expr = this.match_unary_increment(stream, scope);
				break;
			case ari_add:
			case ari_sub:
			case ari_mul:
			case bit_and:
			case bit_not:
			case log_not:
				expr = this.match_unary_operators(stream, scope);
				break;
			default:
				expr = this.match_postfix_expression(stream, scope);
				break;
			}
		} else if (head instanceof CKeywordToken) {
			if (((CKeywordToken) head).get_keyword() == CKeyword.c89_sizeof)
				expr = this.match_postfix_sizeof(stream, scope);
			else
				expr = this.match_postfix_expression(stream, scope);
		} else
			expr = this.match_postfix_expression(stream, scope);
		return expr;
	}

	/**
	 * <code>
	 * 	unary_expr |--> {++, --} unary_expr
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstIncreUnaryExpression match_unary_increment(CTokenStream stream, CScope scope) throws Exception {
		CToken head = stream.get_token();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			switch (((CPunctuatorToken) head).get_punctuator()) {
			case increment:
				operator = factory.new_operator(COperator.increment);
				operator.set_location(head.get_location());
				break;
			case decrement:
				operator = factory.new_operator(COperator.decrement);
				operator.set_location(head.get_location());
				break;
			default:
				operator = null;
				break;
			}
		}

		if (operator != null) {
			AstExpression child;
			stream.consume();
			if ((child = this.match_unary_expression(stream, scope)) != null)
				return factory.new_increment_unary_expression(operator, child);
			else
				throw new RuntimeException("Invalid unary operator: " + operator.get_operator()
						+ "\n\t expected unary_expression following...");
		}
		return null;
	}

	/**
	 * <code>
	 * 	unary_expr |--> {+, -, ~, !, &, *} cast_expr <br>
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstExpression match_unary_operators(CTokenStream stream, CScope scope) throws Exception {
		CToken head = stream.get_token();
		int cursor = stream.get_cursor();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			switch (((CPunctuatorToken) head).get_punctuator()) {
			case ari_add:
				operator = factory.new_operator(COperator.positive);
				operator.set_location(head.get_location());
				break;
			case ari_sub:
				operator = factory.new_operator(COperator.negative);
				operator.set_location(head.get_location());
				break;
			case bit_and:
				operator = factory.new_operator(COperator.address_of);
				operator.set_location(head.get_location());
				break;
			case ari_mul:
				operator = factory.new_operator(COperator.dereference);
				operator.set_location(head.get_location());
				break;
			case bit_not:
				operator = factory.new_operator(COperator.bit_not);
				operator.set_location(head.get_location());
				break;
			case log_not:
				operator = factory.new_operator(COperator.logic_not);
				operator.set_location(head.get_location());
				break;
			default:
				operator = null;
				break;
			}
		}

		if (operator != null) {
			stream.consume();

			AstExpression child;
			if ((child = this.match_cast_expression(stream, scope)) != null) {
				switch (operator.get_operator()) {
				case positive:
				case negative:
					return factory.new_arith_unary_expression(operator, child);
				case address_of:
				case dereference:
					return factory.new_pointer_unary_expression(operator, child);
				case bit_not:
					return factory.new_bitwise_unary_expression(operator, child);
				default:
					return factory.new_logic_unary_expression(operator, child);
				}
			} else
				stream.recover(cursor);
		}
		return null;
	}

	/**
	 * <code>
	 * 	unary_expr |--> <b>sizeof</b> unary_expr | ( type_name ) <br>
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstSizeofExpression match_postfix_sizeof(CTokenStream stream, CScope scope) throws Exception {
		int cursor = stream.get_cursor();

		AstKeyword sizeof;
		if ((sizeof = this.match_keyword(stream, CKeyword.c89_sizeof)) != null) {
			AstExpression child;
			if ((child = this.match_unary_expression(stream, scope)) != null)
				return factory.new_sizeof_expression(sizeof, child);

			AstPunctuator lparanth, rparanth;
			AstTypeName typename;
			if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
				if ((typename = this.match_type_name(stream, scope)) != null) {
					if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
						return factory.new_sizeof_expression(sizeof, lparanth, typename, rparanth);
					}
				}
			}

			stream.recover(cursor);
			throw new RuntimeException("Invalid sizeof-expression: \n\tunary-expression | ( typename ) expected");
		}

		return null;
	}

	// cast-expression
	/**
	 * <code>
	 * 	cast_expr --> unary_expression | ( type_name ) cast_expression
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_cast_expression(CTokenStream stream, CScope scope) throws Exception {
		int cursor = stream.get_cursor();

		AstPunctuator lparanth, rparanth;
		AstTypeName typename;
		AstExpression child;
		if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
			if ((typename = this.match_type_name(stream, scope)) != null) {
				if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
					if ((child = this.match_cast_expression(stream, scope)) != null)
						return factory.new_cast_expression(lparanth, typename, rparanth, child);
					else {
						stream.recover(cursor);
						throw new RuntimeException(
								"Invalid cast-expression...\t\nexpected as cast-expression following \"(\" typename \")\""
										+ "\n\tat: " + this.get_text_from(stream));
					}
				}
			}
		}

		stream.recover(cursor);
		return this.match_unary_expression(stream, scope);
	}

	// multiplicative-expression
	/**
	 * <code>
	 * 	mul_expr |--> cast_expr ({* / %} cast_expr)*<br>
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_multiplicative_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;
		if ((next = this.match_cast_expression(stream, scope)) != null) {
			do {
				expr = next;
				next = this.match_multiplicative_tail(stream, scope, next);
			} while (next != null);
			return expr;
		}
		return null;
	}

	/**
	 * <code>|--> ({* / %} cast_expression)</code>
	 *
	 * @param stream
	 * @param scope
	 * @param child
	 * @return
	 * @throws Exception
	 */
	protected AstArithBinaryExpression match_multiplicative_tail(CTokenStream stream, CScope scope,
			AstExpression loperand) throws Exception {
		CToken head = stream.get_token();
		int cursor = stream.get_cursor();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			switch (((CPunctuatorToken) head).get_punctuator()) {
			case ari_mul:
				operator = factory.new_operator(COperator.arith_mul);
				operator.set_location(head.get_location());
				break;
			case ari_div:
				operator = factory.new_operator(COperator.arith_div);
				operator.set_location(head.get_location());
				break;
			case ari_mod:
				operator = factory.new_operator(COperator.arith_mod);
				operator.set_location(head.get_location());
				break;
			default:
				operator = null;
				break;
			}
		}

		if (operator != null) {
			stream.consume();
			AstExpression roperand;
			if ((roperand = this.match_cast_expression(stream, scope)) != null)
				return factory.new_arith_binary_expression(loperand, operator, roperand);
			else
				stream.recover(cursor);
		}
		return null;
	}

	// additive-expression
	/**
	 * <code>
	 * 	add_expr |--> mul_expr ( {+ -} mul_expr)*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_additive_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;
		if ((next = this.match_multiplicative_expression(stream, scope)) != null) {
			do {
				expr = next;
				next = this.match_additive_tail(stream, scope, next);
			} while (next != null);
			return expr;
		}
		return null;
	}

	/**
	 * <code>
	 * 	--> {+ -} mul_expr
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @param loperand
	 * @return
	 * @throws Exception
	 */
	protected AstArithBinaryExpression match_additive_tail(CTokenStream stream, CScope scope, AstExpression loperand)
			throws Exception {
		CToken head = stream.get_token();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			switch (((CPunctuatorToken) head).get_punctuator()) {
			case ari_add:
				operator = factory.new_operator(COperator.arith_add);
				operator.set_location(head.get_location());
				break;
			case ari_sub:
				operator = factory.new_operator(COperator.arith_sub);
				operator.set_location(head.get_location());
				break;
			default:
				operator = null;
				break;
			}
		}

		if (operator != null) {
			int cursor = stream.get_cursor();
			stream.consume();

			AstExpression roperand;
			if ((roperand = this.match_multiplicative_expression(stream, scope)) != null)
				return factory.new_arith_binary_expression(loperand, operator, roperand);
			else
				stream.recover(cursor);
		}
		return null;
	}

	// shift-expression
	/**
	 * <code>
	 * 	shift_expr |--> add_expr ({>> <<} add_expr)?
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_shift_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;
		if ((next = this.match_additive_expression(stream, scope)) != null) {
			do {
				expr = next;
				next = this.match_shift_tail(stream, scope, next);
			} while (next != null);
			return expr;
		}
		return null;
	}

	/**
	 * <code> --> {>> <<} add_expr</code>
	 *
	 * @param stream
	 * @param scope
	 * @param loperand
	 * @return
	 * @throws Exception
	 */
	protected AstShiftBinaryExpression match_shift_tail(CTokenStream stream, CScope scope, AstExpression loperand)
			throws Exception {
		CToken head = stream.get_token();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			switch (((CPunctuatorToken) head).get_punctuator()) {
			case left_shift:
				operator = factory.new_operator(COperator.left_shift);
				operator.set_location(head.get_location());
				break;
			case right_shift:
				operator = factory.new_operator(COperator.righ_shift);
				operator.set_location(head.get_location());
				break;
			default:
				operator = null;
				break;
			}
		}

		if (operator != null) {
			AstExpression roperand;
			stream.consume();
			if ((roperand = this.match_additive_expression(stream, scope)) != null)
				return factory.new_shift_binary_expression(loperand, operator, roperand);
			else
				throw new RuntimeException("Invalid shift-expression: expected as {<<, >>} additive-expression");
		}
		return null;
	}

	// relational-expression
	/**
	 * <code>
	 * 	rel_expr |--> shift_expr ({>, >=, <=, <} shift_expr)*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_relational_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;
		if ((next = this.match_shift_expression(stream, scope)) != null) {
			do {
				expr = next;
				next = this.match_relational_tail(stream, scope, next);
			} while (next != null);
			return expr;
		}
		return null;
	}

	/**
	 * <code> --> {>, >=, <=, <} shift_expr</code>
	 *
	 * @param stream
	 * @param scope
	 * @param loperand
	 * @return
	 * @throws Exception
	 */
	protected AstRelationExpression match_relational_tail(CTokenStream stream, CScope scope, AstExpression loperand)
			throws Exception {
		CToken head = stream.get_token();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			switch (((CPunctuatorToken) head).get_punctuator()) {
			case greater_tn:
				operator = factory.new_operator(COperator.greater_tn);
				operator.set_location(head.get_location());
				break;
			case greater_eq:
				operator = factory.new_operator(COperator.greater_eq);
				operator.set_location(head.get_location());
				break;
			case smaller_tn:
				operator = factory.new_operator(COperator.smaller_tn);
				operator.set_location(head.get_location());
				break;
			case smaller_eq:
				operator = factory.new_operator(COperator.smaller_eq);
				operator.set_location(head.get_location());
				break;
			default:
				operator = null;
				break;
			}
		}

		if (operator != null) {
			AstExpression roperand;
			stream.consume();
			if ((roperand = this.match_shift_expression(stream, scope)) != null)
				return factory.new_relation_expression(loperand, operator, roperand);
			else
				throw new RuntimeException(
						"Invalid relational-expression: expected as {<, <=, >=, >} shift_expression");
		}
		return null;
	}

	// equality-expression
	/**
	 * <code>
	 * 	eq_expr |--> rel_expr ({==, !=} rel_expr)*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_equality_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;
		if ((next = this.match_relational_expression(stream, scope)) != null) {
			do {
				expr = next;
				next = this.match_equality_tail(stream, scope, next);
			} while (next != null);
			return expr;
		}
		return null;
	}

	/**
	 * <code>--> {==, !=} rel_expr</code>
	 *
	 * @param stream
	 * @param scope
	 * @param loperand
	 * @return
	 * @throws Exception
	 */
	protected AstRelationExpression match_equality_tail(CTokenStream stream, CScope scope, AstExpression loperand)
			throws Exception {
		CToken head = stream.get_token();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			switch (((CPunctuatorToken) head).get_punctuator()) {
			case equal_with:
				operator = factory.new_operator(COperator.equal_with);
				operator.set_location(head.get_location());
				break;
			case not_equals:
				operator = factory.new_operator(COperator.not_equals);
				operator.set_location(head.get_location());
				break;
			default:
				operator = null;
				break;
			}
		}

		if (operator != null) {
			AstExpression roperand;
			stream.consume();
			if ((roperand = this.match_relational_expression(stream, scope)) != null)
				return factory.new_relation_expression(loperand, operator, roperand);
			else
				throw new RuntimeException("Invalid equality-expression, expected: {==, !=} relational-expression");
		}
		return null;
	}

	// and-expression
	/**
	 * <code>
	 * bit_and_expr |--> eq_expr (& eq_expr)*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_bitwise_and_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;
		if ((next = this.match_equality_expression(stream, scope)) != null) {
			do {
				expr = next;
				next = this.match_bitwise_and_tail(stream, scope, next);
			} while (next != null);
			return expr;
		}
		return null;
	}

	/**
	 * <code>
	 * 	--> & eq_expr
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @param loperand
	 * @return
	 * @throws Exception
	 */
	protected AstBitwiseBinaryExpression match_bitwise_and_tail(CTokenStream stream, CScope scope,
			AstExpression loperand) throws Exception {
		CToken head = stream.get_token();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			if (((CPunctuatorToken) head).get_punctuator() == CPunctuator.bit_and) {
				operator = factory.new_operator(COperator.bit_and);
				operator.set_location(head.get_location());
			}
		}

		if (operator != null) {
			AstExpression roperand;
			stream.consume();
			if ((roperand = this.match_equality_expression(stream, scope)) != null)
				return factory.new_bitwise_binary_expression(loperand, operator, roperand);
			else
				throw new RuntimeException("Invalid bitwise-and-expression, expected as:" + "& equality-expression");
		}
		return null;
	}

	// exclusive-or-expression
	/**
	 * <code>
	 * 	bit_xor_expr |--> bit_and_expr (^ bit_and_expr)*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_exclusive_or_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;
		if ((next = this.match_bitwise_and_expression(stream, scope)) != null) {
			do {
				expr = next;
				next = this.match_exclusive_or_tail(stream, scope, next);
			} while (next != null);
			return expr;
		}
		return null;
	}

	/**
	 * <code>
	 * 	--> ^ bit_and_expr
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @param loperand
	 * @return
	 * @throws Exception
	 */
	protected AstBitwiseBinaryExpression match_exclusive_or_tail(CTokenStream stream, CScope scope,
			AstExpression loperand) throws Exception {
		CToken head = stream.get_token();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			if (((CPunctuatorToken) head).get_punctuator() == CPunctuator.bit_xor) {
				operator = factory.new_operator(COperator.bit_xor);
				operator.set_location(head.get_location());
			}
		}

		if (operator != null) {
			AstExpression roperand;
			stream.consume();
			if ((roperand = this.match_bitwise_and_expression(stream, scope)) != null)
				return factory.new_bitwise_binary_expression(loperand, operator, roperand);
		}
		return null;
	}

	// inclusive-or-expression
	/**
	 * <code>
	 * bit_or_expr |--> xor_expr (| xor_expr)*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_bitwise_or_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;
		if ((next = this.match_exclusive_or_expression(stream, scope)) != null) {
			do {
				expr = next;
				next = this.match_bitwise_or_tail(stream, scope, next);
			} while (next != null);
			return expr;
		}
		return null;
	}

	/**
	 * <code> --> | xor_expr</code>
	 *
	 * @param stream
	 * @param scope
	 * @param loperand
	 * @return
	 * @throws Exception
	 */
	protected AstBitwiseBinaryExpression match_bitwise_or_tail(CTokenStream stream, CScope scope,
			AstExpression loperand) throws Exception {
		CToken head = stream.get_token();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			if (((CPunctuatorToken) head).get_punctuator() == CPunctuator.bit_or) {
				operator = factory.new_operator(COperator.bit_or);
				operator.set_location(head.get_location());
			}
		}

		if (operator != null) {
			AstExpression roperand;
			stream.consume();
			if ((roperand = this.match_exclusive_or_expression(stream, scope)) != null)
				return factory.new_bitwise_binary_expression(loperand, operator, roperand);
			else
				throw new RuntimeException("Invalid inclusive-or-expression at line " + this.current_line(stream)
						+ "\n\texpected: | exclusive-or-expression");
		}
		return null;
	}

	// logical-and-expression
	/**
	 * <code>
	 * 	log_and_expr |--> bit_or_expr (&& bit_or_expr)*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_logical_and_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;
		if ((next = this.match_bitwise_or_expression(stream, scope)) != null) {
			do {
				expr = next;
				next = this.match_logical_and_tail(stream, scope, next);
			} while (next != null);
			return expr;
		}
		return null;
	}

	/**
	 * <code> --> && bit_or_expr </code>
	 *
	 * @param stream
	 * @param scope
	 * @param loperand
	 * @return
	 * @throws Exception
	 */
	protected AstLogicBinaryExpression match_logical_and_tail(CTokenStream stream, CScope scope, AstExpression loperand)
			throws Exception {
		CToken head = stream.get_token();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			if (((CPunctuatorToken) head).get_punctuator() == CPunctuator.log_and) {
				operator = factory.new_operator(COperator.logic_and);
				operator.set_location(head.get_location());
			}
		}

		if (operator != null) {
			stream.consume();
			AstExpression roperand;
			if ((roperand = this.match_bitwise_or_expression(stream, scope)) != null)
				return factory.new_logic_binary_expression(loperand, operator, roperand);
			else
				throw new RuntimeException("Invalid logical-and-expression, at line " + this.current_line(stream)
						+ "\n\texpected: && inclusive-or-expression");
		}
		return null;
	}

	// logical-or-expression
	/**
	 * <code>
	 * 	log_or_expr |--> log_and_expr (|| log_and_expr)*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_logical_or_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;
		if ((next = this.match_logical_and_expression(stream, scope)) != null) {
			do {
				expr = next;
				next = this.match_logical_or_tail(stream, scope, next);
			} while (next != null);
			return expr;
		}
		return null;
	}

	/**
	 * <code> --> || log_and_expr </code>
	 *
	 * @param stream
	 * @param scope
	 * @param loperand
	 * @return
	 * @throws Exception
	 */
	protected AstLogicBinaryExpression match_logical_or_tail(CTokenStream stream, CScope scope, AstExpression loperand)
			throws Exception {
		CToken head = stream.get_token();

		AstOperator operator = null;
		if (head instanceof CPunctuatorToken) {
			if (((CPunctuatorToken) head).get_punctuator() == CPunctuator.log_or) {
				operator = factory.new_operator(COperator.logic_or);
				operator.set_location(head.get_location());
			}
		}

		if (operator != null) {
			stream.consume();
			AstExpression roperand;
			if ((roperand = this.match_logical_and_expression(stream, scope)) != null)
				return factory.new_logic_binary_expression(loperand, operator, roperand);
			else
				throw new RuntimeException("Invalid logical-or-expression, at line " + this.current_line(stream)
						+ "\n\texpected: || logical-and-expression");
		}
		return null;
	}

	// conditional-expression
	/**
	 * <code>
	 * cond_expr |--> log_or_expr (? expression : cond_expr)?
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_conditional_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression cond;
		if ((cond = this.match_logical_or_expression(stream, scope)) != null) {
			AstPunctuator question, colon;
			AstExpression tbranch, fbranch;
			if ((question = this.match_punctuator(stream, CPunctuator.question)) != null) {
				if ((tbranch = this.match_expression(stream, scope)) != null) {
					if ((colon = this.match_punctuator(stream, CPunctuator.colon)) != null) {
						if ((fbranch = this.match_conditional_expression(stream, scope)) != null) {
							return factory.new_conditional_expression(cond, question, tbranch, colon, fbranch);
						}
					}
				}

				throw new RuntimeException("Invalid conditional-expression, at line " + this.current_line(stream)
						+ "\n\t ? expression : condition-expression");
			}
		}
		return cond;
	}

	// assignment-expression
	/**
	 * <code>
	 * 	assign_expr |--> conditional_expr <br>
	 * 	assign_expr |--> unary_expr assignment_operator assignment_expression
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_assignment_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr;
		int cursor = stream.get_cursor();

		if ((expr = this.match_unary_expression(stream, scope)) != null) {
			CToken head = stream.get_token();
			if (head instanceof CPunctuatorToken) { /* tail match */

				// determine assignment-operator
				COperator operator;
				switch (((CPunctuatorToken) head).get_punctuator()) {
				case assign:
					operator = COperator.assign;
					break;
				case ari_add_assign:
					operator = COperator.arith_add_assign;
					break;
				case ari_sub_assign:
					operator = COperator.arith_sub_assign;
					break;
				case ari_mul_assign:
					operator = COperator.arith_mul_assign;
					break;
				case ari_div_assign:
					operator = COperator.arith_div_assign;
					break;
				case ari_mod_assign:
					operator = COperator.arith_mod_assign;
					break;
				case left_shift_assign:
					operator = COperator.left_shift_assign;
					break;
				case right_shift_assign:
					operator = COperator.righ_shift_assign;
					break;
				case bit_and_assign:
					operator = COperator.bit_and_assign;
					break;
				case bit_or_assign:
					operator = COperator.bit_or_assign;
					break;
				case bit_xor_assign:
					operator = COperator.bit_xor_assign;
					break;
				default:
					operator = null;
					break;
				}

				// match assignment-operator assignment-expression
				if (operator != null) {
					AstOperator op = factory.new_operator(operator);
					op.set_location(head.get_location());

					stream.consume();
					AstExpression roperand;
					if ((roperand = this.match_assignment_expression(stream, scope)) != null) {
						switch (operator) {
						case arith_add_assign:
						case arith_sub_assign:
						case arith_mul_assign:
						case arith_div_assign:
						case arith_mod_assign:
							return factory.new_arith_assign_expression(expr, op, roperand);
						case left_shift_assign:
						case righ_shift_assign:
							return factory.new_shift_assign_expression(expr, op, roperand);
						case bit_and_assign:
						case bit_or_assign:
						case bit_xor_assign:
							return factory.new_bitwise_assign_expression(expr, op, roperand);
						default:
							return factory.new_assign_expression(expr, op, roperand);
						}
					} else
						throw new RuntimeException("Invalid assignment-expression, at line " + this.current_line(stream)
								+ "\n\texpected: assignment-operator assignment-expression" + "\n\tat: "
								+ this.get_text_from(stream));
				} else {
					stream.recover(cursor);
					expr = this.match_conditional_expression(stream, scope);
				}
			}
		} else
			expr = this.match_conditional_expression(stream, scope);

		return expr;
	}

	// expression
	/**
	 * <code>
	 * 	expr --> assignment_expr (, assignment_expr)*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExpression match_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expr, next;
		if ((next = this.match_assignment_expression(stream, scope)) != null) {
			do {
				expr = next;
				next = this.match_expression_tail(stream, scope, next);
			} while (next != null);
			return expr;
		}
		return null;
	}

	/**
	 * <code>, assign_expr</code>
	 *
	 * @param stream
	 * @param scope
	 * @param loperand
	 * @return
	 * @throws Exception
	 */
	protected AstCommaExpression match_expression_tail(CTokenStream stream, CScope scope, AstExpression loperand)
			throws Exception {
		AstPunctuator comma;
		if ((comma = this.match_punctuator(stream, CPunctuator.comma)) != null) {
			AstExpression roperand;
			if ((roperand = this.match_assignment_expression(stream, scope)) != null) {
				if (!(loperand instanceof AstCommaExpression)) {
					loperand = factory.new_comma_expression(loperand);
				}
				((AstCommaExpression) loperand).append(comma, roperand);
				return (AstCommaExpression) loperand;
			} else
				throw new RuntimeException("Invalid expression at line " + this.current_line(stream)
						+ "\n\texpected as: , assignment-expression");
		}
		return null;
	}

	// const-expression
	/**
	 * <code>const_expr |--> cond_expr</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstConstExpression match_const_expression(CTokenStream stream, CScope scope) throws Exception {
		AstExpression child;
		child = this.match_conditional_expression(stream, scope);
		if (child != null)
			return factory.new_const_expression(child);
		else
			return null;
	}

	// declaration specifier
	/**
	 * <code>decl_spec |--> storage_class_spec | type_qualifier | function_spec | type_keyword | typedef_name | struct_spec | union_spec | enum_spec </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstSpecifier match_declaration_specifier(CTokenStream stream, CScope scope) throws Exception {
		CToken head = stream.get_token();

		if (head instanceof CKeywordToken) {
			AstKeyword keyword = factory.new_keyword(((CKeywordToken) head).get_keyword());
			keyword.set_location(head.get_location());
			switch (((CKeywordToken) head).get_keyword()) {
			case c89_typedef:
			case c89_static:
			case c89_extern:
			case c89_auto:
			case c89_register:
				stream.consume();
				return factory.new_storage_class(keyword);
			case c89_const:
			case c89_volatile:
			case c99_restrict:
				stream.consume();
				return factory.new_type_qualifier(keyword);
			case c99_inline:
				stream.consume();
				return factory.new_function_qualifier(keyword);
			case c89_void:
			case c89_char:
			case c89_short:
			case c89_int:
			case c89_long:
			case c89_float:
			case c89_double:
			case c89_signed:
			case c89_unsigned:
			case c99_bool:
			case c99_complex:
			case c99_imaginary:
			case gnu_builtin_va_list:
				stream.consume();
				return factory.new_type_keyword(keyword);
			case c89_struct:
				return match_struct_specifier(stream, scope);
			case c89_union:
				return match_union_specifier(stream, scope);
			case c89_enum:
				return match_enum_specifier(stream, scope);
			default:
				return null;
			}
		} else if (head instanceof CIdentifierToken) {
			String idname = ((CIdentifierToken) head).get_name();
			if (scope.has_name(idname)) {
				CName cname = scope.get_name(idname);
				if (cname instanceof CTypedefName) {
					AstTypedefName typedefname = factory.new_typedef_name(idname);
					typedefname.set_cname(cname);
					typedefname.set_location(head.get_location());
					stream.consume();
					return typedefname;
				} else
					return null;
			} else
				return null;
		} else
			return null;
	}

	// struct specifier
	/**
	 * <code>struct_spec |--> <b>struct</b> name | (name)? body</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstStructSpecifier match_struct_specifier(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword struct;
		if ((struct = this.match_keyword(stream,
				CKeyword.c89_struct)) != null) { /* match "struct" */
			/* match name */
			AstName struct_name;
			CToken head = stream.get_token();
			if (head instanceof CIdentifierToken) {
				String idname = ((CIdentifierToken) head).get_name();
				if (scope.has_name(idname)) {
					CName cname = scope.get_name(idname);
					if (cname instanceof CStructTypeName) {
						stream.consume();
						struct_name = factory.new_name(idname);
						struct_name.set_cname(cname);
						struct_name.set_location(head.get_location());
					} else
						throw new RuntimeException("Duplicated definition at line " + this.current_line(stream)
								+ "\t\n\"" + idname + "\"");
				} else {
					struct_name = factory.new_name(idname);
					stream.consume();
					CName cname = scope.get_name_table().new_struct_name(struct_name);
					struct_name.set_cname(cname);
					struct_name.set_location(head.get_location());
				}
			} else
				struct_name = null;

			/* match struct-union-body */
			AstStructUnionBody body;
			AstPunctuator lbrace, rbrace;
			AstStructDeclarationList dlist;
			if ((lbrace = this.match_punctuator(stream, CPunctuator.left_brace)) != null) {
				scope = scope.new_child(); /* to the struct body */

				if ((rbrace = this.match_punctuator(stream, CPunctuator.right_brace)) != null) {
					body = factory.new_struct_union_body(lbrace, rbrace);
					scope.set_origin(body);
					body.set_scope(scope);
				} else if ((dlist = this.match_struct_declaration_list(stream, scope)) != null) {
					if ((rbrace = this.match_punctuator(stream, CPunctuator.right_brace)) != null) {
						body = factory.new_struct_union_body(lbrace, dlist, rbrace);
						scope.set_origin(body);
						body.set_scope(scope);
					} else
						throw new RuntimeException("Invalid struct-union-body at line " + this.current_line(stream)
								+ "\n\texpected ending with \"}\"");
				} else
					throw new RuntimeException("Invalid struct-union-body at line " + this.current_line(stream)
							+ "\n\texpected ending with \"}\"");
			} else
				body = null;

			/* return struct-specifier */
			if (struct_name != null && body != null) {
				if (struct_name.get_cname().get_source() != struct_name) {
					AstName ref_name = (AstName) struct_name.get_cname().get_source();
					AstStructSpecifier spec = (AstStructSpecifier) ref_name.get_parent();
					if (spec.has_body())
						throw new RuntimeException("Invalid definition at line " + this.current_line(stream)
								+ "\n\tduplicated definition for struct " + struct_name.get_name());
					else
						struct_name.get_cname().set_source(struct_name);
				}
				return factory.new_struct_specifier(struct, struct_name, body);
			}

			else if (struct_name != null)
				return factory.new_struct_specifier(struct, struct_name);
			else if (body != null)
				return factory.new_struct_specifier(struct, body);
			else
				throw new RuntimeException("Invalid struct-specifier at line " + this.current_line(stream)
						+ "\n\texpected identifier or \"{\"");
		} else
			return null;
	}

	// union specifier
	/**
	 * <code>union_spec |--> <b>union</b> name | (name)? body</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstUnionSpecifier match_union_specifier(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword union;
		if ((union = this.match_keyword(stream,
				CKeyword.c89_union)) != null) { /* match "union" */
			/* match name */
			AstName union_name;
			CToken head = stream.get_token();
			if (head instanceof CIdentifierToken) {
				String idname = ((CIdentifierToken) head).get_name();
				if (scope.has_name(idname)) {
					CName cname = scope.get_name(idname);
					if (cname instanceof CUnionTypeName) {
						stream.consume();
						union_name = factory.new_name(idname);
						union_name.set_cname(cname);
						union_name.set_location(head.get_location());
					} else
						throw new RuntimeException("Duplicated definition at line " + this.current_line(stream)
								+ "\t\n\"" + idname + "\"");
				} else {
					union_name = factory.new_name(idname);
					stream.consume();
					CName cname = scope.get_name_table().new_union_name(union_name);
					union_name.set_cname(cname);
					union_name.set_location(head.get_location());
				}
			} else
				union_name = null;

			/* match struct-union-body */
			AstStructUnionBody body;
			AstPunctuator lbrace, rbrace;
			AstStructDeclarationList dlist;
			if ((lbrace = this.match_punctuator(stream, CPunctuator.left_brace)) != null) {
				scope = scope.new_child(); /* to the union body */

				if ((rbrace = this.match_punctuator(stream, CPunctuator.right_brace)) != null) {
					body = factory.new_struct_union_body(lbrace, rbrace);
					scope.set_origin(body);
					body.set_scope(scope);
				} else if ((dlist = this.match_struct_declaration_list(stream, scope)) != null) {
					if ((rbrace = this.match_punctuator(stream, CPunctuator.right_brace)) != null) {
						body = factory.new_struct_union_body(lbrace, dlist, rbrace);
						scope.set_origin(body);
						body.set_scope(scope);
					} else
						throw new RuntimeException("Invalid struct-union-body at line " + this.current_line(stream)
								+ "\n\texpected ending with \"}\"");
				} else
					throw new RuntimeException("Invalid struct-union-body at line " + this.current_line(stream)
							+ "\n\texpected ending with \"}\"");
			} else
				body = null;

			/* return union-specifier */
			if (union_name != null && body != null) {
				if (union_name.get_cname().get_source() != union_name) {
					AstName ref_name = (AstName) union_name.get_cname().get_source();
					AstUnionSpecifier spec = (AstUnionSpecifier) ref_name.get_parent();
					if (spec.has_body())
						throw new RuntimeException("Invalid definition at line " + this.current_line(stream)
								+ "\n\tduplicated definition for union " + union_name.get_name());
					else
						union_name.get_cname().set_source(union_name);
				}
				return factory.new_union_specifier(union, union_name, body);
			} else if (union_name != null)
				return factory.new_union_specifier(union, union_name);
			else if (body != null)
				return factory.new_union_specifier(union, body);
			else
				throw new RuntimeException("Invalid union-specifier at line " + this.current_line(stream)
						+ "\n\texpected identifier or \"{\"");
		} else
			return null;
	}

	// enum specifier
	/**
	 * <code>enum_spec |--> <b>enum</b> name | (name)? { enumerator_list (,)? }</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstEnumSpecifier match_enum_specifier(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword enum_keyword;
		if ((enum_keyword = this.match_keyword(stream,
				CKeyword.c89_enum)) != null) { /* match "enum" */

			/* match name */
			CToken head = stream.get_token();
			AstName ename;
			if (head instanceof CIdentifierToken) {
				String idname = ((CIdentifierToken) head).get_name();
				if (scope.has_name(idname)) {
					CName cname = scope.get_name(idname);
					if (cname instanceof CEnumTypeName) {
						ename = factory.new_name(idname);
						ename.set_cname(cname);
						stream.consume();
						ename.set_location(head.get_location());
					} else
						throw new RuntimeException("Invalid definition at line " + this.current_line(stream)
								+ "\n\tduplicated declaration of \"" + idname + "\"");
				} else {
					stream.consume();
					ename = factory.new_name(idname);
					CName cname = scope.get_name_table().new_enum_name(ename);
					ename.set_cname(cname);
					ename.set_location(head.get_location());
				}
			} else
				ename = null;

			/* match enumerator-body */
			AstPunctuator lbrace, rbrace, comma;
			AstEnumeratorBody body;
			if ((lbrace = this.match_punctuator(stream, CPunctuator.left_brace)) != null) {
				scope = scope.new_child();

				AstEnumeratorList elist;
				if ((elist = this.match_enumerator_list(stream, scope)) != null) {
					comma = this.match_punctuator(stream, CPunctuator.comma);
					if ((rbrace = this.match_punctuator(stream, CPunctuator.right_brace)) != null) {
						if (comma == null)
							body = factory.new_enumerator_body(lbrace, elist, rbrace);
						else
							body = factory.new_enumerator_body(lbrace, elist, comma, rbrace);
					} else
						throw new RuntimeException(
								"Invalid enumerator-body at line " + this.current_line(stream) + "\n\texpected \"}\"");
				} else
					throw new RuntimeException(
							"Invalid enumerator-body at line " + this.current_line(stream) + "\n\texpected enumerator");

				scope.set_origin(body);
				body.set_scope(scope);
			} else
				body = null;

			/* return enum-specifier */
			if (ename != null && body != null) {
				if (ename.get_cname().get_source() != ename) {
					AstName ref_name = (AstName) ename.get_cname().get_source();
					AstEnumSpecifier spec = (AstEnumSpecifier) ref_name.get_parent();
					if (spec.has_body())
						throw new RuntimeException("Invalid definition at line " + this.current_line(stream)
								+ "\n\tduplicated definition for \"" + ename.get_name() + "\"");
					else
						ename.get_cname().set_source(ename);
				}
				return factory.new_enum_specifier(enum_keyword, ename, body);
			} else if (ename != null)
				return factory.new_enum_specifier(enum_keyword, ename);
			else if (body != null)
				return factory.new_enum_specifier(enum_keyword, body);
			else
				throw new RuntimeException("Invalid enum-specifier at line " + this.current_line(stream)
						+ "\n\texpected as identifier or \"{\"");
		} else
			return null;
	}

	// struct-declaration-list
	/**
	 * <code>struct_decl_list |--> (struct_decl)+</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstStructDeclarationList match_struct_declaration_list(CTokenStream stream, CScope scope)
			throws Exception {
		AstStructDeclaration decl;
		if ((decl = this.match_struct_declaration(stream, scope)) != null) {
			AstStructDeclarationList dlist = factory.new_struct_declaration_list(decl);
			while ((decl = this.match_struct_declaration(stream, scope)) != null)
				dlist.append_declaration(decl);
			return dlist;
		} else
			return null;
	}

	// enumerator-list
	/**
	 * <code>enumerator_list |--> enumerator (, enumerator)*</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstEnumeratorList match_enumerator_list(CTokenStream stream, CScope scope) throws Exception {
		AstEnumerator enumerator;
		if ((enumerator = this.match_enumerator(stream, scope)) != null) {
			AstEnumeratorList elist = factory.new_enumerator_list(enumerator);

			AstPunctuator comma;
			int cursor;
			do {
				cursor = stream.get_cursor();
				if ((comma = this.match_punctuator(stream, CPunctuator.comma)) != null) {
					if ((enumerator = this.match_enumerator(stream, scope)) != null)
						elist.append_enumerator(comma, enumerator);
					else
						stream.recover(cursor);
				}
			} while (comma != null && enumerator != null);

			return elist;
		} else
			return null;
	}

	/**
	 * <code>enumerator |--> identifier (= const_expr)?</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstEnumerator match_enumerator(CTokenStream stream, CScope scope) throws Exception {
		CToken head = stream.get_token();
		AstName name;
		if (head instanceof CIdentifierToken) {
			String idname = ((CIdentifierToken) head).get_name();
			if (scope.has_name(idname))
				throw new RuntimeException("Invalid enumerator at line " + this.current_line(stream)
						+ "\n\tduplicated definition for \"" + idname + "\"");
			else {
				name = factory.new_name(idname);
				stream.consume();
				name.set_location(head.get_location());
			}
		} else
			name = null;

		AstEnumerator enumerator;
		if (name != null) {
			AstPunctuator assign;
			AstConstExpression expr;
			if ((assign = this.match_punctuator(stream, CPunctuator.assign)) != null) {
				if ((expr = this.match_const_expression(stream, scope)) != null)
					enumerator = factory.new_enumerator(name, assign, expr);
				else
					throw new RuntimeException("Invalid enumerator at line " + this.current_line(stream) + "\n\t");
			} else
				enumerator = factory.new_enumerator(name);
		} else
			enumerator = null;

		if (enumerator != null) {
			CScope file_scope = this.get_file_scope(scope);
			CName cname = file_scope.get_name_table().new_enumerator_name(enumerator);
			name.set_cname(cname);
		}
		return enumerator;
	}

	/**
	 * <code> --> type_qualifier | type_specifier </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstSpecifier match_specifier_qualifier(CTokenStream stream, CScope scope) throws Exception {
		CToken head = stream.get_token();

		if (head instanceof CKeywordToken) {
			AstKeyword keyword = factory.new_keyword(((CKeywordToken) head).get_keyword());
			keyword.set_location(head.get_location());
			switch (((CKeywordToken) head).get_keyword()) {
			case c89_const:
			case c89_volatile:
			case c99_restrict:
				stream.consume();
				return factory.new_type_qualifier(keyword);
			case c89_void:
			case c89_char:
			case c89_short:
			case c89_int:
			case c89_long:
			case c89_float:
			case c89_double:
			case c89_signed:
			case c89_unsigned:
			case c99_bool:
			case c99_complex:
			case c99_imaginary:
				stream.consume();
				return factory.new_type_keyword(keyword);
			case c89_struct:
				return match_struct_specifier(stream, scope);
			case c89_union:
				return match_union_specifier(stream, scope);
			case c89_enum:
				return match_enum_specifier(stream, scope);
			default:
				return null;
			}
		} else if (head instanceof CIdentifierToken) {
			String idname = ((CIdentifierToken) head).get_name();
			if (scope.has_name(idname)) {
				CName cname = scope.get_name(idname);
				if (cname instanceof CTypedefName) {
					AstTypedefName typedefname = factory.new_typedef_name(idname);
					typedefname.set_cname(cname);
					typedefname.set_location(head.get_location());
					stream.consume();
					return typedefname;
				} else
					return null;
			} else
				return null;
		} else
			return null;
	}

	/**
	 * <code>--> {*, const, volatile, restrict}</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstNode match_pointer_specifier(CTokenStream stream, CScope scope) throws Exception {
		CToken head = stream.get_token();
		if (head instanceof CKeywordToken) {
			switch (((CKeywordToken) head).get_keyword()) {
			case c89_const:
			case c89_volatile:
			case c99_restrict:
				AstKeyword keyword = factory.new_keyword(((CKeywordToken) head).get_keyword());
				keyword.set_location(head.get_location());
				stream.consume();
				return keyword;
			default:
				return null;
			}
		} else if (head instanceof CPunctuatorToken) {
			if (((CPunctuatorToken) head).get_punctuator() == CPunctuator.ari_mul) {
				AstPunctuator punc = factory.new_punctuator(CPunctuator.ari_mul);
				punc.set_location(head.get_location());
				stream.consume();
				return punc;
			} else
				return null;
		} else
			return null;
	}

	/**
	 * <code>|--> static | const | volatile | restrict</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstKeyword match_array_qualifier_specifier(CTokenStream stream, CScope scope) throws Exception {
		CToken head = stream.get_token();
		AstKeyword keyword;
		if (head instanceof CKeywordToken) {
			switch (((CKeywordToken) head).get_keyword()) {
			case c89_static:
			case c89_const:
			case c89_volatile:
			case c99_restrict:
				keyword = factory.new_keyword(((CKeywordToken) head).get_keyword());
				keyword.set_location(head.get_location());
				stream.consume();
				return keyword;
			default:
				return null;
			}
		} else
			return null;
	}

	// declaration-specifiers
	/**
	 * <code>decl_spec |--> (storage_class_spec | type_qualifier | function_qualifier | type_keyword | typedef_name | struct_spec | union_spec | enum_spec)+</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstDeclarationSpecifiers match_declaration_specifiers(CTokenStream stream, CScope scope) throws Exception {
		AstSpecifier specifier;
		if ((specifier = this.match_declaration_specifier(stream, scope)) != null) {
			AstDeclarationSpecifiers specifiers = factory.new_declaration_specifiers(specifier);
			while ((specifier = this.match_declaration_specifier(stream, scope)) != null)
				specifiers.append_specifier(specifier);
			return specifiers;
		} else
			return null;
	}

	// specifier-qualifier-list
	/**
	 * <code>specifier_qualifier_list |--> (type_qualifier | type_keyword | typedef_name | struct_spec | union_spec | enum_spec)+</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstSpecifierQualifierList match_specifier_qualifier_list(CTokenStream stream, CScope scope)
			throws Exception {
		AstSpecifier specifier;
		if ((specifier = this.match_specifier_qualifier(stream, scope)) != null) {
			AstSpecifierQualifierList list = factory.new_specifier_qualifier_list(specifier);
			while ((specifier = this.match_specifier_qualifier(stream, scope)) != null)
				list.append_specifier(specifier);
			return list;
		} else
			return null;
	}

	/**
	 * <code>|--> {static | const | volatile | restrict}+</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstArrayQualifierList match_array_qualifier_list(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword keyword;
		if ((keyword = this.match_array_qualifier_specifier(stream, scope)) != null) {
			AstArrayQualifierList qlist = factory.new_array_qualifier_list(keyword);
			while ((keyword = this.match_array_qualifier_specifier(stream, scope)) != null)
				qlist.append_keyword(keyword);
			return qlist;
		} else
			return null;
	}

	// pointer
	/**
	 * <code>pointer |--> {*, const, volatile, restrict}+</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstPointer match_pointer(CTokenStream stream, CScope scope) throws Exception {
		AstNode child;
		if ((child = this.match_pointer_specifier(stream, scope)) != null) {
			AstPointer pointer = null;
			do {
				if (pointer == null) {
					if (child instanceof AstKeyword)
						pointer = factory.new_pointer((AstKeyword) child);
					else
						pointer = factory.new_pointer((AstPunctuator) child);
				} else {
					if (child instanceof AstKeyword)
						pointer.append_keyword((AstKeyword) child);
					else
						pointer.append_punctuator((AstPunctuator) child);
				}
				child = this.match_pointer_specifier(stream, scope);
			} while (child != null);
			return pointer;
		} else
			return null;
	}

	// declarator
	/**
	 * <code>declarator |--> (pointer)? direct-declarator</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstDeclarator match_declarator(CTokenStream stream, CScope scope) throws Exception {
		int cursor = stream.get_cursor();

		AstPointer pointer;
		AstDeclarator declarator;
		if ((pointer = this.match_pointer(stream, scope)) != null) {
			if ((declarator = this.match_direct_declarator(stream, scope)) != null)
				return factory.new_declarator(pointer, declarator);
			else {
				stream.recover(cursor);
				return null;
			}
		} else if ((declarator = this.match_direct_declarator(stream, scope)) != null)
			return declarator;
		else
			return null;
	}

	// direct declarator
	/**
	 * <code>
	 * 	direct-declarator |--> identifier | ( declarator ) <br>
	 * 	direct-declarator |--> direct-declarator { [(array_qualifier_list)? (assign_expression)?] | ( (param_type_list | identifier_list)? ) }*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstDeclarator match_direct_declarator(CTokenStream stream, CScope scope) throws Exception {
		AstDeclarator declarator, next;
		if ((next = this.match_direct_declarator_head(stream, scope)) != null) {
			do {
				declarator = next;

				CToken head = stream.get_token();
				if (head instanceof CPunctuatorToken) {
					switch (((CPunctuatorToken) head).get_punctuator()) {
					case left_paranth:
						next = this.match_direct_declarator_parameters(stream, scope, next);
						break;
					case left_bracket:
						next = this.match_direct_declarator_dimension(stream, scope, next);
						break;
					default:
						next = null;
						break;
					}
				} else
					next = null;
			} while (next != null);

			return declarator;
		} else
			return null;
	}

	/**
	 * <code> --> identifier | ( declarator ) </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstDeclarator match_direct_declarator_head(CTokenStream stream, CScope scope) throws Exception {
		int cursor = stream.get_cursor();
		AstName name;
		AstPunctuator lparanth;
		if ((name = this.match_identifier(stream, scope)) != null)
			return factory.new_declarator(name);
		else if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
			AstDeclarator declarator;
			AstPunctuator rparanth;
			if ((declarator = this.match_declarator(stream, scope)) != null) {
				if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null)
					return factory.new_declarator(lparanth, declarator, rparanth);
				else {
					throw new RuntimeException("Invalid paranth-match at line " + this.current_line(stream)
							+ "\n\t expected ending with \")\"");
				}
			} else {
				stream.recover(cursor);
				return null;
			}
		} else
			return null;
	}

	/**
	 * <code>dir_declarator |--> dir_declarator ( param_type_list? | id_list? )</code>
	 *
	 * @param stream
	 * @param scope
	 * @param declarator
	 * @return
	 * @throws Exception
	 */
	protected AstDeclarator match_direct_declarator_parameters(CTokenStream stream, CScope scope,
			AstDeclarator declarator) throws Exception {
		AstPunctuator lparanth, rparanth;
		if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
			AstIdentifierList idlist;
			AstParameterTypeList plist;

			scope = scope.new_child(); /* down to parameter body scope */

			AstParameterBody body;
			if ((idlist = this.match_identifier_list(stream, scope)) != null) {
				if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null)
					body = factory.new_parameter_body(lparanth, idlist, rparanth);
				else
					throw new RuntimeException("Invalid paranth-match at line " + this.current_line(stream)
							+ "\n\texpected ending with \")\"");
			} else if ((plist = this.match_parameter_type_list(stream, scope)) != null) {
				if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null)
					body = factory.new_parameter_body(lparanth, plist, rparanth);
				else
					throw new RuntimeException("Invalid paranth-match at line " + this.current_line(stream)
							+ "\n\texpected ending with \")\"");
			} else if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null)
				body = factory.new_parameter_body(lparanth, rparanth);
			else
				throw new RuntimeException("Invalid paranth-match at line " + this.current_line(stream)
						+ "\n\texpected ending with \")\"");

			scope.set_origin(body);
			body.set_scope(scope); /* refers to parameter body source */

			return factory.new_declarator(declarator, body);
		} else
			return null;
	}

	/**
	 * <code>dir_declarator |--> dir_declarator [(array_qualifier_list)? (assign_expr)?]</code>
	 *
	 * @param stream
	 * @param scope
	 * @param declarator
	 * @return
	 * @throws Exception
	 */
	protected AstDeclarator match_direct_declarator_dimension(CTokenStream stream, CScope scope,
			AstDeclarator declarator) throws Exception {
		AstPunctuator lbracket, rbracket;
		if ((lbracket = this.match_punctuator(stream, CPunctuator.left_bracket)) != null) {
			AstArrayQualifierList specifiers;
			AstExpression expression;
			specifiers = this.match_array_qualifier_list(stream, scope);
			expression = this.match_assignment_expression(stream, scope);
			if (expression != null)
				expression = factory.new_const_expression(expression);

			if ((rbracket = this.match_punctuator(stream, CPunctuator.right_bracket)) != null) {
				AstDimension dimension;
				if (specifiers != null && expression != null) {
					dimension = factory.new_dimension(lbracket, specifiers, (AstConstExpression) expression, rbracket);
				} else if (specifiers != null) {
					dimension = factory.new_dimension(lbracket, specifiers, rbracket);
				} else if (expression != null) {
					dimension = factory.new_dimension(lbracket, (AstConstExpression) expression, rbracket);
				} else
					dimension = factory.new_dimension(lbracket, rbracket);
				return factory.new_declarator(declarator, dimension);
			} else
				throw new RuntimeException(
						"Invalid bracket-match at line " + this.current_line(stream) + "\n\texpected with \"]\"");
		} else
			return null;
	}

	/**
	 * <code>
	 * 	abs_declarator |--> pointer | (pointer)? direct_abs_declarator
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstAbsDeclarator match_abs_declarator(CTokenStream stream, CScope scope) throws Exception {
		AstPointer pointer;
		AstAbsDeclarator declarator;
		if ((pointer = this.match_pointer(stream, scope)) != null) {
			if ((declarator = this.match_direct_abs_declarator(stream, scope)) != null)
				return factory.new_abs_declarator(pointer, declarator);
			else
				return factory.new_abs_declarator(pointer, null);
		} else if ((declarator = this.match_direct_abs_declarator(stream, scope)) != null) {
			return declarator;
		} else
			return null;
	}

	/**
	 * <code>
	 * 	direct_abs_declarator |--> { ( abs_declarator ) }? { [ assignment-expression? ] | ( parameter-type-list? ) }*
	 * </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstAbsDeclarator match_direct_abs_declarator(CTokenStream stream, CScope scope) throws Exception {
		AstAbsDeclarator declarator, next;
		next = this.match_direct_abs_declarator_head(stream, scope);

		do {
			declarator = next;

			CToken head = stream.get_token();
			if (head instanceof CPunctuatorToken) {
				switch (((CPunctuatorToken) head).get_punctuator()) {
				case left_paranth:
					next = this.match_direct_abs_declarator_parameters(stream, scope, next);
					break;
				case left_bracket:
					next = this.match_direct_abs_declarator_dimension(stream, scope, next);
					break;
				default:
					next = null;
					break;
				}
			} else
				next = null;
		} while (next != null);

		return declarator;
	}

	/**
	 * <code> |--> ( abs_declarator ) </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstAbsDeclarator match_direct_abs_declarator_head(CTokenStream stream, CScope scope) throws Exception {
		int cursor = stream.get_cursor();

		AstPunctuator lparanth, rparanth;
		if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
			AstAbsDeclarator declarator;
			if ((declarator = this.match_abs_declarator(stream, scope)) != null) {
				if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
					return factory.new_abs_declarator(lparanth, declarator, rparanth);
				} else
					throw new RuntimeException("Invalid paranth-match at line " + this.current_line(stream));
			} else {
				stream.recover(cursor);
				return null;
			}
		} else
			return null;
	}

	/**
	 * <code> --> abs_declarator ( parameter-type-list? )</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstAbsDeclarator match_direct_abs_declarator_parameters(CTokenStream stream, CScope scope,
			AstAbsDeclarator declarator) throws Exception {
		AstPunctuator lparanth, rparanth;
		if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
			scope = scope.new_child(); /* down to the parameter scope */

			AstParameterTypeList plist;
			AstParameterBody body;
			if ((plist = this.match_parameter_type_list(stream, scope)) != null) {
				if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null)
					body = factory.new_parameter_body(lparanth, plist, rparanth);
				else
					throw new RuntimeException("Invalid paranth match at line " + this.current_line(stream));
			} else if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null)
				body = factory.new_parameter_body(lparanth, rparanth);
			else
				throw new RuntimeException("Invalid paranth match at line " + this.current_line(stream));

			scope.set_origin(body);
			body.set_scope(scope); /* set the scope source to parameter body */

			return factory.new_abs_declarator(declarator, body);
		} else
			return null;
	}

	/**
	 * <code> --> abs_declarator [ assignment-expression? ]</code>
	 *
	 * @param stream
	 * @param scope
	 * @param declarator
	 * @return
	 * @throws Exception
	 */
	protected AstAbsDeclarator match_direct_abs_declarator_dimension(CTokenStream stream, CScope scope,
			AstAbsDeclarator declarator) throws Exception {
		AstPunctuator lbracket, rbracket;
		if ((lbracket = this.match_punctuator(stream, CPunctuator.left_bracket)) != null) {
			AstExpression expression = this.match_assignment_expression(stream, scope);
			if (expression != null)
				expression = factory.new_const_expression(expression);

			AstDimension dimension;
			if ((rbracket = this.match_punctuator(stream, CPunctuator.right_bracket)) != null) {
				if (expression == null)
					dimension = factory.new_dimension(lbracket, rbracket);
				else
					dimension = factory.new_dimension(lbracket, (AstConstExpression) expression, rbracket);
			} else
				throw new RuntimeException("Invalid bracket-match at line " + this.current_line(stream));

			return factory.new_abs_declarator(declarator, dimension);
		} else
			return null;
	}

	// identifier-list
	/**
	 * <code>id-list |--> identifier (, identifier)*</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstIdentifierList match_identifier_list(CTokenStream stream, CScope scope) throws Exception {
		AstName name;
		AstPunctuator comma;
		int cursor;
		if ((name = this.match_identifier(stream, scope)) != null) {
			AstIdentifierList idlist = factory.new_identifier_list(name);

			do {
				cursor = stream.get_cursor();
				if ((comma = this.match_punctuator(stream, CPunctuator.comma)) != null) {
					if ((name = this.match_identifier(stream, scope)) != null)
						idlist.append_identifier(comma, name);
					else {
						stream.recover(cursor);
					}
				}
			} while ((comma != null) && (name != null));

			return idlist;
		} else
			return null;
	}

	// parameter-type-list
	/**
	 * <code> param_type_list |--> param_list (, ...)?</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	// parameter-type-list
	protected AstParameterTypeList match_parameter_type_list(CTokenStream stream, CScope scope) throws Exception {
		AstParameterList plist;
		AstPunctuator comma, ellipsis;
		if ((plist = this.match_parameter_list(stream, scope)) != null) {
			int cursor = stream.get_cursor();
			if ((comma = this.match_punctuator(stream, CPunctuator.comma)) != null) {
				if ((ellipsis = this.match_punctuator(stream, CPunctuator.ellipsis)) != null)
					return factory.new_parameter_type_list(plist, comma, ellipsis);
				else {
					stream.recover(cursor);
					return factory.new_parameter_type_list(plist);
				}
			} else
				return factory.new_parameter_type_list(plist);
		} else
			return null;
	}

	/**
	 * <code> param_list |--> param_decl (, param_decl)* </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstParameterList match_parameter_list(CTokenStream stream, CScope scope) throws Exception {
		AstParameterDeclaration decl;
		AstPunctuator comma;
		int cursor;
		if ((decl = this.match_parameter_declaration(stream, scope)) != null) {
			AstParameterList plist = factory.new_parameter_list(decl);
			do {
				cursor = stream.get_cursor();
				if ((comma = this.match_punctuator(stream, CPunctuator.comma)) != null) {
					if ((decl = this.match_parameter_declaration(stream, scope)) != null)
						plist.append_parameter(comma, decl);
					else {
						stream.recover(cursor);
					}
				}
			} while ((comma != null) && (decl != null));
			return plist;
		} else
			return null;
	}

	// init-declarator-list
	/**
	 * <code>init_declarator_list |--> init_declarator (, init_declarator)* </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstInitDeclaratorList match_init_declarator_list(CTokenStream stream, CScope scope) throws Exception {
		AstInitDeclarator declarator;
		AstPunctuator comma;
		int cursor;
		if ((declarator = this.match_init_declarator(stream, scope)) != null) {
			AstInitDeclaratorList declarator_list = factory.new_init_declarator_list(declarator);
			do {
				cursor = stream.get_cursor();
				if ((comma = this.match_punctuator(stream, CPunctuator.comma)) != null) {
					if ((declarator = this.match_init_declarator(stream, scope)) != null)
						declarator_list.append_init_declarator(comma, declarator);
					else {
						stream.recover(cursor);
					}
				}
			} while ((comma != null) && (declarator != null));
			return declarator_list;
		} else
			return null;
	}

	// init-declarator
	/**
	 * <code>init_declarator |--> declarator (= initializer)?</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstInitDeclarator match_init_declarator(CTokenStream stream, CScope scope) throws Exception {
		AstDeclarator declarator;
		if ((declarator = this.match_declarator(stream, scope)) != null) {
			AstPunctuator assign;
			AstInitializer initializer;

			if ((assign = this.match_punctuator(stream, CPunctuator.assign)) != null) {
				if ((initializer = this.match_initializer(stream, scope)) != null)
					return factory.new_init_declarator(declarator, assign, initializer);
				else
					throw new RuntimeException("Invalid init-declarator at line " + this.current_line(stream)
							+ "\n\texpected initializer following \'=\'");
			} else
				return factory.new_init_declarator(declarator);
		} else
			return null;
	}

	// struct-declarator-list
	/**
	 * <code>struct_declarator_list |--> struct_declarator (, struct_declarator)*</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstStructDeclaratorList match_struct_declarator_list(CTokenStream stream, CScope scope) throws Exception {
		AstStructDeclarator declarator;
		AstPunctuator comma;
		if ((declarator = this.match_struct_declarator(stream, scope)) != null) {
			AstStructDeclaratorList list = factory.new_struct_declarator_list(declarator);
			do {
				if ((comma = this.match_punctuator(stream, CPunctuator.comma)) != null) {
					if ((declarator = this.match_struct_declarator(stream, scope)) != null)
						list.append_declarator(comma, declarator);
					else
						throw new RuntimeException("Invalid struct-declarator-list at line " + this.current_line(stream)
								+ "\n\texpected for struct-declarator after comma");
				}
			} while (comma != null && declarator != null);
			return list;
		} else
			return null;
	}

	// struct-declarator
	/**
	 * <code> declarator | (declarator)? : const_expr</code>
	 *
	 * @param stream
	 * @param scope
	 * @param declarator
	 * @return
	 * @throws Exception
	 */
	protected AstStructDeclarator match_struct_declarator(CTokenStream stream, CScope scope) throws Exception {
		AstDeclarator declarator;
		AstPunctuator colon;
		AstConstExpression expr;
		if ((declarator = this.match_declarator(stream, scope)) != null) {
			if ((colon = this.match_punctuator(stream, CPunctuator.colon)) != null) {
				if ((expr = this.match_const_expression(stream, scope)) != null)
					return factory.new_struct_declarator(declarator, colon, expr);
				else
					throw new RuntimeException("Invalid struct-declarator at line " + this.current_line(stream)
							+ "\n\texpected expression following \':\'");
			} else
				return factory.new_struct_declarator(declarator);
		} else if ((colon = this.match_punctuator(stream, CPunctuator.colon)) != null) {
			if ((expr = this.match_const_expression(stream, scope)) != null)
				return factory.new_struct_declarator(colon, expr);
			else
				throw new RuntimeException("Invalid struct-declarator at line " + this.current_line(stream)
						+ "\n\texpected expression following \':\'");
		} else
			return null;
	}

	// initializer
	/**
	 * <code> initializer |--> expr | init_body </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstInitializer match_initializer(CTokenStream stream, CScope scope) throws Exception {
		AstInitializerBody body;
		AstExpression expr;
		if ((body = this.match_initializer_body(stream, scope)) != null) {
			return factory.new_initializer(body);
		} else if ((expr = this.match_assignment_expression(stream, scope)) != null) {
			return factory.new_initializer(expr);
		} else
			return null;
	}

	// initializer-body
	/**
	 * <code> init_body |--> "{" initializer_list (",")? "}"</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstInitializerBody match_initializer_body(CTokenStream stream, CScope scope) throws Exception {
		int cursor = stream.get_cursor();
		AstPunctuator lbrace, rbrace;
		AstInitializerList list;
		if ((lbrace = this.match_punctuator(stream, CPunctuator.left_brace)) != null) {
			if ((list = this.match_initializer_list(stream, scope)) != null) {
				AstPunctuator comma = this.match_punctuator(stream, CPunctuator.comma);
				if ((rbrace = this.match_punctuator(stream, CPunctuator.right_brace)) != null) {
					if (comma == null)
						return factory.new_initializer_body(lbrace, list, rbrace);
					else
						return factory.new_initializer_body(lbrace, list, comma, rbrace);
				} else
					throw new RuntimeException("Invalid brace-match at line " + this.current_line(stream));
			} else {
				stream.recover(cursor);
				return null;
			}
		} else
			return null;
	}

	/**
	 * <code>initializer_list |--> field_initializer (, field_initializer)*</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstInitializerList match_initializer_list(CTokenStream stream, CScope scope) throws Exception {
		AstFieldInitializer initializer;
		AstPunctuator comma;
		int cursor;
		if ((initializer = this.match_field_initializer(stream, scope)) != null) {
			AstInitializerList list = factory.new_initializer_list(initializer);
			do {
				cursor = stream.get_cursor();
				if ((comma = this.match_punctuator(stream, CPunctuator.comma)) != null) {
					if ((initializer = this.match_field_initializer(stream, scope)) != null)
						list.append(comma, initializer);
					else
						stream.recover(cursor);
				}
			} while (comma != null && initializer != null);
			return list;
		} else
			return null;
	}

	/**
	 * <code>field_initializer |--> (designator_list =)? initializer</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstFieldInitializer match_field_initializer(CTokenStream stream, CScope scope) throws Exception {
		AstDesignatorList designators;
		AstPunctuator assign;
		AstInitializer initializer;
		if ((designators = this.match_designator_list(stream, scope)) != null) {
			if ((assign = this.match_punctuator(stream, CPunctuator.assign)) != null) {
				if ((initializer = this.match_initializer(stream, scope)) != null)
					return factory.new_field_initializer(designators, assign, initializer);
				else
					throw new RuntimeException("Invalid field-initializer at line " + this.current_line(stream)
							+ "\n\tno initializer after \'=\'");
			} else
				throw new RuntimeException(
						"Invalid field-initializer at line " + this.current_line(stream) + "\n\texpected for \'=\'");
		} else if ((initializer = this.match_initializer(stream, scope)) != null)
			return factory.new_field_initializer(initializer);
		else
			return null;
	}

	/**
	 * <code>designator_list |--> designator+</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstDesignatorList match_designator_list(CTokenStream stream, CScope scope) throws Exception {
		AstDesignator designator;
		if ((designator = this.match_designator(stream, scope)) != null) {
			AstDesignatorList dlist = factory.new_designator_list(designator);
			while ((designator = this.match_designator(stream, scope)) != null)
				dlist.append_designator(designator);
			return dlist;
		} else
			return null;
	}

	/**
	 * <code>designator |--> (. field | [ const_expr ] )</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstDesignator match_designator(CTokenStream stream, CScope scope) throws Exception {
		AstPunctuator head, tail;
		if ((head = this.match_punctuator(stream, CPunctuator.dot)) != null) {
			CToken token = stream.get_token();
			if (token instanceof CIdentifierToken) {
				String idname = ((CIdentifierToken) token).get_name();
				AstField field = factory.new_field(idname);
				field.set_location(token.get_location());
				stream.consume();
				return factory.new_designator(head, field);
			} else
				throw new RuntimeException(
						"Invalid designator at line " + this.current_line(stream) + "\n\texpected for .field");
		} else if ((head = this.match_punctuator(stream, CPunctuator.left_bracket)) != null) {
			AstConstExpression expr;
			if ((expr = this.match_const_expression(stream, scope)) != null) {
				if ((tail = this.match_punctuator(stream, CPunctuator.right_bracket)) != null)
					return factory.new_designator(head, expr, tail);
				else
					throw new RuntimeException(
							"Invalid bracket-match at line " + this.current_line(stream) + "\n\texpected for \']\'");
			} else
				throw new RuntimeException(
						"Invalid designator at line " + this.current_line(stream) + "\n\texpected for [ expression ]");
		} else
			return null;
	}

	// struct-declaration
	/**
	 * <code>specifier_qualifier_list struct_declarator_list ;</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstStructDeclaration match_struct_declaration(CTokenStream stream, CScope scope) throws Exception {
		AstSpecifierQualifierList specifiers;
		AstStructDeclaratorList declarators;
		AstPunctuator semicolon;

		int cursor = stream.get_cursor();
		AstStructDeclaration declaration;
		if ((specifiers = this.match_specifier_qualifier_list(stream, scope)) != null) {
			if ((declarators = this.match_struct_declarator_list(stream, scope)) != null) {
				if ((semicolon = this.match_punctuator(stream, CPunctuator.semicolon)) != null)
					declaration = factory.new_struct_declaration(specifiers, declarators, semicolon);
				else
					throw new RuntimeException("Invalid struct-declaration at line " + this.current_line(stream)
							+ "\n\texpected for \';\'");
			} else {
				stream.recover(cursor);
				declaration = null;
			}
		} else
			declaration = null;

		if (declaration != null)
			this.process_scope(declaration, scope);
		return declaration;
	}

	// parameter-declaration
	/**
	 * <code>declaration_specifiers (declarator | abs_declarator)?</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstParameterDeclaration match_parameter_declaration(CTokenStream stream, CScope scope) throws Exception {
		AstDeclarationSpecifiers specifiers;
		AstParameterDeclaration declaration;
		if ((specifiers = this.match_declaration_specifiers(stream, scope)) != null) {
			AstDeclarator declarator;
			AstAbsDeclarator abs_declarator;
			if ((declarator = this.match_declarator(stream, scope)) != null)
				declaration = factory.new_parameter_declaration(specifiers, declarator);
			else if ((abs_declarator = this.match_abs_declarator(stream, scope)) != null)
				declaration = factory.new_parameter_declaration(specifiers, abs_declarator);
			else
				declaration = factory.new_parameter_declaration(specifiers);
		} else
			declaration = null;

		if (declaration != null)
			this.process_scope(declaration, scope);
		return declaration;
	}

	// declaration
	/**
	 * <code>declaration |--> declaration-specifiers (init-declarator-list)?</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstDeclaration match_declaration(CTokenStream stream, CScope scope) throws Exception {
		AstDeclarationSpecifiers specifiers;
		AstDeclaration declaration;
		if ((specifiers = this.match_declaration_specifiers(stream, scope)) != null) {
			AstInitDeclaratorList declarators;
			if ((declarators = this.match_init_declarator_list(stream, scope)) != null)
				declaration = factory.new_declaration(specifiers, declarators);
			else
				declaration = factory.new_declaration(specifiers);
		} else
			declaration = null;
		return declaration;
	}

	// type-name
	/**
	 * <code>specifier_qualifier_list (abs_declarator)?</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstTypeName match_type_name(CTokenStream stream, CScope scope) throws Exception {
		AstSpecifierQualifierList specifiers;
		if ((specifiers = this.match_specifier_qualifier_list(stream, scope)) != null) {
			AstAbsDeclarator declarator;
			if ((declarator = this.match_abs_declarator(stream, scope)) != null)
				return factory.new_typename(specifiers, declarator);
			else
				return factory.new_typename(specifiers);
		} else
			return null;
	}

	// statement
	/**
	 * <code>statement |--> labeled-statement | compound-statement | expression-statement | selection-statement | iteration-statement | jump-statement</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstStatement match_statement(CTokenStream stream, CScope scope) throws Exception {
		AstStatement statement;

		CToken head = stream.get_token();
		if (head instanceof CKeywordToken) {
			switch (((CKeywordToken) head).get_keyword()) {
			case c89_case:
				return this.match_case_statement(stream, scope);
			case c89_default:
				return this.match_default_statement(stream, scope);
			case c89_if:
				return this.match_if_statement(stream, scope);
			case c89_switch:
				return this.match_switch_statement(stream, scope);
			case c89_while:
				return this.match_while_statement(stream, scope);
			case c89_do:
				return this.match_do_while_statement(stream, scope);
			case c89_for:
				return this.match_for_statement(stream, scope);
			case c89_goto:
				return this.match_goto_statement(stream, scope);
			case c89_continue:
				return this.match_continue_statement(stream, scope);
			case c89_break:
				return this.match_break_statement(stream, scope);
			case c89_return:
				return this.match_return_statement(stream, scope);
			default:
				statement = null;
				break;
			}
		} else if (head instanceof CPunctuatorToken) {
			switch (((CPunctuatorToken) head).get_punctuator()) {
			case left_brace:
				statement = this.match_compound_statement(stream, scope);
				break;
			case semicolon:
				statement = this.match_expression_statement(stream, scope);
				break;
			default:
				statement = null;
				break;
			}
		} else if (head instanceof CIdentifierToken) {
			/* identifier : */
			statement = this.match_label_statement(stream, scope);
			/* expression | specifiers */
		} else
			statement = null;

		if (statement == null) {
			if ((statement = this.match_declaration_statement(stream, scope)) == null)
				statement = this.match_expression_statement(stream, scope);
		}
		return statement;
	}

	/**
	 * <code>expr_stmt |--> (expression)? ; </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstExpressionStatement match_expression_statement(CTokenStream stream, CScope scope) throws Exception {
		AstExpression expression;
		AstPunctuator semicolon;
		if ((semicolon = this.match_punctuator(stream, CPunctuator.semicolon)) != null)
			return factory.new_expression_statement(semicolon);
		else if ((expression = this.match_expression(stream, scope)) != null) {
			if ((semicolon = this.match_punctuator(stream, CPunctuator.semicolon)) != null)
				return factory.new_expression_statement(expression, semicolon);
			else
				throw new RuntimeException("Invalid expression-statement at line " + this.current_line(stream)
						+ "\n\texpecting for \';\'\n\twhere " + this.get_text_from(stream));
		} else
			return null;
	}

	/**
	 * <code>declaration-statement |--> declaration ;</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstDeclarationStatement match_declaration_statement(CTokenStream stream, CScope scope) throws Exception {
		int cursor = stream.get_cursor();

		AstDeclaration declaration;
		AstPunctuator semicolon;
		if ((declaration = this.match_declaration(stream, scope)) != null) {
			if ((semicolon = this.match_punctuator(stream, CPunctuator.semicolon)) != null) {
				this.process_scope(declaration, scope);
				return factory.new_declaration_statement(declaration, semicolon);
			} else {
				stream.recover(cursor);
				return null;
			}
		} else
			return null;
	}

	/**
	 * <code>labeled-statement |--> label : </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstLabeledStatement match_label_statement(CTokenStream stream, CScope scope) throws Exception {
		int cursor = stream.get_cursor();
		AstLabel label;
		AstPunctuator colon;
		if ((label = this.match_label(stream, scope)) != null) {
			if ((colon = this.match_punctuator(stream, CPunctuator.colon)) != null) {
				this.process_scope(label, scope);
				return factory.new_labeled_statement(label, colon);
			} else {
				stream.recover(cursor);
				return null;
			}
		} else
			return null;
	}

	/**
	 * <code>case-statement |--> case const-expression :</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstCaseStatement match_case_statement(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword keyword;
		if ((keyword = this.match_keyword(stream, CKeyword.c89_case)) != null) {
			AstConstExpression expr;
			AstPunctuator colon;
			if ((expr = this.match_const_expression(stream, scope)) != null) {
				if ((colon = this.match_punctuator(stream, CPunctuator.colon)) != null)
					return factory.new_case_statement(keyword, expr, colon);
				else
					throw new RuntimeException(
							"Invalid case-statement at line " + this.current_line(stream) + "\n\texpected for \':\'");
			} else
				throw new RuntimeException(
						"Invalid case-statement at line " + this.current_line(stream) + "\n\texpected for expression");
		} else
			return null;
	}

	/**
	 * <code>default-statement |--> <b>default</b> : </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstDefaultStatement match_default_statement(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword keyword;
		AstPunctuator colon;
		if ((keyword = this.match_keyword(stream, CKeyword.c89_default)) != null) {
			if ((colon = this.match_punctuator(stream, CPunctuator.colon)) != null)
				return factory.new_default_statement(keyword, colon);
			else
				throw new RuntimeException(
						"Invalid default-statement at line " + this.current_line(stream) + "\n\texpected for \':\'");
		} else
			return null;
	}

	/**
	 * <code>comp-statement |--> { (statement-list)? }</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstCompoundStatement match_compound_statement(CTokenStream stream, CScope scope) throws Exception {
		AstPunctuator lbrace, rbrace;
		if ((lbrace = this.match_punctuator(stream, CPunctuator.left_brace)) != null) {
			scope = scope.new_child(); /* down to the block */

			AstStatementList list;
			AstCompoundStatement body;
			if ((list = this.match_statement_list(stream, scope)) != null) {
				if ((rbrace = this.match_punctuator(stream, CPunctuator.right_brace)) != null)
					body = factory.new_compound_statement(lbrace, list, rbrace);
				else
					throw new RuntimeException("Invalid compound-statement at line " + this.current_line(stream)
							+ "\n\texpected for \'}\'" + "\n\tat: " + this.get_text_from(stream));
			} else if ((rbrace = this.match_punctuator(stream, CPunctuator.right_brace)) != null)
				body = factory.new_compound_statement(lbrace, rbrace);
			else
				throw new RuntimeException("Invalid compound-statement at line " + this.current_line(stream)
						+ "\n\texpected for \'}\'" + "\n\tat: " + this.get_text_from(stream));

			scope.set_origin(body);
			body.set_scope(scope);
			return body;
		} else
			return null;
	}

	/**
	 * <code>statement-list |--> (statement)+</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstStatementList match_statement_list(CTokenStream stream, CScope scope) throws Exception {
		AstStatement statement;
		if ((statement = this.match_statement(stream, scope)) != null) {
			AstStatementList list = factory.new_statement_list(statement);
			while ((statement = this.match_statement(stream, scope)) != null) {
				list.append_statement(statement);
			}
			return list;
		} else
			return null;
	}

	/**
	 * <code>if-statement |--> <b>if</b> ( expression ) statement {<b>else</b> statement}?</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstIfStatement match_if_statement(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword _if, _else;
		AstPunctuator lparanth, rparanth;
		if ((_if = this.match_keyword(stream, CKeyword.c89_if)) != null) {
			if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
				AstExpression expr;
				AstStatement tbranch, fbranch;
				if ((expr = this.match_expression(stream, scope)) != null) {
					if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
						if ((tbranch = this.match_statement(stream, scope)) != null) {
							if ((_else = this.match_keyword(stream, CKeyword.c89_else)) != null) {
								if ((fbranch = this.match_statement(stream, scope)) != null)
									return factory.new_if_statement(_if, lparanth, expr, rparanth, tbranch, _else,
											fbranch);
								else
									throw new RuntimeException("Invalid if-statement at line "
											+ this.current_line(stream) + "\n\texpected for statement of false-branch");
							} else
								return factory.new_if_statement(_if, lparanth, expr, rparanth, tbranch);
						} else
							throw new RuntimeException("Invalid if-statement at line " + this.current_line(stream)
									+ "\n\texpected for statement of true-branch" + "\n\tat: "
									+ this.get_text_from(stream));
					} else
						throw new RuntimeException(
								"Invalid if-statement at line " + this.current_line(stream) + "\n\texpected for \')\'");
				} else
					throw new RuntimeException("Invalid if-statement at line " + this.current_line(stream)
							+ "\n\texpected for expression" + "\n\t" + this.get_text_from(stream));
			} else
				throw new RuntimeException(
						"Invalid if-statement at line " + this.current_line(stream) + "\n\texpected for \'(\'");
		} else
			return null;
	}

	/**
	 * <code>switch-statement |--> <b>switch</b> ( expression ) statement</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstSwitchStatement match_switch_statement(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword keyword;
		AstPunctuator lparnath, rparanth;
		AstExpression expression;
		AstStatement statement;
		if ((keyword = this.match_keyword(stream, CKeyword.c89_switch)) != null) {
			if ((lparnath = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
				if ((expression = this.match_expression(stream, scope)) != null) {
					if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
						if ((statement = this.match_statement(stream, scope)) != null)
							return factory.new_switch_statement(keyword, lparnath, expression, rparanth, statement);
						else
							throw new RuntimeException("Invalid switch-statement at line " + this.current_line(stream)
									+ "\n\texpected for statement");
					} else
						throw new RuntimeException("Invalid switch-statement at line " + this.current_line(stream)
								+ "\n\texpected for \')\'");
				} else
					throw new RuntimeException("Invalid switch-statement at line " + this.current_line(stream)
							+ "\n\texpected for expression after \'(\'");
			} else
				throw new RuntimeException(
						"Invalid switch-statement at line " + this.current_line(stream) + "\n\texpected for \'(\'");
		} else
			return null;
	}

	/**
	 * <code>while-statement |--> <b>while</b> ( expression ) statement</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstWhileStatement match_while_statement(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword keyword;
		AstPunctuator lparanth, rparanth;
		AstExpression expression;
		AstStatement statement;
		if ((keyword = this.match_keyword(stream, CKeyword.c89_while)) != null) {
			if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
				if ((expression = this.match_expression(stream, scope)) != null) {
					if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
						if ((statement = this.match_statement(stream, scope)) != null)
							return factory.new_while_statement(keyword, lparanth, expression, rparanth, statement);
						else
							throw new RuntimeException("Invalid while-statement at line " + this.current_line(stream)
									+ "\n\texpected for statement" + "\n\tat: " + this.get_text_from(stream));
					} else
						throw new RuntimeException("Invalid while-statement at line " + this.current_line(stream)
								+ "\n\texpected for \')\'");
				} else
					throw new RuntimeException("Invalid while-statement at line " + this.current_line(stream)
							+ "\n\texpected for expression");
			} else
				throw new RuntimeException(
						"Invalid while-statement at line " + this.current_line(stream) + "\n\texpected for \'(\'");
		} else
			return null;
	}

	/**
	 * <code>do-while-statement |--> <b>do</b> statement <b>while</b> ( expression ) ;</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstDoWhileStatement match_do_while_statement(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword _do, _while;
		AstExpression expr;
		AstStatement body;
		AstPunctuator lparanth, rparanth, semicolon;
		if ((_do = this.match_keyword(stream, CKeyword.c89_do)) != null) {
			if ((body = this.match_statement(stream, scope)) != null) {
				if ((_while = this.match_keyword(stream, CKeyword.c89_while)) != null) {
					if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
						if ((expr = this.match_expression(stream, scope)) != null) {
							if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
								if ((semicolon = this.match_punctuator(stream, CPunctuator.semicolon)) != null)
									return factory.new_do_while_statement(_do, body, _while, lparanth, expr, rparanth,
											semicolon);
								else
									throw new RuntimeException("Invalid do-while-statement at line "
											+ this.current_line(stream) + "\n\texpected for \';\'");
							} else
								throw new RuntimeException("Invalid do-while-statement at line "
										+ this.current_line(stream) + "\n\texpected for \')\'");
						} else
							throw new RuntimeException("Invalid do-while-statement at line " + this.current_line(stream)
									+ "\n\texpected for expression");
					} else
						throw new RuntimeException("Invalid do-while-statement at line " + this.current_line(stream)
								+ "\n\texpected for \'(\'");
				} else
					throw new RuntimeException("Invalid do-while-statement at line " + this.current_line(stream)
							+ "\n\texpected for \'while\'");
			} else
				throw new RuntimeException("Invalid do-while-statement at line " + this.current_line(stream)
						+ "\n\texpected for statement");
		} else
			return null;
	}

	/**
	 * <code><b>for</b> ( {declaration-statement | expression-statement} expression-statement {expression}? ) statement</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstForStatement match_for_statement(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword _for;
		AstPunctuator lparanth, rparanth;
		AstStatement initializer, condition, body;
		AstExpression increment;
		if ((_for = this.match_keyword(stream, CKeyword.c89_for)) != null) {
			if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
				if ((initializer = this.match_declaration_statement(stream, scope)) == null)
					if ((initializer = this.match_expression_statement(stream, scope)) == null)
						throw new RuntimeException("Invalid for-statement at line " + this.current_line(stream)
								+ "\n\texpected for expression or declaration or ;");

				if ((condition = this.match_expression_statement(stream, scope)) != null) {
					increment = this.match_expression(stream, scope);

					if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
						if ((body = this.match_statement(stream, scope)) != null) {
							if (increment == null) {
								if (initializer instanceof AstExpressionStatement)
									return factory.new_for_statement(_for, lparanth,
											(AstExpressionStatement) initializer, (AstExpressionStatement) condition,
											rparanth, body);
								else
									return factory.new_for_statement(_for, lparanth,
											(AstDeclarationStatement) initializer, (AstExpressionStatement) condition,
											rparanth, body);
							} else {
								if (initializer instanceof AstExpressionStatement)
									return factory.new_for_statement(_for, lparanth,
											(AstExpressionStatement) initializer, (AstExpressionStatement) condition,
											increment, rparanth, body);
								else
									return factory.new_for_statement(_for, lparanth,
											(AstDeclarationStatement) initializer, (AstExpressionStatement) condition,
											increment, rparanth, body);
							}
						} else
							throw new RuntimeException("Invalid for-statement at line " + this.current_line(stream)
									+ "\n\texpected for body-statement");
					} else
						throw new RuntimeException("Invalid for-statement at line " + this.current_line(stream)
								+ "\n\texpected for \')\'");
				} else
					throw new RuntimeException("Invalid for-statement at line " + this.current_line(stream)
							+ "\n\texpected for expression or ; for condition");
			} else
				throw new RuntimeException(
						"Invalid for-statement at line " + this.current_line(stream) + "\n\texpected for \'(\'");
		} else
			return null;
	}

	/**
	 * <code>goto-statement |--> <b>goto</b> label ;</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstGotoStatement match_goto_statement(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword keyword;
		AstLabel label;
		AstPunctuator semicolon;
		if ((keyword = this.match_keyword(stream, CKeyword.c89_goto)) != null) {
			CToken head = stream.get_token();
			if (head instanceof CIdentifierToken) {
				String idname = ((CIdentifierToken) head).get_name();
				stream.consume();
				label = factory.new_label(idname);
				label.set_location(head.get_location());

				if ((semicolon = this.match_punctuator(stream, CPunctuator.semicolon)) != null)
					return factory.new_goto_statement(keyword, label, semicolon);
				else
					throw new RuntimeException(
							"Invalid goto-statement at line " + this.current_line(stream) + "\n\texpected for \';\'");
			} else
				throw new RuntimeException(
						"Invalid goto-statement at line " + this.current_line(stream) + "\n\texpected for label");
		} else
			return null;
	}

	/**
	 * <code>continue-statement |--> <b>continue</b> ;</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstContinueStatement match_continue_statement(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword keyword;
		AstPunctuator semicolon;
		if ((keyword = this.match_keyword(stream, CKeyword.c89_continue)) != null) {
			if ((semicolon = this.match_punctuator(stream, CPunctuator.semicolon)) != null)
				return factory.new_continue_statement(keyword, semicolon);
			else
				throw new RuntimeException(
						"Invalid continue-statement at line " + this.current_line(stream) + "\n\texpected for \';\'");
		} else
			return null;
	}

	/**
	 * <code>continue-statement |--> <b>break</b> ;</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstBreakStatement match_break_statement(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword keyword;
		AstPunctuator semicolon;
		if ((keyword = this.match_keyword(stream, CKeyword.c89_break)) != null) {
			if ((semicolon = this.match_punctuator(stream, CPunctuator.semicolon)) != null)
				return factory.new_break_statement(keyword, semicolon);
			else
				throw new RuntimeException(
						"Invalid break-statement at line " + this.current_line(stream) + "\n\texpected for \';\'");
		} else
			return null;
	}

	/**
	 * <code>return-statement |--> <b>return</b> (expression)? ;</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstReturnStatement match_return_statement(CTokenStream stream, CScope scope) throws Exception {
		AstKeyword keyword;
		AstExpression expr;
		AstPunctuator semicolon;
		if ((keyword = this.match_keyword(stream, CKeyword.c89_return)) != null) {
			expr = this.match_expression(stream, scope);
			if ((semicolon = this.match_punctuator(stream, CPunctuator.semicolon)) != null) {
				if (expr == null)
					return factory.new_return_statement(keyword, semicolon);
				else
					return factory.new_return_statement(keyword, expr, semicolon);
			} else
				throw new RuntimeException("Invalid return-statmenet at line " + this.current_line(stream)
						+ "\n\texpected for \';\' at \"" + this.get_text_from(stream) + "\"");
		} else
			return null;
	}

	// preprocess-line
	public AstExternalUnit match_preprocess_line(CTokenStream stream, CScope scope) throws Exception {
		CToken head = stream.get_token();
		if (head instanceof CDirectiveToken) {
			AstPreprocessLine pline;
			switch (((CDirectiveToken) head).get_directive()) {
			case cdir_if:
				pline = this.match_preprocess_if_line(stream, scope);
				break;
			case cdir_ifdef:
				pline = this.match_preprocess_ifdef_line(stream, scope);
				break;
			case cdir_ifndef:
				pline = this.match_preprocess_ifndef_line(stream, scope);
				break;
			case cdir_elif:
				pline = this.match_preprocess_elif_line(stream, scope);
				break;
			case cdir_else:
				pline = this.match_preprocess_else_line(stream, scope);
				break;
			case cdir_endif:
				pline = this.match_preprocess_endif_line(stream, scope);
				break;
			case cdir_define:
				pline = this.match_preprocess_define_line(stream, scope);
				break;
			case cdir_undef:
				pline = this.match_preprocess_undef_line(stream, scope);
				break;
			case cdir_include:
				pline = this.match_prepreocess_include_line(stream, scope);
				break;
			case cdir_line:
				pline = this.match_preprocess_line_line(stream, scope);
				break;
			case cdir_error:
				pline = this.match_preprocess_error_line(stream, scope);
				break;
			case cdir_pragma:
				pline = this.match_preprocess_pragma_line(stream, scope);
				break;
			default:
				pline = null;
				break;
			}

			if (pline == null)
				throw new RuntimeException("Invalid directive-line at " + this.current_line(stream));
			else
				return pline;
		} else if (head instanceof CPunctuatorToken) {
			if (((CPunctuatorToken) head).get_punctuator() == CPunctuator.hash)
				return this.match_preprocess_hash_line(stream, scope);
			else
				return null;
		} else
			return null;
	}

	/**
	 * <code>#if const-expression new-line</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessIfLine match_preprocess_if_line(CTokenStream stream, CScope scope) throws Exception {
		AstDirective dir;
		AstConstExpression expr;
		if ((dir = this.match_directive(stream, CDirective.cdir_if)) != null) {
			if ((expr = this.match_const_expression(stream, scope)) != null) {
				if (this.match_newline(stream))
					return factory.new_if_line(dir, expr);
				else
					throw new RuntimeException(
							"Invalid #if-line at " + this.current_line(stream) + "\n\texpected for new-line");
			} else
				throw new RuntimeException(
						"Invalid #if-line at " + this.current_line(stream) + "\n\texpected for const-expression");
		} else
			return null;
	}

	/**
	 * <code>#ifdef macro new-line</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessIfdefLine match_preprocess_ifdef_line(CTokenStream stream, CScope scope) throws Exception {
		AstDirective dir;
		AstMacro macro;
		if ((dir = this.match_directive(stream, CDirective.cdir_ifdef)) != null) {
			CToken head = stream.get_token();
			if (head instanceof CIdentifierToken) {
				String idname = ((CIdentifierToken) head).get_name();
				stream.consume();
				macro = factory.new_macro(idname);
				macro.set_location(head.get_location());
				if (this.match_newline(stream))
					return factory.new_ifdef_line(dir, macro);
				else
					throw new RuntimeException(
							"Invalid #ifdef-line at line " + this.current_line(stream) + "\n\texpected for new-line");
			} else
				throw new RuntimeException(
						"Invalid #ifdef-line at line " + this.current_line(stream) + "\n\texpected for identifier");
		} else
			return null;
	}

	/**
	 * <code>#ifndef macro new-line</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessIfndefLine match_preprocess_ifndef_line(CTokenStream stream, CScope scope) throws Exception {
		AstDirective dir;
		AstMacro macro;
		if ((dir = this.match_directive(stream, CDirective.cdir_ifndef)) != null) {
			CToken head = stream.get_token();
			if (head instanceof CIdentifierToken) {
				String idname = ((CIdentifierToken) head).get_name();
				stream.consume();
				macro = factory.new_macro(idname);
				macro.set_location(head.get_location());
				if (this.match_newline(stream))
					return factory.new_ifndef_line(dir, macro);
				else
					throw new RuntimeException(
							"Invalid #ifndef-line at line " + this.current_line(stream) + "\n\texpected for new-line");
			} else
				throw new RuntimeException(
						"Invalid #ifndef-line at line " + this.current_line(stream) + "\n\texpected for identifier");
		} else
			return null;
	}

	/**
	 * <code>#elif const-expression \n</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessElifLine match_preprocess_elif_line(CTokenStream stream, CScope scope) throws Exception {
		AstDirective dir;
		AstConstExpression expr;
		if ((dir = this.match_directive(stream, CDirective.cdir_elif)) != null) {
			if ((expr = this.match_const_expression(stream, scope)) != null) {
				if (this.match_newline(stream))
					return factory.new_elif_line(dir, expr);
				else
					throw new RuntimeException(
							"Invalid #elif-line at " + this.current_line(stream) + "\n\texpected for new-line");
			} else
				throw new RuntimeException(
						"Invalid #elif-line at " + this.current_line(stream) + "\n\texpected for const-expression");
		} else
			return null;
	}

	/**
	 * <code>#else \n</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessElseLine match_preprocess_else_line(CTokenStream stream, CScope scope) throws Exception {
		AstDirective dir;
		if ((dir = this.match_directive(stream, CDirective.cdir_else)) != null) {
			if (this.match_newline(stream))
				return factory.new_else_line(dir);
			else
				throw new RuntimeException(
						"Invalid #else-line at " + this.current_line(stream) + "\n\texpected for new-line");
		} else
			return null;
	}

	/**
	 * <code>#endif \n</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessEndifLine match_preprocess_endif_line(CTokenStream stream, CScope scope) throws Exception {
		AstDirective dir;
		if ((dir = this.match_directive(stream, CDirective.cdir_endif)) != null) {
			if (this.match_newline(stream))
				return factory.new_endif_line(dir);
			else
				throw new RuntimeException(
						"Invalid #endif-line at " + this.current_line(stream) + "\n\texpected for new-line");
		} else
			return null;
	}

	/**
	 * <code>#define identifier {( idlist | ... )}? tokens newline</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessDefineLine match_preprocess_define_line(CTokenStream stream, CScope scope) throws Exception {
		AstDirective dir;
		AstMacroList list;
		AstMacro macro;
		AstMacroBody tokens;

		if ((dir = this.match_directive(stream, CDirective.cdir_define)) != null) {
			CToken head = stream.get_token();
			if (head instanceof CIdentifierToken) {
				String idname = ((CIdentifierToken) head).get_name();
				stream.consume();
				macro = factory.new_macro(idname);
				macro.set_location(head.get_location());

				if ((list = this.match_macro_list(stream, scope)) != null) {
					tokens = this.match_macro_body(stream);
					return factory.new_define_line(dir, macro, list, tokens);
				} else {
					tokens = this.match_macro_body(stream);
					return factory.new_define_line(dir, macro, tokens);
				}
			} else
				throw new RuntimeException(
						"Invalid #define-line at " + this.current_line(stream) + "\n\texpected for identifier");
		} else
			return null;
	}

	protected AstMacroList match_macro_list(CTokenStream stream, CScope scope) throws Exception {
		int cursor = stream.get_cursor();

		AstPunctuator lparanth, rparanth, ellipsis;
		AstIdentifierList list;
		if ((lparanth = this.match_punctuator(stream, CPunctuator.left_paranth)) != null) {
			if ((list = this.match_identifier_list(stream, scope)) != null) {
				if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
					return factory.new_macro_list(lparanth, list, rparanth);
				} else {
					stream.recover(cursor);
					return null;
				}
			} else if ((ellipsis = this.match_punctuator(stream, CPunctuator.ellipsis)) != null) {
				if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
					return factory.new_macro_list(lparanth, ellipsis, rparanth);
				} else {
					stream.recover(cursor);
					return null;
				}
			} else if ((rparanth = this.match_punctuator(stream, CPunctuator.right_paranth)) != null) {
				return factory.new_macro_list(lparanth, rparanth);
			} else {
				stream.recover(cursor);
				return null;
			}
		} else
			return null;
	}

	protected AstMacroBody match_macro_body(CTokenStream stream) throws Exception {
		AstMacroBody tokens = factory.new_macro_body();
		while (!this.match_newline(stream) && stream.has_token()) {
			tokens.append_token(stream.get_token());
			stream.consume();
		}
		return tokens;
	}

	/**
	 * <code>#undef macro \n</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessUndefLine match_preprocess_undef_line(CTokenStream stream, CScope scope) throws Exception {
		AstDirective dir;
		AstMacro macro;
		if ((dir = this.match_directive(stream, CDirective.cdir_undef)) != null) {
			if ((macro = this.match_macro(stream)) != null) {
				if (this.match_newline(stream))
					return factory.new_undef_line(dir, macro);
				else
					throw new RuntimeException(
							"Invalid #undef-line at " + this.current_line(stream) + "\n\texpected for \\n");
			} else
				throw new RuntimeException(
						"Invalid #undef-line at " + this.current_line(stream) + "\n\texpected for identifier");
		} else
			return null;
	}

	/**
	 * <code>#include header \n</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessIncludeLine match_prepreocess_include_line(CTokenStream stream, CScope scope)
			throws Exception {
		AstDirective dir;
		AstHeader header;
		if ((dir = this.match_directive(stream, CDirective.cdir_include)) != null) {
			if ((header = this.match_header(stream)) != null) {
				if (this.match_newline(stream))
					return factory.new_include_line(dir, header);
				else
					throw new RuntimeException("Invalid #include-line at " + this.current_line(stream)
							+ "\n\texpected for \\n while for " + stream.get_token());
			} else
				throw new RuntimeException(
						"Invalid #include-line at " + this.current_line(stream) + "\n\texpected for header");
		} else
			return null;
	}

	/**
	 * <code>#line constant (literal)? \n</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessLineLine match_preprocess_line_line(CTokenStream stream, CScope scope) throws Exception {
		AstDirective dir;
		AstConstant constant;
		AstLiteral literal;
		if ((dir = this.match_directive(stream, CDirective.cdir_line)) != null) {
			if ((constant = this.match_constant(stream)) != null) {
				if ((literal = this.match_literal(stream)) != null) {
					if (this.match_newline(stream))
						return factory.new_line_line(dir, constant, literal);
					else
						throw new RuntimeException(
								"Invalid #line-line at " + this.current_line(stream) + "\n\texpected for \\n");
				} else if (this.match_newline(stream))
					return factory.new_line_line(dir, constant);
				else
					throw new RuntimeException(
							"Invalid #line-line at " + this.current_line(stream) + "\n\texpected for \\n");
			} else
				throw new RuntimeException(
						"Invalid #line-line at " + this.current_line(stream) + "\n\texpected for constant");
		} else
			return null;
	}

	/**
	 * <code>#error (tokens)+ \n</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessErrorLine match_preprocess_error_line(CTokenStream stream, CScope scope) throws Exception {
		AstDirective dir;
		AstMacroBody body;
		if ((dir = this.match_directive(stream, CDirective.cdir_error)) != null) {
			body = this.match_macro_body(stream);
			return factory.new_error_line(dir, body);
		} else
			return null;
	}

	/**
	 * <code>#pragma (token)+ \n</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessPragmaLine match_preprocess_pragma_line(CTokenStream stream, CScope scope) throws Exception {
		AstDirective dir;
		AstMacroBody body;
		if ((dir = this.match_directive(stream, CDirective.cdir_pragma)) != null) {
			body = this.match_macro_body(stream);
			return factory.new_pragma_line(dir, body);
		} else
			return null;
	}

	/**
	 * <code># \n</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstPreprocessNoneLine match_preprocess_hash_line(CTokenStream stream, CScope scope) throws Exception {
		AstPunctuator hash;
		int cursor = stream.get_cursor();
		if ((hash = this.match_punctuator(stream, CPunctuator.hash)) != null) {
			if (this.match_newline(stream))
				return factory.new_hash_line(hash);
			else {
				stream.recover(cursor);
				return null;
			}
		} else
			return null;
	}

	// function-definition
	/**
	 * <code>declaration-specifiers declarator (declaration-list)? compound-statement</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstFunctionDefinition match_function_definition(CTokenStream stream, CScope scope) throws Exception {
		AstDeclarationSpecifiers specifiers;
		AstDeclarationList declaration_list;
		AstDeclarator declarator;
		AstCompoundStatement body;
		int cursor;

		cursor = stream.get_cursor();
		if ((specifiers = this.match_declaration_specifiers(stream, scope)) != null) {
			if ((declarator = this.match_declarator(stream, scope)) != null) {
				if (this.is_function_declarator(declarator)) {
					scope = scope
							.new_child(); /* try to create function scope */
					this.process_function_scope(declarator, scope);

					AstFunctionDefinition definition;
					if ((declaration_list = this.match_declaration_list(stream, scope)) != null) {
						if ((body = this.match_compound_statement(stream, scope)) != null)
							definition = factory.new_function_definition(specifiers, declarator, declaration_list,
									body);
						else
							throw new RuntimeException(
									"Invalid function-definition at line " + this.current_line(stream));
					} else if (this.is_punctuator(stream, CPunctuator.left_brace)) {
						if ((body = this.match_compound_statement(stream, scope)) != null)
							definition = factory.new_function_definition(specifiers, declarator, body);
						else
							throw new RuntimeException(
									"Invalid function-definition at line " + this.current_line(stream));
					} else {
						scope.get_parent().del_child(scope);
						stream.recover(cursor);
						return null;
					}

					scope.set_origin(definition);
					definition.set_scope(scope);
					return definition;
				} else {
					stream.recover(cursor);
					return null;
				}
			} else {
				stream.recover(cursor);
				return null;
			}
		} else
			return null;
	}

	protected AstDeclarationList match_declaration_list(CTokenStream stream, CScope scope) throws Exception {
		AstDeclarationStatement declaration;
		if ((declaration = this.match_declaration_statement(stream, scope)) != null) {
			AstDeclarationList declaration_list = factory.new_declaration_list(declaration);
			while ((declaration = this.match_declaration_statement(stream, scope)) != null)
				declaration_list.append_declaration(declaration);
			return declaration_list;
		} else
			return null;
	}

	// external-unit
	/**
	 * <code>external-unit |--> preprocess-line | declaration-statement | function-definition </code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public AstExternalUnit match_external_unit(CTokenStream stream, CScope scope) throws Exception {
		if (!stream.has_token())
			return null;
		else {
			AstExternalUnit unit;
			if ((unit = this.match_preprocess_line(stream, scope)) == null) {
				if ((unit = this.match_declaration_statement(stream, scope)) == null) {
					if ((unit = this.match_function_definition(stream, scope)) == null) {
						throw new RuntimeException("Unable to match external-unit at line " + this.current_line(stream)
								+ "\n\tat: " + this.get_text_from(stream));
					}
				}
			}
			return unit;
		}
	}

	// translation-unit
	/**
	 * parse the source code token stream into C99-syntax parse-tree
	 *
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	@Override
	public AstTranslationUnit parse(CTokenStream stream) throws Exception {
		AstTranslationUnit root = factory.new_translation_unit();
		CScope scope = new CScopeImpl();
		scope.set_origin(root);
		root.set_scope(scope);

		AstExternalUnit unit;
		root.set_scope(scope);
		while ((unit = this.match_external_unit(stream, scope)) != null) {
			/*
			 * if(stream.has_token()) System.out.println("\tTo line: " +
			 * this.get_text_from(stream));
			 */
			root.append_unit(unit);
		}

		return root;
	}

	// basic match method
	/**
	 * <code>|--> keyword</code>
	 *
	 * @param stream
	 * @param keyword
	 * @return : null if not matched, otherwise, stream is consumed by one token
	 * @throws Exception
	 */
	protected AstKeyword match_keyword(CTokenStream stream, CKeyword keyword) throws Exception {
		CToken head = stream.get_token();
		if (head instanceof CKeywordToken) {
			CKeyword kw = ((CKeywordToken) head).get_keyword();
			if (kw == keyword) {
				AstKeyword kwnode = factory.new_keyword(keyword);
				kwnode.set_location(head.get_location());
				stream.consume();
				return kwnode;
			}
		}
		return null;
	}

	/**
	 * <code>|--> punctuator</code>
	 *
	 * @param stream
	 * @param punctuator
	 * @return : null if not matched, otherwise, stream is consumed by one token
	 * @throws Exception
	 */
	protected AstPunctuator match_punctuator(CTokenStream stream, CPunctuator punctuator) throws Exception {
		CToken head = stream.get_token();
		if (head instanceof CPunctuatorToken) {
			CPunctuator punc = ((CPunctuatorToken) head).get_punctuator();
			if (punc == punctuator) {
				AstPunctuator puncnode = factory.new_punctuator(punctuator);
				puncnode.set_location(head.get_location());
				stream.consume();
				return puncnode;
			}
		}
		return null;
	}

	/**
	 * <code>|--> directive</code>
	 *
	 * @param stream
	 * @param directive
	 * @return : null if not matched, otherwise, stream is consumed by one token
	 * @throws Exception
	 */
	protected AstDirective match_directive(CTokenStream stream, CDirective directive) throws Exception {
		CToken head = stream.get_token();
		if (head instanceof CDirectiveToken) {
			CDirective dir = ((CDirectiveToken) head).get_directive();
			if (dir == directive) {
				AstDirective dirnode = factory.new_directive(directive);
				dirnode.set_location(head.get_location());
				stream.consume();
				return dirnode;
			}
		}
		return null;
	}

	/**
	 * <code>|--> identifier</code>
	 *
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	protected AstName match_identifier(CTokenStream stream, CScope scope) throws Exception {
		CToken head = stream.get_token();
		if (head instanceof CIdentifierToken) {
			String idname = ((CIdentifierToken) head).get_name();

			if (scope.has_name(idname)) { /* name-filter */
				CName cname = scope.get_name(idname);

				if (cname instanceof CTypeName || cname instanceof CEnumeratorName || cname instanceof CMacroName
						|| cname instanceof CLabelName)
					return null;
			}

			AstName aname = factory.new_name(idname);
			aname.set_location(head.get_location());
			stream.consume();
			return aname;
		} else
			return null;
	}

	/**
	 * <code>|--> identifier</code>
	 *
	 * @param stream
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected AstLabel match_label(CTokenStream stream, CScope scope) throws Exception {
		CToken head = stream.get_token();
		if (head instanceof CIdentifierToken) {
			String idname = ((CIdentifierToken) head).get_name();
			if (scope.has_name(idname)) {
				CName cname = scope.get_name(idname);
				if (!(cname instanceof CLabelName))
					return null;
			}

			AstLabel label = factory.new_label(idname);
			label.set_location(head.get_location());
			stream.consume();
			return label;
		} else
			return null;
	}

	/**
	 *
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	protected boolean match_newline(CTokenStream stream) throws Exception {
		if (stream.get_token() instanceof CNewlineToken) {
			stream.consume();
			return true;
		} else
			return false;
	}

	/**
	 * match identifier
	 *
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	protected AstMacro match_macro(CTokenStream stream) throws Exception {
		CToken head = stream.get_token();
		if (head instanceof CIdentifierToken) {
			String idname = ((CIdentifierToken) head).get_name();
			AstMacro macro = factory.new_macro(idname);
			macro.set_location(head.get_location());
			stream.consume();
			return macro;
		} else
			return null;
	}

	/**
	 * match header
	 *
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	protected AstHeader match_header(CTokenStream stream) throws Exception {
		CToken head = stream.get_token();
		if (head instanceof CHeaderToken) {
			CHeaderToken header = (CHeaderToken) head;

			AstHeader node;
			if (header.is_system_header())
				node = factory.new_system_header(header.get_path());
			else
				node = factory.new_user_header(header.get_path());
			node.set_location(head.get_location());

			stream.consume();
			return node;
		} else
			return null;
	}

	// basic checking method
	private int current_line(CTokenStream stream) throws Exception {
		CLocation loc = stream.get_token().get_location();
		CText text = loc.get_source();
		return text.line_of(loc.get_bias());
	}

	private CScope get_file_scope(CScope scope) {
		while (scope.get_parent() != null)
			scope = scope.get_parent();
		return scope;
	}

	private boolean is_punctuator(CTokenStream stream, CPunctuator punc) throws Exception {
		CToken head = stream.get_token();
		if (head instanceof CPunctuatorToken)
			return ((CPunctuatorToken) head).get_punctuator() == punc;
		else
			return false;
	}

	private String get_text_from(CTokenStream stream) throws Exception {
		StringBuilder buff = new StringBuilder();
		int cursor = stream.get_cursor();

		int count = 12;
		while (stream.has_token() && --count > 0) {
			CToken token = stream.get_token();
			CLocation loc = token.get_location();
			buff.append(loc.read()).append(' ');
			stream.consume();
		}

		stream.recover(cursor);
		return buff.toString();
	}

	private boolean is_function_declarator(AstDeclarator declarator) throws Exception {
		while (declarator != null) {
			if (declarator.get_production() == DeclaratorProduction.declarator_parambody)
				return true;
			else
				declarator = declarator.get_declarator();
		}
		return false;
	}

	// process methods for updating scope
	protected void process_scope(AstStructDeclaration declaration, CScope scope) throws Exception {
		AstStructDeclaratorList declarators = declaration.get_declarators();
		int n = declarators.number_of_declarators();
		for (int i = 0; i < n; i++) {
			AstStructDeclarator declarator = declarators.get_declarator(i);
			if (declarator.has_declarator()) {
				AstName name = this.derive_name(declarator.get_declarator());
				CName cname = scope.get_name_table().new_field_name(name);
				name.set_cname(cname);
			}
		}
	}

	protected void process_scope(AstParameterDeclaration declaration, CScope scope) throws Exception {
		if (declaration.has_declarator()) {
			AstName name = this.derive_name(declaration.get_declarator());
			CName cname = scope.get_name_table().new_parameter_name(name);
			name.set_cname(cname);
		}
	}

	protected void process_scope(AstDeclaration declaration, CScope scope) throws Exception {
		if (declaration.has_declarator_list()) {
			boolean is_typedef = this.is_typedef(declaration.get_specifiers());

			AstInitDeclaratorList declarators = declaration.get_declarator_list();
			int n = declarators.number_of_init_declarators();
			for (int i = 0; i < n; i++) {
				AstInitDeclarator init_declarator = declarators.get_init_declarator(i);
				AstName name = this.derive_name(init_declarator.get_declarator());

				if (is_typedef) {
					CName cname = scope.get_name_table().new_typedef_name(name);
					name.set_cname(cname);
				} else {
					CName cname = scope.get_name_table().new_instance_name(name);
					name.set_cname(cname);
				}
			}
		}
	}

	private AstName derive_name(AstDeclarator declarator) throws Exception {
		while (declarator.get_production() != DeclaratorProduction.identifier)
			declarator = declarator.get_declarator();
		return declarator.get_identifier();
	}

	private boolean is_typedef(AstDeclarationSpecifiers specifiers) throws Exception {
		int n = specifiers.number_of_specifiers();
		for (int i = 0; i < n; i++) {
			AstSpecifier specifier = specifiers.get_specifier(i);
			if (specifier instanceof AstStorageClass) {
				if (((AstStorageClass) specifier).get_keyword().get_keyword() == CKeyword.c89_typedef)
					return true;
			}
		}
		return false;
	}

	protected void process_scope(AstLabel label, CScope scope) throws Exception {
		CName cname = scope.get_name_table().new_label_name(label);
		label.set_cname(cname);
	}

	protected void process_function_scope(AstDeclarator declarator, CScope scope) throws Exception {
		/* derive the name and parameter-body */
		AstParameterBody body = null;
		AstName ast_name = null;
		while (declarator != null) {
			if (declarator.get_production() == DeclaratorProduction.declarator_parambody)
				body = declarator.get_parameter_body();
			if (declarator.get_production() == DeclaratorProduction.identifier)
				ast_name = declarator.get_identifier();
			declarator = declarator.get_declarator();
		}
		if (body == null)
			return;

		/* update the cname for function name in global scope */
		CName cname = scope.get_parent().get_name_table().new_instance_name(ast_name);
		ast_name.set_cname(cname);

		/* update name in parameter body to the function scope */
		CScope param_scope = body.get_scope();
		if (param_scope != null) {
			Iterator<String> names = param_scope.get_name_table().get_names();
			while (names.hasNext()) {
				String name = names.next();
				cname = param_scope.get_name(name);

				if (cname instanceof CParameterName) {
					CInstanceName new_param_name = scope.get_name_table()
							.new_instance_name((AstName) cname.get_source());
					cname.get_source().set_cname(new_param_name);
				}

			}
		}
	}
}
