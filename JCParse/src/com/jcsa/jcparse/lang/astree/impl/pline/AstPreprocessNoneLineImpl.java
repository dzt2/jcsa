package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessNoneLine;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstPreprocessNoneLineImpl extends AstFixedNode implements AstPreprocessNoneLine {

	public AstPreprocessNoneLineImpl(AstPunctuator j) throws Exception {
		super(1);

		if (j == null || j.get_punctuator() != CPunctuator.hash)
			throw new IllegalArgumentException("Invalid jhash: null");
		else
			this.set_child(0, j);
	}

	@Override
	public AstPunctuator get_hash() {
		return (AstPunctuator) children[0];
	}

}
