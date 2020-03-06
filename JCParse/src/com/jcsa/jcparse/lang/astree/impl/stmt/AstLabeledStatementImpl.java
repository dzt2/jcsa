package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstLabeledStatementImpl extends AstFixedNode implements AstLabeledStatement {

	public AstLabeledStatementImpl(AstLabel label, AstPunctuator colon) throws Exception {
		super(2);
		if (colon == null || colon.get_punctuator() != CPunctuator.colon)
			throw new IllegalArgumentException("Invalid colon: null");
		else {
			this.set_child(0, label);
			this.set_child(1, colon);
		}
	}

	@Override
	public AstLabel get_label() {
		return (AstLabel) children[0];
	}

	@Override
	public AstPunctuator get_colon() {
		return (AstPunctuator) children[1];
	}

}
