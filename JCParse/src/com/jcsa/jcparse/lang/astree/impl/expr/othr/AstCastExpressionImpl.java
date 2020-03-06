package com.jcsa.jcparse.lang.astree.impl.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstCastExpressionImpl extends AstFixedExpressionImpl implements AstCastExpression {

	public AstCastExpressionImpl(AstPunctuator lparanth, AstTypeName typename, AstPunctuator rparanth,
			AstExpression expression) throws Exception {
		super(4);

		if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid left-paranth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid right-paranth: null");
		else if (typename == null)
			throw new IllegalArgumentException("Invalid typename: null");
		else if (expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else {
			this.set_child(0, lparanth);
			this.set_child(1, typename);
			this.set_child(2, rparanth);
			this.set_child(3, expression);
		}
	}

	@Override
	public AstPunctuator get_lparanth() {
		return (AstPunctuator) this.children[0];
	}

	@Override
	public AstTypeName get_typename() {
		return (AstTypeName) this.children[1];
	}

	@Override
	public AstPunctuator get_rparanth() {
		return (AstPunctuator) this.children[2];
	}

	@Override
	public AstExpression get_expression() {
		return (AstExpression) this.children[3];
	}

}
