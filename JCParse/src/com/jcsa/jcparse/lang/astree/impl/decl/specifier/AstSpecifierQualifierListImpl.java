package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifierQualifierList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypeKeyword;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypeQualifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypedefName;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstUnionSpecifier;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;

public class AstSpecifierQualifierListImpl extends AstVariableNode implements AstSpecifierQualifierList {

	public AstSpecifierQualifierListImpl(AstSpecifier specifier) throws Exception {
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
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstSpecifier) children.get(k);
	}

	@Override
	public void append_specifier(AstSpecifier spec) throws Exception {
		if (spec instanceof AstTypeQualifier || spec instanceof AstTypeKeyword || spec instanceof AstStructSpecifier
				|| spec instanceof AstUnionSpecifier || spec instanceof AstEnumSpecifier
				|| spec instanceof AstTypedefName)
			this.append_child(spec);
		else
			throw new IllegalArgumentException("Invalid specifier: " + spec.getClass().getSimpleName());
	}

}
