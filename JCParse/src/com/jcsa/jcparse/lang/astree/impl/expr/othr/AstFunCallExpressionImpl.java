package com.jcsa.jcparse.lang.astree.impl.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstFunCallExpressionImpl extends AstFixedExpressionImpl implements AstFunCallExpression {

	public AstFunCallExpressionImpl(AstExpression func, AstPunctuator lparanth, AstPunctuator rparanth)
			throws Exception {
		super(3);

		if (func == null)
			throw new IllegalArgumentException("Invalid function: null");
		else if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid left-paranth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid right-paranth: null");
		else {
			this.set_child(0, func);
			this.set_child(1, lparanth);
			this.set_child(2, rparanth);
		}
	}

	public AstFunCallExpressionImpl(AstExpression func, AstPunctuator lparanth, AstArgumentList arglist,
			AstPunctuator rparanth) throws Exception {
		super(4);

		if (func == null)
			throw new IllegalArgumentException("Invalid function: null");
		else if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid left-paranth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid right-paranth: null");
		else if (arglist == null)
			throw new IllegalArgumentException("Invalid arglist: null");
		else {
			this.set_child(0, func);
			this.set_child(1, lparanth);
			this.set_child(2, arglist);
			this.set_child(3, rparanth);
		}
	}

	@Override
	public AstExpression get_function() {
		return (AstExpression) children[0];
	}

	@Override
	public AstPunctuator get_lparanth() {
		return (AstPunctuator) children[1];
	}

	@Override
	public boolean has_argument_list() {
		return children.length == 4;
	}

	@Override
	public AstArgumentList get_argument_list() {
		if (children.length != 4)
			return null;
		else
			return (AstArgumentList) children[2];
	}

	@Override
	public AstPunctuator get_rparanth() {
		return (AstPunctuator) children[children.length - 1];
	}

}
