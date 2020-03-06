package com.jcsa.jcparse.lang.astree.impl.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstParanthExpressionImpl extends AstFixedExpressionImpl implements AstParanthExpression {

	public AstParanthExpressionImpl(AstPunctuator lparanth, AstExpression expression, AstPunctuator rparanth)
			throws Exception {
		super(3);

		if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid left-paranth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid right-paranth: null");
		else if (expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else {
			this.set_child(0, lparanth);
			this.set_child(1, expression);
			this.set_child(2, rparanth);
		}
	}

	@Override
	public AstPunctuator get_lparanth() {
		return (AstPunctuator) this.children[0];
	}

	@Override
	public AstExpression get_sub_expression() {
		return (AstExpression) this.children[1];
	}

	@Override
	public AstPunctuator get_rparanth() {
		return (AstPunctuator) this.children[2];
	}

}
