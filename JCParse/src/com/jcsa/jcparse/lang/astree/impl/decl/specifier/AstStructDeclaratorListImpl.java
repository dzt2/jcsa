package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaratorList;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstStructDeclaratorListImpl extends AstVariableNode implements AstStructDeclaratorList {

	public AstStructDeclaratorListImpl(AstStructDeclarator declarator) throws Exception {
		super();
		this.append_child(declarator);
	}

	@Override
	public int number_of_declarators() {
		return (children.size() + 1) / 2;
	}

	@Override
	public AstStructDeclarator get_declarator(int k) {
		if (k < 0 || 2 * k >= children.size())
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstStructDeclarator) children.get(2 * k);
	}

	@Override
	public AstPunctuator get_comma(int k) {
		if (k < 0 || 2 * k + 1 >= children.size())
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstPunctuator) children.get(2 * k + 1);
	}

	@Override
	public void append_declarator(AstPunctuator comma, AstStructDeclarator declarator) throws Exception {
		if (comma == null || comma.get_punctuator() != CPunctuator.comma)
			throw new IllegalArgumentException("Invalid comma: no-comma");
		else {
			this.append_child(comma);
			this.append_child(declarator);
		}
	}

}
