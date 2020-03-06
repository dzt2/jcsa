package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifierQualifierList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaration;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaratorList;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstStructDeclarationImpl extends AstFixedNode implements AstStructDeclaration {

	public AstStructDeclarationImpl(AstSpecifierQualifierList specifiers, AstStructDeclaratorList declarators,
			AstPunctuator semicolon) throws Exception {
		super(3);

		if (semicolon == null || semicolon.get_punctuator() != CPunctuator.semicolon)
			throw new IllegalArgumentException("Invalid semicolon: not-semicolon");
		else {
			this.set_child(0, specifiers);
			this.set_child(1, declarators);
			this.set_child(2, semicolon);
		}
	}

	@Override
	public AstSpecifierQualifierList get_specifiers() {
		return (AstSpecifierQualifierList) children[0];
	}

	@Override
	public AstStructDeclaratorList get_declarators() {
		return (AstStructDeclaratorList) children[1];
	}

	@Override
	public AstPunctuator get_semicolon() {
		return (AstPunctuator) children[2];
	}

}
