package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumeratorBody;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumeratorList;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CScope;

public class AstEnumeratorBodyImpl extends AstFixedNode implements AstEnumeratorBody {

	public AstEnumeratorBodyImpl(AstPunctuator lbrace, AstEnumeratorList elist, AstPunctuator rbrace) throws Exception {
		super(3);

		if (lbrace == null || lbrace.get_punctuator() != CPunctuator.left_brace)
			throw new IllegalArgumentException("Invalid lbrace: not left-brace");
		else if (rbrace == null || rbrace.get_punctuator() != CPunctuator.right_brace)
			throw new IllegalArgumentException("Invalid rbrace: not right-brace");
		else {
			this.set_child(0, lbrace);
			this.set_child(1, elist);
			this.set_child(2, rbrace);
		}
	}

	public AstEnumeratorBodyImpl(AstPunctuator lbrace, AstEnumeratorList elist, AstPunctuator comma,
			AstPunctuator rbrace) throws Exception {
		super(4);

		if (lbrace == null || lbrace.get_punctuator() != CPunctuator.left_brace)
			throw new IllegalArgumentException("Invalid lbrace: not left-brace");
		else if (rbrace == null || rbrace.get_punctuator() != CPunctuator.right_brace)
			throw new IllegalArgumentException("Invalid rbrace: not right-brace");
		else if (comma == null || comma.get_punctuator() != CPunctuator.comma)
			throw new IllegalArgumentException("Invalid comma: not-comma");
		else {
			this.set_child(0, lbrace);
			this.set_child(1, elist);
			this.set_child(2, comma);
			this.set_child(3, rbrace);
		}
	}

	@Override
	public AstPunctuator get_lbrace() {
		return (AstPunctuator) children[0];
	}

	@Override
	public AstEnumeratorList get_enumerator_list() {
		return (AstEnumeratorList) children[1];
	}

	@Override
	public boolean has_comma() {
		return children.length == 4;
	}

	@Override
	public AstPunctuator get_comma() {
		if (children.length != 4)
			throw new IllegalArgumentException("Invalid access: no-comma");
		else
			return (AstPunctuator) children[2];
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
