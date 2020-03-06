package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;

public class AstStatementListImpl extends AstVariableNode implements AstStatementList {

	public AstStatementListImpl(AstStatement stmt) throws Exception {
		super();
		this.append_child(stmt);
	}

	@Override
	public int number_of_statements() {
		return children.size();
	}

	@Override
	public AstStatement get_statement(int k) {
		if (k < 0 || k >= children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return (AstStatement) children.get(k);
	}

	@Override
	public void append_statement(AstStatement stmt) throws Exception {
		this.append_child(stmt);
	}

}
