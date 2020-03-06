package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstDoWhileStatementImpl extends AstFixedNode implements AstDoWhileStatement {

	public AstDoWhileStatementImpl(AstKeyword _do, AstStatement body, AstKeyword _while, AstPunctuator lparanth,
			AstExpression expr, AstPunctuator rparanth, AstPunctuator semicolon) throws Exception {
		super(7);

		if (_do == null || _do.get_keyword() != CKeyword.c89_do)
			throw new IllegalArgumentException("Invalid do: null");
		else if (_while == null || _while.get_keyword() != CKeyword.c89_while)
			throw new IllegalArgumentException("Invalid while: null");
		else if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: null");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: null");
		else if (semicolon == null || semicolon.get_punctuator() != CPunctuator.semicolon)
			throw new IllegalArgumentException("Invalid semicolon: null");
		else {
			this.set_child(0, _do);
			this.set_child(1, body);
			this.set_child(2, _while);
			this.set_child(3, lparanth);
			this.set_child(4, expr);
			this.set_child(5, rparanth);
			this.set_child(6, semicolon);
		}
	}

	@Override
	public AstKeyword get_do() {
		return (AstKeyword) children[0];
	}

	@Override
	public AstStatement get_body() {
		return (AstStatement) children[1];
	}

	@Override
	public AstKeyword get_while() {
		return (AstKeyword) children[2];
	}

	@Override
	public AstPunctuator get_lparanth() {
		return (AstPunctuator) children[3];
	}

	@Override
	public AstExpression get_condition() {
		return (AstExpression) children[4];
	}

	@Override
	public AstPunctuator get_rparanth() {
		return (AstPunctuator) children[5];
	}

	@Override
	public AstPunctuator get_semicolon() {
		return (AstPunctuator) children[6];
	}

}
