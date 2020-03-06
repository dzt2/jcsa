package com.jcsa.jcparse.lang.astree.impl.decl;

import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstDeclarationSpecifiers;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;

public class AstDeclarationImpl extends AstFixedNode implements AstDeclaration {

	public AstDeclarationImpl(AstDeclarationSpecifiers specifiers) throws Exception {
		super(1);
		this.set_child(0, specifiers);
	}

	public AstDeclarationImpl(AstDeclarationSpecifiers specifiers, AstInitDeclaratorList declarators) throws Exception {
		super(2);
		this.set_child(0, specifiers);
		this.set_child(1, declarators);
	}

	@Override
	public AstDeclarationSpecifiers get_specifiers() {
		return (AstDeclarationSpecifiers) children[0];
	}

	@Override
	public boolean has_declarator_list() {
		return children.length == 2;
	}

	@Override
	public AstInitDeclaratorList get_declarator_list() {
		if (children.length != 2)
			throw new IllegalArgumentException("Invalid access: no init-declaration-list");
		else
			return (AstInitDeclaratorList) children[1];
	}
}
