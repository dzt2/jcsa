package com.jcsa.jcparse.lang.astree.impl.decl.initializer;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstInitializerListImpl extends AstVariableNode implements AstInitializerList {

	public AstInitializerListImpl(AstFieldInitializer init) throws Exception {
		super();
		this.append_child(init);
	}

	@Override
	public int number_of_initializer() {
		return (children.size() + 1) / 2;
	}

	@Override
	public AstFieldInitializer get_initializer(int k) {
		if (k < 0 || 2 * k >= children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return (AstFieldInitializer) children.get(2 * k);
	}

	@Override
	public AstPunctuator get_comma(int k) {
		if (k < 0 || 2 * k + 1 >= children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return (AstPunctuator) children.get(2 * k + 1);
	}

	@Override
	public void append(AstPunctuator comma, AstFieldInitializer init) throws Exception {
		if (comma == null || comma.get_punctuator() != CPunctuator.comma)
			throw new IllegalArgumentException("Invalid comma: null");
		else {
			this.append_child(comma);
			this.append_child(init);
		}
	}

}
