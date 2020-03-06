package com.jcsa.jcparse.lang.astree.impl.decl.initializer;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstDesignator;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstDesignatorImpl extends AstFixedNode implements AstDesignator {

	public AstDesignatorImpl(AstPunctuator lbracket, AstConstExpression expression, AstPunctuator rbracket)
			throws Exception {
		super(3);

		if (lbracket == null || lbracket.get_punctuator() != CPunctuator.left_bracket)
			throw new IllegalArgumentException("Invalid lbracket: not left-bracket");
		else if (rbracket == null || rbracket.get_punctuator() != CPunctuator.right_bracket)
			throw new IllegalArgumentException("Invalid rbrcket: not right-bracket");
		else {
			this.set_child(0, lbracket);
			this.set_child(1, expression);
			this.set_child(2, rbracket);
		}
	}

	public AstDesignatorImpl(AstPunctuator dot, AstField field) throws Exception {
		super(2);

		if (dot == null || dot.get_punctuator() != CPunctuator.dot)
			throw new IllegalArgumentException("Invalid dot: not dot");
		else {
			this.set_child(0, dot);
			this.set_child(1, field);
		}
	}

	@Override
	public boolean is_dimension() {
		return children.length == 3;
	}

	@Override
	public boolean is_field() {
		return children.length == 2;
	}

	@Override
	public AstPunctuator get_lbracket() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: not dimension");
		else
			return (AstPunctuator) children[0];
	}

	@Override
	public AstConstExpression get_dimension_expression() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: not dimension");
		else
			return (AstConstExpression) children[1];
	}

	@Override
	public AstPunctuator get_rbracket() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: not dimension");
		else
			return (AstPunctuator) children[2];
	}

	@Override
	public AstPunctuator get_dot() {
		if (children.length != 2)
			throw new IllegalArgumentException("Invalid access: not field");
		else
			return (AstPunctuator) children[0];
	}

	@Override
	public AstField get_field() {
		if (children.length != 2)
			throw new IllegalArgumentException("Invalid access: not field");
		else
			return (AstField) children[1];
	}

}
