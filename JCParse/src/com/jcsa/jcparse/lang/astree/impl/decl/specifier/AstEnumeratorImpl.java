package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumerator;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstEnumeratorImpl extends AstFixedNode implements AstEnumerator {

	public AstEnumeratorImpl(AstName name) throws Exception {
		super(1);
		this.set_child(0, name);
	}

	public AstEnumeratorImpl(AstName name, AstPunctuator assign, AstConstExpression expression) throws Exception {
		super(3);

		if (assign == null || assign.get_punctuator() != CPunctuator.assign)
			throw new IllegalArgumentException("Invalid assign: not-assign");
		else {
			this.set_child(0, name);
			this.set_child(1, assign);
			this.set_child(2, expression);
		}
	}

	@Override
	public AstName get_name() {
		return (AstName) children[0];
	}

	@Override
	public boolean has_expression() {
		return children.length == 3;
	}

	@Override
	public AstPunctuator get_assign() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no-assign");
		else
			return (AstPunctuator) children[1];
	}

	@Override
	public AstConstExpression get_expression() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no-expression");
		else
			return (AstConstExpression) children[2];
	}

}
