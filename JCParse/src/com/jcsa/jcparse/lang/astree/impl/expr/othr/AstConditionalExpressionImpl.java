package com.jcsa.jcparse.lang.astree.impl.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstConditionalExpressionImpl extends AstFixedExpressionImpl implements AstConditionalExpression {

	public AstConditionalExpressionImpl(AstExpression condition, AstPunctuator question, AstExpression tbranch,
			AstPunctuator colon, AstExpression fbranch) throws Exception {
		super(5);

		if (condition == null)
			throw new IllegalArgumentException("Invalid condition: null");
		else if (question == null || question.get_punctuator() != CPunctuator.question)
			throw new IllegalArgumentException("Invalid question: null");
		else if (tbranch == null)
			throw new IllegalArgumentException("Invalid tbranch: null");
		else if (colon == null || colon.get_punctuator() != CPunctuator.colon)
			throw new IllegalArgumentException("Invalid colon: null");
		else if (fbranch == null)
			throw new IllegalArgumentException("Invalid fbranch: null");
		else {
			this.set_child(0, condition);
			this.set_child(1, question);
			this.set_child(2, tbranch);
			this.set_child(3, colon);
			this.set_child(4, fbranch);
		}
	}

	@Override
	public AstExpression get_condition() {
		return (AstExpression) this.children[0];
	}

	@Override
	public AstPunctuator get_question() {
		return (AstPunctuator) this.children[1];
	}

	@Override
	public AstExpression get_true_branch() {
		return (AstExpression) this.children[2];
	}

	@Override
	public AstPunctuator get_colon() {
		return (AstPunctuator) this.children[3];
	}

	@Override
	public AstExpression get_false_branch() {
		return (AstExpression) this.children[4];
	}

}
