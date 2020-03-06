package com.jcsa.jcparse.lang.astree.impl.stmt;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CScope;

public class AstCompoundStatementImpl extends AstFixedNode implements AstCompoundStatement {

	public AstCompoundStatementImpl(AstPunctuator lbrace, AstPunctuator rbrace) throws Exception {
		super(2);

		if (lbrace == null || lbrace.get_punctuator() != CPunctuator.left_brace)
			throw new IllegalArgumentException("Invalid lbrace: null");
		else if (rbrace == null || rbrace.get_punctuator() != CPunctuator.right_brace)
			throw new IllegalArgumentException("Invalid rbrace: null");
		else {
			this.set_child(0, lbrace);
			this.set_child(1, rbrace);
		}
	}

	public AstCompoundStatementImpl(AstPunctuator lbrace, AstStatementList slist, AstPunctuator rbrace)
			throws Exception {
		super(3);

		if (lbrace == null || lbrace.get_punctuator() != CPunctuator.left_brace)
			throw new IllegalArgumentException("Invalid lbrace: null");
		else if (rbrace == null || rbrace.get_punctuator() != CPunctuator.right_brace)
			throw new IllegalArgumentException("Invalid rbrace: null");
		else {
			this.set_child(0, lbrace);
			this.set_child(1, slist);
			this.set_child(2, rbrace);
		}
	}

	@Override
	public AstPunctuator get_lbrace() {
		return (AstPunctuator) children[0];
	}

	@Override
	public boolean has_statement_list() {
		return children.length == 3;
	}

	@Override
	public AstStatementList get_statement_list() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no statements");
		else
			return (AstStatementList) children[1];
	}

	@Override
	public AstPunctuator get_rbrace() {
		return (AstPunctuator) children[children.length - 1];
	}

	protected CScope scope;

	@Override
	public CScope get_scope() {
		return scope;
	}

	@Override
	public void set_scope(CScope scope) {
		this.scope = scope;
	}

}
