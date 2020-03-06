package com.jcsa.jcparse.lang.astree.impl.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstArrayExpressionImpl extends AstFixedExpressionImpl implements AstArrayExpression {

	public AstArrayExpressionImpl(AstExpression array, AstPunctuator lbracket, AstExpression dimension,
			AstPunctuator rbracket) throws Exception {
		super(4);

		if (array == null)
			throw new IllegalArgumentException("Invalid array_expr: null");
		else if (lbracket == null || lbracket.get_punctuator() != CPunctuator.left_bracket)
			throw new IllegalArgumentException("Invalid left-bracket: null");
		else if (dimension == null)
			throw new IllegalArgumentException("Invalid dimension: null");
		else if (rbracket == null || rbracket.get_punctuator() != CPunctuator.right_bracket)
			throw new IllegalArgumentException("Invalid right-bracket: null");
		else {
			this.set_child(0, array);
			this.set_child(1, lbracket);
			this.set_child(2, dimension);
			this.set_child(3, rbracket);
		}
	}

	@Override
	public AstExpression get_array_expression() {
		return (AstExpression) this.children[0];
	}

	@Override
	public AstPunctuator get_left_bracket() {
		return (AstPunctuator) this.children[1];
	}

	@Override
	public AstExpression get_dimension_expression() {
		return (AstExpression) this.children[2];
	}

	@Override
	public AstPunctuator get_right_bracket() {
		return (AstPunctuator) this.children[3];
	}

}
