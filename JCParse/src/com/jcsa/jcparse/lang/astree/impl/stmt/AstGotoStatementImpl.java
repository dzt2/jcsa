package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstGotoStatementImpl extends AstFixedNode implements AstGotoStatement {

	public AstGotoStatementImpl(AstKeyword _goto, AstLabel label, AstPunctuator semicolon) throws Exception {
		super(3);

		if (_goto == null || _goto.get_keyword() != CKeyword.c89_goto)
			throw new IllegalArgumentException("Invalid goto: null");
		else if (semicolon == null || semicolon.get_punctuator() != CPunctuator.semicolon)
			throw new IllegalArgumentException("Invalid semicolon: null");
		else {
			this.set_child(0, _goto);
			this.set_child(1, label);
			this.set_child(2, semicolon);
		}
	}

	@Override
	public AstKeyword get_goto() {
		return (AstKeyword) children[0];
	}

	@Override
	public AstLabel get_label() {
		return (AstLabel) children[1];
	}

	@Override
	public AstPunctuator get_semicolon() {
		return (AstPunctuator) children[2];
	}

}
