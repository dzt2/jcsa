package com.jcsa.jcparse.lang.astree.impl.base;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstPunctuatorImpl extends AstFixedNode implements AstPunctuator {

	/** punctuator this node represents **/
	private CPunctuator punctuator;

	public AstPunctuatorImpl(CPunctuator punc) throws Exception {
		super(0);
		if (punc == null)
			throw new IllegalArgumentException("Invalid punctuator: null");
		else
			this.punctuator = punc;
	}

	@Override
	public CPunctuator get_punctuator() {
		return punctuator;
	}

	@Override
	public String toString() {
		return "<Punctuator>::" + punctuator;
	}
}
