package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstCaseStatementImpl extends AstFixedNode implements AstCaseStatement {

	public AstCaseStatementImpl(AstKeyword case_kw, AstConstExpression expr, AstPunctuator colon) throws Exception {
		super(3);

		if (case_kw == null || case_kw.get_keyword() != CKeyword.c89_case)
			throw new IllegalArgumentException("Invalid case: null");
		else if (colon == null || colon.get_punctuator() != CPunctuator.colon)
			throw new IllegalArgumentException("Invalid colon: null");
		else {
			this.set_child(0, case_kw);
			this.set_child(1, expr);
			this.set_child(2, colon);
		}
	}

	@Override
	public AstKeyword get_case() {
		return (AstKeyword) children[0];
	}

	@Override
	public AstConstExpression get_expression() {
		return (AstConstExpression) children[1];
	}

	@Override
	public AstPunctuator get_colon() {
		return (AstPunctuator) children[2];
	}

}
