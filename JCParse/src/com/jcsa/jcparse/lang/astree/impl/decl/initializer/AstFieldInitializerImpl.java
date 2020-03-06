package com.jcsa.jcparse.lang.astree.impl.decl.initializer;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstDesignatorList;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstFieldInitializerImpl extends AstFixedNode implements AstFieldInitializer {

	public AstFieldInitializerImpl(AstDesignatorList designator_list, AstPunctuator assign, AstInitializer initializer)
			throws Exception {
		super(3);

		if (assign == null || assign.get_punctuator() != CPunctuator.assign)
			throw new IllegalArgumentException("Invalid assign: not assignment");
		else {
			this.set_child(0, designator_list);
			this.set_child(1, assign);
			this.set_child(2, initializer);
		}
	}

	public AstFieldInitializerImpl(AstInitializer initializer) throws Exception {
		super(1);
		this.set_child(0, initializer);
	}

	@Override
	public boolean has_designator_list() {
		return children.length == 3;
	}

	@Override
	public AstDesignatorList get_designator_list() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no designator-list");
		else
			return (AstDesignatorList) children[0];
	}

	@Override
	public AstPunctuator get_assign() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no designator-list");
		else
			return (AstPunctuator) children[1];
	}

	@Override
	public AstInitializer get_initializer() {
		return (AstInitializer) children[children.length - 1];
	}

}
