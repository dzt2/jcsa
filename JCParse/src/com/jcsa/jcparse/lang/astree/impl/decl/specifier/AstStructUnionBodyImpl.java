package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarationList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructUnionBody;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CScope;

public class AstStructUnionBodyImpl extends AstFixedNode implements AstStructUnionBody {

	public AstStructUnionBodyImpl(AstPunctuator lbrace, AstPunctuator rbrace) throws Exception {
		super(2);

		if (lbrace == null || lbrace.get_punctuator() != CPunctuator.left_brace)
			throw new IllegalArgumentException("Invalid left-brace: not left-brace");
		else if (rbrace == null || rbrace.get_punctuator() != CPunctuator.right_brace)
			throw new IllegalArgumentException("Invalid right-brace: not right-brace");
		else {
			this.set_child(0, lbrace);
			this.set_child(1, rbrace);
		}
	}

	public AstStructUnionBodyImpl(AstPunctuator lbrace, AstStructDeclarationList declarations, AstPunctuator rbrace)
			throws Exception {
		super(3);

		if (lbrace == null || lbrace.get_punctuator() != CPunctuator.left_brace)
			throw new IllegalArgumentException("Invalid left-brace: not left-brace");
		else if (rbrace == null || rbrace.get_punctuator() != CPunctuator.right_brace)
			throw new IllegalArgumentException("Invalid right-brace: not right-brace");
		else {
			this.set_child(0, lbrace);
			this.set_child(1, declarations);
			this.set_child(2, rbrace);
		}
	}

	@Override
	public AstPunctuator get_lbrace() {
		return (AstPunctuator) children[0];
	}

	@Override
	public boolean has_declaration_list() {
		return children.length == 3;
	}

	@Override
	public AstStructDeclarationList get_declaration_list() {
		if (children.length != 3)
			throw new IllegalArgumentException("Invalid access: no-declarations");
		else
			return (AstStructDeclarationList) children[1];
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
