package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstIdentifierList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstIdentifierListImpl extends AstVariableNode implements AstIdentifierList {

	public AstIdentifierListImpl(AstName identifier) throws Exception {
		super();
		this.append_child(identifier);
	}

	@Override
	public int number_of_identifiers() {
		return (children.size() + 1) / 2;
	}

	@Override
	public AstName get_identifier(int k) {
		if (k < 0 || 2 * k >= children.size())
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstName) this.children.get(2 * k);
	}

	@Override
	public AstPunctuator get_comma(int k) {
		if (k < 0 || 2 * k + 1 >= children.size())
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstPunctuator) this.children.get(2 * k + 1);
	}

	@Override
	public void append_identifier(AstPunctuator comma, AstName name) throws Exception {
		if (comma == null || comma.get_punctuator() != CPunctuator.comma)
			throw new IllegalArgumentException("Invalid comma: not a comma");
		else {
			this.append_child(comma);
			this.append_child(name);
		}
	}

}
