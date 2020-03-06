package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstArrayQualifierList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDimension;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstDimensionImpl extends AstFixedNode implements AstDimension {

	public AstDimensionImpl(AstPunctuator lbracket, AstPunctuator rbracket) throws Exception {
		super(2);

		if (lbracket == null || lbracket.get_punctuator() != CPunctuator.left_bracket)
			throw new IllegalArgumentException("Invalid lbracket: not left-bracket");
		else if (rbracket == null || rbracket.get_punctuator() != CPunctuator.right_bracket)
			throw new IllegalArgumentException("Invalid rbracket: not right-bracket");
		else {
			this.set_child(0, lbracket);
			this.set_child(1, rbracket);
		}
	}

	public AstDimensionImpl(AstPunctuator lbracket, AstArrayQualifierList specifiers, AstPunctuator rbracket)
			throws Exception {
		super(3);

		if (lbracket == null || lbracket.get_punctuator() != CPunctuator.left_bracket)
			throw new IllegalArgumentException("Invalid lbracket: not left-bracket");
		else if (rbracket == null || rbracket.get_punctuator() != CPunctuator.right_bracket)
			throw new IllegalArgumentException("Invalid rbracket: not right-bracket");
		else {
			this.set_child(0, lbracket);
			this.set_child(1, specifiers);
			this.set_child(2, rbracket);
		}
	}

	public AstDimensionImpl(AstPunctuator lbracket, AstConstExpression expression, AstPunctuator rbracket)
			throws Exception {
		super(3);

		if (lbracket == null || lbracket.get_punctuator() != CPunctuator.left_bracket)
			throw new IllegalArgumentException("Invalid lbracket: not left-bracket");
		else if (rbracket == null || rbracket.get_punctuator() != CPunctuator.right_bracket)
			throw new IllegalArgumentException("Invalid rbracket: not right-bracket");
		else {
			this.set_child(0, lbracket);
			this.set_child(1, expression);
			this.set_child(2, rbracket);
		}
	}

	public AstDimensionImpl(AstPunctuator lbracket, AstArrayQualifierList specifiers, AstConstExpression expression,
			AstPunctuator rbracket) throws Exception {
		super(4);

		if (lbracket == null || lbracket.get_punctuator() != CPunctuator.left_bracket)
			throw new IllegalArgumentException("Invalid lbracket: not left-bracket");
		else if (rbracket == null || rbracket.get_punctuator() != CPunctuator.right_bracket)
			throw new IllegalArgumentException("Invalid rbracket: not right-bracket");
		else {
			this.set_child(0, lbracket);
			this.set_child(1, specifiers);
			this.set_child(2, expression);
			this.set_child(3, rbracket);
		}
	}

	@Override
	public boolean has_expression() {
		return children[children.length - 2] instanceof AstConstExpression;
	}

	@Override
	public boolean has_array_qualifier_list() {
		return children[1] instanceof AstArrayQualifierList;
	}

	@Override
	public AstPunctuator get_lbracket() {
		return (AstPunctuator) children[0];
	}

	@Override
	public AstArrayQualifierList get_array_qualifier_list() {
		if (!this.has_array_qualifier_list())
			throw new IllegalArgumentException("Invalid access: no-array-qualifiers");
		else
			return (AstArrayQualifierList) children[1];
	}

	@Override
	public AstConstExpression get_expression() {
		if (!this.has_expression())
			throw new IllegalArgumentException("Invalid access: no expression");
		else
			return (AstConstExpression) children[children.length - 2];
	}

	@Override
	public AstPunctuator get_rbracket() {
		return (AstPunctuator) children[children.length - 1];
	}

}
