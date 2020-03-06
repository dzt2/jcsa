package com.jcsa.jcparse.lang.astree.impl.decl;

import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstAbsDeclarator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifierQualifierList;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.ctype.CType;

public class AstTypeNameImpl extends AstFixedNode implements AstTypeName {

	public AstTypeNameImpl(AstSpecifierQualifierList specifiers) throws Exception {
		super(1);
		this.set_child(0, specifiers);
	}

	public AstTypeNameImpl(AstSpecifierQualifierList specifiers, AstAbsDeclarator declarator) throws Exception {
		super(2);
		this.set_child(0, specifiers);
		this.set_child(1, declarator);
	}

	@Override
	public AstSpecifierQualifierList get_specifiers() {
		return (AstSpecifierQualifierList) children[0];
	}

	@Override
	public AstAbsDeclarator get_declarator() {
		if (children.length != 2)
			throw new IllegalArgumentException("Invalid access: no declarator");
		else
			return (AstAbsDeclarator) children[1];
	}

	@Override
	public boolean has_declarator() {
		return children.length == 2;
	}

	protected CType type;

	@Override
	public CType get_type() {
		return type;
	}

	@Override
	public void set_type(CType type) {
		this.type = type;
	}

}
