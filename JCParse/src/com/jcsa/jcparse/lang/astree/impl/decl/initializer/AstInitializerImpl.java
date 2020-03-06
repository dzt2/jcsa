package com.jcsa.jcparse.lang.astree.impl.decl.initializer;

import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;

public class AstInitializerImpl extends AstFixedNode implements AstInitializer {

	public AstInitializerImpl(AstExpression expr) throws Exception {
		super(1);
		this.set_child(0, expr);
	}

	public AstInitializerImpl(AstInitializerBody body) throws Exception {
		super(1);
		this.set_child(0, body);
	}

	@Override
	public boolean is_expression() {
		return !(children[0] instanceof AstInitializerBody);
	}

	@Override
	public boolean is_body() {
		return children[0] instanceof AstInitializerBody;
	}

	@Override
	public AstExpression get_expression() {
		if (!this.is_expression())
			throw new IllegalArgumentException("Invalid access: not expression");
		else
			return (AstExpression) children[0];
	}

	@Override
	public AstInitializerBody get_body() {
		if (!this.is_body())
			throw new IllegalArgumentException("Invalid access: not body");
		else
			return (AstInitializerBody) children[0];
	}

}
