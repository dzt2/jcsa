package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.decl.declarator.AstAbsDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterDeclaration;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstDeclarationSpecifiers;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;

public class AstParameterDeclarationImpl extends AstFixedNode implements AstParameterDeclaration {

	public AstParameterDeclarationImpl(AstDeclarationSpecifiers specifiers) throws Exception {
		super(1);
		this.set_child(0, specifiers);
	}

	public AstParameterDeclarationImpl(AstDeclarationSpecifiers specifiers, AstDeclarator declarator) throws Exception {
		super(2);
		this.set_child(0, specifiers);
		this.set_child(1, declarator);
	}

	public AstParameterDeclarationImpl(AstDeclarationSpecifiers specifiers, AstAbsDeclarator declarator)
			throws Exception {
		super(2);
		this.set_child(0, specifiers);
		this.set_child(1, declarator);
	}

	@Override
	public AstDeclarationSpecifiers get_specifiers() {
		return (AstDeclarationSpecifiers) children[0];
	}

	@Override
	public boolean has_declarator() {
		return children[children.length - 1] instanceof AstDeclarator;
	}

	@Override
	public boolean has_abs_declarator() {
		return children[children.length - 1] instanceof AstAbsDeclarator;
	}

	@Override
	public AstDeclarator get_declarator() {
		if (!this.has_declarator())
			throw new IllegalArgumentException("Invalid access: no declarator");
		else
			return (AstDeclarator) children[1];
	}

	@Override
	public AstAbsDeclarator get_abs_declarator() {
		if (!this.has_abs_declarator())
			throw new IllegalArgumentException("Invalid access: no declarator");
		else
			return (AstAbsDeclarator) children[1];
	}

}
