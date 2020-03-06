package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstWhileStatementImpl extends AstFixedNode implements AstWhileStatement {

	public AstWhileStatementImpl(AstKeyword _while, AstPunctuator lparanth, AstExpression expr, AstPunctuator rparanth,
			AstStatement body) throws Exception {
		super(5);

		if (_while == null || _while.get_keyword() != CKeyword.c89_while)
			throw new IllegalArgumentException("Invalid while: null");
		else if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: null");
		else {
			this.set_child(0, _while);
			this.set_child(1, lparanth);
			this.set_child(2, expr);
			this.set_child(3, rparanth);
			this.set_child(4, body);
		}
	}

	@Override
	public AstKeyword get_while() {
		return (AstKeyword) children[0];
	}

	@Override
	public AstPunctuator get_lparanth() {
		return (AstPunctuator) children[1];
	}

	@Override
	public AstExpression get_condition() {
		return (AstExpression) children[2];
	}

	@Override
	public AstPunctuator get_rparanth() {
		return (AstPunctuator) children[3];
	}

	@Override
	public AstStatement get_body() {
		return (AstStatement) children[4];
	}

}
