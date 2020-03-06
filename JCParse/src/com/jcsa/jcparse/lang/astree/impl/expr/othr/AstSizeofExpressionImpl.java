package com.jcsa.jcparse.lang.astree.impl.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstSizeofExpressionImpl extends AstFixedExpressionImpl implements AstSizeofExpression {

	public AstSizeofExpressionImpl(AstKeyword sizeof, AstExpression expression) throws Exception {
		super(2);

		if (sizeof == null || sizeof.get_keyword() != CKeyword.c89_sizeof)
			throw new IllegalArgumentException("Invalid sizeof: null");
		else if (expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else {
			this.set_child(0, sizeof);
			this.set_child(1, expression);
		}
	}

	public AstSizeofExpressionImpl(AstKeyword sizeof, AstPunctuator lparanth, AstTypeName typename,
			AstPunctuator rparanth) throws Exception {
		super(4);

		if (sizeof == null || sizeof.get_keyword() != CKeyword.c89_sizeof)
			throw new IllegalArgumentException("Invalid sizeof: null");
		else if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: null");
		else {
			this.set_child(0, sizeof);
			this.set_child(1, lparanth);
			this.set_child(2, typename);
			this.set_child(3, rparanth);
		}
	}

	@Override
	public AstKeyword get_sizeof() {
		return (AstKeyword) this.children[0];
	}

	@Override
	public boolean is_expression() {
		return this.children.length == 2;
	}

	@Override
	public AstExpression get_expression() {
		if (children.length == 2)
			return (AstExpression) this.children[1];
		else
			return null;
	}

	@Override
	public boolean is_typename() {
		return children.length == 4;
	}

	@Override
	public AstPunctuator get_lparanth() {
		if (children.length != 4)
			return null;
		else
			return (AstPunctuator) children[1];
	}

	@Override
	public AstTypeName get_typename() {
		if (children.length != 4)
			return null;
		else
			return (AstTypeName) children[2];
	}

	@Override
	public AstPunctuator get_rparanth() {
		if (children.length != 4)
			return null;
		else
			return (AstPunctuator) children[3];
	}

}
