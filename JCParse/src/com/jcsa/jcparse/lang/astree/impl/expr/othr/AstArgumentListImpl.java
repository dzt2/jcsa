package com.jcsa.jcparse.lang.astree.impl.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstArgumentListImpl extends AstVariableNode implements AstArgumentList {

	public AstArgumentListImpl(AstExpression arg) throws Exception {
		super();
		this.append_child(arg);
	}

	@Override
	public int number_of_arguments() {
		return (children.size() + 1) / 2;
	}

	@Override
	public AstExpression get_argument(int k) {
		if (k < 0 || 2 * k >= children.size())
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstExpression) children.get(2 * k);
	}

	@Override
	public AstPunctuator get_comma(int k) {
		if (k < 0 || 2 * k + 1 >= children.size())
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstPunctuator) children.get(2 * k + 1);
	}

	@Override
	public void append_argument(AstPunctuator comma, AstExpression arg) throws Exception {
		if (comma == null || comma.get_punctuator() != CPunctuator.comma)
			throw new IllegalArgumentException("Invalid comma: not-comma");
		else if (arg == null)
			throw new IllegalArgumentException("Invalid arg: null");
		else {
			this.append_child(comma);
			this.append_child(arg);
		}
	}

}
