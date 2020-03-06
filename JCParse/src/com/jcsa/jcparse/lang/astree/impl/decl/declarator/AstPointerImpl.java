package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstPointer;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstPointerImpl extends AstVariableNode implements AstPointer {

	public AstPointerImpl(AstKeyword keyword) throws Exception {
		super();
		this.append_keyword(keyword);
	}

	public AstPointerImpl(AstPunctuator punc) throws Exception {
		super();
		this.append_punctuator(punc);
	}

	@Override
	public int number_of_keywords() {
		return children.size();
	}

	@Override
	public AstNode get_specifier(int k) {
		if (k < 0 || k >= children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return children.get(k);
	}

	@Override
	public void append_keyword(AstKeyword keyword) throws Exception {
		if (keyword == null)
			throw new IllegalArgumentException("Invalid keyword: null");
		else {
			switch (keyword.get_keyword()) {
			case c89_const:
			case c89_volatile:
			case c99_restrict:
				this.append_child(keyword);
				break;
			default:
				throw new IllegalArgumentException("Invalid keyword: " + keyword.get_keyword());
			}
		}
	}

	@Override
	public void append_punctuator(AstPunctuator punc) throws Exception {
		if (punc == null || punc.get_punctuator() != CPunctuator.ari_mul)
			throw new IllegalArgumentException("Invalid pointer: null");
		else
			this.append_child(punc);
	}

}
