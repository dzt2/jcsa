package com.jcsa.jcparse.lang.astree.impl.unit;

import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.unit.AstDeclarationList;

public class AstDeclarationListImpl extends AstVariableNode implements AstDeclarationList {

	public AstDeclarationListImpl(AstDeclarationStatement decl) throws Exception {
		super();
		this.append_child(decl);
	}

	@Override
	public int number_of_declarations() {
		return children.size();
	}

	@Override
	public AstDeclarationStatement get_declaration(int k) {
		if (k < 0 || k >= children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return (AstDeclarationStatement) this.children.get(k);
	}

	@Override
	public void append_declaration(AstDeclarationStatement decl) throws Exception {
		this.append_child(decl);
	}

}
