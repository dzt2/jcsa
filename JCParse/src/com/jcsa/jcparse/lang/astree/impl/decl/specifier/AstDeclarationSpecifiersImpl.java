package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.decl.specifier.AstDeclarationSpecifiers;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifier;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;

public class AstDeclarationSpecifiersImpl extends AstVariableNode implements AstDeclarationSpecifiers {

	public AstDeclarationSpecifiersImpl(AstSpecifier specifier) throws Exception {
		super();
		this.append_specifier(specifier);
	}

	@Override
	public int number_of_specifiers() {
		return children.size();
	}

	@Override
	public AstSpecifier get_specifier(int k) {
		if (k < 0 || k >= children.size())
			throw new IllegalArgumentException("Out of index: k = " + k);
		else
			return (AstSpecifier) children.get(k);
	}

	@Override
	public void append_specifier(AstSpecifier spec) throws Exception {
		if (spec == null)
			throw new IllegalArgumentException("Invalid specifier: null");
		else
			this.append_child(spec);
	}

}
