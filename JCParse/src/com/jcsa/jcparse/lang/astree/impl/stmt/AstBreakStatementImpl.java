package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstBreakStatementImpl extends AstFixedNode implements AstBreakStatement {

	public AstBreakStatementImpl(AstKeyword break_kw, AstPunctuator semicolon) throws Exception {
		super(2);

		if (break_kw == null || break_kw.get_keyword() != CKeyword.c89_break)
			throw new IllegalArgumentException("Invalid break: null");
		else if (semicolon == null || semicolon.get_punctuator() != CPunctuator.semicolon)
			throw new IllegalArgumentException("Invalid semicolon: null");
		else {
			this.set_child(0, break_kw);
			this.set_child(1, semicolon);
		}
	}

	@Override
	public AstKeyword get_break() {
		return (AstKeyword) children[0];
	}

	@Override
	public AstPunctuator get_semicolon() {
		return (AstPunctuator) children[1];
	}

}
