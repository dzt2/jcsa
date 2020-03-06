package com.jcsa.jcparse.lang.astree.impl.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstCommaExpressionImpl extends AstVariableNode implements AstCommaExpression {

	protected CType type;

	public AstCommaExpressionImpl(AstExpression arg) throws Exception {
		super();
		this.append_child(arg);
	}

	@Override
	public CType get_value_type() {
		return type;
	}

	@Override
	public void set_value_type(CType type) {
		this.type = type;
	}

	@Override
	public int number_of_arguments() {
		return (children.size() + 1) / 2;
	}

	@Override
	public AstExpression get_expression(int k) {
		if (k < 0 || 2 * k >= children.size())
			throw new IllegalArgumentException("Invalid access: k = " + k);
		else
			return (AstExpression) children.get(2 * k);
	}

	@Override
	public AstPunctuator get_comma(int k) {
		if (k < 0 || 2 * k + 1 >= children.size())
			throw new IllegalArgumentException("Invalid access: k = " + k);
		else
			return (AstPunctuator) children.get(2 * k + 1);
	}

	@Override
	public void append(AstPunctuator comma, AstExpression arg) throws Exception {
		if (arg == null)
			throw new IllegalArgumentException("Invalid arg: null");
		else if (comma == null || comma.get_punctuator() != CPunctuator.comma)
			throw new IllegalArgumentException("Invalid comma: not comma");
		else {
			this.append_child(comma);
			this.append_child(arg);
		}
	}

}
