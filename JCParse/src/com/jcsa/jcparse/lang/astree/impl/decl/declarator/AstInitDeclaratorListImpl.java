package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstInitDeclaratorListImpl extends AstVariableNode implements AstInitDeclaratorList {

	public AstInitDeclaratorListImpl(AstInitDeclarator decl) throws Exception {
		super();
		this.append_child(decl);
	}

	@Override
	public int number_of_init_declarators() {
		return (children.size() + 1) / 2;
	}

	@Override
	public AstInitDeclarator get_init_declarator(int k) {
		if (k < 0 || 2 * k >= children.size())
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstInitDeclarator) children.get(2 * k);
	}

	@Override
	public AstPunctuator get_comma(int k) {
		if (k < 0 || 2 * k + 1 >= children.size())
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstPunctuator) children.get(2 * k + 1);
	}

	@Override
	public void append_init_declarator(AstPunctuator comma, AstInitDeclarator decl) throws Exception {
		if (comma == null || comma.get_punctuator() != CPunctuator.comma)
			throw new IllegalArgumentException("Invalid comma: not a comma");
		else {
			this.append_child(comma);
			this.append_child(decl);
		}
	}

}
