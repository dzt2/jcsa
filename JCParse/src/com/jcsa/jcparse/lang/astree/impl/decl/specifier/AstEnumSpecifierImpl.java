package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumeratorBody;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CKeyword;

public class AstEnumSpecifierImpl extends AstFixedNode implements AstEnumSpecifier {

	public AstEnumSpecifierImpl(AstKeyword _enum, AstName name, AstEnumeratorBody body) throws Exception {
		super(3);

		if (_enum == null || _enum.get_keyword() != CKeyword.c89_enum)
			throw new IllegalArgumentException("Invalid enum: null");
		else {
			this.set_child(0, _enum);
			this.set_child(1, name);
			this.set_child(2, body);
		}
	}

	public AstEnumSpecifierImpl(AstKeyword _enum, AstName name) throws Exception {
		super(2);

		if (_enum == null || _enum.get_keyword() != CKeyword.c89_enum)
			throw new IllegalArgumentException("Invalid enum: null");
		else {
			this.set_child(0, _enum);
			this.set_child(1, name);
		}
	}

	public AstEnumSpecifierImpl(AstKeyword _enum, AstEnumeratorBody body) throws Exception {
		super(2);

		if (_enum == null || _enum.get_keyword() != CKeyword.c89_enum)
			throw new IllegalArgumentException("Invalid enum: null");
		else {
			this.set_child(0, _enum);
			this.set_child(1, body);
		}
	}

	@Override
	public AstKeyword get_enum() {
		return (AstKeyword) children[0];
	}

	@Override
	public boolean has_name() {
		return children[1] instanceof AstName;
	}

	@Override
	public AstName get_name() {
		if (this.has_name())
			return (AstName) children[1];
		else
			throw new IllegalArgumentException("Invalid access: no-name");
	}

	@Override
	public boolean has_body() {
		return children[children.length - 1] instanceof AstEnumeratorBody;
	}

	@Override
	public AstEnumeratorBody get_body() {
		if (this.has_body())
			return (AstEnumeratorBody) children[children.length - 1];
		else
			throw new IllegalArgumentException("Invalid access: no-body");
	}

}
