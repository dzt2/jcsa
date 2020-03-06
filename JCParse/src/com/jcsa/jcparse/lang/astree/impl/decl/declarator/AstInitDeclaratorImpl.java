package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstInitDeclaratorImpl extends AstFixedNode implements AstInitDeclarator {

	public AstInitDeclaratorImpl(AstDeclarator declarator) throws Exception {
		super(1);
		this.set_child(0, declarator);
	}

	public AstInitDeclaratorImpl(AstDeclarator declarator, AstPunctuator assign, AstInitializer initializer)
			throws Exception {
		super(3);

		if (assign == null || assign.get_punctuator() != CPunctuator.assign)
			throw new IllegalArgumentException("Invalid assign: not assignment");
		else {
			this.set_child(0, declarator);
			this.set_child(1, assign);
			this.set_child(2, initializer);
		}
	}

	@Override
	public AstDeclarator get_declarator() {
		return (AstDeclarator) children[0];
	}

	@Override
	public AstOperator get_assign() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no-initializer");
		else
			return (AstOperator) children[1];
	}

	@Override
	public AstInitializer get_initializer() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no-initializer");
		else
			return (AstInitializer) children[2];
	}

	@Override
	public boolean has_initializer() {
		return children.length == 3;
	}

}
