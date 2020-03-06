package com.jcsa.jcparse.lang.astree.impl.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;

public class AstFieldExpressionImpl extends AstFixedExpressionImpl implements AstFieldExpression {

	public AstFieldExpressionImpl(AstExpression body, AstPunctuator operator, AstField field) throws Exception {
		super(3);

		if (body == null)
			throw new IllegalArgumentException("Invalid body: null");
		else if (operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else if (field == null)
			throw new IllegalArgumentException("Invalid field: null");
		else {
			switch (operator.get_punctuator()) {
			case dot:
			case arrow:
				break;
			default:
				throw new IllegalArgumentException("Invalid operator: " + operator.get_punctuator());
			}

			this.set_child(0, body);
			this.set_child(1, operator);
			this.set_child(2, field);
		}
	}

	@Override
	public AstExpression get_body() {
		return (AstExpression) this.children[0];
	}

	@Override
	public AstPunctuator get_operator() {
		return (AstPunctuator) this.children[1];
	}

	@Override
	public AstField get_field() {
		return (AstField) this.children[2];
	}

}
