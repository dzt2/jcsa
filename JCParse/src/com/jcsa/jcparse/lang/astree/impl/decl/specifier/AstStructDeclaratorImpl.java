package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarator;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstStructDeclaratorImpl extends AstFixedNode implements AstStructDeclarator {

	public AstStructDeclaratorImpl(AstDeclarator declarator) throws Exception {
		super(1);
		this.set_child(0, declarator);
	}

	public AstStructDeclaratorImpl(AstPunctuator colon, AstConstExpression expression) throws Exception {
		super(2);

		if (colon == null || colon.get_punctuator() != CPunctuator.colon)
			throw new IllegalArgumentException("Invalid colon: not-colon");
		else {
			this.set_child(0, colon);
			this.set_child(1, expression);
		}
	}

	public AstStructDeclaratorImpl(AstDeclarator declarator, AstPunctuator colon, AstConstExpression expression)
			throws Exception {
		super(3);

		if (colon == null || colon.get_punctuator() != CPunctuator.colon)
			throw new IllegalArgumentException("Invalid colon: not-colon");
		else {
			this.set_child(0, declarator);
			this.set_child(1, colon);
			this.set_child(2, expression);
		}
	}

	@Override
	public boolean has_declarator() {
		return children[0] instanceof AstDeclarator;
	}

	@Override
	public boolean has_expression() {
		return children.length > 1;
	}

	@Override
	public AstDeclarator get_declarator() {
		if (!this.has_declarator())
			throw new IllegalArgumentException("Invalid access: no-declarator");
		else
			return (AstDeclarator) children[0];
	}

	@Override
	public AstPunctuator get_colon() {
		if (!this.has_expression())
			throw new IllegalArgumentException("Invalid access: no-colon");
		else
			return (AstPunctuator) children[children.length - 2];
	}

	@Override
	public AstConstExpression get_expression() {
		if (!this.has_expression())
			throw new IllegalArgumentException("Invalid access: no-expression");
		else
			return (AstConstExpression) children[children.length - 1];
	}

}
