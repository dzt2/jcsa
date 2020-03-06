package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstContinueStatementImpl extends AstFixedNode implements AstContinueStatement {

	public AstContinueStatementImpl(AstKeyword continue_kw, AstPunctuator semicolon) throws Exception {
		super(2);

		if (continue_kw == null || continue_kw.get_keyword() != CKeyword.c89_continue)
			throw new IllegalArgumentException("Invalid continue: null");
		else if (semicolon == null || semicolon.get_punctuator() != CPunctuator.semicolon)
			throw new IllegalArgumentException("Invalid semicolon: null");
		else {
			this.set_child(0, continue_kw);
			this.set_child(1, semicolon);
		}
	}

	@Override
	public AstKeyword get_continue() {
		return (AstKeyword) children[0];
	}

	@Override
	public AstPunctuator get_semicolon() {
		return (AstPunctuator) children[1];
	}

}
