package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstReturnStatementImpl extends AstFixedNode implements AstReturnStatement {

	public AstReturnStatementImpl(AstKeyword ret, AstExpression expr, AstPunctuator semicolon) throws Exception {
		super(3);

		if (ret == null || ret.get_keyword() != CKeyword.c89_return)
			throw new IllegalArgumentException("Invalid return: null");
		else if (semicolon == null || semicolon.get_punctuator() != CPunctuator.semicolon)
			throw new IllegalArgumentException("Invalid semicolon: null");
		else {
			this.set_child(0, ret);
			this.set_child(1, expr);
			this.set_child(2, semicolon);
		}
	}

	public AstReturnStatementImpl(AstKeyword ret, AstPunctuator semicolon) throws Exception {
		super(2);

		if (ret == null || ret.get_keyword() != CKeyword.c89_return)
			throw new IllegalArgumentException("Invalid return: null");
		else if (semicolon == null || semicolon.get_punctuator() != CPunctuator.semicolon)
			throw new IllegalArgumentException("Invalid semicolon: null");
		else {
			this.set_child(0, ret);
			this.set_child(1, semicolon);
		}
	}

	@Override
	public AstKeyword get_return() {
		return (AstKeyword) children[0];
	}

	@Override
	public boolean has_expression() {
		return children.length == 3;
	}

	@Override
	public AstExpression get_expression() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no-expression");
		else
			return (AstExpression) children[1];
	}

	@Override
	public AstPunctuator get_semicolon() {
		return (AstPunctuator) children[children.length - 1];
	}

}
