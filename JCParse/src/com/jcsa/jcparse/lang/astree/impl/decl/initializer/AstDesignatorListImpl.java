package com.jcsa.jcparse.lang.astree.impl.decl.initializer;

import com.jcsa.jcparse.lang.astree.decl.initializer.AstDesignator;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstDesignatorList;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;

public class AstDesignatorListImpl extends AstVariableNode implements AstDesignatorList {

	public AstDesignatorListImpl(AstDesignator d) throws Exception {
		super();
		this.append_designator(d);
	}

	@Override
	public int number_of_designators() {
		return children.size();
	}

	@Override
	public AstDesignator get_designator(int k) {
		if (k < 0 || k >= children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return (AstDesignator) children.get(k);
	}

	@Override
	public void append_designator(AstDesignator d) throws Exception {
		this.append_child(d);
	}

}
