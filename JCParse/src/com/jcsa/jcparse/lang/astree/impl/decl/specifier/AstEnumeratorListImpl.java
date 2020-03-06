package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumerator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumeratorList;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstEnumeratorListImpl extends AstVariableNode implements AstEnumeratorList {

	public AstEnumeratorListImpl(AstEnumerator enumerator) throws Exception {
		super();
		this.append_child(enumerator);
	}

	@Override
	public int number_of_enumerators() {
		return (children.size() + 1) / 2;
	}

	@Override
	public AstEnumerator get_enumerator(int k) {
		if (k < 0 || 2 * k >= children.size())
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstEnumerator) children.get(2 * k);
	}

	@Override
	public AstPunctuator get_comma(int k) {
		if (k < 0 || 2 * k + 1 >= children.size())
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstPunctuator) children.get(2 * k + 1);
	}

	@Override
	public void append_enumerator(AstPunctuator comma, AstEnumerator enumerator) throws Exception {
		if (comma == null || comma.get_punctuator() != CPunctuator.comma)
			throw new IllegalArgumentException("Invalid comma: not-comma");
		else {
			this.append_child(comma);
			this.append_child(enumerator);
		}
	}

}
