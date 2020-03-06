package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstDefaultStatementImpl extends AstFixedNode implements AstDefaultStatement {

	public AstDefaultStatementImpl(AstKeyword _default, AstPunctuator colon) throws Exception {
		super(2);

		if (_default == null || _default.get_keyword() != CKeyword.c89_default)
			throw new IllegalArgumentException("Invalid default: null");
		else if (colon == null || colon.get_punctuator() != CPunctuator.colon)
			throw new IllegalArgumentException("Invalid semicolon: null");
		else {
			this.set_child(0, _default);
			this.set_child(1, colon);
		}
	}

	@Override
	public AstKeyword get_default() {
		return (AstKeyword) children[0];
	}

	@Override
	public AstPunctuator get_colon() {
		return (AstPunctuator) children[1];
	}

}
