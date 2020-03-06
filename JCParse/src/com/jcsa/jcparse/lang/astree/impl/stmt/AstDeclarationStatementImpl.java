package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstDeclarationStatementImpl extends AstFixedNode implements AstDeclarationStatement {

	public AstDeclarationStatementImpl(AstDeclaration decl, AstPunctuator semicolon) throws Exception {
		super(2);

		if (semicolon == null || semicolon.get_punctuator() != CPunctuator.semicolon)
			throw new IllegalArgumentException("Invalid semicolon: null");
		else {
			this.set_child(0, decl);
			this.set_child(1, semicolon);
		}
	}

	@Override
	public AstDeclaration get_declaration() {
		return (AstDeclaration) children[0];
	}

	@Override
	public AstPunctuator get_semicolon() {
		return (AstPunctuator) children[1];
	}

}
