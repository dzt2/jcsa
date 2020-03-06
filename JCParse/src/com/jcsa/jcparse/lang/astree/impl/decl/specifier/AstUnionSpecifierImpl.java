package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructUnionBody;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstUnionSpecifier;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CKeyword;

public class AstUnionSpecifierImpl extends AstFixedNode implements AstUnionSpecifier {

	public AstUnionSpecifierImpl(AstKeyword union, AstName name) throws Exception {
		super(2);

		if (union == null || union.get_keyword() != CKeyword.c89_union)
			throw new IllegalArgumentException("Invalid union: not-union");
		else {
			this.set_child(0, union);
			this.set_child(1, name);
		}
	}

	public AstUnionSpecifierImpl(AstKeyword union, AstStructUnionBody body) throws Exception {
		super(2);

		if (union == null || union.get_keyword() != CKeyword.c89_union)
			throw new IllegalArgumentException("Invalid union: not-union");
		else {
			this.set_child(0, union);
			this.set_child(1, body);
		}
	}

	public AstUnionSpecifierImpl(AstKeyword union, AstName name, AstStructUnionBody body) throws Exception {
		super(3);

		if (union == null || union.get_keyword() != CKeyword.c89_union)
			throw new IllegalArgumentException("Invalid union: not-union");
		else {
			this.set_child(0, union);
			this.set_child(1, name);
			this.set_child(2, body);
		}
	}

	@Override
	public AstKeyword get_union() {
		return (AstKeyword) children[0];
	}

	@Override
	public boolean has_name() {
		return children[1] instanceof AstName;
	}

	@Override
	public AstName get_name() {
		if (!this.has_name())
			throw new IllegalArgumentException("Invalid access: no-name");
		else
			return (AstName) children[1];
	}

	@Override
	public boolean has_body() {
		return children[children.length - 1] instanceof AstStructUnionBody;
	}

	@Override
	public AstStructUnionBody get_body() {
		if (!this.has_body())
			throw new IllegalArgumentException("Invalid access: no-body");
		else
			return (AstStructUnionBody) children[children.length - 1];
	}

}
