package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstExpressionStatementImpl extends AstFixedNode implements AstExpressionStatement {

	public AstExpressionStatementImpl(AstPunctuator semicolon) throws Exception {
		super(1);

		if (semicolon == null || semicolon.get_punctuator() != CPunctuator.semicolon)
			throw new IllegalArgumentException("Invalid semicolon: null");
		else
			this.set_child(0, semicolon);
	}

	public AstExpressionStatementImpl(AstExpression expr, AstPunctuator semicolon) throws Exception {
		super(2);

		if (semicolon == null || semicolon.get_punctuator() != CPunctuator.semicolon)
			throw new IllegalArgumentException("Invalid semicolon: null");
		else {
			this.set_child(0, expr);
			this.set_child(1, semicolon);
		}
	}

	@Override
	public boolean has_expression() {
		return children.length == 2;
	}

	@Override
	public AstExpression get_expression() {
		if (children.length != 2)
			throw new IllegalArgumentException("Invalid access: no expression");
		else
			return (AstExpression) children[0];
	}

	@Override
	public AstPunctuator get_semicolon() {
		return (AstPunctuator) children[children.length - 1];
	}

}
