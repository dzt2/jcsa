package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaration;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarationList;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;

public class AstStructDeclarationListImpl extends AstVariableNode implements AstStructDeclarationList {

	public AstStructDeclarationListImpl(AstStructDeclaration declaration) throws Exception {
		super();
		this.append_child(declaration);
	}

	@Override
	public int number_of_declarations() {
		return children.size();
	}

	@Override
	public AstStructDeclaration get_declaration(int k) {
		if (k < 0 || k >= children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return (AstStructDeclaration) children.get(k);
	}

	@Override
	public void append_declaration(AstStructDeclaration decl) throws Exception {
		this.append_child(decl);
	}

}
